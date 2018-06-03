/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

/**
 *
 * @author zqxu
 */
public class ColumnHeaderPoint {

  public int modelIndex;
  public int rowIndex;

  /**
   *
   * @param modelIndex
   * @param lineIndex
   */
  public ColumnHeaderPoint(int modelIndex, int lineIndex) {
    this.modelIndex = modelIndex;
    this.rowIndex = lineIndex;
  }

  /**
   *
   * @param obj
   * @return
   */
  @Override
  public boolean equals(Object obj) {
    return obj != null && obj instanceof ColumnHeaderPoint
            && ((ColumnHeaderPoint) obj).modelIndex == modelIndex
            && ((ColumnHeaderPoint) obj).rowIndex == rowIndex;
  }

  /**
   * 
   * @return
   */
  @Override
  public int hashCode() {
    int hash = 3;
    hash = 97 * hash + this.modelIndex;
    hash = 97 * hash + this.rowIndex;
    return hash;
  }
}
