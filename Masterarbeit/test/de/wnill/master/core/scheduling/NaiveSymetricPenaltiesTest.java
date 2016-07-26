package de.wnill.master.core.scheduling;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import de.wnill.master.core.scheduling.viz.ScheduleVisualizer;
import de.wnill.master.core.valuation.NonMonotonicLatenessValuation;
import de.wnill.master.core.valuation.Valuator;
import de.wnill.master.simulator.Truck;
import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;

public class NaiveSymetricPenaltiesTest {

  @Test
  public void testFindOptimalJobTimes() {
    LinkedList<Job> jobList = new LinkedList<>();
    Job job1 =
        new Job(new Delivery(2, LocalTime.of(0, 12)), LocalTime.of(0, 12), Duration.ofMinutes(10));
    job1.setId("1");

    Job job2 =
        new Job(new Delivery(1, LocalTime.of(0, 10)), LocalTime.of(0, 10), Duration.ofMinutes(5));
    job2.setId("2");

    Job job3 =
        new Job(new Delivery(3, LocalTime.of(0, 20)), LocalTime.of(0, 20), Duration.ofMinutes(3));
    job3.setId("3");

    Job job4 =
        new Job(new Delivery(4, LocalTime.of(0, 23)), LocalTime.of(0, 23), Duration.ofMinutes(3));
    job4.setId("4");


    jobList.add(job1);
    jobList.add(job2);
    jobList.add(job3);
    jobList.add(job4);

    jobList.add(new Job(LocalTime.of(0, 17), Duration.ofMinutes(5)));


    NaiveSymetricPenalties alg = new NaiveSymetricPenalties();
    List<Job> result =
        alg.scheduleJobs(jobList, LocalTime.of(0, 0), LocalTime.of(1, 0),
            new NonMonotonicLatenessValuation());

    System.out.println(result);
    Valuator val = new NonMonotonicLatenessValuation();

    Truck truck = new Truck(1, new NaiveSymetricPenalties(), new NonMonotonicLatenessValuation());
    truck.setSchedule(result);
    ScheduleVisualizer viz = new ScheduleVisualizer(Arrays.asList(truck));

    System.out.println(val.getValuation(result));

    try {
      Thread.sleep(500000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }


  @Test
  public void testManualOptimalJobTimes() {
    LinkedList<Job> jobList = new LinkedList<>();
    Job job1 =
        new Job(new Delivery(2, LocalTime.of(0, 12)), LocalTime.of(0, 12), Duration.ofMinutes(10));
    job1.setId("1");
    job1.setScheduledStart(LocalTime.of(0, 0));

    Job job2 =
        new Job(new Delivery(1, LocalTime.of(0, 10)), LocalTime.of(0, 10), Duration.ofMinutes(5));
    job2.setId("2");
    job2.setScheduledStart(LocalTime.of(0, 15));

    Job job3 =
        new Job(new Delivery(3, LocalTime.of(0, 20)), LocalTime.of(0, 20), Duration.ofMinutes(3));
    job3.setId("3");
    job3.setScheduledStart(LocalTime.of(0, 20));

    Job job4 =
        new Job(new Delivery(4, LocalTime.of(0, 23)), LocalTime.of(0, 23), Duration.ofMinutes(3));
    job4.setId("4");
    job4.setScheduledStart(LocalTime.of(0, 23));


    jobList.add(job1);
    jobList.add(job2);
    jobList.add(job3);
    jobList.add(job4);

    Job pause = new Job(LocalTime.of(0, 17), Duration.ofMinutes(5));
    pause.setScheduledStart(LocalTime.of(0, 10));

    jobList.add(pause);


    NaiveSymetricPenalties alg = new NaiveSymetricPenalties();
    // List<Job> result = alg.findOptimalJobTimes(jobList, LocalTime.of(0, 0), LocalTime.of(1, 0));

    System.out.println(jobList);
    Valuator val = new NonMonotonicLatenessValuation();

    Truck truck = new Truck(1, new NaiveSymetricPenalties(), new NonMonotonicLatenessValuation());
    truck.setSchedule(jobList);
    ScheduleVisualizer viz = new ScheduleVisualizer(Arrays.asList(truck));

    System.out.println(val.getValuation(jobList));

    try {
      Thread.sleep(500000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

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
