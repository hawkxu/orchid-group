/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author zqxu
 */
public class JocTableCellEditorBeanInfo extends OrchidBaseBeanInfo {

  public JocTableCellEditorBeanInfo() {
    super(JocTableCellEditor.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "dateTimePattern", "numberPattern",
            "clickCountToStart", "horizontalAlignment", "editorClass",
            "editorComponent");
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
      new EnumerationValue("JocValueField", JocValueField.class,
      "com.xzq.osc.JocValueField.class"),
      new EnumerationValue("JTextField", JTextField.class,
      "javax.swing.JTextField.class"),
      new EnumerationValue("JPasswordField", JPasswordField.class,
      "javax.swing.JPasswordField.class"),
      new EnumerationValue("JCheckBox", JCheckBox.class,
      "javax.swing.JCheckBox.class"),
      new EnumerationValue("JRadioButton", JRadioButton.class,
      "javax.swing.JRadioButton.class"),
      new EnumerationValue("JComboBox", JComboBox.class,
      "javax.swing.JComboBox.class"),};
    setEnumerationValues(classes, "editorClass");
  }
}
