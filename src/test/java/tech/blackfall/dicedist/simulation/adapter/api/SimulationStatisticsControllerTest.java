package tech.blackfall.dicedist.simulation.adapter.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tech.blackfall.dicedist.simulation.AbstractIntegrationTest;

@Testcontainers
class SimulationStatisticsControllerTest extends AbstractIntegrationTest {

  @Container
  protected static final PostgreSQLContainer<?> DATABASE = new PostgreSQLContainer<>("postgres:11.1");

  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", DATABASE::getJdbcUrl);
    registry.add("spring.datasource.username", DATABASE::getUsername);
    registry.add("spring.datasource.password", DATABASE::getPassword);
  }

  @Autowired
  SimulationMocker simulationMocker;

  @AfterEach
  void afterEach() {
    simulationMocker.resetMocks();
  }

  @Test
  void shouldReturnStatisticsForMultipleIterations() throws Exception {
    simulationMocker.loopNextRandomValues(0);
    int iterations = 3;
    for (int i = 0; i < iterations; ++i) {
      mockMvc.perform(get("/v1/simulation")
          .param("dice", "15")
          .param("sides", "16")
          .header("Content-Type", "application/json"))
          .andExpect(status().isOk())
          .andExpect(content().json("{\"totals\":[15],\"occurrences\":[100]}"));
    }

    mockMvc.perform(get("/v1/simulation/stats")
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "stats": [
                {
                  "diceSides": "15-16",
                  "totalRolls": 300,
                  "simulationsPerformed": 3
                }
              ]
            }
            """));
  }

  @Test
  void shouldReturnDistributionMultipleSearches() throws Exception {
    simulationMocker.loopNextRandomValues(0);
    mockMvc.perform(get("/v1/simulation")
        .param("dice", "20")
        .param("sides", "20")
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"totals\":[20],\"occurrences\":[100]}"));

    simulationMocker.loopNextRandomValues(1);
    mockMvc.perform(get("/v1/simulation")
        .param("dice", "20")
        .param("sides", "20")
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"totals\":[40],\"occurrences\":[100]}"));

    mockMvc.perform(get("/v1/simulation/distribution?dice=20&sides=20")
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "givenDice": 20,
              "givenSides": 20,
              "totals": [
                20,
                40
              ],
              "percentages": [
                "50.00",
                "50.00"
              ]
            }
            """));
  }

  @Test
  void shouldReturnBadRequestOnMissingDistributionParameters() throws Exception {
    mockMvc.perform(get("/v1/simulation/distribution")
        .header("Content-Type", "application/json"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnNotFoundOnNoDistributionForGivenInput() throws Exception {
    mockMvc.perform(get("/v1/simulation/distribution?dice=1000&sides=123456")
        .header("Content-Type", "application/json"))
        .andExpect(status().isNotFound());
  }
}
