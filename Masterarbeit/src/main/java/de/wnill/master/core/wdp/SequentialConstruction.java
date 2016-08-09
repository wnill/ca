package de.wnill.master.core.wdp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.wnill.master.simulator.types.Bid;
import de.wnill.master.simulator.types.Delivery;

public class SequentialConstruction implements WinnerDeterminationAlgorithm {

  @Override
  public Set<Bid> determineWinners(Collection<Bid> bids, Collection<Delivery> deliveries) {

    HashSet<Bid> winnerSet = new HashSet<>();
    for (Delivery delivery : deliveries) {
      int id = delivery.getId();

      Bid bestCandidate = null;
      long bestValuation = Long.MAX_VALUE;

      for (Bid bid : bids) {
        if (bid.getDeliveryIds().size() == 1 && bid.getDeliveryIds().get(0) == id) {
          if (bestCandidate == null || bid.getValuation() < bestValuation) {
            bestCandidate = bid;
            bestValuation = bid.getValuation();
          }
        }
      }
      winnerSet.add(bestCandidate);
    }

    return winnerSet;
  }

}
