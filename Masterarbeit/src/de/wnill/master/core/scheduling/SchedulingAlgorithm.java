package de.wnill.master.core.scheduling;

import java.util.List;

import de.wnill.master.simulator.types.Job;

@FunctionalInterface
public interface SchedulingAlgorithm {

  public List<Job> scheduleJobs(List<Job> jobs, long earliestStart, long latestComplete);

}
