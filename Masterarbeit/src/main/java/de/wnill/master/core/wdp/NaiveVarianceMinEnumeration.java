package de.wnill.master.core.wdp;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wnill.master.core.utils.PowerSet;
import de.wnill.master.simulator.types.Bid;
import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.utils.DeliveryProposedTimeComparator;

/**
 * Determines winning bid set by creating a power set of all bids, then calculating the subset with
 * lowest total variance which assigns each delivery exactly once.
 *
 */
public class NaiveVarianceMinEnumeration implements WinnerDeterminationAlgorithm {

  private static final Logger logger = LoggerFactory.getLogger(NaiveVarianceMinEnumeration.class);

  @Override
  public Set<Bid> determineWinners(Collection<Bid> bids, Collection<Delivery> deliveries) {

    ArrayList<Integer> allDeliveryIds = new ArrayList<>();
    for (Delivery delivery : deliveries) {
      allDeliveryIds.add(delivery.getId());
    }

    Set<Bid> bestSet = Collections.EMPTY_SET;
    long lowestVariance = Long.MAX_VALUE;

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

      // must contain all delivery IDs
      if (!containsAllDeliveryIds(allDeliveryIds, subset)) {
        continue;
      }

      LocalTime firstDelivery = null;
      LocalTime lastDelivery = null;
      LinkedList<Delivery> allDeliveries = new LinkedList<>();
      int deliveryCount = 0;

      for (Bid bid : subset) {
        if (bid != null) {
          if (firstDelivery == null
              || firstDelivery.isBefore(bid.getDeliveries().getFirst().getProposedTime())) {
            firstDelivery = bid.getDeliveries().getFirst().getProposedTime();
          }
          if (lastDelivery == null
              || lastDelivery.isAfter(bid.getDeliveries().getLast().getProposedTime())) {
            lastDelivery = bid.getDeliveries().getLast().getProposedTime();
          }
          deliveryCount += bid.getDeliveryIds().size();
          allDeliveries.addAll(bid.getDeliveries());
        }
      }

      long optimalInterval =
          Duration.between(firstDelivery, lastDelivery).toMinutes() / deliveryCount;

      Collections.sort(allDeliveries, new DeliveryProposedTimeComparator());

      LocalTime expectedDelivery = firstDelivery;
      long variance = 0;
      for (Delivery delivery : allDeliveries) {
        variance +=
            Duration.between(delivery.getProposedTime(), expectedDelivery).abs().toMinutes();
        expectedDelivery = expectedDelivery.plus(Duration.ofMinutes(optimalInterval));
      }


      if (variance < lowestVariance) {
        lowestVariance = variance;
        bestSet = subset;
      }
    }

    logger.info("Best bid set with total variance of " + lowestVariance + ": " + bestSet);

    return bestSet;
  }


  private boolean containsAllDeliveryIds(ArrayList<Integer> allDeliveryIds, Set<Bid> subset) {
    Set<Integer> containedIds = new HashSet<>();
    for (Bid bid : subset) {
      containedIds.addAll(bid.getDeliveryIds());
    }
    Set<Integer> expectedIds = new HashSet<>(allDeliveryIds);
    return containedIds.containsAll(expectedIds);
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
