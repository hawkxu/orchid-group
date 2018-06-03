/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.CharCase;
import com.xzq.osc.OrchidUtils;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author zqxu
 */
public abstract class AbstractDateRangeModel
        extends AbstractRangeModel<Date> implements Serializable {

  private AbstractDateToStringConverter converter;

  public AbstractDateRangeModel(String pattern, boolean multipleRange,
          boolean rangeInterval, RangeList<Date> wholeRanges,
          String defaultMask, CharCase defaultCase) {
    super(multipleRange, rangeInterval, wholeRanges, defaultMask, defaultCase);
    setPattern(pattern);
  }

  @Override
  protected void initializeLocalVars() {
    super.initializeLocalVars();
    converter = createDefaultConverter();
  }

  /**
   * Returns preferred pattern.
   *
   * @return preferred pattern.
   * @see AbstractDateToStringConverter#getPattern()
   */
  public String getPattern() {
    return converter.getPattern();
  }

  /**
   * Sets preferred pattrn.
   *
   * @param pattern preferred pattern
   * @see AbstractDateToStringConverter#setPattern(java.lang.String)
   */
  public void setPattern(String pattern) {
    String old = getPattern();
    if (!OrchidUtils.equals(old, pattern)) {
      converter.setPattern(pattern);
      reparseWholeRanges(true);
      firePropertyChange("pattern", old, pattern);
    }
  }

  @Override
  public Class<? extends Date> getValueClass() {
    return Date.class;
  }

  @Override
  public Date parseValue(String text) {
    return converter.convertReverse(text);
  }

  @Override
  public String formatValue(Date value) {
    return converter.convertForward(value);
  }

  @Override
  public boolean parseSupported() {
    return converter.reverseSupported();
  }

  /**
   * force the value in the range to match the preferred pattern.
   *
   * @param range the range
   * @return a new range that with value match the preferred pattern.
   */
  @Override
  protected Range<Date> getRangeCopy(Range<Date> range) {
    return new Range<Date>(range.getSign(), range.getOption(),
            parseValue(formatValue(range.getLowValue())),
            parseValue(formatValue(range.getHighValue())));
  }

  /**
   * Create default converter used to convert between date and string.
   *
   * @return default converter.
   */
  protected abstract AbstractDateToStringConverter createDefaultConverter();
}
