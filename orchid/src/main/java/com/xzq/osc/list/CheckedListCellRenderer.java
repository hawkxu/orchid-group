/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.list;

import com.xzq.osc.CheckState;
import com.xzq.osc.plaf.AbstractCheckedCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * List cell renderer using JCheckBox, does not support Icon.
 *
 * @author zqxu
 */
public class CheckedListCellRenderer extends AbstractCheckedCellRenderer
        implements ListCellRenderer, SwingConstants {

  private Border focusBorder;

  /**
   *
   */
  public CheckedListCellRenderer() {
    super();
  }

  /**
   * Always return insets as [2, 2, 2, 2]
   */
  @Override
  public Insets getInsets() {
    return new Insets(2, 2, 2, 2);
  }

  /**
   * Always return insets as [2, 2, 2, 2]
   */
  @Override
  public Insets getInsets(Insets insets) {
    return new Insets(2, 2, 2, 2);
  }

  @Override
  public Component getListCellRendererComponent(JList list, Object value,
          int index, boolean selected, boolean cellHasFocus) {
    updateCheckedIcon(selected ? CheckState.CHECKED : CheckState.UNCHECK);
    contentLabel.setText(value == null ? "" : value.toString());
    Color bg = null;
    Color fg = null;
    JList.DropLocation dropLocation = list.getDropLocation();
    if (dropLocation != null
            && !dropLocation.isInsert()
            && dropLocation.getIndex() == index) {
      bg = UIManager.getColor("List.dropCellBackground");
      fg = UIManager.getColor("List.dropCellForeground");
      selected = true;
    }
    if (selected) {
      setBackground(bg == null ? list.getSelectionBackground() : bg);
      setForeground(fg == null ? list.getSelectionForeground() : fg);
    } else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }
    setEnabled(list.isEnabled());
    setFont(list.getFont());
    focusBorder = null;
    if (cellHasFocus) {
      if (selected) {
        focusBorder = UIManager.getBorder(
                "List.focusSelectedCellHighlightBorder");
      }
      if (focusBorder == null) {
        focusBorder = UIManager.getBorder(
                "List.focusCellHighlightBorder");
      }
    }
    setComponentOrientation(list.getComponentOrientation());
    return this;
  }

  @Override
  public void paint(Graphics g) {
    paintCell(g);
    super.paint(g);
  }

  protected void paintCell(Graphics g) {
    Rectangle rect = getContentPaintBounds();
    g.setColor(getBackground());
    g.fillRect(rect.x, rect.y, rect.width, rect.height);
    if (focusBorder != null) {
      focusBorder.paintBorder(this, g,
              rect.x, rect.y, rect.width, rect.height);
    }
  }

  protected Rectangle getContentPaintBounds() {
    Rectangle bounds = contentLabel.getBounds();
    Insets insets = getInsets();
    if (getComponentOrientation().isLeftToRight()) {
      bounds.x--;
      bounds.width += insets.right + 1;
    } else {
      bounds.x -= insets.left;
      bounds.width += insets.left + 1;
    }
    return new Rectangle(bounds.x, 0, bounds.width, getHeight());
  }

  @Override
  public boolean isOpaque() {
    Color back = getBackground();
    Component p = getParent();
    if (p != null) {
      p = p.getParent();
    }
    // p should now be the JList.
    boolean colorMatch = (back != null) && (p != null)
            && back.equals(p.getBackground())
            && p.isOpaque();
    return !colorMatch && super.isOpaque();
  }
}
