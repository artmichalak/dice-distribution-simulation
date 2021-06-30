package tech.blackfall.dicedist.simulation.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiceSidesStatisticsResult {

  List<DiceSidesStatisticsEntry> entries;
}
