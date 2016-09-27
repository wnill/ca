package de.wnill.master.core;

import de.wnill.master.evaluation.StructuredDominantScenarioGenerator;

public class EvaluationStart {

  public static void main(String[] args) {

    StructuredDominantScenarioGenerator gen = new StructuredDominantScenarioGenerator();
    gen.enumerateScenarios();
  }

}
