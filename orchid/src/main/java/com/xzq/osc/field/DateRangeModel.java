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
public class DateRangeModel extends AbstractDateRangeModel
        implements Serializable {

  public DateRangeModel() {
    this(null, true, true, null);
  }

  public DateRangeModel(String pattern, RangeList<Date> wholeRanges) {
    this(pattern, true, true, wholeRanges);
  }

  public DateRangeModel(String pattern, boolean multipleRange,
          boolean rangeInterval, RangeList<Date> wholeRanges) {
    this(pattern, multipleRange, rangeInterval, wholeRanges, "[0-9:/-]{0,10}",
            null);
  }

  public DateRangeModel(String pattern, boolean multipleRange,
          boolean rangeInterval, RangeList<Date> wholeRanges,
          String defaultMask, CharCase defaultCase) {
    super(pattern, multipleRange, rangeInterval, wholeRanges,
            defaultMask, defaultCase);
  }

  /**
   * Returns a new instance of DateToStringConverter.
   *
   * @return default converter.
   * @see DateToStringConverter
   */
  @Override
  protected AbstractDateToStringConverter createDefaultConverter() {
    return new DateToStringConverter();
  }

  @Override
  public DateRangeModel clone() {
    return new DateRangeModel(getPattern(), multipleRange, rangeInterval,
            wholeRanges, defaultMask, defaultCase);
  }
}
