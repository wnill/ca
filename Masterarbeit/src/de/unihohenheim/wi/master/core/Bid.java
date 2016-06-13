package de.unihohenheim.wi.master.core;

import java.util.Map;

/**
 * Models a "bid" given by a truck for a set of deliveries by specifying estimated delivery dates.
 */
public class Bid {

  private int id;

  private long maxLateness = 0;

  private static int idCounter = 0;

  /** Delivery -> offered time */
  private Map<Delivery, Long> bidSet;


  public Bid(Map<Delivery, Long> bidmap) {
    bidSet = bidmap;
    id = idCounter;
    idCounter++;

    if (bidSet != null && !bidSet.isEmpty()) {
      maxLateness = 0;
      for (Long value : bidSet.values()) {
        if (Math.abs(value) > maxLateness) {
          maxLateness = Math.abs(value);
        }
      }
    }
  }

  /**
   * @return the bidSet
   */
  public Map<Delivery, Long> getBidSet() {
    return bidSet;
  }


  /**
   * @return the maxLateness
   */
  public long getMaxLateness() {
    return maxLateness;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Bid [id=" + id + ", maxLateness=" + maxLateness + ", bidSet=" + bidSet + "]";
  }


}
