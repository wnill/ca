package de.wnill.master.core.valuation;

import java.time.Duration;
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

  @Override
  /**
   * ATTENTION: Works only for one truck - do not mix jobs of different trucks!
   */
  public long getValuation(Collection<Job> jobs) {

    LinkedList<Job> allJobs = new LinkedList<>(jobs);
    Collections.sort(allJobs, new JobStartTimeComparator());
    Duration idle = Duration.ZERO;

    for (int i = 1; i < allJobs.size(); i++) {
      idle =
          idle.plus(Duration.between(allJobs.get(i - 1).getScheduledEnd(), allJobs.get(i)
              .getScheduledStart()));
    }

    return idle.toMinutes();
  }

  /**
   * Calculates idle times for a complete schedule from multiple trucks;
   * 
   * @param completeSchedule
   * @return
   */
  public long getValuation(List<List<Job>> completeSchedule) {

    long totalIdleTimes = 0;
    for (List<Job> jobList : completeSchedule) {
      totalIdleTimes += getValuation(jobList);
    }

    return totalIdleTimes;
  }

}
