package de.wnill.master.simulator;

import java.time.Duration;
import java.time.LocalTime;

import de.wnill.master.core.scheduling.NaiveSymetricPenalties;
import de.wnill.master.core.valuation.NonMonotonicLatenessValuation;
import de.wnill.master.core.wdp.SimpleTreeSearch;
import de.wnill.master.simulator.types.Condition;
import de.wnill.master.simulator.types.OrderType;
import de.wnill.master.simulator.types.Scenario;

public class Simulator {

  public void runScenario(Scenario scenario) {

    Engine engine = new Engine(new Condition() {

      @Override
      public boolean isMet() {
        // TODO Auto-generated method stub
        return false;
      }
    }, scenario);
    engine.run();
  }


  public static void main(String[] args) {

    // Configure BUNDLE scenario
    Scenario scenario = new Scenario();
    scenario.setEndTime(LocalTime.of(14, 0));
    scenario.setFirstDockingTime(LocalTime.of(12, 0));
    scenario.setOffloadingDuration(Duration.ofMinutes(5));
    scenario.setOptimalDeliveryInterval(Duration.ofMinutes(10));
    scenario.setOrderAheadMaximum(6);
    scenario.setOrderAheadMinimum(6);
    scenario.setOrderType(OrderType.BUNDLE);
    scenario.setRoundtripTime(Duration.ofMinutes(20));
    scenario.setSchedulingAlgorithm(new NaiveSymetricPenalties());
    scenario.setValuator(new NonMonotonicLatenessValuation());
    scenario.setWinnerDeterminationAlgorithm(new SimpleTreeSearch());
    scenario.setStartTime(LocalTime.of(11, 40));
    scenario.setTruckCount(2);


    // Configure SEQUENTIAL scenario
    // Scenario scenario = new Scenario();
    // scenario.setEndTime(LocalTime.of(14, 0));
    // scenario.setFirstDockingTime(LocalTime.of(12, 0));
    // scenario.setOffloadingDuration(Duration.ofMinutes(5));
    // scenario.setOptimalDeliveryInterval(Duration.ofMinutes(10));
    // scenario.setOrderAheadMaximum(6);
    // scenario.setOrderAheadMinimum(6);
    // scenario.setOrderType(OrderType.SEQUENTIAL);
    // scenario.setRoundtripTime(Duration.ofMinutes(20));
    // scenario.setSchedulingAlgorithm(new NaiveSymetricPenalties());
    // scenario.setStartTime(LocalTime.of(11, 40));
    // scenario.setTruckCount(2);


    Engine engine = new Engine(new Condition() {

      @Override
      public boolean isMet() {
        // TODO Auto-generated method stub
        return false;
      }
    }, scenario);
    engine.run();

  }

}
