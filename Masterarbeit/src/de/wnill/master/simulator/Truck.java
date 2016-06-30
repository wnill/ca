package de.wnill.master.simulator;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.wnill.master.core.Bid;
import de.wnill.master.core.scheduling.MaximumLateness;
import de.wnill.master.core.utils.PowerSet;
import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;

public class Truck {

  private int id;

  /**
   * time needed for a complete delivery roundtrip (drive to batch plant, loading, drive to
   * construction site, offloading).
   */
  private long roundtripTime;

  private List<Timeslot> blockers = new LinkedList<>();

  public Truck(int id) {
    this.id = id;
  }

  public Truck(int id, long roundtripTime) {
    this.id = id;
    this.roundtripTime = roundtripTime;
  }

  /**
   * Models a timespan in which the truck is not available for deliveries, e.g. due to wait times.
   * 
   * @param start proposed start date
   * @param end proposed end date
   * @param latestEnd latest allowed finish date for this activity
   */
  public void addBlocker(long duration, long latestEnd) {
    Timeslot slot = new Timeslot(duration, latestEnd);
    slot.setLatestEnd(latestEnd);
    blockers.add(slot);
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
  public List<Bid> makeBidsForAllDeliveries(final List<Delivery> deliveries, long earliestStart,
      long latestComplete) {

    Set<Set<Long>> powerset = getPowerSet(deliveries);
    List<Bid> bids = new LinkedList<>();

    for (Set<Long> bundle : powerset) {
      // Now we have a bundle containing one possible combination of deliveries
      TreeSet<Long> sortedBundle = new TreeSet<Long>(bundle);
      Bid newBid = createBid(sortedBundle, earliestStart, latestComplete, deliveries);
      bids.add(newBid);
    }
    return bids;
  }

  /**
   * Creates a bid (valuations for a given set of requested deliveries).
   * 
   * @param sortedBundle
   * @param earliestStart
   * @param latestComplete
   * @param deliveries
   * @return
   */
  private Bid createBid(TreeSet<Long> sortedBundle, long earliestStart, long latestComplete,
      final List<Delivery> deliveries) {

    // convert delivery dates to jobs
    LinkedList<Job> jobs = new LinkedList<>();
    for (Long dueDate : sortedBundle) {
      jobs.add(new Job(getDeliveryForRequestedTime(dueDate, deliveries), dueDate, roundtripTime));
    }

    // insert blockers
    for (Timeslot blocker : blockers) {
      jobs.add(new Job(null, blocker.getLatestEnd(), blocker.getDuration()));
    }

    // sort the complete list by job target start date
    Collections.sort(jobs, new Comparator<Job>() {
      @Override
      public int compare(Job j1, Job j2) {
        return (int) (j1.getTargetedStart() - j2.getTargetedStart());
      }
    });

    // TODO how to determine weights?
    MaximumLateness alg = new MaximumLateness(2, 3);
    List<Job> schedule = alg.scheduleJobs(jobs, earliestStart, latestComplete);

    HashMap<Delivery, Long> bidmap = new HashMap<Delivery, Long>();

    for (Job job : schedule) {
      if (job.getDelivery() != null) {
        job.getDelivery().setProposedTime(job.getScheduledEnd());
        long valuation = job.getScheduledEnd() - job.getDue();
        bidmap.put(job.getDelivery(), valuation);
      }
    }
    Bid bid = new Bid(bidmap);
    return bid;
  }

  /**
   * Returns a copy of a delivery contained in the given list which is due on a given time.
   * 
   * @param time
   * @param deliveries
   * @return
   */
  public Delivery getDeliveryForRequestedTime(long time, List<Delivery> deliveries) {
    for (Delivery delivery : deliveries) {
      if (delivery.getRequestedTime() == time) {
        return delivery.clone();
      }
    }
    return null;
  }

  /**
   * Returns a set of all possible subsets of a given set / list.
   * 
   * @param deliveries
   * @return
   */
  private Set<Set<Long>> getPowerSet(List<Delivery> deliveries) {
    Set<Long> deliveryDates = new HashSet<>();
    for (Delivery delivery : deliveries) {
      deliveryDates.add(delivery.getRequestedTime());
    }
    return PowerSet.powerSet(deliveryDates);
  }



  public class Timeslot {

    private long duration;
    private long latestEnd;

    /**
     * 
     * @param duration
     * @param latestEnd
     */
    public Timeslot(long duration, long latestEnd) {
      this.duration = duration;
      this.latestEnd = latestEnd;
    }

    /**
     * @return the latestEnd
     */
    public long getLatestEnd() {
      return latestEnd;
    }

    /**
     * @param latestEnd the latestEnd to set
     */
    public void setLatestEnd(long latestEnd) {
      this.latestEnd = latestEnd;
    }

    /**
     * @return the duration
     */
    public long getDuration() {
      return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(long duration) {
      this.duration = duration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "Timeslot [duration=" + duration + ", latestEnd=" + latestEnd + "]";
    }


  }
}
