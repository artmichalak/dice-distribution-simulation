package tech.blackfall.dicedist.simulation.domain;

import lombok.Value;

@Value(staticConstructor = "of")
public class RunSimulationCommand {

  SimulationMode simulationMode;
  int numberOfDice;
  int numberOfSides;
  int numberOfRolls;
}
