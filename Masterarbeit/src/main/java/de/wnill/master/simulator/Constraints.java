package de.wnill.master.simulator;

import java.time.Duration;


public class Constraints {

  private static Duration truckPauseAfter;

  private static Duration truckPauseDuration;

  /**
   * @return the truckPauseAfter
   */
  public static Duration getTruckPauseAfter() {
    return truckPauseAfter;
  }

  /**
   * @param truckPauseAfter the truckPauseAfter to set
   */
  public static void setTruckPauseAfter(Duration truckPauseAfter) {
    Constraints.truckPauseAfter = truckPauseAfter;
  }

  /**
   * @return the truckPauseDuration
   */
  public static Duration getTruckPauseDuration() {
    return truckPauseDuration;
  }

  /**
   * @param truckPauseDuration the truckPauseDuration to set
   */
  public static void setTruckPauseDuration(Duration truckPauseDuration) {
    Constraints.truckPauseDuration = truckPauseDuration;
  }

}
