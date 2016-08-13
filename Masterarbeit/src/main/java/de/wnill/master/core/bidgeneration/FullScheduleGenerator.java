package de.wnill.master.core.bidgeneration;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import de.wnill.master.simulator.Constraints;
import de.wnill.master.simulator.Truck;
import de.wnill.master.simulator.types.Bid;
import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;

public class FullScheduleGenerator implements BidGenerator {

  @Override
  public List<Bid> generateBids(Truck truck, LocalTime startTime, LocalTime endTime) {


    // First job is always a delivery
    List<Job> existingSchedule = truck.getSchedule();
    LocalTime earliestStart = startTime;
    if (!existingSchedule.isEmpty()
        && existingSchedule.get(existingSchedule.size() - 1).getScheduledEnd()
            .isAfter(earliestStart)) {
      earliestStart = existingSchedule.get(existingSchedule.size() - 1).getScheduledEnd();
    }

    List<Bid> bids = new LinkedList<>();


    int offset = 1;

    while (true) {

      LocalTime breakDue = earliestStart.plus(Constraints.getTruckPauseAfter());
      if (!truck.getLastBreak().equals(LocalTime.MIN)) {
        breakDue = truck.getLastBreak().plus(Constraints.getTruckPauseAfter());
      }

      // Create one bid
      LinkedList<Delivery> deliveries = new LinkedList<>();
      List<Job> unprodJob = new LinkedList<>();
      LocalTime nextStart = earliestStart;

      if (nextStart.plus(Constraints.getTruckPauseDuration())
          .plus((truck.getRoundtripTime().multipliedBy(offset))).isAfter(breakDue)) {
        break;
      }
      int counter = 1;

      // first job is always a delivery
      while (nextStart.isBefore(endTime)) {

        if (nextStart.plus(Constraints.getTruckPauseDuration())
            .plus((truck.getRoundtripTime().multipliedBy(offset))).isAfter(breakDue)) {
          unprodJob.add(new Job(nextStart, breakDue, Constraints.getTruckPauseDuration()));
          nextStart = nextStart.plus(Constraints.getTruckPauseDuration());
          breakDue = nextStart.plus(Constraints.getTruckPauseAfter());
        } else {
          deliveries.add(new Delivery(truck.getId() * 10 + counter, nextStart, nextStart.plus(truck
              .getRoundtripTime())));
          nextStart = nextStart.plus(truck.getRoundtripTime());
          counter++;
        }
      }
      bids.add(new Bid(deliveries, unprodJob, truck, 0));
      offset++;
    }


    return bids;
  }
}
