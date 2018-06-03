/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import java.util.ResourceBundle;

/**
 *
 * @author zqxu
 */
public class BeanInfoResource {

  private static ResourceBundle bundle;

  private static ResourceBundle getResourceBundle() {
    if (bundle == null) {
      bundle = ResourceBundle.getBundle(BeanInfoResource.class.getName());
    }
    return bundle;
  }

  public static String getString(String keyName) {
    return getResourceBundle().getString(keyName);
  }
}
