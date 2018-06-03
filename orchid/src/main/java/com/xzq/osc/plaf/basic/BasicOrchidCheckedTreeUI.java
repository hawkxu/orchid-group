/**
 * *****************************************************************************
 * 2013, All rights reserved.
 * *****************************************************************************
 */
package com.xzq.osc.plaf.basic;

import com.xzq.osc.JocCheckedTree;
import com.xzq.osc.tree.CheckedTreeCellEditor;
import com.xzq.osc.tree.CheckedTreeCellRenderer;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 * Description of BasicOrchidCheckedTreeUI.
 *
 * @author zqxu
 */
public class BasicOrchidCheckedTreeUI extends BasicTreeUI {

  /**
   * create UI.
   *
   * @param c table component.
   * @return new UI.
   */
  public static ComponentUI createUI(JComponent c) {
    return new BasicOrchidCheckedTreeUI();
  }

  @Override
  protected TreeCellRenderer createDefaultCellRenderer() {
    return new CheckedTreeCellRenderer();
  }

  @Override
  protected TreeCellEditor createDefaultCellEditor() {
    if (currentCellRenderer instanceof CheckedTreeCellRenderer) {
      return new CheckedTreeCellEditor(tree,
              (CheckedTreeCellRenderer) currentCellRenderer);
    }
    return new CheckedTreeCellEditor(tree, null);
  }

  @Override
  public Rectangle getPathBounds(JTree tree, TreePath path) {
    Rectangle pathBounds = super.getPathBounds(tree, path);
    if (pathBounds != null && isEntireRowMode()) {
      adjustBoundsForEntireRow(pathBounds);
    }
    return pathBounds;
  }

  @Override
  protected boolean startEditing(TreePath path, MouseEvent event) {
    boolean editing = super.startEditing(path, event);
    if (editing && editingComponent != null && isEntireRowMode()) {
      editingComponent.setBounds(getPathBounds(tree, path));
    }
    return editing;
  }

  @Override
  protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets,
          Rectangle bounds, TreePath path, int row, boolean isExpanded,
          boolean hasBeenExpanded, boolean isLeaf) {
    if (isEntireRowMode()) {
      adjustBoundsForEntireRow(bounds);
    }
    super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded,
            hasBeenExpanded, isLeaf);
  }

  private boolean isEntireRowMode() {
    return tree instanceof JocCheckedTree
            && ((JocCheckedTree) tree).isEntireRowMode();
  }

  protected void adjustBoundsForEntireRow(Rectangle bounds) {
    Insets insets = tree.getInsets();
    if (tree.getComponentOrientation().isLeftToRight()) {
      bounds.width = tree.getWidth() - insets.right - bounds.x;
    } else {
      bounds.width += bounds.x - insets.left;
      bounds.x = insets.left;
    }
  }

  @Override
  protected void selectPathForEvent(TreePath path, MouseEvent event) {
    if (!isMultiSelectEvent(event)
            && !isToggleSelectionEvent(event)
            && SwingUtilities.isLeftMouseButton(event)) {
      if (isToggleEvent(event)) {
        toggleExpandState(path);
        return;
      }
      event = new MouseEvent(event.getComponent(), event.getID(),
              event.getWhen(), event.getModifiers() + MouseEvent.CTRL_MASK,
              event.getX(), event.getY(), event.getClickCount(),
              event.isPopupTrigger(), event.getButton());
    }
    super.selectPathForEvent(path, event);
  }
}
