package de.wnill.master.core.bidgeneration;

import java.time.LocalTime;
import java.util.List;

import de.wnill.master.simulator.Truck;
import de.wnill.master.simulator.types.Bid;

public interface BidGenerator {

  List<Bid> generateBids(Truck truck, LocalTime startTime, LocalTime endTime);

}
