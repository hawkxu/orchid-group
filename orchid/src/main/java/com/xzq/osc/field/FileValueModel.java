/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.CharCase;
import java.io.File;
import java.io.Serializable;

/**
 *
 * @author zqxu
 */
public class FileValueModel extends AbstractValueModel<File>
        implements Serializable {

  private FileToStringConverter converter;

  public FileValueModel() {
    this(null, null, null);
  }

  public FileValueModel(File value) {
    this(value, null, null);
  }

  public FileValueModel(File value,
          String defaultMask, CharCase defaultCase) {
    super(value, defaultMask, defaultCase);
    converter = new FileToStringConverter();
  }

  @Override
  public Class<? extends File> getValueClass() {
    return File.class;
  }

  @Override
  public File parseValue(String text) {
    return converter.convertReverse(text);
  }

  @Override
  public String formatValue(File value) {
    return converter.convertForward(value);
  }

  @Override
  public boolean parseSupported() {
    return converter.reverseSupported();
  }

  @Override
  public FileValueModel clone() {
    return new FileValueModel(value, defaultMask, defaultCase);
  }
}
