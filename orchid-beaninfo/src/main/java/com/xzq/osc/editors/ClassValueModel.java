/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import com.xzq.osc.field.AbstractValueModel;

/**
 *
 * @author zqxu
 */
public class ClassValueModel extends AbstractValueModel<Class> {

  public ClassValueModel() {
    super(null, null, null);
  }

  @Override
  public Class<? extends Class> getValueClass() {
    return Class.class;
  }

  @Override
  public Class parseValue(String text) {
    return null;
  }

  @Override
  public String formatValue(Class value) {
    return value == null ? "null" : value.getName();
  }

  @Override
  public boolean parseSupported() {
    return false;
  }

  @Override
  public AbstractValueModel<Class> clone() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
