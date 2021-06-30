package tech.blackfall.dicedist.simulation.adapter.api;

import lombok.Value;

@Value
public class SimulationPartialResultDto {

  long id;
  long totalValue;
  int numberOfOccurrences;
}
