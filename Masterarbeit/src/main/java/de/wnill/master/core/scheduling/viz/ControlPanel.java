package de.wnill.master.core.scheduling.viz;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jaret.util.ui.timebars.swing.TimeBarViewer;

public class ControlPanel extends JPanel {

  public ControlPanel(TimeBarViewer viewer) {
    setLayout(new FlowLayout());
    createControls(viewer);
  }


  private void createControls(TimeBarViewer _viewer) {
    JLabel label = new JLabel("Zoom ");
    add(label);
    final JSlider timeScaleSlider = new JSlider(1, 10);
    timeScaleSlider.setValue((int) (_viewer.getPixelPerSecond() * 10));
    timeScaleSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        double pixPerSecond = (double) timeScaleSlider.getValue() / 10;
        _viewer.setPixelPerSecond(pixPerSecond);
      }
    });
    add(timeScaleSlider);
    /**
     * label = new JLabel("rowHeight"); add(label);
     * 
     * final JSlider rowHeigthSlider = new JSlider(10, 300);
     * rowHeigthSlider.setValue(_viewer.getRowHeight()); rowHeigthSlider.addChangeListener(new
     * ChangeListener() { public void stateChanged(ChangeEvent e) {
     * _viewer.setRowHeight(rowHeigthSlider.getValue()); } }); add(rowHeigthSlider);
     * 
     * final JCheckBox gapCheck = new JCheckBox("GapRenderer");
     * gapCheck.setSelected(_viewer.getGapRenderer() != null); gapCheck.addActionListener(new
     * ActionListener() {
     * 
     * @Override public void actionPerformed(ActionEvent e) { if (gapCheck.isSelected()) {
     *           _viewer.setGapRenderer(new DefaultGapRenderer()); } else {
     *           _viewer.setGapRenderer(null); } } }); add(gapCheck);
     * 
     *           final JCheckBox optScrollingCheck = new JCheckBox("Optimize scrolling");
     *           optScrollingCheck.setSelected(_viewer.getOptimizeScrolling());
     *           optScrollingCheck.addActionListener(new ActionListener() { public void
     *           actionPerformed(ActionEvent e) {
     *           _viewer.setOptimizeScrolling(optScrollingCheck.isSelected()); } });
     *           add(optScrollingCheck);
     * 
     *           label = new JLabel("time scale position"); add(label); final JComboBox
     *           timeScalePosCombo = new JComboBox(); timeScalePosCombo.addItem("top");
     *           timeScalePosCombo.addItem("bottom"); timeScalePosCombo.addItem("none");
     *           timeScalePosCombo.addActionListener(new ActionListener() {
     * 
     *           public void actionPerformed(ActionEvent e) { if
     *           (timeScalePosCombo.getSelectedItem().equals("top")) {
     *           _viewer.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_TOP); } else
     *           if (timeScalePosCombo.getSelectedItem().equals("bottom")) {
     *           _viewer.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_BOTTOM); }
     *           else if (timeScalePosCombo.getSelectedItem().equals("none")) {
     *           _viewer.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_NONE); } }
     * 
     *           }); add(timeScalePosCombo);
     * 
     *           label = new JLabel("orientation"); add(label); final JComboBox orientationCombo =
     *           new JComboBox(); orientationCombo.addItem("horizontal");
     *           orientationCombo.addItem("vertical"); orientationCombo.addActionListener(new
     *           ActionListener() {
     * 
     *           public void actionPerformed(ActionEvent e) { if
     *           (orientationCombo.getSelectedItem().equals("horizontal")) {
     *           _viewer.setTBOrientation(TimeBarViewerInterface.Orientation.HORIZONTAL); } else if
     *           (orientationCombo.getSelectedItem().equals("vertical")) {
     *           _viewer.setTBOrientation(TimeBarViewerInterface.Orientation.VERTICAL); } } });
     *           add(orientationCombo);
     * 
     *           final JCheckBox boxTSRCheck = new JCheckBox("BoxTimeScaleRenderer");
     *           boxTSRCheck.setSelected(_viewer.getTimeScaleRenderer() instanceof
     *           BoxTimeScaleRenderer); boxTSRCheck.addActionListener(new ActionListener() { public
     *           void actionPerformed(ActionEvent e) {
     *           _viewer.setTimeScaleRenderer(boxTSRCheck.isSelected() ? new BoxTimeScaleRenderer()
     *           : new DefaultTimeScaleRenderer()); } }); add(boxTSRCheck);
     * 
     *           final JCheckBox boxVRHCheck = new JCheckBox("Variable row height + dragging");
     *           boxVRHCheck.setSelected(_viewer.getTimeBarViewState().getUseVariableRowHeights());
     *           boxVRHCheck.addActionListener(new ActionListener() { public void
     *           actionPerformed(ActionEvent e) {
     *           _viewer.getTimeBarViewState().setUseVariableRowHeights(boxVRHCheck.isSelected());
     *           _viewer.setRowHeightDraggingAllowed(boxVRHCheck.isSelected()); } });
     *           add(boxVRHCheck);
     */
  }

}
