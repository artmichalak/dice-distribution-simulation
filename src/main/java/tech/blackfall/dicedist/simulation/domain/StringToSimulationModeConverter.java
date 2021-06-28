package tech.blackfall.dicedist.simulation.domain;

import org.springframework.core.convert.converter.Converter;

public class StringToSimulationModeConverter implements Converter<String, SimulationMode> {

  @Override
  public SimulationMode convert(String source) {
    try {
      return SimulationMode.valueOf(source.toUpperCase());
    } catch (IllegalArgumentException e) {
      return SimulationMode.ITER;
    }
  }
}
