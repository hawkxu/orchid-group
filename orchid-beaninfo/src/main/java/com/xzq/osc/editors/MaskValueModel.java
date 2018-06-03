/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import com.xzq.osc.JocInputMask;
import com.xzq.osc.field.AbstractValueModel;

/**
 *
 * @author zqxu
 */
public class MaskValueModel extends AbstractValueModel<String> {

  public MaskValueModel() {
    super(null, null, null);
  }

  @Override
  public Class<? extends String> getValueClass() {
    return String.class;
  }

  @Override
  public String parseValue(String text) {
    JocInputMask mask = new JocInputMask();
    mask.setMask(text);
    return text;
  }

  @Override
  public String formatValue(String value) {
    return value;
  }

  @Override
  public boolean parseSupported() {
    return true;
  }

  @Override
  public AbstractValueModel<String> clone() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
