package de.wnill.master.core.wdp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.wnill.master.core.utils.PowerSet;
import de.wnill.master.simulator.types.Bid;
import de.wnill.master.simulator.types.Delivery;

/**
 * Determines winning bid set by creating a power set of all bids, then calculating the subset with
 * lowest total lateness which assigns each delivery exactly once.
 *
 */
public class NaiveExhaustiveEnumeration implements WinnerDeterminationAlgorithm {

  @Override
  public Set<Bid> determineWinners(Collection<Bid> bids, Collection<Delivery> deliveries) {

    ArrayList<Integer> allDeliveryIds = new ArrayList<>();
    for (Delivery delivery : deliveries) {
      allDeliveryIds.add(delivery.getId());
    }

    Set<Bid> bestSet = Collections.EMPTY_SET;
    long lowestLateness = Long.MAX_VALUE;

    PowerSet<Bid> ps = new PowerSet<>();
    Set<Set<Bid>> allSubSets = ps.powerSet(new HashSet<>(bids));

    for (Set<Bid> subset : allSubSets) {
      // Calculates lateness
      if (subset == null || subset.isEmpty() || (subset.size() == 1 && subset.contains(null)))
        continue;

      // if this subset covers a delivery multiple times it is invalid
      if (containsDuplicateDeliveries(subset))
        continue;

      // only one bid per truck may be in final result
      if (containsDuplicateTrucks(subset))
        continue;

      ArrayList<Integer> uncoveredIds = (ArrayList<Integer>) allDeliveryIds.clone();
      long tmpLateness = 0;
      for (Bid bid : subset) {
        if (bid != null) {
          tmpLateness += bid.getSumLateness().toMinutes();
          for (Delivery delivery : bid.getDeliveries()) {

            Iterator<Integer> it = uncoveredIds.iterator();
            while (it.hasNext()) {
              Integer next = it.next();
              if (next.equals(delivery.getId())) {
                it.remove();
              }
            }
          }
        }
      }

      if (uncoveredIds.isEmpty() && tmpLateness < lowestLateness) {
        lowestLateness = tmpLateness;
        bestSet = subset;
      }
    }

    System.out.println("Best bid set with total lateness of " + lowestLateness + ": " + bestSet);

    return bestSet;
  }


  private boolean containsDuplicateDeliveries(Set<Bid> subset) {
    boolean duplicates = false;
    HashSet<Integer> coveredIds = new HashSet<>();
    for (Bid bid : subset) {
      if (bid != null) {
        for (Delivery delivery : bid.getDeliveries()) {
          if (!coveredIds.add(delivery.getId())) {
            duplicates = true;
          }
        }
      }
    }
    return duplicates;
  }

  private boolean containsDuplicateTrucks(Set<Bid> subset) {
    boolean duplicates = false;
    HashSet<Integer> coveredTruckIds = new HashSet<>();
    for (Bid bid : subset) {
      if (bid != null && !coveredTruckIds.add(bid.getTruck().getId())) {
        duplicates = true;
      }
    }
    return duplicates;
  }
}
