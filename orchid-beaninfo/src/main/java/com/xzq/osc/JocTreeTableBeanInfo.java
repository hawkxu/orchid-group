/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocTreeTableBeanInfo extends OrchidBaseBeanInfo {

  public JocTreeTableBeanInfo() {
    super(JocTreeTable.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setHidden(true, new String[]{"componentOrientation",
              "autoCreateRowSorter", "rowSorter"});
    setPreferred(false, "model");
    setPreferred(true, new String[]{
              "treeTableModel", "rootVisible", "showsRootHandles"});
  }
}
