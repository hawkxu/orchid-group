/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.CharCase;
import com.xzq.osc.JocInputMask;
import com.xzq.osc.OrchidUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.event.EventListenerList;

/**
 *
 * @author zqxu
 */
public abstract class AbstractFieldModel<V> implements FieldModel<V>,
        Serializable {

  protected String defaultMask;
  protected CharCase defaultCase;
  protected EventListenerList listenerList;

  public AbstractFieldModel() {
    this(null, null);
  }

  public AbstractFieldModel(String defaultMask, CharCase defaultCase) {
    initializeLocalVars();
    setDefaultMask(defaultMask);
    setDefaultCase(defaultCase);
  }

  /**
   * initialize local variables.
   */
  protected void initializeLocalVars() {
    listenerList = new EventListenerList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDefaultMask() {
    return defaultMask;
  }

  /**
   * Sets default input mask.
   *
   * @param defaultMask default input mask.
   */
  public void setDefaultMask(String defaultMask) {
    if (!OrchidUtils.equals(defaultMask, this.defaultMask)) {
      //validate mask
      JocInputMask mask = new JocInputMask();
      mask.setMask(defaultMask);
      String old = this.defaultMask;
      this.defaultMask = defaultMask;
      firePropertyChange("defaultMask", old, defaultMask);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CharCase getDefaultCase() {
    return defaultCase;
  }

  /**
   * Set default char case.
   *
   * @param defaultCase char case.
   */
  public void setDefaultCase(CharCase defaultCase) {
    if (!OrchidUtils.equals(defaultCase, this.defaultCase)) {
      CharCase old = this.defaultCase;
      this.defaultCase = defaultCase;
      firePropertyChange("defaultCase", old, defaultCase);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPropertyChangeListener(PropertyChangeListener l) {
    listenerList.add(PropertyChangeListener.class, l);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removePropertyChangeListener(PropertyChangeListener l) {
    listenerList.remove(PropertyChangeListener.class, l);
  }

  /**
   * Returns all installed property change listeners.
   *
   * @return
   */
  protected PropertyChangeListener[] getPropertyChangeListeners() {
    return listenerList.getListeners(PropertyChangeListener.class);
  }

  /**
   * fire property change event.
   *
   * @param propertyName property name
   * @param oldValue old value
   * @param newValue new value
   */
  protected void firePropertyChange(String propertyName,
          Object oldValue, Object newValue) {
    PropertyChangeEvent evt = new PropertyChangeEvent(
            this, propertyName, oldValue, newValue);
    for (PropertyChangeListener l : getPropertyChangeListeners()) {
      l.propertyChange(evt);
    }
  }
}
