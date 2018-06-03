/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import com.xzq.osc.OrchidLogger;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zqxu
 */
public class GenericXML {

  private static final Map<Class, String> patterns;

  static {
    patterns = new HashMap<Class, String>();
    patterns.put(String.class, "%s");
    patterns.put(Class.class, "%s");
    patterns.put(Boolean.class, "%s");
    patterns.put(Byte.class, "%s");
    patterns.put(Short.class, "%s");
    patterns.put(Integer.class, "%s");
    patterns.put(Long.class, "%s");
    patterns.put(Float.class, "%s");
    patterns.put(Double.class, "%s");
    patterns.put(BigInteger.class, "%s");
    patterns.put(BigDecimal.class, "%s");
    patterns.put(Date.class, "%s");
    patterns.put(File.class, "%s");
    patterns.put(URI.class, "%s");
  }

  public static String getAsString(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Enum) {
      return ((Enum) value).name();
    }
    Class vClass = value.getClass();
    if (!patterns.containsKey(vClass)) {
      throw new IllegalArgumentException(
              "Not supported type: " + vClass.getName());
    }
    if (vClass == Date.class) {
      value = ((Date) value).getTime();
    } else if (vClass == File.class) {
      value = ((File) value).getAbsolutePath();
    } else if (vClass == Class.class) {
      value = ((Class) value).getName();
    }
    return String.format(patterns.get(vClass), value);
  }

  @SuppressWarnings("unchecked")
  public static <T> T parseValue(String text, Class<T> valueClass) {
    if (text == null || text.isEmpty()) {
      return (T) (valueClass == String.class ? "" : null);
    }
    try {
      if (valueClass == String.class) {
        return (T) text;
      } else if (valueClass == Class.class) {
        return (T) Class.forName(text);
      } else if (valueClass == Boolean.class) {
        return (T) Boolean.valueOf(text);
      } else if (valueClass == Byte.class) {
        return (T) Byte.valueOf(text);
      } else if (valueClass == Short.class) {
        return (T) Short.valueOf(text);
      } else if (valueClass == Integer.class) {
        return (T) Integer.valueOf(text);
      } else if (valueClass == Long.class) {
        return (T) Long.valueOf(text);
      } else if (valueClass == Float.class) {
        return (T) Float.valueOf(text);
      } else if (valueClass == Double.class) {
        return (T) Double.valueOf(text);
      } else if (valueClass == BigInteger.class) {
        return (T) new BigInteger(text);
      } else if (valueClass == BigDecimal.class) {
        return (T) new BigDecimal(text);
      } else if (valueClass == Date.class) {
        return (T) new Date(Long.valueOf(text));
      } else if (valueClass == File.class) {
        return (T) new File(text);
      } else if (valueClass == URI.class) {
        return (T) URI.create(text);
      } else if (Enum.class.isAssignableFrom(valueClass)) {
        return (T) Enum.valueOf((Class<Enum>) valueClass, text);
      }
    } catch (Exception ex) {
      OrchidLogger.warning("parse value fail.", ex);
    }
    return null;
  }
}
