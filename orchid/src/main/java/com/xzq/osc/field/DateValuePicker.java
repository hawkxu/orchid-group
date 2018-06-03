/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.JocGregCalendarPane;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 *
 * @author zqxu
 */
public class DateValuePicker extends DropValuePicker {

  private JocGregCalendarPane calendar;

  public DateValuePicker() {
    calendar = new JocGregCalendarPane();
    calendar.addPropertyChangeListener(
            "selectedDate", new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        commitValuePicker(calendar.getSelectedDate());
      }
    });
  }

  @Override
  public Component preparePickComponent(Object value) {
    calendar.setSelectedDate(null);
    if (value instanceof Date) {
      calendar.setSelectedDate((Date) value);
    }
    return calendar;
  }
}
