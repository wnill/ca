package de.wnill.master.core.wdp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.wnill.master.simulator.types.Bid;
import de.wnill.master.simulator.types.Delivery;

public class EveryOneIsAWinner implements WinnerDeterminationAlgorithm {

  @Override
  public Set<Bid> determineWinners(Collection<Bid> bids, Collection<Delivery> deliveries) {
    return new HashSet<>(bids);
  }

}
