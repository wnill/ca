package de.wnill.master.simulator.types;

import java.time.Duration;
import java.time.LocalTime;

public class Scenario {

  /** the simulation start time */
  private LocalTime startTime;

  /** the target time for the first delivery. */
  private LocalTime firstDockingTime;

  private LocalTime endTime;

  private Duration loadingDuration;

  private Duration offloadingDuration;

  private OrderType orderType;

  /** in meters */
  private long distanceToPave;

  /** required amount of asphalt per meter - models parameters such as screed width */
  private double truckloadPerMeter;

  /**
   * @return the firstDockingTime
   */
  public LocalTime getFirstDockingTime() {
    return firstDockingTime;
  }

  /**
   * @param firstDockingTime the firstDockingTime to set
   */
  public void setFirstDockingTime(LocalTime firstDockingTime) {
    this.firstDockingTime = firstDockingTime;
  }

  /**
   * @return the loadingDuration
   */
  public Duration getLoadingDuration() {
    return loadingDuration;
  }

  /**
   * @param loadingDuration the loadingDuration to set
   */
  public void setLoadingDuration(Duration loadingDuration) {
    this.loadingDuration = loadingDuration;
  }

  /**
   * @return the offloadingDuration
   */
  public Duration getOffloadingDuration() {
    return offloadingDuration;
  }

  /**
   * @param offloadingDuration the offloadingDuration to set
   */
  public void setOffloadingDuration(Duration offloadingDuration) {
    this.offloadingDuration = offloadingDuration;
  }

  /**
   * @return the orderType
   */
  public OrderType getOrderType() {
    return orderType;
  }

  /**
   * @param orderType the orderType to set
   */
  public void setOrderType(OrderType orderType) {
    this.orderType = orderType;
  }

  /**
   * @return the distanceToPave
   */
  public long getDistanceToPave() {
    return distanceToPave;
  }

  /**
   * @param distanceToPave the distanceToPave to set
   */
  public void setDistanceToPave(long distanceToPave) {
    this.distanceToPave = distanceToPave;
  }

  /**
   * @return the truckloadPerMeter
   */
  public double getTruckloadPerMeter() {
    return truckloadPerMeter;
  }

  /**
   * @param truckloadPerMeter the truckloadPerMeter to set
   */
  public void setTruckloadPerMeter(double truckloadPerMeter) {
    this.truckloadPerMeter = truckloadPerMeter;
  }

  /**
   * @return the startTime
   */
  public LocalTime getStartTime() {
    return startTime;
  }

  /**
   * @param startTime the startTime to set
   */
  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
  }

  /**
   * @return the endTime
   */
  public LocalTime getEndTime() {
    return endTime;
  }

  /**
   * @param endTime the endTime to set
   */
  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }

}
