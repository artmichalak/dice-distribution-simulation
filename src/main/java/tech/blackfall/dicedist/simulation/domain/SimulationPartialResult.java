package tech.blackfall.dicedist.simulation.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimulationPartialResult {

  Long id;
  long totalValue;
  int numberOfOccurrences;
}
