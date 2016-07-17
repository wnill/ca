package de.wnill.master.simulator.events;

import java.time.LocalTime;

import de.wnill.master.simulator.types.Context;
import de.wnill.master.simulator.types.Event;

/**
 * Requests the paver to place a delivery order. Used to startup the simulation and for follow-up
 * orders.
 *
 */
public class StartOrders extends Event {

  public StartOrders(Context context, LocalTime time) {
    super(context, time);
  }

  @Override
  public void execute() {

    if (getContext().getPaver() == null || getContext().getTrucks() == null) {
      throw new IllegalStateException("No paver or no trucks defined");
    }

    getContext().getPaver().placeOrder(getContext().getTrucks());
  }

}
