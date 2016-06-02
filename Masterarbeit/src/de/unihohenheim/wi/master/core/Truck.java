package de.unihohenheim.wi.master.core;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Truck {

  private int id;

  private long timeToDeliver;

  private long timeToReturn;

  private List<Timeslot> blockedSlots = new LinkedList<>();

  public Truck(int id, long deliverTime, long returnTime) {
    this.id = id;
    this.timeToDeliver = deliverTime;
    this.timeToReturn = returnTime;
  }


  public void blockSlot(long start, long end) {
    blockedSlots.add(new Timeslot(start, end));
  }

  public List<Bid> makeBids(List<Delivery> deliveries) {

    // First find all possible subsets of requested deliveries
    Set<Set<Long>> powerset = getPowerSet(deliveries);

    List<Bid> bids = new LinkedList<>();
    // Second, evaluate the "price" (deviation from requested time) of all sets


    for (Set<Long> bundle : powerset) {
      Map<Delivery, Long> bid = new HashMap<>();

      // Now we have a bundle containing one possible combination of deliveries
      TreeSet<Long> sortedBundle = new TreeSet<Long>(bundle);

      List<Timeslot> tempBlockedSlots = new LinkedList<Timeslot>();
      for (Timeslot blocker : blockedSlots) {
        tempBlockedSlots.add(blocker);
      }

      // determine "price" for each delivery in the bundle
      for (Long expectedDelivery : sortedBundle) {

        long offeredDelivery = expectedDelivery;
        Timeslot deliveryTime = new Timeslot(expectedDelivery - timeToDeliver, expectedDelivery);
        Timeslot returnTime = new Timeslot(expectedDelivery, expectedDelivery + timeToReturn);

        // check if there is some collision of this delivery at target time
        boolean startAgain = true;
        while (startAgain) {
          for (Timeslot block : tempBlockedSlots) {
            if (block.collidesWith(deliveryTime) && !block.collidesWith(returnTime)) {
              // must delay this delivery
              long overlap = block.getEnd() - deliveryTime.getStart();
              deliveryTime.addOffset(overlap);
              returnTime.addOffset(overlap);
              break;
            } else if (!block.collidesWith(deliveryTime) && block.collidesWith(returnTime)) {
              // must deliver earlier
              long overlap = returnTime.getEnd() - block.getStart();
              deliveryTime.addOffset(-overlap);
              returnTime.addOffset(-overlap);
              break;
            } else if (block.collidesWith(deliveryTime) && block.collidesWith(returnTime)) {
              // TODO what now?
              // right now: just bump to the end
              long overlap = block.getEnd() - deliveryTime.getStart();
              deliveryTime.addOffset(overlap);
              returnTime.addOffset(overlap);
            }
          }
          // we ran through the for without moving - done!
          startAgain = false;
        }

        // now we have our final delivery times and we can make a proposal
        offeredDelivery = deliveryTime.getEnd();
        bid.put(getDeliveryForRequestedTime(expectedDelivery, deliveries), offeredDelivery);
        tempBlockedSlots.add(new Timeslot(deliveryTime.getStart(), returnTime.getEnd()));
      }

      Bid newBid = new Bid(bid);
      bids.add(newBid);
    }
    return bids;
  }

  public Delivery getDeliveryForRequestedTime(long time, List<Delivery> deliveries) {
    for (Delivery delivery : deliveries) {
      if (delivery.getRequestedTime() == time) {
        return delivery;
      }
    }
    return null;
  }

  private Set<Set<Long>> getPowerSet(List<Delivery> deliveries) {
    Set<Long> deliveryDates = new HashSet<>();
    for (Delivery delivery : deliveries) {
      deliveryDates.add(delivery.getRequestedTime());
    }
    return powerSet(deliveryDates);
  }

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
