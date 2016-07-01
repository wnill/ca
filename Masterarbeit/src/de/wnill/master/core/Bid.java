package de.wnill.master.core;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;

import de.wnill.master.simulator.types.Delivery;

/**
 * Models a "bid" given by a truck for a set of deliveries by specifying estimated delivery dates.
 */
public class Bid {

  private int id;

  private Duration maxLateness = Duration.ZERO;

  private Duration sumLateness = Duration.ZERO;

  private static int idCounter = 0;

  private LinkedList<Delivery> deliveries = new LinkedList<>();


  public Bid(Collection<Delivery> deliveries) {
    for (Delivery delivery : deliveries) {
      this.deliveries.add(delivery.clone());
      Duration deviation =
          Duration.between(delivery.getRequestedTime(), delivery.getProposedTime()).abs();
      sumLateness = sumLateness.plus(deviation);
      if (deviation.compareTo(maxLateness) > 0) {
        maxLateness = deviation;
      }
    }

    id = idCounter;
    idCounter++;
  }


  /**
   * @return the id
   */
  public int getId() {
    return id;
  }


  /**
   * @return the maxLateness
   */
  public Duration getMaxLateness() {
    return maxLateness;
  }


  /**
   * @return the sumLateness
   */
  public Duration getSumLateness() {
    return sumLateness;
  }


  /**
   * @return the deliveries
   */
  public LinkedList<Delivery> getDeliveries() {
    return deliveries;
  }


  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Bid [id=" + id + ", maxLateness=" + maxLateness + ", sumLateness=" + sumLateness
        + ", deliveries=" + deliveries + "]";
  }



}
