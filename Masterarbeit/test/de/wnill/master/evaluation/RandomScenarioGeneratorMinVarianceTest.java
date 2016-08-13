package de.wnill.master.evaluation;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import de.wnill.master.simulator.types.Delivery;
import de.wnill.master.simulator.types.Job;


public class RandomScenarioGeneratorMinVarianceTest {

  @Test
  public void testCalculateVariance() {
    List<Job> jobs1 = new LinkedList<Job>();

    Delivery d1 = new Delivery(1, LocalTime.of(8, 0));
    d1.setProposedTime(LocalTime.of(8, 55));
    Job job1 = new Job(d1, LocalTime.of(8, 0), Duration.ofMinutes(110));
    job1.setScheduledStart(LocalTime.of(7, 5));
    jobs1.add(job1);

    Delivery d3 = new Delivery(3, LocalTime.of(9, 4));
    d3.setProposedTime(LocalTime.of(10, 45));
    Job job3 = new Job(d3, LocalTime.of(9, 4), Duration.ofMinutes(110));
    job3.setScheduledStart(LocalTime.of(8, 55));
    jobs1.add(job3);

    List<Job> jobs2 = new LinkedList<Job>();

    Delivery d2 = new Delivery(2, LocalTime.of(8, 32));
    d2.setProposedTime(LocalTime.of(8, 0));
    Job job2 = new Job(d2, LocalTime.of(8, 32), Duration.ofMinutes(110));
    job2.setScheduledStart(LocalTime.of(6, 10));
    jobs2.add(job2);

    Delivery d4 = new Delivery(4, LocalTime.of(9, 36));
    d4.setProposedTime(LocalTime.of(9, 50));
    Job job4 = new Job(d4, LocalTime.of(9, 36), Duration.ofMinutes(110));
    job4.setScheduledStart(LocalTime.of(8, 0));
    jobs2.add(job4);

    List<List<Job>> allJobs = new LinkedList<>();
    allJobs.add(jobs1);
    allJobs.add(jobs2);

    RandomScenarioGeneratorMinVariance gen = new RandomScenarioGeneratorMinVariance();
    assertEquals(0, gen.calculateVariance(0.5, gen.getDeliveries(allJobs)));

  }
}
