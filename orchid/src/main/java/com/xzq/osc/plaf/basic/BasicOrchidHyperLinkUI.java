/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.plaf.basic;

import java.awt.Cursor;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 *
 * @author zqxu
 */
public class BasicOrchidHyperLinkUI extends BasicButtonUI {

  private static final Border emptyBorder = new EmptyBorder(1, 1, 1, 1);

  public static ComponentUI createUI(JComponent c) {
    return new BasicOrchidHyperLinkUI();
  }

  /**
   *
   * @param b
   */
  @Override
  protected void installDefaults(AbstractButton b) {
    super.installDefaults(b);
    LookAndFeel.installProperty(b, "opaque", false);
    b.setRolloverEnabled(true);
    b.setBorder(emptyBorder);
  }

  /**
   * 
   * @param b
   * @return
   */
  @Override
  protected BasicButtonListener createButtonListener(AbstractButton b) {
    return new HyperLinkButtonHandler(b);
  }

  private static class HyperLinkButtonHandler extends BasicButtonListener {

    public HyperLinkButtonHandler(AbstractButton b) {
      super(b);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      AbstractButton button = (AbstractButton) e.getSource();
      if (button.isRolloverEnabled()) {
        button.setCursor(button.getModel().isRollover()
                ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : null);
      }
      super.stateChanged(e);
    }
  }
}