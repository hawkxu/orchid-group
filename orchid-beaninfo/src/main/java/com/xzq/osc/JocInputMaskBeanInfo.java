/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocInputMaskBeanInfo extends OrchidBaseBeanInfo {

  public JocInputMaskBeanInfo() {
    super(JocInputMask.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, new String[]{"mask", "autoFixedChar",
              "charCase", "overwriteMode", "textComponent",});
  }
}
