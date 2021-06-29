package tech.blackfall.dicedist.simulation.adapter.api;

class SimulationConstants {

  private SimulationConstants() {
  }

  static final int MIN_NUMBER_OF_DICE = 1;
  static final int MIN_NUMBER_OF_ROLLS = 1;
  static final int MIN_NUMBER_OF_SIDES = 4;
  static final int MAX_NUMBER_OF_DICE = Integer.MAX_VALUE - 1;
  static final int MAX_NUMBER_OF_ROLLS = Integer.MAX_VALUE - 1;
  static final int MAX_NUMBER_OF_SIDES = Integer.MAX_VALUE - 1;

  static final String DEFAULT_NUMBER_OF_DICE = "3";
  static final String DEFAULT_NUMBER_OF_ROLLS = "100";
  static final String DEFAULT_NUMBER_OF_SIDES = "6";

  static final String DEFAULT_SIMULATION_MODE = "ITER";
}
