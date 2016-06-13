package de.unihohenheim.wi.master.core.scheduling;

import java.util.LinkedList;
import java.util.List;

import de.unihohenheim.wi.master.core.Job;

public class TotalWeightedTardiness {

  private List<Job> recursiveSequence(long startTime, List<Job> jobs) {

    if (jobs.isEmpty()) {
      return new LinkedList<Job>();
    }

    Job longest = findLongestJob(jobs);
    long lowestTardiness = Long.MAX_VALUE;
    List<Job> bestSequence = new LinkedList<Job>();

    for (int j = jobs.indexOf(longest); j < jobs.size(); j++) {
      List<Job> set1 = jobs.subList(0, j);
      set1.remove(longest);
      long t1 = startTime;
      List<Job> sequence1 = recursiveSequence(t1, set1);

      List<Job> sequence2 = new LinkedList<>();

      if (j < jobs.size()) {
        List<Job> set2 = jobs.subList(j + 1, jobs.size() - 1);

        long duration = 0;
        for (int i = 0; i < j; i++) {
          duration += jobs.get(i).getDuration();
        }
        long t2 = startTime + duration;
        sequence2 = recursiveSequence(t2, set2);
      }
      List<Job> fullSequence = sequence1;
      fullSequence.add(longest);
      fullSequence.addAll(sequence2);

      long tardiness = calculateTardiness(fullSequence, startTime);

      if (tardiness < lowestTardiness) {
        bestSequence = fullSequence;
        lowestTardiness = tardiness;
      }

    }
    return bestSequence;
  }

  private long calculateTardiness(List<Job> fullSequence, long startTime) {

    long tardiness = 0;


    // TODO Auto-generated method stub
    return 0;
  }

  private Job findLongestJob(List<Job> jobs) {
    long duration = 0;
    Job longest = jobs.get(0);
    for (Job job : jobs) {
      if (job.getDuration() > duration) {
        duration = job.getDuration();
        longest = job;
      }
    }
    return longest;
  }

}
