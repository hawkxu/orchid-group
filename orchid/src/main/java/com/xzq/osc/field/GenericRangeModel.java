/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.CharCase;
import com.xzq.osc.OrchidUtils;
import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 *
 * @author zqxu
 */
public class GenericRangeModel extends AbstractRangeModel<Object>
        implements Serializable {

  private static final Class[] argTypes = new Class[]{String.class};
  private Class<? extends Object> valueClass;
  private Converter converter;

  public GenericRangeModel() {
    this(true, true, null);
  }

  @SuppressWarnings("unchecked")
  public GenericRangeModel(Class valueClass) {
    this(valueClass, null, null);
  }

  public GenericRangeModel(RangeList wholeRanges) {
    this(true, true, wholeRanges);
  }

  public GenericRangeModel(boolean multipleRange, boolean rangeInterval,
          RangeList wholeRanges) {
    this(multipleRange, rangeInterval, wholeRanges, null, null);
  }

  public <V> GenericRangeModel(Class<? extends V> valueClass,
          Converter<V, String> converter, RangeList wholeRanges) {
    this(valueClass, converter, true, true, wholeRanges, null, null);
  }

  public GenericRangeModel(boolean multipleRange, boolean rangeInterval,
          RangeList wholeRanges,
          String defaultMask, CharCase defaultCase) {
    this(null, null, multipleRange, rangeInterval, wholeRanges, defaultMask,
            defaultCase);
  }

  @SuppressWarnings("unchecked")
  public <V> GenericRangeModel(Class<? extends V> valueClass,
          Converter<V, String> converter, boolean multipleRange,
          boolean rangeInterval, RangeList wholeRanges,
          String defaultMask, CharCase defaultCase) {
    super(multipleRange, rangeInterval, wholeRanges, defaultMask, defaultCase);
    setClassConverter(valueClass, converter);
  }

  public <V> void setClassConverter(Class<? extends V> valueClass,
          Converter<V, String> converter) {
    Class<? extends Object> oClass = this.valueClass;
    Converter oConverter = this.converter;
    if (!OrchidUtils.equals(oClass, valueClass)
            || !OrchidUtils.equals(oConverter, this.converter)) {
      this.valueClass = valueClass;
      this.converter = converter;
      if (!OrchidUtils.equals(oClass, valueClass)) {
        setWholeRanges(null);
      } else if (!OrchidUtils.equals(oConverter, converter)) {
        reparseWholeRanges(true);
      }
    }
  }

  @Override
  public Class<? extends Object> getValueClass() {
    return valueClass != null ? valueClass : Object.class;
  }

  public Converter getConverter() {
    return converter;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object parseValue(String text) {
    if (converter == null) {
      try {
        return getConstructor().newInstance(text);
      } catch (Exception ex) {
        throw new IllegalArgumentException("can not parse value.", ex);
      }
    } else {
      return converter.convertReverse(text);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public String formatValue(Object value) {
    if (converter == null) {
      return value == null ? "" : value.toString();
    } else {
      return (String) converter.convertForward(value);
    }
  }

  @Override
  public boolean parseSupported() {
    return converter == null ? getConstructor() != null
            : converter.reverseSupported();
  }

  @SuppressWarnings("unchecked")
  private Constructor getConstructor() {
    try {
      Class type = getValueClass();
      type = type == Object.class ? String.class : type;
      return type.getConstructor(argTypes);
    } catch (Exception ex) {
      return null;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public GenericRangeModel clone() {
    return new GenericRangeModel(valueClass, converter, multipleRange,
            rangeInterval, wholeRanges, defaultMask, defaultCase);
  }
}
