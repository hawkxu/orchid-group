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
public class StringValueModel extends AbstractValueModel<String>
        implements Serializable {

  private boolean autoTrim;

  public StringValueModel() {
    this(true, null, null, null);
  }

  public StringValueModel(boolean autoTrim, String value) {
    this(autoTrim, value, null, null);
  }

  public StringValueModel(boolean autoTrim, String value,
          String defaultMask, CharCase defaultCase) {
    super(value, defaultMask, defaultCase);
    setAutoTrim(autoTrim);
  }

  public boolean isAutoTrim() {
    return autoTrim;
  }

  public void setAutoTrim(boolean autoTrim) {
    if (autoTrim != this.autoTrim) {
      boolean old = this.autoTrim;
      this.autoTrim = autoTrim;
      String text = parseValue(value);
      if (!text.equals(value)) {
        setValue(text);
      }
      firePropertyChange("autoTrim", old, autoTrim);
    }
  }

  @Override
  public Class<? extends String> getValueClass() {
    return String.class;
  }

  @Override
  public void setValue(String value) {
    super.setValue(parseValue(value));
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
  public AbstractValueModel<String> clone() {
    return new StringValueModel(autoTrim, value, defaultMask, defaultCase);
  }
}
