package de.wnill.master.core.bidgeneration;

import java.util.LinkedList;
import java.util.List;

import de.wnill.master.simulator.types.Bid;

public class BidFilter {

  public List<Bid> filterUnwantedBids(List<Bid> bids) {
    List<Bid> filtered = new LinkedList<>();
    for (Bid bid : bids) {
      if (bid.getDeliveries().size() > 1) {
        filtered.add(bid);
      }
    }

    // if no bid possible otherwise, add 1-element-bids
    if (filtered.isEmpty()) {
      for (Bid bid : bids) {
        if (bid.getDeliveries().size() == 1) {
          filtered.add(bid);
        }
      }
    }

    return filtered;
  }
}
