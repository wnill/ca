package de.wnill.master.scenario;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wnill.master.core.scheduling.NeighborhoodSearch;
import de.wnill.master.core.scheduling.second.NoBreaksScheduleShifter;
import de.wnill.master.core.valuation.NonMonotonicLatenessValuation;
import de.wnill.master.core.valuation.Valuator;
import de.wnill.master.core.wdp.SimpleTreeSearch;
import de.wnill.master.simulator.Config;
import de.wnill.master.simulator.Simulator;
import de.wnill.master.simulator.types.Job;
import de.wnill.master.simulator.types.OrderType;
import de.wnill.master.simulator.types.Scenario;
import de.wnill.master.simulator.utils.JobStartTimeComparator;

public class RandomScenarioGeneratorMinVariance {

  private static final Logger logger = LoggerFactory
      .getLogger(RandomScenarioGeneratorMinVariance.class);

  private final int MAX_ROUNDTRIP_TIME_IN_MIN = 180;

  private final int MAX_TRUCK_COUNT = 5;

  private final int SIM_RUNS = 100;

  private final int MIN_ORDER_AHEAD = 8;

  public void findOptimalDemoScenario() {
    Config.setEnableVisualisation(false);
    Simulator simulator = new Simulator();
    Valuator val = new NonMonotonicLatenessValuation();
    double highestDeviation = 0;
    Scenario bestScenario = null;

    int improvement = 0;
    int worse = 0;

    for (int i = 0; i < SIM_RUNS; i++) {
      Scenario randomScenario = generateRandomScenario();
      double deviation = executeComparingRun(simulator, val, randomScenario);

      if (deviation > 0) {
        improvement++;
      } else if (deviation < 0) {
        worse++;
      }

      if (deviation > highestDeviation) {
        highestDeviation = deviation;
        bestScenario = randomScenario;
      }
    }

    DecimalFormat df = new DecimalFormat("#.00");
    logger.info("Highest deviation between sequential and bundle simulation: " + highestDeviation
        + " (StdDev: " + df.format(Math.sqrt(highestDeviation)) + ") based on scenario: "
        + bestScenario);
    logger.info("Schedules improved in "
        + df.format((double) improvement / (double) SIM_RUNS * 100) + "%, worse in "
        + df.format((double) worse / (double) SIM_RUNS * 100) + "% of simulation runs");
  }

  private double executeComparingRun(Simulator simulator, Valuator val, Scenario randomScenario) {
    randomScenario.setOrderType(OrderType.SEQUENTIAL);

    logger.info("Starting a sequential simulation run with scenario: " + randomScenario);
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

    List<List<Job>> completeSchedule = simulator.getResultMap().get(randomScenario.hashCode());
    double seqVariance = calculateVariance(completeSchedule);
    logger.info("Completed sequential run. Variance: " + seqVariance);

    logger.info("Starting a bundle simulation run with scenario: " + randomScenario);
    Scenario randomBundleScenario = randomScenario;
    randomBundleScenario.setOrderType(OrderType.BUNDLE);
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

    completeSchedule = simulator.getResultMap().get(randomBundleScenario.hashCode());
    double bundleVariance = calculateVariance(completeSchedule);
    logger.info("Completed bundle run. Variance: " + bundleVariance);

    double deviation = seqVariance - bundleVariance;
    logger.info("Deviation: " + deviation);
    if (deviation < 0) {
      logger.info("Bad scenario! " + randomBundleScenario);
    }

    return deviation;
  }

  double calculateVariance(List<List<Job>> completeSchedule) {
    // filter only deliveries
    LinkedList<Job> deliveries = new LinkedList<>();
    for (List<Job> jobList : completeSchedule) {
      for (Job job : jobList) {
        if (job.getDelivery() != null) {
          deliveries.add(job);
        }
      }
    }
    Collections.sort(deliveries, new JobStartTimeComparator());


    double meanInterval =
        Duration.between(deliveries.getFirst().getScheduledEnd(),
            deliveries.getLast().getScheduledEnd()).toMinutes();
    meanInterval = meanInterval / (deliveries.size() - 1);


    double variance = 0;

    for (int i = 1; i < deliveries.size(); i++) {
      long interval =
          Duration.between(deliveries.get(i - 1).getScheduledEnd(),
              deliveries.get(i).getScheduledEnd()).toMinutes();
      variance += (interval - meanInterval) * (interval - meanInterval);
    }
    return variance;
  }

  /**
   * Generates a scenario with random interval, order count, roundtrip time and truck count.
   * 
   * @return
   */
  public Scenario generateRandomScenario() {
    Scenario scenario = new Scenario();
    scenario.setStartTime(LocalTime.of(0, 0));
    scenario.setEndTime(LocalTime.of(23, 59));
    scenario.setFirstDockingTime(LocalTime.of(8, 0));
    scenario.setOffloadingDuration(Duration.ofMinutes(10));


    List<LocalTime> breaks = new LinkedList<>();
    // breaks.add(LocalTime.of(ThreadLocalRandom.current().nextInt(11, 14), ThreadLocalRandom
    // .current().nextInt(0, 59)));

    scenario.setTruckBreaksDue(breaks);

    List<Duration> durations = new LinkedList<>();
    // durations.add(Duration.ofMinutes(ThreadLocalRandom.current().nextInt(30, 45)));
    scenario.setTruckBreakDurations(durations);
    scenario.setTruckCount(ThreadLocalRandom.current().nextInt(2, MAX_TRUCK_COUNT));

    long interval =
        ThreadLocalRandom.current().nextInt((int) scenario.getOffloadingDuration().toMinutes(),
            (int) (5 * scenario.getOffloadingDuration().toMinutes() + 1));
    scenario.setOptimalDeliveryInterval(Duration.ofMinutes(interval));

    scenario.setOrderAheadMinimum(ThreadLocalRandom.current().nextInt(scenario.getTruckCount(),
        MIN_ORDER_AHEAD));
    scenario.setOrderAheadMaximum(scenario.getOrderAheadMinimum()
        + ThreadLocalRandom.current().nextInt(1, 11));
    scenario.setRoundtripTime(Duration.ofMinutes(ThreadLocalRandom.current().nextInt(1,
        MAX_ROUNDTRIP_TIME_IN_MIN + 1)));


    // TODO do not hardcode algorithms
    scenario.setSecondPassProcessor(new NoBreaksScheduleShifter());
    scenario.setSchedulingAlgorithm(new NeighborhoodSearch());
    scenario.setValuator(new NonMonotonicLatenessValuation());
    scenario.setWinnerDeterminationAlgorithm(new SimpleTreeSearch());

    return scenario;
  }

  public static void main(String[] args) {
    RandomScenarioGeneratorMinVariance gen = new RandomScenarioGeneratorMinVariance();
    gen.findOptimalDemoScenario();
  }
}
