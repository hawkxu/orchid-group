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
public class FileRangeModel extends AbstractRangeModel<File>
        implements Serializable {

  private FileToStringConverter converter;

  public FileRangeModel() {
    this(true, true, null);
  }

  public FileRangeModel(RangeList<File> wholeRanges) {
    this(true, true, wholeRanges);
  }

  public FileRangeModel(boolean multipleRange, boolean rangeInterval,
          RangeList<File> wholeRanges) {
    this(multipleRange, rangeInterval, wholeRanges, null, null);
  }

  public FileRangeModel(boolean multipleRange, boolean rangeInterval,
          RangeList<File> wholeRanges,
          String defaultMask, CharCase defaultCase) {
    super(multipleRange, rangeInterval, wholeRanges, defaultMask, defaultCase);
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
    return true;
  }

  @Override
  public FileRangeModel clone() {
    return new FileRangeModel(multipleRange, rangeInterval,
            wholeRanges, defaultMask, defaultCase);
  }
}
