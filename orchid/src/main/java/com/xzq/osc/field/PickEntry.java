/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.OrchidUtils;

/**
 *
 * @author zqxu
 */
public class PickEntry {

  private Object value;
  private String description;

  /**
   *
   * @param value
   */
  public PickEntry(Object value) {
    this(value, null);
  }

  /**
   *
   * @param value
   * @param description
   */
  public PickEntry(Object value, String description) {
    this.value = value;
    this.description = description;
  }

  /**
   *
   * @return
   */
  public Object getValue() {
    return value;
  }

  /**
   *
   * @return
   */
  public String getDescription() {
    return description;
  }

  /**
   *
   * @param description
   */
  public void setDescription(String description) {
    this.description = description == null ? "" : description;
  }

  /**
   *
   * @param obj
   * @return
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof PickEntry
            && OrchidUtils.equals(value, ((PickEntry) obj).value);
  }

  /**
   *
   * @return
   */
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 83 * hash + (this.value != null ? this.value.hashCode() : 0);
    return hash;
  }
}