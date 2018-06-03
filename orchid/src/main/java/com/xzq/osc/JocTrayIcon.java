/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.trayicon.TrayIconEvent;
import com.xzq.osc.trayicon.TrayIconListener;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Simply install and remove system tray icon.
 *
 * @author zqxu
 */
public class JocTrayIcon implements OrchidAboutIntf {

  private String actionCommand;
  private Window window;
  private Icon icon;
  private Image image;
  private String toolTipText;
  private JPopupMenu popupMenu;
  private PopupMenuListener popupMenuHandler;
  private JDialog popupContainer;
  private boolean iconAutoSize;
  private boolean autoInstallAtOpened;
  private boolean autoInstallAtHidden;
  private boolean autoRemoveAtShown;
  private boolean autoHideMinWindow;
  private WindowHandler windowAncestorHandler;
  private boolean trayIconInstalled;
  private ActionListener trayIconActionHandler;
  private MouseAdapter trayIconMouseHandler;
  private int clickCountToShowWindow = 2;
  protected TrayIcon trayIcon;
  protected EventListenerList listenerList;
  private long restoreAt;

  /**
   * Constructor.
   */
  public JocTrayIcon() {
    this(null, null);
  }

  /**
   * Constructor with icon.
   *
   * @param icon icon.
   */
  public JocTrayIcon(Icon icon) {
    this(icon, null);
  }

  /**
   * Constructor with icon and popup menu.
   *
   * @param icon icon.
   * @param popupMenu popup menu.
   */
  public JocTrayIcon(Icon icon, JPopupMenu popupMenu) {
    super();
    this.icon = icon;
    this.popupMenu = popupMenu;
    autoInstallAtOpened = true;
    listenerList = new EventListenerList();
  }

  /**
   * Adds the specified action listener to receive
   * <code>ActionEvent</code>s from this
   * <code>TrayIcon</code>.
   *
   * @param listener the action listener
   */
  public void addActionListener(ActionListener listener) {
    listenerList.add(ActionListener.class, listener);
  }

  /**
   * Removes the specified action listener. Calling this method with
   * <code>null</code> or an invalid value has no effect.
   *
   * @param listener the action listener
   */
  public void removeActionListener(ActionListener listener) {
    listenerList.remove(ActionListener.class, listener);
  }

  /**
   * Adds the specified mouse listener to receive mouse events from this
   * <code>TrayIcon</code>. Calling this method with a
   * <code>null</code> value has no effect.
   *
   * @param listener the mouse listener
   */
  public void addMouseListener(MouseListener listener) {
    listenerList.add(MouseListener.class, listener);
  }

  /**
   * Removes the specified mouse listener. Calling this method with
   * <code>null</code> or an invalid value has no effect.
   *
   * @param listener the mouse listener
   */
  public void removeMouseListener(MouseListener listener) {
    listenerList.remove(MouseListener.class, listener);
  }

  /**
   * Adds the specified mouse listener to receive mouse-motion events from this
   * <code>TrayIcon</code>. Calling this method with a
   * <code>null</code> value has no effect.
   *
   * @param listener the mouse listener
   */
  public void addMouseMotionListener(MouseMotionListener listener) {
    listenerList.add(MouseMotionListener.class, listener);
  }

  /**
   * Removes the specified mouse-motion listener. Calling this method with
   * <code>null</code> or an invalid value has no effect.
   *
   * @param listener the mouse listener
   */
  public void removeMouseMotionListener(MouseMotionListener listener) {
    listenerList.remove(MouseMotionListener.class, listener);
  }

  /**
   *
   * @param l
   */
  public void addPropertyChangeListener(PropertyChangeListener l) {
    listenerList.add(PropertyChangeListener.class, l);
  }

  /**
   *
   * @param l
   */
  public void removePropertyChangeListener(PropertyChangeListener l) {
    listenerList.remove(PropertyChangeListener.class, l);
  }

  /**
   *
   * @param l
   */
  public void addTrayIconListener(TrayIconListener l) {
    listenerList.add(TrayIconListener.class, l);
  }

  /**
   *
   * @param l
   */
  public void removeTrayIconListener(TrayIconListener l) {
    listenerList.remove(TrayIconListener.class, l);
  }

  /**
   * Displays a popup message near the tray icon. The message will disappear
   * after a time or if the user clicks on it. Clicking on the message may
   * trigger an {@code ActionEvent}.<br> Calling this method before tray icon
   * installation has no effect.
   *
   * @param caption the caption displayed above the text, usually in bold; may
   * be null
   * @param text the text displayed for the particular message; may be null
   * @param messageType an enum indicating the message type
   */
  public void displayMessage(String caption, String text,
          MessageType messageType) {
    if (trayIcon != null) {
      trayIcon.displayMessage(caption, text, messageType);
    }
  }

  /**
   * Returns the command name of the action event fired by this tray icon.
   *
   * @return the action command name, or <code>null</code> if none exists
   */
  public String getActionCommand() {
    return actionCommand;
  }

  /**
   * Sets the command name for the action event fired by this tray icon. By
   * default, this action command is set to
   * <code>null</code>.
   *
   * @param actionCommand a string used to set the tray icon's action command.
   */
  public void setActionCommand(String actionCommand) {
    String old = this.actionCommand;
    if (!OrchidUtils.equals(old, actionCommand)) {
      this.actionCommand = actionCommand;
      updateTrayIconProperties();
      firePropertyChange("actionCommand", old, actionCommand);
    }
  }

  public Window getWindow() {
    return window;
  }

  public void setWindow(Window window) {
    Window old = this.window;
    if (old != window) {
      if (old != null) {
        uninstallWindowListeners(old);
      }
      this.window = window;
      if (window != null) {
        installWindowListeners(window);
      }
      firePropertyChange("window", old, window);
    }
  }

  private void installWindowListeners(Window window) {
    WindowHandler handler = getWindowHandler();
    window.addWindowListener(handler);
    window.addComponentListener(handler);
    window.addPropertyChangeListener("iconImage", handler);
  }

  private void uninstallWindowListeners(Window window) {
    WindowHandler handler = getWindowHandler();
    window.removeWindowListener(handler);
    window.removeComponentListener(handler);
    window.removePropertyChangeListener("iconImage", handler);
  }

  /**
   * Returns the current icon used for this
   * <code>TrayIcon</code>.
   *
   * @return the icon
   */
  public Icon getIcon() {
    return icon;
  }

  /**
   * Sets the image for this TrayIcon.
   *
   * @param icon the icon, may be null.
   */
  public void setIcon(Icon icon) {
    Icon old = this.icon;
    if (!OrchidUtils.equals(old, icon)) {
      this.icon = icon;
      this.image = null;
      updateTrayIconProperties();
      firePropertyChange("icon", old, icon);
    }
  }

  /**
   * Returns the popup menu associated with this
   * <code>TrayIcon</code>.
   *
   * @return the popup menu or <code>null</code> if none exists
   */
  public JPopupMenu getPopupMenu() {
    return popupMenu;
  }

  /**
   * Sets the popup menu for this
   * <code>TrayIcon</code>. If
   * <code>popup</code> is
   * <code>null</code>, no popup menu will be associated with this
   * <code>TrayIcon</code>.
   *
   * @param popupMenu a <code>PopupMenu</code> or <code>null</code> to remove
   * any popup menu
   */
  public void setPopupMenu(JPopupMenu popupMenu) {
    JPopupMenu old = this.popupMenu;
    if (!OrchidUtils.equals(old, popupMenu)) {
      if (old != null) {
        old.removePopupMenuListener(getPopupMenuHandler());
      }
      this.popupMenu = popupMenu;
      if (popupMenu != null) {
        popupMenu.addPopupMenuListener(getPopupMenuHandler());
      }
      firePropertyChange("popupMenu", old, popupMenu);
    }
  }

  /**
   * Create and returns a PopupMenuListener for internal TrayIcon object.
   *
   * @return the popup menu listener
   */
  protected PopupMenuListener getPopupMenuHandler() {
    if (popupMenuHandler == null) {
      popupMenuHandler = new PopupMenuHandler();
    }
    return popupMenuHandler;
  }

  public String getToolTipText() {
    return toolTipText;
  }

  /**
   * Sets the tooltip string for this
   * <code>TrayIcon</code>. The tooltip is displayed automatically when the
   * mouse hovers over the icon. Setting the tooltip to
   * <code>null</code> removes any tooltip text.
   *
   * @param toolTipText the string for the tooltip; if the value is
   * <code>null</code> no tooltip is shown
   */
  public void setToolTipText(String toolTipText) {
    String oldText = getToolTipText();
    if (!OrchidUtils.equals(oldText, toolTipText)) {
      this.toolTipText = toolTipText;
      updateTrayIconProperties();
      firePropertyChange("toolTipText", oldText, toolTipText);
    }
  }

  /**
   * Returns the size, in pixels, of the space that the tray icon occupies in
   * the system tray. For the tray icon that is not yet added to the system
   * tray, the returned size is equal to the result of the
   * {@link SystemTray#getTrayIconSize}.
   *
   * @return the size of the tray icon, in pixels
   */
  public Dimension getTrayIconSize() {
    return SystemTray.getSystemTray().getTrayIconSize();
  }

  /**
   * Returns the value of the auto-size property.
   *
   * @return <code>true</code> if the icon will be auto-sized,
   * <code>false</code> otherwise
   */
  public boolean isIconAutoSize() {
    return iconAutoSize;
  }

  /**
   * Sets the auto-size property. Auto-size determines whether the tray image is
   * automatically sized to fit the space allocated for the image on the tray.
   * By default, the auto-size property is set to
   * <code>false</code>.
   *
   * @param iconAutoSize <code>true</code> to auto-size the image,
   * <code>false</code> otherwise
   */
  public void setIconAutoSize(boolean iconAutoSize) {
    boolean old = this.iconAutoSize;
    if (!OrchidUtils.equals(old, iconAutoSize)) {
      this.iconAutoSize = iconAutoSize;
      updateTrayIconProperties();
      firePropertyChange("iconAutoSize", old, iconAutoSize);
    }
  }

  /**
   * Returns the value of the auto-install-at-start property.
   *
   * @return <code>true</code> if the icon will be auto install at window
   * ancestor opened, <code>false</code> otherwise
   */
  public boolean getAutoInstallAtOpened() {
    return autoInstallAtOpened;
  }

  /**
   * Sets the auto-install-at-start property. true for auto install tay icon at
   * window ancestor opened, no effect if this TrayIcon not inside window. By
   * default, the auto-install-at-start property is set to
   * <code>true</code>.
   *
   * @param autoInstallAtOpened
   */
  public void setAutoInstallAtOpened(boolean autoInstallAtOpened) {
    boolean old = this.autoInstallAtOpened;
    if (old != autoInstallAtOpened) {
      this.autoInstallAtOpened = autoInstallAtOpened;
      firePropertyChange("autoInstall", old, autoInstallAtOpened);
    }
  }

  /**
   * Returns the value of the click-count-to-show-window property.
   *
   * @return click count to show window, zero for ignore.
   */
  public int getClickCountToShowWindow() {
    return clickCountToShowWindow;
  }

  /**
   * Specifies the number of clicks needed to show window ancestor. no effect is
   * this TrayIcon not inside window.
   *
   * @param clickCount an int specifying the number of clicks needed to show
   * window ancestor.
   */
  public void setClickCountToShowWindow(int clickCount) {
    int old = this.clickCountToShowWindow;
    if (old != clickCountToShowWindow) {
      this.clickCountToShowWindow = clickCount;
      firePropertyChange("clickCountToShowWindow", old, clickCount);
    }
  }

  /**
   * install this TrayIcon to system tray, no effect if this TrayIcon already
   * installed.
   */
  public void installTrayIcon() {
    if (isTrayIconInstalled()) {
      return;
    }
    if (trayIcon == null) {
      initializeTrayIcon();
    } else {
      updateTrayIconProperties();
    }
    try {
      SystemTray.getSystemTray().add(trayIcon);
    } catch (AWTException ex) {
      throw new IllegalStateException(ex);
    }
    trayIconInstalled = true;
    fireTrayIconInstalled();
  }

  /**
   * initialize the internal java.awt.TrayIcon object.
   */
  protected void initializeTrayIcon() {
    trayIcon = new TrayIcon(getImage());
    trayIcon.setActionCommand(getActionCommand());
    trayIcon.setImageAutoSize(isIconAutoSize());
    trayIcon.setToolTip(getToolTipText());
    trayIcon.addActionListener(getTrayIconActionHandler());
    trayIcon.addMouseListener(getTrayIconMouseHandler());
    trayIcon.addMouseMotionListener(getTrayIconMouseHandler());
  }

  private Image getImage() {
    if (image == null) {
      image = getImageFromIcon(getIcon());
    }
    if (image == null) {
      image = getImageFromWindow();
    }
    return image;
  }

  private Image getImageFromIcon(Icon icon) {
    if (icon == null) {
      return null;
    }
    BufferedImage tmpImage = new BufferedImage(icon.getIconWidth(),
            icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics graphics = tmpImage.createGraphics();
    icon.paintIcon(null, graphics, 0, 0);
    graphics.dispose();
    return tmpImage;
  }

  private Image getImageFromWindow() {
    if (window != null) {
      java.util.List<Image> images = window.getIconImages();
      if (!OrchidUtils.isEmpty(images)) {
        return getNearestSize(images);
      }
    }
    return getImageFromIcon(UIManager.getIcon("InternalFrame.icon"));
  }

  private Image getNearestSize(java.util.List<Image> imageList) {
    int diff = Integer.MAX_VALUE;
    Image suitedImage = null;
    Dimension size = getTrayIconSize();
    for (Image tmpImage : imageList) {
      int tmp = Math.abs(tmpImage.getWidth(null) - size.width)
              + Math.abs(tmpImage.getHeight(null) - size.height);
      if (tmp < diff) {
        diff = tmp;
        suitedImage = tmpImage;
      }
    }
    return suitedImage;
  }

  /**
   * Create and returns action listener for internal java.awt.TrayIcon object.
   *
   * @return action listener.
   */
  protected ActionListener getTrayIconActionHandler() {
    if (trayIconActionHandler == null) {
      trayIconActionHandler = new TrayIconActionHandler();
    }
    return trayIconActionHandler;
  }

  /**
   * Create and returns mouse listener for internal java.awt.TrayIcon object.
   *
   * @return mouse listener
   */
  protected MouseAdapter getTrayIconMouseHandler() {
    if (trayIconMouseHandler == null) {
      trayIconMouseHandler = new TrayIconMouseHandler();
    }
    return trayIconMouseHandler;
  }

  /**
   * Remove this TrayIcon from system tray, no effect if TrayIcon had not been
   * installed.
   */
  public void uninstallTrayIcon() {
    if (isTrayIconInstalled()) {
      SystemTray.getSystemTray().remove(trayIcon);
      trayIconInstalled = false;
      fireTrayIconUninstalled();
    }
  }

  /**
   * Returns whether this TrayIcon has installed.
   *
   * @return true if this TrayIcon has been installed, otherwise false.
   */
  public boolean isTrayIconInstalled() {
    return trayIconInstalled;
  }

  private void updateTrayIconProperties() {
    if (trayIcon != null) {
      trayIcon.setActionCommand(getActionCommand());
      trayIcon.setImage(getImage());
      trayIcon.setImageAutoSize(isIconAutoSize());
      trayIcon.setToolTip(getToolTipText());
    }
  }

  /**
   * Show popup menu for TrayIcon PopupTrigger event.
   *
   * @param locationOnScreen mouse point location on screen.
   */
  protected void showPopupMenu(Point locationOnScreen) {
    JPopupMenu popup = getPopupMenu();
    if (popup != null) {
      showPopupContainer(locationOnScreen);
      OrchidUtils.showPopupMenu(popup, popupContainer);
    }
  }

  private void showPopupContainer(Point locationOnScreen) {
    if (popupContainer == null) {
      popupContainer = new JDialog();
      popupContainer.setUndecorated(true);
      popupContainer.setAlwaysOnTop(true);
    }
    popupContainer.setLocation(locationOnScreen);
    popupContainer.setVisible(true);
  }

  /**
   * Show window ancestor when user click at this TrayIcon with count value of
   * the property clickCountToShowWindow.
   */
  protected void showWindow() {
    if (window == null) {
      return;
    }
    if (!window.isVisible()) {
      window.setVisible(true);
    }
    if (window instanceof Frame
            && ((Frame) window).getState() == Frame.ICONIFIED) {
      restoreAt = new Date().getTime();
      ((Frame) window).setState(Frame.NORMAL);
    }
    window.toFront();
    if (window.getFocusOwner() != null) {
      window.getFocusOwner().requestFocus();
    }
  }

  /**
   * Returns the value of auto-install-at-hidden property.
   *
   * @return true for auto install tray icon at window ancestor been hidden,
   * otherwise false.
   */
  public boolean getAutoInstallAtHidden() {
    return autoInstallAtHidden;
  }

  /**
   * Sets the value of auto-install-at-hidden property. no effect if this
   * TrayIcon not inside window.
   *
   * @param autoInstallAtHidden true for auto install tray icon at window
   * ancestor been hidden, otherwise false.
   */
  public void setAutoInstallAtHidden(boolean autoInstallAtHidden) {
    if (autoInstallAtHidden != this.autoInstallAtHidden) {
      boolean old = this.autoInstallAtHidden;
      this.autoInstallAtHidden = autoInstallAtHidden;
      firePropertyChange("autoInstallAtHidden", old, autoInstallAtHidden);
    }
  }

  /**
   * Returns the value of auto-remove-at-shown property.
   *
   * @return true for auto remove tray icon at window ancestor been visible from
   * hidden, otherwise false.
   */
  public boolean getAutoRemoveAtShown() {
    return autoRemoveAtShown;
  }

  /**
   * Sets the value of auto-remove-at-shown property.
   *
   * @param autoRemoveAtShown true for auto remove tray icon at window ancestor
   * been visible from hidden, otherwise false.
   */
  public void setAutoRemoveAtShown(boolean autoRemoveAtShown) {
    if (autoRemoveAtShown != this.autoRemoveAtShown) {
      boolean old = this.autoRemoveAtShown;
      this.autoRemoveAtShown = autoRemoveAtShown;
      firePropertyChange("autoRemoveAtShown", old, autoRemoveAtShown);
    }
  }

  /**
   * Returns whether auto hide minimized window or not.
   *
   * @return true for auto hide minimized window or false not.
   */
  public boolean getAutoHideMinWindow() {
    return autoHideMinWindow;
  }

  /**
   * Sets whether auto hide minimized window or not. default is false.
   *
   * @param autoHideMinWindow true or false.
   */
  public void setAutoHideMinWindow(boolean autoHideMinWindow) {
    if (autoHideMinWindow != this.autoHideMinWindow) {
      boolean old = this.autoHideMinWindow;
      this.autoHideMinWindow = autoHideMinWindow;
      firePropertyChange("autoHideMinWindow", old, autoHideMinWindow);
    }
  }

  private WindowHandler getWindowHandler() {
    if (windowAncestorHandler == null) {
      windowAncestorHandler = new WindowHandler();
    }
    return windowAncestorHandler;
  }

  /**
   * Invoked when icon image of window ancestor has been changed.
   */
  protected void windowIconImageChanged() {
    if (getIcon() == null) {
      this.image = null;
      updateTrayIconProperties();
    }
  }

  /**
   * Invoked when window has been opened.
   */
  protected void windowOpened() {
    if (getAutoInstallAtOpened()) {
      installTrayIcon();
    }
  }

  /**
   * The window attached to is minimized.
   */
  protected void windowIconified() {
    long current = new Date().getTime();
    if (current - restoreAt < 500) {
      if (window instanceof Frame) {
        ((Frame) window).setState(Frame.NORMAL);
      }
    } else if (getAutoHideMinWindow()) {
      window.setVisible(false);
    }
  }

  /**
   * Invoked when window ancestor visibility changed, that Shown to Hidden or
   * Hidden to Shown.
   */
  protected void windowVisibilityChanged() {
    boolean visible = window.isVisible();
    if (visible && getAutoRemoveAtShown()) {
      uninstallTrayIcon();
    } else if (!visible && getAutoInstallAtHidden()) {
      installTrayIcon();
    }
  }

  /**
   * Returns an array of all the action listeners registered on this
   * <code>TrayIcon</code>.
   *
   * @return all of the <code>ActionListeners</code> registered on this
   * <code>TrayIcon</code> or an empty array if no action listeners are
   * currently registered
   */
  protected ActionListener[] getActionListeners() {
    return listenerList.getListeners(ActionListener.class);
  }

  /**
   * fire TrayIcon action performed event.
   *
   * @param evt ActionEvent object.
   */
  protected void fireTrayIconActionPerformed(ActionEvent evt) {
    for (ActionListener listener : getActionListeners()) {
      listener.actionPerformed(evt);
    }
  }

  /**
   * Returns an array of all the mouse listeners registered on this
   * <code>TrayIcon</code>.
   *
   * @return all of the <code>MouseListeners</code> registered on this
   * <code>TrayIcon</code> or an empty array if no mouse listeners are currently
   * registered
   */
  protected MouseListener[] getMouseListeners() {
    return listenerList.getListeners(MouseListener.class);
  }

  /**
   * fire TrayIcon mouse clicked event.
   *
   * @param evt MouseEvent object.
   */
  protected void fireTrayIconMouseClicked(MouseEvent evt) {
    for (MouseListener listener : getMouseListeners()) {
      listener.mouseClicked(evt);
    }
  }

  /**
   * fire TrayIcon mouse pressed event.
   *
   * @param evt MouseEvent object.
   */
  protected void fireTrayIconMousePressed(MouseEvent evt) {
    for (MouseListener listener : getMouseListeners()) {
      listener.mousePressed(evt);
    }
  }

  /**
   * fire TrayIcon mouse released event.
   *
   * @param evt MouseEvent object.
   */
  protected void fireTrayIconMouseReleased(MouseEvent evt) {
    for (MouseListener listener : getMouseListeners()) {
      listener.mouseReleased(evt);
    }
  }

  /**
   * fire TrayIcon mouse entered event.
   *
   * @param evt MouseEvent object.
   */
  protected void fireTrayIconMouseEntered(MouseEvent evt) {
    for (MouseListener listener : getMouseListeners()) {
      listener.mouseEntered(evt);
    }
  }

  /**
   * fire TrayIcon mouse exited event.
   *
   * @param evt MouseEvent object.
   */
  protected void fireTrayIconMouseExited(MouseEvent evt) {
    for (MouseListener listener : getMouseListeners()) {
      listener.mouseExited(evt);
    }
  }

  /**
   * Returns an array of all the mouse-motion listeners registered on this
   * <code>TrayIcon</code>.
   *
   * @return all of the <code>MouseInputListeners</code> registered on this
   * <code>TrayIcon</code> or an empty array if no mouse listeners are currently
   * registered
   */
  protected MouseMotionListener[] getMouseMotionListeners() {
    return listenerList.getListeners(MouseMotionListener.class);
  }

  /**
   * fire TrayIcon mouse dragged event.
   *
   * @param evt MouseEvent object.
   */
  protected void fireTrayIconMouseDragged(MouseEvent evt) {
    for (MouseMotionListener listener : getMouseMotionListeners()) {
      listener.mouseDragged(evt);
    }
  }

  /**
   * fire TrayIcon mouse moved event.
   *
   * @param evt MouseEvent object.
   */
  protected void fireTrayIconMouseMoved(MouseEvent evt) {
    for (MouseMotionListener listener : getMouseMotionListeners()) {
      listener.mouseMoved(evt);
    }
  }

  /**
   *
   * @return
   */
  protected PropertyChangeListener[] getPropertyChangeListeners() {
    return listenerList.getListeners(PropertyChangeListener.class);
  }

  /**
   *
   * @param propertyName
   * @param oldValue
   * @param newValue
   */
  protected void firePropertyChange(String propertyName, Object oldValue,
          Object newValue) {
    PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName,
            oldValue, newValue);
    for (PropertyChangeListener listener : getPropertyChangeListeners()) {
      listener.propertyChange(evt);
    }
  }

  /**
   *
   * @return
   */
  protected TrayIconListener[] getTrayIconListeners() {
    return listenerList.getListeners(TrayIconListener.class);
  }

  /**
   *
   */
  protected void fireTrayIconInstalled() {
    TrayIconEvent evt = new TrayIconEvent(this);
    for (TrayIconListener l : getTrayIconListeners()) {
      l.trayIconInstalled(evt);
    }
  }

  /**
   *
   */
  protected void fireTrayIconUninstalled() {
    TrayIconEvent evt = new TrayIconEvent(this);
    for (TrayIconListener l : getTrayIconListeners()) {
      l.trayIconUninstalled(evt);
    }
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

  private class WindowHandler extends WindowAdapter
          implements ComponentListener, PropertyChangeListener {

    @Override
    public void windowOpened(WindowEvent e) {
      JocTrayIcon.this.windowOpened();
    }

    @Override
    public void componentShown(ComponentEvent e) {
      windowVisibilityChanged();
    }

    @Override
    public void componentHidden(ComponentEvent e) {
      windowVisibilityChanged();
    }

    @Override
    public void componentResized(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
      JocTrayIcon.this.windowIconified();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      windowIconImageChanged();
    }
  }

  private class PopupMenuHandler implements PopupMenuListener {

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      popupContainer.setVisible(false);
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
      popupContainer.setVisible(false);
    }
  }

  private class TrayIconActionHandler implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      fireTrayIconActionPerformed(e);
    }
  }

  private class TrayIconMouseHandler extends MouseAdapter {

    @Override
    public void mouseClicked(MouseEvent evt) {
      fireTrayIconMouseClicked(evt);
      if (evt.isConsumed()) {
        return;
      }
      if (evt.getButton() == MouseEvent.BUTTON1
              && evt.getClickCount() == getClickCountToShowWindow()) {
        showWindow();
      }
    }

    @Override
    public void mousePressed(MouseEvent evt) {
      fireTrayIconMousePressed(evt);
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
      fireTrayIconMouseReleased(evt);
      if (evt.isConsumed()) {
        return;
      }
      if (SwingUtilities.isRightMouseButton(evt)
              && evt.getClickCount() == 1) {
        showPopupMenu(evt.getLocationOnScreen());
      }
    }

    @Override
    public void mouseEntered(MouseEvent evt) {
      fireTrayIconMouseEntered(evt);
    }

    @Override
    public void mouseExited(MouseEvent evt) {
      fireTrayIconMouseExited(evt);
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
      fireTrayIconMouseDragged(evt);
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
      fireTrayIconMouseMoved(evt);
    }
  }
}