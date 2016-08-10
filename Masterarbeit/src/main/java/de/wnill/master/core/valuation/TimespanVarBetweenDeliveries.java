package de.wnill.master.core.valuation;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;

import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;

public class TimespanVarBetweenDeliveries implements Valuator {

  private final Duration ACCEPTABLE_EARLINESS_THRESHOLD_NONPROD_JOBS = Duration.ofMinutes(30);

  @Override
  public long getValuation(Collection<Job> jobs, Collection<Delivery> deliveries) {
    // TODO Auto-generated method stub
    return -1;
  }

  @Override
  public long getValuation(Collection<Job> jobs) {

    long result = 0;
    LinkedList<Job> productive = new LinkedList<Job>();
    LinkedList<Job> nonProd = new LinkedList<Job>();
    for (Job job : jobs) {
      if (job.getDelivery() == null) {
        nonProd.add(job);
      } else {
        productive.add(job);
      }
    }
    result += valuationOfNonProdDelay(nonProd);
    if (result < Long.MAX_VALUE) {
      result += valuationOfDeliveryDelay(productive);
    }
    return result;


  }

  private long valuationOfDeliveryDelay(Collection<Job> jobs) {
    // Default valuation by valuation for sequential cases
    if (jobs.size() == 1) {
      Job job = jobs.iterator().next();
      return Duration.between(job.getScheduledEnd(), job.getDue()).abs().toMinutes();
    }

    LinkedList<Job> deliveries = new LinkedList<>(jobs);

    // Second, calculate average time between deliveries
    double average = 0;
    for (int i = 0; i < deliveries.size() - 1; i++) {
      average +=
          Duration.between(deliveries.get(i).getScheduledEnd(),
              deliveries.get(i + 1).getScheduledEnd()).toMinutes();
    }
    average = average / (deliveries.size() - 1);

    // Third, calculate variance
    double variance = 0;
    for (int i = 0; i < deliveries.size() - 1; i++) {
      variance +=
          Math.pow(
              (Duration.between(deliveries.get(i).getScheduledEnd(),
                  deliveries.get(i + 1).getScheduledEnd()).toMinutes() - average), 2);
    }

    return Math.round(variance);
  }

  private long valuationOfNonProdDelay(Collection<Job> jobs) {
    long valuation = 0;

    for (Job job : jobs) {
      if (job.getScheduledEnd().isAfter(job.getDue())) {
        return Long.MAX_VALUE;
      } else if (job.getScheduledEnd().plus(ACCEPTABLE_EARLINESS_THRESHOLD_NONPROD_JOBS)
          .isBefore(job.getDue())) {
        valuation +=
            Math.pow(
                Duration.between(job.getScheduledEnd().plus(Duration.ofMinutes(30)), job.getDue())
                    .toMinutes(), 2);
      }
    }
    return valuation;
  }
}
