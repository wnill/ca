package de.wnill.master.core.scheduling.second;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

public class MultiObjectiveProblem extends AbstractProblem {

  public MultiObjectiveProblem() {
    super(13, 2, 20);
  }

  @Override
  public void evaluate(Solution solution) {
    double C1 = EncodingUtils.getReal(solution.getVariable(0));
    double C2 = EncodingUtils.getReal(solution.getVariable(1));
    double C3 = EncodingUtils.getReal(solution.getVariable(2));
    double C4 = EncodingUtils.getReal(solution.getVariable(3));
    double C5 = EncodingUtils.getReal(solution.getVariable(4));

    double d0 = EncodingUtils.getReal(solution.getVariable(5));
    double d1 = EncodingUtils.getReal(solution.getVariable(6));
    double d2 = EncodingUtils.getReal(solution.getVariable(7));
    double d3 = EncodingUtils.getReal(solution.getVariable(8));
    double d4 = EncodingUtils.getReal(solution.getVariable(9));
    double d5 = EncodingUtils.getReal(solution.getVariable(10));

    double bnull0 = EncodingUtils.getReal(solution.getVariable(11));
    double bnull1 = EncodingUtils.getReal(solution.getVariable(12));

    solution.setObjective(0, C1 + C2 + C3 + C4 + C5);
    solution.setObjective(1, (d5 - d3 - 65) + (d3 - d1 - 50) + (d4 - d2 - 65) + (d2 - d0 - 50));

    solution.setConstraint(0, d0 == 0 ? 0.0 : d0);

    double cons1 = d1 - d0 - (0.2 * d5) - C1;
    double cons2 = (-1 * d1) + d0 + (0.2 * d5) - 1 * C1;
    double cons3 = d2 - d1 - (0.2 * d5) - C2;
    double cons4 = -d2 + d1 + (0.2 * d5) - C2;
    double cons5 = d3 - d2 - (0.2 * d5) - C3;
    double cons6 = -d3 + d2 + (0.2 * d5) - C3;
    double cons7 = d4 - d3 - (0.2 * d5) - C4;
    double cons8 = -d4 + d3 + (0.2 * d5) - C4;
    double cons9 = d5 - d4 - (0.2 * d5) - C5;
    double cons10 = -d5 + d4 + (0.2 * d5) - C5;


    solution.setConstraint(1, cons1 <= 0 ? 0.0 : cons1);
    solution.setConstraint(2, cons2 <= 0 ? 0.0 : cons2);
    solution.setConstraint(3, cons3 <= 0 ? 0.0 : cons3);
    solution.setConstraint(4, cons4 <= 0 ? 0.0 : cons4);
    solution.setConstraint(5, cons5 <= 0 ? 0.0 : cons5);
    solution.setConstraint(6, cons6 <= 0 ? 0.0 : cons6);
    solution.setConstraint(7, cons7 <= 0 ? 0.0 : cons7);
    solution.setConstraint(8, cons8 <= 0 ? 0.0 : cons8);
    solution.setConstraint(9, cons9 <= 0 ? 0.0 : cons9);
    solution.setConstraint(10, cons10 <= 0 ? 0.0 : cons10);

    double cons11 = d2 - d0;
    double cons12 = d4 - d2;
    double cons13 = bnull1 - d2;
    double cons14 = d4 - bnull1;
    double cons15 = d3 - d1;
    double cons16 = d5 - d3;
    double cons17 = bnull0 - d3;
    double cons18 = d5 - bnull0;


    solution.setConstraint(11, cons11 >= 50 ? 0.0 : cons11);
    solution.setConstraint(12, cons12 >= 65 ? 0.0 : cons12);
    solution.setConstraint(13, cons13 >= 15 ? 0.0 : cons13);
    solution.setConstraint(14, cons14 >= 50 ? 0.0 : cons14);
    solution.setConstraint(15, cons15 >= 50 ? 0.0 : cons15);
    solution.setConstraint(16, cons16 >= 65 ? 0.0 : cons16);
    solution.setConstraint(17, cons17 >= 15 ? 0.0 : cons17);
    solution.setConstraint(18, cons18 >= 50 ? 0.0 : cons18);

    solution.setConstraint(19, d2 - d0 > 0 ? 0.0 : d2 - d0);

  }

  @Override
  public Solution newSolution() {
    Solution solution = new Solution(13, 2, 20);
    solution.setVariable(0, EncodingUtils.newReal(0.0, 40.0));
    solution.setVariable(1, EncodingUtils.newReal(0.0, 40.0));
    solution.setVariable(2, EncodingUtils.newReal(0.0, 40.0));
    solution.setVariable(3, EncodingUtils.newReal(0.0, 40.0));
    solution.setVariable(4, EncodingUtils.newReal(0.0, 40.0));
    solution.setVariable(5, EncodingUtils.newReal(0.0, 200.0));
    solution.setVariable(6, EncodingUtils.newReal(0.0, 200.0));
    solution.setVariable(7, EncodingUtils.newReal(0.0, 200.0));
    solution.setVariable(8, EncodingUtils.newReal(0.0, 200.0));
    solution.setVariable(9, EncodingUtils.newReal(0.0, 200.0));
    solution.setVariable(10, EncodingUtils.newReal(0.0, 200.0));
    solution.setVariable(11, EncodingUtils.newReal(0.0, 200.0));
    solution.setVariable(12, EncodingUtils.newReal(0.0, 200.0));

    return solution;
  }

  public MultiObjectiveProblem(int numberOfVariables, int numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives);
  }

}
