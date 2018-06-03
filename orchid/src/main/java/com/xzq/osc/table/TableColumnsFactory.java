/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import com.xzq.osc.JocTable;

/**
 * Table column factory for JocTable
 *
 * @author zqxu
 */
public interface TableColumnsFactory {

  /**
   * create and set columns for the table.
   *
   * @param table the table.
   */
  public void createColumnsFromModel(JocTable table);
}