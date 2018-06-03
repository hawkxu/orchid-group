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
public class BooleanToStringConverter
        implements Converter<Boolean, String>, Serializable {

  /**
   *
   * @param value
   * @return
   */
  @Override
  public String convertForward(Boolean value) {
    return String.valueOf(value);
  }

  /**
   *
   * @param value
   * @return
   */
  @Override
  public Boolean convertReverse(String value) {
    return OrchidUtils.isEmpty(value) ? null : Boolean.valueOf(value);
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
