package de.wnill.master.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wnill.master.core.bidgeneration.FullScheduleGenerator;
import de.wnill.master.core.scheduling.NeighborhoodSearch;
import de.wnill.master.core.scheduling.second.MinVarAndIdleShifter;
import de.wnill.master.core.valuation.NonMonotonicLatenessValuation;
import de.wnill.master.core.valuation.Valuator;
import de.wnill.master.core.wdp.EveryOneIsAWinner;
import de.wnill.master.core.wdp.SimpleTreeSearch;
import de.wnill.master.simulator.Config;
import de.wnill.master.simulator.Constraints;
import de.wnill.master.simulator.Simulator;
import de.wnill.master.simulator.types.Job;
import de.wnill.master.simulator.types.OrderType;
import de.wnill.master.simulator.types.Scenario;

public class StructuredDominantScenarioGenerator {

  private static final Logger logger = LoggerFactory
      .getLogger(StructuredDominantScenarioGenerator.class);

  private final String SIM_LOG_PATH = "sim/sim_results.csv";

  private int TRUCK_COUNT = 4;

  private int DELIVERIES = 20;

  private int DELIVERY_DURATION = 30;

  private int TARGET_INTERVAL = 5;

  private int PAUSE_INTERVAL = 180;

  private int PAUSE_DURATION = 30;

  private int MAX_PI = 10000;

  // private ResultsCollector collector = new ResultsCollector();


  public void findOptimalDemoScenario() {
    Config.setEnableVisualisation(false);
    Simulator simulator = new Simulator();
    Valuator val = new NonMonotonicLatenessValuation();

    int weaklyDominated = 0;
    int strictlyDominated = 0;
    int validRuns = 0;

    // for (int i = 0; i <= MAX_PI; i++) {
    Scenario scenario = new Scenario();
    scenario.setStartTime(LocalTime.of(0, 0));
    scenario.setEndTime(LocalTime.of(15, 00));
    scenario.setFirstDockingTime(LocalTime.of(8, 0));
    scenario.setOffloadingDuration(Duration.ofMinutes(10));


    Constraints.setTruckPauseAfter(Duration.ofMinutes(PAUSE_INTERVAL));
    Constraints.setTruckPauseDuration(Duration.ofMinutes(PAUSE_DURATION));
    scenario.setTruckCount(TRUCK_COUNT);
    scenario.setOrderAheadMinimum(DELIVERIES);
    scenario.setOrderAheadMaximum(DELIVERIES);
    scenario.setRoundtripTime(Duration.ofMinutes(DELIVERY_DURATION));
    scenario.setOptimalDeliveryInterval(Duration.ofMinutes(TARGET_INTERVAL));


    int result = executeComparingRun(simulator, val, scenario);

    if (result > 0) {
      weaklyDominated++;
    }
    if (result == 2) {
      strictlyDominated++;
    }
    if (result > -1) {
      validRuns++;
    }
    // }

    DecimalFormat df = new DecimalFormat("#.00");
    logger.info("Found weakly dominant Schedules in "
        + df.format((double) weaklyDominated / validRuns * 100d) + "%, strictly dominated in "
        + df.format((double) strictlyDominated / validRuns * 100d) + " of simulation runs");

    // collector.printResults();
  }

  /**
   * Compares a sequential run with bundle run (testing different parameters).
   * 
   * @param simulator
   * @param val
   * @param randomScenario
   * @return 1 if schedules improved, 0 if not, -1 if schedule were not improvable (already
   *         perfect).
   */
  private int executeComparingRun(Simulator simulator, Valuator val, Scenario randomScenario) {
    randomScenario.setOrderType(OrderType.SEQUENTIAL);

    // TODO remove
    randomScenario.setValuator(new NonMonotonicLatenessValuation());
    randomScenario.setWinnerDeterminationAlgorithm(new SimpleTreeSearch());
    randomScenario.setSecondPassProcessor(null);
    randomScenario.setBidGenerator(null);
    randomScenario.setSchedulingAlgorithm(new NeighborhoodSearch());

    simulator.runScenario(randomScenario);


    synchronized (simulator) {
      try {
        while (simulator.getResultMap().get(randomScenario.hashCode()) == null) {
          simulator.wait();
        }
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    List<List<Job>> completeSchedule = simulator.getResultMap().remove(randomScenario.hashCode());
    LinkedList<Job> deliveries = EvaluationUtils.unionSchedules(completeSchedule);
    double seqMeanDelivery = EvaluationUtils.calculateMeanDelivery(deliveries);
    // double seqVariance = EvaluationUtils.calculateVariance(seqMeanDelivery, deliveries);
    double seqStdDev = EvaluationUtils.calculateStdDev(seqMeanDelivery, deliveries);
    long seqIdleTimes = EvaluationUtils.calculateIdleTimes(completeSchedule);

    logResult(randomScenario, 0, seqMeanDelivery, seqStdDev,
        EvaluationUtils.calcAvgIdleTimes(completeSchedule));

    logger.info("SEQ StdDev: " + seqStdDev + ", Idle: " + seqIdleTimes);
    // collector.putResult(OrderType.SEQUENTIAL, seqIdleTimes, seqVariance, seqMeanDelivery);

    if (seqStdDev == 0 && seqIdleTimes == 0) {
      return -1;
    }

    Scenario randomBundleScenario = randomScenario;
    randomBundleScenario.setOrderType(OrderType.BUNDLE);

    for (int pi = 0; pi < MAX_PI; pi++) {

      randomBundleScenario.setSchedulingAlgorithm(new NeighborhoodSearch());
      randomBundleScenario.setBidGenerator(new FullScheduleGenerator());
      randomBundleScenario.setWinnerDeterminationAlgorithm(new EveryOneIsAWinner());
      randomBundleScenario.setSecondPassProcessor(new MinVarAndIdleShifter(pi));
      simulator.runScenario(randomBundleScenario);

      synchronized (simulator) {
        try {

          while (simulator.getResultMap().get(randomBundleScenario.hashCode()) == null) {
            simulator.wait();
          }
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      completeSchedule = simulator.getResultMap().remove(randomBundleScenario.hashCode());

      deliveries = EvaluationUtils.unionSchedules(completeSchedule);
      double bunMeanDelivery = EvaluationUtils.calculateMeanDelivery(deliveries);
      // double bundleVariance = EvaluationUtils.calculateVariance(bunMeanDelivery, deliveries);
      double bundleStdDev = EvaluationUtils.calculateStdDev(bunMeanDelivery, deliveries);
      long bunIdleTimes = EvaluationUtils.calculateIdleTimes(completeSchedule);
      // collector.putResult(OrderType.BUNDLE, bunIdleTimes, bundleVariance, bunMeanDelivery);

      logResult(randomScenario, pi, bunMeanDelivery, bundleStdDev,
          EvaluationUtils.calcAvgIdleTimes(completeSchedule));


      if (bunIdleTimes < 0) {
        logger.warn("invalid schedule: " + completeSchedule);
        continue;
      }

      if (bunIdleTimes < seqIdleTimes && bundleStdDev < seqStdDev) {
        logger.info("Dominant Alternative BUN StdDev: " + bundleStdDev + ", Idle: " + bunIdleTimes);
        return 2;
      } else if (bunIdleTimes <= seqIdleTimes && bundleStdDev <= seqStdDev) {
        logger.info("Dominant Alternative BUN StdDev: " + bundleStdDev + ", Idle: " + bunIdleTimes);
        return 1;
      } else if (bunIdleTimes > seqIdleTimes && bundleStdDev > seqStdDev) {
        return 0;
      }
    }

    return 0;
  }


  /**
   * Writes the sim results to disk in a CSV File. Pattern:
   * 
   * ALLOC_TYPE, TRUCK_COUNT, DELIVERIES, DELIVERY_DURATION, TARGET_INTERVAL, PAUSE_INTERVAL,
   * PAUSE_DURATION, PI, MEAN_INTERVAL, STD_DEV_IDLE, AVG_TRUCK_WAIT
   * 
   * @param scenario
   * @param completeSchedule
   * @param pi
   */
  private void logResult(Scenario scenario, int pi, double meanInterval, double stdDev,
      double avgTruckWait) {

    File file = new File(SIM_LOG_PATH);

    // if file doesnt exists, then create it
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
      BufferedWriter bw = new BufferedWriter(fw);

      StringBuilder sb = new StringBuilder();
      sb.append(scenario.getOrderType()).append(",").append(scenario.getTruckCount()).append(",")
          .append(scenario.getOrderAheadMinimum()).append(",")
          .append(scenario.getRoundtripTime().toMinutes()).append(",")
          .append(scenario.getOptimalDeliveryInterval().toMinutes()).append(",")
          .append(Constraints.getTruckPauseAfter().toMinutes()).append(",")
          .append(Constraints.getTruckPauseDuration().toMinutes()).append(",").append(pi)
          .append(",").append(meanInterval).append(",").append(stdDev).append(",")
          .append(avgTruckWait);

      if (scenario.getOrderType().equals(OrderType.SEQUENTIAL) && stdDev == 0 && avgTruckWait == 0) {
        sb.append(",").append("SKIPPED");
      }


      bw.write(sb.append("\n").toString());
      bw.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }



  public static void main(String[] args) {
    StructuredDominantScenarioGenerator gen = new StructuredDominantScenarioGenerator();
    gen.findOptimalDemoScenario();
  }
}
