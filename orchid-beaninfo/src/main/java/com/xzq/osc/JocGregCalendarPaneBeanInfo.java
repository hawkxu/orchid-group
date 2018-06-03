/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocGregCalendarPaneBeanInfo extends OrchidBaseBeanInfo {

  public JocGregCalendarPaneBeanInfo() {
    super(JocGregCalendarPane.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, new String[]{"rolloverDate", "selectedDate",
              "minimumYear", "maximumYear", "preferredDayCellSize",
              "cellBorderColor", "titleBackground", "outMonthForeground",
              "rolloverBackground", "selectionBackground",
              "selectionBackground", "weekNumberBackground",
              "weekdayNameBackground", "weekendForeground",});
  }
}
