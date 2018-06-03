/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.JocValueField;
import java.util.EventObject;

/**
 *
 * @author zqxu
 */
public class ValuePickEvent extends EventObject {

  private Object currentValue;
  protected boolean consumed = false;

  /**
   *
   * @param field
   * @param currentValue
   */
  public ValuePickEvent(JocValueField field, Object currentValue) {
    super(field);
    this.currentValue = currentValue;
  }

  /**
   *
   * @return
   */
  @Override
  public JocValueField getSource() {
    return (JocValueField) source;
  }

  /**
   *
   * @return
   */
  public Object getCurrentValue() {
    return currentValue;
  }

  /**
   * Consumes this event so that it will not be processed in the default manner
   * by the source which originated it.
   */
  public void consume() {
    consumed = true;
  }

  /**
   * Returns whether or not this event has been consumed.
   *
   * @see #consume
   */
  public boolean isConsumed() {
    return consumed;
  }
}
