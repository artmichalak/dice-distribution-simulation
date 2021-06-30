package tech.blackfall.dicedist.simulation.domain;

public interface SimulationPersister {

  SimulationResult saveSimulationResult(SaveSimulationResultsCommand cmd);
}
