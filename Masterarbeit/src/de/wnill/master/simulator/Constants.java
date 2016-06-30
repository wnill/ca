package de.wnill.master.simulator;

public class Constants {

  /**
   * in truckloads. Means that if a truck undocks and there is no more waiting truck, paver can
   * continue operations as if there was still half a truck unloading until it stops.
   */
  private final double PAVER_BUFFER_SIZE = 0.5;

}
