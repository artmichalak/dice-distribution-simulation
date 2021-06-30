package tech.blackfall.dicedist.simulation.adapter.database;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.blackfall.dicedist.simulation.domain.DiceSidesPartialPercentageResult;
import tech.blackfall.dicedist.simulation.domain.DiceSidesRelativePercentageResult;
import tech.blackfall.dicedist.simulation.domain.DiceSidesStatisticsProvider;
import tech.blackfall.dicedist.simulation.domain.DiceSidesStatisticsResult;
import tech.blackfall.dicedist.simulation.domain.GenerateRelativePercentageStatisticsCommand;
import tech.blackfall.dicedist.simulation.domain.TotalValueOccurrenceEntry;

@Component
@Slf4j
@RequiredArgsConstructor
class DiceSidesStatisticsAdapter implements DiceSidesStatisticsProvider {

  private final SimulationResultRepository repository;

  @Override
  public DiceSidesStatisticsResult fetchGlobalDiceSidesStatistics() {
    return new DiceSidesStatisticsResult(repository.fetchGlobalStatistics());
  }

  @Override
  public DiceSidesRelativePercentageResult generateRelativePercentageStatistics(
      GenerateRelativePercentageStatisticsCommand cmd) {
    List<TotalValueOccurrenceEntry> entries = repository
        .fetchRelativeDistribution(cmd.getNumberOfDice(), cmd.getNumberOfSides());

    long allOccurrences = entries.stream()
        .map(TotalValueOccurrenceEntry::getNumberOfOccurrences)
        .reduce(0L, Long::sum);

    List<DiceSidesPartialPercentageResult> values = entries.stream().map(
        entry -> new DiceSidesPartialPercentageResult(
            entry.getTotalValue(), computePercentage(allOccurrences, entry.getNumberOfOccurrences())))
        .collect(Collectors.toList());

    return new DiceSidesRelativePercentageResult(cmd.getNumberOfDice(), cmd.getNumberOfSides(), values);
  }

  private float computePercentage(long allOccurrences, long entryOccurrence) {
    return (float) ((entryOccurrence * 100) / (double) allOccurrences);
  }
}
