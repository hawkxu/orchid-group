/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocTrayIconBeanInfo extends OrchidBaseBeanInfo {

  public JocTrayIconBeanInfo() {
    super(JocTrayIcon.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "actionCommand", "window", "icon", "popupMenu",
            "iconAutoSize", "autoInstallAtOpened", "autoInstallAtHidden",
            "autoRemoveAtShown", "autoHideMinWindow",
            "clickCountToShowWindow", "toolTipText");

  }
}
