/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.summarytable;

import com.xzq.osc.JocTableCellRenderer;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;

/**
 *
 * @author zqxu
 */
public class DefaultSummaryRowRenderer extends JocTableCellRenderer {

  public DefaultSummaryRowRenderer() {
    super();
    setEmptyNullCell(true);
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column) {
    Component component = super.getTableCellRendererComponent(table, value,
            isSelected, hasFocus, row, column);
    Font font = component.getFont();
    if (font != null && !font.isBold()) {
      component.setFont(font.deriveFont(Font.BOLD));
    }
    return component;
  }
}
