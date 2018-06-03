/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.tree.CheckedTreeSelectionModel;

/**
 *
 * @author zqxu
 */
public class JocCheckedTreeBeanInfo extends OrchidBaseBeanInfo {

  public JocCheckedTreeBeanInfo() {
    super(JocCheckedTree.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "entireRowMode", "branchSelection", "branchThreeStates",
            "extendAddSelection", "extendRemoveSelection");
    EnumerationValue[] branchSelections = new EnumerationValue[]{
      new EnumerationValue("BRANCH_SELF",
      CheckedTreeSelectionModel.BRANCH_SELF,
      "com.xzq.osc.tree.CheckedTreeSelectionModel.BRANCH_SELF"),
      new EnumerationValue("MIX_CHILDREN",
      CheckedTreeSelectionModel.MIX_CHILDREN,
      "com.xzq.osc.tree.CheckedTreeSelectionModel.MIX_CHILDREN"),
      new EnumerationValue("RELY_CHILDREN",
      CheckedTreeSelectionModel.RELY_CHILDREN,
      "com.xzq.osc.tree.CheckedTreeSelectionModel.RELY_CHILDREN")
    };
    setEnumerationValues(branchSelections, "branchSelection");
  }
}
