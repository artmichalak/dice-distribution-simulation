package tech.blackfall.dicedist.simulation.domain;

public interface SimulationDistributionProvider {

  SimulationResult runSimulation(RunSimulationCommand cmd);

  boolean supports(SimulationMode mode);
}
