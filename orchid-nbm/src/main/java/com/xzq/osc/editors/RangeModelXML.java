/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import com.xzq.osc.CharCase;
import com.xzq.osc.OrchidUtils;
import com.xzq.osc.field.AbstractRangeModel;
import com.xzq.osc.field.DateRangeModel;
import com.xzq.osc.field.FileRangeModel;
import com.xzq.osc.field.GenericRangeModel;
import com.xzq.osc.field.NumberRangeModel;
import com.xzq.osc.field.Range;
import com.xzq.osc.field.Range.Option;
import com.xzq.osc.field.Range.Sign;
import com.xzq.osc.field.RangeList;
import com.xzq.osc.field.RangeModel;
import com.xzq.osc.field.StringRangeModel;
import com.xzq.osc.field.TimeRangeModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author zqxu
 */
public class RangeModelXML {

  public static RangeModel readFromXML(Node node) {
    NamedNodeMap attributes = node.getAttributes();
    Node typeNode = attributes.getNamedItem("type");
    Class modelType = null;
    try {
      modelType = Class.forName(typeNode.getNodeValue());
    } catch (ClassNotFoundException ex) {
    }
    RangeModel model;
    if (modelType == StringRangeModel.class) {
      model = readStringRangeModel(attributes);
    } else if (modelType == TimeRangeModel.class) {
      model = readTimeRangeModel(attributes);
    } else if (modelType == DateRangeModel.class) {
      model = readDateRangeModel(attributes);
    } else if (modelType == NumberRangeModel.class) {
      model = readNumberRangeModel(attributes);
    } else if (modelType == FileRangeModel.class) {
      model = readFileRangeModel(attributes);
    } else if (modelType == GenericRangeModel.class) {
      model = readGenericRangeModel(attributes);
    } else {
      return null;
    }
    readWholeRanges(node, (AbstractRangeModel) model);
    readRangeModelDefault(attributes, (AbstractRangeModel) model);
    return model;
  }

  private static RangeModel readStringRangeModel(NamedNodeMap attributes) {
    StringRangeModel model = new StringRangeModel();
    String trimText = getNodeValue(attributes, "autoTrim");
    model.setAutoTrim(Boolean.valueOf(trimText));
    return model;
  }

  private static RangeModel readTimeRangeModel(NamedNodeMap attributes) {
    TimeRangeModel model = new TimeRangeModel();
    model.setPattern(getNodeValue(attributes, "pattern"));
    return model;
  }

  private static RangeModel readDateRangeModel(NamedNodeMap attributes) {
    DateRangeModel model = new DateRangeModel();
    model.setPattern(getNodeValue(attributes, "pattern"));
    return model;
  }

  @SuppressWarnings("unchecked")
  private static RangeModel readNumberRangeModel(NamedNodeMap attributes) {
    NumberRangeModel model = new NumberRangeModel();
    String typeText = getNodeValue(attributes, "numberType");
    try {
      model.setNumberType((Class<? extends Number>) Class.forName(typeText));
    } catch (Exception ex) {
      // ignore exception
    }
    model.setPattern(getNodeValue(attributes, "pattern"));
    return model;
  }

  private static RangeModel readFileRangeModel(NamedNodeMap attributes) {
    return new FileRangeModel();
  }

  private static RangeModel readGenericRangeModel(NamedNodeMap attributes) {
    return new GenericRangeModel();
  }

  @SuppressWarnings("unchecked")
  private static void readWholeRanges(Node modelNode,
          AbstractRangeModel model) {
    RangeList rangeList = new RangeList();
    Class vClass = model.getValueClass();
    NodeList nodeList = modelNode.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node rangeNode = nodeList.item(i);
      if (rangeNode.getNodeType() == Node.ELEMENT_NODE) {
        rangeList.add(readRange(rangeNode, vClass));
      }
    }
    model.setWholeRanges(rangeList);
  }

  @SuppressWarnings("unchecked")
  private static Range readRange(Node rangeNode, Class valueClass) {
    NamedNodeMap attributes = rangeNode.getAttributes();
    Sign sign = GenericXML.parseValue(getNodeValue(attributes, "sign"),
            Sign.class);
    Option option = GenericXML.parseValue(getNodeValue(attributes, "option"),
            Option.class);
    Object low = GenericXML.parseValue(getNodeValue(attributes, "lowValue"),
            valueClass);
    Object high = GenericXML.parseValue(getNodeValue(attributes, "highValue"),
            valueClass);
    return new Range(sign, option, low, high);
  }

  private static void readRangeModelDefault(NamedNodeMap attributes,
          AbstractRangeModel model) {
    String multText = getNodeValue(attributes, "multipleRange");
    if (multText != null) {
      model.setMultipleRange(Boolean.valueOf(multText));
    }
    String intvText = getNodeValue(attributes, "rangeInterval");
    if (intvText != null) {
      model.setRangeInterval(Boolean.valueOf(intvText));
    }
    String maskText = getNodeValue(attributes, "defaultMask");
    if (maskText != null) {
      model.setDefaultMask(maskText);
    }
    String caseText = getNodeValue(attributes, "defaultCase");
    if (caseText != null) {
      model.setDefaultCase(GenericXML.parseValue(caseText, CharCase.class));
    }
  }

  private static String getNodeValue(NamedNodeMap attributes, String name) {
    Node valueNode = attributes.getNamedItem(name);
    return valueNode == null ? null : valueNode.getNodeValue();
  }

  public static Node storeToXML(Document dcmnt, RangeModel model) {
    Class vClass = model.getClass();
    Element element = dcmnt.createElement("model");
    element.setAttribute("type", model.getClass().getName());
    if (vClass == StringRangeModel.class) {
      storeToXML(element, (StringRangeModel) model);
    } else if (vClass == TimeRangeModel.class) {
      storeToXML(element, (TimeRangeModel) model);
    } else if (vClass == DateRangeModel.class) {
      storeToXML(element, (DateRangeModel) model);
    } else if (vClass == NumberRangeModel.class) {
      storeToXML(element, (NumberRangeModel) model);
    } else if (vClass == FileRangeModel.class) {
      storeToXML(element, (FileRangeModel) model);
    } else if (vClass == GenericRangeModel.class) {
      storeToXML(element, (GenericRangeModel) model);
    } else {
      return null;
    }
    storeToXML(dcmnt, element, model.getWholeRanges());
    return element;
  }

  private static void storeToXML(Element node, StringRangeModel model) {
    node.setAttribute("autoTrim", "" + model.isAutoTrim());
    storeModelDefault(node, model, new StringRangeModel());
  }

  private static void storeToXML(Element node, TimeRangeModel model) {
    node.setAttribute("pattern", model.getPattern());
    storeModelDefault(node, model, new TimeRangeModel());
  }

  private static void storeToXML(Element node, DateRangeModel model) {
    node.setAttribute("pattern", model.getPattern());
    storeModelDefault(node, model, new DateRangeModel());
  }

  private static void storeToXML(Element node, NumberRangeModel model) {
    node.setAttribute("numberType", model.getNumberType().getName());
    node.setAttribute("pattern", model.getPattern());
    storeModelDefault(node, model, new NumberRangeModel());
  }

  private static void storeToXML(Element node, FileRangeModel model) {
    storeModelDefault(node, model, new FileRangeModel());
  }

  private static void storeToXML(Element node, GenericRangeModel model) {
    storeModelDefault(node, model, new GenericRangeModel());
  }

  @SuppressWarnings("unchecked")
  private static void storeToXML(Document dcmnt, Element modelNode,
          RangeList wholeRanges) {
    for (Range range : (RangeList<Object>) wholeRanges) {
      Element rangeNode = dcmnt.createElement("range");
      rangeNode.setAttribute("sign",
              GenericXML.getAsString(range.getSign()));
      rangeNode.setAttribute("option",
              GenericXML.getAsString(range.getOption()));
      rangeNode.setAttribute("lowValue",
              GenericXML.getAsString(range.getLowValue()));
      rangeNode.setAttribute("highValue",
              GenericXML.getAsString(range.getHighValue()));
      modelNode.appendChild(rangeNode);
    }
  }

  private static void storeModelDefault(Element node, RangeModel model,
          RangeModel init) {
    if (model.getMultipleRange() != init.getMultipleRange()) {
      node.setAttribute("multipleRange", "" + model.getMultipleRange());
    }
    if (model.getRangeInterval() != init.getRangeInterval()) {
      node.setAttribute("rangeInterval", "" + model.getRangeInterval());
    }
    if (!OrchidUtils.equals(model.getDefaultMask(), init.getDefaultMask())) {
      node.setAttribute("defaultMask", model.getDefaultMask());
    }
    if (!OrchidUtils.equals(model.getDefaultCase(), init.getDefaultCase())) {
      node.setAttribute(
              "defaultCase", GenericXML.getAsString(model.getDefaultCase()));
    }
  }
}
