package de.wnill.master.simulator.types;

import java.time.LocalTime;

public abstract class Event {

  private LocalTime time;

  private Context context;

  public Event(Context context, LocalTime time) {
    this.context = context;
    this.time = time;
  }

  /**
   * @return the time
   */
  public LocalTime getTime() {
    return time;
  }

  /**
   * @param time the time to set
   */
  public void setTime(LocalTime time) {
    this.time = time;
  }

  /**
   * @return the context
   */
  public Context getContext() {
    return context;
  }

  /**
   * @param context the context to set
   */
  public void setContext(Context context) {
    this.context = context;
  }

  public abstract void execute();

}
