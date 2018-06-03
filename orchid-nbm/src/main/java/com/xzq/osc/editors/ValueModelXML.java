/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import com.xzq.osc.CharCase;
import com.xzq.osc.OrchidUtils;
import com.xzq.osc.field.AbstractValueModel;
import com.xzq.osc.field.DateValueModel;
import com.xzq.osc.field.FileValueModel;
import com.xzq.osc.field.GenericValueModel;
import com.xzq.osc.field.NumberValueModel;
import com.xzq.osc.field.StringValueModel;
import com.xzq.osc.field.TimeValueModel;
import com.xzq.osc.field.ValueModel;
import java.io.File;
import java.util.Date;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author zqxu
 */
public class ValueModelXML {

  public static ValueModel readFromXML(Node node) {
    NamedNodeMap attributes = node.getAttributes();
    Node typeNode = attributes.getNamedItem("type");
    Class modelType = null;
    try {
      modelType = Class.forName(typeNode.getNodeValue());
    } catch (ClassNotFoundException ex) {
    }
    if (modelType == StringValueModel.class) {
      return readStringValueModel(attributes);
    } else if (modelType == TimeValueModel.class) {
      return readTimeValueModel(attributes);
    } else if (modelType == DateValueModel.class) {
      return readDateValueModel(attributes);
    } else if (modelType == NumberValueModel.class) {
      return readNumberValueModel(attributes);
    } else if (modelType == FileValueModel.class) {
      return readFileValueModel(attributes);
    } else if (modelType == GenericValueModel.class) {
      return readGenericValueModel(attributes);
    }
    return null;
  }

  private static ValueModel readStringValueModel(NamedNodeMap attributes) {
    StringValueModel model = new StringValueModel();
    String trimText = getNodeValue(attributes, "autoTrim");
    model.setAutoTrim(Boolean.valueOf(trimText));
    model.setValue(getNodeValue(attributes, "value"));
    readValueModelDefault(attributes, model);
    return model;
  }

  private static ValueModel readTimeValueModel(NamedNodeMap attributes) {
    TimeValueModel model = new TimeValueModel();
    model.setPattern(getNodeValue(attributes, "pattern"));
    String valueText = getNodeValue(attributes, "value");
    model.setValue(GenericXML.parseValue(valueText, Date.class));
    readValueModelDefault(attributes, model);
    return model;
  }

  private static ValueModel readDateValueModel(NamedNodeMap attributes) {
    DateValueModel model = new DateValueModel();
    model.setPattern(getNodeValue(attributes, "pattern"));
    String valueText = getNodeValue(attributes, "value");
    model.setValue(GenericXML.parseValue(valueText, Date.class));
    readValueModelDefault(attributes, model);
    return model;
  }

  @SuppressWarnings("unchecked")
  private static ValueModel readNumberValueModel(NamedNodeMap attributes) {
    NumberValueModel model = new NumberValueModel();
    String typeText = getNodeValue(attributes, "numberType");
    try {
      model.setNumberType((Class<? extends Number>) Class.forName(typeText));
    } catch (Exception ex) {
      // ignore exception
    }
    model.setPattern(getNodeValue(attributes, "pattern"));
    String valueText = getNodeValue(attributes, "value");
    model.setValue(GenericXML.parseValue(valueText, model.getNumberType()));
    readValueModelDefault(attributes, model);
    return model;
  }

  private static ValueModel readFileValueModel(NamedNodeMap attributes) {
    FileValueModel model = new FileValueModel();
    String valueText = getNodeValue(attributes, "value");
    model.setValue(GenericXML.parseValue(valueText, File.class));
    readValueModelDefault(attributes, model);
    return model;
  }

  private static ValueModel readGenericValueModel(NamedNodeMap attributes) {
    GenericValueModel model = new GenericValueModel();
    readValueModelDefault(attributes, model);
    return model;
  }

  private static void readValueModelDefault(NamedNodeMap attributes,
          AbstractValueModel model) {
    String maskText = getNodeValue(attributes, "defaultMask");
    if (maskText != null) {
      model.setDefaultMask(maskText);
    }
    String caseText = getNodeValue(attributes, "defaultCase");
    model.setDefaultCase(GenericXML.parseValue(caseText, CharCase.class));
  }

  private static String getNodeValue(NamedNodeMap attributes, String name) {
    Node valueNode = attributes.getNamedItem(name);
    return valueNode == null ? null : valueNode.getNodeValue();
  }

  public static Node storeToXML(Document dcmnt, ValueModel model) {
    Class vClass = model.getClass();
    Element element = dcmnt.createElement("model");
    element.setAttribute("type", vClass.getName());
    if (vClass == StringValueModel.class) {
      storeToXML(element, (StringValueModel) model);
    } else if (vClass == TimeValueModel.class) {
      storeToXML(element, (TimeValueModel) model);
    } else if (vClass == DateValueModel.class) {
      storeToXML(element, (DateValueModel) model);
    } else if (vClass == NumberValueModel.class) {
      storeToXML(element, (NumberValueModel) model);
    } else if (vClass == FileValueModel.class) {
      storeToXML(element, (FileValueModel) model);
    } else if (vClass == GenericValueModel.class) {
      storeToXML(element, (GenericValueModel) model);
    } else {
      element = null;
    }
    return element;
  }

  private static void storeToXML(Element node, StringValueModel model) {
    node.setAttribute("autoTrim", "" + model.isAutoTrim());
    node.setAttribute("value", model.getValue());
    storeModelDefault(node, model, new StringValueModel());
  }

  private static void storeToXML(Element node, TimeValueModel model) {
    node.setAttribute("pattern", model.getPattern());
    node.setAttribute("value", GenericXML.getAsString(model.getValue()));
    storeModelDefault(node, model, new TimeValueModel());
  }

  private static void storeToXML(Element node, DateValueModel model) {
    node.setAttribute("pattern", model.getPattern());
    node.setAttribute("value", GenericXML.getAsString(model.getValue()));
    storeModelDefault(node, model, new DateValueModel());
  }

  private static void storeToXML(Element node, NumberValueModel model) {
    node.setAttribute("numberType", model.getNumberType().getName());
    node.setAttribute("pattern", model.getPattern());
    node.setAttribute("value", GenericXML.getAsString(model.getValue()));
    storeModelDefault(node, model, new NumberValueModel());
  }

  private static void storeToXML(Element node, FileValueModel model) {
    node.setAttribute("value", GenericXML.getAsString(model.getValue()));
    storeModelDefault(node, model, new StringValueModel());
  }

  private static void storeToXML(Element node, GenericValueModel model) {
    storeModelDefault(node, model, new StringValueModel());
  }

  private static void storeModelDefault(Element node, ValueModel model,
          ValueModel init) {
    if (!OrchidUtils.equals(model.getDefaultMask(), init.getDefaultMask())) {
      node.setAttribute("defaultMask",
              GenericXML.getAsString(model.getDefaultMask()));
    }
    if (!OrchidUtils.equals(model.getDefaultCase(), init.getDefaultCase())) {
      node.setAttribute("defaultCase",
              GenericXML.getAsString(model.getDefaultCase()));
    }
  }
}
