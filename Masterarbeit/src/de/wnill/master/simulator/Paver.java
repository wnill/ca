package de.wnill.master.simulator;

import java.util.PriorityQueue;

import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Scenario;

public class Paver {

  /** meter per minute */
  private double speed;

  /** meter per minute */
  private double maxSpeed;

  /** 1 truck is assumed to be unloading, additional trucks are queued up waiting. */
  private int trucksInFrontOfPaver;

  /** contains all deliveries that have been completed yet, sorted by docking time. */
  private PriorityQueue<Delivery> pendingDeliveries;

  private Scenario scenario;

  public Paver(Scenario scenario) {
    this.scenario = scenario;
  }

  public void placeOrder() {
    // TODO Auto-generated method stub

  }

}
