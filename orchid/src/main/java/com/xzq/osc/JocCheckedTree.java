/**
 * *****************************************************************************
 * 2013, All rights reserved.
 * *****************************************************************************
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.plaf.LookAndFeelManager;
import com.xzq.osc.tree.CheckedTreeSelectionModel;
import javax.swing.ActionMap;
import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Description of JocCheckedTree.
 *
 * @author zqxu
 */
public class JocCheckedTree extends JTree implements OrchidAboutIntf {

  private static final String uiClassID = "OrchidCheckedTreeUI";
  private boolean entireRowMode;
  private boolean pathCollapsing;

  /**
   * The constructor.
   */
  public JocCheckedTree() {
    super();
    updateActionMap();
    setSelectionModel(new CheckedTreeSelectionModel(this));
  }

  @Override
  public String getUIClassID() {
    return uiClassID;
  }

  private void updateActionMap() {
    ActionMap map = getActionMap();
    map.put("addToSelection", map.get("toggleAndAnchor"));
    map.put("scrollRight", map.get("scrollRightChangeLead"));
    map.put("selectNext", map.get("selectNextChangeLead"));
    map.put("selectChild", map.get("selectChildChangeLead"));
    map.put("selectLast", map.get("selectLastChangeLead"));
    map.put("selectPrevious", map.get("selectPreviousChangeLead"));
    map.put("scrollLeft", map.get("scrollLeftChangeLead"));
    map.put("selectFirst", map.get("selectFirstChangeLead"));
    map.put("selectParent", map.get("selectParentChangeLead"));
  }

  /**
   * Returns entireRowMode. When this property set to true, any node will paint
   * and response on entire row. default is false.
   *
   * @return entireRowMode true for entire row mode otherwise false
   */
  public boolean isEntireRowMode() {
    return this.entireRowMode;
  }

  /**
   * Sets a value to property entireRowMode.
   *
   * @param entireRowMode true for entire row mode otherwise false
   */
  public void setEntireRowMode(boolean entireRowMode) {
    if (this.entireRowMode != entireRowMode) {
      this.entireRowMode = entireRowMode;
      repaint();
      firePropertyChange("entireRowMode", !entireRowMode, entireRowMode);
    }
  }

  public int getBranchSelection() {
    if (selectionModel instanceof CheckedTreeSelectionModel) {
      return ((CheckedTreeSelectionModel) selectionModel).getBranchSelection();
    }
    return CheckedTreeSelectionModel.BRANCH_SELF;
  }

  public void setBranchSelection(int branchSelection) {
    ((CheckedTreeSelectionModel) selectionModel)
            .setBranchSelection(branchSelection);
  }

  public boolean isBranchThreeStates() {
    if (selectionModel instanceof CheckedTreeSelectionModel) {
      return ((CheckedTreeSelectionModel) selectionModel).isBranchThreeStates();
    }
    return false;
  }

  public void setBranchThreeStates(boolean branchThreeStates) {
    ((CheckedTreeSelectionModel) selectionModel)
            .setBranchThreeStates(branchThreeStates);
  }

  public boolean isExtendAddSelection() {
    if (selectionModel instanceof CheckedTreeSelectionModel) {
      return ((CheckedTreeSelectionModel) selectionModel)
              .isExtendAddSelection();
    }
    return false;
  }

  public void setExtendAddSelection(boolean extendAddSelection) {
    ((CheckedTreeSelectionModel) selectionModel)
            .setExtendAddSelection(extendAddSelection);
  }

  public boolean isExtendRemoveSelection() {
    if (selectionModel instanceof CheckedTreeSelectionModel) {
      return ((CheckedTreeSelectionModel) selectionModel)
              .isExtendRemoveSelection();
    }
    return false;
  }

  public void setExtendRemoveSelection(boolean extendRemoveSelection) {
    ((CheckedTreeSelectionModel) selectionModel)
            .setExtendRemoveSelection(extendRemoveSelection);
  }

  @Override
  public void updateUI() {
    LookAndFeelManager.installOrchidUI();
    super.updateUI();
  }

  @Override
  protected void setExpandedState(TreePath path, boolean state) {
    pathCollapsing = !state;
    try {
      super.setExpandedState(path, state);
    } finally {
      pathCollapsing = false;
    }
  }

  @Override
  protected boolean removeDescendantSelectedPaths(TreePath path,
          boolean includePath) {
    if (pathCollapsing) {
      return false;
    }
    return super.removeDescendantSelectedPaths(path, includePath);
  }

  /**
   * Returns about box dialog
   *
   * @return An about box dialog
   */
  @Override
  public JDialog getAboutBox() {
    return DefaultOrchidAbout.getDefaultAboutBox(getClass());
  }

  /**
   * internal use.
   *
   * @param aboutBox about dialog
   */
  public void setAboutBox(JDialog aboutBox) {
    // no contents need.
  }
}
