package tech.blackfall.dicedist.simulation.adapter.database;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import tech.blackfall.dicedist.simulation.domain.DiceSidesStatisticsEntry;

interface SimulationResultRepository extends CrudRepository<SimulationResultEntity, Long> {

  @Query("""
      SELECT new tech.blackfall.dicedist.simulation.domain.DiceSidesStatisticsEntry(
        s.numberOfDice, s.numberOfSides, SUM(s.numberOfRolls), COUNT(s.id))
        FROM SimulationResultEntity AS s
        GROUP BY s.numberOfDice, s.numberOfSides ORDER BY s.numberOfDice, s.numberOfSides
      """)
  List<DiceSidesStatisticsEntry> fetchGlobalStatistics();
}
