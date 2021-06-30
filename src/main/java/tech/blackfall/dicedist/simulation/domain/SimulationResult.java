package tech.blackfall.dicedist.simulation.domain;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimulationResult {

  Long id;
  int numberOfDice;
  int numberOfSides;
  int numberOfRolls;
  List<SimulationPartialResult> values;
}
