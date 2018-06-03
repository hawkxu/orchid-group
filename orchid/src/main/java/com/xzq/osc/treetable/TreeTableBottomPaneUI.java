/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.treetable;

import com.xzq.osc.JocTreeTable;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author zqxu
 */
public class TreeTableBottomPaneUI extends ComponentUI {

  private Handler handler;
  protected TreeTableBottomPane pane;

  /**
   *
   */
  public TreeTableBottomPaneUI() {
    super();
  }

  /**
   *
   * @param c
   * @return
   */
  public static TreeTableBottomPaneUI createUI(JComponent c) {
    return new TreeTableBottomPaneUI();
  }

  /**
   *
   * @param c
   */
  @Override
  public void installUI(JComponent c) {
    pane = (TreeTableBottomPane) c;
    pane.setLayout(getHandler());
  }

  /**
   *
   * @param c
   */
  @Override
  public void uninstallUI(JComponent c) {
    pane.setLayout(null);
    pane = null;
  }

  private Handler getHandler() {
    if (handler == null) {
      handler = new Handler();
    }
    return handler;
  }

  private class Handler implements LayoutManager {

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
      return pane.getScroller().getPreferredSize();
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
      return preferredLayoutSize(parent);
    }

    @Override
    public void layoutContainer(Container parent) {
      JocTreeTable treeTable = (JocTreeTable) pane.getTable();
      Insets insets = pane.getInsets();
      int width = pane.getWidth() - insets.left - insets.right;
      int height = pane.getHeight() - insets.top - insets.bottom;
      Component adjuster = pane.getFixedColumnAdjuster();
      if (pane.getFixedColumnAllowed()) {
        int aw = adjuster.getPreferredSize().width;
        adjuster.setBounds(insets.left, insets.top, aw, height);
        width -= aw;
        insets.left += aw;
      } else {
        adjuster.setBounds(0, 0, 0, 0);
      }
      int hc = treeTable.getHierarchicalColumn();
      if (hc == -1) {
        pane.getTreeColumnScroller().setBounds(0, 0, 0, 0);
      } else {
        int hw = pane.getTable().getColumn(hc).getWidth();
        if (treeTable.getLeftPane() != null) {
          hw += treeTable.getLeftPane().getWidth();
        }
        hw -= adjuster.getWidth();
        pane.getTreeColumnScroller().setBounds(insets.left, insets.top,
                hw, height);
        width -= hw;
        insets.left += hw;
      }
      if (pane.getSelectionLabelVisible()) {
        Component selection = pane.getSelectionLabel();
        Dimension size = selection.getPreferredSize();
        size.width = Math.min(width, size.width);
        int x = insets.left + width - size.width;
        selection.setBounds(x, insets.top, size.width, height);
        width -= size.width;
      }
      pane.getScroller().setBounds(insets.left, insets.top, width, height);
      pane.updateScroller();
      pane.updateTreeColumnScroller();
    }
  }
}
