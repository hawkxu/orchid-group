/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.OrchidUtils;
import com.xzq.osc.field.Range.Option;
import com.xzq.osc.field.Range.Sign;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author zqxu
 */
public class OptionsPopupMenu extends JPopupMenu
        implements ActionListener {

  private Sign sign;
  private Option selected;
  private Map<Option, JMenuItem> menuItemMap;

  public OptionsPopupMenu() {
    this(Sign.I);
  }

  public OptionsPopupMenu(Sign sign) {
    super();
    if (sign != null) {
      this.sign = sign;
    } else {
      this.sign = Sign.I;
    }
    initializeMenuItems();
  }

  protected void initializeMenuItems() {
    menuItemMap = new EnumMap<Option, JMenuItem>(Option.class);
    for (Option option : Option.values()) {
      createMenuItem(sign, option);
    }
  }

  private void createMenuItem(Sign sign, Option option) {
    JMenuItem item = new JMenuItem();
    item.addActionListener(this);
    item.setActionCommand("" + option);
    item.setText(RangeUtils.getOptionText(option));
    item.setIcon(RangeUtils.getOptionIcon(sign, option));
    add(item);
    menuItemMap.put(option, item);
  }

  public Sign getSign() {
    return sign;
  }

  public Option getSelected() {
    return selected;
  }

  public void updateMenuItems(Range range) {
    updateItemIcon(range == null ? Sign.I : range.getSign());
    Option option = range == null ? null : range.getOption();
    Object lowValue = range == null ? null : range.getLowValue();
    boolean bt = option == Option.BT || option == Option.NB;
    boolean lk = RangeUtils.hasWildcard(lowValue);
    boolean cm = lowValue instanceof Comparable
            && !OrchidUtils.isEmpty(lowValue);
    menuItemMap.get(Option.LK).setVisible(lk);
    menuItemMap.get(Option.NL).setVisible(lk);
    menuItemMap.get(Option.BT).setVisible(bt);
    menuItemMap.get(Option.NB).setVisible(bt);
    menuItemMap.get(Option.EQ).setVisible(!bt);
    menuItemMap.get(Option.NE).setVisible(!bt);
    menuItemMap.get(Option.LT).setVisible(!bt && cm);
    menuItemMap.get(Option.LE).setVisible(!bt && cm);
    menuItemMap.get(Option.GT).setVisible(!bt && cm);
    menuItemMap.get(Option.GE).setVisible(!bt && cm);
  }

  private void updateItemIcon(Sign sign) {
    if (sign == this.sign) {
      return;
    }
    this.sign = sign;
    for (Option option : Option.values()) {
      menuItemMap.get(option).setIcon(
              RangeUtils.getOptionIcon(sign, option));
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    selected = Option.valueOf(evt.getActionCommand());
    fireActionPerformed(evt.getActionCommand());
  }

  public void addActionListener(ActionListener l) {
    listenerList.add(ActionListener.class, l);
  }

  public void removeActionListener(ActionListener l) {
    listenerList.remove(ActionListener.class, l);
  }

  protected void fireActionPerformed(String actionCommand) {
    ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
            actionCommand);
    for (ActionListener l : listenerList.getListeners(ActionListener.class)) {
      l.actionPerformed(evt);
    }
  }
}
