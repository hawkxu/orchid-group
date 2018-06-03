/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import javax.swing.ListSelectionModel;

/**
 *
 * @author zqxu
 */
public class JocTableBeanInfo extends OrchidBaseBeanInfo {

  public JocTableBeanInfo() {
    super(JocTable.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, new String[]{
              "editable", "fixedColumnCount",
              "fixedRowCount", "tableLayoutModel"});
    EnumerationValue[] rowSelectionModes = new EnumerationValue[]{
      new EnumerationValue("MULTIPLE_INTERVAL_SELECTION",
      ListSelectionModel.MULTIPLE_INTERVAL_SELECTION,
      "javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION"),
      new EnumerationValue("SINGLE_INTERVAL_SELECTION",
      ListSelectionModel.SINGLE_INTERVAL_SELECTION,
      "javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION"),
      new EnumerationValue("SINGLE_SELECTION",
      ListSelectionModel.SINGLE_SELECTION,
      "javax.swing.ListSelectionModel.SINGLE_SELECTION"),};
    setEnumerationValues(rowSelectionModes, "rowSelectionMode");
  }
}
