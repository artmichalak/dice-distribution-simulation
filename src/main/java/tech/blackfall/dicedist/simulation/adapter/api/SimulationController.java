package tech.blackfall.dicedist.simulation.adapter.api;

import static java.util.stream.Collectors.joining;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_DICE;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_ROLLS;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_SIDES;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MIN_NUMBER_OF_DICE;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MIN_NUMBER_OF_ROLLS;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MIN_NUMBER_OF_SIDES;

import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tech.blackfall.dicedist.simulation.domain.RunSimulationCommand;
import tech.blackfall.dicedist.simulation.domain.SimulationResult;
import tech.blackfall.dicedist.simulation.domain.SimulationService;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
@Tag(name = "Dice Distribution Simulation")
class SimulationController {

  private static final String VALUES_SEPARATOR = ",";

  private final SimulationService simulationService;

  @GetMapping("/v1/simulation")
  @ResponseBody
  public SimulationResultResponse runSimulation(
      @RequestParam(name = "dice", defaultValue = DEFAULT_NUMBER_OF_DICE) @Min(MIN_NUMBER_OF_DICE) @Max(Integer.MAX_VALUE) int numberOfDice,
      @RequestParam(name = "sides", defaultValue = DEFAULT_NUMBER_OF_SIDES) @Min(MIN_NUMBER_OF_SIDES) @Max(Integer.MAX_VALUE) int numberOfSides,
      @RequestParam(name = "rolls", defaultValue = DEFAULT_NUMBER_OF_ROLLS) @Min(MIN_NUMBER_OF_ROLLS) @Max(Integer.MAX_VALUE) int numberOfRolls) {
    log.info("Running simulation of rolls={} for dice={} with sides={}", numberOfRolls, numberOfDice, numberOfSides);

    var simulationResult = simulationService
        .runSimulation(RunSimulationCommand.of(numberOfDice, numberOfSides, numberOfRolls));
    return SimulationResultResponse.from(simulationResult);
  }

  @Value
  static class SimulationResultResponse {

    String totals;
    String occurrences;

    static SimulationResultResponse from(SimulationResult simulationResult) {
      String totals = simulationResult.getValues().stream()
          .map(partial -> Integer.toString(partial.getTotalValue()))
          .collect(joining(VALUES_SEPARATOR));

      String occurrences = simulationResult.getValues().stream()
          .map(partial -> Integer.toString(partial.getNumberOfOccurrences()))
          .collect(joining(VALUES_SEPARATOR));

      return new SimulationResultResponse(totals, occurrences);
    }
  }
}
