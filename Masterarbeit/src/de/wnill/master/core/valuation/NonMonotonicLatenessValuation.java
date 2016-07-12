package de.wnill.master.core.valuation;

import java.time.Duration;
import java.util.Collection;

import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;

public class NonMonotonicLatenessValuation implements Valuator {

  private static final Duration ACCEPTABLE_EARLINESS_THRESHOLD_NONPROD_JOBS = Duration
      .ofMinutes(30);

  private static final long NONPROD_LATENESS_PENALTY_MULTIPLICATOR = 100;

  private static final long NONPROD_EARLINESS_PENALTY_MULTIPLICATOR = 100;


  /*
   * (non-Javadoc)
   * 
   * @see de.wnill.master.core.utils.Valuation#getValuation(java.util.Collection,
   * java.util.Collection)
   */
  @Override
  public long getValuation(Collection<Job> jobs, Collection<Delivery> deliveries) {
    long result = 0;
    for (Job job : jobs) {
      if (job.getDelivery() == null) {
        result += valuationOfNonProdDelay(Duration.between(job.getDue(), job.getScheduledEnd()));
      }
    }
    for (Delivery delivery : deliveries) {
      result +=
          valuationOfDeliveryDelay(Duration.between(delivery.getRequestedTime(),
              delivery.getProposedTime()));
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.wnill.master.core.utils.Valuation#getValuation(java.util.Collection)
   */
  @Override
  public long getValuation(Collection<Job> jobs) {
    long result = 0;
    for (Job job : jobs) {
      if (job.getDelivery() == null) {
        result += valuationOfNonProdDelay(Duration.between(job.getDue(), job.getScheduledEnd()));
      } else {
        result += valuationOfDeliveryDelay(Duration.between(job.getDue(), job.getScheduledEnd()));
      }
    }
    return result;
  }


  /**
   * Returns the deviation in minutes.
   * 
   * @param delta
   * @return
   */
  private long valuationOfDeliveryDelay(Duration delta) {
    return delta.abs().toMinutes();
  }

  /**
   * Punishes late or too early scheduling of non-productive activites.
   * 
   * @param delta
   * @return
   */
  private long valuationOfNonProdDelay(Duration delta) {

    long valuation = 0;

    // Penalty for breaks that are much too early
    if (delta.isNegative()
        && delta.abs().compareTo(ACCEPTABLE_EARLINESS_THRESHOLD_NONPROD_JOBS) > 0) {
      valuation = delta.abs().multipliedBy(NONPROD_EARLINESS_PENALTY_MULTIPLICATOR).toMinutes();
      // Penalty if breaks are a bit early
    } else if (delta.isNegative()) {
      valuation = delta.abs().toMinutes();
      // Penalty if breaks are too late
    } else if (!delta.isNegative()) {
      valuation = delta.multipliedBy(NONPROD_LATENESS_PENALTY_MULTIPLICATOR).toMinutes();
    }
    return valuation;
  }

}
