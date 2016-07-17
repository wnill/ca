package de.wnill.master.core.wdp;

import java.util.Collection;
import java.util.Set;

import de.wnill.master.simulator.types.Bid;
import de.wnill.master.simulator.types.Delivery;

@FunctionalInterface
public interface WinnerDeterminationAlgorithm {

  public Set<Bid> determineWinners(Collection<Bid> bids, Collection<Delivery> deliveries);

}
