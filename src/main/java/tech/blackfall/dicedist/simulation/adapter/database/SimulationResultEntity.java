package tech.blackfall.dicedist.simulation.adapter.database;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
class SimulationResultEntity {

  @Id
  @GeneratedValue
  private Long id;

  private Integer numberOfRolls;

  private Integer numberOfDice;

  private Integer numberOfSides;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "simulation_id")
  private Set<SimulationPartialResultEntity> values;
}
