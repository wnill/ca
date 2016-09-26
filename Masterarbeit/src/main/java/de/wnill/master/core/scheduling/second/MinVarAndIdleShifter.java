package de.wnill.master.core.scheduling.second;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import de.wnill.master.simulator.types.Job;
import de.wnill.master.simulator.utils.DeliveryProposedTimeComparator;

public class MinVarAndIdleShifter implements SecondPassProcessor {

  private double weightOfVariance = 0.8;

  private int varianceLowerBound = 20;

  public MinVarAndIdleShifter() {}

  public MinVarAndIdleShifter(double varianceLowerBound) {
    this.varianceLowerBound = (int) varianceLowerBound;
  }


  @Override
  public Set<Bid> updateBids(Set<Bid> originalBids) {

    // First, determine which truck begins first - its schedule will remain fixed
    Set<Bid> bids = new HashSet<>(originalBids);
    LocalTime firstDelivery = LocalTime.MAX;
    Bid firstStarterBid = null;
    int totalDeliveries = 0;

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

    if (firstStarterBid == null) {
      return originalBids;
    }

    // Save a list of all fixed times
    HashMap<Integer, Long> fixedTimes = new HashMap<>();
    for (Delivery delivery : firstStarterBid.getDeliveries()) {
      fixedTimes.put(delivery.getId(), Duration.between(firstDelivery, delivery.getProposedTime())
          .toMinutes());
    }

    Collections.sort(deliveries, new DeliveryProposedTimeComparator());
    HashMap<String, Long> offsets = solveIlp(bids, totalDeliveries, deliveries, fixedTimes);

    if (offsets.isEmpty()) {
      return originalBids;
    }

    // Adjust delivery times
    LocalTime startTime = deliveries.getFirst().getProposedTime();
    for (int i = 0; i < deliveries.size(); i++) {
      deliveries.get(i).setProposedTime(startTime.plus(Duration.ofMinutes(offsets.get("d" + i))));
    }

    // adjust breaks
    for (Bid bid : bids) {
      for (Job job : bid.getUnproductiveJobs()) {
        job.setScheduledStart(startTime.plus(
            Duration.ofMinutes(offsets.get("b" + job.getId() + bid.getId()))).minus(
            job.getDuration()));
      }
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
  HashMap<String, Long> solveIlp(Set<Bid> bids, int totalDeliveries,
      LinkedList<Delivery> deliveries, HashMap<Integer, Long> fixedTimes) {
    Problem problem = new Problem();
    Linear linear = new Linear();
    // Define objective function
    int deliveriesToConsider = totalDeliveries - 1;
    // Minimize completion time variance
    for (int i = 1; i <= deliveriesToConsider; i++) {
      linear.add(weightOfVariance, "C" + i);
      problem.setVarType("C" + i, Double.class);
    }

    // Minimize idle times
    // for (Bid bid : bids) {
    // linear.add(3.9, "d" + (deliveries.indexOf(bid.getDeliveries().get(0))));
    // }
    linear.add(1 - weightOfVariance, "d5");
    linear.add(-(1 - weightOfVariance), "d1");
    linear.add(1 - weightOfVariance, "d4");
    linear.add(-(1 - weightOfVariance), "d0");
    problem.setObjective(linear, OptType.MIN);

    linear = new Linear();
    for (int i = 1; i <= totalDeliveries; i++) {
      linear.add(1, "C" + i);
    }
    problem.add(linear, ">=", varianceLowerBound);



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
      // if (fixedTimes.containsKey(delivery.getId())) {
      // linear = new Linear();
      // linear.add(1, "d" + deliveryCounter);
      // problem.add(linear, "=", fixedTimes.get(delivery.getId()));
      // }

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
          problem.add(linear, ">=", duration);
        }

        // Add breaks
        for (Job job : bid.getUnproductiveJobs()) {

          // also, sequence must remain the same -> find out preceeding and/or succeeding delivery
          int firstSuccessor = -1;
          int lastPredecessor = -1;
          for (int i = 0; i < bid.getDeliveries().size(); i++) {
            if (bid.getDeliveries().get(i).getProposedTime().isBefore(job.getScheduledEnd())) {
              lastPredecessor = i;
            }
          }

          for (int i = bid.getDeliveries().size() - 1; i >= 0; i--) {
            if (bid.getDeliveries().get(i).getProposedTime().isAfter(job.getScheduledEnd())) {
              firstSuccessor = i;
            }
          }

          // Now add duration constraints of job
          if (lastPredecessor > -1) {
            linear = new Linear();
            linear.add(1, "b" + job.getId() + bid.getId());
            linear.add(-1, "d" + (deliveries.indexOf(bid.getDeliveries().get(lastPredecessor))));
            problem.add(linear, ">=", job.getDuration().toMinutes());
          }

          if (firstSuccessor > -1) {
            linear = new Linear();
            long duration =
                Duration.between(bid.getDeliveries().get(firstSuccessor).getStartTime(),
                    bid.getDeliveries().get(firstSuccessor).getProposedTime()).toMinutes();
            linear.add(1, "d" + (deliveries.indexOf(bid.getDeliveries().get(firstSuccessor))));
            linear.add(-1, "b" + job.getId() + bid.getId());
            problem.add(linear, ">=", duration);
          }
        }
      }
    }

    SolverFactory factory = new SolverFactoryLpSolve();
    factory.setParameter(Solver.VERBOSE, 0);
    factory.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds

    Solver solver = factory.get();
    Result result = solver.solve(problem);

    HashMap<String, Long> offsets = new HashMap<>();
    if (result != null) {

      offsets.put("d0", 0L);
      for (int i = 1; i <= deliveriesToConsider; i++) {
        offsets.put("d" + i, (long) Math.round((double) result.getPrimalValue("d" + i)));
      }

      for (Bid bid : bids) {
        for (Job job : bid.getUnproductiveJobs()) {
          offsets.put("b" + job.getId() + bid.getId(),
              (long) Math.round((double) result.getPrimalValue("b" + job.getId() + bid.getId())));
        }
      }
    }

    return offsets;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(varianceLowerBound);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(weightOfVariance);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MinVarAndIdleShifter other = (MinVarAndIdleShifter) obj;
    if (Double.doubleToLongBits(varianceLowerBound) != Double
        .doubleToLongBits(other.varianceLowerBound))
      return false;
    if (Double.doubleToLongBits(weightOfVariance) != Double
        .doubleToLongBits(other.weightOfVariance))
      return false;
    return true;
  }
}
