/*
 * Global resource manager, for multi-language support usage.
 * Author: Xu Zi Qiang
 */
package com.xzq.osc.resource;

import com.xzq.osc.OrchidUtils;
import java.awt.Image;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author zqxu
 */
public class Resource {

  private static final String IMG_BASE = "/com/xzq/osc/img/";
  public static final String BASE_NAME = "com/xzq/osc/resource/orchid";
  private static Locale locale = Locale.getDefault();
  private static ResourceBundle resourceBundle;

  /**
   *
   * @return
   */
  public static Locale getLocale() {
    return locale;
  }

  /**
   * 
   * @param locale
   */
  public static void setLocale(Locale locale) {
    if (OrchidUtils.equals(locale, Resource.locale)) {
      return;
    }
    Resource.locale = locale;
    Resource.resourceBundle = null;
  }

  /**
   * Returns a resource bundle using the the default locale.
   *
   * @return A resource bundle using the the default locale.
   */
  private static ResourceBundle getResourceBundle() {
    if (resourceBundle == null) {
      resourceBundle = ResourceBundle.getBundle(BASE_NAME, locale);
    }
    return resourceBundle;
  }

  /**
   * Get a string for the given key from resource file.
   *
   * @param key resource key
   * @return The string for the given key
   */
  public static String getString(String key) {
    ResourceBundle bundle = getResourceBundle();
    if (key == null || !bundle.containsKey(key)) {
      return null;
    } else {
      return getResourceBundle().getString(key);
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

  /**
   * A shortcut to Class.getResource, Finds a resource with a given name, for
   * internal use.
   *
   * @see Class#getResource(java.lang.String)
   * @param resouceName name of the desired resource
   * @return URL A {@link java.net.URL} object or <tt>null</tt> if no resource
   * with this name is found
   */
  public static URL getResource(String resouceName) {
    return Resource.class.getResource(resouceName);
  }

  /**
   * Returns icon with given name in Orchid package, for internal use.
   *
   * @param iconName name of the desired icon.
   * @return an icon object
   */
  public static Icon getOrchidIcon(String iconName) {
    URL resource = getResource(IMG_BASE + iconName);
    return resource == null ? null : new ImageIcon(resource);
  }

  /**
   * Returns image for given name in Orchid package, for internal use.
   *
   * @param iconName name of the desired icon
   * @return an image object
   */
  public static Image getOrchidImage(String iconName) {
    URL resource = getResource(IMG_BASE + iconName);
    return resource == null ? null : new ImageIcon(resource).getImage();
  }
}
