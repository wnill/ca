package de.wnill.master.core.scheduling;

import java.time.LocalTime;
import java.util.List;

import de.wnill.master.core.valuation.Valuator;
import de.wnill.master.simulator.types.Job;

@FunctionalInterface
public interface SchedulingAlgorithm {

  public List<Job> scheduleJobs(List<Job> jobs, LocalTime earliestStart, LocalTime latestComplete,
      Valuator valuator);

}
