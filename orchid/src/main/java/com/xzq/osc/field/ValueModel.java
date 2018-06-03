/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

/**
 *
 * @author zqxu
 */
public interface ValueModel<V> extends FieldModel<V>, Cloneable {

  /**
   * Returns current value of model.
   *
   * @return current value.
   */
  public V getValue();

  /**
   * Sets current value of model.
   *
   * @param value current model.
   */
  public void setValue(V value);

  /**
   * clone model.
   *
   * @return new instance of model.
   */
  public ValueModel<V> clone();
}
