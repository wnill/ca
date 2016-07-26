package de.wnill.master.simulator.utils;

import java.util.Comparator;

import de.wnill.master.simulator.types.Job;

public class JobStartTimeComparator implements Comparator<Job> {

  @Override
  public int compare(Job j1, Job j2) {
    if (j1.getScheduledStart().isBefore(j2.getScheduledStart()))
      return -1;
    else if (j1.getScheduledStart().isAfter(j2.getScheduledStart()))
      return 1;
    else
      return 0;
  }

}
