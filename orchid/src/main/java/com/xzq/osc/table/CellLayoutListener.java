/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import java.awt.Color;
import java.util.EventListener;

/**
 *
 * @author zqxu
 */
public interface CellLayoutListener extends EventListener {

  /**
   * get cell background color from listener. listener should return null for no
   * color specified.
   *
   * @param evt event object
   * @return cell background color, or null if no background color specified.
   */
  public Color getCellBackground(CellLayoutEvent evt);

  /**
   * get cell foreground color from listener. listener should return null for no
   * color specified.
   *
   * @param evt event object
   * @return cell foreground color, or null if no foreground color specified.
   */
  public Color getCellForeground(CellLayoutEvent evt);
}
