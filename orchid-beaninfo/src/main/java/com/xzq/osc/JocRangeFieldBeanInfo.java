/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocRangeFieldBeanInfo extends OrchidBaseBeanInfo {

  public JocRangeFieldBeanInfo() {
    super(JocRangeField.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "model", "valueClass", "required", "columns",
            "inputMask", "charCase");
  }
}
