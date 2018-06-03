/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.tabbedpane.TabClosingEvent;
import com.xzq.osc.tabbedpane.TabClosingListener;
import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.plaf.LookAndFeelManager;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

/**
 *
 * @author zqxu
 */
public class JocTabbedPane extends JTabbedPane implements OrchidAboutIntf {

  private static final String uiClassID = "OrchidTabbedPaneUI";
  private Color borderColor;
  private Color selectedColor;
  private boolean showListButton;
  private boolean showCloseButton;
  private boolean showCloseButtonOnTab;
  private Map<Component, Boolean> closableMap;

  /**
   *
   */
  public JocTabbedPane() {
    this(TOP, SCROLL_TAB_LAYOUT);
  }

  /**
   *
   * @param tabPlacement
   */
  public JocTabbedPane(int tabPlacement) {
    this(tabPlacement, SCROLL_TAB_LAYOUT);
  }

  /**
   *
   * @param tabPlacement
   * @param tabLayoutPolicy
   */
  public JocTabbedPane(int tabPlacement, int tabLayoutPolicy) {
    super(tabPlacement, tabLayoutPolicy);
    initializeLocalVars();
  }

  /**
   *
   * @return
   */
  @Override
  public String getUIClassID() {
    return uiClassID;
  }

  /**
   *
   */
  @Override
  public void updateUI() {
    LookAndFeelManager.installOrchidUI();
    super.updateUI();
  }

  private void initializeLocalVars() {
    setBorderColor(new Color(130, 160, 190));
    setSelectedColor(new Color(220, 220, 255));
    setShowListButton(true);
    setShowCloseButton(true);
    closableMap = new HashMap<Component, Boolean>();
  }

  /**
   *
   * @param index
   */
  @Override
  public void removeTabAt(int index) {
    Component tab = getComponentAt(index);
    super.removeTabAt(index);
    if (indexOfComponent(tab) == -1) {
      closableMap.remove(tab);
    }
  }

  /**
   *
   * @return
   */
  public Color getBorderColor() {
    return borderColor;
  }

  /**
   *
   * @param borderColor
   */
  public void setBorderColor(Color borderColor) {
    Color old = this.borderColor;
    if (!OrchidUtils.equals(old, borderColor)) {
      this.borderColor = borderColor;
      firePropertyChange("borderColor", old, borderColor);
    }
  }

  /**
   *
   * @return
   */
  public Color getSelectedColor() {
    return selectedColor;
  }

  /**
   *
   * @param selectedColor
   */
  public void setSelectedColor(Color selectedColor) {
    Color old = this.selectedColor;
    if (!OrchidUtils.equals(old, selectedColor)) {
      this.selectedColor = selectedColor;
      firePropertyChange("selectedColor", old, selectedColor);
    }
  }

  /**
   *
   * @return
   */
  public boolean getShowListButton() {
    return showListButton;
  }

  /**
   *
   * @param showListButton
   */
  public void setShowListButton(boolean showListButton) {
    boolean old = this.showListButton;
    if (!OrchidUtils.equals(old, showListButton)) {
      this.showListButton = showListButton;
      firePropertyChange("showListButton", old, showListButton);
    }
  }

  /**
   *
   * @return
   */
  public boolean getShowCloseButton() {
    return showCloseButton;
  }

  /**
   *
   * @param showCloseButton
   */
  public void setShowCloseButton(boolean showCloseButton) {
    boolean old = this.showCloseButton;
    if (!OrchidUtils.equals(old, showCloseButton)) {
      this.showCloseButton = showCloseButton;
      firePropertyChange("showCloseButton", old, showCloseButton);
    }
  }

  /**
   * Returns whether show close button on tab or not. default is true.
   *
   * @return true for show close button on tab and false not.
   */
  public boolean getShowCloseButtonOnTab() {
    return showCloseButtonOnTab;
  }

  /**
   * Set whether show close button on tab or not.
   *
   * @param showCloseButtonOnTab true for show close button on tab and false
   * not.
   */
  public void setShowCloseButtonOnTab(boolean showCloseButtonOnTab) {
    if (this.showCloseButtonOnTab != showCloseButtonOnTab) {
      this.showCloseButtonOnTab = showCloseButtonOnTab;
      if (showCloseButtonOnTab) {
        setShowCloseButton(true);
      }
      firePropertyChange("showCloseButtonOnTab", !showCloseButtonOnTab,
              showCloseButtonOnTab);
    }
  }

  /**
   *
   * @param tab
   * @return
   */
  public boolean isTabClosable(Component tab) {
    if (indexOfComponent(tab) == -1) {
      return false;
    } else {
      return !closableMap.containsKey(tab);
    }
  }

  /**
   *
   * @param tabIndex
   * @return
   */
  public boolean isTabClosable(int tabIndex) {
    if (tabIndex < 0 || tabIndex >= getTabCount()) {
      return false;
    } else {
      return isTabClosable(getComponentAt(tabIndex));
    }
  }

  /**
   *
   * @param tab
   * @param closable
   */
  public void setTabClosable(Component tab, boolean closable) {
    if (isTabClosable(tab) != closable) {
      if (closable) {
        closableMap.remove(tab);
      } else {
        closableMap.put(tab, Boolean.FALSE);
      }
      firePropertyChange("tabClosable", !closable, closable);
    }
  }

  /**
   *
   * @param tabIndex
   * @param closable
   */
  public void setTabClosable(int tabIndex, boolean closable) {
    Component tab = getComponentAt(tabIndex);
    if (tab != null) {
      setTabClosable(tab, closable);
    }
  }

  //<editor-fold defaultstate="collapsed" desc="Closing Tab Action">
  /**
   *
   * @param l
   */
  public void addTabClosingListener(TabClosingListener l) {
    listenerList.add(TabClosingListener.class, l);
  }

  /**
   *
   * @param l
   */
  public void removeTabClosingListener(TabClosingListener l) {
    listenerList.remove(TabClosingListener.class, l);
  }

  /**
   *
   * @param closingTabIndex
   */
  public void fireTabClosing(int closingTabIndex) {
    TabClosingListener[] listeners = listenerList.getListeners(
            TabClosingListener.class);
    if (listeners.length > 0) {
      TabClosingEvent evt = new TabClosingEvent(this, closingTabIndex);
      for (TabClosingListener l : listeners) {
        l.ClosingTab(evt);
      }
    } else {
      doDefaultTabCloseAction(closingTabIndex);
    }
  }

  /**
   *
   * @param closingTabIndex
   */
  protected void doDefaultTabCloseAction(int closingTabIndex) {
    removeTabAt(closingTabIndex);
  }
  //</editor-fold>

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
