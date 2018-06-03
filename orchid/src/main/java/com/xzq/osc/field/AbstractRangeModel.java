/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.CharCase;
import com.xzq.osc.field.Range.Option;
import java.io.Serializable;

/**
 *
 * @author zqxu
 */
public abstract class AbstractRangeModel<V> extends AbstractFieldModel<V>
        implements RangeModel<V>, Serializable {

  protected boolean multipleRange;
  protected boolean rangeInterval;
  protected RangeList<V> wholeRanges;

  public AbstractRangeModel(RangeList<V> wholeRanges) {
    this(true, true, wholeRanges);
  }

  public AbstractRangeModel(boolean multipleRange,
          boolean rangeInterval, RangeList<V> wholeRanges) {
    this(multipleRange, rangeInterval, wholeRanges, null, null);
  }

  public AbstractRangeModel(boolean multipleRange,
          boolean rangeInterval, RangeList<V> wholeRanges,
          String defaultMask, CharCase defaultCase) {
    super(defaultMask, defaultCase);
    setMultipleRange(multipleRange);
    setRangeInterval(rangeInterval);
    setWholeRanges(wholeRanges);
  }

  /**
   * initialize local variables.
   */
  @Override
  protected void initializeLocalVars() {
    super.initializeLocalVars();
    wholeRanges = new RangeList<V>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean getMultipleRange() {
    return multipleRange;
  }

  /**
   * Set multiple range property.
   *
   * @param multipleRange true or false.
   */
  public void setMultipleRange(boolean multipleRange) {
    if (multipleRange != this.multipleRange) {
      boolean old = this.multipleRange;
      this.multipleRange = multipleRange;
      if (!multipleRange && wholeRanges.size() > 1) {
        setSingleRange(getFirstRange());
      }
      firePropertyChange("multipleRange", old, multipleRange);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean getRangeInterval() {
    return rangeInterval;
  }

  /**
   * Sets rangeInterval property.
   *
   * @param rangeInterval true or false.
   */
  public void setRangeInterval(boolean rangeInterval) {
    if (rangeInterval != this.rangeInterval) {
      boolean old = this.rangeInterval;
      this.rangeInterval = rangeInterval;
      if (!rangeInterval) {
        updateWholeRangesNoInterval();
      }
      firePropertyChange("rangeInterval", old, rangeInterval);
    }
  }

  private void updateWholeRangesNoInterval() {
    boolean update = false;
    for (int i = wholeRanges.size() - 1; i >= 0; i--) {
      Range<V> range = wholeRanges.get(i);
      if (range.getOption() == Option.BT
              || range.getOption() == Option.NB) {
        update = true;
        wholeRanges.remove(i);
      }
    }
    if (update) {
      fireWholeRangesChange(new RangeModelEvent(this));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Range<V> getFirstRange() {
    if (wholeRanges.isEmpty()) {
      return new Range<V>();
    } else {
      return wholeRanges.get(0).clone();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFirstRange(Range<V> firstRange) {
    if (!isNullOrEmpty(firstRange)) {
      firstRange = getRangeCopy(firstRange);
    }
    if (wholeRanges.isEmpty()) {
      if (isNullOrEmpty(firstRange)) {
        return;
      } else {
        wholeRanges.add(firstRange);
      }
    } else if (isNullOrEmpty(firstRange)) {
      wholeRanges.remove(0);
    } else {
      wholeRanges.set(0, firstRange);
    }
    fireWholeRangesChange(new RangeModelEvent(this));
  }

  private boolean isNullOrEmpty(Range<V> range) {
    return range == null || range.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSingleRange(Range<V> range) {
    RangeList<V> copyList = new RangeList<V>();
    if (range != null && !range.isEmpty()) {
      copyList.add(range);
    }
    setWholeRanges(copyList);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RangeList<V> getWholeRanges() {
    RangeList<V> copyList = new RangeList<V>();
    for (Range<V> range : wholeRanges) {
      copyList.add(range.clone());
    }
    return copyList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setWholeRanges(RangeList<V> ranges) {
    if (!multipleRange && ranges != null && ranges.size() > 1) {
      throw new IllegalArgumentException("multiple range not accept!");
    }
    RangeList<V> tmp = new RangeList<V>();
    if (ranges != null) {
      for (Range<V> range : ranges) {
        if (range == null || range.isEmpty() || tmp.contains(range)) {
          continue;
        }
        Option opt = range.getOption();
        if (!rangeInterval && (opt == Option.BT || opt == Option.NB)) {
          throw new IllegalArgumentException("range interval not accept!");
        }
        tmp.add(getRangeCopy(range));
      }
    }
    wholeRanges = tmp;
    fireWholeRangesChange(new RangeModelEvent(this));
  }

  /**
   * Returns a copy of the range used to add to the model.
   *
   * @param range the range, always not null.
   * @return a copy of the range.
   */
  protected Range<V> getRangeCopy(Range<V> range) {
    return range.clone();
  }

  /**
   * re-parse whole ranges.
   *
   * @param forceFireEvent force fire range model event.
   */
  protected void reparseWholeRanges(boolean forceFireEvent) {
    if (wholeRanges.isEmpty() || !parseSupported()) {
      return;
    }
    boolean update = false;
    RangeList<V> tmp = new RangeList<V>();
    for (int i = 0; i < wholeRanges.size(); i++) {
      Range<V> range = wholeRanges.get(i);
      try {
        Range<V> copy = new Range<V>(range.getSign(), range.getOption(),
                parseValue(formatValue(range.getLowValue())),
                parseValue(formatValue(range.getHighValue())));
        if (!copy.equals(range) || copy.isEmpty() || tmp.contains(copy)) {
          update = true;
        }
        if (!copy.isEmpty() && !tmp.contains(copy)) {
          tmp.add(copy);
        }
      } catch (Exception ex) {
        update = true;
      }
    }
    wholeRanges = tmp;
    if (update || forceFireEvent) {
      fireWholeRangesChange(new RangeModelEvent(this));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() {
    wholeRanges.clear();
    fireWholeRangesChange(new RangeModelEvent(this));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getRangeCount() {
    return wholeRanges.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract AbstractRangeModel<V> clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public void addRangeModelListener(RangeModelListener l) {
    listenerList.add(RangeModelListener.class, l);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeRangeModelListener(RangeModelListener l) {
    listenerList.remove(RangeModelListener.class, l);
  }

  /**
   * Return all installed range model listeners.
   *
   * @return all installed range model listeners.
   */
  protected RangeModelListener[] getRangeModelListeners() {
    return listenerList.getListeners(RangeModelListener.class);
  }

  /**
   * fire range model event.
   *
   * @param evt range model event object.
   */
  protected void fireWholeRangesChange(RangeModelEvent evt) {
    for (RangeModelListener l : getRangeModelListeners()) {
      l.wholeRangesChange(evt);
    }
  }
}
