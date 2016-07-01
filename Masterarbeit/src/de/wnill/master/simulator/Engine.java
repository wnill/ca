package de.wnill.master.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import de.wnill.master.simulator.events.StartOrders;
import de.wnill.master.simulator.types.Condition;
import de.wnill.master.simulator.types.Context;
import de.wnill.master.simulator.types.Event;
import de.wnill.master.simulator.types.Scenario;
import de.wnill.master.simulator.utils.EventComparator;

/**
 * Executes a simulation by processing events until there are none left or some other ending
 * condition is met. The simulation environment is specified by the scenario.
 *
 */
public class Engine implements Runnable {

  private Scenario scenario;

  private Condition endingCondition;

  private Clock clock;

  private Paver paver;

  private List<Truck> trucks;


  private PriorityQueue<Event> events;

  public Engine(Condition endingCondition, Scenario scenario) {
    clock = new Clock(scenario.getStartTime());
    this.scenario = scenario;

    events = new PriorityQueue<>(new EventComparator());
  }

  @Override
  public void run() {

    initialize();

    // Run simulation
    while (!endingCondition.isMet() && clock.getCurrentTime().isBefore(scenario.getEndTime())
        && !events.isEmpty()) {
      Event nextEvent = events.poll();
      clock.setCurrentTime(nextEvent.getTime());
      nextEvent.execute();
      // Update statistics
    }

    // Generate report

  }

  /**
   * Set up initial state and first event.
   */
  private void initialize() {
    // Initialize state
    clock.setCurrentTime(scenario.getStartTime());
    paver = new Paver(scenario);
    trucks = new ArrayList<>();
    for (int i = 0; i < scenario.getTruckCount(); i++) {
      trucks.add(new Truck(i, scenario.getSchedulingAlgorithm()));
    }

    // Schedule initial event
    addEvent(new StartOrders(new Context.ContextBuilder().paver(paver).build(),
        scenario.getStartTime()));
  }

  public void addEvent(Event event) {
    events.add(event);
  }
}
