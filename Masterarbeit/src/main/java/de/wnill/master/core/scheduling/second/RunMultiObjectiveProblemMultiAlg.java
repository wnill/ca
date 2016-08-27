package de.wnill.master.core.scheduling.second;

import org.moeaframework.Analyzer;
import org.moeaframework.Executor;

public class RunMultiObjectiveProblemMultiAlg {

  public static void main(String[] args) {
    Executor executor =
        new Executor().withProblemClass(MultiObjectiveProblem.class).withMaxEvaluations(10000);

    String[] algorithms = {"NSGAII", "GDE3", "eMOEA"};

    Analyzer analyzer =
        new Analyzer().withProblemClass(MultiObjectiveProblem.class).includeHypervolume()
            .showStatisticalSignificance();

    // run each algorithm for 50 seeds
    for (String algorithm : algorithms) {
      analyzer.addAll(algorithm, executor.withAlgorithm(algorithm).runSeeds(50));
    }

    // print the results
    analyzer.printAnalysis();

  }

}
