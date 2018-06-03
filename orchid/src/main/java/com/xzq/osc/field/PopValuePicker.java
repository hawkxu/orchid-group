/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.JocValueField;
import com.xzq.osc.resource.Resource;
import javax.swing.Icon;

/**
 *
 * @author zqxu
 */
public abstract class PopValuePicker extends AbstractValuePicker {

  @Override
  public Icon getPickButtonIcon() {
    return Resource.getOrchidIcon("pickup.png");
  }

  /**
   * Call doPickupValue and set state of valuePicking.
   *
   * @param field the field
   * @param value current value of the field.
   */
  @Override
  public void pickupValue(JocValueField field, Object value) {
    setValuePicking(true);
    try {
      doPickupValue(field, value);
    } finally {
      setValuePicking(false);
    }
  }

  /**
   * pickup value and set the value to the field.
   *
   * @param field the field
   * @param value current value of the field.
   */
  protected abstract void doPickupValue(JocValueField field, Object value);
}