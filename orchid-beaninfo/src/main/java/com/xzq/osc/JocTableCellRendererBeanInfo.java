/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.JocTableCellRenderer.CheckRenderer;
import com.xzq.osc.JocTableCellRenderer.LabelRenderer;
import com.xzq.osc.JocTableCellRenderer.PasswordRenderer;
import com.xzq.osc.JocTableCellRenderer.RadioRenderer;
import javax.swing.SwingConstants;

/**
 *
 * @author zqxu
 */
public class JocTableCellRendererBeanInfo extends OrchidBaseBeanInfo {

  public JocTableCellRendererBeanInfo() {
    super(JocTableCellRenderer.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "background", "dateTimePattern", "foreground",
            "horizontalAlignment", "icon", "numberPattern", "rendererClass",
            "text", "emptyNullCell", "emptyZeroNumber");
    EnumerationValue[] horizontals = new EnumerationValue[]{
      new EnumerationValue("SWING_DEFAULT", OrchidConstants.SWING_DEFAULT,
      "com.xzq.osc.OrchidConstants.SWING_DEFAULT"),
      new EnumerationValue("LEADING", SwingConstants.LEADING,
      "javax.swing.SwingConstants.LEADING"),
      new EnumerationValue("LEFT", SwingConstants.LEFT,
      "javax.swing.SwingConstants.LEFT"),
      new EnumerationValue("CENTER", SwingConstants.CENTER,
      "javax.swing.SwingConstants.CENTER"),
      new EnumerationValue("RIGHT", SwingConstants.RIGHT,
      "javax.swing.SwingConstants.RIGHT"),
      new EnumerationValue("TRAILING", SwingConstants.TRAILING,
      "javax.swing.SwingConstants.TRAILING")};
    setEnumerationValues(horizontals, "horizontalAlignment");
    EnumerationValue[] classes = new EnumerationValue[]{
      new EnumerationValue("LabelRenderer", LabelRenderer.class,
      "com.xzq.osc.JocTableCellRenderer.LabelRenderer.class"),
      new EnumerationValue("PasswordRenderer", PasswordRenderer.class,
      "com.xzq.osc.JocTableCellRenderer.PasswordRenderer.class"),
      new EnumerationValue("CheckRenderer", CheckRenderer.class,
      "com.xzq.osc.JocTableCellRenderer.CheckRenderer.class"),
      new EnumerationValue("RadioRenderer", RadioRenderer.class,
      "com.xzq.osc.JocTableCellRenderer.RadioRenderer.class")};
    setEnumerationValues(classes, "rendererClass");
  }
}
