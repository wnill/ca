package de.wnill.master.simulator;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.wnill.master.core.bidgeneration.BidGenerator;
import de.wnill.master.core.scheduling.SchedulingAlgorithm;
import de.wnill.master.core.utils.ConversionHandler;
import de.wnill.master.core.utils.PowerSet;
import de.wnill.master.core.valuation.NonMonotonicLatenessValuation;
import de.wnill.master.core.valuation.Valuator;
import de.wnill.master.simulator.types.Bid;
import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;
import de.wnill.master.simulator.utils.JobStartTimeComparator;

public class Truck {

  private int id;

  private SchedulingAlgorithm scheduler;

  private Valuator valuator;

  // TODO make this a parameter
  private final Duration EARLIEST_BREAK_SCHEDULING_BEFORE_DUE = Duration.ofMinutes(15);

  private LocalTime lastBreak = LocalTime.MIN;

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
  private Duration roundtripTime = Duration.ZERO;

  public Truck(int id, SchedulingAlgorithm scheduler, Valuator valuator) {
    this.id = id;
    this.scheduler = scheduler;
    this.valuator = valuator;
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
   * @return the roundtripTime
   */
  public Duration getRoundtripTime() {
    return roundtripTime;
  }


  public List<Bid> makeBids(final List<Delivery> deliveries, LocalTime earliestStart,
      LocalTime latestComplete, BidGenerator generator) {

    if (generator == null) {
      return makeBids(deliveries, earliestStart, latestComplete);
    } else {
      return generator.generateBids(this, earliestStart, latestComplete);
    }
  }


  /**
   * ATTENTION: Is now only used for sequential bids!
   * 
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

    if (roundtripTime.isZero())
      throw new IllegalStateException("roundtriptime is null for truck " + id);

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
      if (newBid != null) {
        bids.add(newBid);
      }
    }

    return bids;
  }

  /**
   * Attention! Is now only used for sequential bids!
   * 
   * Creates a bid by scheduling a set of given jobs and then calculating a valuation, based on
   * deviations of proposed delivery times to requested delivery times.
   * 
   * @param sortedBundle
   * @param earliestStart early bound - deliveries before are invalid
   * @param latestComplete late bound - deliveries after are invalid
   * @return
   */
  private Bid createBid(Set<Delivery> bundle, LocalTime earliestStart, LocalTime latestComplete) {

    // convert deliveries to jobs
    LinkedList<Job> jobs = new LinkedList<>();
    // maps delivery id -> delivery
    HashMap<Integer, Delivery> deliveryMap = new HashMap<>();
    LocalTime lastDue = LocalTime.of(0, 0);
    Duration minimumRequiredTime = Duration.ZERO;
    for (Delivery delivery : bundle) {
      // TODO what if truck needs less than a roundtrip time (stops halfway, e.g.)?
      jobs.add(new Job(delivery, delivery.getRequestedTime(), roundtripTime));
      deliveryMap.put(delivery.getId(), delivery);
      minimumRequiredTime = minimumRequiredTime.plus(roundtripTime);
      if (delivery.getRequestedTime().isAfter(lastDue)) {
        lastDue = delivery.getRequestedTime();
      }
    }

    // insert "blockers", that is, unscheduled private jobs
    // for (Job privateJob : unscheduledPrivateJobs) {
    // if (privateJob.getTargetedStart().equals(lastDue)
    // || privateJob.getTargetedStart().isBefore(lastDue)
    // || earliestStart.plus(privateJob.getDuration()).plus(minimumRequiredTime)
    // .equals(privateJob.getDue())
    // || earliestStart.plus(privateJob.getDuration()).plus(minimumRequiredTime)
    // .isAfter(privateJob.getDue())) {
    // jobs.add(privateJob.clone());
    // }
    // }

    if (Constraints.getTruckPauseAfter() != null
        && !Constraints.getTruckPauseAfter().equals(Duration.ZERO)) {

      if (lastBreak.equals(LocalTime.MIN)) {
        lastBreak = earliestStart;
      }
      LocalTime breakDue = lastBreak.plus(Constraints.getTruckPauseAfter());

      if (lastDue.plus(Constraints.getTruckPauseDuration()).plus(roundtripTime).isAfter(breakDue)) {
        jobs.add(new Job(breakDue, Constraints.getTruckPauseDuration()));
      }
    }


    List<Job> bestSchedule =
        scheduler.scheduleJobs(jobs, earliestStart, latestComplete,
            new NonMonotonicLatenessValuation());

    // No feasible schedule within given bounds
    if (bestSchedule.isEmpty())
      return null;

    LinkedList<Job> unproductiveJobs = new LinkedList<>();
    LinkedList<Delivery> deliveries = new LinkedList<>();
    for (Job job : bestSchedule) {
      if (job.getDelivery() != null) {
        Delivery delivery = deliveryMap.get(job.getDelivery().getId());
        delivery.setProposedTime(job.getScheduledEnd());
        delivery.setStartTime(job.getScheduledStart());
        deliveries.add(delivery);
      } else {
        unproductiveJobs.add(job);
        lastBreak = job.getScheduledEnd();
      }
    }

    return new Bid(deliveries, unproductiveJobs, this, valuator.getValuation(bestSchedule));

  }

  /**
   * Assigns the deliveries specified in given bid to this truck.
   * 
   * @param bid
   */
  public void awardBid(Bid bid) {

    for (Job job : bid.getUnproductiveJobs()) {
      job.setId("B");
      schedule.add(job);

      Iterator<Job> it = unscheduledPrivateJobs.iterator();
      while (it.hasNext()) {
        Job unscheduled = it.next();
        if (unscheduled.getDue().equals(job.getDue())
            && unscheduled.getDuration().equals(job.getDuration())) {
          it.remove();
        }
      }
    }

    for (Delivery delivery : bid.getDeliveries()) {
      schedule.add(ConversionHandler.convertDeliveryToJob(delivery, roundtripTime));
    }

    Collections.sort(schedule, new JobStartTimeComparator());

    // Check if there is an unproductive job left, which must be scheduled next urgently
    Iterator<Job> it = unscheduledPrivateJobs.iterator();
    while (it.hasNext()) {
      Job unscheduled = it.next();
      if (Duration.between(schedule.get(schedule.size() - 1).getScheduledEnd(),
          unscheduled.getDue()).compareTo(roundtripTime) < 0) {
        unscheduled.setScheduledStart(schedule.get(schedule.size() - 1).getScheduledEnd());
        schedule.add(unscheduled);
        it.remove();
      }
    }
  }

  /**
   * No bids were awarded in this bid round. Thus, schedule unproductive jobs if possible.
   * 
   */
  public void rejectAllBids() {
    if (!schedule.isEmpty()) {
      LocalTime begin = schedule.get(schedule.size() - 1).getScheduledEnd();
      Iterator<Job> it = unscheduledPrivateJobs.iterator();
      while (it.hasNext()) {
        Job job = it.next();
        if (Duration.between(begin.plus(job.getDuration()), job.getDue()).compareTo(
            EARLIEST_BREAK_SCHEDULING_BEFORE_DUE) < 0) {
          job.setScheduledStart(begin);
          schedule.add(job);
          it.remove();
          begin = job.getScheduledEnd();
        }
      }
    }
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
    PowerSet<Delivery> ps = new PowerSet<>();
    return ps.powerSet(deliverySet);
  }



  /**
   * @return the schedule
   */
  public ArrayList<Job> getSchedule() {
    return schedule;
  }


  /**
   * @param schedule the schedule to set
   */
  public void setSchedule(List<Job> schedule) {
    this.schedule = new ArrayList<Job>(schedule);
  }


  /**
   * @return the lastBreak
   */
  public LocalTime getLastBreak() {
    return lastBreak;
  }


  /**
   * @param lastBreak the lastBreak to set
   */
  public void setLastBreak(LocalTime lastBreak) {
    this.lastBreak = lastBreak;
  }


  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

}
