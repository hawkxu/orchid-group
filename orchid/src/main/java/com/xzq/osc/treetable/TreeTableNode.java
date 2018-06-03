/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.treetable;

import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 *
 * @author zqxu
 */
public interface TreeTableNode extends TreeNode {

  @Override
  Enumeration<? extends TreeTableNode> children();

  Object getValueAt(int column);

  @Override
  TreeTableNode getChildAt(int childIndex);

  int getColumnCount();

  @Override
  TreeTableNode getParent();

  boolean isEditable(int column);

  void setValueAt(int column, Object aValue);

  Object getUserObject();

  void setUserObject(Object userObject);
}
