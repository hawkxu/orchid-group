/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocBusyIconBeanInfo extends OrchidBaseBeanInfo {

  public JocBusyIconBeanInfo() {
    super(JocBusyIcon.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, new String[]{"busy", "delay", "points", "direction",
              "baseColor", "highlightColor", "trailLength"});
  }
}
