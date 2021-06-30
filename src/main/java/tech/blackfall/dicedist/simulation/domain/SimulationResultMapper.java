package tech.blackfall.dicedist.simulation.domain;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.blackfall.dicedist.simulation.adapter.api.SimulationResultDto;

@Mapper
public interface SimulationResultMapper {

  SimulationResultMapper INSTANCE = Mappers.getMapper(SimulationResultMapper.class);

  SimulationResultDto mapToResponse(SimulationResult simulationResult);
}
