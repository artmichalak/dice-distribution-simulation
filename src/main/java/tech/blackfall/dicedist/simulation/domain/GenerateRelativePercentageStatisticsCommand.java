package tech.blackfall.dicedist.simulation.domain;

import lombok.Value;

@Value(staticConstructor = "of")
public class GenerateRelativePercentageStatisticsCommand {

  int numberOfDice;
  int numberOfSides;
}
