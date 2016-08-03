package de.wnill.master.scenario;

import java.time.Duration;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wnill.master.core.scheduling.NeighborhoodSearch;
import de.wnill.master.core.valuation.NonMonotonicLatenessValuation;
import de.wnill.master.core.valuation.Valuator;
import de.wnill.master.core.wdp.SimpleTreeSearch;
import de.wnill.master.simulator.Config;
import de.wnill.master.simulator.Simulator;
import de.wnill.master.simulator.types.Job;
import de.wnill.master.simulator.types.OrderType;
import de.wnill.master.simulator.types.Scenario;

public class RandomizedScenarioGenerator {

  private static final Logger logger = LoggerFactory.getLogger(RandomizedScenarioGenerator.class);

  private final int MAX_ROUNDTRIP_TIME_IN_MIN = 180;

  private final int MAX_TRUCK_COUNT = 10;

  private final int SIM_RUNS = 20;

  private final int MIN_ORDER_AHEAD = 5;

  public void findOptimalDemoScenario() {
    Config.setEnableVisualisation(false);
    Simulator simulator = new Simulator();
    Valuator val = new NonMonotonicLatenessValuation();
    long highestDeviation = 0;
    Scenario bestScenario = null;

    for (int i = 0; i < SIM_RUNS; i++) {
      Scenario randomScenario = generateRandomScenario();
      long deviation = executeComparingRun(simulator, val, randomScenario);

      if (deviation > highestDeviation) {
        highestDeviation = deviation;
        bestScenario = randomScenario;
      }
    }

    logger.info("Highest deviation between sequential and bundle simulation: " + highestDeviation
        + " based on scenario: " + bestScenario);
  }

  private long executeComparingRun(Simulator simulator, Valuator val, Scenario randomScenario) {
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


    long seqValuation = 0;
    for (List<Job> schedule : completeSchedule) {
      seqValuation += val.getValuation(schedule);
    }
    logger.info("Completed sequential run. Valuation: " + seqValuation);

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

    long bundleValuation = 0;
    for (List<Job> schedule : completeSchedule) {
      bundleValuation += val.getValuation(schedule);
    }
    logger.info("Completed bundle run. Valuation: " + bundleValuation);

    long deviation = seqValuation - bundleValuation;
    logger.info("Deviation: " + deviation);
    return deviation;
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
    breaks.add(LocalTime.of(ThreadLocalRandom.current().nextInt(11, 14), ThreadLocalRandom
        .current().nextInt(0, 59)));

    scenario.setTruckBreaksDue(breaks);

    List<Duration> durations = new LinkedList<>();
    durations.add(Duration.ofMinutes(ThreadLocalRandom.current().nextInt(30, 45)));
    scenario.setTruckBreakDurations(durations);

    long interval =
        ThreadLocalRandom.current().nextInt((int) scenario.getOffloadingDuration().toMinutes(),
            (int) (5 * scenario.getOffloadingDuration().toMinutes() + 1));
    scenario.setOptimalDeliveryInterval(Duration.ofMinutes(interval));

    scenario.setOrderAheadMinimum(ThreadLocalRandom.current().nextInt(1, MIN_ORDER_AHEAD));
    scenario.setOrderAheadMaximum(scenario.getOrderAheadMinimum()
        + ThreadLocalRandom.current().nextInt(1, 11));
    scenario.setRoundtripTime(Duration.ofMinutes(ThreadLocalRandom.current().nextInt(1,
        MAX_ROUNDTRIP_TIME_IN_MIN + 1)));
    scenario.setTruckCount(ThreadLocalRandom.current().nextInt(1, MAX_TRUCK_COUNT));

    // TODO do not hardcode algorithms
    scenario.setSchedulingAlgorithm(new NeighborhoodSearch());
    scenario.setValuator(new NonMonotonicLatenessValuation());
    scenario.setWinnerDeterminationAlgorithm(new SimpleTreeSearch());

    return scenario;
  }

  public static void main(String[] args) {
    RandomizedScenarioGenerator gen = new RandomizedScenarioGenerator();
    gen.findOptimalDemoScenario();
  }
}
