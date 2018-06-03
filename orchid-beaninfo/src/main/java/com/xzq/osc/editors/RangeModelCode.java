/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import com.xzq.osc.OrchidUtils;
import com.xzq.osc.editors.GenericCode.CodeString;
import com.xzq.osc.field.DateRangeModel;
import com.xzq.osc.field.FileRangeModel;
import com.xzq.osc.field.GenericRangeModel;
import com.xzq.osc.field.NumberRangeModel;
import com.xzq.osc.field.Range;
import com.xzq.osc.field.RangeList;
import com.xzq.osc.field.RangeModel;
import com.xzq.osc.field.StringRangeModel;
import com.xzq.osc.field.TimeRangeModel;

/**
 *
 * @author zqxu
 */
public class RangeModelCode {

  public static String getInitCode(RangeModel model) {
    Class vClass = model == null ? null : model.getClass();
    if (vClass == null) {
      return "null";
    } else if (vClass == StringRangeModel.class) {
      return getInitCode((StringRangeModel) model);
    } else if (vClass == TimeRangeModel.class) {
      return getInitCode((TimeRangeModel) model);
    } else if (vClass == DateRangeModel.class) {
      return getInitCode((DateRangeModel) model);
    } else if (vClass == NumberRangeModel.class) {
      return getInitCode((NumberRangeModel) model);
    } else if (vClass == FileRangeModel.class) {
      return getInitCode((FileRangeModel) model);
    } else if (vClass == GenericRangeModel.class) {
      return getInitCode((GenericRangeModel) model);
    }
    throw new IllegalArgumentException(
            "Not support model type:" + vClass.getName());
  }

  private static String getInitCode(StringRangeModel model) {
    StringRangeModel init = new StringRangeModel();
    RangeList rangeList = ((RangeModel) model).getWholeRanges();
    CodeString rgCode = getInitCode(rangeList, model.getValueClass());
    if (!isDefaultAsInit(model, init)) {
      return GenericCode.getInitCode(StringRangeModel.class,
              model.isAutoTrim(), model.getMultipleRange(),
              model.getRangeInterval(), rgCode,
              model.getDefaultMask(), model.getDefaultCase());
    } else if (isRangeOpAsInit(model, init)) {
      return GenericCode.getInitCode(StringRangeModel.class,
              model.isAutoTrim(), rgCode);
    } else {
      return GenericCode.getInitCode(StringRangeModel.class,
              model.isAutoTrim(), model.getMultipleRange(),
              model.getRangeInterval(), rgCode);
    }
  }

  private static String getInitCode(TimeRangeModel model) {
    TimeRangeModel init = new TimeRangeModel();
    RangeList rangeList = ((RangeModel) model).getWholeRanges();
    CodeString rgCode = getInitCode(rangeList, model.getValueClass());
    if (!isDefaultAsInit(model, init)) {
      return GenericCode.getInitCode(TimeRangeModel.class,
              model.getPattern(), model.getMultipleRange(),
              model.getRangeInterval(), rgCode,
              model.getDefaultMask(), model.getDefaultCase());
    } else if (isRangeOpAsInit(model, init)) {
      return GenericCode.getInitCode(TimeRangeModel.class,
              model.getPattern(), rgCode);
    } else {
      return GenericCode.getInitCode(TimeRangeModel.class,
              model.getPattern(), model.getMultipleRange(),
              model.getRangeInterval(), rgCode);
    }
  }

  private static String getInitCode(DateRangeModel model) {
    DateRangeModel init = new DateRangeModel();
    RangeList rangeList = ((RangeModel) model).getWholeRanges();
    CodeString rgCode = getInitCode(rangeList, model.getValueClass());
    if (!isDefaultAsInit(model, init)) {
      return GenericCode.getInitCode(DateRangeModel.class,
              model.getPattern(), model.getMultipleRange(),
              model.getRangeInterval(), rgCode,
              model.getDefaultMask(), model.getDefaultCase());
    } else if (isRangeOpAsInit(model, init)) {
      return GenericCode.getInitCode(DateRangeModel.class,
              model.getPattern(), rgCode);
    } else {
      return GenericCode.getInitCode(DateRangeModel.class,
              model.getPattern(), model.getMultipleRange(),
              model.getRangeInterval(), rgCode);
    }
  }

  private static String getInitCode(NumberRangeModel model) {
    NumberRangeModel init = new NumberRangeModel();
    RangeList rangeList = ((RangeModel) model).getWholeRanges();
    CodeString rgCode = getInitCode(rangeList, model.getValueClass());
    CodeString typeCode = new CodeString(
            model.getNumberType().getName() + ".class");
    if (!isDefaultAsInit(model, init)) {
      return GenericCode.getInitCode(NumberRangeModel.class,
              typeCode, model.getPattern(),
              model.getMultipleRange(),
              model.getRangeInterval(), rgCode,
              model.getDefaultMask(), model.getDefaultCase());
    } else if (isRangeOpAsInit(model, init)) {
      return GenericCode.getInitCode(NumberRangeModel.class,
              typeCode, model.getPattern(), rgCode);
    } else {
      return GenericCode.getInitCode(NumberRangeModel.class,
              typeCode, model.getPattern(),
              model.getMultipleRange(), model.getRangeInterval(), rgCode);
    }
  }

  private static String getInitCode(FileRangeModel model) {
    FileRangeModel init = new FileRangeModel();
    RangeList rangeList = ((RangeModel) model).getWholeRanges();
    CodeString rgCode = getInitCode(rangeList, model.getValueClass());
    if (!isDefaultAsInit(model, init)) {
      return GenericCode.getInitCode(FileRangeModel.class,
              model.getMultipleRange(), model.getRangeInterval(),
              rgCode, model.getDefaultMask(), model.getDefaultCase());
    } else if (isRangeOpAsInit(model, init)) {
      return GenericCode.getInitCode(FileRangeModel.class, rgCode);
    } else {
      return GenericCode.getInitCode(FileRangeModel.class,
              model.getMultipleRange(), model.getRangeInterval(), rgCode);
    }
  }

  private static String getInitCode(GenericRangeModel model) {
    GenericRangeModel init = new GenericRangeModel();
    RangeList rangeList = ((RangeModel) model).getWholeRanges();
    CodeString rgCode = getInitCode(rangeList, model.getValueClass());
    if (!isDefaultAsInit(model, init)) {
      return GenericCode.getInitCode(GenericRangeModel.class,
              model.getMultipleRange(), model.getRangeInterval(),
              rgCode, model.getDefaultMask(), model.getDefaultCase());
    } else if (isRangeOpAsInit(model, init)) {
      return GenericCode.getInitCode(GenericRangeModel.class, rgCode);
    } else {
      return GenericCode.getInitCode(GenericRangeModel.class,
              model.getMultipleRange(), model.getRangeInterval(), rgCode);
    }
  }

  @SuppressWarnings("unchecked")
  private static CodeString getInitCode(RangeList rangeList,
          Class valueClass) {
    if (rangeList == null || rangeList.isEmpty()) {
      return null;
    }
    StringBuilder codeBuilder = new StringBuilder();
    codeBuilder.append(RangeList.class.getName()).append(".asList(\n");
    int count = rangeList.size();
    for (int i = 0; i < count; i++) {
      Range range = ((RangeList<Object>) rangeList).get(i);
      codeBuilder.append("        ").
              append(getInitCode(range, valueClass));
      if (i < count - 1) {
        codeBuilder.append(",\n");
      }
    }
    return new CodeString(codeBuilder.append(")").toString());
  }

  private static String getInitCode(Range range, Class valueClass) {
    if (range == null) {
      return "null";
    }
    return GenericCode.getGenericCode(Range.class, valueClass,
            range.getSign(), range.getOption(),
            range.getLowValue(), range.getHighValue());
  }

  private static boolean isDefaultAsInit(RangeModel model, RangeModel init) {
    return OrchidUtils.equals(model.getDefaultMask(), init.getDefaultMask())
            && OrchidUtils.equals(model.getDefaultCase(), init.getDefaultCase());
  }

  private static boolean isRangeOpAsInit(RangeModel model, RangeModel init) {
    return model.getMultipleRange() == init.getMultipleRange()
            && model.getRangeInterval() == init.getRangeInterval();
  }
}
