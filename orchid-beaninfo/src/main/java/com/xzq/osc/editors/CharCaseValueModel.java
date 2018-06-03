/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import com.xzq.osc.CharCase;
import com.xzq.osc.field.AbstractValueModel;

/**
 *
 * @author zqxu
 */
public class CharCaseValueModel extends AbstractValueModel<CharCase> {

  public CharCaseValueModel() {
    super(null, null, null);
  }

  @Override
  public Class<? extends CharCase> getValueClass() {
    return CharCase.class;
  }

  @Override
  public CharCase parseValue(String text) {
    return null;
  }

  @Override
  public String formatValue(CharCase value) {
    return String.valueOf(value);
  }

  @Override
  public boolean parseSupported() {
    return false;
  }

  @Override
  public AbstractValueModel<CharCase> clone() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
