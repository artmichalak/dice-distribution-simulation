package tech.blackfall.dicedist.simulation.domain;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimulationService {

  private final Collection<SimulationDistributionProvider> simulationDistributionProviders;
  private final SimulationPersister simulationPersister;
  private final DiceSidesStatisticsProvider diceSidesStatisticsProvider;

  public SimulationResult runSimulation(RunSimulationCommand cmd) {
    SimulationDistributionProvider provider = simulationDistributionProviders.stream()
        .filter(simulationDistributionProvider -> simulationDistributionProvider.supports(cmd.getSimulationMode()))
        .findFirst()
        .orElseThrow(NoMatchingSimulationModeException::new);
    var simulationResult = provider.runSimulation(cmd);
    return simulationPersister.saveSimulationResult(SaveSimulationResultsCommand.of(simulationResult));
  }

  public DiceSidesStatisticsResult fetchDiceSidesStatistics() {
    return diceSidesStatisticsProvider.fetchGlobalDiceSidesStatistics();
  }

  public DiceSidesRelativePercentageResult generateRelativePercentageStatistics(
      GenerateRelativePercentageStatisticsCommand cmd) {
    DiceSidesRelativePercentageResult result = diceSidesStatisticsProvider
        .generateRelativePercentageStatistics(cmd);
    if (result.getValues().isEmpty()) {
      throw new DistributionNotFoundException(cmd.getNumberOfDice(), cmd.getNumberOfSides());
    }
    return result;
  }
}
