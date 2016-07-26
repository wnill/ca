package de.wnill.master.simulator.types;

import java.time.LocalTime;

public class Delivery {

  private int id;

  private LocalTime requestedTime;

  private LocalTime proposedTime;

  /**
   * 
   * @param id
   * @param reqTime the due date for this delivery
   */
  public Delivery(int id, LocalTime reqTime) {
    this.id = id;
    requestedTime = reqTime;
  }

  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(int id) {
    this.id = id;
  }



  /**
   * @return the requestedTime
   */
  public LocalTime getRequestedTime() {
    return requestedTime;
  }

  /**
   * @param requestedTime the requestedTime to set
   */
  public void setRequestedTime(LocalTime requestedTime) {
    this.requestedTime = requestedTime;
  }

  /**
   * @return the proposedTime
   */
  public LocalTime getProposedTime() {
    return proposedTime;
  }

  /**
   * @param proposedTime the proposedTime to set
   */
  public void setProposedTime(LocalTime proposedTime) {
    this.proposedTime = proposedTime;
  }

  /**
   * Returns a deep copy of this object.
   */
  public Delivery clone() {
    Delivery clone = new Delivery(this.getId(), this.getRequestedTime());
    clone.setProposedTime(this.proposedTime);
    return clone;
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
    result = prime * result + id;
    result = prime * result + ((proposedTime == null) ? 0 : proposedTime.hashCode());
    result = prime * result + ((requestedTime == null) ? 0 : requestedTime.hashCode());
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
    Delivery other = (Delivery) obj;
    if (id != other.id)
      return false;
    if (proposedTime == null) {
      if (other.proposedTime != null)
        return false;
    } else if (!proposedTime.equals(other.proposedTime))
      return false;
    if (requestedTime == null) {
      if (other.requestedTime != null)
        return false;
    } else if (!requestedTime.equals(other.requestedTime))
      return false;
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Delivery [id=" + id + ", requestedTime=" + requestedTime + ", proposedTime="
        + proposedTime + "]";
  }



}
