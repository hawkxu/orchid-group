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
public class NumberValueModel extends AbstractValueModel<Number>
        implements Serializable {

  private NumberToStringConverter converter;

  public NumberValueModel() {
    this(Integer.class, null, null);
  }

  public NumberValueModel(Class<? extends Number> numberType) {
    this(numberType, null, null);
  }

  public NumberValueModel(Class<? extends Number> numberType,
          String pattern, Number value) {
    this(numberType, pattern, value,
            NumberTypeUtils.getMask(numberType), null);
  }

  public NumberValueModel(Class<? extends Number> numberType,
          String pattern, Number value,
          String defaultMask, CharCase defaultCase) {
    super(value, defaultMask, defaultCase);
    setNumberType(numberType);
    setPattern(pattern);
  }

  @Override
  protected void initializeLocalVars() {
    super.initializeLocalVars();
    converter = new NumberToStringConverter();
  }

  public Class<? extends Number> getNumberType() {
    return converter.getNumberType();
  }

  public void setNumberType(Class<? extends Number> numberType) {
    Class<? extends Number> old = getNumberType();
    if (!OrchidUtils.equals(old, numberType)) {
      converter.setNumberType(numberType);
      String oldMask = getDefaultMask();
      if (OrchidUtils.equals(oldMask, NumberTypeUtils.getMask(old))) {
        setDefaultMask(NumberTypeUtils.getMask(numberType));
      }
      setValue(getValue()); // force value match type
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
      Number number = parseValue(formatValue(value));
      if (!OrchidUtils.equals(number, value)) {
        // update value match pattern
        setValue(number);
      } else {
        // force update value text
        firePropertyChange("value", value, value);
      }
      firePropertyChange("pattern", old, pattern);
    }
  }

  @Override
  public Class<? extends Number> getValueClass() {
    return converter.getNumberType();
  }

  @Override
  public void setValue(Number value) {
    Class<? extends Number> numberType = getNumberType();
    super.setValue(NumberTypeUtils.convert(value, numberType));
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

  @Override
  public NumberValueModel clone() {
    return new NumberValueModel(getNumberType(), getPattern(),
            value, defaultMask, defaultCase);
  }
}
