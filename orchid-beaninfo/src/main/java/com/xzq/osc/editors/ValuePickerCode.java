/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import com.xzq.osc.OrchidUtils;
import com.xzq.osc.field.ListValuePicker;
import com.xzq.osc.field.ValuePickEntry;
import com.xzq.osc.field.ValuePicker;
import java.util.List;

/**
 *
 * @author zqxu
 */
public class ValuePickerCode {

  public static Class<?> getKeyValueClass(
          List<ValuePickEntry> valuePickList) {
    if (valuePickList != null) {
      for (ValuePickEntry entry : valuePickList) {
        if (entry.getKey() != null) {
          return entry.getKey().getClass();
        }
      }
    }
    return String.class;
  }

  public static String getInitCode(ValuePicker valuePicker) {
    if (valuePicker instanceof ListValuePicker) {
      return getInitCode((ListValuePicker) valuePicker);
    }
    return "null";
  }

  private static String getInitCode(ListValuePicker valuePicker) {
    if (valuePicker == null
            || OrchidUtils.isEmpty(valuePicker.getValuePickList())) {
      return "null";
    }
    StringBuilder codeBuilder = new StringBuilder("new ");
    codeBuilder.append(valuePicker.getClass().getName()).append("(\n")
            .append(getFormatCode(valuePicker.getDisplayFormat()));
    List<ValuePickEntry> pickList = valuePicker.getValuePickList();
    int count = pickList.size();
    for (int index = 0; index < count; index++) {
      codeBuilder.append("\n  ");
      codeBuilder.append(getInitCode(pickList.get(index)));
      if (index < count - 1) {
        codeBuilder.append(",");
      }
    }
    return codeBuilder.append(")").toString();
  }

  private static String getFormatCode(int format) {
    String classText = ListValuePicker.class.getName() + ".";
    switch (format) {
      case ListValuePicker.KEY_ONLY:
        return classText + "KEY_ONLY,";
      case ListValuePicker.TEXT_ONLY:
        return classText + "TEXT_ONLY,";
      case ListValuePicker.KEY_AND_TEXT:
        return classText + "KEY_AND_TEXT,";
      default:
        return classText + "UI_DEFAULTS,";
    }
  }

  private static String getInitCode(ValuePickEntry entry) {
    return GenericCode.getInitCode(ValuePickEntry.class,
            entry.getKey(), entry.getText());
  }
}
