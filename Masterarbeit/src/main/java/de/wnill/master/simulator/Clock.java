package de.wnill.master.simulator;

import java.time.LocalTime;

public class Clock {

  private LocalTime currentTime;

  private static Clock instance;

  private Clock() {}

  public static Clock getInstance() {
    if (instance == null) {
      instance = new Clock();
    }
    return instance;
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
