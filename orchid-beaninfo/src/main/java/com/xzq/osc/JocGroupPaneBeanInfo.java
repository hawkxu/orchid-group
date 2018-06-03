/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocGroupPaneBeanInfo extends OrchidBaseBeanInfo {

  public JocGroupPaneBeanInfo() {
    super(JocGroupPane.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "icon", "text", "backgroundImage", "collapseIcon",
            "expandIcon", "flexible", "titleBarVisible", "expanded",
            "margin", "drawBorder", "expansionDirection", "paneGroup");
  }
}
