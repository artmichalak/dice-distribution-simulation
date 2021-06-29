package tech.blackfall.dicedist.simulation.adapter.api;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import tech.blackfall.dicedist.simulation.kernel.domain.RandomGeneratorProvider;

@Component
@Primary
@RequiredArgsConstructor
class SimulationMocker implements RandomGeneratorProvider {

  private static final int LOOP_INDEFINITELY = -1;

  private final Random random = mock(Random.class);

  private int[] prepareMockArray(int first, int[] rest) {
    int[] array = new int[rest.length + 1];
    array[0] = first;
    System.arraycopy(rest, 0, array, 1, rest.length);
    return array;
  }

  void loopNextRandomValues(int first, int... rest) {
    when(random.nextInt(anyInt()))
        .thenAnswer(new LoopingAnswer(prepareMockArray(first, rest)));
  }

  void mockNextRandomValues(int first, int... rest) {
    when(random.nextInt(anyInt()))
        .thenAnswer(new LoopingAnswer(prepareMockArray(first, rest), 1));
  }

  void loopNextRandomValuesFromTo(int from, int to) {
    when(random.nextInt(anyInt()))
        .thenAnswer(new FromToAnswer(from, to));
  }

  void mockNextRandomValuesFromTo(int from, int to) {
    when(random.nextInt(anyInt()))
        .thenAnswer(new FromToAnswer(from, to, 1));
  }

  static class LoopingAnswer implements Answer<Integer> {

    private final int[] array;
    private final int maxLoops;

    private int index = 0;
    private int loop = 0;

    LoopingAnswer(int[] array) {
      this(array, LOOP_INDEFINITELY);
    }

    LoopingAnswer(int[] array, int loops) {
      this.array = array;
      this.maxLoops = loops;
    }

    @Override
    public Integer answer(InvocationOnMock invocation) {
      Integer next = array[index];
      index = (index + 1) % array.length;
      if (maxLoops >= 0 && index == 0 && ++loop > maxLoops) {
        throw new IllegalStateException("Max loops exceeded: " + maxLoops);
      }
      return next;
    }
  }

  static class FromToAnswer implements Answer<Integer> {

    private final int startingValue;
    private final int upperBound;
    private final int maxLoops;

    private int currentValue;
    private int loop = 0;

    FromToAnswer(int startingValue, int upperBound) {
      this(startingValue, upperBound, LOOP_INDEFINITELY);
    }

    FromToAnswer(int startingValue, int upperBound, int maxLoops) {
      this.startingValue = startingValue;
      this.upperBound = upperBound;
      this.maxLoops = maxLoops;
      this.currentValue = startingValue;
    }

    @Override
    public Integer answer(InvocationOnMock invocation) {
      int returnValue = currentValue++;
      if (maxLoops >= 0 && currentValue == upperBound && ++loop > maxLoops) {
        throw new IllegalStateException("Max loops exceeded: " + maxLoops);
      }
      if (currentValue == upperBound) {
        currentValue = startingValue;
      }
      return returnValue;
    }
  }


  @Override
  public Random getRandom() {
    return random;
  }
}
