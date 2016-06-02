package de.unihohenheim.wi.master.core;

import java.util.Map;


public class Bid {

  /** Delivery -> offered time */
  private Map<Delivery, Long> bidSet;


  public Bid(Map<Delivery, Long> bidmap) {
    bidSet = bidmap;
  }

  /**
   * @return the bidSet
   */
  public Map<Delivery, Long> getBidSet() {
    return bidSet;
  }

  /**
   * @param bidSet the bidSet to set
   */
  public void setBidSet(Map<Delivery, Long> bidSet) {
    this.bidSet = bidSet;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Bid [bidSet=" + bidSet + "]";
  }


}
