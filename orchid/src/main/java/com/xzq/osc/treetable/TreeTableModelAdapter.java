/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.treetable;

import com.xzq.osc.JocTreeTable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author zqxu
 */
public class TreeTableModelAdapter extends AbstractTableModel {

  protected JocTreeTable treeTable;
  protected TreeTableModel treeTableModel;
  protected TreeColumnCellRenderer treeRenderer;
  private PropertyChangeListener treetablePropertyListener;
  private List<String> registeredProperties;
  private PropertyChangeListener rootVisibleListener;
  private TreeExpansionHandler treeExpansionHandler;
  private TreeModelListener treetableModelListener;

  /**
   *
   * @param treeTable
   */
  public void bind(JocTreeTable treeTable) {
    if (treeTable == null) {
      throw new IllegalArgumentException("null treeTable!");
    }
    if (this.treeTable != null) {
      throw new IllegalStateException("adapter already bound!");
    }
    this.treeTable = treeTable;
    treeTableModelChange(treeTable.getTreeTableModel());
    treeRendererChange(treeTable.getTreeColumnCellRenderer());
    registerTreetablePropertyListener("treeTableModel");
    registerTreetablePropertyListener("treeColumnCellRenderer");
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
    treeTable = null;
    treeTableModelChange(null);
    treeRendererChange(null);
  }

  /**
   *
   * @return
   */
  @Override
  public int getRowCount() {
    return treeRenderer == null ? 0 : treeRenderer.getRowCount();
  }

  /**
   *
   * @return
   */
  @Override
  public int getColumnCount() {
    return treeTableModel == null ? 0 : treeTableModel.getColumnCount();
  }

  /**
   *
   * @param row
   * @param column
   * @return
   */
  @Override
  public Object getValueAt(int row, int column) {
    Object node = nodeForRow(row);
    return node == null || treeTableModel == null
            ? null : treeTableModel.getValueAt(node, column);
  }

  /**
   *
   * @param column
   * @return
   */
  @Override
  public String getColumnName(int column) {
    return treeTableModel == null
            ? super.getColumnName(column)
            : treeTableModel.getColumnName(column);
  }

  /**
   *
   * @param column
   * @return
   */
  @Override
  public Class<?> getColumnClass(int column) {
    return treeTableModel == null
            ? super.getColumnClass(column)
            : treeTableModel.getColumnClass(column);
  }

  /**
   *
   * @param row
   * @param column
   * @return
   */
  @Override
  public boolean isCellEditable(int row, int column) {
    Object node = nodeForRow(row);
    return node == null || treeTableModel == null
            ? false : treeTableModel.isCellEditable(node, column);
  }

  /**
   *
   * @param aValue
   * @param row
   * @param column
   */
  @Override
  public void setValueAt(Object aValue, int row, int column) {
    Object node = treeRenderer.nodeForRow(row);
    if (node != null && treeTableModel != null) {
      treeTableModel.setValueAt(node, column, aValue);
    }
  }

  /**
   *
   * @param row
   * @return
   */
  protected Object nodeForRow(int row) {
    return treeRenderer == null ? null : treeRenderer.nodeForRow(row);
  }

  // <editor-fold defaultstate="collapsed" desc="tree table property listener">
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
    if (treetablePropertyListener == null) {
      treetablePropertyListener = new TreetablePropertyListener();
    }
    return treetablePropertyListener;
  }

  /**
   *
   * @param propertyName
   */
  protected void treetablePropertyChange(String propertyName) {
    if (propertyName.equals("treeTableModel")) {
      treeTableModelChange(treeTable.getTreeTableModel());
    } else if (propertyName.equals("treeColumnCellRenderer")) {
      treeRendererChange(treeTable.getTreeColumnCellRenderer());
    }
  }

  /**
   *
   * @param treeTableModel
   */
  protected void treeTableModelChange(TreeTableModel treeTableModel) {
    TreeModelListener l = getTreeModelListener();
    if (this.treeTableModel != null) {
      this.treeTableModel.removeTreeModelListener(l);
    }
    this.treeTableModel = treeTableModel;
    if (treeTableModel != null) {
      treeTableModel.addTreeModelListener(l);
    }
    fireTableStructureChanged();
  }

  /**
   *
   * @param treeRenderer
   */
  protected void treeRendererChange(TreeColumnCellRenderer treeRenderer) {
    TreeExpansionHandler handler = getTreeExpansionHandler();
    if (this.treeRenderer != null) {
      this.treeRenderer.removeTreeExpansionListener(handler);
      this.treeRenderer.removePropertyChangeListener("rootVisible",
              getRootVisibleListener());
    }
    this.treeRenderer = treeRenderer;
    if (treeRenderer != null) {
      treeRenderer.addTreeExpansionListener(handler);
      treeRenderer.addPropertyChangeListener("rootVisible",
              getRootVisibleListener());
    }
    fireTableDataChanged();
  }

  private PropertyChangeListener getRootVisibleListener() {
    if (rootVisibleListener == null) {
      rootVisibleListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          delayedFireTableDataChanged();
        }
      };
    }
    return rootVisibleListener;
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="tree collapse and expansion">
  private TreeExpansionHandler getTreeExpansionHandler() {
    if (treeExpansionHandler == null) {
      return new TreeExpansionHandler();
    }
    return treeExpansionHandler;
  }

  /**
   *
   */
  protected void updateAfterExpansionEvent() {
    fireTableDataChanged();
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="tree model event">
  private TreeModelListener getTreeModelListener() {
    if (treetableModelListener == null) {
      treetableModelListener = new TreeTableModelListener();
    }
    return treetableModelListener;
  }

  /**
   *
   */
  protected void delayedFireTableDataChanged() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        fireTableDataChanged();
      }
    });
  }

  /**
   *
   */
  protected void delayedFireTableStructureChanged() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        fireTableStructureChanged();
      }
    });
  }
  // </editor-fold>

  private class TreetablePropertyListener
          implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      treetablePropertyChange(evt.getPropertyName());
    }
  }

  private class TreeTableModelListener implements TreeModelListener {

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
      delayedFireTableDataChanged();
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
      delayedFireTableDataChanged();
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
      delayedFireTableDataChanged();
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
      if (isTableStructureChanged(e)) {
        delayedFireTableStructureChanged();
      } else {
        delayedFireTableDataChanged();
      }
    }

    private boolean isTableStructureChanged(TreeModelEvent e) {
      return e.getTreePath() == null
              || e.getTreePath().getParentPath() == null;
    }
  }

  private class TreeExpansionHandler implements TreeExpansionListener {

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
      updateAfterExpansionEvent();
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
      updateAfterExpansionEvent();
    }
  }
}
