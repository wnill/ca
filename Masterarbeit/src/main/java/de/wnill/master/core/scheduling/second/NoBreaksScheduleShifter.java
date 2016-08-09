package de.wnill.master.core.scheduling.second;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;
import de.wnill.master.simulator.types.Bid;
import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.utils.DeliveryProposedTimeComparator;

public class NoBreaksScheduleShifter implements SecondPassProcessor {

  @Override
  public Set<Bid> updateBids(Set<Bid> originalBids) {
    // First, determine which truck begins first - its schedule will remain fixed
    Set<Bid> bids = new HashSet<>(originalBids);
    LocalTime firstDelivery = LocalTime.MAX;
    Bid firstStarterBid = null;
    int totalDeliveries = 0;
    // DeliveryID -> duration
    LinkedList<Delivery> deliveries = new LinkedList<>();
    for (Bid bid : bids) {
      for (Delivery delivery : bid.getDeliveries()) {
        if (delivery.getProposedTime().isBefore(firstDelivery)) {
          firstDelivery = delivery.getProposedTime();
          firstStarterBid = bid;
        }
        totalDeliveries++;
        deliveries.add(delivery);
      }
    }

    // Save a list of all fixed times
    HashMap<Integer, Long> fixedTimes = new HashMap<>();
    for (Delivery delivery : firstStarterBid.getDeliveries()) {
      fixedTimes.put(delivery.getId(), Duration.between(firstDelivery, delivery.getProposedTime())
          .toMinutes());
    }

    Collections.sort(deliveries, new DeliveryProposedTimeComparator());
    List<Long> offsets = solveIlp(bids, totalDeliveries, deliveries, fixedTimes);

    // Adjust delivery times
    LocalTime startTime = deliveries.getFirst().getProposedTime();
    for (int i = 0; i < deliveries.size(); i++) {
      deliveries.get(i).setProposedTime(startTime.plus(Duration.ofMinutes(offsets.get(i))));
    }

    return bids;
  }

  /**
   * Converts a problem instance into a linear programming formulation, solves it and returns the
   * optimal delivery times relative to the first delivery.
   * 
   * @param bids
   * @param totalDeliveries
   * @param deliveries
   * @param fixedTimes
   * @return
   */
  ArrayList<Long> solveIlp(Set<Bid> bids, int totalDeliveries, LinkedList<Delivery> deliveries,
      HashMap<Integer, Long> fixedTimes) {
    Problem problem = new Problem();
    Linear linear = new Linear();
    // Define objective function
    int deliveriesToConsider = totalDeliveries - 1;
    for (int i = 1; i <= deliveriesToConsider; i++) {
      linear.add(1, "C" + i);
      problem.setVarType("C" + i, Double.class);
    }
    problem.setObjective(linear, OptType.MIN);

    // Add constraints
    linear = new Linear();
    linear.add(1, "d0");
    problem.add(linear, "=", 0);
    problem.setVarType("d0", Double.class);

    linear = new Linear();
    linear.add(1, "d1");
    problem.add(linear, ">=", 0);

    int deliveryCounter = 1;
    for (Delivery delivery : deliveries) {
      // First delivery is assumed to be fixed
      if (delivery == deliveries.getFirst()) {
        continue;
      }

      // goal is to minimize the absolute value of deviation between delivery time and equal
      // distribution
      linear = new Linear();
      linear.add(1, "d" + deliveryCounter);
      linear.add(-1, "d" + (deliveryCounter - 1));
      linear.add((double) -1 / deliveriesToConsider, "d" + deliveriesToConsider);
      linear.add(-1, "C" + deliveryCounter);
      problem.add(linear, "<=", 0);
      problem.setVarType("d" + deliveryCounter, Double.class);

      linear = new Linear();
      linear.add(-1, "d" + deliveryCounter);
      linear.add(1, "d" + (deliveryCounter - 1));
      linear.add((double) 1 / deliveriesToConsider, "d" + deliveriesToConsider);
      linear.add(-1, "C" + deliveryCounter);
      problem.add(linear, "<=", 0);

      // all deliveries of the truck which start first are assumed to be fixed
      if (fixedTimes.containsKey(delivery.getId())) {
        linear = new Linear();
        linear.add(1, "d" + deliveryCounter);
        problem.add(linear, "=", fixedTimes.get(delivery.getId()));
      }

      deliveryCounter++;
    }

    // add constraints to fix the duration of each delivery
    for (Bid bid : bids) {
      if (bid.getDeliveries().size() > 1) {
        for (int i = 1; i < bid.getDeliveries().size(); i++) {
          long duration =
              Duration.between(bid.getDeliveries().get(i - 1).getProposedTime(),
                  bid.getDeliveries().get(i).getProposedTime()).toMinutes();
          linear = new Linear();
          linear.add(1, "d" + (deliveries.indexOf(bid.getDeliveries().get(i))));
          linear.add(-1, "d" + (deliveries.indexOf(bid.getDeliveries().get(i - 1))));
          problem.add(linear, "=", duration);
        }
      } else if (bid.getDeliveries().size() == 1) {
        // if only one delivery is offered we can set it to desired optimal time
        linear = new Linear();
        linear.add(1, "d" + (deliveries.indexOf(bid.getDeliveries().getFirst())));
        problem.add(
            linear,
            "=",
            Duration.between(deliveries.getFirst().getProposedTime(),
                bid.getDeliveries().getFirst().getProposedTime()).toMinutes());
      }
    }


    SolverFactory factory = new SolverFactoryLpSolve();
    factory.setParameter(Solver.VERBOSE, 0);
    factory.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds

    Solver solver = factory.get();
    Result result = solver.solve(problem);

    ArrayList<Long> offsets = new ArrayList<>();
    offsets.add(0L);
    for (int i = 1; i <= deliveriesToConsider; i++) {
      offsets.add((long) Math.round((double) result.getPrimalValue("d" + i)));
    }

    return offsets;
  }
}
