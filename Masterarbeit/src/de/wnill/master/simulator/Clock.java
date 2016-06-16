package de.wnill.master.simulator;

import java.time.LocalTime;

public class Clock {

  private LocalTime currentTime;

  public Clock(LocalTime initValue) {
    currentTime = initValue;
  }

  /**
   * @return the currentTime
   */
  public LocalTime getCurrentTime() {
    return currentTime;
  }

  /**
   * @param currentTime the currentTime to set
   */
  public void setCurrentTime(LocalTime currentTime) {
    this.currentTime = currentTime;
  }

}
