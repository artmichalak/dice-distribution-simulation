package tech.blackfall.dicedist.simulation.adapter.api;

import static java.util.stream.Collectors.toList;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_DICE;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_ROLLS;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_SIDES;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_SIMULATION_MODE;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MIN_NUMBER_OF_DICE;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MIN_NUMBER_OF_ROLLS;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MIN_NUMBER_OF_SIDES;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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
import tech.blackfall.dicedist.simulation.domain.SimulationMode;
import tech.blackfall.dicedist.simulation.domain.SimulationPartialResult;
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
  public SimulationResultResponse getSimulation(
      @RequestParam(name = "dice", defaultValue = DEFAULT_NUMBER_OF_DICE) @Min(MIN_NUMBER_OF_DICE) @Max(Integer.MAX_VALUE) int dice,
      @RequestParam(name = "sides", defaultValue = DEFAULT_NUMBER_OF_SIDES) @Min(MIN_NUMBER_OF_SIDES) @Max(Integer.MAX_VALUE) int sides,
      @RequestParam(name = "rolls", defaultValue = DEFAULT_NUMBER_OF_ROLLS) @Min(MIN_NUMBER_OF_ROLLS) @Max(Integer.MAX_VALUE) int rolls,
      @RequestParam(name = "mode", defaultValue = DEFAULT_SIMULATION_MODE) SimulationMode mode) {
    log.info("Running simulation of rolls={} for dice={} with sides={}, using mode={}", rolls, dice, sides, mode);

    var simulationResult = simulationService
        .runSimulation(RunSimulationCommand.of(mode, dice, sides, rolls));
    return SimulationResultResponse.from(simulationResult);
  }

  @Value
  static class SimulationResultResponse {

    List<Integer> totals;
    List<Integer> occurrences;

    static SimulationResultResponse from(SimulationResult simulationResult) {
      List<Integer> totals = simulationResult.getValues().stream()
          .map(SimulationPartialResult::getTotalValue)
          .collect(toList());

      List<Integer> occurrences = simulationResult.getValues().stream()
          .map(SimulationPartialResult::getNumberOfOccurrences)
          .collect(toList());

      return new SimulationResultResponse(totals, occurrences);
    }
  }
}
