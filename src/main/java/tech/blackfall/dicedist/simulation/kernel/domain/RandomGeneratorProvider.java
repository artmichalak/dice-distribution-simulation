package tech.blackfall.dicedist.simulation.kernel.domain;

import java.util.Random;

public interface RandomGeneratorProvider {
  Random getRandom();
}
