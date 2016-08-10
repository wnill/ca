package de.wnill.master.simulator;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.wnill.master.core.scheduling.NeighborhoodSearch;
import de.wnill.master.core.scheduling.second.NoBreaksScheduleShifter;
import de.wnill.master.core.valuation.NonMonotonicLatenessValuation;
import de.wnill.master.core.wdp.SimpleTreeSearch;
import de.wnill.master.simulator.types.Condition;
import de.wnill.master.simulator.types.Job;
import de.wnill.master.simulator.types.OrderType;
import de.wnill.master.simulator.types.Scenario;

public class Simulator {

  /** Maps scenario hashcode -> list of all schedules */
  private Map<Integer, List<List<Job>>> resultMap = new HashMap<>();

  public void runScenario(Scenario scenario) {

    Engine engine = new Engine(new Condition() {

      @Override
      public boolean isMet() {
        // TODO Auto-generated method stub
        return false;
      }
    }, scenario);
    engine.registerResultCallback(this);
    engine.run();
  }


  public static void main(String[] args) {


    // Configure a scenario
    // Scenario scenario = new Scenario();
    // scenario.setEndTime(LocalTime.of(23, 59));
    // scenario.setFirstDockingTime(LocalTime.of(8, 0));
    // scenario.setOffloadingDuration(Duration.ofMinutes(10));
    // scenario.setOptimalDeliveryInterval(Duration.ofMinutes(27));
    // scenario.setOrderAheadMaximum(12);
    // scenario.setOrderAheadMinimum(6);
    // scenario.setOrderType(OrderType.BUNDLE);
    // scenario.setRoundtripTime(Duration.ofMinutes(145));
    // scenario.setSchedulingAlgorithm(new NeighborhoodSearch());
    // scenario.setStartTime(LocalTime.of(0, 0));
    // scenario.setTruckCount(2);
    // scenario.setTruckBreaksDue(new ArrayList<>());
    // scenario.setTruckBreakDurations(new ArrayList<>());
    // scenario.setValuator(new NonMonotonicLatenessValuation());
    // scenario.setWinnerDeterminationAlgorithm(new SimpleTreeSearch());
    // scenario.setSecondPassProcessor(new NoBreaksScheduleShifter());

    Scenario scenario = new Scenario();
    scenario.setEndTime(LocalTime.of(23, 59));
    scenario.setFirstDockingTime(LocalTime.of(8, 0));
    scenario.setOffloadingDuration(Duration.ofMinutes(10));
    scenario.setOptimalDeliveryInterval(Duration.ofMinutes(47));
    scenario.setOrderAheadMaximum(14);
    scenario.setOrderAheadMinimum(4);
    scenario.setOrderType(OrderType.BUNDLE);
    scenario.setRoundtripTime(Duration.ofMinutes(57));
    scenario.setSchedulingAlgorithm(new NeighborhoodSearch());
    scenario.setStartTime(LocalTime.of(0, 0));
    scenario.setTruckCount(3);
    scenario.setTruckBreaksDue(new ArrayList<>());
    scenario.setTruckBreakDurations(new ArrayList<>());
    scenario.setValuator(new NonMonotonicLatenessValuation());
    scenario.setWinnerDeterminationAlgorithm(new SimpleTreeSearch());
    scenario.setSecondPassProcessor(new NoBreaksScheduleShifter());



    Engine engine = new Engine(new Condition() {

      @Override
      public boolean isMet() {
        // TODO Auto-generated method stub
        return false;
      }
    }, scenario);
    engine.registerResultCallback(new Simulator());
    engine.run();

  }

  public void reportResults(Scenario scenario, List<List<Job>> schedules) {

    resultMap.put(scenario.hashCode(), schedules);

    synchronized (this) {
      notifyAll();
    }
  }


  /**
   * @return the resultMap
   */
  public Map<Integer, List<List<Job>>> getResultMap() {
    return resultMap;
  }

}
