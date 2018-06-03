/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.trayicon;

import java.util.EventListener;

/**
 *
 * @author zqxu
 */
public interface TrayIconListener extends EventListener {

  /**
   *
   * @param evt
   */
  public void trayIconInstalled(TrayIconEvent evt);

  /**
   * 
   * @param evt
   */
  public void trayIconUninstalled(TrayIconEvent evt);
}
