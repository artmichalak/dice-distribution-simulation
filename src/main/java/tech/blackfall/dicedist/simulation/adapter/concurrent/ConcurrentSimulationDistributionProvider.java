package tech.blackfall.dicedist.simulation.adapter.concurrent;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.blackfall.dicedist.simulation.domain.AbstractSimulationDistributionProvider;
import tech.blackfall.dicedist.simulation.domain.RunSimulationCommand;
import tech.blackfall.dicedist.simulation.domain.SimulationMode;
import tech.blackfall.dicedist.simulation.kernel.domain.RandomGeneratorProvider;

@Component
@Slf4j
class ConcurrentSimulationDistributionProvider extends AbstractSimulationDistributionProvider {

  private final ForkJoinPool workerPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

  ConcurrentSimulationDistributionProvider(RandomGeneratorProvider randomGeneratorProvider) {
    super(SimulationMode.CONC, randomGeneratorProvider);
  }

  protected Map<Long, Integer> computeDistribution(RunSimulationCommand cmd) {
    log.info("Running concurrent simulation for command: " + cmd.toString());
    return workerPool
        .invoke(new RollsSplitAction(cmd.getNumberOfRolls(), cmd.getNumberOfDice(), cmd.getNumberOfSides()));
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  class RollsSplitAction extends RecursiveTask<Map<Long, Integer>> {

    static final int ROLLS_SPLIT_THRESHOLD = 1024;

    Integer numberOfRolls;
    Integer numberOfDice;
    Integer numberOfSides;

    @Override
    protected Map<Long, Integer> compute() {
      if (numberOfRolls > ROLLS_SPLIT_THRESHOLD) {
        return ForkJoinTask.invokeAll(createSubTasks(ROLLS_SPLIT_THRESHOLD))
            .stream()
            .map(ForkJoinTask::join)
            .map(Map::entrySet)
            .flatMap(Collection::stream)
            .collect(toMap(Entry::getKey, Entry::getValue, Integer::sum));
      } else {
        return computeLocally();
      }
    }

    private Map<Long, Integer> computeLocally() {
      Map<Long, Integer> distribution = new HashMap<>();
      for (var roll = 0; roll < numberOfRolls; ++roll) {
        var sum = new DiceSplitAction(numberOfDice, numberOfSides).invoke();
        int currentValue = distribution.getOrDefault(sum, 0);
        distribution.put(sum, ++currentValue);
      }
      return distribution;
    }

    private Collection<RollsSplitAction> createSubTasks(int divider) {
      List<RollsSplitAction> subTasks = IntStream.range(0, numberOfRolls / divider).boxed()
          .map(i -> createSubTask(divider))
          .collect(Collectors.toCollection(LinkedList::new));
      int remaining = numberOfRolls % divider;
      if (remaining > 0) {
        subTasks.add(createSubTask(remaining));
      }
      return subTasks;
    }

    private RollsSplitAction createSubTask(int partialRolls) {
      return new RollsSplitAction(partialRolls, numberOfDice,  numberOfSides);
    }
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  class DiceSplitAction extends RecursiveTask<Long> {

    static final int DICE_SPLIT_THRESHOLD = 128;

    Integer numberOfDice;
    Integer numberOfSides;

    @Override
    protected Long compute() {
      if (numberOfDice > DICE_SPLIT_THRESHOLD) {
        return ForkJoinTask.invokeAll(createSubTasks(DICE_SPLIT_THRESHOLD)).stream()
            .map(ForkJoinTask::join)
            .reduce(0L, Long::sum);
      } else {
        return computeLocally();
      }
    }

    private Long computeLocally() {
      var random = randomGeneratorProvider.getRandom();
      var sum = 0L;
      for (var dice = 0; dice < numberOfDice; ++dice) {
        var value = random.nextInt(numberOfSides) + 1;
        sum += value;
      }
      return sum;
    }

    private Collection<DiceSplitAction> createSubTasks(int divider) {
      List<DiceSplitAction> subTasks = IntStream.range(0, numberOfDice / divider).boxed()
          .map(i -> createSubTask(divider))
          .collect(Collectors.toCollection(LinkedList::new));
      int remaining = numberOfDice % divider;
      if (remaining > 0) {
        subTasks.add(createSubTask(remaining));
      }
      return subTasks;
    }

    private DiceSplitAction createSubTask(int partialDice) {
      return new DiceSplitAction(partialDice,  numberOfSides);
    }
  }
}
