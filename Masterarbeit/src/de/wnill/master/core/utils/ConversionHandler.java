package de.wnill.master.core.utils;

import java.time.Duration;

import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;

public class ConversionHandler {

  /**
   * 
   * @param delivery
   * @return
   */
  public static Job convertDeliveryToJob(Delivery delivery, Duration duration) {
    Job job = new Job(delivery, delivery.getRequestedTime(), duration);
    job.setScheduledStart(delivery.getProposedTime().minus(duration));
    job.setId("D" + delivery.getId());
    return job;
  }


}
