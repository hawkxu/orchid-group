/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import java.util.EventListener;
import javax.swing.event.PopupMenuEvent;

/**
 *
 * @author zqxu
 */
public interface OptionPopupListener extends EventListener{

  /**
   * This method is called before the option popup menu becomes visible
   */
  void optionPopupWillBecomeVisible(PopupMenuEvent e);

  /**
   * This method is called before the option popup menu becomes invisible Note
   * that a JPopupMenu can become invisible any time
   */
  void optionPopupWillBecomeInvisible(PopupMenuEvent e);

  /**
   * This method is called when the option popup menu is canceled
   */
  void optionPopupCanceled(PopupMenuEvent e);
}
