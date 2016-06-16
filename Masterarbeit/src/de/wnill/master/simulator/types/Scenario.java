package de.wnill.master.simulator.types;

import java.time.Duration;
import java.time.LocalTime;

public class Scenario {


  private LocalTime startTime;

  private LocalTime endTime;

  private Duration loadingDuration;

  private Duration offloadingDuration;

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
