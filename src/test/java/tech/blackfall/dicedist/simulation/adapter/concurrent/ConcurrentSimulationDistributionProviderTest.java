package tech.blackfall.dicedist.simulation.adapter.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tech.blackfall.dicedist.simulation.domain.RunSimulationCommand;
import tech.blackfall.dicedist.simulation.domain.SimulationMode;
import tech.blackfall.dicedist.simulation.domain.SimulationResult;
import tech.blackfall.dicedist.simulation.kernel.domain.ThreadLocalRandomGeneratorProvider;

class ConcurrentSimulationDistributionProviderTest {

  private final ConcurrentSimulationDistributionProvider provider = new ConcurrentSimulationDistributionProvider(
      new ThreadLocalRandomGeneratorProvider());

  @Test
  void shouldExecuteWithoutContentionWithThreadLocalRandom() {
    int numberOfDice = 300;
    int numberOfRolls = 10000;
    long upperBound = numberOfDice * 6;

    SimulationResult result = provider
        .runSimulation(RunSimulationCommand.of(SimulationMode.CONC, numberOfDice, 6, numberOfRolls));

    result.getValues().forEach(partialResult -> {

      assertThat(partialResult.getTotalValue())
          .isGreaterThanOrEqualTo(numberOfDice)
          .isLessThanOrEqualTo(upperBound);

      assertThat(partialResult.getNumberOfOccurrences()).isLessThanOrEqualTo(numberOfRolls);
    });

  }

}
