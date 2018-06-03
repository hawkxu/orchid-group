/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import com.xzq.osc.OrchidUtils;
import com.xzq.osc.editors.GenericCode.CodeString;
import com.xzq.osc.field.DateValueModel;
import com.xzq.osc.field.FileValueModel;
import com.xzq.osc.field.GenericValueModel;
import com.xzq.osc.field.NumberValueModel;
import com.xzq.osc.field.StringValueModel;
import com.xzq.osc.field.TimeValueModel;
import com.xzq.osc.field.ValueModel;

/**
 *
 * @author zqxu
 */
public class ValueModelCode {

  public static String getInitCode(ValueModel model) {
    Class vClass = model == null ? null : model.getClass();
    if (vClass == null) {
      return "null";
    } else if (vClass == StringValueModel.class) {
      return getInitCode((StringValueModel) model);
    } else if (vClass == TimeValueModel.class) {
      return getInitCode((TimeValueModel) model);
    } else if (vClass == DateValueModel.class) {
      return getInitCode((DateValueModel) model);
    } else if (vClass == NumberValueModel.class) {
      return getInitCode((NumberValueModel) model);
    } else if (vClass == FileValueModel.class) {
      return getInitCode((FileValueModel) model);
    } else if (vClass == GenericValueModel.class) {
      return getInitCode((GenericValueModel) model);
    }
    throw new IllegalArgumentException(
            "Not support model type:" + vClass.getName());
  }

  private static String getInitCode(StringValueModel model) {
    if (isDefaultAsInit(model, new StringValueModel())) {
      return GenericCode.getInitCode(StringValueModel.class,
              model.isAutoTrim(), model.getValue());
    } else {
      return GenericCode.getInitCode(StringValueModel.class,
              model.isAutoTrim(), model.getValue(),
              model.getDefaultMask(), model.getDefaultCase());
    }
  }

  private static String getInitCode(TimeValueModel model) {
    if (isDefaultAsInit(model, new TimeValueModel())) {
      return GenericCode.getInitCode(TimeValueModel.class,
              model.getPattern(), model.getValue());
    } else {
      return GenericCode.getInitCode(TimeValueModel.class,
              model.getPattern(), model.getValue(),
              model.getDefaultMask(), model.getDefaultCase());
    }
  }

  private static String getInitCode(DateValueModel model) {
    if (isDefaultAsInit(model, new DateValueModel())) {
      return GenericCode.getInitCode(DateValueModel.class,
              model.getPattern(), model.getValue());
    } else {
      return GenericCode.getInitCode(DateValueModel.class,
              model.getPattern(), model.getValue(),
              model.getDefaultMask(), model.getDefaultCase());
    }
  }

  private static String getInitCode(NumberValueModel model) {
    CodeString typeCode = new CodeString(
            GenericCode.getClassCode(model.getNumberType()) + ".class");
    if (isDefaultAsInit(model, new NumberValueModel())) {
      return GenericCode.getInitCode(NumberValueModel.class,
              typeCode, model.getPattern(), model.getValue());
    } else {
      return GenericCode.getInitCode(NumberValueModel.class,
              typeCode, model.getPattern(), model.getValue(),
              model.getDefaultMask(), model.getDefaultCase());
    }
  }

  private static String getInitCode(FileValueModel model) {
    if (isDefaultAsInit(model, new FileValueModel())) {
      return GenericCode.getInitCode(FileValueModel.class, model.getValue());
    } else {
      return GenericCode.getInitCode(FileValueModel.class,
              model.getValue(), model.getDefaultMask(), model.getDefaultCase());
    }
  }

  private static String getInitCode(GenericValueModel model) {
    if (isDefaultAsInit(model, new GenericValueModel())) {
      return GenericCode.getInitCode(GenericValueModel.class, model.getValue());
    } else {
      return GenericCode.getInitCode(GenericValueModel.class,
              model.getValue(), model.getDefaultMask(), model.getDefaultCase());
    }
  }

  private static boolean isDefaultAsInit(ValueModel model, ValueModel init) {
    return OrchidUtils.equals(model.getDefaultMask(), init.getDefaultMask())
            && OrchidUtils.equals(model.getDefaultCase(), init.getDefaultCase());
  }
}
