package de.wnill.master.core.scheduling;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import de.wnill.master.core.Delivery;
import de.wnill.master.core.Job;

public class NaiveSymetricPenaltiesTest {

  @Test
  public void testFindOptimalJobTimes() {
    LinkedList<Job> jobList = new LinkedList<>();
    jobList.add(new Job(new Delivery(1, 10), 10, 5));
    jobList.add(new Job(new Delivery(1, 12), 12, 5));
    jobList.add(new Job(new Delivery(1, 20), 20, 5));
    jobList.add(new Job(new Delivery(1, 23), 23, 5));

    NaiveSymetricPenalties alg = new NaiveSymetricPenalties();
    List<Job> result = alg.findOptimalJobTimes(jobList);

    System.out.println(result);
  }

  @Test
  public void testScheduleJobs() {
    LinkedList<Job> jobList = new LinkedList<>();
    jobList.add(new Job(new Delivery(1, 10), 10, 5));
    jobList.add(new Job(new Delivery(1, 12), 12, 10));
    jobList.add(new Job(new Delivery(1, 20), 20, 5));
    jobList.add(new Job(new Delivery(1, 23), 23, 5));

    NaiveSymetricPenalties alg = new NaiveSymetricPenalties();
    List<Job> result = alg.scheduleJobs(jobList, 0, 50);

    System.out.println("Best schedule: " + result);
  }

}
