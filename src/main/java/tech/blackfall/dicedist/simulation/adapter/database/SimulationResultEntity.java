package tech.blackfall.dicedist.simulation.adapter.database;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
class SimulationResultEntity {

  @Id
  @GeneratedValue
  private Long id;

  private Integer rolls;

  private Integer dices;

  private Integer sides;

  @OneToMany
  private Set<SimulationPartialResultEntity> values;
}
