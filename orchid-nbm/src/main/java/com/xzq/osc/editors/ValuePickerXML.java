/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import com.xzq.osc.field.ListValuePicker;
import com.xzq.osc.field.ValuePickEntry;
import com.xzq.osc.field.ValuePicker;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author zqxu
 */
public class ValuePickerXML {

  public static ValuePicker readFromXML(Node node) {
    String typeName = getNodeValue(node.getAttributes(), "type");
    Class pickerType = null;
    try {
      pickerType = Class.forName(typeName);
    } catch (ClassNotFoundException ex) {
    }
    if (pickerType == ListValuePicker.class) {
      return readListValuePicker(node);
    }
    return null;
  }

  private static ValuePicker readListValuePicker(Node pickerNode) {
    try {
      int displayFormat = Integer.parseInt(
              getNodeValue(pickerNode.getAttributes(), "format"));
      Class<?> keyClass = Class.forName(
              getNodeValue(pickerNode.getAttributes(), "keyClass"));
      List<ValuePickEntry> pickList = new ArrayList<ValuePickEntry>();
      NodeList nodeList = pickerNode.getChildNodes();
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node entryNode = nodeList.item(i);
        if (entryNode.getNodeType() == Node.ELEMENT_NODE) {
          pickList.add(readValuePickEntry(entryNode, keyClass));
        }
      }
      return new ListValuePicker(displayFormat, pickList);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException("error reading ListValuePicker.", ex);
    }
  }

  private static ValuePickEntry readValuePickEntry(Node entryNode,
          Class<?> keyClass) {
    NamedNodeMap attributes = entryNode.getAttributes();
    Object keyValue = GenericXML.parseValue(
            getNodeValue(attributes, "key"), keyClass);
    String displayText = GenericXML.parseValue(
            getNodeValue(attributes, "text"), String.class);
    return new ValuePickEntry(keyValue, displayText);
  }

  private static String getNodeValue(NamedNodeMap attributes, String name) {
    Node valueNode = attributes.getNamedItem(name);
    return valueNode == null ? null : valueNode.getNodeValue();
  }

  public static Node storeToXML(Document dcmnt, ValuePicker picker) {
    if (picker instanceof ListValuePicker) {
      ListValuePicker listPicker = (ListValuePicker) picker;
      List<ValuePickEntry> valuePickList = listPicker.getValuePickList();
      if (valuePickList == null || valuePickList.isEmpty()) {
        return null;
      }
      Element pickerNode = dcmnt.createElement("valuePicker");
      pickerNode.setAttribute("type", listPicker.getClass().getName());
      pickerNode.setAttribute("format",
              String.valueOf(listPicker.getDisplayFormat()));
      pickerNode.setAttribute("keyClass",
              ValuePickerCode.getKeyValueClass(valuePickList).getName());
      for (ValuePickEntry entry : valuePickList) {
        Element entryNode = dcmnt.createElement("entry");
        entryNode.setAttribute("key",
                GenericXML.getAsString(entry.getKey()));
        entryNode.setAttribute("text",
                GenericXML.getAsString(entry.getText()));
        pickerNode.appendChild(entryNode);
      }
      return pickerNode;
    }
    return null;
  }
}
