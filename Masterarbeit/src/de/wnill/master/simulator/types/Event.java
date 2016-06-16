package de.wnill.master.simulator.types;

import java.time.LocalTime;

public abstract class Event {

  private LocalTime time;

  /**
   * @return the time
   */
  public LocalTime getTime() {
    return time;
  }

  public void execute() {}

}
