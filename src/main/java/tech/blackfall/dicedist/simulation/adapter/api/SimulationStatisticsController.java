package tech.blackfall.dicedist.simulation.adapter.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tech.blackfall.dicedist.simulation.domain.DiceSidesStatisticsResult;
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
  @Operation(summary = "requests global statistics grouped by dice and sides")
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
}
