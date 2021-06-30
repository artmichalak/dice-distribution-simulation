package tech.blackfall.dicedist.simulation.adapter.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.blackfall.dicedist.simulation.domain.SaveSimulationResultsCommand;
import tech.blackfall.dicedist.simulation.domain.SimulationPersister;
import tech.blackfall.dicedist.simulation.domain.SimulationResult;

@Component
@Slf4j
@RequiredArgsConstructor
class SimulationPersisterAdapter implements SimulationPersister {

  private final SimulationResultRepository repository;

  @Override
  public SimulationResult saveSimulationResult(SaveSimulationResultsCommand cmd) {
    SimulationResult result = cmd.getResult();
    log.info("Saving simulation result: [dice={}, sides={}, rolls={}]", result.getNumberOfDice(),
        result.getNumberOfSides(), result.getNumberOfRolls());

    SimulationResultEntity entity = SimulationResultEntityMapper.INSTANCE.mapToEntity(result);
    entity = repository.save(entity);

    return SimulationResultEntityMapper.INSTANCE.mapToDomain(entity);
  }
}
