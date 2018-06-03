/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.OrchidUtils;
import java.io.Serializable;

/**
 *
 * @author zqxu
 */
public class Range<V> implements Serializable {

  /**
   * wildcard indicate one character
   */
  public static char wildcardSingleChar = '?';
  /**
   * wildcard indicate none or more characters
   */
  public static char wildcardStringChar = '*';
  private Sign sign = Sign.I;
  private Option option;
  private V lowValue;
  private V highValue;

  public Range() {
  }

  public Range(Sign sign) {
    this(sign, null, null, null);
  }

  public Range(V lowValue) {
    this(Sign.I, null, lowValue, null);
  }

  public Range(Sign sign, V lowValue) {
    this(sign, null, lowValue, null);
  }

  public Range(Option option, V lowValue) {
    this(Sign.I, option, lowValue, null);
  }

  public Range(Sign sign, Option option, V lowValue) {
    this(sign, option, lowValue, null);
  }

  public Range(V lowValue, V highValue) {
    this(Sign.I, Option.BT, lowValue, highValue);
  }

  /**
   *
   * @param sign
   * @param option
   * @param lowValue
   * @param highValue
   */
  public Range(Sign sign, Option option, V lowValue, V highValue) {
    checkHighValueGreaterThanLowValue(lowValue, highValue);
    this.sign = sign;
    this.option = option;
    this.lowValue = lowValue;
    this.highValue = highValue;
    updateOption(option);
  }

  /**
   * Returns sign.
   *
   * @return sign.
   */
  public Sign getSign() {
    return sign;
  }

  /**
   * Sets sign.
   *
   * @param sign sign.
   */
  public void setSign(Sign sign) {
    this.sign = sign == Sign.E ? Sign.E : Sign.I;
  }

  /**
   * Returns option.
   *
   * @return option.
   */
  public Option getOption() {
    return option;
  }

  /**
   * Sets option. the new option of range may be not exactly be the option. if
   * the option is not valid for current values, the range auto correct it.
   *
   *
   * @param option option.
   */
  public void setOption(Option option) {
    updateOption(option);
  }

  /**
   * Returns low value.
   *
   * @return low value.
   */
  public V getLowValue() {
    return lowValue;
  }

  /**
   * Sets low value. Set low value maybe cause the option updated.
   *
   * @param lowValue low value
   * @throws IllegalArgumentException if lowValue is empty and highValue is not
   * empty or the lowValue is greater than current highValue.
   */
  @SuppressWarnings("unchecked")
  public void setLowValue(V lowValue) {
    Object oldValue = this.lowValue;
    if (OrchidUtils.equals(oldValue, lowValue)) {
      return;
    }
    checkHighValueGreaterThanLowValue(lowValue, highValue);
    Option optionNew = option;
    if (!RangeUtils.hasWildcard(lowValue)
            && (option == Option.LK || option == Option.NL)) {
      optionNew = Option.EQ;
    } else if (RangeUtils.hasWildcard(lowValue)
            && !RangeUtils.hasWildcard(oldValue)
            && option != Option.LK && option != Option.NL) {
      optionNew = Option.LK;
    }
    this.lowValue = lowValue;
    updateOption(shouldNullOption() ? null : optionNew);
  }

  public V getHighValue() {
    return highValue;
  }

  /**
   * Sets high value. Set the highValue may be cause the option updated.
   *
   * @param highValue high value
   * @throws IllegalArgumentException if the highValue is not a comparable
   * object or it less than current lowValue.
   */
  @SuppressWarnings("unchecked")
  public void setHighValue(V highValue) {
    if (highValue != null && !(highValue instanceof Comparable)) {
      throw new IllegalArgumentException(
              "Can not set high value with non-comparable value.");
    }
    if (OrchidUtils.equals(highValue, this.highValue)) {
      return;
    }
    checkHighValueGreaterThanLowValue(lowValue, highValue);
    this.highValue = highValue;
    updateOption(shouldNullOption() ? null : option);
  }

  @SuppressWarnings("unchecked")
  private void checkHighValueGreaterThanLowValue(V lowValue,
          V highValue) {
    if (OrchidUtils.isEmpty(highValue)) {
      return;
    }
    if (OrchidUtils.isEmpty(lowValue)
            || ((Comparable) highValue).compareTo(lowValue) < 0) {
      throw new IllegalArgumentException(
              "High value must be greater than or equal to the low value");
    }
  }

  private boolean shouldNullOption() {
    return OrchidUtils.isEmpty(lowValue) && OrchidUtils.isEmpty(highValue);
  }

  /**
   * update current option to the new option, this method will validate the new
   * option and correct it if the new option is not valid for current values.
   *
   * @param option the new option
   */
  protected void updateOption(Option option) {
    if (!OrchidUtils.isEmpty(highValue)) {
      option = option == Option.NB ? option : Option.BT;
    } else if (!OrchidUtils.isEmpty(lowValue) && (option == null
            || option == Option.BT || option == Option.NB)) {
      option = RangeUtils.hasWildcard(lowValue) ? Option.LK : Option.EQ;
    }
    if ((option == Option.LT || option == Option.LE
            || option == Option.GT || option == Option.GE)
            && !(lowValue instanceof Comparable)) {
      return;
    }
    this.option = option;
    this.sign = option == null ? Sign.I : this.sign;
  }

  /**
   * Returns true if range is empty, otherwise false.
   *
   * @return true or false.
   */
  public boolean isEmpty() {
    return option == null;
  }

  @Override
  public Range<V> clone() {
    return new Range<V>(sign, option, lowValue, highValue);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + (sign != null ? sign.hashCode() : 0);
    hash = 97 * hash + (option != null ? option.hashCode() : 0);
    hash = 97 * hash + (lowValue != null ? lowValue.hashCode() : 0);
    hash = 97 * hash + (highValue != null ? highValue.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Range)) {
      return false;
    }
    Range other = (Range) obj;
    return other.sign == this.sign && other.option == this.option
            && OrchidUtils.equals(other.lowValue, this.lowValue)
            && OrchidUtils.equals(other.highValue, this.highValue);
  }

  @Override
  public String toString() {
    return Range.class.getName() + "[sign=Sign." + sign
            + ", option=Option." + option
            + ", lowValue=" + lowValue + ", highValue=" + highValue + "]";
  }

  public static enum Sign {

    /**
     * Include
     */
    I,
    /**
     * Exclude
     */
    E;
  }

  public static enum Option {

    /**
     * Like
     */
    LK,
    /**
     * Not like
     */
    NL,
    /**
     * Between
     */
    BT,
    /**
     * Not between
     */
    NB,
    /**
     * Equal
     */
    EQ,
    /**
     * Not equal
     */
    NE,
    /**
     * Less than
     */
    LT,
    /**
     * Less than or equal
     */
    LE,
    /**
     * Greater than
     */
    GT,
    /**
     * Greater than or equal
     */
    GE;
  }
}
