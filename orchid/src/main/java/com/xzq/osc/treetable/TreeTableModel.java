/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.treetable;

import javax.swing.tree.TreeModel;

/**
 *
 * @author zqxu
 */
public interface TreeTableModel extends TreeModel {

  /**
   * Returns column count in model.
   *
   * @return column count
   */
  public int getColumnCount();

  /**
   * Returns column name.
   *
   * @param column column index.
   * @return column name.
   */
  public String getColumnName(int column);

  /**
   * Returns value type of column.
   *
   * @param column column index.
   * @return value type of column.
   */
  public Class<?> getColumnClass(int column);

  /**
   * Returns column index of tree column.
   *
   * @return column index of tree column.
   */
  public int getHierarchicalColumn();

  /**
   * Returns cell value on tree node and column.
   *
   * @param node tree node.
   * @param column column index.
   * @return cell value.
   */
  public Object getValueAt(Object node, int column);

  /**
   * Returns whether cell editable or not.
   *
   * @param node tree node.
   * @param column column index.
   * @return true for cell editable or false not.
   */
  public boolean isCellEditable(Object node, int column);

  /**
   * Set cell value.
   *
   * @param node tree node.
   * @param column column index.
   * @param value cell value
   */
  public void setValueAt(Object node, int column, Object value);
}
