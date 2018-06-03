/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 *
 * @author zqxu
 */
public class NumberTypeUtils {

  public static final Class[] supportedTypes = new Class[]{
    Byte.class, Short.class, Integer.class, Long.class,
    Float.class, Double.class, BigInteger.class, BigDecimal.class
  };

  public static void checkSupported(Class<? extends Number> numberType) {
    if (!Arrays.asList(supportedTypes).contains(numberType)) {
      throw new IllegalArgumentException(
              "Unsupported number type: " + numberType.getName());
    }
  }

  public static void configureFormat(DecimalFormat format,
          Class<? extends Number> numberType) {
    if (numberType == Float.class
            || numberType == Double.class) {
      format.setParseBigDecimal(false);
      format.setParseIntegerOnly(false);
    } else if (numberType == BigDecimal.class) {
      format.setParseBigDecimal(true);
      format.setParseIntegerOnly(false);
    } else {
      format.setParseBigDecimal(false);
      format.setParseIntegerOnly(true);
    }
  }

  public static String getMask(Class<? extends Number> numberType) {
    if (numberType == Byte.class) {
      return "[+-]{0,1}#{1,3}";
    } else if (numberType == Short.class) {
      return "[+-]{0,1}[0-9,]{1,6}";
    } else if (numberType == Integer.class) {
      return "[+-]{0,1}[0-9,]{1,13}";
    } else if (numberType == Long.class) {
      return "[+-]{0,1}[0-9,]{1,25}";
    } else if (numberType == Float.class) {
      return "[+-]{0,1}[0-9,]{1,51}.#{1,51}";
    } else if (numberType == Double.class) {
      return "[+-]{0,1}[0-9,]{1,411}.#{1,411}";
    } else if (numberType == BigInteger.class) {
      return "[+-]{0,1}[0-9,]{1,}";
    }
    return "[+-]{0,1}[0-9,]{1,}.#{1,}";
  }

  public static Number convert(Number value,
          Class<? extends Number> numberType) {
    if (value == null || value.getClass() == numberType) {
      return value;
    } else if (numberType == Byte.class) {
      return value.byteValue();
    } else if (numberType == Short.class) {
      return value.shortValue();
    } else if (numberType == Integer.class) {
      return value.intValue();
    } else if (numberType == Long.class) {
      return value.longValue();
    } else if (numberType == Float.class) {
      return value.floatValue();
    } else if (numberType == Double.class) {
      return value.doubleValue();
    } else if (numberType == BigInteger.class) {
      return BigInteger.valueOf(value.longValue());
    } else if (numberType == BigDecimal.class) {
      return BigDecimal.valueOf(value.doubleValue());
    }
    throw new IllegalArgumentException(
            "Unsupported number type: " + numberType.getName());
  }
}
