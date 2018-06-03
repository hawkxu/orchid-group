/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author zqxu
 */
public class TableSidePaneUI extends ComponentUI {

  @Override
  public void installUI(JComponent c) {
    LookAndFeel.installColorsAndFont(c,
            "Panel.background", "Panel.foreground", "Panel.font");
    LookAndFeel.installProperty(c, "opaque", Boolean.TRUE);
  }
}
