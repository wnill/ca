package de.unihohenheim.wi.master.core;

public class Job {

  private long duration;

  private long due;

  private long scheduledStart;

  private Delivery delivery;

  private String id;

  /**
   * 
   * @param id
   * @param due
   * @param duration
   */
  public Job(String id, long due, long duration) {
    this.id = id;
    this.due = due;
    this.duration = duration;
  }


  /**
   * @return the duration
   */
  public long getDuration() {
    return duration;
  }

  /**
   * @param duration the duration to set
   */
  public void setDuration(long duration) {
    this.duration = duration;
  }

  /**
   * @return the due
   */
  public long getDue() {
    return due;
  }

  /**
   * @param due the due to set
   */
  public void setDue(long due) {
    this.due = due;
  }

  public long getTargetedStart() {
    return due - duration;
  }

  /**
   * @return the scheduled
   */
  public long getScheduledStart() {
    return scheduledStart;
  }

  /**
   * @param scheduled the scheduled to set
   */
  public void setScheduledStart(long scheduled) {
    this.scheduledStart = scheduled;
  }

  public long getScheduledEnd() {
    return scheduledStart + duration;
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


}
