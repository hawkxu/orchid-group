/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.plaf.LookAndFeelManager;
import com.xzq.osc.print.PrintUtils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * A button with a dropdown arrow, no need to process button click action, just
 * assign a JPopupMenu to dropMenu property, then when user click on button, the
 * popup menu display, if defaultMenuItem property refer to a valid menu item (
 * exists and instance of JMenuItem ), when user click in button main area, the
 * menu item will be executed instead of show popup menu.
 *
 * @author zqxu
 */
public class JocDropButton extends JToggleButton implements OrchidAboutIntf {

  private static final Icon defaultDropArrowIcon
          = new DropArrowIcon(Color.BLACK);
  private static final Icon defaultDisabledDropArrowIcon
          = new DropArrowIcon(Color.GRAY);
  private boolean borderPainting, textPainting;
  private Icon dropArrowIcon;
  private Icon disabledDropArrowIcon;
  private JPopupMenu dropMenu;
  private int defaultMenuItem;
  private boolean dropArrowVisible = true;
  private PopupMenuListener dropMenuHandler;

  /**
   * Constructor of JocDropButton, without dropdown menu and defaultMenuItem set
   * to -1.
   */
  public JocDropButton() {
    this(null, -1);
  }

  /**
   * Constructor of JocDropButton, with dropdown menu and defaultMenuItem set to
   * -1.
   *
   * @param dropMenu
   */
  public JocDropButton(JPopupMenu dropMenu) {
    this(dropMenu, -1);
  }

  /**
   * Constructor of JocDropButton with dropdown menu and defaultMenuItem.
   *
   * @param dropMenu
   * @param defaultMenuItem
   */
  public JocDropButton(JPopupMenu dropMenu, int defaultMenuItem) {
    super();
    this.dropMenu = dropMenu;
    this.defaultMenuItem = defaultMenuItem;
    super.addActionListener(new DropButtonActionHandler());
  }

  /**
   * Returns the JPopupMenu associated with this button, defaults is null.
   *
   * @return JPopupMenu object
   */
  public JPopupMenu getDropMenu() {
    return dropMenu;
  }

  /**
   * Associate a JPopupMenu to this button or null to cancel association.
   *
   * @param dropMenu JPopupMenu object
   */
  public void setDropMenu(JPopupMenu dropMenu) {
    JPopupMenu old = this.dropMenu;
    if (!OrchidUtils.equals(old, dropMenu)) {
      if (old != null) {
        old.removePopupMenuListener(getDropMenuHandler());
      }
      this.dropMenu = dropMenu;
      if (dropMenu != null) {
        dropMenu.addPopupMenuListener(getDropMenuHandler());
      }
      firePropertyChange("dropMenu", old, dropMenu);
    }
  }

  protected PopupMenuListener getDropMenuHandler() {
    if (dropMenuHandler == null) {
      dropMenuHandler = new DropMenuHandler();
    }
    return dropMenuHandler;
  }

  /**
   * Returns default menu item index, defaults is -1.
   *
   * @return default menu item index.
   */
  public int getDefaultMenuItem() {
    return defaultMenuItem;
  }

  /**
   * Set default menu item index, if defaultMenuItem refer to a valid menu item
   * (exists and instance of JMenuItem), then when user click in button main
   * area, the menu item will be executed.
   *
   * @param defaultMenuItem default menu item index
   */
  public void setDefaultMenuItem(int defaultMenuItem) {
    int old = this.defaultMenuItem;
    if (old != defaultMenuItem) {
      this.defaultMenuItem = defaultMenuItem;
      firePropertyChange("defaultMenuItem", old, defaultMenuItem);
    }
  }

  /**
   * Execute button drop action, if executeDefaultMenuItem parameter is true and
   * defaultMenuItem refer to a valid menu item, then the menu item be executed,
   * otherwise the associated JPopupMenu displayed, if no popup menu associated,
   * then no action.
   *
   * @param executeDefaultMenuItem true or false
   */
  public void doDropAction(boolean executeDefaultMenuItem) {
    if (dropMenu == null) {
      return;
    }
    if (executeDefaultMenuItem && defaultMenuItem >= 0
            && defaultMenuItem < dropMenu.getComponentCount()) {
      Component menu = dropMenu.getComponent(defaultMenuItem);
      if (menu instanceof JMenuItem) {
        setSelected(false);
        ((JMenuItem) menu).doClick();
        return;
      }
    }
    OrchidUtils.showPopupMenu(dropMenu, this);
  }

  // <editor-fold defaultstate="collapsed" desc="painting relative...">
  /**
   * Returns width of drop arrow area, defaults is 20.
   *
   * @return width of drop arrow area.
   */
  protected int getDropAreaWidth() {
    return 20;
  }

  /**
   * extend button width to contains drop arrow.
   *
   * @see JComponent#getPreferredSize()
   * @return preferred size
   */
  @Override
  public Dimension getPreferredSize() {
    Dimension size = super.getPreferredSize();
    if (isDropArrowVisible()) {
      size.width += getDropAreaWidth();
    }
    return size;
  }

  /**
   * extend button width to contains drop arrow
   *
   * @see JComponent#getMaximumSize()
   * @return maximum size
   */
  @Override
  public Dimension getMaximumSize() {
    Dimension size = super.getMaximumSize();
    if (isDropArrowVisible()) {
      size.width += getDropAreaWidth();
    }
    return size;
  }

  @Override
  public Dimension getSize() {
    return new Dimension(getWidth(), getHeight());
  }

  /**
   * override for internal paint need.
   *
   * @see JComponent#getWidth()
   * @return width
   */
  @Override
  public int getWidth() {
    return textPainting && isDropArrowVisible()
            ? super.getWidth() - getDropAreaWidth() : super.getWidth();
  }

  /**
   * override for internal paint need.
   *
   * @see AbstractButton#getIcon()
   * @return icon
   */
  @Override
  public Icon getIcon() {
    return borderPainting && isDropArrowVisible() ? null : super.getIcon();
  }

  /**
   * override for internal paint need.
   *
   * @see AbstractButton#getText()
   * @return text
   */
  @Override
  public String getText() {
    return borderPainting && isDropArrowVisible() ? "" : super.getText();
  }

  /**
   * Retruns true if drop arrow currently visible, defaults is true.
   *
   * @return true or false indicate currenty visibility state.
   */
  public boolean isDropArrowVisible() {
    return dropArrowVisible;
  }

  /**
   * Set drop arrow visibility state.
   *
   * @param dropArrowVisible true for visible and false invisible.
   */
  public void setDropArrowVisible(boolean dropArrowVisible) {
    boolean old = this.dropArrowVisible;
    if (old != dropArrowVisible) {
      this.dropArrowVisible = dropArrowVisible;
      revalidate();
      firePropertyChange("dropArrowVisible", old, dropArrowVisible);
    }
  }

  /**
   * override for interal paint need.
   *
   * @see JComponent#paint(java.awt.Graphics)
   */
  @Override
  public void paint(Graphics g) {
    if (isDropArrowVisible()) {
      doSuperPaint(g);
      paintDropArrow(g);
    } else {
      super.paint(g);
    }
  }

  /**
   * execute super class paint to paint button border, icon and text in
   * appropriate area.
   *
   * @param g Graphics device
   */
  protected void doSuperPaint(Graphics g) {
    Shape clip = g.getClip();
    borderPainting = true;
    super.paint(g);
    borderPainting = false;
    textPainting = true;
    g.setClip(getClipRectForIconText());
    super.paint(g);
    textPainting = false;
    g.setClip(clip);
  }

  private Rectangle getClipRectForIconText() {
    Insets insets = getInsets();
    Rectangle clipRect = new Rectangle(getWidth(), getHeight());
    clipRect.width -= insets.right;
    if (LookAndFeelManager.isMetalLookAndFeel()) {
      clipRect.width++;
    } else if (LookAndFeelManager.isWindowsLookAndFeel()) {
      Insets margin = getMargin();
      if (margin != null && margin.right > 1) {
        clipRect.width -= 1;
      } else {
        clipRect.width -= 2;
      }
    } else if (LookAndFeelManager.isNimbusLookAndFeel()) {
      clipRect.width -= 1;
    }
    return clipRect;
  }

  private void paintDropArrow(Graphics g) {
    int x = getSeparatorX();
    Insets insets = getInsets();
    g.setColor(getSeparatorColor());
    g.drawLine(x, insets.top, x, getHeight() - insets.bottom);
    Rectangle dropArrowRect = getDropArrowRect(x + 1);
    Icon dropIcon = isEnabled()
            ? getDropArrowIcon() : getDisabledDropArrowIcon();
    if (dropIcon == null) {
      dropIcon = isEnabled()
              ? defaultDropArrowIcon : defaultDisabledDropArrowIcon;
    }
    PrintUtils.drawIcon((Graphics2D) g, this, dropIcon, dropArrowRect);
  }

  /**
   * Get drop arrow separator line X positon.
   *
   * @return int
   */
  protected int getSeparatorX() {
    if (LookAndFeelManager.isMotifLookAndFeel()
            && !(getParent() instanceof JToolBar)) {
      return getWidth() - getDropAreaWidth() - 6;
    }
    return getWidth() - getDropAreaWidth();
  }

  /**
   * Get drop arrow separator line color
   *
   * @return color
   */
  protected Color getSeparatorColor() {
    if (LookAndFeelManager.isMotifLookAndFeel()) {
      return Color.GRAY;
    } else {
      return Color.LIGHT_GRAY;
    }
  }

  /**
   * Get drop arrow paint rect.
   *
   * @param x drop arrow area start X position.
   * @return drop arrow paint rect.
   */
  protected Rectangle getDropArrowRect(int x) {
    Insets insets = getInsets();
    int width = getDropAreaWidth();
    int height = getHeight() - insets.top - insets.bottom;
    if (LookAndFeelManager.isWindowsLookAndFeel()
            || LookAndFeelManager.isNimbusLookAndFeel()) {
      x--;
    }
    return new Rectangle(x + 2, insets.top, width - 4, height);
  }

  /**
   * Get specified drop arrow icon, defaults is null.
   *
   * @return specified drop arrow icon.
   */
  public Icon getDropArrowIcon() {
    return dropArrowIcon;
  }

  /**
   * Set specified drop arrow icon.
   *
   * @param dropArrowIcon specified drop arrow icon.
   */
  public void setDropArrowIcon(Icon dropArrowIcon) {
    Icon old = this.dropArrowIcon;
    if (!OrchidUtils.equals(old, dropArrowIcon)) {
      this.dropArrowIcon = dropArrowIcon;
      repaint();
      firePropertyChange("dropArrowIcon", old, dropArrowIcon);
    }
  }

  /**
   * Get specified disabled drop arrow icon, defaults is null.
   *
   * @return specified disabled drop arrow icon.
   */
  public Icon getDisabledDropArrowIcon() {
    return disabledDropArrowIcon;
  }

  /**
   * Set specified disabled drop arrow icon.
   *
   * @param disabledDropArrowIcon specified disabled drop arrow icon.
   */
  public void setDisabledDropArrowIcon(Icon disabledDropArrowIcon) {
    Icon old = this.disabledDropArrowIcon;
    if (!OrchidUtils.equals(old, disabledDropArrowIcon)) {
      this.disabledDropArrowIcon = disabledDropArrowIcon;
      repaint();
      firePropertyChange("disabledDropArrowIcon", old, disabledDropArrowIcon);
    }
  }
  // </editor-fold>

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

  private static class DropArrowIcon implements Icon {

    private Color color;

    public DropArrowIcon(Color color) {
      this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.setColor(color);
      g.fillPolygon(new int[]{x, x + 4, x + 8},
              new int[]{y + 2, y + 6, y + 2}, 3);
    }

    @Override
    public int getIconWidth() {
      return 9;
    }

    @Override
    public int getIconHeight() {
      return 9;
    }
  }

  private class DropButtonActionHandler extends MouseAdapter
          implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      Point point = MouseInfo.getPointerInfo().getLocation();
      SwingUtilities.convertPointFromScreen(point, JocDropButton.this);
      doDropAction(point.x < getSeparatorX());
    }
  }

  private class DropMenuHandler implements PopupMenuListener {

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      setSelected(false);
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }
  }
}
