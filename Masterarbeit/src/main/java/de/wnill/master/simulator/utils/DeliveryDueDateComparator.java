package de.wnill.master.simulator.utils;

import java.util.Comparator;

import de.wnill.master.simulator.types.Delivery;

/**
 * Sorts events by event time (ascending)
 * 
 */
public class DeliveryDueDateComparator implements Comparator<Delivery> {

  @Override
  public int compare(Delivery d1, Delivery d2) {
    if (d1.getRequestedTime().isBefore(d2.getRequestedTime()))
      return -1;
    else if (d1.getRequestedTime().isAfter(d2.getRequestedTime()))
      return 1;
    else
      return 0;
  }
}
