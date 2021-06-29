package tech.blackfall.dicedist.simulation.adapter.api;

import static java.lang.Integer.parseInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_DICE;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import tech.blackfall.dicedist.simulation.AbstractIntegrationTest;

class SimulationControllerTest extends AbstractIntegrationTest {

  @Autowired
  SimulationMocker simulationMocker;

  @ParameterizedTest
  @ValueSource(ints = {0,1,2,3,4,5})
  void shouldRunWithUniformRandomsUsingDefaults(int value) throws Exception {
    simulationMocker.loopNextRandomValues(value);
    int expectedTotal = (value + 1) * parseInt(DEFAULT_NUMBER_OF_DICE);

    mockMvc.perform(get("/v1/simulation")
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"totals\":[" + expectedTotal + "],\"occurrences\":[100]}"));
  }

  @ParameterizedTest
  @ValueSource(ints = {1,20,150,1000,23456})
  void shouldRunWithUniformRandomsUsingSpecifiedRolls(int rolls) throws Exception {
    simulationMocker.loopNextRandomValues(1);

    mockMvc.perform(get("/v1/simulation")
        .param("rolls", Integer.toString(rolls))
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"totals\":[6],\"occurrences\":[" + rolls + "]}"));
  }

  @ParameterizedTest
  @ValueSource(ints = {1,5,10})
  void shouldRunWithUniformRandomsUsingSpecifiedDice(int dice) throws Exception {
    simulationMocker.loopNextRandomValues(1);
    int expectedTotal = 2 * dice;

    mockMvc.perform(get("/v1/simulation")
        .param("dice", Integer.toString(dice))
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"totals\":[" + expectedTotal + "],\"occurrences\":[100]}"));
  }

  @ParameterizedTest
  @ValueSource(ints = {5,10,50})
  void shouldRunWithUniformRandomsUsingSpecifiedSides(int sides) throws Exception {
    int expectedOccurrences = 3;
    int requiredRolls = sides * expectedOccurrences;
    simulationMocker.loopNextRandomValuesFromTo(0, sides);

    mockMvc.perform(get("/v1/simulation")
        .param("sides", Integer.toString(sides))
        .param("rolls", Integer.toString(requiredRolls))
        .header("Content-Type", "application/json"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"totals\":[" + expectedOccurrences + "],\"occurrences\":[100]}"));
  }
}
