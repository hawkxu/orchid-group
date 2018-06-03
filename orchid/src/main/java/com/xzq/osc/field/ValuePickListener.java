/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import java.util.EventListener;

/**
 *
 * @author zqxu
 */
public interface ValuePickListener extends EventListener {

  /**
   * pickup value for JocValueField component, use evt.getSource() to get the
   * component which need pick up value. the listener should call evt.consume()
   * after set value for the source field.
   *
   * @param evt pick value event object.
   */
  public void pickupValue(ValuePickEvent evt);
}
