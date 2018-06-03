/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocGroupBorderBeanInfo extends OrchidBaseBeanInfo {

  public JocGroupBorderBeanInfo() {
    super(JocGroupBorder.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    EnumerationValue[] titleAlignments = new EnumerationValue[]{
      new EnumerationValue("LEFT", OrchidUtils.LEFT, "com.xzq.osc.OrchidUtils.LEFT"),
      new EnumerationValue("CENTER", OrchidUtils.CENTER, "com.xzq.osc.OrchidUtils.CENTER"),
      new EnumerationValue("RIGHT", OrchidUtils.RIGHT, "com.xzq.osc.OrchidUtils.RIGHT"),
      new EnumerationValue("LEADING", OrchidUtils.LEADING, "com.xzq.osc.OrchidUtils.LEADING"),
      new EnumerationValue("TRAILING", OrchidUtils.TRAILING, "com.xzq.osc.OrchidUtils.TRAILING")};
    setEnumerationValues(titleAlignments, "titleAlignment");
  }
}
