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
public class DateToStringConverter extends AbstractDateToStringConverter
        implements Serializable {

  /**
   * Constructor with null date format and null supported patterns
   */
  public DateToStringConverter() {
    this(null, null);
  }

  /**
   * Constructor with null date format and preferred pattern.
   *
   * @param pattern preferred pattern
   */
  public DateToStringConverter(String pattern) {
    this(pattern, null);
  }

  /**
   * Constructor with date format and null preferred pattern.
   *
   * @param dateFormat SimpleDateFormat object.
   */
  public DateToStringConverter(SimpleDateFormat dateFormat) {
    this(null, dateFormat);
  }

  /**
   * Constructor with preferred pattern and date format.
   *
   * @param dateFormat SimpleDateFormat object.
   * @param pattern preferred pattern
   */
  public DateToStringConverter(String pattern,
          SimpleDateFormat dateFormat) {
    super(pattern, dateFormat);
  }

  /**
   * Always retursn OrchidDefaults.SUPPORTED_DATE_PATTERNS
   *
   * @return OrchidDefaults.SUPPORTED_DATE_PATTERNS
   */
  @Override
  protected String getDefaultPatternsKey() {
    return OrchidDefaults.SUPPORTED_DATE_PATTERNS;
  }
}
