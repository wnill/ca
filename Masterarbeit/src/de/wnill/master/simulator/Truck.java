package de.wnill.master.simulator;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.wnill.master.core.Bid;
import de.wnill.master.core.scheduling.SchedulingAlgorithm;
import de.wnill.master.core.utils.PowerSet;
import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;

public class Truck {

  private int id;

  private SchedulingAlgorithm scheduler;

  /**
   * Contains a list of all truck-specific jobs that MUST be executed - pauses, maintenance, etc.
   * but have not been scheduled yet.
   */
  private List<Job> unscheduledPrivateJobs = new LinkedList<>();

  /** The truck's schedule, thus, jobs that have been assigned an execution time. */
  private ArrayList<Job> schedule = new ArrayList<>();

  /**
   * time needed for a complete delivery roundtrip (drive to batch plant, loading, drive to
   * construction site, offloading).
   */
  private Duration roundtripTime;

  public Truck(int id, SchedulingAlgorithm scheduler) {
    this.id = id;
    this.scheduler = scheduler;
  }


  /**
   * Adds a job to the list of truck jobs, during which it is not available for deliveries, e.g. due
   * to wait times.
   * 
   * @param start proposed start date
   * @param end proposed end date
   * @param latestEnd latest allowed finish date for this activity
   */
  public void addPrivateJob(Duration duration, LocalTime latestEnd) {
    unscheduledPrivateJobs.add(new Job(latestEnd, duration));
  }

  /**
   * @param roundtripTime the roundtripTime to set
   */
  public void setRoundtripTime(Duration roundtripTime) {
    this.roundtripTime = roundtripTime;
  }


  /**
   * Creates bids for the powerset of given deliveries, which are to be executed between an earliest
   * start date and latest completion date.
   * 
   * @param deliveries
   * @param earliestStart
   * @param latestComplete
   * @return List of bids
   */
  public List<Bid> makeBids(final List<Delivery> deliveries, LocalTime earliestStart,
      LocalTime latestComplete) {

    Set<Set<Delivery>> powerset = getPowerSet(deliveries);
    List<Bid> bids = new LinkedList<>();

    // earliest start time for this set of deliveries may be postponed due to already scheduled
    // jobs.
    if (!schedule.isEmpty()
        && schedule.get(schedule.size() - 1).getScheduledEnd().isAfter(earliestStart)) {
      earliestStart = schedule.get(schedule.size() - 1).getScheduledEnd();
    }

    for (Set<Delivery> bundle : powerset) {
      // Now we have a bundle containing one possible combination of deliveries
      Bid newBid = createBid(bundle, earliestStart, latestComplete);
      bids.add(newBid);
    }
    return bids;
  }

  /**
   * Creates a bid by scheduling a set of given jobs and then calculating a valuation, based on
   * deviations of proposed delivery times to requested delivery times.
   * 
   * @param sortedBundle
   * @param earliestStart
   * @param latestComplete
   * @return
   */
  private Bid createBid(Set<Delivery> bundle, LocalTime earliestStart, LocalTime latestComplete) {


    // convert deliveries to jobs
    LinkedList<Job> jobs = new LinkedList<>();
    HashMap<Integer, Delivery> deliveryMap = new HashMap<>();
    for (Delivery delivery : bundle) {
      // TODO what if truck needs less than a roundtrip time (stops halfway, e.g.)?
      jobs.add(new Job(delivery, delivery.getRequestedTime(), roundtripTime));
      deliveryMap.put(delivery.getId(), delivery);
    }

    // insert "blockers", that is, unscheduled private jobs
    for (Job privateJob : unscheduledPrivateJobs) {
      jobs.add(privateJob);
    }

    List<Job> bestSchedule = scheduler.scheduleJobs(jobs, earliestStart, latestComplete);

    // No feasible schedule within given bounds
    if (bestSchedule.isEmpty())
      return null;

    for (Job job : bestSchedule) {
      if (job.getDelivery() != null) {
        Delivery delivery = deliveryMap.get(job.getDelivery().getId());
        delivery.setProposedTime(job.getScheduledEnd());
      }
    }

    return new Bid(deliveryMap.values());
  }


  /**
   * Returns a set of all possible subsets of a given set / list.
   * 
   * @param deliveries
   * @return
   */
  private Set<Set<Delivery>> getPowerSet(List<Delivery> deliveries) {
    Set<Delivery> deliverySet = new HashSet<>();
    for (Delivery delivery : deliveries) {
      deliverySet.add(delivery);
    }
    return PowerSet.powerSet(deliverySet);
  }



  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

}
