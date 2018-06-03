/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.plaf.LookAndFeelManager;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.UIManager;

/**
 *
 * @author zqxu
 */
public class JocGregCalendarPane extends JComponent
        implements OrchidAboutIntf {

  private static final String uiClassID = "OrchidGregCalendarPaneUI";
  private Dimension preferredDayCellSize;
  private Color titleBackground;
  private Color weekdayNameBackground;
  private Color weekNumberBackground;
  private Color weekendForeground;
  private Color outMonthForeground;
  private Color selectionForeground;
  private Color selectionBackground;
  private Color rolloverBackground;
  private Color cellBorderColor;
  private int minimumYear;
  private int maximumYear;
  private Date selectedDate;
  private Date rolloverDate;

  /**
   * Constructor for JocGregCalendarPane.
   */
  public JocGregCalendarPane() {
    super();
    updateUI();
  }

  @Override
  public String getUIClassID() {
    return uiClassID;
  }

  @Override
  public void updateUI() {
    LookAndFeelManager.installOrchidUI();
    setUI(UIManager.getUI(this));
  }

  // <editor-fold defaultstate="collapsed" desc="display relative...">
  /**
   * Returns preferred day cell size.
   *
   * @return preferred day cel size.
   */
  public Dimension getPreferredDayCellSize() {
    return preferredDayCellSize;
  }

  /**
   * Sets preferred day cell size, component should caculate size from this
   * value.
   *
   * @param preferredDayCellSize preferred day cell size
   */
  public void setPreferredDayCellSize(Dimension preferredDayCellSize) {
    Dimension old = this.preferredDayCellSize;
    if (!OrchidUtils.equals(old, preferredDayCellSize)) {
      this.preferredDayCellSize = preferredDayCellSize;
      revalidate();
      firePropertyChange("preferredDayCellSize", old, preferredDayCellSize);
    }
  }

  /**
   * Return title bar backgroud color.
   *
   * @return title bar backgroud color.
   */
  public Color getTitleBackground() {
    return titleBackground;
  }

  /**
   * Sets title bar backgroud color.
   *
   * @param titleBackground title bar backgroud color.
   */
  public void setTitleBackground(Color titleBackground) {
    Color old = this.titleBackground;
    if (!OrchidUtils.equals(old, titleBackground)) {
      this.titleBackground = titleBackground;
      repaint();
      firePropertyChange("titleBackground", old, titleBackground);
    }
  }

  /**
   * Return week day name cell background color.
   *
   * @return week day name cell background color.
   */
  public Color getWeekdayNameBackground() {
    return weekdayNameBackground;
  }

  /**
   * Sets week day name cell background color
   *
   * @param weekdayNameBackground week day name cell background color.
   */
  public void setWeekdayNameBackground(Color weekdayNameBackground) {
    Color old = this.weekdayNameBackground;
    if (!OrchidUtils.equals(old, weekdayNameBackground)) {
      this.weekdayNameBackground = weekdayNameBackground;
      repaint();
      firePropertyChange("weekdayNameBackground", old, weekdayNameBackground);
    }
  }

  /**
   * Returns week number cell background color.
   *
   * @return week number cell background color.
   */
  public Color getWeekNumberBackground() {
    return weekNumberBackground;
  }

  /**
   * Sets week number cell background color.
   *
   * @param weekNumberBackground week number cell background color.
   */
  public void setWeekNumberBackground(Color weekNumberBackground) {
    Color old = this.weekNumberBackground;
    if (!OrchidUtils.equals(old, weekNumberBackground)) {
      this.weekNumberBackground = weekNumberBackground;
      repaint();
      firePropertyChange("weekNumberBackground", old, weekNumberBackground);
    }
  }

  /**
   * Returns foreground color for selection day cell.
   *
   * @return foreground color of selection day cell.
   */
  public Color getSelectionForeground() {
    return selectionForeground;
  }

  /**
   * Sets foreground color for selection day cell.
   *
   * @param selectionForeground foreground color for selection day cell.
   */
  public void setSelectionForeground(Color selectionForeground) {
    Color old = this.selectionForeground;
    if (!OrchidUtils.equals(old, selectionForeground)) {
      this.selectionForeground = selectionForeground;
      repaint();
      firePropertyChange("selectionForeground", old, selectionForeground);
    }
  }

  /**
   * Returns background color for selection day cell.
   *
   * @return background color for selection day cell.
   */
  public Color getSelectionBackground() {
    return selectionBackground;
  }

  /**
   * Sets background color for selection day cell.
   *
   * @param selectionBackground background color for selection day cell.
   */
  public void setSelectionBackground(Color selectionBackground) {
    Color old = this.selectionBackground;
    if (!OrchidUtils.equals(old, selectionBackground)) {
      this.selectionBackground = selectionBackground;
      repaint();
      firePropertyChange("selectionBackground", old, selectionBackground);
    }
  }

  /**
   * Returns foreground color for cell of day out of month
   *
   * @return foreground color for cell of day out of month
   */
  public Color getOutMonthForeground() {
    return outMonthForeground;
  }

  /**
   * Sets foreground color for cell of day out of month
   *
   * @param outMonthForeground foreground color for cell of day out of month
   */
  public void setOutMonthForeground(Color outMonthForeground) {
    Color old = this.outMonthForeground;
    if (!OrchidUtils.equals(old, outMonthForeground)) {
      this.outMonthForeground = outMonthForeground;
      repaint();
      firePropertyChange("outMonthForeground", old, outMonthForeground);
    }
  }

  /**
   * Returns background color for rolloverDate day cell.
   *
   * @return background color for rolloverDate day cell.
   */
  public Color getRolloverBackground() {
    return rolloverBackground;
  }

  /**
   * Sets background color for rolloverDate day cell.
   *
   * @param rolloverBackground background color for rolloverDate day cell.
   */
  public void setRolloverBackground(Color rolloverBackground) {
    Color old = this.rolloverBackground;
    if (!OrchidUtils.equals(old, rolloverBackground)) {
      this.rolloverBackground = rolloverBackground;
      repaint();
      firePropertyChange("rolloverBackground", old, rolloverBackground);
    }
  }

  /**
   * Returns foreground color for weekend day cell.
   *
   * @return foreground color for weekend day cell.
   */
  public Color getWeekendForeground() {
    return weekendForeground;
  }

  /**
   * Sets foreground color for weekend day cell.
   *
   * @param weekendForeground foreground color for weekend day cell.
   */
  public void setWeekendForeground(Color weekendForeground) {
    Color old = this.weekendForeground;
    if (!OrchidUtils.equals(old, weekendForeground)) {
      this.weekendForeground = weekendForeground;
      repaint();
      firePropertyChange("weekendForeground", old, weekendForeground);
    }
  }

  /**
   * Returns color for cell border.
   *
   * @return color for cell border.
   */
  public Color getCellBorderColor() {
    return cellBorderColor;
  }

  /**
   * Sets color for cell border.
   *
   * @param cellBorderColor color for cell border.
   */
  public void setCellBorderColor(Color cellBorderColor) {
    Color old = this.cellBorderColor;
    if (!OrchidUtils.equals(old, cellBorderColor)) {
      this.cellBorderColor = cellBorderColor;
      repaint();
      firePropertyChange("cellBorderColor", old, cellBorderColor);
    }
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Day Selection Relative...">
  /**
   * Returns allowed minimum year of calendar pane, defaults is 1901.
   *
   * @return minimum year of calendar pane.
   */
  public int getMinimumYear() {
    return minimumYear;
  }

  /**
   * Sets minimum year of calendar pane.
   *
   * @param minimumYear minimum year of calendar pane.
   */
  public void setMinimumYear(int minimumYear) {
    int old = this.minimumYear;
    if (old != minimumYear) {
      this.minimumYear = minimumYear;
      firePropertyChange("minimumYear", old, minimumYear);
    }
  }

  /**
   * Returns allowed maximum year of calendar pane, defaults is 2100.
   *
   * @return maximum year of calendar pane.
   */
  public int getMaximumYear() {
    return maximumYear;
  }

  /**
   * Sets allowed maximum year of calendar pane.
   *
   * @param maximumYear maximum year of calendar pane.
   */
  public void setMaximumYear(int maximumYear) {
    int old = this.maximumYear;
    if (old != maximumYear) {
      this.maximumYear = maximumYear;
      firePropertyChange("maximumYear", old, maximumYear);
    }
  }

  /**
   * Returns current rolloverDate date.
   *
   * @return current rolloverDate date.
   */
  public Date getRolloverDate() {
    return rolloverDate;
  }

  /**
   * Sets current rolloverDate date.
   *
   * @param rolloverDate rolloverDate date.
   * @exception IllegalArgumentException if rolloverDate parameter is null.
   */
  public void setRolloverDate(Date rolloverDate) {
    if (rolloverDate == null) {
      throw new IllegalArgumentException("Can not set null rollover date!");
    }
    int rolloverYear = OrchidUtils.getDateField(rolloverDate, Calendar.YEAR);
    if (rolloverYear < minimumYear || rolloverYear > maximumYear) {
      throw new IllegalArgumentException("rollover date out of range!");
    }
    Date old = this.rolloverDate;
    if (!OrchidUtils.equals(old, rolloverDate)) {
      this.rolloverDate = rolloverDate;
      firePropertyChange("rolloverDate", old, rolloverDate);
    }
  }

  /**
   * Returns selectedDate date.
   *
   * @return selectedDate date.
   */
  public Date getSelectedDate() {
    return selectedDate;
  }

  /**
   * Sets selectedDate date.
   *
   * @param selectedDate selectedDate date.
   */
  public void setSelectedDate(Date selectedDate) {
    Date old = this.selectedDate;
    if (!OrchidUtils.equals(old, selectedDate)) {
      this.selectedDate = selectedDate;
      firePropertyChange("selectedDate", old, selectedDate);
    }
  }
  // </editor-fold>

  /**
   * Returns about box dialog
   *
   * @return An about box dialog
   */
  @Override
  public JDialog getAboutBox() {
    return DefaultOrchidAbout.getDefaultAboutBox(getClass());
  }

  /**
   * internal use.
   *
   * @param aboutBox about dialog
   */
  public void setAboutBox(JDialog aboutBox) {
    // no contents need.
  }
}
