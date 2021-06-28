package tech.blackfall.dicedist.simulation.adapter.concurrent;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.blackfall.dicedist.simulation.domain.AbstractSimulationDistributionProvider;
import tech.blackfall.dicedist.simulation.domain.RunSimulationCommand;
import tech.blackfall.dicedist.simulation.domain.SimulationMode;

@Component
@Slf4j
class ConcurrentSimulationDistributionProvider extends AbstractSimulationDistributionProvider {

  private final ForkJoinPool workerPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

  ConcurrentSimulationDistributionProvider() {
    super(SimulationMode.CONC);
  }

  protected Map<Integer, Integer> computeDistribution(RunSimulationCommand cmd) {
    log.info("Running concurrent simulation for command: " + cmd.toString());
    return workerPool
        .invoke(new RollsSplitAction(cmd.getNumberOfRolls(), cmd.getNumberOfDice(), cmd.getNumberOfSides()));
  }

  @Value
  @EqualsAndHashCode(callSuper=false)
  static class RollsSplitAction extends RecursiveTask<Map<Integer, Integer>> {

    static final int ROLLS_SPLIT_THRESHOLD = 1024;

    Integer numberOfRolls;
    Integer numberOfDice;
    Integer numberOfSides;

    @Override
    protected Map<Integer, Integer> compute() {
      if (numberOfRolls > ROLLS_SPLIT_THRESHOLD) {
        return ForkJoinTask.invokeAll(createSubTasks())
            .stream()
            .map(ForkJoinTask::join)
            .map(Map::entrySet)
            .flatMap(Collection::stream)
            .collect(toMap(Entry::getKey, Entry::getValue, Integer::sum));
      } else {
        return computeLocally();
      }
    }

    private Map<Integer, Integer> computeLocally() {
      Map<Integer, Integer> distribution = new HashMap<>();
      var random = ThreadLocalRandom.current();
      for (var roll = 0; roll < numberOfRolls; ++roll) {

        var sum = 0;
        for (var dice = 0; dice < numberOfDice; ++dice) {
          var value = random.nextInt(0, numberOfSides) + 1;
          sum += value;
        }

        int currentValue = distribution.getOrDefault(sum, 0);
        distribution.put(sum, ++currentValue);
      }
      return distribution;
    }

    private Collection<RollsSplitAction> createSubTasks() {
      int firstPart = numberOfRolls / 2;
      int secondPart = numberOfRolls - firstPart;
      return List.of(new RollsSplitAction(firstPart, numberOfDice, numberOfSides),
          new RollsSplitAction(secondPart, numberOfDice, numberOfSides));
    }
  }
}
