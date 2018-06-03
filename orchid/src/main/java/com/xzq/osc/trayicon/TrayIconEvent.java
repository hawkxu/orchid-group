/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.trayicon;

import com.xzq.osc.JocTrayIcon;
import java.util.EventObject;

/**
 *
 * @author zqxu
 */
public class TrayIconEvent extends EventObject {

  /**
   *
   * @param source
   */
  public TrayIconEvent(JocTrayIcon source) {
    super(source);
  }

  /**
   * 
   * @return
   */
  @Override
  public JocTrayIcon getSource() {
    return (JocTrayIcon) super.getSource();
  }
}
