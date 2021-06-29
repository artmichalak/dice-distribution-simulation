package tech.blackfall.dicedist.simulation.kernel.domain;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class ThreadLocalRandomGeneratorProvider implements RandomGeneratorProvider {

  @Override
  public Random getRandom() {
    return ThreadLocalRandom.current();
  }
}
