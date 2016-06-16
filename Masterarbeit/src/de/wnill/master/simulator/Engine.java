package de.wnill.master.simulator;

import java.util.PriorityQueue;

import de.wnill.master.simulator.types.Condition;
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


  private PriorityQueue<Event> events;

  public Engine(Condition endingCondition, Scenario scenario) {
    clock = new Clock(scenario.getStartTime());
    this.scenario = scenario;
    events = new PriorityQueue<>(new EventComparator());
  }

  @Override
  public void run() {

    // Initialize state
    // Schedule initial event

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

  public void addEvent(Event event) {
    events.add(event);
  }
}
