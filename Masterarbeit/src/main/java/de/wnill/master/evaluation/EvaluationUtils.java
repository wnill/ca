package de.wnill.master.evaluation;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.wnill.master.simulator.types.Job;
import de.wnill.master.simulator.utils.JobStartTimeComparator;

public class EvaluationUtils {

  public static double calculateMeanDelivery(LinkedList<Job> deliveries) {

    if (deliveries.isEmpty() || deliveries.size() < 2) {
      return 0;
    }

    double meanInterval =
        Duration.between(deliveries.getFirst().getScheduledEnd(),
            deliveries.getLast().getScheduledEnd()).toMinutes();
    return meanInterval / (deliveries.size() - 1);
  }

  public static double calculateVariance(double meanInterval, List<Job> deliveries) {
    double variance = 0;

    for (int i = 1; i < deliveries.size(); i++) {
      long interval =
          Duration.between(deliveries.get(i - 1).getScheduledEnd(),
              deliveries.get(i).getScheduledEnd()).toMinutes();
      variance += (interval - meanInterval) * (interval - meanInterval);
    }
    return variance;
  }

  public static double calculateStdDev(double meanInterval, List<Job> deliveries) {

    return Math.sqrt(calculateVariance(meanInterval, deliveries));
  }



  public static LinkedList<Job> unionSchedules(List<List<Job>> completeSchedule) {
    // filter only deliveries
    LinkedList<Job> deliveries = new LinkedList<>();
    for (List<Job> jobList : completeSchedule) {
      for (Job job : jobList) {
        if (job.getDelivery() != null) {
          deliveries.add(job);
        }
      }
    }
    Collections.sort(deliveries, new JobStartTimeComparator());

    return deliveries;
  }

  /**
   * Sums the idle times between the jobs of each truck. Does not consider idle times before or
   * after a block of jobs!
   * 
   * @param jobs
   * @return
   */
  public static long calculateIdleTimes(List<List<Job>> jobs) {
    long idle = 0;
    for (List<Job> truckSchedule : jobs) {
      if (truckSchedule.size() > 1) {
        for (int i = 1; i < truckSchedule.size(); i++) {
          idle +=
              Duration.between(truckSchedule.get(i - 1).getScheduledEnd(),
                  truckSchedule.get(i).getScheduledStart()).toMinutes();
        }
      }
    }
    return idle;
  }

  public static double calcAvgIdleTimes(List<List<Job>> jobs) {
    int truckCount = jobs.size();
    long totalIdleTimes = calculateIdleTimes(jobs);

    return ((double) totalIdleTimes) / truckCount;
  }
}
