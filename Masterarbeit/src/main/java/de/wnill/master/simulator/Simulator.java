package de.wnill.master.simulator;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wnill.master.core.bidgeneration.FullScheduleGenerator;
import de.wnill.master.core.scheduling.NeighborhoodSearch;
import de.wnill.master.core.scheduling.second.MinVarAndIdleShifter;
import de.wnill.master.core.valuation.NonMonotonicLatenessValuation;
import de.wnill.master.core.wdp.EveryOneIsAWinner;
import de.wnill.master.evaluation.EvaluationUtils;
import de.wnill.master.simulator.types.Condition;
import de.wnill.master.simulator.types.Job;
import de.wnill.master.simulator.types.OrderType;
import de.wnill.master.simulator.types.Scenario;

public class Simulator {

  private static final Logger logger = LoggerFactory.getLogger(Simulator.class);

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
    // scenario.setTruckCount(6);
    // scenario.setOrderAheadMinimum(14);
    // scenario.setRoundtripTime(Duration.ofMinutes(105));
    // scenario.setOptimalDeliveryInterval(Duration.ofMinutes(15));
    // Constraints.setTruckPauseAfter(Duration.ofMinutes(220));
    // Constraints.setTruckPauseDuration(Duration.ofMinutes(45));
    //
    // scenario.setStartTime(LocalTime.of(0, 0));
    // scenario.setEndTime(LocalTime.of(21, 0));
    // scenario.setFirstDockingTime(LocalTime.of(3, 0));
    // scenario.setOffloadingDuration(Duration.ofMinutes(10));
    // scenario.setOrderAheadMaximum(scenario.getOrderAheadMinimum());
    // scenario.setOrderType(OrderType.SEQUENTIAL);
    // scenario.setSchedulingAlgorithm(new NeighborhoodSearch());
    // scenario.setValuator(new NonMonotonicLatenessValuation());
    // scenario.setWinnerDeterminationAlgorithm(new SimpleTreeSearch());


    Scenario scenario = new Scenario();

    scenario.setTruckCount(6);
    scenario.setOrderAheadMinimum(14);
    scenario.setRoundtripTime(Duration.ofMinutes(105));
    scenario.setOptimalDeliveryInterval(Duration.ofMinutes(15));
    Constraints.setTruckPauseAfter(Duration.ofMinutes(220));
    Constraints.setTruckPauseDuration(Duration.ofMinutes(45));
    scenario.setSecondPassProcessor(new MinVarAndIdleShifter(0));


    scenario.setStartTime(LocalTime.of(0, 0));
    scenario.setEndTime(LocalTime.of(21, 0));
    scenario.setFirstDockingTime(LocalTime.of(3, 0));
    scenario.setOffloadingDuration(Duration.ofMinutes(10));
    scenario.setOrderAheadMaximum(scenario.getOrderAheadMinimum());
    scenario.setOrderType(OrderType.BUNDLE);
    scenario.setSchedulingAlgorithm(new NeighborhoodSearch());
    scenario.setValuator(new NonMonotonicLatenessValuation());
    scenario.setWinnerDeterminationAlgorithm(new EveryOneIsAWinner());
    scenario.setBidGenerator(new FullScheduleGenerator());



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

    LinkedList<Job> schedule = EvaluationUtils.unionSchedules(schedules);
    double mean = EvaluationUtils.calculateMeanDelivery(schedule);
    logger.info("-----------------------------------------------");
    logger.info("Mean: " + mean);
    logger.info("StdDev: " + EvaluationUtils.calculateStdDev(mean, schedule));
    logger.info("AvgIdleTimes: " + EvaluationUtils.calcAvgIdleTimes(schedules));
    logger.info("Scenario: " + scenario);
    logger.info("-----------------------------------------------");
  }

  /**
   * @return the resultMap
   */
  public Map<Integer, List<List<Job>>> getResultMap() {
    return resultMap;
  }

}
