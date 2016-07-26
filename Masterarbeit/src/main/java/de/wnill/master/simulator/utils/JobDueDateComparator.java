package de.wnill.master.simulator.utils;

import java.util.Comparator;

import de.wnill.master.simulator.types.Job;

public class JobDueDateComparator implements Comparator<Job> {

  @Override
  public int compare(Job j1, Job j2) {
    if (j1.getDue().isBefore(j2.getDue()))
      return -1;
    else if (j1.getDue().isAfter(j2.getDue()))
      return 1;
    else
      return 0;
  }

}
