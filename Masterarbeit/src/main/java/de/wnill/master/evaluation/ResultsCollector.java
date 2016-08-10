package de.wnill.master.evaluation;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wnill.master.simulator.types.OrderType;

public class ResultsCollector {

  private static final Logger logger = LoggerFactory.getLogger(ResultsCollector.class);

  private ArrayList<Long> idleSeq = new ArrayList<>();

  private ArrayList<Long> idleBun = new ArrayList<>();

  private ArrayList<Double> varSeq = new ArrayList<>();

  private ArrayList<Double> varBun = new ArrayList<>();

  public void putResult(OrderType type, long idleTimes, double variance) {
    if (type.equals(OrderType.SEQUENTIAL)) {
      idleSeq.add(idleTimes);
      varSeq.add(variance);
    } else if (type.equals(OrderType.BUNDLE)) {
      idleBun.add(idleTimes);
      varBun.add(variance);
    }
  }

  public void printResults() {

    int idleBetter = 0;
    int idleWorse = 0;
    int varBetter = 0;
    int varWorse = 0;

    for (int i = 0; i < idleSeq.size(); i++) {
      if (idleBun.get(i) < idleSeq.get(i)) {
        idleBetter++;
      } else if (idleBun.get(i) > idleSeq.get(i)) {
        idleWorse++;
      }

      if (varBun.get(i) < varSeq.get(i)) {
        varBetter++;
      } else if (varBun.get(i) > varSeq.get(i)) {
        varWorse++;
      }
    }

    int totalRuns = varSeq.size();

    DecimalFormat df = new DecimalFormat("#.00");
    logger.info("#########################");
    logger.info("Variance better in " + df.format((double) varBetter / totalRuns * 100)
        + "%, worse in " + df.format((double) varWorse / totalRuns * 100) + "% of runs");
    logger.info("Idle times better in " + df.format((double) idleBetter / totalRuns * 100)
        + "%, worse in " + df.format((double) idleWorse / totalRuns * 100) + "% of runs");
    logger.info("#########################");
  }

}
