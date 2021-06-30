package tech.blackfall.dicedist.simulation.adapter.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
class SimulationPartialResultEntity {

  @Id
  @GeneratedValue
  private Long id;

  private Long totalValue;

  private Integer occurrences;
}
