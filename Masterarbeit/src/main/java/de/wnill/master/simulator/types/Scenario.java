package de.wnill.master.simulator.types;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import de.wnill.master.core.scheduling.SchedulingAlgorithm;
import de.wnill.master.core.valuation.Valuator;
import de.wnill.master.core.wdp.WinnerDeterminationAlgorithm;

public class Scenario {

  // General

  /** the simulation start time */
  private LocalTime startTime;

  private LocalTime endTime;

  private OrderType orderType;

  // Truck related

  private int truckCount;

  private Duration roundtripTime;

  private Duration offloadingDuration;

  private SchedulingAlgorithm schedulingAlgorithm;

  private List<LocalTime> truckBreaksDue;

  private List<Duration> truckBreakDurations;


  // Order related
  /** the target time for the first delivery. */
  private LocalTime firstDockingTime;

  private Duration optimalDeliveryInterval;

  /** how many deliveries must be ordered at least at any given time */
  private int orderAheadMinimum;

  /** how many deliveries may be ordered at most at any given time */
  private int orderAheadMaximum;

  private Valuator valuator;

  private WinnerDeterminationAlgorithm winnerDeterminationAlgorithm;


  /**
   * @return the truckBreaks
   */
  public List<LocalTime> getTruckBreaks() {
    return truckBreaksDue;
  }

  /**
   * @param truckBreaks the truckBreaks to set
   */
  public void setTruckBreaksDue(List<LocalTime> truckBreaks) {
    this.truckBreaksDue = truckBreaks;
  }

  /**
   * @return the truckBreakDurations
   */
  public List<Duration> getTruckBreakDurations() {
    return truckBreakDurations;
  }

  /**
   * @param truckBreakDurations the truckBreakDurations to set
   */
  public void setTruckBreakDurations(List<Duration> truckBreakDurations) {
    this.truckBreakDurations = truckBreakDurations;
  }

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
   * @return the roundtripTime
   */
  public Duration getRoundtripTime() {
    return roundtripTime;
  }

  /**
   * @param roundtripTime the roundtripTime to set
   */
  public void setRoundtripTime(Duration roundtripTime) {
    this.roundtripTime = roundtripTime;
  }

  /**
   * @return the offloadingDuration
   */
  public Duration getOffloadingDuration() {
    return offloadingDuration;
  }

  /**
   * @return the optimalDeliveryInterval
   */
  public Duration getOptimalDeliveryInterval() {
    return optimalDeliveryInterval;
  }

  /**
   * @param optimalDeliveryInterval the optimalDeliveryInterval to set
   */
  public void setOptimalDeliveryInterval(Duration optimalDeliveryInterval) {
    this.optimalDeliveryInterval = optimalDeliveryInterval;
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
   * @return the winnerDeterminationAlgorithm
   */
  public WinnerDeterminationAlgorithm getWinnerDeterminationAlgorithm() {
    return winnerDeterminationAlgorithm;
  }

  /**
   * @param winnerDeterminationAlgorithm the winnerDeterminationAlgorithm to set
   */
  public void setWinnerDeterminationAlgorithm(
      WinnerDeterminationAlgorithm winnerDeterminationAlgorithm) {
    this.winnerDeterminationAlgorithm = winnerDeterminationAlgorithm;
  }

  /**
   * @return the valuator
   */
  public Valuator getValuator() {
    return valuator;
  }

  /**
   * @param valuator the valuator to set
   */
  public void setValuator(Valuator valuator) {
    this.valuator = valuator;
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
    result = prime * result + ((firstDockingTime == null) ? 0 : firstDockingTime.hashCode());
    result = prime * result + ((offloadingDuration == null) ? 0 : offloadingDuration.hashCode());
    result =
        prime * result
            + ((optimalDeliveryInterval == null) ? 0 : optimalDeliveryInterval.hashCode());
    result = prime * result + orderAheadMaximum;
    result = prime * result + orderAheadMinimum;
    result = prime * result + ((orderType == null) ? 0 : orderType.hashCode());
    result = prime * result + ((roundtripTime == null) ? 0 : roundtripTime.hashCode());
    result = prime * result + ((schedulingAlgorithm == null) ? 0 : schedulingAlgorithm.hashCode());
    result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
    result = prime * result + ((truckBreakDurations == null) ? 0 : truckBreakDurations.hashCode());
    result = prime * result + ((truckBreaksDue == null) ? 0 : truckBreaksDue.hashCode());
    result = prime * result + truckCount;
    result = prime * result + ((valuator == null) ? 0 : valuator.hashCode());
    result =
        prime
            * result
            + ((winnerDeterminationAlgorithm == null) ? 0 : winnerDeterminationAlgorithm.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Scenario [startTime=" + startTime + ", endTime=" + endTime + ", orderType=" + orderType
        + ", truckCount=" + truckCount + ", roundtripTime=" + roundtripTime
        + ", offloadingDuration=" + offloadingDuration + ", schedulingAlgorithm="
        + schedulingAlgorithm + ", truckBreaks=" + truckBreaksDue + ", truckBreakDurations="
        + truckBreakDurations + ", firstDockingTime=" + firstDockingTime
        + ", optimalDeliveryInterval=" + optimalDeliveryInterval + ", orderAheadMinimum="
        + orderAheadMinimum + ", orderAheadMaximum=" + orderAheadMaximum + ", valuator=" + valuator
        + ", winnerDeterminationAlgorithm=" + winnerDeterminationAlgorithm + "]";
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
    Scenario other = (Scenario) obj;
    if (endTime == null) {
      if (other.endTime != null)
        return false;
    } else if (!endTime.equals(other.endTime))
      return false;
    if (firstDockingTime == null) {
      if (other.firstDockingTime != null)
        return false;
    } else if (!firstDockingTime.equals(other.firstDockingTime))
      return false;
    if (offloadingDuration == null) {
      if (other.offloadingDuration != null)
        return false;
    } else if (!offloadingDuration.equals(other.offloadingDuration))
      return false;
    if (optimalDeliveryInterval == null) {
      if (other.optimalDeliveryInterval != null)
        return false;
    } else if (!optimalDeliveryInterval.equals(other.optimalDeliveryInterval))
      return false;
    if (orderAheadMaximum != other.orderAheadMaximum)
      return false;
    if (orderAheadMinimum != other.orderAheadMinimum)
      return false;
    if (orderType != other.orderType)
      return false;
    if (roundtripTime == null) {
      if (other.roundtripTime != null)
        return false;
    } else if (!roundtripTime.equals(other.roundtripTime))
      return false;
    if (schedulingAlgorithm == null) {
      if (other.schedulingAlgorithm != null)
        return false;
    } else if (!schedulingAlgorithm.equals(other.schedulingAlgorithm))
      return false;
    if (startTime == null) {
      if (other.startTime != null)
        return false;
    } else if (!startTime.equals(other.startTime))
      return false;
    if (truckBreakDurations == null) {
      if (other.truckBreakDurations != null)
        return false;
    } else if (!truckBreakDurations.equals(other.truckBreakDurations))
      return false;
    if (truckBreaksDue == null) {
      if (other.truckBreaksDue != null)
        return false;
    } else if (!truckBreaksDue.equals(other.truckBreaksDue))
      return false;
    if (truckCount != other.truckCount)
      return false;
    if (valuator == null) {
      if (other.valuator != null)
        return false;
    } else if (!valuator.equals(other.valuator))
      return false;
    if (winnerDeterminationAlgorithm == null) {
      if (other.winnerDeterminationAlgorithm != null)
        return false;
    } else if (!winnerDeterminationAlgorithm.equals(other.winnerDeterminationAlgorithm))
      return false;
    return true;
  }

}
