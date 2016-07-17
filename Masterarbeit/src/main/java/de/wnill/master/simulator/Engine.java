package de.wnill.master.simulator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import de.wnill.master.simulator.events.StartOrders;
import de.wnill.master.simulator.types.Condition;
import de.wnill.master.simulator.types.Context;
import de.wnill.master.simulator.types.Event;
import de.wnill.master.simulator.types.Job;
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

  private Paver paver;

  private List<Truck> trucks;

  private Simulator callback;


  private PriorityQueue<Event> events;

  public Engine(Condition endingCondition, Scenario scenario) {
    Clock.getInstance().setCurrentTime(scenario.getStartTime());
    this.endingCondition = endingCondition;
    this.scenario = scenario;

    events = new PriorityQueue<>(new EventComparator());
  }

  @Override
  public void run() {

    initialize();

    // Run simulation
    while (!endingCondition.isMet()
        && Clock.getInstance().getCurrentTime().isBefore(scenario.getEndTime())
        && !events.isEmpty()) {
      Event nextEvent = events.poll();
      Clock.getInstance().setCurrentTime(nextEvent.getTime());
      nextEvent.execute();
      // Update statistics
    }

    // Generate report
    List<List<Job>> completeSchedule = new LinkedList<>();
    for (Truck truck : trucks) {
      completeSchedule.add(truck.getSchedule());
    }
    callback.reportResults(scenario, completeSchedule);
  }

  /**
   * Set up initial state and first event.
   */
  private void initialize() {
    // Initialize state
    Clock.getInstance().setCurrentTime(scenario.getStartTime());
    paver = new Paver(scenario);
    trucks = new ArrayList<>();
    for (int i = 0; i < scenario.getTruckCount(); i++) {
      Truck truck = new Truck(i, scenario.getSchedulingAlgorithm(), scenario.getValuator());
      truck.setRoundtripTime(scenario.getRoundtripTime());
      trucks.add(truck);
    }

    // Schedule initial event
    addEvent(new StartOrders(new Context.ContextBuilder().paver(paver).trucks(trucks).build(),
        scenario.getStartTime()));
  }

  public void addEvent(Event event) {
    events.add(event);
  }

  public void registerResultCallback(Simulator simulator) {
    this.callback = simulator;
  }
}
