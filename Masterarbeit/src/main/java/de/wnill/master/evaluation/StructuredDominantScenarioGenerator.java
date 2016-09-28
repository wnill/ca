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

  private static final int ABORT_AFTER_UNCHANGED_RESULTS = 50;

  private static final Logger logger = LoggerFactory
      .getLogger(StructuredDominantScenarioGenerator.class);

  private final String SIM_LOG_PATH = "sim/sim_results.csv";

  private final String SIM_SHORT_LOG_PATH = "sim_short_results.csv";

  // private int TRUCK_MIN = 6;
  //
  // private int TRUCK_MAX = 6;
  //
  // private int TRUCK_DELTA = 1;
  //
  // private int DELIVERIES_MIN = 10;
  //
  // private int DELIVERIES_MAX = 24;
  //
  // private int DELIVERIES_DELTA = 4;
  //
  // private int DELIVERY_DUR_MIN = 30;
  //
  // private int DELIVERY_DUR_MAX = 150;
  //
  // private int DELIVERY_DUR_DELTA = 10;
  //
  // private int TARGET_MIN = 5;
  //
  // private int TARGET_MAX = 20;
  //
  // private int TARGET_DELTA = 5;
  //
  // private int PAUSE_INT_MIN = 180;
  //
  // private int PAUSE_INT_MAX = 270;
  //
  // private int PAUSE_INT_DELTA = 30;
  //
  // private int PAUSE_DUR_MIN = 30;
  //
  // private int PAUSE_DUR_MAX = 45;
  //
  // private int PAUSE_DUR_DELTA = 5;
  //
  // private long MAX_PI = 500;



  private int TRUCK_MIN = 6;

  private int TRUCK_MAX = 6;

  private int TRUCK_DELTA = 1;

  private int DELIVERIES_MIN = 22;

  private int DELIVERIES_MAX = 22;

  private int DELIVERIES_DELTA = 4;

  private int DELIVERY_DUR_MIN = 30;

  private int DELIVERY_DUR_MAX = 30;

  private int DELIVERY_DUR_DELTA = 10;

  private int TARGET_MIN = 10;

  private int TARGET_MAX = 10;

  private int TARGET_DELTA = 5;

  private int PAUSE_INT_MIN = 180;

  private int PAUSE_INT_MAX = 180;

  private int PAUSE_INT_DELTA = 30;

  private int PAUSE_DUR_MIN = 35;

  private int PAUSE_DUR_MAX = 35;

  private int PAUSE_DUR_DELTA = 5;

  private long MAX_PI = 500;



  public void enumerateScenarios() {
    Config.setEnableVisualisation(false);
    Simulator simulator = new Simulator();
    Valuator val = new NonMonotonicLatenessValuation();

    int weaklyDominated = 0;
    int strictlyDominated = 0;
    int validRuns = 0;

    Scenario scenario = new Scenario();
    scenario.setStartTime(LocalTime.of(0, 0));
    scenario.setEndTime(LocalTime.of(21, 00));
    scenario.setFirstDockingTime(LocalTime.of(3, 0));
    scenario.setOffloadingDuration(Duration.ofMinutes(10));

    for (int trucks = TRUCK_MIN; trucks <= TRUCK_MAX; trucks += TRUCK_DELTA) {
      for (int deliveries = DELIVERIES_MIN; deliveries <= DELIVERIES_MAX; deliveries +=
          DELIVERIES_DELTA) {
        for (int duration = DELIVERY_DUR_MIN; duration <= DELIVERY_DUR_MAX; duration +=
            DELIVERY_DUR_DELTA) {
          for (int target = TARGET_MIN; target <= TARGET_MAX; target += TARGET_DELTA) {
            for (int pauseDur = PAUSE_DUR_MIN; pauseDur <= PAUSE_DUR_MAX; pauseDur +=
                PAUSE_DUR_DELTA) {
              for (int pauseInt = PAUSE_INT_MIN; pauseInt <= PAUSE_INT_MAX; pauseInt +=
                  PAUSE_INT_DELTA) {

                Constraints.setTruckPauseAfter(Duration.ofMinutes(pauseInt));
                Constraints.setTruckPauseDuration(Duration.ofMinutes(pauseDur));
                scenario.setTruckCount(trucks);
                scenario.setOrderAheadMinimum(deliveries);
                scenario.setOrderAheadMaximum(deliveries);
                scenario.setRoundtripTime(Duration.ofMinutes(duration));
                scenario.setOptimalDeliveryInterval(Duration.ofMinutes(target));

                // prevent invalid parameter combination
                if (pauseInt < duration + pauseDur) {
                  continue;
                }


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

              }
            }
          }
        }
      }
    }

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
   * @param scenario
   * @return 1 if schedules improved, 0 if not, -1 if schedule were not improvable (already
   *         perfect).
   */
  private int executeComparingRun(Simulator simulator, Valuator val, Scenario scenario) {
    scenario.setOrderType(OrderType.SEQUENTIAL);

    scenario.setValuator(new NonMonotonicLatenessValuation());
    scenario.setWinnerDeterminationAlgorithm(new SimpleTreeSearch());
    scenario.setSecondPassProcessor(null);
    scenario.setBidGenerator(null);
    scenario.setSchedulingAlgorithm(new NeighborhoodSearch());

    simulator.runScenario(scenario);


    synchronized (simulator) {
      try {
        while (simulator.getResultMap().get(scenario.hashCode()) == null) {
          simulator.wait();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    List<List<Job>> completeSchedule = simulator.getResultMap().remove(scenario.hashCode());
    LinkedList<Job> deliveries = EvaluationUtils.unionSchedules(completeSchedule);
    double seqMeanDelivery = EvaluationUtils.calculateMeanDelivery(deliveries);
    double seqStdDev = EvaluationUtils.calculateStdDev(seqMeanDelivery, deliveries);
    double seqIdleTimes = EvaluationUtils.calcAvgIdleTimes(completeSchedule);
    //
    // logOneRun(scenario, 0, seqMeanDelivery, seqStdDev,
    // EvaluationUtils.calcAvgIdleTimes(completeSchedule));

    logger.info("SEQ StdDev: " + seqStdDev + ", Idle: " + seqIdleTimes);

    // if (seqStdDev == 0 && seqIdleTimes == 0) {
    // return -1;
    // }

    Scenario bundleScenario = scenario;
    bundleScenario.setOrderType(OrderType.BUNDLE);

    int run = 0;
    double lastStdDev = -1;

    double bestMean = Double.MAX_VALUE;
    double bestStdDev = Double.MAX_VALUE;
    double bestAvgIdle = Double.MAX_VALUE;
    double bestPi = -1;

    for (int pi = 0; pi < MAX_PI; pi++) {

      bundleScenario.setSchedulingAlgorithm(new NeighborhoodSearch());
      bundleScenario.setBidGenerator(new FullScheduleGenerator());
      bundleScenario.setWinnerDeterminationAlgorithm(new EveryOneIsAWinner());
      bundleScenario.setSecondPassProcessor(new MinVarAndIdleShifter(pi));
      simulator.runScenario(bundleScenario);

      synchronized (simulator) {
        try {

          while (simulator.getResultMap().get(bundleScenario.hashCode()) == null) {
            simulator.wait();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      completeSchedule = simulator.getResultMap().remove(bundleScenario.hashCode());

      deliveries = EvaluationUtils.unionSchedules(completeSchedule);
      double bunMeanDelivery = EvaluationUtils.calculateMeanDelivery(deliveries);
      double bundleStdDev = EvaluationUtils.calculateStdDev(bunMeanDelivery, deliveries);
      double bunIdleTimes = EvaluationUtils.calcAvgIdleTimes(completeSchedule);

      if (bundleStdDev < bestStdDev && bunIdleTimes < bestAvgIdle) {
        bestMean = bunMeanDelivery;
        bestStdDev = bundleStdDev;
        bestAvgIdle = bunIdleTimes;
        bestPi = pi;
      }

      if (bundleStdDev != lastStdDev) {
        lastStdDev = bundleStdDev;
        run = pi;
      }


      // logOneRun(scenario, pi, bunMeanDelivery, bundleStdDev,
      // EvaluationUtils.calcAvgIdleTimes(completeSchedule));


      if (bunIdleTimes < 0 || seqIdleTimes < 0) {
        logger.warn("invalid schedule: " + completeSchedule);
        continue;
      }

      if (bunIdleTimes < seqIdleTimes && bundleStdDev < seqStdDev) {
        logger.info("Dominant Alternative BUN StdDev: " + bundleStdDev + ", Idle: " + bunIdleTimes);
        findEvenBetterSolution(simulator, scenario, pi, seqMeanDelivery, seqStdDev, seqIdleTimes,
            bunMeanDelivery, bundleStdDev, bunIdleTimes);
        return 2;
      } else if (bunIdleTimes <= seqIdleTimes && bundleStdDev <= seqStdDev) {
        logger.info("Dominant Alternative BUN StdDev: " + bundleStdDev + ", Idle: " + bunIdleTimes);
        findEvenBetterSolution(simulator, scenario, pi, seqMeanDelivery, seqStdDev, seqIdleTimes,
            bunMeanDelivery, bundleStdDev, bunIdleTimes);
        return 1;
        // Abort if result did not change for a lot of iterations
      } else if (bunIdleTimes > seqIdleTimes && bundleStdDev > seqStdDev
          && (pi - run) > ABORT_AFTER_UNCHANGED_RESULTS) {


        logShortResults(bundleScenario, seqMeanDelivery, seqStdDev, seqIdleTimes, bestPi, bestMean,
            bestStdDev, bestAvgIdle);
        return 0;
      }
    }

    logShortResults(bundleScenario, seqMeanDelivery, seqStdDev, seqIdleTimes, bestPi, bestMean,
        bestStdDev, bestAvgIdle);
    logger.info("Best result: " + bestMean + ", " + bestStdDev + ", " + bestAvgIdle + " - "
        + bestPi);

    return 0;
  }


  private void findEvenBetterSolution(Simulator simulator, Scenario scenario, int pi,
      double seqMean, double seqStdDev, double seqAvgIdle, double bunMean, double bunStdDev,
      double bundAvgIdle) {

    double bestStdDev = bunStdDev;
    double bestIdleTimes = bundAvgIdle;
    double bestMeanInterval = bunMean;
    double bestPi = pi;

    for (double i = pi; i >= pi - 1; i = i - 0.1) {
      scenario.setSecondPassProcessor(new MinVarAndIdleShifter(pi));
      simulator.runScenario(scenario);

      synchronized (simulator) {
        try {

          while (simulator.getResultMap().get(scenario.hashCode()) == null) {
            simulator.wait();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      List<List<Job>> completeSchedule = simulator.getResultMap().remove(scenario.hashCode());

      LinkedList<Job> deliveries = EvaluationUtils.unionSchedules(completeSchedule);
      double bunMeanDelivery = EvaluationUtils.calculateMeanDelivery(deliveries);
      double newStdDev = EvaluationUtils.calculateStdDev(bunMeanDelivery, deliveries);
      double newIdleTimes = EvaluationUtils.calcAvgIdleTimes(completeSchedule);

      if (newStdDev < bestStdDev && newIdleTimes < bestIdleTimes) {
        bestStdDev = newStdDev;
        bestIdleTimes = newIdleTimes;
        bestPi = i;
        bestMeanInterval = bunMeanDelivery;
      }
    }

    for (double i = pi; i <= pi + 1; i = i + 0.1) {
      scenario.setSecondPassProcessor(new MinVarAndIdleShifter(pi));
      simulator.runScenario(scenario);

      synchronized (simulator) {
        try {

          while (simulator.getResultMap().get(scenario.hashCode()) == null) {
            simulator.wait();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      List<List<Job>> completeSchedule = simulator.getResultMap().remove(scenario.hashCode());

      LinkedList<Job> deliveries = EvaluationUtils.unionSchedules(completeSchedule);
      double bunMeanDelivery = EvaluationUtils.calculateMeanDelivery(deliveries);
      double newStdDev = EvaluationUtils.calculateStdDev(bunMeanDelivery, deliveries);
      double newIdleTimes = EvaluationUtils.calcAvgIdleTimes(completeSchedule);

      if (newStdDev < bestStdDev && newIdleTimes < bestIdleTimes) {
        bestStdDev = newStdDev;
        bestIdleTimes = newIdleTimes;
        bestPi = i;
        bestMeanInterval = bunMeanDelivery;
      }
    }

    if (bestPi != pi) {
      logger.info("found an even better solution!");
      // logOneRun(scenario, bestPi, bestMeanInterval, bestStdDev, bestIdleTimes);
    }

    logShortResults(scenario, seqMean, seqStdDev, seqAvgIdle, bestPi, bestMeanInterval, bestStdDev,
        bestIdleTimes);
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
  private void logOneRun(Scenario scenario, double pi, double meanInterval, double stdDev,
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

  /**
   * Writes the sim results to disk in a CSV File. Pattern:
   * 
   * TRUCK_COUNT, DELIVERIES, DELIVERY_DURATION, TARGET_INTERVAL, PAUSE_INTERVAL, PAUSE_DURATION,
   * SEQ_MEAN, SEQ_STDDEV, SEQ_AVGIDLE, PI, BUN_MEAN, BUN_STDDEV, BUN_AVGIDLE
   * 
   * @param scenario
   * @param completeSchedule
   * @param pi
   */
  private void logShortResults(Scenario scenario, double seqMean, double seqStdDev,
      double seqAvgIdle, double pi, double bunMean, double bunStdDev, double bunAvgIdle) {

    File file = new File(SIM_SHORT_LOG_PATH + "t" + scenario.getTruckCount());

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
      sb.append(scenario.getTruckCount()).append(",").append(scenario.getOrderAheadMinimum())
          .append(",").append(scenario.getRoundtripTime().toMinutes()).append(",")
          .append(scenario.getOptimalDeliveryInterval().toMinutes()).append(",")
          .append(Constraints.getTruckPauseAfter().toMinutes()).append(",")
          .append(Constraints.getTruckPauseDuration().toMinutes()).append(",").append(seqMean)
          .append(",").append(seqStdDev).append(",").append(seqAvgIdle).append(",").append(pi)
          .append(",").append(bunMean).append(",").append(bunStdDev).append(",").append(bunAvgIdle);

      if (seqStdDev == 0 && seqAvgIdle == 0) {
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
    gen.enumerateScenarios();
  }

  /**
   * @param tRUCK_MIN the tRUCK_MIN to set
   */
  public void setTRUCK_MIN(int tRUCK_MIN) {
    TRUCK_MIN = tRUCK_MIN;
  }

  /**
   * @param tRUCK_MAX the tRUCK_MAX to set
   */
  public void setTRUCK_MAX(int tRUCK_MAX) {
    TRUCK_MAX = tRUCK_MAX;
  }

  /**
   * @param tRUCK_DELTA the tRUCK_DELTA to set
   */
  public void setTRUCK_DELTA(int tRUCK_DELTA) {
    TRUCK_DELTA = tRUCK_DELTA;
  }

  /**
   * @param dELIVERIES_MIN the dELIVERIES_MIN to set
   */
  public void setDELIVERIES_MIN(int dELIVERIES_MIN) {
    DELIVERIES_MIN = dELIVERIES_MIN;
  }

  /**
   * @param dELIVERIES_MAX the dELIVERIES_MAX to set
   */
  public void setDELIVERIES_MAX(int dELIVERIES_MAX) {
    DELIVERIES_MAX = dELIVERIES_MAX;
  }

  /**
   * @param dELIVERIES_DELTA the dELIVERIES_DELTA to set
   */
  public void setDELIVERIES_DELTA(int dELIVERIES_DELTA) {
    DELIVERIES_DELTA = dELIVERIES_DELTA;
  }
}
