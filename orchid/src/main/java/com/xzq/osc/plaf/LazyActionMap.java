/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.plaf;

import javax.swing.Action;
import javax.swing.plaf.ActionMapUIResource;

/**
 *
 * @author zqxu
 */
public class LazyActionMap extends ActionMapUIResource {

  /**
   * 
   */
  public LazyActionMap() {
  }

  /**
   *
   * @param action
   */
  public void put(Action action) {
    super.put(action.getValue(Action.NAME), action);
  }
}
