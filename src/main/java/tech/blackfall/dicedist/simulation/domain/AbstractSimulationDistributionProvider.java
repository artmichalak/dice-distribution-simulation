package tech.blackfall.dicedist.simulation.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import tech.blackfall.dicedist.simulation.kernel.domain.RandomGeneratorProvider;

public abstract class AbstractSimulationDistributionProvider implements SimulationDistributionProvider {

  private final SimulationMode supportedSimulationMode;
  protected final RandomGeneratorProvider randomGeneratorProvider;

  protected AbstractSimulationDistributionProvider(SimulationMode supportedSimulationMode,
      RandomGeneratorProvider randomGeneratorProvider) {
    this.supportedSimulationMode = supportedSimulationMode;
    this.randomGeneratorProvider = randomGeneratorProvider;
  }

  @Override
  public boolean supports(SimulationMode mode) {
    return supportedSimulationMode == mode;
  }

  @Override
  public SimulationResult runSimulation(RunSimulationCommand cmd) {
    Map<Long, Integer> distribution = computeDistribution(cmd);

    List<SimulationPartialResult> partialResults = distribution.entrySet().stream()
        .map(entry -> new SimulationPartialResult(entry.getKey(), entry.getValue()))
        .sorted(Comparator.comparing(SimulationPartialResult::getTotalValue))
        .collect(Collectors.toList());

    return new SimulationResult(partialResults);
  }

  protected abstract Map<Long, Integer> computeDistribution(RunSimulationCommand cmd);
}
