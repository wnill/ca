package de.wnill.master.simulator.types;

import java.time.Duration;
import java.time.LocalTime;

import de.wnill.master.core.scheduling.SchedulingAlgorithm;

public class Scenario {

  // General

  /** the simulation start time */
  private LocalTime startTime;

  private LocalTime endTime;

  private OrderType orderType;

  // Truck related

  private int truckCount;

  private Duration loadingDuration;

  private Duration offloadingDuration;

  private SchedulingAlgorithm schedulingAlgorithm;


  // Paver related
  /** the target time for the first delivery. */
  private LocalTime firstDockingTime;

  /** how many deliveries must be ordered at least at any given time */
  private int orderAheadMinimum;

  /** how many deliveries may be ordered at most at any given time */
  private int orderAheadMaximum;

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
   * @return the truckCount
   */
  public int getTruckCount() {
    return truckCount;
  }

  /**
   * @param truckCount the truckCount to set
   */
  public void setTruckCount(int truckCount) {
    this.truckCount = truckCount;
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
   * @return the orderAheadMinimum
   */
  public int getOrderAheadMinimum() {
    return orderAheadMinimum;
  }

  /**
   * @param orderAheadMinimum the orderAheadMinimum to set
   */
  public void setOrderAheadMinimum(int orderAheadMinimum) {
    this.orderAheadMinimum = orderAheadMinimum;
  }

  /**
   * @return the orderAheadMaximum
   */
  public int getOrderAheadMaximum() {
    return orderAheadMaximum;
  }

  /**
   * @param orderAheadMaximum the orderAheadMaximum to set
   */
  public void setOrderAheadMaximum(int orderAheadMaximum) {
    this.orderAheadMaximum = orderAheadMaximum;
  }


  /**
   * @return the schedulingAlgorithm
   */
  public SchedulingAlgorithm getSchedulingAlgorithm() {
    return schedulingAlgorithm;
  }

  /**
   * @param schedulingAlgorithm the schedulingAlgorithm to set
   */
  public void setSchedulingAlgorithm(SchedulingAlgorithm schedulingAlgorithm) {
    this.schedulingAlgorithm = schedulingAlgorithm;
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
