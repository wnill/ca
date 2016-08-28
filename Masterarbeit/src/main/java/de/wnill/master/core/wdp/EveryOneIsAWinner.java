package de.wnill.master.core.wdp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.wnill.master.simulator.types.Bid;
import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;

public class EveryOneIsAWinner implements WinnerDeterminationAlgorithm {

  @Override
  public Set<Bid> determineWinners(Collection<Bid> bids, Collection<Delivery> deliveries) {
    // Reduce the bid sizes to the required amount of deliveries
    int requiredCartloads = deliveries.size();

    int currentSize = 0;
    for (Bid bid : bids) {
      currentSize += bid.getDeliveries().size();
    }

    while (currentSize > requiredCartloads) {
      Bid biggest = getBiggestBid(bids);
      if (biggest.getDeliveries() != null && !biggest.getDeliveries().isEmpty()) {
        biggest.getDeliveries().removeLast();
        currentSize--;
      }
    }

    // Afterwards, clean up unnecessary breaks
    for (Bid bid : bids) {
      Iterator<Job> it = bid.getUnproductiveJobs().iterator();
      while (it.hasNext()) {
        Job job = it.next();
        if (job.getScheduledStart().isAfter(bid.getDeliveries().getLast().getProposedTime())) {
          it.remove();
        }
      }
    }

    return new HashSet<>(bids);
  }

  private Bid getBiggestBid(Collection<Bid> bids) {
    Bid biggest = null;
    for (Bid bid : bids) {
      if (biggest == null || bid.getDeliveries().size() > biggest.getDeliveries().size()) {
        biggest = bid;
      }
    }
    return biggest;
  }
}
