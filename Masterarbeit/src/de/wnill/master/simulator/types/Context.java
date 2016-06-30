package de.wnill.master.simulator.types;

import de.wnill.master.simulator.Paver;

public class Context {

  private final Paver paver;

  private Context(ContextBuilder builder) {
    this.paver = builder.paver;
  }

  public static class ContextBuilder {
    private Paver paver;

    public ContextBuilder paver(Paver paver) {
      this.paver = paver;
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

}
