/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.EventListenerList;

/**
 * A wrapping class for using Action in IDE, implement some property
 * getter/setter function.
 *
 * @author zqxu
 */
public class JocAction extends AbstractAction implements OrchidAboutIntf {

  protected EventListenerList listenerList = new EventListenerList();

  /**
   * Constructor of JocAction with defaults.
   */
  public JocAction() {
    super();
    putValue(SELECTED_KEY, false);
    putValue(NAME, getClass().getSimpleName());
  }

  /**
   * Constructor of JocAction with name.
   *
   * @param name name.
   */
  public JocAction(String name) {
    super(name);
  }

  /**
   * Constructor of JocAction with name and small icon.
   *
   * @param name name.
   * @param icon small icon.
   */
  public JocAction(String name, Icon icon) {
    super(name, icon);
  }

  /**
   * Returns value of property Action.NAME.
   *
   * @return name value.
   */
  public String getName() {
    Object name = getValue(NAME);
    return name == null ? null : name.toString();
  }

  /**
   * Set value of property Action.NAME.
   *
   * @param name name value.
   * @see javax.swing.Action#NAME
   */
  public void setName(String name) {
    putValue(NAME, name);
  }

  /**
   * Returns value of property Action.SMALL_ICON.
   *
   * @return small icon.
   */
  public Icon getSmallIcon() {
    Object icon = getValue(SMALL_ICON);
    return icon instanceof Icon ? (Icon) icon : null;
  }

  /**
   * Set value of property Action.SMALL_ICON.
   *
   * @param smallIcon small icon.
   * @see javax.swing.Action#SMALL_ICON
   */
  public void setSmallIcon(Icon smallIcon) {
    putValue(SMALL_ICON, smallIcon);
  }

  /**
   * Returns value of property Action.SHORT_DESCRIPTION.
   *
   * @return short description..
   */
  public String getShortDescription() {
    Object desc = getValue(SHORT_DESCRIPTION);
    return desc == null ? null : desc.toString();
  }

  /**
   * Set value of property Action.SHORT_DESCRIPTION.
   *
   * @param shortDescription short description..
   * @see javax.swing.Action#SHORT_DESCRIPTION
   */
  public void setShortDescription(String shortDescription) {
    putValue(SHORT_DESCRIPTION, shortDescription);
  }

  /**
   * Returns value of property Action.LONG_DESCRIPTION.
   *
   * @return long description.
   */
  public String getLongDescription() {
    Object desc = getValue(LONG_DESCRIPTION);
    return desc == null ? null : desc.toString();
  }

  /**
   * Set value of property Action.LONG_DESCRIPTION.
   *
   * @param longDescription long description.
   * @see javax.swing.Action#LONG_DESCRIPTION
   */
  public void setLongDescription(String longDescription) {
    putValue(LONG_DESCRIPTION, longDescription);
  }

  /**
   * Returns value of property Action.MNEMONIC_KEY.
   *
   * @return mnemonic.
   */
  public int getMnemonicKey() {
    Object mnemonicKey = getValue(MNEMONIC_KEY);
    return mnemonicKey instanceof Integer ? (Integer) mnemonicKey : -1;
  }

  /**
   * Set value of property Action.MNEMONIC_KEY.
   *
   * @param mnemonicKey mnemonic.
   * @see javax.swing.Action#MNEMONIC_KEY
   */
  public void setMnemonicKey(int mnemonicKey) {
    putValue(MNEMONIC_KEY, mnemonicKey);
  }

  /**
   * Returns value of property Action.ACTION_COMMAND_KEY.
   *
   * @return action command..
   */
  public String getActionCommand() {
    Object command = getValue(ACTION_COMMAND_KEY);
    return command == null ? null : command.toString();
  }

  /**
   * Set value of property Action.ACTION_COMMAND_KEY.
   *
   * @param actionCommand action command..
   * @see javax.swing.Action#ACTION_COMMAND_KEY
   */
  public void setActionCommand(String actionCommand) {
    putValue(ACTION_COMMAND_KEY, actionCommand);
  }

  /**
   * Returns value of property Action.ACCELERATOR_KEY.
   *
   * @return accelerator key.
   */
  public KeyStroke getAcceleratorKey() {
    Object key = getValue(ACCELERATOR_KEY);
    return key instanceof KeyStroke ? (KeyStroke) key : null;
  }

  /**
   * Set value of property Action.ACCELERATOR_KEY.
   *
   * @param acceleratorKey accelerator key.
   * @see javax.swing.Action#ACCELERATOR_KEY
   */
  public void setAcceleratorKey(KeyStroke acceleratorKey) {
    putValue(ACCELERATOR_KEY, acceleratorKey);
  }

  /**
   * Returns value of property Action.LARGE_ICON_KEY.
   *
   * @return large icon.
   */
  public Icon getLargeIcon() {
    Object icon = getValue(LARGE_ICON_KEY);
    return icon instanceof Icon ? (Icon) icon : null;
  }

  /**
   * Set value of property Action.LARGE_ICON_KEY.
   *
   * @param largeIcon large icon.
   * @see javax.swing.Action#LARGE_ICON_KEY
   */
  public void setLargeIcon(Icon largeIcon) {
    putValue(LARGE_ICON_KEY, largeIcon);
  }

  /**
   * Returns value of property Action.SELECTED_KEY.
   *
   * @return true or false
   */
  public boolean isSelected() {
    return Boolean.TRUE.equals(getValue(SELECTED_KEY));
  }

  /**
   * Set value of property Action.SELECTED_KEY.
   *
   * @param selected selected state.
   * @see javax.swing.Action#SELECTED_KEY
   */
  public void setSelected(boolean selected) {
    putValue(SELECTED_KEY, selected);
  }

  /**
   * Call action listeners (if exists) to process action event.
   *
   * @param e action event object.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    for (ActionListener act : listenerList.getListeners(ActionListener.class)) {
      act.actionPerformed(e);
    }
  }

  /**
   * Add action listener to listener list.
   *
   * @param listener action listener.
   */
  public void addActionListener(ActionListener listener) {
    listenerList.add(ActionListener.class, listener);
  }

  /**
   * Remove action listener from listener list.
   *
   * @param listener action listener.
   */
  public void removeActionListener(ActionListener listener) {
    listenerList.remove(ActionListener.class, listener);
  }

  /**
   * Returns about box dialog
   *
   * @return An about box dialog
   */
  @Override
  public JDialog getAboutBox() {
    return DefaultOrchidAbout.getDefaultAboutBox(getClass());
  }

  /**
   * internal use.
   *
   * @param aboutBox about dialog
   */
  public void setAboutBox(JDialog aboutBox) {
    // no contents need.
  }
}
