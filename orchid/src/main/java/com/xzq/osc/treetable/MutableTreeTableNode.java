/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xzq.osc.treetable;

import java.util.Enumeration;

/**
 *
 * @author zqxu
 */
public interface MutableTreeTableNode extends TreeTableNode {
    Enumeration<? extends MutableTreeTableNode> children();

    void insert(MutableTreeTableNode child, int index);

    void remove(int index);

    void remove(MutableTreeTableNode node);

    void removeFromParent();

    void setParent(MutableTreeTableNode newParent);
}
