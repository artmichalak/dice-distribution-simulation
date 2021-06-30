package tech.blackfall.dicedist.simulation.adapter.api;

import static java.util.stream.Collectors.toList;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MAX_NUMBER_OF_DICE;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MAX_NUMBER_OF_SIDES;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MIN_NUMBER_OF_DICE;
import static tech.blackfall.dicedist.simulation.adapter.api.SimulationConstants.MIN_NUMBER_OF_SIDES;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
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
import tech.blackfall.dicedist.simulation.domain.DiceSidesPartialPercentageResult;
import tech.blackfall.dicedist.simulation.domain.DiceSidesRelativePercentageResult;
import tech.blackfall.dicedist.simulation.domain.DiceSidesStatisticsResult;
import tech.blackfall.dicedist.simulation.domain.GenerateRelativePercentageStatisticsCommand;
import tech.blackfall.dicedist.simulation.domain.SimulationService;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
@Tag(name = "Dice Distribution Statistics")
class SimulationStatisticsController {

  private final SimulationService simulationService;

  @GetMapping("/v1/simulation/stats")
  @ResponseBody
  @Operation(summary = "Requests global statistics grouped by dice and sides")
  @ApiResponse(
      responseCode = "200",
      description = """
          Returns a list of entries containing grouped results by dice and sides. The number of rolls is a sum of
          all simulations performed for a specific dice-sides combo.
          """,
      content = @Content(schema = @Schema(implementation = DiceSidesStatisticsResponse.class)))
  public DiceSidesStatisticsResponse getDiceSidesGlobalStatistics() {
    log.info("Getting global simulation statistics");
    DiceSidesStatisticsResult result = simulationService.fetchDiceSidesStatistics();
    return DiceSidesStatisticsResponse.from(result);
  }

  @Value
  static class DiceSidesStatisticsResponse {

    private static final String DICE_SIDES_SEPARATOR = "-";

    List<DiceSidesStatisticsEntryDto> stats;

    private static String formatDiceSides(int dice, int sides) {
      return dice + DICE_SIDES_SEPARATOR + sides;
    }

    static DiceSidesStatisticsResponse from(DiceSidesStatisticsResult result) {
      List<DiceSidesStatisticsEntryDto> stats = result.getEntries().stream()
          .map(entry -> new DiceSidesStatisticsEntryDto(formatDiceSides(entry.getDice(), entry.getSides()),
              entry.getTotalRolls(), entry.getSimulationsPerformed()))
          .collect(Collectors.toList());
      return new DiceSidesStatisticsResponse(stats);
    }
  }

  @GetMapping(value = "/v1/simulation/distribution")
  @ResponseBody
  @Operation(summary = "Requests relative statistics compared to the total rolls for all the simulations.")
  @ApiResponse(
      responseCode = "200",
      description = """
          For a given dice number–dice side combination, returns the relative distribution,
          compared to the total rolls, for all the simulations.
              """,
      content = @Content(schema = @Schema(implementation = DiceSidesStatisticsResponse.class)))
  @ApiResponse(
      responseCode = "404",
      description = """
          Returned when no distribution is found for given dice and given sides.
              """)
  public DiceSidesRelativePercentageResponse getSimulationDistribution(
      @RequestParam(name = "dice") @Min(MIN_NUMBER_OF_DICE) @Max(MAX_NUMBER_OF_DICE) int dice,
      @RequestParam(name = "sides") @Min(MIN_NUMBER_OF_SIDES) @Max(MAX_NUMBER_OF_SIDES) int sides
  ) {
    log.info("Getting relative dice-sides distribution for dice={} with sides={}", dice, sides);
    DiceSidesRelativePercentageResult result = simulationService.generateRelativePercentageStatistics(
        GenerateRelativePercentageStatisticsCommand.of(dice, sides));
    return DiceSidesRelativePercentageResponse.from(result);
  }

  @Value
  static class DiceSidesRelativePercentageResponse {

    private static final String PERCENTAGE_FORMAT = "%.2f";

    int givenDice;
    int givenSides;
    List<Long> totals;
    List<String> percentages;

    static DiceSidesRelativePercentageResponse from(DiceSidesRelativePercentageResult result) {
      List<Long> totals = result.getValues().stream()
          .map(DiceSidesPartialPercentageResult::getTotalValue)
          .collect(toList());

      List<String> percentages = result.getValues().stream()
          .map(DiceSidesPartialPercentageResult::getPercentage)
          .map(value -> String.format(PERCENTAGE_FORMAT, value))
          .collect(toList());

      return new DiceSidesRelativePercentageResponse(
          result.getNumberOfDice(), result.getNumberOfSides(), totals, percentages);
    }
  }
}
