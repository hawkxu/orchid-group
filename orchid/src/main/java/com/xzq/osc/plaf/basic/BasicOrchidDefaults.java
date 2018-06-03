/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.plaf.basic;

import com.xzq.osc.field.DateValuePicker;
import com.xzq.osc.field.DefaultValuePickerProvider;
import com.xzq.osc.field.FileValuePicker;
import com.xzq.osc.field.ListValuePicker;
import com.xzq.osc.plaf.OrchidDefaults;
import com.xzq.osc.resource.Resource;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Date;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 *
 * @author zqxu
 */
public final class BasicOrchidDefaults implements OrchidDefaults {

  public static void install(UIDefaults defaults) {
    defaults.put(FULL_CHECKED_ICON, Resource.getOrchidIcon("checkfull.png"));
    defaults.put(NONE_CHECKED_ICON, Resource.getOrchidIcon("checknone.png"));
    defaults.put(GRAY_CHECKED_ICON, Resource.getOrchidIcon("checkgray.png"));
    defaults.put(DEFAULT_PICKUP_ICON, Resource.getOrchidIcon("pickup.png"));
    defaults.put(LIST_PICKER_ROWS, Integer.valueOf(8));
    defaults.put(LIST_PICKER_FORMAT, ListValuePicker.KEY_AND_TEXT);
    defaults.put(DEFAULT_PICKER_PROVIDER, getDefaultPickerProvider());
    defaults.put(SUPPORTED_DATE_PATTERNS, new String[]{
      "yyyy-MM-dd", "MM-dd-yyyy", "yyyyMMdd", "MMddyyyy",
      "yyyy/MM/dd", "MM/dd/yyyy", "yyyy.MM.dd", "MM.dd.yyyy"});
    defaults.put(SUPPORTED_TIME_PATTERNS, new String[]{
      "HH:mm:ss", "HHmmss", "HH:mm", "HHmm"});
    defaults.put(FIELD_PICK_KEYCODE, KeyEvent.VK_F4);
    defaults.put(TABLE_TITLE_SORTER, Boolean.TRUE);
    defaults.put(TABLE_SELECTION_LABEL, Boolean.TRUE);
    defaults.put(TABLE_READONLY_BACKGROUND,
            UIManager.getColor("Label.background"));
  }

  private static Object getDefaultPickerProvider() {
    DefaultValuePickerProvider provider = new DefaultValuePickerProvider();
    provider.putClassValuePicker(Date.class, new DateValuePicker());
    provider.putClassValuePicker(File.class, new FileValuePicker());
    return provider;
  }
}
