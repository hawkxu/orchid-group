/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.CharCase;
import java.beans.PropertyChangeListener;

/**
 *
 * @author zqxu
 */
public interface FieldModel<V> {

  /**
   * Returns actual value class of model.
   *
   * @return actual value class of model.
   */
  public Class<? extends V> getValueClass();

  /**
   * convert text to value.
   *
   * @param text text
   * @return value
   */
  public V parseValue(String text) throws IllegalArgumentException;

  /**
   * convert value to text.
   *
   * @param value value
   * @return text.
   */
  public String formatValue(V value);

  /**
   * Returns true for support convert value from text or false not.
   *
   * @return true or false.
   */
  public boolean parseSupported();

  /**
   * Returns default input mask of model
   *
   * @return default input mask.
   */
  public String getDefaultMask();

  /**
   * Returns default char case of model.
   *
   * @return default char case.
   */
  public CharCase getDefaultCase();

  /**
   * install property change listener to model.
   *
   * @param l property change listener.
   */
  public void addPropertyChangeListener(PropertyChangeListener l);

  /**
   * remove property change listener from model.
   *
   * @param l property change listener.
   */
  public void removePropertyChangeListener(PropertyChangeListener l);
}
