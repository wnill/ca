package de.wnill.master.simulator;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.wnill.master.core.scheduling.NaiveSymetricPenalties;
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

    // // Configure BUNDLE scenario
    // Scenario scenario = new Scenario();
    // scenario.setEndTime(LocalTime.of(14, 0));
    // scenario.setFirstDockingTime(LocalTime.of(12, 0));
    // scenario.setOffloadingDuration(Duration.ofMinutes(5));
    // scenario.setOptimalDeliveryInterval(Duration.ofMinutes(10));
    // scenario.setOrderAheadMaximum(6);
    // scenario.setOrderAheadMinimum(6);
    // scenario.setOrderType(OrderType.BUNDLE);
    // scenario.setRoundtripTime(Duration.ofMinutes(20));
    // scenario.setSchedulingAlgorithm(new NaiveSymetricPenalties());
    // scenario.setValuator(new NonMonotonicLatenessValuation());
    // scenario.setWinnerDeterminationAlgorithm(new SimpleTreeSearch());
    // scenario.setStartTime(LocalTime.of(11, 40));
    // scenario.setTruckCount(2);


    // Configure SEQUENTIAL scenario
    Scenario scenario = new Scenario();
    scenario.setEndTime(LocalTime.of(14, 0));
    scenario.setFirstDockingTime(LocalTime.of(8, 0));
    scenario.setOffloadingDuration(Duration.ofMinutes(5));
    scenario.setOptimalDeliveryInterval(Duration.ofMinutes(23));
    scenario.setOrderAheadMaximum(12);
    scenario.setOrderAheadMinimum(3);
    scenario.setOrderType(OrderType.BUNDLE);
    scenario.setRoundtripTime(Duration.ofMinutes(105));
    scenario.setSchedulingAlgorithm(new NaiveSymetricPenalties());
    scenario.setStartTime(LocalTime.of(0, 0));
    scenario.setTruckCount(1);
    scenario.setTruckBreaksDue(Arrays.asList(LocalTime.of(11, 03)));
    scenario.setTruckBreakDurations(Arrays.asList(Duration.ofMinutes(34)));
    scenario.setValuator(new NonMonotonicLatenessValuation());
    scenario.setWinnerDeterminationAlgorithm(new SimpleTreeSearch());


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
