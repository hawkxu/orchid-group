/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocSummaryTableBeanInfo extends OrchidBaseBeanInfo {

  public JocSummaryTableBeanInfo() {
    super(JocSummaryTable.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setHidden(true, "autoCreateRowSorter", "rowSorter");
  }
}
