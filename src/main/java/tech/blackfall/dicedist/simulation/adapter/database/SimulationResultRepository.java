package tech.blackfall.dicedist.simulation.adapter.database;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import tech.blackfall.dicedist.simulation.domain.DiceSidesStatisticsEntry;
import tech.blackfall.dicedist.simulation.domain.TotalValueOccurrenceEntry;

interface SimulationResultRepository extends CrudRepository<SimulationResultEntity, Long> {

  @Query("""
      SELECT new tech.blackfall.dicedist.simulation.domain.DiceSidesStatisticsEntry(
        s.numberOfDice, s.numberOfSides, SUM(s.numberOfRolls), COUNT(s.id))
        FROM SimulationResultEntity AS s
        GROUP BY s.numberOfDice, s.numberOfSides ORDER BY s.numberOfDice, s.numberOfSides
      """)
  List<DiceSidesStatisticsEntry> fetchGlobalStatistics();

  @Query("""
      SELECT new tech.blackfall.dicedist.simulation.domain.TotalValueOccurrenceEntry(pr.totalValue, SUM(pr.occurrences))
        FROM SimulationResultEntity sr JOIN sr.values pr
        WHERE sr.numberOfDice = :numberOfDice AND sr.numberOfSides = :numberOfSides
        GROUP BY pr.totalValue ORDER BY pr.totalValue
      """)
  List<TotalValueOccurrenceEntry> fetchRelativeDistribution(int numberOfDice, int numberOfSides);

}
