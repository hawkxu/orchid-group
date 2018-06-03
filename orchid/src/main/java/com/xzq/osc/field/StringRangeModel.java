/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.CharCase;
import java.io.Serializable;

/**
 *
 * @author zqxu
 */
public class StringRangeModel extends AbstractRangeModel<String>
        implements Serializable {

  private boolean autoTrim;

  public StringRangeModel() {
    this(true, true, true, null);
  }

  public StringRangeModel(boolean autoTrim, RangeList<String> wholeRanges) {
    this(autoTrim, true, true, wholeRanges);
  }

  public StringRangeModel(boolean autoTrim, boolean multipleRange,
          boolean rangeInterval, RangeList<String> wholeRanges) {
    this(autoTrim, multipleRange, rangeInterval, wholeRanges, null, null);
  }

  public StringRangeModel(boolean autoTrim, boolean multipleRange,
          boolean rangeInterval, RangeList<String> wholeRanges,
          String defaultMask, CharCase defaultCase) {
    super(multipleRange, rangeInterval, wholeRanges, defaultMask, defaultCase);
    setAutoTrim(autoTrim);
  }

  public boolean isAutoTrim() {
    return autoTrim;
  }

  public void setAutoTrim(boolean autoTrim) {
    if (autoTrim != this.autoTrim) {
      boolean old = this.autoTrim;
      this.autoTrim = autoTrim;
      reparseWholeRanges(false);
      firePropertyChange("autoTrim", old, autoTrim);
    }
  }

  @Override
  public Class<? extends String> getValueClass() {
    return String.class;
  }

  @Override
  public String parseValue(String text) {
    if (text == null) {
      return "";
    } else if (autoTrim) {
      return text.trim();
    }
    return text;
  }

  @Override
  public String formatValue(String value) {
    return parseValue(value);
  }

  @Override
  public boolean parseSupported() {
    return true;
  }

  @Override
  public StringRangeModel clone() {
    return new StringRangeModel(autoTrim, multipleRange, rangeInterval,
            wholeRanges, defaultMask, defaultCase);
  }
}
