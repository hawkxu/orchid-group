/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import java.beans.BeanDescriptor;

/**
 *
 * @author zqxu
 */
public class JocLabelEditorBeanInfo extends OrchidBaseBeanInfo {

  public JocLabelEditorBeanInfo() {
    super(JocLabelEditor.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, new String[]{"alignToRootPane", "alignmentRoot",
              "columnWidth", "columnsLeftRoot", "drawGuideLine",
              "focusOnClickLabel", "labelEditorGap", "labelIcon",
              "labelPlacement", "labelText", "stretchEditorToFill"});
    EnumerationValue[] values = new EnumerationValue[]{
      new EnumerationValue("LEFT", OrchidUtils.LEFT, "com.xzq.osc.OrchidUtils.LEFT"),
      new EnumerationValue("TOP", OrchidUtils.TOP, "com.xzq.osc.OrchidUtils.TOP")
    };
    setEnumerationValues(values, new String[]{"labelPlacement"});
  }

  @Override
  public BeanDescriptor getBeanDescriptor() {
    BeanDescriptor descriptor = super.getBeanDescriptor();
    if (descriptor != null) {
      descriptor.setValue("containerDelegate", "getEditorContainer");
    }
    return descriptor;
  }
}
