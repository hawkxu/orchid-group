/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JComponent;

/**
 *
 * @author zqxu
 */
public class TableBottomPaneUI extends TableSidePaneUI {

  private Handler handler;
  protected TableBottomPane pane;

  public static TableBottomPaneUI createUI(JComponent c) {
    return new TableBottomPaneUI();
  }

  /**
   *
   * @param c
   */
  @Override
  public void installUI(JComponent c) {
    super.installUI(c);
    pane = (TableBottomPane) c;
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
    }
  }
}
