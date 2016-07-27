package de.wnill.master.core.scheduling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import de.wnill.master.core.scheduling.NeighborhoodSearch.Cluster;
import de.wnill.master.core.scheduling.viz.ScheduleVisualizer;
import de.wnill.master.core.valuation.NonMonotonicLatenessValuation;
import de.wnill.master.core.valuation.Valuator;
import de.wnill.master.simulator.Truck;
import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;

public class NeighborhoodSearchTest {

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


    NeighborhoodSearch alg = new NeighborhoodSearch();
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

  @SuppressWarnings("deprecation")
  @Test
  public void testClusterGeneration() {
    LinkedList<Job> jobs = new LinkedList<>();

    Job job = new Job(LocalTime.of(0, 12), Duration.ofMinutes(3));
    job.setId("1");
    jobs.add(job);

    job = new Job(LocalTime.of(0, 4), Duration.ofMinutes(2));
    job.setId("2");
    jobs.add(job);

    job = new Job(LocalTime.of(0, 26), Duration.ofMinutes(7));
    job.setId("3");
    jobs.add(job);

    job = new Job(LocalTime.of(0, 18), Duration.ofMinutes(3));
    job.setId("4");
    jobs.add(job);

    job = new Job(LocalTime.of(0, 16), Duration.ofMinutes(6));
    job.setId("5");
    jobs.add(job);

    job = new Job(LocalTime.of(0, 25), Duration.ofMinutes(2));
    job.setId("6");
    jobs.add(job);

    job = new Job(LocalTime.of(0, 30), Duration.ofMinutes(8));
    job.setId("7");
    jobs.add(job);

    List<Integer> alphas = Arrays.asList(10, 20, 18, 9, 10, 16, 11);
    List<Integer> betas = Arrays.asList(12, 25, 38, 12, 12, 18, 15);


    NeighborhoodSearch alg = new NeighborhoodSearch();
    List<Cluster> clusters = alg.initializeClusters(LocalTime.of(0, 0), jobs, alphas, betas);

    assertEquals(3, clusters.size());
    assertTrue(clusters.get(0).getJobs().get(0).getId().equals("1"));
    assertTrue(clusters.get(0).getJobs().get(0).getScheduledEnd().equals(LocalTime.of(0, 3)));
    assertTrue(clusters.get(0).getJobs().get(1).getId().equals("2"));
    assertTrue(clusters.get(0).getJobs().get(1).getScheduledEnd().equals(LocalTime.of(0, 5)));
    assertTrue(clusters.get(1).getJobs().get(0).getId().equals("3"));
    assertTrue(clusters.get(1).getJobs().get(0).getScheduledEnd().equals(LocalTime.of(0, 12)));
    assertTrue(clusters.get(1).getJobs().get(1).getId().equals("4"));
    assertTrue(clusters.get(1).getJobs().get(1).getScheduledEnd().equals(LocalTime.of(0, 15)));
    assertTrue(clusters.get(1).getJobs().get(2).getId().equals("5"));
    assertTrue(clusters.get(1).getJobs().get(2).getScheduledEnd().equals(LocalTime.of(0, 21)));
    assertTrue(clusters.get(2).getJobs().get(0).getId().equals("6"));
    assertTrue(clusters.get(2).getJobs().get(0).getScheduledEnd().equals(LocalTime.of(0, 23)));
    assertTrue(clusters.get(2).getJobs().get(1).getId().equals("7"));
    assertTrue(clusters.get(02).getJobs().get(1).getScheduledEnd().equals(LocalTime.of(0, 31)));

    assertEquals("1", clusters.get(0).getLastEarlyJob().getId());
    assertEquals("4", clusters.get(1).getLastEarlyJob().getId());
    assertEquals("6", clusters.get(2).getLastEarlyJob().getId());

    assertEquals(9, clusters.get(0).getE());
    assertEquals(3, clusters.get(1).getE());
    assertEquals(2, clusters.get(2).getE());

    assertEquals(-15, clusters.get(0).getDelta());
    assertEquals(15, clusters.get(1).getDelta());
    assertEquals(1, clusters.get(2).getDelta());
  }

  @Test
  public void testClusterProcessing() {
    LinkedList<Job> jobs = new LinkedList<>();

    Job job = new Job(LocalTime.of(0, 12), Duration.ofMinutes(3));
    job.setId("1");
    jobs.add(job);
    job = new Job(LocalTime.of(0, 4), Duration.ofMinutes(2));
    job.setId("2");
    jobs.add(job);
    job = new Job(LocalTime.of(0, 26), Duration.ofMinutes(7));
    job.setId("3");
    jobs.add(job);
    job = new Job(LocalTime.of(0, 18), Duration.ofMinutes(3));
    job.setId("4");
    jobs.add(job);
    job = new Job(LocalTime.of(0, 16), Duration.ofMinutes(6));
    job.setId("5");
    jobs.add(job);
    job = new Job(LocalTime.of(0, 25), Duration.ofMinutes(2));
    job.setId("6");
    jobs.add(job);
    job = new Job(LocalTime.of(0, 30), Duration.ofMinutes(8));
    job.setId("7");
    jobs.add(job);

    List<Integer> alphas = Arrays.asList(10, 20, 18, 9, 10, 16, 11);
    List<Integer> betas = Arrays.asList(12, 25, 38, 12, 12, 18, 15);
    NeighborhoodSearch alg = new NeighborhoodSearch();
    LinkedList<Cluster> clusters = alg.initializeClusters(LocalTime.of(0, 0), jobs, alphas, betas);

    alg.processNextCluster(clusters);

  }


  @Test
  public void testTimeJobs() {
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

    NeighborhoodSearch alg = new NeighborhoodSearch();
    List<Job> result =
        alg.scheduleJobs(jobList, LocalTime.of(0, 0), LocalTime.of(1, 0),
            new NonMonotonicLatenessValuation());


    Valuator val = new NonMonotonicLatenessValuation();

    Truck truck = new Truck(1, new NeighborhoodSearch(), new NonMonotonicLatenessValuation());
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
}
