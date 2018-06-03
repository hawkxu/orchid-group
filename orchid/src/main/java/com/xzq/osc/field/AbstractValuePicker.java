/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

/**
 *
 * @author zqxu
 */
public abstract class AbstractValuePicker implements ValuePicker {

  private boolean valuePicking;

  @Override
  public boolean isValuePicking() {
    return valuePicking;
  }

  /**
   * set value picking state.
   *
   * @param valuePicking true or false.
   */
  protected void setValuePicking(boolean valuePicking) {
    this.valuePicking = valuePicking;
  }

  /**
   * Default return null for no converter need.
   *
   * @return null
   */
  @Override
  public Converter<Object, String> getConverter() {
    return null;
  }
}
