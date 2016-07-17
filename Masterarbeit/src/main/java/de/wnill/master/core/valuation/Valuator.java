package de.wnill.master.core.valuation;

import java.util.Collection;

import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;



public interface Valuator {

  /**
   * Gives a valuation of the proposed schedule (consisting of deliveries and non-productive jobs)
   * against the requested schedule.
   * 
   * @param jobs
   * @param deliveries
   * @return
   */
  public abstract long getValuation(Collection<Job> jobs, Collection<Delivery> deliveries);

  /**
   * Gives a valuation of the proposed schedule (consisting of deliveries and non-productive jobs)
   * against the requested schedule.
   * 
   * @param jobs
   * @return
   */
  public abstract long getValuation(Collection<Job> jobs);

}
