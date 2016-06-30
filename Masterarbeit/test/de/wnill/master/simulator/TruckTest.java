package de.wnill.master.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import de.wnill.master.core.Bid;
import de.wnill.master.simulator.Truck;
import de.wnill.master.simulator.types.Delivery;


public class TruckTest {


  @Test
  public void testScheduleWithinValidRange() {
    Truck truck = new Truck(1, 20);
    LinkedList<Delivery> request = new LinkedList<>();
    // truck.blockSlot(20, 50);
    request.add(new Delivery(1, 10));
    request.add(new Delivery(2, 30));

    truck.addBlocker(20, 60);
    request.add(new Delivery(3, 50));
    request.add(new Delivery(4, 70));
    List<Bid> bids = truck.makeBidsForAllDeliveries(request, 0, 60);

    for (Bid bid : bids) {
      Map<Delivery, Long> map = bid.getBidSet();

      for (Entry<Delivery, Long> entry : map.entrySet()) {
        assertTrue(entry.getKey().getProposedTime() - 20 >= 0);
        assertTrue(entry.getKey().getProposedTime() <= 60);
      }

    }
  }

  @Test
  public void testValidBidValue() {
    Truck truck = new Truck(1, 20);
    LinkedList<Delivery> request = new LinkedList<>();
    // truck.blockSlot(20, 50);
    request.add(new Delivery(1, 10));
    request.add(new Delivery(2, 30));

    truck.addBlocker(20, 60);
    request.add(new Delivery(3, 50));
    request.add(new Delivery(4, 70));
    List<Bid> bids = truck.makeBidsForAllDeliveries(request, 0, 100);

    for (Bid bid : bids) {
      Map<Delivery, Long> map = bid.getBidSet();

      for (Entry<Delivery, Long> entry : map.entrySet()) {
        assertEquals((long) entry.getValue(), entry.getKey().getProposedTime()
            - entry.getKey().getRequestedTime());
      }

    }
  }
}
