/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.CharCase;
import com.xzq.osc.OrchidUtils;
import java.io.Serializable;

/**
 *
 * @author zqxu
 */
public class NumberRangeModel extends AbstractRangeModel<Number>
        implements Serializable {

  private NumberToStringConverter converter;

  public NumberRangeModel() {
    this(Integer.class, null, true, true, null);
  }

  public NumberRangeModel(Class<? extends Number> numberType,
          String pattern, RangeList<Number> wholeRanges) {
    this(numberType, pattern, true, true, wholeRanges);
  }

  public NumberRangeModel(Class<? extends Number> numberType,
          String pattern, boolean multipleRange,
          boolean rangeInterval, RangeList<Number> wholeRanges) {
    this(numberType, pattern, multipleRange, rangeInterval, wholeRanges,
            NumberTypeUtils.getMask(numberType), null);
  }

  public NumberRangeModel(Class<? extends Number> numberType,
          String pattern, boolean multipleRange,
          boolean rangeInterval, RangeList<Number> wholeRanges,
          String defaultMask, CharCase defaultCase) {
    super(multipleRange, rangeInterval, wholeRanges, defaultMask, defaultCase);
    setNumberType(numberType);
    setPattern(pattern);
  }

  @Override
  protected void initializeLocalVars() {
    super.initializeLocalVars();
    converter = createDefaultConverter();
  }

  public Class<? extends Number> getNumberType() {
    return converter.getNumberType();
  }

  public void setNumberType(Class<? extends Number> numberType) {
    Class<? extends Number> old = getNumberType();
    if (!OrchidUtils.equals(old, numberType)) {
      converter.setNumberType(numberType);
      setDefaultMask(NumberTypeUtils.getMask(numberType));
      reparseWholeRanges(true);
      firePropertyChange("numberType", old, numberType);
    }
  }

  public String getPattern() {
    return converter.getPattern();
  }

  public void setPattern(String pattern) {
    String old = getPattern();
    if (!OrchidUtils.equals(old, pattern)) {
      converter.setPattern(pattern);
      reparseWholeRanges(true);
      firePropertyChange("pattern", old, pattern);
    }
  }

  @Override
  public Class<? extends Number> getValueClass() {
    return Number.class;
  }

  @Override
  public Number parseValue(String text) {
    return converter.convertReverse(text);
  }

  @Override
  public String formatValue(Number value) {
    return converter.convertForward(value);
  }

  @Override
  public boolean parseSupported() {
    return true;
  }

  protected NumberToStringConverter createDefaultConverter() {
    return new NumberToStringConverter();
  }

  @Override
  public NumberRangeModel clone() {
    return new NumberRangeModel(getNumberType(), getPattern(), multipleRange,
            rangeInterval, wholeRanges, defaultMask, defaultCase);
  }
}
