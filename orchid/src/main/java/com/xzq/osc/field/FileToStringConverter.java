/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.OrchidUtils;
import java.io.File;
import java.io.Serializable;

/**
 *
 * @author zqxu
 */
public class FileToStringConverter implements Converter<File, String>,
        Serializable {

  /**
   *
   * @param value
   * @return
   */
  @Override
  public String convertForward(File value) {
    return value == null ? "" : value.getAbsolutePath();
  }

  /**
   *
   * @param value
   * @return
   */
  @Override
  public File convertReverse(String value) {
    return OrchidUtils.isEmpty(value) ? null : new File(value);
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
