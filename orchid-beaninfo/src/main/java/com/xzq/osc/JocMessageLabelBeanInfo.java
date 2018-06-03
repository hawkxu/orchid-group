/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocMessageLabelBeanInfo extends OrchidBaseBeanInfo {

  public JocMessageLabelBeanInfo() {
    super(JocMessageLabel.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, new String[]{
              "busyIcon", "errorIcon", "idleIcon", "okIcon", "timeout",
              "warningIcon"});
  }
}
