/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.OrchidUtils;
import com.xzq.osc.plaf.OrchidDefaults;
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.UIManager;

/**
 *
 * @author zqxu
 */
public class NumberToStringConverter implements Converter<Number, String>,
        Serializable {

  private String[] supportedPatterns;
  private Class<? extends Number> numberType;
  private String pattern;
  private DecimalFormat defaultFormat;
  private DecimalFormat numberFormat;

  /**
   * Constructor with null number format and no supported patterns.
   */
  public NumberToStringConverter() {
    this(null, Integer.class, null);
  }

  public NumberToStringConverter(String pattern) {
    this(pattern, Integer.class, null);
  }

  public NumberToStringConverter(Class<? extends Number> numberType) {
    this(null, numberType, null);
  }

  public NumberToStringConverter(String pattern,
          Class<? extends Number> numberType) {
    this(pattern, numberType, null);
  }

  public NumberToStringConverter(String pattern,
          Class<? extends Number> numberType, DecimalFormat numberFormat) {
    setPattern(pattern);
    setNumberType(numberType);
    setNumberFormat(numberFormat);
  }

  /**
   *
   * @return
   */
  public String getPattern() {
    return pattern;
  }

  /**
   *
   * @param pattern
   */
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  /**
   * Returns supported patterns of the converter. default is null.
   *
   * @return supported patterns
   */
  public String[] getSupportedPatterns() {
    return supportedPatterns;
  }

  /**
   * Sets supported patterns of the converter.
   *
   * @param supportedPatterns supported patterns.
   */
  public void setSupportedPatterns(String[] supportedPatterns) {
    this.supportedPatterns = supportedPatterns;
  }

  public Class<? extends Number> getNumberType() {
    return numberType;
  }

  public void setNumberType(Class<? extends Number> numberType) {
    NumberTypeUtils.checkSupported(numberType);
    this.numberType = numberType;
  }

  /**
   *
   * @return
   */
  public DecimalFormat getNumberFormat() {
    return numberFormat;
  }

  /**
   *
   * @param numberFormat
   */
  public void setNumberFormat(DecimalFormat numberFormat) {
    this.numberFormat = numberFormat;
  }

  /**
   *
   * @return
   */
  protected DecimalFormat getUsingFormat() {
    DecimalFormat format = getNumberFormat();
    if (format == null) {
      if (defaultFormat == null) {
        defaultFormat = createDefaultFormat();
      }
      format = defaultFormat;
    }
    // return a copy of format to avoid changes
    return (DecimalFormat) format.clone();
  }

  /**
   *
   * @return
   */
  protected DecimalFormat createDefaultFormat() {
    DecimalFormat format = new DecimalFormat();
    format.setRoundingMode(RoundingMode.HALF_UP);
    return format;
  }

  /**
   *
   * @param value
   * @return
   * @throws RuntimeException
   */
  @Override
  public String convertForward(Number value) throws RuntimeException {
    if (value == null) {
      return "";
    }
    DecimalFormat format = getUsingFormat();
    if (pattern != null && !pattern.isEmpty()) {
      format.applyPattern(pattern);
    } else {
      List<String> patterns = getPatternList();
      if (patterns.size() > 0) {
        format.applyPattern(patterns.get(0));
      }
    }
    return format.format(value);
  }

  /**
   *
   * @param value
   * @return
   * @throws RuntimeException
   */
  @Override
  public Number convertReverse(String value) throws RuntimeException {
    if (OrchidUtils.isEmpty(value)) {
      return null;
    }
    Number number = convertToNumber(value);
    Number realNumber = NumberTypeUtils.convert(number, numberType);
    if (realNumber == null
            || realNumber.doubleValue() == number.doubleValue()) {
      return number;
    }
    throw new IllegalArgumentException(
            "Can not convert \"" + value + "\" to " + numberType);
  }

  /**
   * convert String value to Number using supported patterns.
   *
   * @param text the string value
   * @return converted Number value
   * @throws IllegalArgumentException if value can no convert to Number using
   * supported patterns.
   */
  protected Number convertToNumber(String text)
          throws IllegalArgumentException {
    DecimalFormat format = getUsingFormat();
    NumberTypeUtils.configureFormat(format, numberType);
    List<String> patternList = getPatternList();
    if (patternList.isEmpty()) {
      patternList.add(null);
    }
    for (String usingpt : patternList) {
      ParsePosition pos = new ParsePosition(0);
      if (usingpt != null) {
        format.applyPattern(usingpt);
      }
      Number number = format.parse(text, pos);
      if (pos.getIndex() == text.length()
              && pos.getErrorIndex() == -1) {
        return number;
      }
    }
    return null;
  }

  protected List<String> getPatternList() {
    List<String> patternList = new ArrayList<String>();
    if (pattern != null && !pattern.isEmpty()) {
      patternList.add(pattern);
    }
    String[] patterns = getSupportedPatterns();
    if (patterns == null) {
      patterns = (String[]) UIManager
              .get(OrchidDefaults.SUPPORTED_NUMBER_PATTERNS);
    }
    if (patterns != null) {
      patternList.addAll(Arrays.asList(patterns));
    }
    return patternList;
  }

  /**
   *
   * @return
   */
  @Override
  public boolean reverseSupported() {
    return true;
  }
}
