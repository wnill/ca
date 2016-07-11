package de.wnill.master.core.scheduling.viz;

import java.awt.Color;
import java.awt.GridLayout;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;

import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultTimeBarRenderer;
import de.wnill.master.simulator.Truck;
import de.wnill.master.simulator.types.Job;

public class ScheduleVisualizer {

  public static final List<DefaultRowHeader> headerList = new ArrayList<>();

  public ScheduleVisualizer(List<Truck> trucks) {
    JFrame f = new JFrame("Schedules");
    f.setSize(1200, 400);
    f.getContentPane().setLayout(new GridLayout(2, 1));
    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    TimeBarModel model = createModel(trucks);
    TimeBarViewer tbv = new TimeBarViewer(model);
    tbv.setPixelPerSecond(0.5);
    tbv.setTimeBarRenderer(new CustomTimeBarRenderer());

    f.getContentPane().add(tbv);
    f.getContentPane().add(new ControlPanel(tbv));

    f.setVisible(true);
  }



  private TimeBarModel createModel(List<Truck> trucks) {

    DefaultTimeBarModel model = new DefaultTimeBarModel();

    for (Truck truck : trucks) {
      DefaultRowHeader header = new DefaultRowHeader("T" + truck.getId());
      headerList.add(header);
      DefaultTimeBarRowModel row = new DefaultTimeBarRowModel(header);

      for (Job job : truck.getSchedule()) {
        JobInterval interval =
            new JobInterval(job.getScheduledStart(), job.getScheduledEnd(), Duration.between(
                job.getDue(), job.getScheduledEnd()), job.getId(), job.getDelivery() != null);
        row.addInterval(interval);
      }
      System.out.println("Created row: " + row.getIntervals());
      model.addRow(row);

    }

    return model;
  }

  public class CustomTimeBarRenderer extends DefaultTimeBarRenderer {

    // private final Color PRODUCTIVE_BG = new Color(47, 139, 179);
    private final Color PRODUCTIVE_BG = new Color(43, 77, 115);

    private final Color PRODUCTIVE_FG = Color.WHITE;

    // private final Color NOT_PRODUCTIVE_BG = new Color(82, 161, 195);
    private final Color NOT_PRODUCTIVE_BG = new Color(233, 103, 67);



    @Override
    public JComponent getTimeBarRendererComponent(TimeBarViewer tbv, Interval value,
        boolean isSelected, boolean overlapping) {

      JobInterval job = (JobInterval) value;

      _component.setText(value.toString());
      _component.setToolTipText(value.toString());
      if (job.isProductive()) {
        _component.setBackground(PRODUCTIVE_BG);
        _component.setForeground(PRODUCTIVE_FG);
      } else {
        _component.setBackground(NOT_PRODUCTIVE_BG);
      }
      _component.setBorder(BorderFactory.createLineBorder(Color.white, 1));
      return _component;
    }
  }

  public class JobInterval extends IntervalImpl {

    private String jobId;

    private Duration delay;

    private boolean productive;

    public JobInterval(LocalTime start, LocalTime end, Duration delay, String jobId,
        boolean productive) {
      this._begin = new JaretDate(1, 1, 2016, start.getHour(), start.getMinute(), 0);
      this._begin.addPropertyChangeListener(this);
      this._end = new JaretDate(1, 1, 2016, end.getHour(), end.getMinute(), 0);
      this._end.addPropertyChangeListener(this);
      this.jobId = jobId;
      this.delay = delay;
      this.productive = productive;
    }

    /**
     * @return the productive
     */
    public boolean isProductive() {
      return productive;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(jobId).append(": \n").append(_begin.getHours()).append(":")
          .append(_begin.getMinutes());
      if (_begin.getMinutes() == 0) {
        sb.append("0");
      }
      sb.append(" - ").append(_end.getHours()).append(":").append(_end.getMinutes());
      if (_end.getMinutes() == 0) {
        sb.append("0");
      }
      sb.append(" (");
      if (!delay.isNegative()) {
        sb.append("+");
      }
      sb.append(delay.toMinutes()).append("m)");

      return sb.toString();
    }
  }

}
