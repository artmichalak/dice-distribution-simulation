package tech.blackfall.dicedist.simulation.adapter.api;

import java.util.List;
import lombok.Value;

@Value
public class SimulationResultDto {

  long id;
  List<SimulationPartialResultDto> values;
}
