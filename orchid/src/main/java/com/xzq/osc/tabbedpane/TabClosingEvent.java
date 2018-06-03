/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.tabbedpane;

import java.util.EventObject;

/**
 *
 * @author zqxu
 */
public class TabClosingEvent extends EventObject {

  private int closingTabIndex;

  /**
   *
   * @param source
   * @param closingTabIndex
   */
  public TabClosingEvent(Object source, int closingTabIndex) {
    super(source);
    this.closingTabIndex = closingTabIndex;
  }

  /**
   *
   * @return
   */
  public int getClosingTabIndex() {
    return closingTabIndex;
  }
}
