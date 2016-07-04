package de.wnill.master.simulator.utils;

import java.time.Duration;
import java.util.List;

import de.wnill.master.simulator.types.Job;

public class ScheduleLogger {

  public static void logSchedule(List<Job> schedule, long valuation) {

    StringBuilder str =
        new StringBuilder().append(schedule.size()).append("\t | ").append(valuation)
            .append("\t | [");
    for (Job job : schedule) {
      if (job.getDelivery() != null) {
        str.append("D").append(job.getDelivery().getId());
      } else {
        str.append("B").append(job.getId());
      }
      str.append(": ").append(job.getScheduledStart()).append("-").append(job.getScheduledEnd())
          .append(" (").append(Duration.between(job.getDue(), job.getScheduledEnd()).toMinutes())
          .append("m)], ");
    }
    System.out.println(str.toString());
  }

}
