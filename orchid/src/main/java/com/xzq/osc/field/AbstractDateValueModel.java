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
public abstract class AbstractDateValueModel
        extends AbstractValueModel<Date> implements Serializable {

  private AbstractDateToStringConverter converter;

  public AbstractDateValueModel(String pattern, Date value,
          String defaultMask, CharCase defaultCase) {
    super(value, defaultMask, defaultCase);
    setPattern(pattern);
  }

  @Override
  protected void initializeLocalVars() {
    super.initializeLocalVars();
    converter = createDefaultConverter();
  }

  public String getPattern() {
    return converter.getPattern();
  }

  public void setPattern(String pattern) {
    String old = getPattern();
    if (!OrchidUtils.equals(old, pattern)) {
      Date oldValue = getValue();
      converter.setPattern(pattern);
      setValue(oldValue);
      //force update text.
      if (OrchidUtils.equals(oldValue, getValue())) {
        firePropertyChange("value", oldValue, oldValue);
      }
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

  /**
   * convert the date value to match the preferred pattern then set to model.
   *
   * @param value date value.
   */
  @Override
  public void setValue(Date value) {
    if (value != null) {
      value = parseValue(formatValue(value));
    }
    super.setValue(value);
  }

  @Override
  public boolean parseSupported() {
    return converter.reverseSupported();
  }

  /**
   * create default converter used to convert between date and string.
   *
   * @return default converter
   */
  protected abstract AbstractDateToStringConverter createDefaultConverter();
}
