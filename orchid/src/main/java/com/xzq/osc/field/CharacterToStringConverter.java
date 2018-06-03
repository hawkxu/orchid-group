/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.OrchidUtils;
import java.io.Serializable;

/**
 *
 * @author zqxu
 */
public class CharacterToStringConverter
        implements Converter<Character, String>, Serializable {

  /**
   *
   * @param value
   * @return
   */
  @Override
  public String convertForward(Character value) {
    return value.toString();
  }

  /**
   *
   * @param value
   * @return
   */
  @Override
  public Character convertReverse(String value) {
    return OrchidUtils.isEmpty(value) ? null : value.charAt(0);
  }

  /**
   *
   * @return
   */
  @Override
  public boolean reverseSupported() {
    return true;
  }
}
