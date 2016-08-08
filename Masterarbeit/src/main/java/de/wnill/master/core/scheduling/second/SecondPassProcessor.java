package de.wnill.master.core.scheduling.second;

import java.util.Set;

import de.wnill.master.simulator.types.Bid;

public interface SecondPassProcessor {

  public Set<Bid> updateBids(Set<Bid> bids);

}
