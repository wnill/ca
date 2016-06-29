package de.wnill.master.core.scheduling;

import java.util.List;

import de.wnill.master.core.Job;

@FunctionalInterface
public interface SchedulingAlgorithm {

  public List<Job> scheduleJobs(List<Job> jobs, long earliestStart, long latestComplete);

}
