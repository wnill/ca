package de.wnill.master.core.bidgeneration;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.junit.Test;

import de.wnill.master.core.scheduling.NeighborhoodSearch;
import de.wnill.master.core.valuation.NonMonotonicLatenessValuation;
import de.wnill.master.simulator.Constraints;
import de.wnill.master.simulator.Truck;
import de.wnill.master.simulator.types.Bid;


public class FullScheduleGeneratorTest {


  @Test
  public void testGenerateBids() {
    Truck truck = new Truck(1, new NeighborhoodSearch(), new NonMonotonicLatenessValuation());
    truck.setRoundtripTime(Duration.ofMinutes(15));
    Constraints.setTruckPauseAfter(Duration.ofMinutes(40));
    Constraints.setTruckPauseDuration(Duration.ofMinutes(20));
    FullScheduleGenerator gen = new FullScheduleGenerator();
    List<Bid> bids = gen.generateBids(truck, LocalTime.of(8, 0), LocalTime.of(10, 00));

    System.out.println(bids);
  }
}
