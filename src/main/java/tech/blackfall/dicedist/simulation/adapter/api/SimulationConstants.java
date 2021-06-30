package tech.blackfall.dicedist.simulation.adapter.api;

import tech.blackfall.dicedist.simulation.domain.SimulationMode;

class SimulationConstants {

  private SimulationConstants() {
  }

  static final int MIN_NUMBER_OF_DICE = 1;
  static final int MIN_NUMBER_OF_ROLLS = 1;
  static final int MIN_NUMBER_OF_SIDES = 4;
  static final int MAX_NUMBER_OF_DICE = Integer.MAX_VALUE - 1;
  static final int MAX_NUMBER_OF_ROLLS = Integer.MAX_VALUE - 1;
  static final int MAX_NUMBER_OF_SIDES = Integer.MAX_VALUE - 1;

  static final int DEFAULT_NUMBER_OF_DICE = 3;
  static final int DEFAULT_NUMBER_OF_ROLLS = 100;
  static final int DEFAULT_NUMBER_OF_SIDES = 6;
  static final SimulationMode DEFAULT_SIMULATION_MODE = SimulationMode.ITER;
  static final String DEFAULT_SIMULATION_MODE_STR = "ITER";

  static final String DEFAULT_NUMBER_OF_DICE_STR = "" + DEFAULT_NUMBER_OF_DICE;
  static final String DEFAULT_NUMBER_OF_ROLLS_STR = "" + DEFAULT_NUMBER_OF_ROLLS;
  static final String DEFAULT_NUMBER_OF_SIDES_STR = "" + DEFAULT_NUMBER_OF_SIDES;

}
