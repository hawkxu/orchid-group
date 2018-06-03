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
public class DateValueModel extends AbstractDateValueModel
        implements Serializable {

  public DateValueModel() {
    this(null, null);
  }

  public DateValueModel(Date value) {
    this(null, value);
  }

  public DateValueModel(String pattern, Date value) {
    this(pattern, value, "[0-9:/-]{0,10}", null);
  }

  public DateValueModel(String pattern, Date value,
          String defaultMask, CharCase defaultCase) {
    super(pattern, value, defaultMask, defaultCase);
  }

  /**
   * Returns a new instance of DateToStringConverter
   *
   * @return default converter
   */
  @Override
  protected AbstractDateToStringConverter createDefaultConverter() {
    return new DateToStringConverter();
  }

  @Override
  public DateValueModel clone() {
    return new DateValueModel(getPattern(), value, defaultMask, defaultCase);
  }
}
