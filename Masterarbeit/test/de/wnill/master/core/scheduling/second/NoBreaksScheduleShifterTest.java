package de.wnill.master.core.scheduling.second;


import static org.junit.Assert.assertEquals;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import de.wnill.master.core.scheduling.NeighborhoodSearch;
import de.wnill.master.core.valuation.NonMonotonicLatenessValuation;
import de.wnill.master.simulator.Truck;
import de.wnill.master.simulator.types.Bid;
import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.utils.DeliveryProposedTimeComparator;

public class NoBreaksScheduleShifterTest {

  @Test
  public void testILPvalidity() {
    NoBreaksScheduleShifter instance = new NoBreaksScheduleShifter();

    Set<Bid> bids = new HashSet<>();

    LinkedList<Delivery> deliveries1 = new LinkedList<>();
    Delivery delivery = new Delivery(1, LocalTime.of(8, 0));
    delivery.setProposedTime(LocalTime.of(8, 0));
    deliveries1.add(delivery);

    delivery = new Delivery(3, LocalTime.of(8, 54));
    delivery.setProposedTime(LocalTime.of(10, 25));
    deliveries1.add(delivery);

    delivery = new Delivery(5, LocalTime.of(9, 48));
    delivery.setProposedTime(LocalTime.of(13, 25));
    deliveries1.add(delivery);

    Bid bid1 =
        new Bid(deliveries1, new LinkedList<>(), new Truck(1, new NeighborhoodSearch(),
            new NonMonotonicLatenessValuation()), 0);
    bids.add(bid1);

    LinkedList<Delivery> deliveries2 = new LinkedList<>();
    delivery = new Delivery(2, LocalTime.of(8, 27));
    delivery.setProposedTime(LocalTime.of(8, 27));
    deliveries2.add(delivery);

    delivery = new Delivery(4, LocalTime.of(9, 21));
    delivery.setProposedTime(LocalTime.of(11, 27));
    deliveries2.add(delivery);

    delivery = new Delivery(6, LocalTime.of(10, 15));
    delivery.setProposedTime(LocalTime.of(13, 52));
    deliveries2.add(delivery);

    Bid bid2 =
        new Bid(deliveries2, new LinkedList<>(), new Truck(2, new NeighborhoodSearch(),
            new NonMonotonicLatenessValuation()), 0);
    bids.add(bid2);

    LinkedList<Delivery> allDeliveries = new LinkedList<>();
    allDeliveries.addAll(deliveries1);
    allDeliveries.addAll(deliveries2);
    Collections.sort(allDeliveries, new DeliveryProposedTimeComparator());

    HashMap<Integer, Long> fixedTimes = new HashMap<>();
    fixedTimes.put(1, 0L);
    fixedTimes.put(3, 145L);
    fixedTimes.put(5, 325L);

    List<Long> result = instance.solveIlp(bids, 6, allDeliveries, fixedTimes);

    assertEquals(0L, (Long) result.get(0), 0);
    assertEquals(67, (Long) result.get(1), 0);
    assertEquals(145, (Long) result.get(2), 0);
    assertEquals(247, (Long) result.get(3), 0);
    assertEquals(325, (Long) result.get(4), 0);
    assertEquals(392, (Long) result.get(5), 0);
  }

  @Test
  public void testUpdateBids() {
    NoBreaksScheduleShifter instance = new NoBreaksScheduleShifter();

    Set<Bid> bids = new HashSet<>();

    LinkedList<Delivery> deliveries1 = new LinkedList<>();
    Delivery delivery = new Delivery(1, LocalTime.of(8, 0));
    delivery.setProposedTime(LocalTime.of(8, 0));
    deliveries1.add(delivery);

    delivery = new Delivery(3, LocalTime.of(8, 54));
    delivery.setProposedTime(LocalTime.of(10, 25));
    deliveries1.add(delivery);

    delivery = new Delivery(5, LocalTime.of(9, 48));
    delivery.setProposedTime(LocalTime.of(13, 25));
    deliveries1.add(delivery);

    Bid bid1 =
        new Bid(deliveries1, new LinkedList<>(), new Truck(1, new NeighborhoodSearch(),
            new NonMonotonicLatenessValuation()), 0);
    bids.add(bid1);

    LinkedList<Delivery> deliveries2 = new LinkedList<>();
    delivery = new Delivery(2, LocalTime.of(8, 27));
    delivery.setProposedTime(LocalTime.of(8, 27));
    deliveries2.add(delivery);

    delivery = new Delivery(4, LocalTime.of(9, 21));
    delivery.setProposedTime(LocalTime.of(11, 27));
    deliveries2.add(delivery);

    delivery = new Delivery(6, LocalTime.of(10, 15));
    delivery.setProposedTime(LocalTime.of(13, 52));
    deliveries2.add(delivery);

    Bid bid2 =
        new Bid(deliveries2, new LinkedList<>(), new Truck(2, new NeighborhoodSearch(),
            new NonMonotonicLatenessValuation()), 0);
    bids.add(bid2);

    Set<Bid> updated = instance.updateBids(bids);

    LinkedList<Delivery> updatedDeliveries = new LinkedList<>();
    for (Bid bid : updated) {
      updatedDeliveries.addAll(bid.getDeliveries());
    }
    Collections.sort(updatedDeliveries, new DeliveryProposedTimeComparator());

    assertEquals(LocalTime.of(8, 0), updatedDeliveries.get(0).getProposedTime());
    assertEquals(LocalTime.of(9, 7), updatedDeliveries.get(1).getProposedTime());
    assertEquals(LocalTime.of(10, 25), updatedDeliveries.get(2).getProposedTime());
    assertEquals(LocalTime.of(12, 7), updatedDeliveries.get(3).getProposedTime());
    assertEquals(LocalTime.of(13, 25), updatedDeliveries.get(4).getProposedTime());
    assertEquals(LocalTime.of(14, 32), updatedDeliveries.get(5).getProposedTime());


  }
}
