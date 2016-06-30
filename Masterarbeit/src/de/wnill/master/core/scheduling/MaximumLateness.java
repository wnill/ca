package de.wnill.master.core.scheduling;

import java.util.LinkedList;
import java.util.List;

import de.wnill.master.simulator.types.Job;

/**
 * Implementation of the scheduling algorithm proposed by Sidney (1977) in: Optimal Single-Machine
 * Scheduling with Earliness and Tariness Penalties.
 *
 */
public class MaximumLateness implements SchedulingAlgorithm {

  /** Specifies penalty applied to early deliveries. */
  private long earlinessWeight;

  /** Specifies penalty applied to late deliveries. */
  private long tardinessWeight;

  /**
   * Instantiation of Sidney's scheduling algorithm.
   * 
   * @param earlinessWeight
   * @param tardinessWeight
   */
  public MaximumLateness(long earlinessWeight, long tardinessWeight) {
    this.earlinessWeight = earlinessWeight;
    this.tardinessWeight = tardinessWeight;
  }

  /**
   * It is assumed that an ordered list is given as parameter, where for all jobs i < j due dates
   * are di < dj.
   * 
   * @param jobs
   * @return
   */
  public List<Job> scheduleJobs(List<Job> jobs, long earliestStart, long latestComplete) {
    List<Job> admissible = getAdmissibleSchedule(jobs);
    long delta = calculateLowerBound(admissible);

    if (delta > 0) {
      reschedule(admissible, delta);
    }
    return adjustForFeasibility(admissible, earliestStart, latestComplete);
  }

  private void reschedule(List<Job> jobs, long delta) {
    long eStar = calculateEarlinessOffset(delta);

    jobs.get(0).setScheduledStart(jobs.get(0).getScheduledStart() - eStar);
    if (jobs.size() > 1) {
      for (int j = 1; j < jobs.size(); j++) {
        long start =
            Math.max(jobs.get(j - 1).getScheduledEnd(), jobs.get(j).getTargetedStart() - eStar);
        jobs.get(j).setScheduledStart(start);
      }
    }
  }

  /**
   * An admissible schedule is one, where for all jobs i and j with targeted start dates s[i]< s[j]
   * it holds: due dates d[i] < d[j]. Such an admissible schedule is generated for given list of
   * jobs.
   * 
   * @param jobs
   * @return
   */
  private List<Job> getAdmissibleSchedule(List<Job> jobs) {
    jobs.get(0).setScheduledStart(jobs.get(0).getTargetedStart());

    if (jobs.size() > 1) {
      for (int i = 1; i < jobs.size(); i++) {
        long start = Math.max(jobs.get(i).getTargetedStart(), jobs.get(i - 1).getScheduledEnd());
        jobs.get(i).setScheduledStart(start);
      }
    }
    return jobs;
  }


  /**
   * TODO - more efficient than original approach. See paper of that indian guy.
   * 
   * @param jobs
   * @return
   */
  private long calculateMaximumLateness(List<Job> jobs) {
    long maxLateness = 0;


    return maxLateness;
  }


  /**
   * Calculates delta as proposed by Sidney. Same as <code>calculateMaximumLateness</code> but more
   * inefficient (n²).
   * 
   * @param jobs
   * @return
   */
  private long calculateLowerBound(List<Job> jobs) {
    long bound = 0;

    for (int i = 0; i < jobs.size(); i++) {
      long duration = jobs.get(i).getDuration();
      for (int j = i + 1; j < jobs.size(); j++) {
        duration += jobs.get(j).getDuration();
        long delta = duration - (jobs.get(j).getDue() - jobs.get(i).getTargetedStart());
        if (delta > bound) {
          bound = delta;
        }
      }
    }
    return bound;
  }

  /**
   * Calculates E* in Sidney's notation.
   * 
   * @param delta
   * @return
   */
  private long calculateEarlinessOffset(long delta) {
    return delta / (1 + earlinessWeight / tardinessWeight);
  }


  /**
   * Shifts schedule to comply with earliest start / latest finish constraints.
   * 
   * @param jobs
   * @return
   */
  private List<Job> adjustForFeasibility(List<Job> jobs, long earliestStart, long latestComplete) {

    if (jobs.get(0).getScheduledStart() < earliestStart
        && jobs.get(jobs.size() - 1).getScheduledEnd() > latestComplete) {
      // schedule infeasible within given bounds
      return new LinkedList<Job>();
    }

    if (jobs.get(0).getScheduledStart() < earliestStart) {
      // Shift to right
      long start = Math.max(jobs.get(0).getScheduledStart(), earliestStart);
      jobs.get(0).setScheduledStart(start);

      if (jobs.size() > 1) {
        for (int j = 1; j < jobs.size(); j++) {
          jobs.get(j).setScheduledStart(
              Math.min(jobs.get(j).getScheduledStart(), jobs.get(j - 1).getScheduledStart()
                  + jobs.get(j - 1).getDuration()));
        }
      }
    } else if (jobs.get(jobs.size() - 1).getScheduledEnd() > latestComplete) {
      // shift to left
      int indexOfLast = jobs.size() - 1;
      long end = Math.min(jobs.get(indexOfLast).getScheduledEnd(), latestComplete);
      jobs.get(indexOfLast).setScheduledStart(end - jobs.get(indexOfLast).getDuration());

      if (jobs.size() > 1) {
        for (int j = indexOfLast - 1; j >= 0; j--) {
          jobs.get(j).setScheduledStart(
              Math.min(jobs.get(j).getScheduledStart(), jobs.get(j + 1).getScheduledStart()
                  - jobs.get(j + 1).getDuration()));
        }
      }

    }

    return jobs;
  }

}
