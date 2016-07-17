package de.wnill.master.simulator.utils;

import java.util.Comparator;

import de.wnill.master.simulator.types.Event;

/**
 * Sorts events by event time (ascending)
 * 
 */
public class EventComparator implements Comparator<Event> {

  @Override
  public int compare(Event e1, Event e2) {
    if (e1.getTime().isBefore(e2.getTime()))
      return -1;
    else if (e1.getTime().isAfter(e2.getTime()))
      return 1;
    else
      return 0;
  }

}
