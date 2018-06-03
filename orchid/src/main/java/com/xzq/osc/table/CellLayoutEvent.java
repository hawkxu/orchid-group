/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import java.util.EventObject;
import javax.swing.JTable;

/**
 *
 * @author zqxu
 */
public class CellLayoutEvent extends EventObject {

  /**
   * row index of cell
   */
  private int row;
  /**
   * column index of cell
   */
  private int column;
  /**
   * is this event consumed
   */
  private boolean consumed;

  /**
   * Constructor
   * @param source
   * @param row
   * @param column
   */
  public CellLayoutEvent(JTable source,
          int row, int column) {
    super(source);
    this.row = row;
    this.column = column;
  }

  /**
   *
   * @return
   */
  @Override
  public JTable getSource() {
    return (JTable) super.getSource();
  }

  /**
   * row index of table view.
   * @return
   */
  public int getRow() {
    return row;
  }

  /**
   * column index
   * @return
   */
  public int getColumn() {
    return column;
  }

  /**
   *
   */
  public void consume() {
    this.consumed = true;
  }

  /**
   *
   * @return
   */
  public boolean isConsumed() {
    return consumed;
  }
}
