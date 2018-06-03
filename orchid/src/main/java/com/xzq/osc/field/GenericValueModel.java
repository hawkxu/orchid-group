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
public class GenericValueModel extends AbstractValueModel<Object>
        implements Serializable {

  private static final Class[] argTypes = new Class[]{String.class};
  private Class<? extends Object> valueClass;
  private Converter converter;

  public GenericValueModel() {
    this(null, null, null, null, null);
  }

  @SuppressWarnings("unchecked")
  public GenericValueModel(Class valueClass) {
    this(valueClass, (Converter) null, null);
  }

  @SuppressWarnings("unchecked")
  public <V> GenericValueModel(Class<? extends V> valueClass,
          Converter<V, String> converter, V value) {
    this(valueClass, converter, value, null, null);
  }

  public GenericValueModel(Object value,
          String defaultMask, CharCase defaultCase) {
    this(null, null, value, defaultMask, defaultCase);
  }

  public <V> GenericValueModel(Class<? extends V> valueClass,
          Converter<V, String> converter, V value,
          String defaultMask, CharCase defaultCase) {
    super(value, defaultMask, defaultCase);
    setClassConverter(valueClass, converter);
  }

  public <V> void setClassConverter(Class<? extends V> valueClass,
          Converter<V, String> converter) {
    this.valueClass = valueClass;
    this.converter = converter;
    Object nValue = getValue();
    if (valueClass == null || (nValue != null
            && !valueClass.isAssignableFrom(nValue.getClass()))) {
      nValue = null;
    }
    if (parseSupported()) {
      try {
        nValue = parseValue(formatValue(nValue));
      } catch (Exception ex) {
        nValue = null;
      }
    }
    if (!OrchidUtils.equals(value, nValue)) {
      setValue(nValue);
    } else {
      // force update value text
      firePropertyChange("value", value, value);
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
  public void setValue(Object value) {
    if (valueClass == null) {
      throw new IllegalStateException("valueClass not set");
    }
    if (value != null && !valueClass.isAssignableFrom(value.getClass())) {
      throw new IllegalArgumentException(
              "value is not an instance of " + valueClass.getName());
    }
    super.setValue(value);
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
  public GenericValueModel clone() {
    return new GenericValueModel(valueClass, converter, value,
            defaultMask, defaultCase);
  }
}
