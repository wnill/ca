package de.unihohenheim.wi.master.core;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;


public class TruckTest {

  @Test
  public void testBlockerAtTheEnd() {
    Truck truck = new Truck(1, 10, 10);
    LinkedList<Delivery> request = new LinkedList<>();
    truck.blockSlot(20, 50);
    Delivery delivery1 = new Delivery(1, 10);
    Delivery delivery2 = new Delivery(2, 30);

    request.add(delivery1);
    request.add(delivery2);

    List<Bid> bids = truck.makeBids(request);
    assertEquals(4, bids.size());

    // first delivery arrives at 10, second at 60
    for (Bid bid : bids) {
      if (bid.getBidSet().size() == 2) {
        assertEquals(10, (long) bid.getBidSet().get(delivery1));
        assertEquals(60, (long) bid.getBidSet().get(delivery2));
      }
    }
  }

  @Test
  public void testBlockerAtTheBeginning() {
    Truck truck = new Truck(1, 10, 10);
    LinkedList<Delivery> request = new LinkedList<>();
    truck.blockSlot(10, 22);
    Delivery delivery1 = new Delivery(1, 20);
    Delivery delivery2 = new Delivery(2, 40);

    request.add(delivery1);
    request.add(delivery2);

    List<Bid> bids = truck.makeBids(request);
    assertEquals(4, bids.size());

    // first delivery arrives at 10, second at 60
    for (Bid bid : bids) {
      System.out.println(bid);
      if (bid.getBidSet().size() == 2) {
        assertEquals(32, (long) bid.getBidSet().get(delivery1));
        assertEquals(52, (long) bid.getBidSet().get(delivery2));
      }
    }

  }


  @Test
  public void testBlockerInBetween() {
    Truck truck = new Truck(1, 10, 10);
    LinkedList<Delivery> request = new LinkedList<>();
    truck.blockSlot(20, 30);
    Delivery delivery1 = new Delivery(1, 20);
    Delivery delivery2 = new Delivery(2, 30);

    request.add(delivery1);
    request.add(delivery2);

    List<Bid> bids = truck.makeBids(request);
    assertEquals(4, bids.size());

    for (Bid bid : bids) {
      System.out.println(bid);
      if (bid.getBidSet().size() == 2) {
        assertEquals(10, (long) bid.getBidSet().get(delivery1));
        assertEquals(40, (long) bid.getBidSet().get(delivery2));
      }
    }

  }

}
