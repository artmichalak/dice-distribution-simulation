package tech.blackfall.dicedist.simulation.adapter.api;

import lombok.Value;

@Value
class DiceSidesStatisticsEntryDto {

  String diceSides;
  long totalRolls;
  long simulationsPerformed;
}
