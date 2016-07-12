package de.wnill.master.core.scheduling;

import java.time.Duration;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import de.wnill.master.core.valuation.NonMonotonicLatenessValuation;
import de.wnill.master.core.valuation.Valuator;
import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;

public class NaiveSymetricPenaltiesTest {

  @Test
  public void testFindOptimalJobTimes() {
    LinkedList<Job> jobList = new LinkedList<>();
    jobList.add(new Job(new Delivery(2, LocalTime.of(0, 12)), LocalTime.of(0, 12), Duration
        .ofMinutes(10)));

    jobList.add(new Job(new Delivery(1, LocalTime.of(0, 10)), LocalTime.of(0, 10), Duration
        .ofMinutes(5)));

    jobList.add(new Job(new Delivery(3, LocalTime.of(0, 20)), LocalTime.of(0, 20), Duration
        .ofMinutes(3)));
    jobList.add(new Job(new Delivery(4, LocalTime.of(0, 23)), LocalTime.of(0, 23), Duration
        .ofMinutes(3)));

    NaiveSymetricPenalties alg = new NaiveSymetricPenalties();
    List<Job> result = alg.findOptimalJobTimes(jobList, LocalTime.of(0, 0), LocalTime.of(1, 0));

    System.out.println(result);
    Valuator val = new NonMonotonicLatenessValuation();
    System.out.println(val.getValuation(result));
  }

  @Test
  public void testScheduleJobs() {
    LinkedList<Job> jobList = new LinkedList<>();
    jobList.add(new Job(new Delivery(1, LocalTime.of(0, 10)), LocalTime.of(0, 10), Duration
        .ofMinutes(5)));
    jobList.add(new Job(new Delivery(2, LocalTime.of(0, 12)), LocalTime.of(0, 12), Duration
        .ofMinutes(10)));
    jobList.add(new Job(new Delivery(3, LocalTime.of(0, 20)), LocalTime.of(0, 20), Duration
        .ofMinutes(5)));
    jobList.add(new Job(new Delivery(4, LocalTime.of(0, 23)), LocalTime.of(0, 23), Duration
        .ofMinutes(5)));

    NaiveSymetricPenalties alg = new NaiveSymetricPenalties();
    List<Job> result =
        alg.scheduleJobs(jobList, LocalTime.of(0, 0), LocalTime.of(0, 50),
            new NonMonotonicLatenessValuation());

    System.out.println("Best schedule: " + result);
  }

}
