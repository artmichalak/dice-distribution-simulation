package tech.blackfall.dicedist.simulation.domain;

import java.util.List;
import lombok.Value;

@Value
public class SimulationResult {

  List<SimulationPartialResult> values;
}
