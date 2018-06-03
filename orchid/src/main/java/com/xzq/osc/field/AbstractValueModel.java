/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.CharCase;
import com.xzq.osc.OrchidUtils;
import java.io.Serializable;

/**
 *
 * @author zqxu
 */
public abstract class AbstractValueModel<V> extends AbstractFieldModel<V>
        implements ValueModel<V>, Serializable {

  protected V value;

  public AbstractValueModel() {
    this(null, null, null);
  }

  public AbstractValueModel(V value, String defaultMask,
          CharCase defaultCase) {
    super(defaultMask, defaultCase);
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V getValue() {
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValue(V value) {
    if (!OrchidUtils.equals(value, this.value)) {
      V old = this.value;
      this.value = value;
      firePropertyChange("value", old, value);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract AbstractValueModel<V> clone();
}
