package tech.blackfall.dicedist.simulation.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimulationService {

  private final SimulationDistributionProvider simulationDistributionProvider;

  public SimulationResult runSimulation(RunSimulationCommand cmd) {
    return simulationDistributionProvider.runSimulation(cmd);
  }
}
