/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import static com.xzq.osc.resource.Resource.getString;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * control locale of orchid compoents. usually, the orchid components using
 * system locale.
 *
 * @author zqxu
 */
public final class OrchidLocale {

  private static final String bundle = OrchidLocale.class.getName();
  private static Locale locale = Locale.getDefault();

  /**
   * Returns current locale of OrchidLocale.
   * @return current locale
   */
  public static Locale getLocale() {
    return getResource().getLocale();
  }

  /**
   * Sets locale of OrchidLocale.
   * @param locale current locale.
   */
  public static void setLocale(Locale locale) {
    OrchidLocale.locale = locale;
  }

  private static ResourceBundle getResource() {
    return ResourceBundle.getBundle(bundle, locale);
  }

  /**
   * Get a string for the given key from resource file.
   *
   * @param key resource key
   * @return The string for the given key
   */
  public static String getString(String key) {
    ResourceBundle resource = getResource();
    if (key == null || !resource.containsKey(key)) {
      return null;
    } else {
      return resource.getString(key);
    }
  }

  /**
   * Get a formatted string for the given key and arguments.
   *
   * @param key resource key
   * @param args arguments
   * @return a formatted string
   */
  public static String getString(String key, Object... args) {
    String value = getString(key);
    if (value == null) {
      return null;
    } else {
      return String.format(value, args);
    }
  }
}
