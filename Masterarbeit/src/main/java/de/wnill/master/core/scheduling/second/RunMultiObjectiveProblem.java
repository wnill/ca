package de.wnill.master.core.scheduling.second;

import org.moeaframework.Executor;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

public class RunMultiObjectiveProblem {

  public static void main(String[] args) {
    NondominatedPopulation result =
        new Executor().withAlgorithm("GDE3").withProblemClass(MultiObjectiveProblem.class)
            .withMaxEvaluations(50000).run();

    for (Solution solution : result) {
      if (!solution.violatesConstraints()) {

        System.out.printf("d0 %.5f, d1 %.5f, d2 %.5f, d3 %.5f, d4 %.5f, d5 %.5f => %.5f, %.5f\n",
            EncodingUtils.getReal(solution.getVariable(5)),
            EncodingUtils.getReal(solution.getVariable(6)),
            EncodingUtils.getReal(solution.getVariable(7)),
            EncodingUtils.getReal(solution.getVariable(8)),
            EncodingUtils.getReal(solution.getVariable(9)),
            EncodingUtils.getReal(solution.getVariable(10)), solution.getObjective(0),
            solution.getObjective(1));
      }
    }

    new Plot().add("NSGAII", result).show();

  }

}
