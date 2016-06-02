package de.unihohenheim.wi.master.core;

public class Delivery {

  private int id;

  private long requestedTime;

  public Delivery(int id, long time) {
    this.id = id;
    requestedTime = time;
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
  public long getRequestedTime() {
    return requestedTime;
  }

  /**
   * @param requestedTime the requestedTime to set
   */
  public void setRequestedTime(long requestedTime) {
    this.requestedTime = requestedTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Delivery [id=" + id + ", requestedTime=" + requestedTime + "]";
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
    result = prime * result + (int) (requestedTime ^ (requestedTime >>> 32));
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
    if (requestedTime != other.requestedTime)
      return false;
    return true;
  }



}
