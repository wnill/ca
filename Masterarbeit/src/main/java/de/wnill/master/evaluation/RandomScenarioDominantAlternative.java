package de.wnill.master.evaluation;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

public class RandomScenarioDominantAlternative {

  private static final Logger logger = LoggerFactory
      .getLogger(RandomScenarioDominantAlternative.class);


  private final int MAX_TRUCK_COUNT = 4;

  private final int SIM_RUNS = 10;

  private final int MIN_ORDER_AHEAD = 8;

  // private ResultsCollector collector = new ResultsCollector();


  public void findOptimalDemoScenario() {
    Config.setEnableVisualisation(false);
    Simulator simulator = new Simulator();
    Valuator val = new NonMonotonicLatenessValuation();

    int weaklyDominated = 0;
    int strictlyDominated = 0;
    int validRuns = 0;

    for (int i = 0; i < SIM_RUNS; i++) {
      Scenario randomScenario = generateRandomScenario();


      int result = executeComparingRun(simulator, val, randomScenario);

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
    double seqVariance = EvaluationUtils.calculateVariance(seqMeanDelivery, deliveries);
    long seqIdleTimes = EvaluationUtils.calculateIdleTimes(completeSchedule);

    logger.info("SEQ Var: " + seqVariance + ", Idle: " + seqIdleTimes);
    // collector.putResult(OrderType.SEQUENTIAL, seqIdleTimes, seqVariance, seqMeanDelivery);

    if (seqVariance == 0 && seqIdleTimes == 0) {
      return -1;
    }

    Scenario randomBundleScenario = randomScenario;
    randomBundleScenario.setOrderType(OrderType.BUNDLE);

    for (int i = 0; i < 1000; i++) {

      randomBundleScenario.setSchedulingAlgorithm(new NeighborhoodSearch());
      randomBundleScenario.setBidGenerator(new FullScheduleGenerator());
      randomBundleScenario.setWinnerDeterminationAlgorithm(new EveryOneIsAWinner());
      randomBundleScenario.setSecondPassProcessor(new MinVarAndIdleShifter(i));
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
      double bundleVariance = EvaluationUtils.calculateVariance(bunMeanDelivery, deliveries);
      long bunIdleTimes = EvaluationUtils.calculateIdleTimes(completeSchedule);
      // collector.putResult(OrderType.BUNDLE, bunIdleTimes, bundleVariance, bunMeanDelivery);

      if (bunIdleTimes < 0) {
        logger.warn("invalid schedule: " + completeSchedule);
        continue;
      }
      
      if (bunIdleTimes < seqIdleTimes && bundleVariance < seqVariance) {
        logger.info("Dominant Alternative BUN Var: " + bundleVariance + ", Idle: " + bunIdleTimes);
        return 2;
      } else if (bunIdleTimes <= seqIdleTimes && bundleVariance <= seqVariance) {
        logger.info("Dominant Alternative BUN Var: " + bundleVariance + ", Idle: " + bunIdleTimes);
        return 1;
      } else if (bunIdleTimes > seqIdleTimes && bundleVariance > seqVariance) {
        return 0;
      }
    }

    return 0;
  }



  /**
   * Generates a scenario with random interval, order count, roundtrip time and truck count.
   * 
   * @return
   */
  public Scenario generateRandomScenario() {
    Scenario scenario = new Scenario();
    scenario.setStartTime(LocalTime.of(0, 0));
    scenario.setEndTime(LocalTime.of(12, 00));
    scenario.setFirstDockingTime(LocalTime.of(8, 0));
    scenario.setOffloadingDuration(Duration.ofMinutes(10));


    Constraints
        .setTruckPauseAfter(Duration.ofMinutes(ThreadLocalRandom.current().nextInt(60, 180)));
    Constraints.setTruckPauseDuration(Duration.ofMinutes(ThreadLocalRandom.current()
        .nextInt(30, 45)));

    scenario.setTruckCount(ThreadLocalRandom.current().nextInt(2, MAX_TRUCK_COUNT));

    scenario.setOrderAheadMinimum(ThreadLocalRandom.current().nextInt(scenario.getTruckCount(),
        MIN_ORDER_AHEAD));
    scenario.setOrderAheadMaximum(scenario.getOrderAheadMinimum()
        + ThreadLocalRandom.current().nextInt(1, 11));
    scenario.setRoundtripTime(Duration.ofMinutes(ThreadLocalRandom.current().nextInt(
        (int) scenario.getOffloadingDuration().toMinutes(),
        4 * (int) scenario.getOffloadingDuration().toMinutes() + 1)));

    long interval =
        ThreadLocalRandom.current().nextInt(
            (int) scenario.getRoundtripTime().toMinutes() / scenario.getTruckCount(),
            (int) (5 * scenario.getOffloadingDuration().toMinutes() + 1));
    scenario.setOptimalDeliveryInterval(Duration.ofMinutes(interval));


    return scenario;
  }

  public static void main(String[] args) {
    RandomScenarioDominantAlternative gen = new RandomScenarioDominantAlternative();
    gen.findOptimalDemoScenario();
  }
}
