/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import java.util.EventListener;

/**
 *
 * @author zqxu
 */
public interface TableLayoutModelListener extends EventListener {

  /**
   * cell layout changed event.
   *
   * @param row row index
   * @param column column index.
   */
  void cellLayoutChanged(int row, int column);

  /**
   * count of fixed rows changed.
   */
  void fixedRowCountChanged();

  /**
   * count of fixed columns changed.
   */
  void fixedColumnCountChanged();
}
