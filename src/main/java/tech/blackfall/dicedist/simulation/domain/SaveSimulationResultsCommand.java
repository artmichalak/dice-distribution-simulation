package tech.blackfall.dicedist.simulation.domain;

import lombok.Value;

@Value(staticConstructor = "of")
public class SaveSimulationResultsCommand {

  SimulationResult result;
}
