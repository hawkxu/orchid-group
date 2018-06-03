/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.CharCase;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author zqxu
 */
public class TimeValueModel extends AbstractDateValueModel
        implements Serializable {

  public TimeValueModel() {
    this(null, null);
  }

  public TimeValueModel(Date value) {
    this(null, value);
  }

  public TimeValueModel(String pattern, Date value) {
    this(pattern, value, "[0-9:]{0,8}", null);
  }

  public TimeValueModel(String pattern, Date value,
          String defaultMask, CharCase defaultCase) {
    super(pattern, value, defaultMask, defaultCase);
  }

  /**
   * Returns a new instance of TimeToStringConverter
   *
   * @return default converter.
   */
  @Override
  protected AbstractDateToStringConverter createDefaultConverter() {
    return new TimeToStringConverter();
  }

  @Override
  public TimeValueModel clone() {
    return new TimeValueModel(getPattern(), value, defaultMask, defaultCase);
  }
}
