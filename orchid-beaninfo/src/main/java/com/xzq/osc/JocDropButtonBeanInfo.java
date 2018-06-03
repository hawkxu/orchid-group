/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocDropButtonBeanInfo extends OrchidBaseBeanInfo {

  public JocDropButtonBeanInfo() {
    super(JocDropButton.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "dropMenu", "defaultMenuItem");
  }
}
