/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.treetable;

import com.xzq.osc.JocTable;
import com.xzq.osc.JocTreeTable;
import com.xzq.osc.table.TableBottomPane;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JScrollBar;
import javax.swing.UIManager;

/**
 *
 * @author zqxu
 */
public class TreeTableBottomPane extends TableBottomPane {

  private static final String uiClassID = "ocTreeTableBottomPane";
  protected JScrollBar treeColumnScroller;
  private boolean ignoreTreeScroller;
  private boolean treeScrollerUpdating;
  private AdjustmentListener treeColumnScrollerListener;

  /**
   *
   */
  public TreeTableBottomPane() {
    super();
    setTreeColumnScroller(createDefaultTreeColumnScroller());
  }

  static {
    UIManager.put(uiClassID, "com.xzq.osc.treetable.TreeTableBottomPaneUI");
  }

  /**
   *
   * @param table
   */
  @Override
  protected void install(JocTable table) {
    super.install(table);
    registerTablePropertyListener("treeColumnHorizontal");
  }

  /**
   *
   * @return
   */
  @Override
  public String getUIClassID() {
    return uiClassID;
  }

  /**
   *
   * @return
   */
  public JScrollBar getTreeColumnScroller() {
    return treeColumnScroller;
  }

  /**
   *
   * @param treeColumnScroller
   */
  public void setTreeColumnScroller(JScrollBar treeColumnScroller) {
    if (treeColumnScroller == null) {
      throw new IllegalArgumentException(
              "Can not set null treeColumnScroller!");
    }
    if (treeColumnScroller != this.treeColumnScroller) {
      JScrollBar old = this.treeColumnScroller;
      if (old != null) {
        old.removeAdjustmentListener(getTreeColumnScrollerListener());
        remove(old);
      }
      this.treeColumnScroller = treeColumnScroller;
      treeColumnScroller.addAdjustmentListener(getTreeColumnScrollerListener());
      add(treeColumnScroller);
      resizeAndRepaint();
      firePropertyChange("scroller", old, treeColumnScroller);
    }
  }

  private JScrollBar createDefaultTreeColumnScroller() {
    JScrollBar sb = new JScrollBar();
    sb.setOrientation(JScrollBar.HORIZONTAL);
    return sb;
  }

  private AdjustmentListener getTreeColumnScrollerListener() {
    if (treeColumnScrollerListener == null) {
      treeColumnScrollerListener = new TreeColumnScrollerListener();
    }
    return treeColumnScrollerListener;
  }

  /**
   *
   */
  public void updateTreeColumnScroller() {
    int tc;
    TreeColumnCellRenderer cr;
    JocTreeTable treeTable = (JocTreeTable) getTable();
    if (treeTable != null
            && (tc = treeTable.getHierarchicalColumn()) != -1
            && (cr = treeTable.getTreeColumnCellRenderer()) != null) {
      int margin = treeTable.getColumnModel().getColumnMargin();
      int preferredWidth = cr.getPreferredSize().width;
      int width = treeTable.getColumn(tc).getWidth();
      treeColumnScroller.setValueIsAdjusting(true);
      treeScrollerUpdating = true;
      treeColumnScroller.setMaximum(preferredWidth + margin);
      treeColumnScroller.setValue(treeTable.getTreeColumnHorizontal());
      treeColumnScroller.setVisibleAmount(width);
      treeColumnScroller.setUnitIncrement(width / 5);
      treeColumnScroller.setBlockIncrement(width / 2);
      treeScrollerUpdating = false;
      treeColumnScroller.setValueIsAdjusting(false);
    }
  }

  /**
   *
   * @param propertyName
   */
  @Override
  protected void tablePropertyChange(String propertyName) {
    if (propertyName.equals("treeColumnHorizontal")) {
      ignoreTreeScroller = true;
      updateTreeColumnScroller();
      ignoreTreeScroller = false;
    } else {
      super.tablePropertyChange(propertyName);
    }
  }

  /**
   *
   */
  protected void treeColumnScrollerValueChanged() {
    ((JocTreeTable) table).setTreeColumnHorizontal(
            treeColumnScroller.getValue());
  }

  private class TreeColumnScrollerListener implements AdjustmentListener {

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
      if (!ignoreTreeScroller && !treeScrollerUpdating) {
        treeColumnScrollerValueChanged();
      }
    }
  }
}
