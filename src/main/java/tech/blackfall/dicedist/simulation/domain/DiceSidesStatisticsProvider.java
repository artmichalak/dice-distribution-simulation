package tech.blackfall.dicedist.simulation.domain;

public interface DiceSidesStatisticsProvider {

  DiceSidesStatisticsResult fetchGlobalDiceSidesStatistics();

  DiceSidesRelativePercentageResult generateRelativePercentageStatistics(
      GenerateRelativePercentageStatisticsCommand cmd);
}
