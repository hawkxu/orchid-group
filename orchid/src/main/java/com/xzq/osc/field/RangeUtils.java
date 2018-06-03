/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.OrchidLocale;
import com.xzq.osc.field.Range.Option;
import com.xzq.osc.field.Range.Sign;
import com.xzq.osc.resource.Resource;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;

/**
 *
 * @author zqxu
 */
public class RangeUtils {

  private static Map<String, Icon> iconsMap;
  private static Map<Object, String> textsMap;

  public static Icon getOptionIcon(Sign sign, Option option) {
    String iconName;
    if (option == null) {
      if (sign != null) {
        iconName = "OPT_NULL";
      } else {
        iconName = "OPT_DELE";
      }
    } else {
      iconName = "OPT" + sign + "_" + option;
    }
    return getCachedIcon(iconName);
  }

  public static String getOptionText(Option option) {
    if (textsMap == null) {
      textsMap = new HashMap<Object, String>();
    }
    if (!textsMap.containsKey(option)) {
      textsMap.put(option, OrchidLocale.getString("OPTION_" + option));
    }
    return textsMap.get(option);
  }

  public static Icon getMultipleIcon(int rangeCount) {
    String iconName = "MULT_NONE";
    if (rangeCount > 1) {
      iconName = "MULT_MULT";
    }
    return getCachedIcon(iconName);
  }

  private static Icon getCachedIcon(String iconName) {
    if (iconsMap == null) {
      iconsMap = new HashMap<String, Icon>();
    }
    if (!iconsMap.containsKey(iconName)) {
      iconsMap.put(iconName, Resource.getOrchidIcon(iconName + ".png"));
    }
    return iconsMap.get(iconName);
  }

  /**
   * Returns true if value is a String and contains
   * <code>Range.wildcardSingleChar</code> or
   * <code>Range.wildcardStringChar</code>.
   *
   * @param value value
   * @return true or false.
   */
  public static boolean hasWildcard(Object value) {
    if (value instanceof String) {
      String text = (String) value;
      return text.indexOf(Range.wildcardSingleChar) != -1
              || text.indexOf(Range.wildcardStringChar) != -1;
    }
    return false;
  }
}
