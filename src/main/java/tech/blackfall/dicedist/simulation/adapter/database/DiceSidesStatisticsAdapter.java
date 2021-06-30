package tech.blackfall.dicedist.simulation.adapter.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.blackfall.dicedist.simulation.domain.DiceSidesStatisticsProvider;
import tech.blackfall.dicedist.simulation.domain.DiceSidesStatisticsResult;

@Component
@Slf4j
@RequiredArgsConstructor
class DiceSidesStatisticsAdapter implements DiceSidesStatisticsProvider {

  private final SimulationResultRepository repository;

  @Override
  public DiceSidesStatisticsResult fetchGlobalDiceSidesStatistics() {
    return new DiceSidesStatisticsResult(repository.fetchGlobalStatistics());
  }
}
