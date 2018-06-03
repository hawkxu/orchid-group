/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import com.xzq.osc.JocTable;
import java.awt.Component;
import javax.swing.Icon;

/**
 *
 * @author zqxu
 */
public interface TableHeadRenderer {

  /**
   * Returns column header cell renderer component.
   *
   * @param table table
   * @param icon column header cell icon
   * @param value column header cell value.
   * @param isPressed is pressed.
   * @param isRollover is rollover.
   * @param column the column, in terms of the model.
   * @param rowIndex row index of column header cell.
   * @return column header cell renderer component.
   */
  Component getTableHeadRendererComponent(JocTable table, Icon icon,
          Object value, boolean isPressed, boolean isRollover,
          int column, int rowIndex);
}
