package de.wnill.master.simulator.utils;

import java.util.Comparator;

import de.wnill.master.simulator.types.Delivery;

/**
 * Sorts events by event time (ascending)
 * 
 */
public class DeliveryProposedTimeComparator implements Comparator<Delivery> {

  @Override
  public int compare(Delivery d1, Delivery d2) {
    if (d1.getProposedTime().isBefore(d2.getProposedTime()))
      return -1;
    else if (d1.getProposedTime().isAfter(d2.getProposedTime()))
      return 1;
    else
      return 0;
  }

}
