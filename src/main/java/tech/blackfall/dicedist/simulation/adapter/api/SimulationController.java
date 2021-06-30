package tech.blackfall.dicedist.simulation.adapter.api;

import static java.util.stream.Collectors.toList;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_DICE;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_DICE_STR;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_ROLLS;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_ROLLS_STR;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_SIDES;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_NUMBER_OF_SIDES_STR;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_SIMULATION_MODE;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.DEFAULT_SIMULATION_MODE_STR;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MAX_NUMBER_OF_DICE;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MAX_NUMBER_OF_ROLLS;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MAX_NUMBER_OF_SIDES;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MIN_NUMBER_OF_DICE;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MIN_NUMBER_OF_ROLLS;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MIN_NUMBER_OF_SIDES;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tech.blackfall.dicedist.simulation.domain.RunSimulationCommand;
import tech.blackfall.dicedist.simulation.domain.SimulationMode;
import tech.blackfall.dicedist.simulation.domain.SimulationResult;
import tech.blackfall.dicedist.simulation.domain.SimulationResultMapper;
import tech.blackfall.dicedist.simulation.domain.SimulationService;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
@Tag(name = "Dice Distribution Simulation")
class SimulationController {

  private final SimulationService simulationService;

  @GetMapping("/v1/simulation")
  @ResponseBody
  public SimulationResultResponse getSimulation(
      @RequestParam(name = "dice", defaultValue = DEFAULT_NUMBER_OF_DICE_STR)
      @Min(MIN_NUMBER_OF_DICE) @Max(MAX_NUMBER_OF_DICE) int dice,
      @RequestParam(name = "sides", defaultValue = DEFAULT_NUMBER_OF_SIDES_STR)
      @Min(MIN_NUMBER_OF_SIDES) @Max(MAX_NUMBER_OF_SIDES) int sides,
      @RequestParam(name = "rolls", defaultValue = DEFAULT_NUMBER_OF_ROLLS_STR)
      @Min(MIN_NUMBER_OF_ROLLS) @Max(MAX_NUMBER_OF_ROLLS) int rolls,
      @RequestParam(name = "mode", defaultValue = DEFAULT_SIMULATION_MODE_STR) SimulationMode mode) {
    log.info("Running simulation of rolls={} for dice={} with sides={}, using mode={}", rolls, dice, sides, mode);
    return runSimulation(RunSimulationCommand.of(mode, dice, sides, rolls));
  }

  @PostMapping("/v1/simulation")
  @ResponseBody
  public SimulationResultResponse postSimulation(@Valid @RequestBody SimulationRequest request) {
    log.info("Running simulation of rolls={} for dice={} with sides={}, using mode={}", request.getRolls(),
        request.getDice(), request.getSides(), request.getMode());
    return runSimulation(RunSimulationCommand
        .of(request.getMode(), request.getDice(), request.getSides(), request.getRolls()));
  }

  private SimulationResultResponse runSimulation(RunSimulationCommand cmd) {
    var simulationResult = simulationService.runSimulation(cmd);
    return SimulationResultResponse.from(simulationResult);
  }

  @Data
  static class SimulationRequest {

    @Min(MIN_NUMBER_OF_DICE)
    @Max(MAX_NUMBER_OF_DICE)
    @NotNull
    private int dice = DEFAULT_NUMBER_OF_DICE;

    @Min(MIN_NUMBER_OF_SIDES)
    @Max(MAX_NUMBER_OF_SIDES)
    @NotNull
    private int sides = DEFAULT_NUMBER_OF_SIDES;

    @Min(MIN_NUMBER_OF_ROLLS)
    @Max(MAX_NUMBER_OF_ROLLS)
    @NotNull
    private int rolls = DEFAULT_NUMBER_OF_ROLLS;

    @NotNull
    private SimulationMode mode = DEFAULT_SIMULATION_MODE;
  }

  @Value
  static class SimulationResultResponse {

    long id;
    List<Long> totals;
    List<Integer> occurrences;

    static SimulationResultResponse from(SimulationResult simulationResult) {
      SimulationResultDto dto = SimulationResultMapper.INSTANCE.mapToResponse(simulationResult);

      List<Long> totals = dto.getValues().stream()
          .map(SimulationPartialResultDto::getTotalValue)
          .collect(toList());

      List<Integer> occurrences = dto.getValues().stream()
          .map(SimulationPartialResultDto::getNumberOfOccurrences)
          .collect(toList());

      return new SimulationResultResponse(dto.getId(), totals, occurrences);
    }
  }
}
