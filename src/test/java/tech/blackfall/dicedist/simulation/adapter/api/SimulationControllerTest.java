package tech.blackfall.dicedist.simulation.adapter.api;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.joining;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_DICE;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import tech.blackfall.dicedist.simulation.AbstractIntegrationTest;

class SimulationControllerTest extends AbstractIntegrationTest {

  private final int defaultNumberOfDice = parseInt(DEFAULT_NUMBER_OF_DICE);

  @Autowired
  SimulationMocker simulationMocker;

  @AfterEach
  void afterEach() {
    simulationMocker.resetMocks();
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5})
  void shouldRunWithUniformRandomsUsingDefaults(int value) throws Exception {
    simulationMocker.loopNextRandomValues(value);
    int expectedTotal = (value + 1) * defaultNumberOfDice;

    mockMvc.perform(get("/v1/simulation")
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"totals\":[" + expectedTotal + "],\"occurrences\":[100]}"));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 20, 150, 1000, 23456})
  void shouldRunWithUniformRandomsUsingSpecifiedRolls(int rolls) throws Exception {
    simulationMocker.loopNextRandomValues(1);

    mockMvc.perform(get("/v1/simulation")
        .param("rolls", valueOf(rolls))
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"totals\":[6],\"occurrences\":[" + rolls + "]}"));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 5, 10})
  void shouldRunWithUniformRandomsUsingSpecifiedDice(int dice) throws Exception {
    simulationMocker.loopNextRandomValues(1);
    int expectedTotal = 2 * dice;

    mockMvc.perform(get("/v1/simulation")
        .param("dice", valueOf(dice))
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"totals\":[" + expectedTotal + "],\"occurrences\":[100]}"));
  }

  @ParameterizedTest
  @ValueSource(ints = {5, 10, 50})
  void shouldRunWithUniformRandomsUsingSpecifiedSides(int sides) throws Exception {
    int occurrencesForEachSide = 3;
    int requiredRolls = sides * occurrencesForEachSide;
    simulationMocker.loopNextRandomValuesFromTo(0, sides);

    String expectedTotals = IntStream.range(1, sides + 1).boxed().map(String::valueOf).collect(joining(","));
    String expectedOccurrences = IntStream.range(0, sides).map(i -> occurrencesForEachSide).boxed().map(String::valueOf)
        .collect(joining(","));

    mockMvc.perform(get("/v1/simulation")
        .param("sides", valueOf(sides))
        .param("rolls", valueOf(requiredRolls))
        .param("dice", "1")
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(
            content().json("{\"totals\":[" + expectedTotals + "],\"occurrences\":[" + expectedOccurrences + "]}"));
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0, 0",
      "1, 1, 1",
      "5, 6, 0",
      "0, 6, 10"
  })
  void shouldReturn400ForInvalidDiceSidesAndRolls(int dice, int sides, int rolls) throws Exception {
    mockMvc.perform(get("/v1/simulation")
        .param("dice", valueOf(dice))
        .param("sides", valueOf(sides))
        .param("rolls", valueOf(rolls))
        .header("Content-Type", "application/json"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400ForNonNumericalInput() throws Exception {
    mockMvc.perform(get("/v1/simulation")
        .param("dice", "a")
        .param("sides", "b")
        .param("rolls", "c")
        .header("Content-Type", "application/json"))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(ints = {60, 600, 960})
  void shouldRunIterativeAndConcurrentModesAndResultsMatchNotExceedingThreshold(int numberOfRolls) throws Exception {
    simulationMocker.loopNextRandomValues(0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5);

    String numberOfRollsParam = valueOf(numberOfRolls);
    String expectedTotals = IntStream.range(1, 7).map(operand -> operand * defaultNumberOfDice)
        .boxed().map(String::valueOf).collect(Collectors.joining(","));
    String expectedOccurrences = IntStream.range(0, 6).map(i -> numberOfRolls / 6).boxed().map(String::valueOf)
        .collect(joining(","));
    String expectedContent = "{\"totals\":[" + expectedTotals + "],\"occurrences\":[" + expectedOccurrences + "]}";

    mockMvc.perform(get("/v1/simulation")
        .param("mode", "iter")
        .param("rolls", numberOfRollsParam)
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedContent));

    mockMvc.perform(get("/v1/simulation")
        .param("mode", "conc")
        .param("rolls", numberOfRollsParam)
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedContent));
  }

  @ParameterizedTest
  @ValueSource(ints = {1200, 6000, 60000})
  void shouldRunIterativeAndConcurrentModesAndResultsMatchExceedingThreshold(int numberOfRolls) throws Exception {
    simulationMocker.loopNextRandomValues(0);

    String numberOfRollsParam = valueOf(numberOfRolls);
    String expectedTotals = valueOf(defaultNumberOfDice);
    String expectedOccurrences = valueOf(numberOfRolls);
    String expectedContent = "{\"totals\":[" + expectedTotals + "],\"occurrences\":[" + expectedOccurrences + "]}";

    mockMvc.perform(get("/v1/simulation")
        .param("mode", "iter")
        .param("rolls", numberOfRollsParam)
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedContent));

    mockMvc.perform(get("/v1/simulation")
        .param("mode", "conc")
        .param("rolls", numberOfRollsParam)
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedContent));
  }
}
