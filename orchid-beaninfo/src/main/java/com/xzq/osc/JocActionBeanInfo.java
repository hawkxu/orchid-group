/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocActionBeanInfo extends OrchidBaseBeanInfo {

  public JocActionBeanInfo() {
    super(JocAction.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "name", "smallIcon", "actionCommand",
            "mnemonicKey", "selected", "enabled");
  }
}
