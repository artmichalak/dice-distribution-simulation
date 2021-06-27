package tech.blackfall.dicedist.simulation.domain;

import lombok.Value;

@Value(staticConstructor = "of")
public class RunSimulationCommand {

  int numberOfDice;
  int numberOfSides;
  int numberOfRolls;
}
