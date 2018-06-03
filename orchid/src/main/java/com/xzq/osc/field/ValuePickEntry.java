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
public class ValuePickEntry {

  private Object key;
  private String text;

  public ValuePickEntry(Object key) {
    this(key, key == null ? "" : key.toString());
  }

  public ValuePickEntry(Object key, String text) {
    this.key = key;
    this.text = text;
  }

  public Object getKey() {
    return key;
  }

  public String getText() {
    return text;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof ValuePickEntry)
            && OrchidUtils.equals(key, ((ValuePickEntry) obj).key);
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 71 * hash + (this.key != null ? this.key.hashCode() : 0);
    return hash;
  }
}
