/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocTabbedPaneBeanInfo extends OrchidBaseBeanInfo {

  public JocTabbedPaneBeanInfo() {
    super(JocTabbedPane.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "borderColor", "selectedColor",
            "showListButton", "showCloseButton", "showCloseButtonOnTab");
  }
}
