/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.plaf.OrchidDefaults;
import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 *
 * @author zqxu
 */
public class TimeToStringConverter extends AbstractDateToStringConverter
        implements Serializable {

  public TimeToStringConverter() {
    this(null, null);
  }

  public TimeToStringConverter(String pattern) {
    this(pattern, null);
  }

  public TimeToStringConverter(SimpleDateFormat dateFormat) {
    this(null, dateFormat);
  }

  public TimeToStringConverter(String pattern, SimpleDateFormat dateFormat) {
    super(pattern, dateFormat);
  }

  /**
   * Always returns OrchidDefaults.SUPPORTED_TIME_PATTERNS
   *
   * @return OrchidDefaults.SUPPORTED_TIME_PATTERNS
   */
  @Override
  protected String getDefaultPatternsKey() {
    return OrchidDefaults.SUPPORTED_TIME_PATTERNS;
  }
}
