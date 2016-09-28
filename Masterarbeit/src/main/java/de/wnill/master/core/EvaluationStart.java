package de.wnill.master.core;

import de.wnill.master.evaluation.StructuredDominantScenarioGenerator;

public class EvaluationStart {

  public static void main(String[] args) {

    StructuredDominantScenarioGenerator gen = new StructuredDominantScenarioGenerator();

    if (args.length > 0) {
      gen.setTRUCK_MIN(Integer.valueOf(args[0]));
      gen.setTRUCK_MAX(Integer.valueOf(args[0]));
      gen.setTRUCK_DELTA(2);

      gen.setDELIVERIES_MIN(Integer.valueOf(args[1]));
      gen.setDELIVERIES_MAX(Integer.valueOf(args[2]));
      gen.setDELIVERIES_DELTA(Integer.valueOf(args[3]));
    }


    gen.enumerateScenarios();
  }

}
