package de.wnill.master.core.valuation;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingFormatArgumentException;

import de.wnill.master.core.utils.ConversionHandler;
import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;
import de.wnill.master.simulator.utils.JobStartTimeComparator;

public class TruckIdleTimes implements Valuator {

  @Override
  /**
   * ATTENTION: Works only for one truck - do not mix jobs of different trucks!
   */
  public long getValuation(Collection<Job> jobs, Collection<Delivery> deliveries) {

    List<Job> allJobs = new LinkedList<>();
    allJobs.addAll(jobs);

    for (Delivery delivery : deliveries) {
      if (delivery.getStartTime() == null) {
        throw new MissingFormatArgumentException("Delivery Start Time must be set!");
      }
      allJobs.add(ConversionHandler.convertDeliveryToJob(delivery,
          Duration.between(delivery.getStartTime(), delivery.getProposedTime())));
    }

    return getValuation(allJobs);
  }


  /**
   * ATTENTION: Works only for one truck - do not mix jobs of different trucks!
   */
  public long getValuation(Collection<Job> jobs, LocalTime start) {

    if (jobs.isEmpty())
      return 0;

    LinkedList<Job> allJobs = new LinkedList<>(jobs);
    Collections.sort(allJobs, new JobStartTimeComparator());
    Duration idle = Duration.between(start, allJobs.getFirst().getScheduledStart());

    for (int i = 1; i < allJobs.size(); i++) {
      idle =
          idle.plus(Duration.between(allJobs.get(i - 1).getScheduledEnd(), allJobs.get(i)
              .getScheduledStart()));
    }

    return idle.toMinutes();
  }

  @Override
  /**
   * ATTENTION: Works only for one truck - do not mix jobs of different trucks!
   */
  public long getValuation(Collection<Job> jobs) {

    LinkedList<Job> allJobs = new LinkedList<>(jobs);
    Collections.sort(allJobs, new JobStartTimeComparator());
    LocalTime start = allJobs.getFirst().getScheduledStart();

    return getValuation(jobs, start);
  }

  /**
   * Calculates idle times for a complete schedule from multiple trucks.
   * 
   * @param completeSchedule
   * @return
   */
  public long getValuation(List<List<Job>> completeSchedule) {

    // find start time
    // LocalTime earliest = LocalTime.MAX;
    // for (List<Job> jobList : completeSchedule) {
    // if (jobList.size() > 0 && jobList.get(0).getScheduledStart().isBefore(earliest)) {
    // earliest = jobList.get(0).getScheduledStart();
    // }
    // }

    long totalIdleTimes = 0;
    for (List<Job> jobList : completeSchedule) {
      if (!jobList.isEmpty()) {
        totalIdleTimes += getValuation(jobList, jobList.get(0).getScheduledStart());
      }
    }

    return totalIdleTimes;
  }

}
