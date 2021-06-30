package tech.blackfall.dicedist.simulation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiceSidesStatisticsEntry {

  Integer dice;
  Integer sides;
  Long totalRolls;
  Long simulationsPerformed;
}
