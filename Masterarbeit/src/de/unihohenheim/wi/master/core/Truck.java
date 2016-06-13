package de.unihohenheim.wi.master.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.unihohenheim.wi.master.core.scheduling.MaximumLateness;

public class Truck {

  private int id;

  private long timeToDeliver;

  private long timeToReturn;

  private List<Timeslot> blockers = new LinkedList<>();

  public Truck(int id, long deliverTime, long returnTime) {
    this.id = id;
    this.timeToDeliver = deliverTime;
    this.timeToReturn = returnTime;
  }


  public void addBlocker(long start, long end, long latest) {
    Timeslot slot = new Timeslot(start, end);
    slot.setLatestEnd(latest);
    blockers.add(slot);
  }

  public List<Bid> makeBidsForAllDeliveries(final List<Delivery> deliveries, long earliestStart,
      long latestComplete) {

    // First find all possible subsets of requested deliveries
    Set<Set<Long>> powerset = getPowerSet(deliveries);

    List<Bid> bids = new LinkedList<>();
    // Second, evaluate the "price" (deviation from requested time) of all sets


    for (Set<Long> bundle : powerset) {

      // Now we have a bundle containing one possible combination of deliveries
      TreeSet<Long> sortedBundle = new TreeSet<Long>(bundle);
      Bid newBid = createBid(sortedBundle, earliestStart, latestComplete, deliveries);
      bids.add(newBid);
    }
    return bids;
  }

  /**
   * Input is a sorted set containing the desired deliveries. This method tries to fit these desired
   * delivery dates as good as possible into the schedule, considering blocked slots.
   * 
   * @param sortedBundle
   * @return
   */
  private Bid createBid(TreeSet<Long> sortedBundle, long earliestStart, long latestComplete,
      final List<Delivery> deliveries) {

    // convert delivery dates to jobs
    LinkedList<Job> jobs = new LinkedList<>();
    for (Long dueDate : sortedBundle) {
      jobs.add(new Job("delivery", dueDate, timeToDeliver + timeToReturn));
    }

    // insert blockers
    for (Timeslot blocker : blockers) {
      jobs.add(new Job("blocker", blocker.getLatestEnd(), blocker.getEnd() - blocker.getStart()));
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

    System.out.println("Optimal Schedule: " + schedule + " for target delivery dates "
        + sortedBundle);

    for (Job job : schedule) {
      if ("delivery".equals(job.getId())) {
        Delivery delivery = getDeliveryForRequestedTime(job.getDue(), deliveries);
        delivery.setProposedTime(job.getScheduledEnd());
        long valuation = job.getScheduledEnd() - job.getDue();
        bidmap.put(delivery, valuation);
      }
    }
    Bid bid = new Bid(bidmap);
    return bid;
  }

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
    return powerSet(deliveryDates);
  }

  /**
   * Returns a set of all possible subsets of a given set.
   * 
   * @param originalSet
   * @return
   */
  private static Set<Set<Long>> powerSet(Set<Long> originalSet) {
    Set<Set<Long>> sets = new HashSet<Set<Long>>();
    if (originalSet.isEmpty()) {
      sets.add(new HashSet<Long>());
      return sets;
    }
    List<Long> list = new ArrayList<Long>(originalSet);
    Long head = list.get(0);
    Set<Long> rest = new HashSet<Long>(list.subList(1, list.size()));
    for (Set<Long> set : powerSet(rest)) {
      Set<Long> newSet = new HashSet<Long>();
      newSet.add(head);
      newSet.addAll(set);
      sets.add(newSet);
      sets.add(set);
    }
    return sets;
  }


  public class Timeslot {

    private long start;
    private long end;
    private long latestEnd;

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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "Timeslot [start=" + start + ", end=" + end + "]";
    }

    public Timeslot(long start, long end) {
      this.start = start;
      this.end = end;
    }

    /**
     * @return the start
     */
    public long getStart() {
      return start;
    }

    /**
     * @return the end
     */
    public long getEnd() {
      return end;
    }

    public boolean isTimeWithinThisSlot(long time) {
      if (time > getStart() && time < getEnd())
        return true;
      else
        return false;
    }

    public boolean collidesWith(Timeslot other) {
      if ((other.getStart() < start && other.getEnd() > start)
          || (other.getStart() > start && other.getEnd() < end)
          || (other.getStart() < end && other.getEnd() > end))
        return true;
      else
        return false;
    }

    public void addOffset(long time) {
      start += time;
      end += time;
    }
  }
}
