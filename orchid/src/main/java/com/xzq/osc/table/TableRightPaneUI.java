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
public class TableRightPaneUI extends TableSidePaneUI {

  private Handler handler;
  protected TableRightPane pane;

  public static TableRightPaneUI createUI(JComponent c) {
    return new TableRightPaneUI();
  }

  /**
   *
   * @param c
   */
  @Override
  public void installUI(JComponent c) {
    super.installUI(c);
    pane = (TableRightPane) c;
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
      Dimension size = pane.getScroller().getPreferredSize();
      Component adjuster = pane.getFixedRowAdjuster();
      Component optionButton = pane.getOptionLabel();
      if (pane.getFixedRowAllowed()) {
        size.height += adjuster.getPreferredSize().height;
      }
      if (optionButton.isVisible()) {
        size.height += optionButton.getPreferredSize().height;
      }
      return size;
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
      Component optionButton = pane.getOptionLabel();
      if (optionButton.isVisible()) {
        int ah = optionButton.getPreferredSize().height;
        optionButton.setBounds(insets.left, insets.top, width, ah);
        height -= ah;
        insets.top += ah;
      }
      Component adjuster = pane.getFixedRowAdjuster();
      if (pane.getFixedRowAllowed()) {
        int ah = adjuster.getPreferredSize().width;
        adjuster.setBounds(insets.left, insets.top, width, ah);
        height -= ah;
        insets.top += ah;
      } else {
        adjuster.setBounds(0, 0, 0, 0);
      }
      pane.getScroller().setBounds(insets.left, insets.top, width, height);
      pane.updateScroller();
    }
  }
}
