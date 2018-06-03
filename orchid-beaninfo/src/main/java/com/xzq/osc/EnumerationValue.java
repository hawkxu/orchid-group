/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public final class EnumerationValue {

  private String name;
  private Object value;
  private String javaInitializationString;

  public EnumerationValue(String name, Object value, String javaInitString) {
    this.name = name;
    this.value = value;
    this.javaInitializationString = javaInitString;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return this.name;
  }

  public Object getValue() {
    return this.value;
  }

  public String getJavaInitializationString() {
    return this.javaInitializationString;
  }
}
