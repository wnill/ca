package de.wnill.master.core.scheduling;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import de.wnill.master.simulator.types.Job;
import de.wnill.master.simulator.utils.JobComparator;
import de.wnill.master.simulator.utils.ScheduleLogger;

/**
 * Algorithm as proposed by Garey, Tarjan and Wilfong (1988).
 *
 */
public class NaiveSymetricPenalties implements SchedulingAlgorithm {

  @Override
  public List<Job> scheduleJobs(List<Job> originalList, LocalTime earliestStart,
      LocalTime latestComplete) {

    long lowestLateness = Long.MAX_VALUE;
    List<Job> bestSchedule = Collections.emptyList();

    List<List<Job>> allPermutations = generatePermutations(originalList);

    for (List<Job> jobList : allPermutations) {
      if (!jobList.isEmpty()) {
        List<Job> schedule = findOptimalJobTimes(jobList);
        long lateness = calculateLatenessSum(schedule);
        if (lateness < lowestLateness) {
          bestSchedule = new LinkedList<Job>();
          for (Job job : schedule) {
            bestSchedule.add(job.clone());
          }
          lowestLateness = lateness;
        }
      }
    }

    Collections.sort(bestSchedule, new JobComparator());
    ScheduleLogger.logSchedule(bestSchedule, lowestLateness);

    return bestSchedule;
  }


  protected List<Job> findOptimalJobTimes(List<Job> jobList) {
    // block index
    int t = 0;
    // denotes the index of the first job in the block which is indicated by array index
    int[] first = new int[jobList.size()];
    first[0] = 0;
    // denotes the index of the last job in the block which is indicated by array index
    int[] last = new int[jobList.size()];
    last[0] = 0;
    // for array index j: number of jobs in Increase(j)
    int[] inc = new int[jobList.size()];
    inc[0] = 1;
    // for array index j: number of jobs in Decrease(j)
    int[] dec = new int[jobList.size()];
    dec[0] = 0;

    jobList.get(0).setScheduledStart(jobList.get(0).getTargetedStart());

    List<PriorityQueue<Entry>> heaps = new ArrayList<>();
    heaps.add(0, new PriorityQueue<Entry>(new EntryComparator()));

    for (int n = 0; n < jobList.size() - 1; n++) {

      if (jobList.get(n).getScheduledStart().plus(jobList.get(n).getDuration())
          .isBefore(jobList.get(n + 1).getTargetedStart())) {
        t++;
        first[t] = n + 1;
        last[t] = n + 1;
        inc[t] = 1;
        dec[t] = 0;
        jobList.get(n + 1).setScheduledStart(jobList.get(n + 1).getTargetedStart());
        heaps.add(t, new PriorityQueue<Entry>(new EntryComparator()));
      } else if (jobList.get(n).getScheduledStart().plus(jobList.get(n).getDuration())
          .equals(jobList.get(n + 1).getTargetedStart())) {
        last[t] = n + 1;
        inc[t]++;
        jobList.get(n + 1).setScheduledStart(
            jobList.get(n).getScheduledStart().plus(jobList.get(n).getDuration()));
      } else if (jobList.get(n).getScheduledStart().plus(jobList.get(n).getDuration())
          .isAfter(jobList.get(n + 1).getTargetedStart())) {
        last[t] = n + 1;
        dec[t]++;
        jobList.get(n + 1).setScheduledStart(
            jobList.get(n).getScheduledStart().plus(jobList.get(n).getDuration()));
        PriorityQueue<Entry> heap = heaps.get(t);

        Duration difference =
            Duration.between(jobList.get(n + 1).getTargetedStart(), jobList.get(n + 1)
                .getScheduledStart());

        heap.add(new Entry(difference, n + 1));
        if (dec[t] == inc[t]) {
          // shift
          Duration delta1 = Duration.from(heaps.get(t).peek().key);
          Duration delta2;


          if (t == 0) {
            delta2 = Duration.between(LocalTime.MIN, jobList.get(0).getScheduledStart());
          } else {
            Duration betweenFirstAndLast =
                Duration.between(jobList.get(last[t - 1]).getScheduledStart(), jobList
                    .get(first[t]).getScheduledStart());
            delta2 = betweenFirstAndLast.minus(jobList.get(last[n - 1]).getDuration());
          }

          Duration delta;
          if (delta1.compareTo(delta2) < 0) {
            delta = delta1;
          } else {
            delta = delta2;
          }


          for (Entry entry : heap) {
            entry.key = entry.key.minus(delta);
          }
          jobList.get(first[t]).setScheduledStart(
              jobList.get(first[t]).getScheduledStart().minus(delta));
          jobList.get(last[t]).setScheduledStart(
              jobList.get(last[t]).getScheduledStart().minus(delta));

          while (!heap.isEmpty() && heap.peek().key.isZero()) {
            heap.poll();
            dec[t]--;
            inc[t]++;
          }

          // ATTENTION: t > 0 contraint added by me!

          if (t > 0
              && jobList
                  .get(first[t])
                  .getScheduledStart()
                  .equals(
                      jobList.get(last[t - 1]).getScheduledStart()
                          .plus(jobList.get(last[t - 1]).getDuration()))) {
            heaps.get(t - 1).addAll(heap);
            heap.clear();
            last[t - 1] = last[t];
            inc[t - 1] += inc[t];
            dec[t - 1] += dec[t];
            t--;
          }

        }
      }
    }


    // now correct all starting times
    for (int i = 0; i < jobList.size(); i++) {
      boolean containedInFirst = false;

      for (int index = 0; index < first.length; index++) {
        if (first[index] == i) {
          jobList.get(i).setScheduledStart(jobList.get(first[index]).getScheduledStart());
          containedInFirst = true;
        }
      }

      if (!containedInFirst) {
        jobList.get(i).setScheduledStart(
            jobList.get(i - 1).getScheduledStart().plus(jobList.get(i - 1).getDuration()));
      }
    }


    return jobList;
  }

  /**
   * Lateness calculated in minutes
   * 
   * @param schedule
   * @return
   */
  public long calculateLatenessSum(List<Job> schedule) {
    long totalLateness = 0;

    for (Job job : schedule) {
      totalLateness +=
          Math.abs(Duration.between(job.getScheduledEnd(), job.getDelivery().getRequestedTime())
              .toMinutes());
    }

    return totalLateness;
  }

  /**
   * Creates all permutations of given list.
   * 
   * Not my code, source: http://stackoverflow.com/questions/10305153/generating-all-possible-
   * permutations-of-a-list-recursively
   * 
   * @param original list
   * @return list of all permutations
   */
  public List<List<Job>> generatePermutations(final List<Job> original) {
    if (original.isEmpty()) {
      List<List<Job>> result = new ArrayList<>();
      result.add(new ArrayList<Job>());
      return result;
    }
    Job firstElement = original.remove(0);
    List<List<Job>> returnValue = new ArrayList<>();
    List<List<Job>> permutations = generatePermutations(original);
    for (List<Job> smallerPermutated : permutations) {
      for (int index = 0; index <= smallerPermutated.size(); index++) {
        List<Job> temp = new ArrayList<>(smallerPermutated);
        temp.add(index, firstElement);
        returnValue.add(temp);
      }
    }
    return returnValue;
  }



  public class Entry {
    private Duration key;
    private int value;

    /**
     * 
     * @param key
     * @param value
     */
    public Entry(Duration key, Integer value) {
      this.key = key;
      this.value = value;
    }
  }

  public class EntryComparator implements Comparator<Entry> {

    @Override
    public int compare(Entry o1, Entry o2) {
      return (int) (o1.key.compareTo(o2.key));
    }

  }

}
