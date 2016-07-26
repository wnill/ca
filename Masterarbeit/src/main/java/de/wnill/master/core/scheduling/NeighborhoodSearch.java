package de.wnill.master.core.scheduling;

import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.wnill.master.core.valuation.Valuator;
import de.wnill.master.simulator.types.Job;
import de.wnill.master.simulator.utils.JobDueDateComparator;

/**
 * Scheduling based on static priorization (by due date) as start sequence. Afterwards, breaks are
 * shifted earlier stepwise, to ensure they do not exceed their due dates.
 */
public class NeighborhoodSearch implements SchedulingAlgorithm {

  @Override
  public List<Job> scheduleJobs(List<Job> jobs, LocalTime earliestStart, LocalTime latestComplete,
      Valuator valuator) {

    // Start sequence is ordering by Earliest Due Date (EDD) first
    Collections.sort(jobs, new JobDueDateComparator());

    List<Job> bestSequence = determineSequence(jobs, valuator);

    NaiveSymetricPenalties alg = new NaiveSymetricPenalties();
    List<Job> result = alg.findOptimalJobTimes(bestSequence, earliestStart, latestComplete);

    return result;
  }

  private List<Job> determineSequence(List<Job> jobs, Valuator valuator) {
    long bestValuation = valuator.getValuation(jobs);
    List<Job> bestSequence = jobs;

    for (Job job : jobs) {
      // First, find a break
      if (job.getDelivery() == null) {
        // Second, create permutations of the sequence, where break is scheduled earlier and
        // evaluate
        int originalPosition = jobs.indexOf(job);
        int position = originalPosition - 1;

        while (position > 0) {
          LinkedList<Job> permutation = new LinkedList<>(jobs);
          Job theBreak = permutation.remove(originalPosition);
          permutation.add(position, theBreak);
          if (valuator.getValuation(permutation) < bestValuation) {
            bestValuation = valuator.getValuation(permutation);
            bestSequence = permutation;
          }
          position--;
        }
      }
    }
    return bestSequence;
  }
}
