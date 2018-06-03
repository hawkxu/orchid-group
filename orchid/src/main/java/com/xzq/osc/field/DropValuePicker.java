/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.JocValueField;
import com.xzq.osc.resource.Resource;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author zqxu
 */
public abstract class DropValuePicker extends AbstractValuePicker {

  /**
   * while value picking, indicate the invoker
   */
  protected JocValueField field;
  /**
   * popup menu for show drop picker.
   */
  protected JPopupMenu dropMenu;
  private JScrollPane container;

  public DropValuePicker() {
    dropMenu = new JPopupMenu();
    dropMenu.setLayout(new BorderLayout());
    container = new JScrollPane();
    container.setBorder(null);
    dropMenu.add(container, BorderLayout.CENTER);
    dropMenu.addPopupMenuListener(createDropMenuHandler());
  }

  @Override
  public Icon getPickButtonIcon() {
    return Resource.getOrchidIcon("picklist.png");
  }

  @Override
  public void pickupValue(JocValueField field, Object value) {
    this.field = field;
    try {
      container.setViewportView(preparePickComponent(value));
      invokeValuePicker();
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          container.getViewport().getView().requestFocus();
        }
      });
    } catch (RuntimeException ex) {
      releaseValuePicker();
      throw ex;
    }
  }

  /**
   * prepare valid picker.
   *
   * @param value current value of the invoker.
   */
  public abstract Component preparePickComponent(Object value);

  /**
   * show value picker
   */
  protected void invokeValuePicker() {
    Dimension fieldSize = field.getSize();
    Dimension popupSize = dropMenu.getPreferredSize();
    if (popupSize.width < fieldSize.width) {
      popupSize.width = fieldSize.width;
    }
    dropMenu.setPopupSize(popupSize);
    dropMenu.show(field, 0, fieldSize.height);
    setValuePicking(true);
  }

  protected void commitValuePicker(Object value) {
    if (isValuePicking()) {
      field.setValue(value);
      dropMenu.setVisible(false);
    }
  }

  private PopupMenuListener createDropMenuHandler() {
    return new PopupMenuListener() {
      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        pickerWillBecomeVisible(e);
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        pickerWillBecomeInvisible(e);
      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
        pickerCanceled(e);
      }
    };
  }

  protected void pickerCanceled(PopupMenuEvent e) {
  }

  protected void pickerWillBecomeVisible(PopupMenuEvent e) {
  }

  protected void pickerWillBecomeInvisible(PopupMenuEvent e) {
    releaseValuePicker();
  }

  /**
   * after picker commit or canceled, release value picker.
   */
  protected void releaseValuePicker() {
    this.field = null;
    container.setViewportView(null);
    setValuePicking(false);
  }
}