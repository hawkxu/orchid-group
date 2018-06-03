/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

/**
 *
 * @author zqxu
 */
public interface RangeModel<V> extends FieldModel<V>, Cloneable {

  /**
   * Returns true for multiple range allowed, false for single range only.
   *
   * @return true or false.
   */
  public boolean getMultipleRange();

  /**
   * Returns true for range interval allowed, false not.
   *
   * @return true or false.
   */
  public boolean getRangeInterval();

  /**
   * Returns first range of model, if currently no range in model, an empty
   * range should be returned.
   *
   * @return first range of model.
   */
  public Range<V> getFirstRange();

  /**
   * Sets first range of model, if currently no range in model, then add the
   * range to model.
   *
   * @param range range.
   */
  public void setFirstRange(Range<V> range);

  /**
   * remove all exists ranges, and set single range to model.
   *
   * @param range range.
   */
  public void setSingleRange(Range<V> range);

  /**
   * Returns whole ranges of model.
   *
   * @return whole ranges of model.
   */
  public RangeList<V> getWholeRanges();

  /**
   * Sets whole ranges of model.
   *
   * @param wholeRanges whole ranges, null for clear ranges in model.
   */
  public void setWholeRanges(RangeList<V> wholeRanges);

  /**
   * Clear ranges in model.
   */
  public void clear();

  /**
   * Returns range count in model.
   *
   * @return range count.
   */
  public int getRangeCount();

  /**
   * clone model.
   *
   * @return new instance of model.
   */
  public RangeModel<V> clone();

  /**
   * install range model listener to model.
   *
   * @param l range model listener.
   */
  public void addRangeModelListener(RangeModelListener l);

  /**
   * remove range model listener from model.
   *
   * @param l range model listener.
   */
  public void removeRangeModelListener(RangeModelListener l);
}