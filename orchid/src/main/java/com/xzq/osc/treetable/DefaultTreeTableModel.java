/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.treetable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.TreePath;

/**
 *
 * @author zqxu
 */
public class DefaultTreeTableModel extends AbstractTreeTableModel {

  protected List<?> columnIdentifiers;
  private boolean autoCalculatedIdentifiers;

  /**
   *
   */
  public DefaultTreeTableModel() {
    this(null);
  }

  /**
   *
   * @param root
   */
  public DefaultTreeTableModel(TreeTableNode root) {
    this(root, null);
  }

  /**
   *
   * @param root
   * @param columnNames
   */
  public DefaultTreeTableModel(TreeTableNode root, List<?> columnNames) {
    super(root);
    setColumnIdentifiers(columnNames);
  }

  /**
   *
   * @return
   */
  @Override
  public int getColumnCount() {
    return columnIdentifiers.size();
  }

  /**
   *
   * @param column
   * @return
   */
  @Override
  public String getColumnName(int column) {
    Object id = null;
    if (column < columnIdentifiers.size()) {
      id = columnIdentifiers.get(column);
    }
    return (id == null) ? super.getColumnName(column) : id.toString();
  }

  /**
   *
   * @param node
   * @param column
   * @return
   */
  @Override
  public Object getValueAt(Object node, int column) {
    TreeTableNode ttn = (TreeTableNode) node;
    if (column >= ttn.getColumnCount()) {
      return null;
    }
    return ttn.getValueAt(column);
  }

  /**
   *
   * @param node
   * @param column
   * @return
   */
  @Override
  public boolean isCellEditable(Object node, int column) {
    TreeTableNode ttn = (TreeTableNode) node;
    if (column >= ttn.getColumnCount()) {
      return false;
    }
    return ttn.isEditable(column);
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
    TreeTableNode ttn = (TreeTableNode) node;
    ttn.setValueAt(column, value);
    nodeChanged(ttn);
  }

  /**
   *
   * @param parent
   * @param index
   * @return
   */
  @Override
  public Object getChild(Object parent, int index) {
    return ((TreeTableNode) parent).getChildAt(index);
  }

  /**
   *
   * @param parent
   * @return
   */
  @Override
  public int getChildCount(Object parent) {
    return ((TreeTableNode) parent).getChildCount();
  }

  /**
   *
   * @param node
   * @return
   */
  @Override
  public boolean isLeaf(Object node) {
    return ((TreeTableNode) node).isLeaf();
  }

  /**
   *
   * @param parent
   * @param child
   * @return
   */
  @Override
  public int getIndexOfChild(Object parent, Object child) {
    if (parent == null || child == null) {
      return -1;
    }
    return ((TreeTableNode) parent).getIndex((TreeTableNode) child);
  }

  /**
   *
   * @return
   */
  @Override
  public TreeTableNode getRoot() {
    return (TreeTableNode) root;
  }

  /**
   *
   * @param root
   */
  public void setRoot(TreeTableNode root) {
    this.root = root;
    if (autoCalculatedIdentifiers) {
      setColumnIdentifiers(null);
    } else {
      fireNewRoot();
    }
  }

  /**
   *
   * @param columnIdentifiers
   */
  public void setColumnIdentifiers(List<?> columnIdentifiers) {
    autoCalculatedIdentifiers = columnIdentifiers == null;
    this.columnIdentifiers = autoCalculatedIdentifiers
            ? getAutoCalculatedIdentifiers(getRoot())
            : columnIdentifiers;
    fireNewRoot();
  }

  private static List<String> getAutoCalculatedIdentifiers(
          TreeTableNode exemplar) {
    List<String> autoCalculatedIndentifiers = new ArrayList<String>();
    if (exemplar != null) {
      for (int i = 0, len = exemplar.getColumnCount(); i < len; i++) {
        autoCalculatedIndentifiers.add(null);
      }
    }
    return autoCalculatedIndentifiers;
  }

  /**
   *
   * @param path
   * @param newValue
   */
  @Override
  public void valueForPathChanged(TreePath path, Object newValue) {
    if (path.getPathComponent(0) != root) {
      throw new IllegalArgumentException("invalid path");
    }
    TreeTableNode node = (TreeTableNode) path.getLastPathComponent();
    node.setUserObject(newValue);
    nodeChanged(node);
  }

  /**
   *
   * @param aNode
   * @return
   */
  public TreeTableNode[] getPathToRoot(TreeTableNode aNode) {
    List<TreeTableNode> path = new ArrayList<TreeTableNode>();
    TreeTableNode node = aNode;
    while (node != root) {
      path.add(0, node);
      node = node.getParent();
    }
    if (node == root) {
      path.add(0, node);
    }
    return path.toArray(new TreeTableNode[0]);
  }

  /**
   *
   * @param newChild
   * @param parent
   * @param index
   */
  public void insertNodeInto(MutableTreeTableNode newChild,
          MutableTreeTableNode parent, int index) {
    parent.insert(newChild, index);
    int[] newIndexs = new int[1];
    newIndexs[0] = index;
    nodesWereInserted(parent, newIndexs);
  }

  /**
   * Message this to remove node from its parent. This will message
   * nodesWereRemoved to create the appropriate event. This is the preferred way
   * to remove a node as it handles the event creation for you.
   */
  public void removeNodeFromParent(MutableTreeTableNode node) {
    MutableTreeTableNode parent = (MutableTreeTableNode) node.getParent();
    if (parent == null) {
      throw new IllegalArgumentException("node does not have a parent.");
    }
    int[] childIndex = new int[1];
    Object[] removedArray = new Object[1];

    childIndex[0] = parent.getIndex(node);
    parent.remove(childIndex[0]);
    removedArray[0] = node;
    nodesWereRemoved(parent, childIndex, removedArray);
  }

  /**
   *
   * @param node
   */
  public void nodeInserted(TreeTableNode node) {
    TreeTableNode parent = node.getParent();
    if (parent != null) {
      nodesWereInserted(parent, new int[]{parent.getIndex(node)});
    }
  }

  /**
   * Invoke this method after you've changed how node is to be represented in
   * the tree.
   */
  public void nodeChanged(TreeTableNode node) {
    TreeTableNode parent = node.getParent();
    if (parent != null) {
      int anIndex = parent.getIndex(node);
      if (anIndex != -1) {
        int[] cIndexs = new int[1];
        cIndexs[0] = anIndex;
        nodesChanged(parent, cIndexs);
      }
    } else if (node == getRoot()) {
      nodesChanged(node, null);
    }
  }

  /**
   *
   * @param node
   */
  public void nodeRemoved(TreeTableNode node) {
    TreeTableNode parent = node.getParent();
    if (parent != null) {
      nodesWereRemoved(parent, new int[]{parent.getIndex(node)},
              new Object[]{node});
    }
  }

  /**
   * Invoke this method if you've modified the {@code TreeNode}s upon which this
   * model depends. The model will notify all of its listeners that the model
   * has changed below the given node.
   *
   * @param node the node below which the model has changed
   */
  public void reload(TreeTableNode node) {
    if (node != null) {
      fireTreeStructureChanged(this, new TreePath(getPathToRoot(node)),
              null, null);
    }
  }

  /**
   * Invoke this method after you've inserted some TreeNodes into node.
   * childIndices should be the index of the new elements and must be sorted in
   * ascending order.
   */
  public void nodesWereInserted(TreeTableNode node, int[] childIndices) {
    if (listenerList != null && node != null && childIndices != null
            && childIndices.length > 0) {
      int cCount = childIndices.length;
      Object[] newChildren = new Object[cCount];

      for (int counter = 0; counter < cCount; counter++) {
        newChildren[counter] = node.getChildAt(childIndices[counter]);
      }
      fireTreeNodesInserted(this, new TreePath(getPathToRoot(node)),
              childIndices, newChildren);
    }
  }

  /**
   * Invoke this method after you've removed some TreeNodes from node.
   * childIndices should be the index of the removed elements and must be sorted
   * in ascending order. And removedChildren should be the array of the children
   * objects that were removed.
   */
  public void nodesWereRemoved(TreeTableNode node, int[] childIndices,
          Object[] removedChildren) {
    if (node != null && childIndices != null) {
      fireTreeNodesRemoved(this, new TreePath(getPathToRoot(node)),
              childIndices, removedChildren);
    }
  }

  /**
   * Invoke this method after you've changed how the children identified by
   * childIndicies are to be represented in the tree.
   */
  public void nodesChanged(TreeTableNode node, int[] childIndices) {
    if (node != null) {
      if (childIndices != null) {
        int cCount = childIndices.length;

        if (cCount > 0) {
          Object[] cChildren = new Object[cCount];

          for (int counter = 0; counter < cCount; counter++) {
            cChildren[counter] = node.getChildAt(childIndices[counter]);
          }
          fireTreeNodesChanged(this, new TreePath(getPathToRoot(node)),
                  childIndices, cChildren);
        }
      } else if (node == getRoot()) {
        fireTreeNodesChanged(this, new TreePath(getPathToRoot(node)),
                null, null);
      }
    }
  }

  /**
   * Invoke this method if you've totally changed the children of node and its
   * childrens children... This will post a treeStructureChanged event.
   */
  public void nodeStructureChanged(TreeTableNode node) {
    if (node != null) {
      fireTreeStructureChanged(this, new TreePath(getPathToRoot(node)),
              null, null);
    }
  }
}
