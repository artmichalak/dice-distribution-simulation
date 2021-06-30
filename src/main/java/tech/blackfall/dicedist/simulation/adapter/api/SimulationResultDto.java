package tech.blackfall.dicedist.simulation.adapter.api;

import java.util.List;
import lombok.Value;

@Value
public class SimulationResultDto {

  List<SimulationPartialResultDto> values;
}
