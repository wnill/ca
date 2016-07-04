package de.wnill.master.simulator;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import de.wnill.master.core.Bid;
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
  private LinkedList<Delivery> pendingDeliveries = new LinkedList<>();

  private LinkedList<Delivery> completedDeliveries = new LinkedList<>();

  private Scenario scenario;

  private int deliveryCounter = 1;

  public Paver(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Triggers the next batch of delivery order(s).
   */
  public void placeOrder(List<Truck> trucks) {
    LinkedList<Delivery> requests = new LinkedList<>();
    LocalTime nextDeliveryTime = scenario.getFirstDockingTime();

    if (!pendingDeliveries.isEmpty()) {
      nextDeliveryTime =
          pendingDeliveries.getLast().getRequestedTime()
              .plus(scenario.getOptimalDeliveryInterval());
    }

    while (pendingDeliveries.size() < scenario.getOrderAheadMinimum()) {
      Delivery delivery = new Delivery(deliveryCounter++, nextDeliveryTime);
      requests.add(delivery);
      pendingDeliveries.add(delivery);
      nextDeliveryTime = nextDeliveryTime.plus(scenario.getOptimalDeliveryInterval());
    }

    System.out.println("Requested deliveries: " + pendingDeliveries);

    for (Truck truck : trucks) {

      System.out.println("Requesting schedule for truck " + truck.getId());
      List<Bid> bids =
          truck.makeBids(requests, Clock.getInstance().getCurrentTime(), scenario.getEndTime());
      System.out.println(bids);
    }

  }
}
