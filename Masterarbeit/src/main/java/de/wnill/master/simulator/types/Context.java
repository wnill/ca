package de.wnill.master.simulator.types;

import java.util.List;

import de.wnill.master.simulator.Paver;
import de.wnill.master.simulator.Truck;

public class Context {

  private final Paver paver;

  private final List<Truck> trucks;

  private Context(ContextBuilder builder) {
    this.paver = builder.paver;
    this.trucks = builder.trucks;
  }

  public static class ContextBuilder {
    private Paver paver;
    private List<Truck> trucks;

    public ContextBuilder paver(Paver paver) {
      this.paver = paver;
      return this;
    }

    public ContextBuilder trucks(List<Truck> trucks) {
      this.trucks = trucks;
      return this;
    }

    public Context build() {
      return new Context(this);
    }
  }


  /**
   * @return the paver
   */
  public Paver getPaver() {
    return paver;
  }


  public List<Truck> getTrucks() {
    return trucks;
  }

}
