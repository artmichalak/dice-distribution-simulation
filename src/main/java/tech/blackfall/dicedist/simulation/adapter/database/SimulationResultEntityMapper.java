package tech.blackfall.dicedist.simulation.adapter.database;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tech.blackfall.dicedist.simulation.domain.SimulationPartialResult;
import tech.blackfall.dicedist.simulation.domain.SimulationResult;

@Mapper
interface SimulationResultEntityMapper {

  SimulationResultEntityMapper INSTANCE = Mappers.getMapper(SimulationResultEntityMapper.class);

  SimulationResultEntity mapToEntity(SimulationResult simulationResult);

  SimulationResult mapToDomain(SimulationResultEntity entity);

  @Mapping(source = "numberOfOccurrences", target = "occurrences")
  SimulationPartialResultEntity mapToEntity(SimulationPartialResult partialResult);

  @Mapping(source = "occurrences", target = "numberOfOccurrences")
  SimulationPartialResult mapToDomain(SimulationPartialResultEntity entity);
}
