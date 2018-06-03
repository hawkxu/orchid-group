/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.JocValueField;

/**
 * Value picker provider, JocValueField use the provider to get a value picker.
 * A default value picker provider can be set in UI defaults with key
 * OrchidConstants.DEFAULT_PICKER_PROVIDER
 *
 * @author zqxu
 */
public interface ValuePickerProvider {

  /**
   *
   * @param field
   * @return
   */
  public ValuePicker findValuePicker(JocValueField field);
}