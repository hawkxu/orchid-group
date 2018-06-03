/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.treetable;

import com.xzq.osc.JocTreeTable;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 *
 * @author zqxu
 */
public class TreeColumnCellRenderer extends JTree
        implements TableCellRenderer {

  protected JocTreeTable treeTable;
  protected int paintingRow;
  protected Border highlightBorder = null;
  private PropertyChangeListener treeTablePropertyListener;
  private List<String> registeredProperties;

  /**
   *
   */
  public TreeColumnCellRenderer() {
    super();
    setRootVisible(false);
    setShowsRootHandles(true);
    DefaultTreeCellRenderer tcr = new DefaultTreeCellRenderer();
    tcr.setOpaque(true);
    setCellRenderer(tcr);
  }

  /**
   *
   * @param treeTable
   */
  public void bind(JocTreeTable treeTable) {
    if (treeTable == null) {
      throw new IllegalArgumentException("null treeTable");
    }
    if (this.treeTable != null) {
      throw new IllegalStateException("renderer already bound!");
    }
    this.treeTable = treeTable;
    setRowHeight(treeTable.getRowHeight());
    setModel(treeTable.getTreeTableModel());
    registerTreetablePropertyListener("treeTableModel");
  }

  /**
   *
   */
  public void unbind() {
    if (treeTable != null && registeredProperties != null) {
      PropertyChangeListener l = getTreetablePropertyListener();
      for (String propertyName : registeredProperties) {
        treeTable.removePropertyChangeListener(propertyName, l);
      }
      registeredProperties.clear();
      registeredProperties = null;
    }
    setModel(null);
    treeTable = null;
  }

  /**
   *
   * @param x
   * @param y
   * @param width
   * @param height
   */
  @Override
  public void setBounds(int x, int y, int width, int height) {
    if (treeTable != null) {
      height = treeTable.getRowCount() * this.getRowHeight();
      width = getPreferredSize().width;
      int hc = treeTable.getHierarchicalColumn();
      if (hc >= 0) {
        TableColumn column = treeTable.getColumn(hc);
        width = column.getWidth() + treeTable.getTreeColumnHorizontal();
      }
      width = Math.max(width, getPreferredSize().width);
    }
    super.setBounds(0, 0, width, height);
  }

  /**
   *
   * @param row
   * @return
   */
  @Override
  public boolean isRowSelected(int row) {
    return treeTable == null ? false : treeTable.isRowSelected(row);
  }

  /**
   *
   * @param path
   * @return true if the path selected or false not.
   */
  @Override
  public boolean isPathSelected(TreePath path) {
    return isRowSelected(getRowForPath(path));
  }

  /**
   *
   * @param table
   * @param value
   * @param isSelected
   * @param hasFocus
   * @param row
   * @param column
   * @return cell renderer component.
   */
  @Override
  public Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected, boolean hasFocus,
          int row, int column) {
    paintingRow = row;
    if (isSelected) {
      setBackground(table.getSelectionBackground());
      setForeground(table.getSelectionForeground());
    } else {
      setBackground(table.getBackground());
      setForeground(table.getForeground());
    }
    highlightBorder = null;
    if (treeTable != null && hasFocus) {
      highlightBorder = UIManager.getBorder(
              "Table.focusCellHighlightBorder");
    }
    paintingRow = row;
    return this;
  }

  /**
   *
   * @param g
   */
  @Override
  public void paint(Graphics g) {
    Rectangle rect = treeTable.getCellRect(paintingRow, 0, false);
    int x = treeTable.getTreeColumnHorizontal();
    int y = getRowHeight() * paintingRow;
    g.translate(-x, -y);
    super.paint(g);
    if (highlightBorder != null) {
      highlightBorder.paintBorder(this, g, x, y,
              rect.width, rect.height);
    }
  }

  /**
   *
   * @param path
   * @param state
   */
  @Override
  protected void setExpandedState(TreePath path, boolean state) {
    TableCellEditor editor = treeTable.getCellEditor();
    if (editor != null) {
      if (!editor.stopCellEditing()) {
        editor.cancelCellEditing();
      }
    }
    super.setExpandedState(path, state);
  }

  /**
   *
   * @param row
   * @return
   */
  public Object nodeForRow(int row) {
    TreePath path = getPathForRow(row);
    return path != null ? path.getLastPathComponent() : null;
  }

  /**
   *
   * @param propertyName
   */
  protected void registerTreetablePropertyListener(String propertyName) {
    if (treeTable != null) {
      if (registeredProperties == null) {
        registeredProperties = new ArrayList<String>();
      }
      registeredProperties.add(propertyName);
      treeTable.addPropertyChangeListener(propertyName,
              getTreetablePropertyListener());
    }
  }

  private PropertyChangeListener getTreetablePropertyListener() {
    if (treeTablePropertyListener == null) {
      treeTablePropertyListener = new TreetablePropertyListener();
    }
    return treeTablePropertyListener;
  }

  /**
   *
   * @param propertyName
   */
  public void treetablePropertyChange(String propertyName) {
    if (propertyName.equals("treeTableModel")) {
      setModel(treeTable.getTreeTableModel());
    }
  }

  /**
   *
   * @param rowHeight
   */
  @Override
  public void setRowHeight(int rowHeight) {
    super.setRowHeight(rowHeight);
    adjustTreetableRowHeight(getRowHeight());
  }

  /**
   *
   * @param rowHeight
   */
  protected void adjustTreetableRowHeight(int rowHeight) {
    if (treeTable != null
            && treeTable.getRowHeight() != rowHeight) {
      treeTable.setRowHeight(rowHeight);
    }
  }

  /**
   *
   * @param path
   */
  @Override
  public void scrollPathToVisible(TreePath path) {
    scrollRowToVisible(getRowForPath(path));
  }

  /**
   *
   * @param row
   */
  @Override
  public void scrollRowToVisible(int row) {
    if (treeTable != null) {
      treeTable.scrollRowToVisible(row);
    }
  }

  private class TreetablePropertyListener
          implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      treetablePropertyChange(evt.getPropertyName());
    }
  }
}
