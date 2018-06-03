/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.treetable;

import java.util.Arrays;

/**
 *
 * @author zqxu
 */
public class DefaultMutableTreeTableNode
        extends AbstractMutableTreeTableNode {

  protected Object[] values;

  /**
   *
   */
  public DefaultMutableTreeTableNode() {
    this(null, null, true);
  }

  /**
   *
   * @param userObject
   */
  public DefaultMutableTreeTableNode(Object userObject) {
    this(userObject, null, true);
  }

  /**
   *
   * @param userObject
   * @param values
   */
  public DefaultMutableTreeTableNode(Object userObject,
          Object[] values) {
    this(userObject, values, true);
  }

  /**
   *
   * @param userObject
   * @param allowsChildren
   */
  public DefaultMutableTreeTableNode(Object userObject,
          boolean allowsChildren) {
    this(userObject, null, allowsChildren);
  }

  /**
   *
   * @param userObject
   * @param values
   * @param allowsChildren
   */
  public DefaultMutableTreeTableNode(Object userObject,
          Object[] values, boolean allowsChildren) {
    super(userObject, allowsChildren);
    this.values = values;
  }

  /**
   *
   * @param column
   * @return
   */
  @Override
  public Object getValueAt(int column) {
    return column == 0 ? getUserObject() : values[column - 1];
  }

  /**
   *
   * @return
   */
  @Override
  public int getColumnCount() {
    return values == null ? 1 : values.length + 1;
  }

  /**
   *
   * @param columnCount
   */
  public void setColumnCount(int columnCount) {
    assert (columnCount >= 1);
    if (columnCount == 1) {
      values = null;
    } else if (values == null) {
      values = new Object[columnCount - 1];
    } else {
      values = Arrays.copyOf(values, columnCount - 1);
    }
  }

  /**
   *
   * @param column
   * @return
   */
  @Override
  public boolean isEditable(int column) {
    return true;
  }

  /**
   *
   * @param aValue
   * @param column
   */
  @Override
  public void setValueAt(int column, Object aValue) {
    if (column == 0) {
      setUserObject(aValue);
    } else {
      values[column - 1] = aValue;
    }
  }
}
