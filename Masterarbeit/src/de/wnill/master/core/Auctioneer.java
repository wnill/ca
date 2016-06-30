package de.wnill.master.core;

import java.util.LinkedList;
import java.util.List;

import de.wnill.master.simulator.Truck;
import de.wnill.master.simulator.types.Delivery;


public class Auctioneer {

  public static void main(String[] args) {
    Truck truck = new Truck(1, 20);
    LinkedList<Delivery> request = new LinkedList<>();
    // truck.blockSlot(20, 50);
    request.add(new Delivery(1, 10));
    request.add(new Delivery(2, 30));

    truck.addBlocker(20, 60);
    request.add(new Delivery(3, 50));
    request.add(new Delivery(4, 70));
    List<Bid> bids = truck.makeBidsForAllDeliveries(request, 0, 100);
    System.out.println("Truck offers " + bids.size() + " bids: ");
    for (Bid bid : bids) {
      System.out.println(bid);
    }

  }

}
