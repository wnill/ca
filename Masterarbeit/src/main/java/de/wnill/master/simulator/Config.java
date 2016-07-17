package de.wnill.master.simulator;

public class Config {

  private static boolean enableVisualisation = true;

  /**
   * @return the enableVisualisation
   */
  public static boolean isEnableVisualisation() {
    return enableVisualisation;
  }

  /**
   * @param enableVisualisation the enableVisualisation to set
   */
  public static void setEnableVisualisation(boolean enableVisualisation) {
    Config.enableVisualisation = enableVisualisation;
  }

}
