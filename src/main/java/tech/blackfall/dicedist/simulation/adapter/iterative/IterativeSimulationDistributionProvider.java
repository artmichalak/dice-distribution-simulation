package tech.blackfall.dicedist.simulation.adapter.iterative;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.blackfall.dicedist.simulation.domain.RunSimulationCommand;
import tech.blackfall.dicedist.simulation.domain.SimulationDistributionProvider;
import tech.blackfall.dicedist.simulation.domain.SimulationPartialResult;
import tech.blackfall.dicedist.simulation.domain.SimulationResult;

@Component
@Slf4j
class IterativeSimulationDistributionProvider implements SimulationDistributionProvider {

  @Override
  public SimulationResult runSimulation(RunSimulationCommand cmd) {
    Map<Integer, Integer> distribution = computeDistribution(cmd);

    List<SimulationPartialResult> partialResults = distribution.entrySet().stream()
        .map(entry -> new SimulationPartialResult(entry.getKey(), entry.getValue()))
        .sorted(Comparator.comparing(SimulationPartialResult::getTotalValue))
        .collect(Collectors.toList());

    return new SimulationResult(partialResults);
  }

  private Map<Integer, Integer> computeDistribution(RunSimulationCommand cmd) {
    var random = ThreadLocalRandom.current();

    Map<Integer, Integer> distribution = new HashMap<>();
    for (var roll = 0; roll < cmd.getNumberOfRolls(); ++roll) {

      var sum = 0;
      for (var dice = 0; dice < cmd.getNumberOfDice(); ++dice) {
        var value = random.nextInt(0, cmd.getNumberOfSides()) + 1;
        sum += value;
      }

      int currentValue = distribution.getOrDefault(sum, 0);
      distribution.put(sum, ++currentValue);
    }
    return distribution;
  }
}
