/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.OrchidUtils;
import java.io.Serializable;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.UIManager;

/**
 *
 * @author zqxu
 */
public abstract class AbstractDateToStringConverter
        implements Converter<Date, String>, Serializable {

  private String[] supportedPatterns;
  private String pattern;
  private SimpleDateFormat defaultFormat;
  private SimpleDateFormat dateFormat;

  /**
   * Constructor with preferred pattern and date format.
   *
   * @param dateFormat SimpleDateFormat object.
   * @param pattern preferred pattern
   */
  public AbstractDateToStringConverter(String pattern,
          SimpleDateFormat dateFormat) {
    this.pattern = pattern;
    this.dateFormat = dateFormat;
  }

  /**
   * Returns the preferred pattern for convert date to string and convert string
   * to date. if this property is null, then the first pattern of supported
   * pattern should be used. default is null.
   *
   * @return preferred pattern.
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Sets the preferred pattern for convert date to string and convert string to
   * date.
   *
   * @param pattern preferred pattern.
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

  /**
   * Returns SimpleDateFormat object for this convert. default is null.
   *
   * @return SimpleDateFormat object.
   */
  public SimpleDateFormat getDateFormat() {
    return dateFormat;
  }

  /**
   * Sets SimpleDateFormat object for this convert.
   *
   * @param dateFormat SimpleDateFormat object
   */
  public void setDateFormat(SimpleDateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }

  /**
   * Returns SimpleDateFormat object used to convert between Date and String, if
   * getDateFormat returns null, then a default SimpleDateFormat object will be
   * used.
   *
   * @return SimpleDateFormat object
   */
  protected SimpleDateFormat getUsingFormat() {
    SimpleDateFormat format = getDateFormat();
    if (format == null) {
      if (defaultFormat == null) {
        defaultFormat = createDefaultFormat();
      }
      format = defaultFormat;
    }
    return format;
  }

  /**
   * Create default SimpleDateFormat object.
   *
   * @return SimpleDateFormat object
   */
  protected SimpleDateFormat createDefaultFormat() {
    SimpleDateFormat dtFormat;
    dtFormat = new SimpleDateFormat();
    dtFormat.setLenient(false);
    return dtFormat;
  }

  /**
   * Convert a date value to string, using pattern of this converter or first
   * pattern of the supported patterns of this converter.
   *
   * @param value date value.
   * @return converted string from date value.
   */
  @Override
  public String convertForward(Date value) {
    if (value == null) {
      return "";
    }
    SimpleDateFormat format = getUsingFormat();
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
   * Convert a string to date using pattern and supported patterns of this
   * converter. if the string cant not convert to date using any pattern in
   * supported patterns, an exception throwed.
   *
   * @param value date in string format.
   * @return converted date value from string value.
   * @throws IllegalArgumentException can not convert the value to date.
   */
  @Override
  public Date convertReverse(String value) {
    if (OrchidUtils.isEmpty(value)) {
      return null;
    }
    return convertToDate(getUsingFormat(), value);
  }

  /**
   * convert a string to date using the SimpleDateFormat format.
   *
   * @param value date in string format.
   * @return converted date
   * @throws IllegalArgumentException can not convert the value to date.
   */
  protected Date convertToDate(SimpleDateFormat format, String value)
          throws IllegalArgumentException {
    List<String> patternList = getPatternList();
    if (patternList.isEmpty()) {
      patternList.add("");
    }
    for (String usingpt : patternList) {
      ParsePosition pos = new ParsePosition(0);
      format.applyPattern(usingpt);
      Date date = format.parse(value, pos);
      if (pos.getIndex() == value.length()
              && pos.getErrorIndex() == -1) {
        return date;
      }
    }
    throw new IllegalArgumentException("Can not convert to Date: " + value);
  }

  /**
   * Returns a list of patterns include the preferred pattern and supported
   * patterns. if the supported patterns of this converter is null, then default
   * supported patterns in UIDefaults will be used.
   *
   * @return list of patterns.
   */
  protected List<String> getPatternList() {
    List<String> patternList = new ArrayList<String>();
    if (pattern != null && !pattern.isEmpty()) {
      patternList.add(pattern);
    }
    String[] patterns = getSupportedPatterns();
    if (patterns == null) {
      patterns = (String[]) UIManager.get(getDefaultPatternsKey());
    }
    if (patterns != null) {
      patternList.addAll(Arrays.asList(patterns));
    }
    return patternList;
  }

  /**
   * Returns true indicate that reverse convert supported.
   *
   * @return true.
   */
  @Override
  public boolean reverseSupported() {
    return true;
  }

  /**
   * Returns key of the default supported patterns in UIDefaults.
   *
   * @return key
   */
  protected abstract String getDefaultPatternsKey();
}