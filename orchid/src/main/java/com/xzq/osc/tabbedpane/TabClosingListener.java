/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.tabbedpane;

import java.util.EventListener;

/**
 *
 * @author zqxu
 */
public interface TabClosingListener extends EventListener {

  public void ClosingTab(TabClosingEvent evt);
}
