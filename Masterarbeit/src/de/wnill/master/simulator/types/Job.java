package de.wnill.master.simulator.types;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Models a job to be scheduled, which can be "productive" (a delivery) or "non-productive" (e.g.
 * wait times).
 *
 */
public class Job {

  private Duration duration;

  private LocalTime due;

  private LocalTime scheduledStart;

  /** reference to a delivery, if there is any. */
  private Delivery delivery;

  private String id;

  /**
   * Constructor for productive jobs.
   * 
   * @param id
   * @param due
   * @param duration
   */
  public Job(Delivery delivery, LocalTime due, Duration duration) {
    this.delivery = delivery;
    this.due = due;
    this.duration = duration;
  }

  /**
   * Constructor for non-productive jobs.
   * 
   * @param due
   * @param duration
   */
  public Job(LocalTime due, Duration duration) {
    this.due = due;
    this.duration = duration;
  }



  /**
   * @return the duration
   */
  public Duration getDuration() {
    return duration;
  }



  /**
   * @param duration the duration to set
   */
  public void setDuration(Duration duration) {
    this.duration = duration;
  }



  /**
   * @return the due
   */
  public LocalTime getDue() {
    return due;
  }



  /**
   * @param due the due to set
   */
  public void setDue(LocalTime due) {
    this.due = due;
  }



  /**
   * @return the scheduledStart
   */
  public LocalTime getScheduledStart() {
    return scheduledStart;
  }



  /**
   * @param scheduledStart the scheduledStart to set
   */
  public void setScheduledStart(LocalTime scheduledStart) {
    this.scheduledStart = scheduledStart;
  }

  /**
   * @return the scheduledEnd
   */
  public LocalTime getScheduledEnd() {
    return scheduledStart.plus(duration);
  }



  /**
   * The targeted time to start the job, so that the deviation from due time is 0.
   * 
   * @return
   */
  public LocalTime getTargetedStart() {
    return due.minus(duration);
  }


  /**
   * @return the delivery
   */
  public Delivery getDelivery() {
    return delivery;
  }

  /**
   * @param delivery the delivery to set
   */
  public void setDelivery(Delivery delivery) {
    this.delivery = delivery;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }


  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Job [duration=" + duration + ", due=" + due + ", scheduledStart=" + scheduledStart
        + ", delivery=" + delivery + ", id=" + id + "]";
  }

  public Job clone() {
    Job newJob = new Job(delivery, due, duration);
    newJob.setId(id);
    newJob.setScheduledStart(scheduledStart);
    return newJob;
  }

}
