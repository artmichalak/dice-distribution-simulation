package tech.blackfall.dicedist.simulation.domain;

import java.util.List;
import lombok.Value;

@Value
public class DiceSidesRelativePercentageResult {

  int numberOfDice;
  int numberOfSides;
  List<DiceSidesPartialPercentageResult> values;
}
