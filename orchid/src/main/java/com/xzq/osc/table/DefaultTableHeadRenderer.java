/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import com.xzq.osc.JocTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableModel;

/**
 *
 * @author zqxu
 */
public class DefaultTableHeadRenderer extends JComponent
        implements TableHeadRenderer {

  private JLabel label;
  private JLabel sorter;

  /**
   *
   */
  public DefaultTableHeadRenderer() {
    super();
    add(label = new JLabel());
    add(sorter = new JLabel());
    setLayout(new LayoutHandler());
    label.setHorizontalAlignment(JLabel.HORIZONTAL);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public Component getTableHeadRendererComponent(JocTable table,
          Icon icon, Object value, boolean isPressed, boolean isRollover,
          int column, int rowIndex) {
    Icon sortIcon = getColumnSortIcon(table, column);
    sorter.setIcon(sortIcon);
    sorter.setVisible(sortIcon != null);
    label.setIcon(icon);
    label.setText(value == null ? "" : value.toString());
    setBorder(getHeaderCellBorder(table, isPressed, isRollover));
    return this;
  }

  /**
   *
   * @param table
   * @param column the column, interms of the model.
   * @return
   */
  protected Icon getColumnSortIcon(JocTable table, int column) {
    SortOrder order = getColumnSortOrder(table, column);
    if (order == SortOrder.ASCENDING) {
      return UIManager.getIcon("Table.ascendingSortIcon");
    } else if (order == SortOrder.DESCENDING) {
      return UIManager.getIcon("Table.descendingSortIcon");
    } else {
      return UIManager.getIcon("Table.naturalSortIcon");
    }
  }

  /**
   *
   * @param table
   * @param column the column, in terms of the model.
   * @return
   */
  protected SortOrder getColumnSortOrder(JocTable table, int column) {
    if (table.getRowSorter() != null) {
      for (RowSorter.SortKey key : table.getRowSorter().getSortKeys()) {
        if (key.getColumn() == column) {
          return key.getSortOrder();
        }
      }
    }
    return null;
  }

  private Border getHeaderCellBorder(JocTable table, boolean pressed,
          boolean rollover) {
    HeaderCellBorder border = new HeaderCellBorder();
    border.dark = table.getGridColor();
    border.pressed = pressed;
    border.rollover = rollover;
    return border;
  }

  /**
   *
   * @param g
   */
  @Override
  public void paint(Graphics g) {
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());
    super.paint(g);
  }

  private class LayoutHandler implements LayoutManager {

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
      Insets insets = getInsets();
      Dimension labelSize = label.getPreferredSize();
      Dimension sorterSize = sorter.getPreferredSize();
      int width = insets.left + insets.right + labelSize.width
              + (sorter.isVisible() ? sorterSize.width : 0);
      int height = insets.top + insets.bottom + Math.max(labelSize.height,
              sorter.isVisible() ? sorterSize.height : 0);
      return new Dimension(width, height);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
      return new Dimension();
    }

    @Override
    public void layoutContainer(Container parent) {
      Insets insets = getInsets();
      int width = getWidth() - insets.left - insets.right;
      int height = getHeight() - insets.top - insets.bottom;
      int sorterWidth = sorter.getPreferredSize().width;
      if (sorter.isVisible()) {
        int sorterX = getWidth() - insets.right - sorterWidth;
        sorter.setBounds(sorterX, insets.top, sorterWidth, height);
        label.setBounds(insets.left, insets.top, width - sorterWidth, height);
      } else {
        sorter.setBounds(0, 0, 0, 0);
        label.setBounds(insets.left, insets.top, width, height);
      }
    }
  }

  private class HeaderCellBorder implements Border {

    private Color light = Color.WHITE;
    private Color dark = Color.GRAY;
    private Color rollLight = new Color(255, 200, 60);
    private Color rollDark = new Color(230, 140, 45);
    private boolean pressed = false;
    private boolean rollover = false;

    @Override
    public Insets getBorderInsets(Component c) {
      return new Insets(1, 2, 3, 2);
    }

    @Override
    public boolean isBorderOpaque() {
      return true;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y,
            int width, int height) {
      Graphics2D g2 = (Graphics2D) g;
      g2.translate(x, y);
      g2.setColor(pressed ? dark : light);
      g2.drawLine(0, 0, 0, height - 1);
      g2.drawLine(0, 0, width - 1, 0);
      g2.setColor(pressed ? light : dark);
      g2.drawLine(width - 1, 0, width - 1, height - 1);
      g2.drawLine(0, height - 1, width - 1, height - 1);
      if (rollover) {
        g2.setColor(rollLight);
        g2.drawLine(1, height - 3, width - 2, height - 3);
        g2.drawLine(1, height - 2, width - 2, height - 2);
        g2.setColor(rollDark);
        g2.drawLine(1, height - 1, width - 2, height - 1);
      }
    }
  }
}
