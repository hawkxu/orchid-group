/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.JocValueField;
import javax.swing.Icon;

/**
 *
 * @author zqxu
 */
public interface ValuePicker {

  /**
   * Returns true if the value picker is currently picking value for a field.
   *
   * @return true of false.
   */
  public boolean isValuePicking();

  /**
   * Returns icon to show on the pick button in JocValueField. or null to use
   * field default pick button icon.
   *
   * @return icon.
   */
  public Icon getPickButtonIcon();

  /**
   * the JocValueField use this converter to convert between value and string.
   * the value picker should return null if no converter need.
   *
   * @return converter
   */
  public Converter<Object, String> getConverter();

  /**
   * Pickup value for the field.
   *
   * @param field the field need pickup value.
   * @param value current value of the field.
   */
  public void pickupValue(JocValueField field, Object value);
}