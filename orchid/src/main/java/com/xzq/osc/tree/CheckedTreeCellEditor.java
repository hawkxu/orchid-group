/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;

/**
 *
 * @author zqxu
 */
public class CheckedTreeCellEditor extends DefaultTreeCellEditor {

  protected CheckedTreeCellRenderer checkedRenderer;
  protected Icon checkedIcon;
  protected Icon contentIcon;

  public CheckedTreeCellEditor(JTree tree,
          CheckedTreeCellRenderer renderer) {
    super(tree, null);
    this.checkedRenderer = renderer;
  }

  @Override
  public Component getTreeCellEditorComponent(JTree tree, Object value,
          boolean isSelected, boolean expanded, boolean leaf, int row) {
    Component component = super.getTreeCellEditorComponent(
            tree, value, isSelected, expanded, leaf, row);
    Font cfont = getFont();
    if (cfont == null) {
      if (checkedRenderer != null) {
        cfont = checkedRenderer.getFont();
      }
      if (cfont == null) {
        cfont = tree.getFont();
      }
    }
    component.setFont(cfont);
    return component;
  }

  @Override
  protected void determineOffset(JTree tree, Object value, boolean selected,
          boolean expanded, boolean leaf, int row) {
    if (checkedRenderer == null) {
      offset = 0;
      checkedIcon = null;
      contentIcon = null;
    } else {
      checkedRenderer.getTreeCellRendererComponent(
              tree, value, selected, expanded, leaf, row, false);
      offset = checkedRenderer.getContentTextOffset();
      checkedIcon = checkedRenderer.getCheckedIcon();
      contentIcon = checkedRenderer.getContentIcon();
    }
  }

  @Override
  protected Container createContainer() {
    return new EditorContainer();
  }

  public class EditorContainer extends DefaultTreeCellEditor.EditorContainer {

    @Override
    public void doLayout() {
      if (editingComponent == null) {
        return;
      }
      Dimension size = getSize();
      if (tree.getComponentOrientation().isLeftToRight()) {
        editingComponent.setBounds(offset, 0,
                size.width - offset, size.height);
      } else {
        editingComponent.setBounds(0, 0,
                size.width - offset, size.height);
      }
    }

    @Override
    public void paint(Graphics g) {
      Dimension size = getSize();
      boolean ltr = tree.getComponentOrientation().isLeftToRight();
      int x = 0, y;
      if (checkedIcon != null) {
        if (!ltr) {
          x = size.width - checkedIcon.getIconWidth();
        }
        y = (size.height - checkedIcon.getIconHeight()) / 2;
        checkedIcon.paintIcon(this, g, x, y);
      }
      if (contentIcon != null) {
        x = checkedRenderer.getContentIconOffset();
        if (!ltr) {
          x = size.width - x - contentIcon.getIconWidth();
        }
        y = (size.height - contentIcon.getIconHeight()) / 2;
        contentIcon.paintIcon(this, g, x, y);
      }

      // Border selection color
      Color background = getBorderSelectionColor();
      if (background != null) {
        g.setColor(background);
        g.drawRect(0, 0, size.width - 1, size.height - 1);
      }
      super.paint(g);
    }
  }
}
