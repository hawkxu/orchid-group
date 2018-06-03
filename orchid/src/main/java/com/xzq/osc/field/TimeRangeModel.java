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
public class TimeRangeModel extends AbstractDateRangeModel
        implements Serializable {

  public TimeRangeModel() {
    this(null, true, true, null);
  }

  public TimeRangeModel(String pattern, RangeList<Date> wholeRanges) {
    this(pattern, true, true, wholeRanges);
  }

  public TimeRangeModel(String pattern, boolean multipleRange,
          boolean rangeInterval, RangeList<Date> wholeRanges) {
    this(pattern, multipleRange, rangeInterval, wholeRanges, "[0-9:]{0,8}",
            null);
  }

  public TimeRangeModel(String pattern, boolean multipleRange,
          boolean rangeInterval, RangeList<Date> wholeRanges,
          String defaultMask, CharCase defaultCase) {
    super(pattern, multipleRange, rangeInterval, wholeRanges, defaultMask,
            defaultCase);
  }

  /**
   * Returns a new instance of TimeToStringConverter.
   *
   * @return default converter
   * @see TimeToStringConverter
   */
  @Override
  protected AbstractDateToStringConverter createDefaultConverter() {
    return new TimeToStringConverter();
  }

  @Override
  public TimeRangeModel clone() {
    return new TimeRangeModel(getPattern(), multipleRange, rangeInterval,
            wholeRanges, defaultMask, defaultCase);
  }
}
