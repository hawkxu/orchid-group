/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

/**
 * Converter interface
 *
 * @author zqxu
 */
public interface Converter<S extends Object, T extends Object> {

  /**
   * Converts a value from the source type to the target type. Should throws a
   * RuntimeException to indicate a problem with the conversion.
   *
   * @param value the source value to convert
   * @return the value, converted to the target type
   */
  public T convertForward(S value) throws RuntimeException;

  /**
   * Converts a value from the target type to the source type. Should throws a
   * RuntimeException to indicate a problem with the conversion.
   *
   * @param value the target value to convert
   * @return the value, converted to the source type.
   */
  public S convertReverse(T value) throws RuntimeException;

  /**
   * If converter support reverse convert, return true, otherwise return false.
   *
   * @return return true indicate the converter support reverse convert, else
   * fase.
   */
  public boolean reverseSupported();
}