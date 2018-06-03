/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.treetable;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/**
 *
 * @author zqxu
 */
public abstract class AbstractTreeTableModel implements TreeTableModel {

  protected Object root;
  protected EventListenerList listenerList;

  /**
   *
   */
  public AbstractTreeTableModel() {
    this(null);
  }

  /**
   *
   * @param root
   */
  public AbstractTreeTableModel(Object root) {
    this.root = root;
    listenerList = new EventListenerList();
  }

  /**
   *
   * @return
   */
  @Override
  public Object getRoot() {
    return root;
  }

  /**
   *
   * @param column
   * @return
   */
  @Override
  public String getColumnName(int column) {
    String result = "";
    for (; column >= 0; column = column / 26 - 1) {
      result = (char) ((char) (column % 26) + 'A') + result;
    }
    return result;
  }

  /**
   *
   * @param column
   * @return
   */
  @Override
  public Class<?> getColumnClass(int column) {
    return Object.class;
  }

  /**
   *
   * @return
   */
  @Override
  public int getHierarchicalColumn() {
    return getColumnCount() == 0 ? -1 : 0;
  }

  /**
   *
   * @param node
   * @param column
   * @return
   */
  @Override
  public boolean isCellEditable(Object node, int column) {
    return false;
  }

  /**
   *
   *
   * @param node
   * @param column
   * @param value
   */
  @Override
  public void setValueAt(Object node, int column, Object value) {
  }

  /**
   *
   * @param node
   * @return
   */
  @Override
  public boolean isLeaf(Object node) {
    return getChildCount(node) == 0;
  }

  /**
   *
   * @param path
   * @param newValue
   */
  @Override
  public void valueForPathChanged(TreePath path, Object newValue) {
    // do nothing
  }

  /**
   *
   * @param l
   */
  @Override
  public void addTreeModelListener(TreeModelListener l) {
    listenerList.add(TreeModelListener.class, l);
  }

  /**
   *
   * @return
   */
  public TreeModelListener[] getTreeModelListeners() {
    return listenerList.getListeners(TreeModelListener.class);
  }

  /**
   *
   * @param l
   */
  @Override
  public void removeTreeModelListener(TreeModelListener l) {
    listenerList.remove(TreeModelListener.class, l);
  }

  /**
   *
   */
  protected void fireNewRoot() {
    Object rootNode = getRoot();
    TreePath path = (rootNode != null) ? new TreePath(rootNode) : null;
    fireTreeStructureChanged(this, path);
  }

  /**
   *
   * @param source
   * @param path
   * @param childIndices
   * @param children
   */
  protected void fireTreeNodesInserted(Object source, TreePath path,
          int[] childIndices, Object[] children) {
    TreeModelEvent e = new TreeModelEvent(source, path,
            childIndices, children);
    for (TreeModelListener l : getTreeModelListeners()) {
      l.treeNodesInserted(e);
    }
  }

  /**
   *
   * @param source
   * @param path
   * @param childIndices
   * @param children
   */
  protected void fireTreeNodesChanged(Object source, TreePath path,
          int[] childIndices, Object[] children) {
    TreeModelEvent e = new TreeModelEvent(source, path,
            childIndices, children);
    for (TreeModelListener l : getTreeModelListeners()) {
      l.treeNodesChanged(e);
    }
  }

  /**
   *
   * @param source
   * @param path
   * @param childIndices
   * @param children
   */
  protected void fireTreeNodesRemoved(Object source, TreePath path,
          int[] childIndices, Object[] children) {
    TreeModelEvent e = new TreeModelEvent(source, path,
            childIndices, children);
    for (TreeModelListener l : getTreeModelListeners()) {
      l.treeNodesRemoved(e);
    }
  }

  /**
   *
   * @param source
   * @param path
   */
  protected void fireTreeStructureChanged(Object source, TreePath path) {
    TreeModelEvent e = new TreeModelEvent(source, path);
    for (TreeModelListener l : getTreeModelListeners()) {
      l.treeStructureChanged(e);
    }
  }

  /**
   *
   * @param source
   * @param path
   * @param childIndices
   * @param children
   */
  protected void fireTreeStructureChanged(Object source, TreePath path,
          int[] childIndices, Object[] children) {
    TreeModelEvent e = new TreeModelEvent(source, path,
            childIndices, children);
    for (TreeModelListener l : getTreeModelListeners()) {
      l.treeStructureChanged(e);
    }
  }
}
