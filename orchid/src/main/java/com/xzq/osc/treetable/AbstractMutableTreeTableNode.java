/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.treetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;

/**
 *
 * @author zqxu
 */
public abstract class AbstractMutableTreeTableNode
        implements MutableTreeTableNode {

  protected MutableTreeTableNode parent;
  protected final List<MutableTreeTableNode> children;
  protected transient Object userObject;
  protected boolean allowsChildren;

  /**
   *
   */
  public AbstractMutableTreeTableNode() {
    this(null);
  }

  /**
   *
   * @param userObject
   */
  public AbstractMutableTreeTableNode(Object userObject) {
    this(userObject, true);
  }

  /**
   *
   * @param userObject
   * @param allowsChildren
   */
  public AbstractMutableTreeTableNode(Object userObject,
          boolean allowsChildren) {
    this.userObject = userObject;
    this.allowsChildren = allowsChildren;
    children = new ArrayList<MutableTreeTableNode>();
  }

  /**
   *
   * @param child
   */
  public void add(MutableTreeTableNode child) {
    insert(child, getChildCount());
  }

  /**
   *
   * @param child
   * @param index
   */
  @Override
  public void insert(MutableTreeTableNode child, int index) {
    if (!allowsChildren) {
      throw new IllegalStateException("this node cannot accept children");
    }

    if (children.contains(child)) {
      children.remove(child);
      index--;
    }

    children.add(index, child);

    if (child.getParent() != this) {
      child.setParent(this);
    }
  }

  /**
   *
   * @param index
   */
  @Override
  public void remove(int index) {
    children.remove(index).setParent(null);
  }

  /**
   *
   * @param node
   */
  @Override
  public void remove(MutableTreeTableNode node) {
    children.remove(node);
    node.setParent(null);
  }

  /**
   * Remove all children of this node.
   */
  public void removeAllChildren() {
    while (getChildCount() > 0) {
      remove(0);
    }
  }

  /**
   *
   */
  @Override
  public void removeFromParent() {
    parent.remove(this);
  }

  /**
   *
   * @param newParent
   */
  @Override
  public void setParent(MutableTreeTableNode newParent) {
    if (newParent == null || newParent.getAllowsChildren()) {
      if (parent != null && parent.getIndex(this) != -1) {
        parent.remove(this);
      }
    } else {
      throw new IllegalArgumentException(
              "newParent does not allow children");
    }

    parent = newParent;

    if (parent != null && parent.getIndex(this) == -1) {
      parent.insert(this, parent.getChildCount());
    }
  }

  /**
   *
   * @return
   */
  @Override
  public Object getUserObject() {
    return userObject;
  }

  /**
   *
   * @param object
   */
  @Override
  public void setUserObject(Object object) {
    userObject = object;
  }

  /**
   *
   * @param childIndex
   * @return
   */
  @Override
  public TreeTableNode getChildAt(int childIndex) {
    return children.get(childIndex);
  }

  /**
   *
   * @param node
   * @return
   */
  @Override
  public int getIndex(TreeNode node) {
    return children.indexOf(node);
  }

  /**
   *
   * @return
   */
  @Override
  public TreeTableNode getParent() {
    return parent;
  }

  /**
   *
   * @return
   */
  @Override
  public Enumeration<? extends MutableTreeTableNode> children() {
    return Collections.enumeration(children);
  }

  /**
   *
   * @return
   */
  @Override
  public boolean getAllowsChildren() {
    return allowsChildren;
  }

  /**
   *
   * @param allowsChildren
   */
  public void setAllowsChildren(boolean allowsChildren) {
    this.allowsChildren = allowsChildren;

    if (!this.allowsChildren) {
      removeAllChildren();
    }
  }

  /**
   *
   * @return
   */
  @Override
  public int getChildCount() {
    return children.size();
  }

  /**
   *
   * @return
   */
  @Override
  public boolean isLeaf() {
    return getChildCount() == 0;
  }

  /**
   *
   * @param column
   * @return
   */
  @Override
  public boolean isEditable(int column) {
    return false;
  }

  /**
   *
   * @param aValue
   * @param column
   */
  @Override
  public void setValueAt(int column, Object aValue) {
    // does nothing
  }

  /**
   *
   * @return
   */
  @Override
  public String toString() {
    if (userObject == null) {
      return "";
    } else {
      return userObject.toString();
    }
  }
}
