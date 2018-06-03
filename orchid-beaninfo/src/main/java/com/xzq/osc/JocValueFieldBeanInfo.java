/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocValueFieldBeanInfo extends OrchidBaseBeanInfo {

  public JocValueFieldBeanInfo() {
    super(JocValueField.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "model", "value", "valueClass", "required",
            "inputMask", "charCase");
  }
}
