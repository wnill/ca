package de.wnill.master.simulator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wnill.master.core.scheduling.viz.ScheduleVisualizer;
import de.wnill.master.simulator.types.Bid;
import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.OrderType;
import de.wnill.master.simulator.types.Scenario;

public class Paver {

  private static final Logger logger = LoggerFactory.getLogger(Paver.class);


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

    logger.debug("Requested deliveries: " + pendingDeliveries);

    if (scenario.getOrderType().equals(OrderType.BUNDLE)) {
      orderDeliveries(trucks, requests);
    } else if (scenario.getOrderType().equals(OrderType.SEQUENTIAL)) {
      for (Delivery request : requests) {
        LinkedList<Delivery> oneElementList = new LinkedList<>();
        oneElementList.add(request);
        orderDeliveries(trucks, oneElementList);
      }
    }

    // TODO put this in the right place
    if (Config.isEnableVisualisation()) {
      new ScheduleVisualizer(trucks);
    }
  }

  /**
   * Collects the best bids for a given set of deliveries and awards the winning trucks.
   * 
   * @param trucks
   * @param requests
   */
  private void orderDeliveries(List<Truck> trucks, LinkedList<Delivery> requests) {

    // Collect bids
    List<Bid> bids = new ArrayList<>();
    for (Truck truck : trucks) {
      logger.debug("Requesting schedule for truck " + truck.getId());
      LocalTime earliest =
          Clock.getInstance().getCurrentTime()
              .isAfter(scenario.getFirstDockingTime().minus(scenario.getRoundtripTime())) ? Clock
              .getInstance().getCurrentTime() : scenario.getFirstDockingTime().minus(
              scenario.getRoundtripTime());
      bids.addAll(truck.makeBids(requests, earliest, scenario.getEndTime()));
      logger.debug(bids.toString());
    }

    Set<Bid> winningBids = findWinningBids(bids, requests);

    if (scenario.getSecondPassProcessor() != null) {
      winningBids = scenario.getSecondPassProcessor().updateBids(winningBids);
    }

    awardWinningBids(winningBids, trucks);
  }

  /**
   * Decides on which bids to award deliveries from the given set of bids.
   * 
   * @param bids
   * @return
   */
  private Set<Bid> findWinningBids(List<Bid> bids, List<Delivery> requests) {
    HashSet<Bid> bestBids = new HashSet<>();
    // Find best bid (only for sequential ordering)
    if (scenario.getOrderType().equals(OrderType.SEQUENTIAL)) {
      Bid bestBid = null;
      for (Bid bid : bids) {
        if (bid != null && (bestBid == null || bid.getValuation() < bestBid.getValuation())
            && bid.getDeliveryIds().contains(requests.get(0).getId())) {
          bestBid = bid;
        }
      }
      bestBids.add(bestBid);
    } else if (scenario.getOrderType().equals(OrderType.BUNDLE)) {
      bestBids.addAll(scenario.getWinnerDeterminationAlgorithm().determineWinners(bids, requests));
    }

    return bestBids;
  }

  /**
   * For each of the winning bids orders the owning truck to carry out the delivery, respectively.
   * All other trucks get negative feedback.
   * 
   * @param winningBids
   */
  private void awardWinningBids(Set<Bid> winningBids, List<Truck> allTrucks) {
    Set<Truck> winningTrucks = new HashSet<>();
    if (winningBids != null) {
      for (Bid bid : winningBids) {
        if (bid != null) {
          bid.getTruck().awardBid(bid);
          winningTrucks.add(bid.getTruck());
          logger.info("Awarded truck " + bid.getTruck().getId() + " with bid " + bid);
        }
      }
    }
    for (Truck truck : allTrucks) {
      if (!winningTrucks.contains(truck)) {
        truck.rejectAllBids();
      }
    }
  }
}
