package tech.blackfall.dicedist.simulation.adapter.iterative;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.blackfall.dicedist.simulation.domain.AbstractSimulationDistributionProvider;
import tech.blackfall.dicedist.simulation.domain.RunSimulationCommand;
import tech.blackfall.dicedist.simulation.domain.SimulationMode;

@Component
@Slf4j
class IterativeSimulationDistributionProvider extends AbstractSimulationDistributionProvider {

  IterativeSimulationDistributionProvider() {
    super(SimulationMode.ITER);
  }

  protected Map<Integer, Integer> computeDistribution(RunSimulationCommand cmd) {
    log.info("Running iterative simulation for command: " + cmd.toString());
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
