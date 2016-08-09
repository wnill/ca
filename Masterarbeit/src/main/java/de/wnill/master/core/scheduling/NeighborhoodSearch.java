package de.wnill.master.core.scheduling;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wnill.master.core.valuation.Valuator;
import de.wnill.master.simulator.types.Job;
import de.wnill.master.simulator.utils.JobDueDateComparator;

/**
 * Scheduling based on static priorization (by due date) as start sequence. Afterwards, breaks are
 * shifted earlier stepwise, to ensure they do not exceed their due dates.
 */
public class NeighborhoodSearch implements SchedulingAlgorithm {

  private static final Logger logger = LoggerFactory.getLogger(NeighborhoodSearch.class);
  private LocalTime earliestStart;
  private LocalTime latestComplete;

  @Override
  public List<Job> scheduleJobs(List<Job> jobs, LocalTime earliestStart, LocalTime latestComplete,
      Valuator valuator) {

    this.earliestStart = earliestStart;
    this.latestComplete = latestComplete;

    // Start sequence is ordering by Earliest Due Date (EDD) first
    Collections.sort(jobs, new JobDueDateComparator());

    List<Job> bestSequence = findBestSchedule(jobs, valuator);
    logger.debug("best Sequence: " + bestSequence);

    return timeJobs(bestSequence, earliestStart);
  }

  /**
   * Based on Szwarc.
   * 
   * @param jobs
   * @return
   */
  public List<Job> timeJobs(List<Job> jobs, LocalTime earliestStart) {

    if (jobs.size() == 1 && jobs.get(0).getDelivery() != null
        && jobs.get(0).getDelivery().getId() == 2) {
      System.out.println("break!");
    }

    LinkedList<Job> schedule = new LinkedList<>(jobs);
    List<Integer> alphas = generatesAlphas(jobs);
    List<Integer> betas = generateBetas(jobs);

    // Step 0 - Identify clusters and caluclate related values
    LinkedList<Cluster> clusters = initializeClusters(earliestStart, schedule, alphas, betas);

    processNextCluster(clusters);
    return schedule;
  }

  protected void processNextCluster(LinkedList<Cluster> clusters) {

    if (clusters.isEmpty())
      return;

    // Step 1 - find cluster with smallest index and aggregated delta <= 0
    int s = -1;
    long aggregatedDelta = 0;
    for (Cluster cluster : clusters) {
      aggregatedDelta += cluster.getDelta();
      if (aggregatedDelta <= 0) {
        s = clusters.indexOf(cluster);
        break;
      }
    }

    if (s == clusters.size() - 1) {
      return;
    }

    // Step 3
    if (s == -1) {
      // Determine minimum E
      long minE = Long.MAX_VALUE;
      for (Cluster cluster : clusters) {
        if (cluster.getE() < minE) {
          minE = cluster.getE();
        }
      }


      for (Cluster cluster : clusters) {
        // Shift each Job
        for (Job job : cluster.getJobs()) {
          job.setScheduledStart(job.getScheduledStart().plus(Duration.ofMinutes(minE)));
        }

        // Remove latest early jobs that are not early anymore
        while (cluster.getLastEarlyJob() != null
            && (cluster.getLastEarlyJob().getScheduledEnd()
                .isAfter(cluster.getLastEarlyJob().getDue()) || cluster.getLastEarlyJob()
                .getScheduledEnd().equals(cluster.getLastEarlyJob().getDue()))) {
          int index = cluster.getJobs().indexOf(cluster.getLastEarlyJob());
          if (index > 0) {
            cluster.setLastEarlyJob(cluster.getJobs().get(index - 1));
          } else {
            cluster.setLastEarlyJob(null);
          }
        }
        // Update E and delta
        cluster.calculateValues();
      }
      processNextCluster(clusters);
    }


    // Step 2
    if (s >= 0 && s < clusters.size() - 1) {
      int toRemove = s + 1;
      for (int i = 0; i < toRemove; i++) {
        clusters.removeFirst();
      }
      processNextCluster(clusters);
    }


  }

  private List<Job> findBestSchedule(List<Job> jobs, Valuator valuator) {
    List<Job> initSchedule = timeJobs(jobs, earliestStart);

    long bestValuation = valuator.getValuation(initSchedule);
    List<Job> bestSequence = jobs;

    for (Job job : jobs) {
      // First, find a break
      if (job.getDelivery() == null) {
        // Second, create permutations of the sequence, where break is scheduled earlier and
        // evaluate
        int originalPosition = jobs.indexOf(job);
        int position = originalPosition - 1;

        while (position >= 0) {
          LinkedList<Job> permutation = new LinkedList<>(jobs);
          Job theBreak = permutation.remove(originalPosition);
          permutation.add(position, theBreak);
          List<Job> schedule = timeJobs(permutation, earliestStart);
          if (valuator.getValuation(schedule) < bestValuation) {
            bestValuation = valuator.getValuation(schedule);
            bestSequence = schedule;
          }
          position--;
        }
      }
    }
    return bestSequence;
  }

  private List<Integer> generateBetas(List<Job> jobs) {
    ArrayList<Integer> betas = new ArrayList<>();
    for (Job job : jobs) {
      if (job.getDelivery() == null) {
        betas.add(10000);
      } else {
        betas.add(10);
      }
    }

    return betas;
  }

  private List<Integer> generatesAlphas(List<Job> jobs) {
    ArrayList<Integer> alphas = new ArrayList<>();
    for (Job job : jobs) {
      if (job.getDelivery() == null) {
        alphas.add(1);
      } else {
        alphas.add(5);
      }
    }

    return alphas;
  }

  protected LinkedList<Cluster> initializeClusters(LocalTime earliestStart,
      LinkedList<Job> schedule, List<Integer> alphas, List<Integer> betas) {
    LinkedList<Cluster> clusters = new LinkedList<>();
    Cluster cluster = new Cluster();
    clusters.add(cluster);

    // Step 0 - initialize
    LocalTime startTime = earliestStart;
    Duration earliness = Duration.ofMinutes(100000);
    for (int i = 0; i < schedule.size(); i++) {
      Job job = schedule.get(i);

      job.setScheduledStart(startTime);
      Duration newEarliness = Duration.between(job.getScheduledEnd(), job.getDue());

      if (i == 0 && !newEarliness.isNegative() && !newEarliness.isZero()) {
        cluster.setLastEarlyJob(schedule.getFirst());
      }

      if (!earliness.isNegative() && newEarliness.isNegative() && i > 0) {
        cluster.setLastEarlyJob(schedule.get(i - 1));
      }

      if (newEarliness.compareTo(earliness) > 0) {
        cluster.calculateValues();
        cluster = new Cluster();
        clusters.add(cluster);

        if (!newEarliness.isNegative() && !newEarliness.isZero()) {
          cluster.setLastEarlyJob(schedule.get(i));
        }

      }
      cluster.addJob(job, alphas.get(i), betas.get(i));
      earliness = newEarliness;
      startTime = startTime.plus(job.getDuration());
    }

    // dont forget to calculate the values of last cluster
    cluster.calculateValues();

    // Cleanup empty clusters
    Iterator<Cluster> it = clusters.iterator();
    while (it.hasNext()) {
      if (it.next().getJobs().isEmpty()) {
        it.remove();
      }
    }

    return clusters;
  }

  public class Cluster {
    private List<Job> jobs;
    private List<Integer> alphas;
    private List<Integer> betas;
    private Job lastEarlyJob;
    private long delta0 = 0;
    private long E;
    private long delta;

    public Cluster() {
      jobs = new LinkedList<>();
      alphas = new LinkedList<>();
      betas = new LinkedList<>();
    }

    public void calculateValues() {
      delta0 = 0;
      for (Integer beta : betas) {
        delta0 -= beta;
      }

      if (lastEarlyJob == null) {
        E = Long.MAX_VALUE;
        delta = delta0;
        return;
      }

      int indexOfLastEarlyJob = jobs.indexOf(lastEarlyJob);
      delta =
          calcDelta(indexOfLastEarlyJob - 1) + alphas.get(indexOfLastEarlyJob)
              + betas.get(indexOfLastEarlyJob);

      E = Long.MAX_VALUE;
      for (int i = 0; i <= indexOfLastEarlyJob; i++) {
        long newE =
            Duration.between(jobs.get(i).getScheduledEnd(), jobs.get(i).getDue()).toMinutes();
        if (newE < E) {
          E = newE;
        }
      }

    }

    private long calcDelta(int i) {
      if (i < 0) {
        return delta0;
      } else {
        return calcDelta(i - 1) + alphas.get(i) + betas.get(i);
      }
    }

    /**
     * @return the delta0
     */
    public long getDelta0() {
      return delta0;
    }

    /**
     * @param delta0 the delta0 to set
     */
    public void setDelta0(long delta0) {
      this.delta0 = delta0;
    }

    public void addJob(Job job, Integer alpha, Integer beta) {
      jobs.add(job);
      alphas.add(alpha);
      betas.add(beta);
    }

    public List<Job> getJobs() {
      return jobs;
    }

    /**
     * @return the lastEarlyJobIndex
     */
    public Job getLastEarlyJob() {
      return lastEarlyJob;
    }

    /**
     * @param lastEarlyJobIndex the lastEarlyJobIndex to set
     */
    public void setLastEarlyJob(Job lastEarlyJob) {
      this.lastEarlyJob = lastEarlyJob;
    }

    /**
     * @return the e
     */
    public long getE() {
      return E;
    }

    /**
     * @return the delta
     */
    public long getDelta() {
      return delta;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "Cluster [jobs=" + jobs + ", lastEarlyJob=" + lastEarlyJob + ", delta0=" + delta0
          + "]";
    }
  }
}
