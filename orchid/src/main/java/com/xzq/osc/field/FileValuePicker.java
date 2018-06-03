/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.JocValueField;
import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author zqxu
 */
public class FileValuePicker extends PopValuePicker {

  @Override
  public void doPickupValue(JocValueField field, Object value) {
    JFileChooser fc = new JFileChooser();
    if (value instanceof File) {
      fc.setSelectedFile((File) value);
    }
    fc.setAcceptAllFileFilterUsed(true);
    if (fc.showOpenDialog(field) == JFileChooser.APPROVE_OPTION) {
      field.setValue(fc.getSelectedFile());
    }
  }
}
