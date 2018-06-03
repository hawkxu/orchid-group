/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.plaf.basic;

import com.xzq.osc.JocTabbedPane;
import com.xzq.osc.OrchidUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;

/**
 *
 * @author zqxu
 */
public class BasicOrchidTabbedPaneUI extends BasicTabbedPaneUI {

  private static int tabCloseButtonWidth = 13;
  protected TabContainer tabContainer;
  protected ScrollableTabSupport tabScroller;
  protected boolean isRunsDirty;
  protected int focusIndex;
  protected boolean calculatedBaseline;
  private Handler handler;
  private int baseline;
  private HashMap<Integer, Integer> mnemonicToIndexMap;
  private InputMap mnemonicInputMap;
  private ArrayList<View> htmlViews;
  private int rolloverTabClose = -1;
  private int pressedTabClose = -1;

  public static ComponentUI createUI(JComponent c) {
    return new BasicOrchidTabbedPaneUI();
  }

  //<editor-fold defaultstate="collapsed" desc="install/uninstall ui">
  /**
   *
   * @param c
   */
  @Override
  public void installUI(JComponent c) {
    super.installUI(c);
    focusIndex = -1;
    calculatedBaseline = false;
  }

  /**
   * install components
   */
  @Override
  protected void installComponents() {
    if (scrollableTabLayoutEnabled()) {
      if (tabScroller == null) {
        tabScroller = new ScrollableTabSupport();
        tabPane.add(tabScroller.tabViewport);
        tabPane.add(tabScroller.tabButtonBar);
      }
    }
    installTabContainer();
  }

  /**
   *
   */
  protected void installTabContainer() {
    for (int i = 0; i < tabPane.getTabCount(); i++) {
      Component tabComponent = tabPane.getTabComponentAt(i);
      if (tabComponent != null) {
        if (tabContainer == null) {
          tabContainer = new TabContainer();
        }
        tabContainer.add(tabComponent);
      }
    }
    if (tabContainer == null) {
      return;
    }
    if (scrollableTabLayoutEnabled()) {
      tabScroller.tabPanel.add(tabContainer);
    } else {
      tabPane.add(tabContainer);
    }
  }

  /**
   *
   */
  @Override
  protected void uninstallComponents() {
    uninstallTabContainer();
    if (scrollableTabLayoutEnabled()) {
      tabPane.remove(tabScroller.tabViewport);
      tabPane.remove(tabScroller.tabButtonBar);
      tabScroller = null;
    }
  }

  /**
   *
   */
  protected void uninstallTabContainer() {
    if (tabContainer == null) {
      return;
    }
    tabContainer.removeAll();
    if (scrollableTabLayoutEnabled()) {
      tabScroller.tabPanel.remove(tabContainer);
    } else {
      tabPane.remove(tabContainer);
    }
    tabContainer = null;
  }

  /**
   *
   */
  @Override
  protected void installListeners() {
    if ((propertyChangeListener = createPropertyChangeListener()) != null) {
      tabPane.addPropertyChangeListener(propertyChangeListener);
    }
    if ((tabChangeListener = createChangeListener()) != null) {
      tabPane.addChangeListener(tabChangeListener);
    }
    if ((mouseListener = createMouseListener()) != null) {
      tabPane.addMouseListener(mouseListener);
    }
    tabPane.addMouseMotionListener(getHandler());
    if ((focusListener = createFocusListener()) != null) {
      tabPane.addFocusListener(focusListener);
    }
    tabPane.addContainerListener(getHandler());
    if (tabPane.getTabCount() > 0) {
      htmlViews = createHTMLArrayList();
    }
  }

  /**
   *
   */
  @Override
  protected void uninstallListeners() {
    if (mouseListener != null) {
      tabPane.removeMouseListener(mouseListener);
      mouseListener = null;
    }
    tabPane.removeMouseMotionListener(getHandler());
    if (focusListener != null) {
      tabPane.removeFocusListener(focusListener);
      focusListener = null;
    }

    tabPane.removeContainerListener(getHandler());
    if (htmlViews != null) {
      htmlViews.clear();
      htmlViews = null;
    }
    if (tabChangeListener != null) {
      tabPane.removeChangeListener(tabChangeListener);
      tabChangeListener = null;
    }
    if (propertyChangeListener != null) {
      tabPane.removePropertyChangeListener(propertyChangeListener);
      propertyChangeListener = null;
    }
    handler = null;
  }

  /**
   *
   * @return
   */
  @Override
  protected MouseListener createMouseListener() {
    return getHandler();
  }

  /**
   *
   * @return
   */
  @Override
  protected FocusListener createFocusListener() {
    return getHandler();
  }

  /**
   *
   * @return
   */
  @Override
  protected ChangeListener createChangeListener() {
    return getHandler();
  }

  /**
   *
   * @return
   */
  @Override
  protected PropertyChangeListener createPropertyChangeListener() {
    return getHandler();
  }

  private Handler getHandler() {
    if (handler == null) {
      handler = new Handler();
    }
    return handler;
  }

  /**
   *
   */
  @Override
  protected void installKeyboardActions() {
    super.installKeyboardActions();
    ActionMap actions = tabPane.getActionMap();
    actions.put(Actions.SET_SELECTED, new Actions(Actions.SET_SELECTED));
    actions.put(Actions.SCROLL_FORWARD, new Actions(Actions.SCROLL_FORWARD));
    actions.put(Actions.SCROLL_BACKWARD, new Actions(Actions.SCROLL_BACKWARD));
    actions.put(Actions.LIST_TAB, new Actions(Actions.LIST_TAB));
    actions.put(Actions.CLOSE_TAB, new Actions(Actions.CLOSE_TAB));
    updateMnemonics();
  }

  /**
   *
   */
  @Override
  protected void uninstallKeyboardActions() {
    super.uninstallKeyboardActions();
    mnemonicToIndexMap = null;
    mnemonicInputMap = null;
  }

  /**
   *
   * @return
   */
  @Override
  protected LayoutManager createLayoutManager() {
    if (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
      return new TabbedPaneScrollLayout();
    } else { /* WRAP_TAB_LAYOUT */
      return new TabbedPaneWrapLayout();
    }
  }

  /**
   *
   * @return
   */
  protected boolean scrollableTabLayoutEnabled() {
    return (tabPane.getLayout() instanceof TabbedPaneScrollLayout);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="paint tab button">
  /**
   *
   * @param g2
   * @param direction
   * @param rect
   * @param enabled
   * @param pressed
   * @param rollover
   */
  protected void paintArrowButton(Graphics2D g2, int direction,
          Rectangle rect, boolean enabled, boolean rollover, boolean pressed) {
    Color origColor = g2.getColor();
    paintButtonState(g2, rect, enabled, rollover, pressed);
    int w = rect.width - 4, h = rect.height - 4;
    int half = Math.max(2, Math.min(h * 3 / 7, w * 3 / 7));
    Polygon triangle = new Polygon();
    switch (direction) {
      case NORTH:
        w = (h = half) * 2 - 1;
        triangle.addPoint(0, h - 1);
        triangle.addPoint(w - 1, h - 1);
        triangle.addPoint(w / 2, 0);
        break;
      case SOUTH:
        w = (h = half) * 2 - 1;
        triangle.addPoint(0, 0);
        triangle.addPoint(w - 1, 0);
        triangle.addPoint(w / 2, h - 1);
        break;
      case EAST:
        h = (w = half) * 2 - 1;
        triangle.addPoint(0, 0);
        triangle.addPoint(0, h - 1);
        triangle.addPoint(w - 1, h / 2);
        break;
      case WEST:
      default:
        h = (w = half) * 2 - 1;
        triangle.addPoint(w - 1, 0);
        triangle.addPoint(w - 1, h - 1);
        triangle.addPoint(0, h / 2);
        break;
    }
    int x = rect.x + (rect.width - w) / 2;
    int y = rect.y + (rect.height - h) / 2;
    g2.translate(x, y);
    if (!enabled) {
      g2.setColor(highlight);
      g2.translate(1, 1);
      paintArrowIcon(g2, triangle);
      g2.translate(-1, -1);
    }
    g2.setColor(enabled ? darkShadow : shadow);
    paintArrowIcon(g2, triangle);
    g2.translate(-x, -y);
    g2.setColor(origColor);
  }

  private void paintArrowIcon(Graphics2D g2, Polygon triangle) {
    g2.drawLine(triangle.xpoints[0], triangle.ypoints[0],
            triangle.xpoints[1], triangle.ypoints[1]);
    g2.drawLine(triangle.xpoints[1], triangle.ypoints[1],
            triangle.xpoints[2], triangle.ypoints[2]);
    g2.drawLine(triangle.xpoints[2], triangle.ypoints[2],
            triangle.xpoints[0], triangle.ypoints[0]);
    g2.fillPolygon(triangle);
  }

  /**
   *
   * @param g2
   * @param rect
   * @param enabled
   * @param pressed
   * @param rollover
   */
  protected void paintListButton(Graphics2D g2, Rectangle rect,
          boolean enabled, boolean rollover, boolean pressed) {
    Color origColor = g2.getColor();
    paintButtonState(g2, rect, enabled, rollover, pressed);
    int h = (rect.height - 4) * 2 / 3;
    h = Math.max(1, (h - 5) / 4) * 4 + 5;
    int w = h * 9 / 10;
    int x = rect.x + (rect.width - w) / 2;
    int y = rect.y + (rect.height - h) / 2;
    g2.translate(x, y);
    if (!enabled) {
      g2.setColor(highlight);
      g2.translate(1, 1);
      paintListIcon(g2, w, h);
      g2.translate(-1, -1);
    }
    g2.setColor(enabled ? darkShadow : shadow);
    paintListIcon(g2, w, h);
    g2.translate(-x, -y);
    g2.setColor(origColor);
  }

  private void paintListIcon(Graphics2D g2, int width, int height) {
    g2.drawRect(0, 0, width - 1, height - 1);
    int spacing = (height - 1) / 4;
    for (int i = 1; i < 4; i++) {
      g2.drawLine(2, i * spacing, width - 3, i * spacing);
    }
  }

  /**
   *
   * @param g2
   * @param rect
   * @param enabled
   * @param pressed
   * @param rollover
   */
  protected void paintCloseButton(Graphics2D g2, Rectangle rect,
          boolean enabled, boolean rollover, boolean pressed) {
    Color origColor = g2.getColor();
    paintButtonState(g2, rect, enabled, rollover, pressed);
    int h = (rect.height - 4) * 3 / 5;
    int w = h + 1;
    int x = rect.x + (rect.width - w) / 2;
    int y = rect.y + (rect.height - h) / 2;
    g2.translate(x, y);
    if (!enabled) {
      g2.setColor(highlight);
      g2.translate(1, 1);
      paintCloseIcon(g2, w, h);
      g2.translate(-1, -1);
    }
    g2.setColor(enabled ? darkShadow : shadow);
    paintCloseIcon(g2, w, h);
    g2.translate(-x, -y);
    g2.setColor(origColor);
  }

  private void paintCloseIcon(Graphics2D g2, int width, int height) {
    g2.drawLine(0, 0, width - 2, height - 1);
    g2.drawLine(1, 0, width - 1, height - 1);
    g2.drawLine(0, height - 1, width - 2, 0);
    g2.drawLine(1, height - 1, width - 1, 0);
  }

  /**
   *
   * @param g2
   * @param rect
   * @param enabled
   * @param rollover
   * @param pressed
   */
  protected void paintButtonState(Graphics2D g2, Rectangle rect,
          boolean enabled, boolean rollover, boolean pressed) {
    if (enabled && rollover && !pressed) {
      g2.setColor(highlight);
      g2.fill(rect);
      g2.setColor(shadow);
      g2.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
    } else if (enabled && pressed) {
      g2.setColor(OrchidUtils.riseDensity(highlight, 15));
      g2.fill(rect);
      g2.setColor(darkShadow);
      g2.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="caculate base line">
  /**
   *
   * @param c
   * @param width
   * @param height
   * @return
   */
  @Override
  public int getBaseline(JComponent c, int width, int height) {
    super.getBaseline(c, width, height);
    int tmpBaseLine = calculateBaselineIfNecessary();
    if (tmpBaseLine != -1) {
      int placement = tabPane.getTabPlacement();
      Insets insets = tabPane.getInsets();
      Insets taInsets = getTabAreaInsets(placement);
      switch (placement) {
        case JTabbedPane.TOP:
          tmpBaseLine += insets.top + taInsets.top;
          return tmpBaseLine;
        case JTabbedPane.BOTTOM:
          tmpBaseLine = height - insets.bottom
                  - taInsets.bottom - maxTabHeight + tmpBaseLine;
          return tmpBaseLine;
        case JTabbedPane.LEFT:
        case JTabbedPane.RIGHT:
          tmpBaseLine += insets.top + taInsets.top;
          return tmpBaseLine;
      }
    }
    return -1;
  }

  /**
   *
   * @return
   */
  protected int calculateBaselineIfNecessary() {
    if (!calculatedBaseline) {
      calculatedBaseline = true;
      baseline = -1;
      if (tabPane.getTabCount() > 0) {
        calculateBaseline();
      }
    }
    return baseline;
  }

  private void calculateBaseline() {
    int tabCount = tabPane.getTabCount();
    int tabPlacement = tabPane.getTabPlacement();
    maxTabHeight = calculateMaxTabHeight(tabPlacement);
    baseline = getBaseline(0);
    int placement = tabPane.getTabPlacement();
    if (placement == TOP || placement == BOTTOM) {
      for (int i = 1; i < tabCount; i++) {
        if (getBaseline(i) != baseline) {
          baseline = -1;
          break;
        }
      }
    } else {
      // left/right, tabs may be different sizes.
      FontMetrics fontMetrics = getFontMetrics();
      int fontHeight = fontMetrics.getHeight();
      int height = calculateTabHeight(tabPlacement, 0, fontHeight);
      for (int i = 1; i < tabCount; i++) {
        int newHeight = calculateTabHeight(tabPlacement, i, fontHeight);
        if (height != newHeight) {
          // assume different baseline
          baseline = -1;
          break;
        }
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="tab placement and region">
  /**
   *
   * @param pane
   * @param i
   * @return
   */
  @Override
  public Rectangle getTabBounds(JTabbedPane pane, int i) {
    ensureCurrentLayout();
    Rectangle tabRect = new Rectangle();
    return getTabBounds(i, tabRect);
  }

  /**
   *
   * @param pane
   * @return
   */
  @Override
  public int getTabRunCount(JTabbedPane pane) {
    ensureCurrentLayout();
    return runCount;
  }

  /**
   *
   */
  protected void ensureCurrentLayout() {
    if (!tabPane.isValid()) {
      tabPane.validate();
    }
    /* If tabPane doesn't have a peer yet, the validate() call will
     * silently fail.  We handle that by forcing a layout if tabPane
     * is still invalid.  See bug 4237677.
     */
    if (!tabPane.isValid()) {
      TabbedPaneLayout layout = (TabbedPaneLayout) tabPane.getLayout();
      layout.calculateLayoutInfo();
    }
  }

  /**
   *
   * @param tabIndex
   * @param dest
   * @return
   */
  @Override
  protected Rectangle getTabBounds(int tabIndex, Rectangle dest) {
    dest.width = rects[tabIndex].width;
    dest.height = rects[tabIndex].height;

    if (scrollableTabLayoutEnabled()) { // SCROLL_TAB_LAYOUT
      // Need to translate coordinates based on viewport location &
      // view position
      Point vpp = tabScroller.tabViewport.getLocation();
      Point viewp = tabScroller.tabViewport.getViewPosition();
      dest.x = rects[tabIndex].x + vpp.x - viewp.x;
      dest.y = rects[tabIndex].y + vpp.y - viewp.y;

    } else { // WRAP_TAB_LAYOUT
      dest.x = rects[tabIndex].x;
      dest.y = rects[tabIndex].y;
    }
    return dest;
  }

  private int getClosestTab(int x, int y) {
    int min = 0;
    int tabCount = Math.min(rects.length, tabPane.getTabCount());
    int max = tabCount;
    int tabPlacement = tabPane.getTabPlacement();
    boolean useX = (tabPlacement == TOP || tabPlacement == BOTTOM);
    int want = (useX) ? x : y;

    while (min != max) {
      int current = (max + min) / 2;
      int minLoc;
      int maxLoc;

      if (useX) {
        minLoc = rects[current].x;
        maxLoc = minLoc + rects[current].width;
      } else {
        minLoc = rects[current].y;
        maxLoc = minLoc + rects[current].height;
      }
      if (want < minLoc) {
        max = current;
        if (min == max) {
          return Math.max(0, current - 1);
        }
      } else if (want >= maxLoc) {
        min = current;
        if (max - min <= 1) {
          return Math.max(current + 1, tabCount - 1);
        }
      } else {
        return current;
      }
    }
    return min;
  }

  /**
   *
   * @param pane
   * @param x
   * @param y
   * @return
   */
  @Override
  public int tabForCoordinate(JTabbedPane pane, int x, int y) {
    return tabForCoordinate(pane, x, y, true);
  }

  private int tabForCoordinate(JTabbedPane pane, int x, int y,
          boolean validateIfNecessary) {
    if (validateIfNecessary) {
      ensureCurrentLayout();
    }
    if (isRunsDirty) {
      return -1;
    }
    Point p = new Point(x, y);
    if (scrollableTabLayoutEnabled()) {
      translatePointToTabPanel(x, y, p);
      Rectangle viewRect = tabScroller.tabViewport.getViewRect();
      if (!viewRect.contains(p)) {
        return -1;
      }
    }
    int tabCount = tabPane.getTabCount();
    for (int i = 0; i < tabCount; i++) {
      if (rects[i].contains(p.x, p.y)) {
        return i;
      }
    }
    return -1;
  }

  /**
   *
   * @param srcx
   * @param srcy
   * @param dest
   * @return
   */
  protected Point translatePointToTabPanel(int srcx, int srcy, Point dest) {
    Point vpp = tabScroller.tabViewport.getLocation();
    Point viewp = tabScroller.tabViewport.getViewPosition();
    dest.x = srcx - vpp.x + viewp.x;
    dest.y = srcy - vpp.y + viewp.y;
    return dest;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="calculate tab area size">
  /**
   *
   * @param tabPlacement
   * @param tabIndex
   * @param metrics
   * @return
   */
  @Override
  protected int calculateTabWidth(int tabPlacement, int tabIndex,
          FontMetrics metrics) {
    int tabWidth = super.calculateTabWidth(tabPlacement, tabIndex, metrics);
    if (isTabCloseButtonVisible(tabIndex)) {
      Insets insets = getTabInsets(tabPlacement, tabIndex);
      tabWidth += tabCloseButtonWidth + insets.right;
    }
    return tabWidth;
  }

  /**
   *
   * @param tabPlacement
   * @param vertRunCount
   * @param maxTabWidth
   * @return
   */
  @Override
  protected int calculateTabAreaWidth(int tabPlacement, int vertRunCount,
          int maxTabWidth) {
    return vertRunCount == 0 ? getTabAreaInsets(tabPlacement).left
            : super.calculateTabAreaWidth(tabPlacement, vertRunCount, maxTabWidth);
  }

  /**
   *
   * @param tabPlacement
   * @param horizRunCount
   * @param maxTabHeight
   * @return
   */
  @Override
  protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount,
          int maxTabHeight) {
    return horizRunCount == 0 ? getTabAreaInsets(tabPlacement).top
            : super.calculateTabAreaHeight(tabPlacement, horizRunCount, maxTabHeight);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="tab area painting">
  /**
   *
   * @param g
   * @param c
   */
  @Override
  public void paint(Graphics g, JComponent c) {
    int selectedIndex = tabPane.getSelectedIndex();
    int tabPlacement = tabPane.getTabPlacement();
    boolean tabsOverlapBorder = UIManager.getBoolean(
            "TabbedPane.tabsOverlapBorder");
    ensureCurrentLayout();
    // Paint content border and tab area
    if (tabsOverlapBorder) {
      paintContentBorder(g, tabPlacement, selectedIndex);
    }
    // If scrollable tabs are enabled, the tab area will be
    // painted by the scrollable tab panel instead.
    //
    if (!scrollableTabLayoutEnabled()) { // WRAP_TAB_LAYOUT
      paintTabArea(g, tabPlacement, selectedIndex);
    }
    if (!tabsOverlapBorder) {
      paintContentBorder(g, tabPlacement, selectedIndex);
    }
  }

  /**
   *
   * @param g
   * @param tabPlacement
   * @param rects
   * @param tabIndex
   * @param iconRect
   * @param textRect
   */
  @Override
  protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects,
          int tabIndex, Rectangle iconRect, Rectangle textRect) {
    super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
    if (isTabCloseButtonVisible(tabIndex)) {
      paintCloseButtonOnTab(g, rects, tabPlacement, tabIndex);
    }
  }

  /**
   *
   * @param g
   * @param rects
   * @param tabPlacement
   * @param tabIndex
   */
  protected void paintCloseButtonOnTab(Graphics g, Rectangle[] rects,
          int tabPlacement, int tabIndex) {
    Rectangle closeButtonRect = calculateTabCloseRect(tabPlacement, tabIndex,
            rects[tabIndex]);
    boolean enabled = tabPane.isEnabledAt(tabIndex);
    boolean rollover = tabIndex == getRolloverTabClose();
    boolean pressed = tabIndex == getPressedTabClose();
    paintCloseButton((Graphics2D) g, closeButtonRect,
            enabled, rollover, pressed);
  }

  /**
   *
   * @param tabPlacement
   * @param tabIndex
   * @param tabRect
   * @return
   */
  protected Rectangle calculateTabCloseRect(int tabPlacement, int tabIndex,
          Rectangle tabRect) {
    Insets insets = getTabInsets(tabPlacement, tabIndex);
    Rectangle closeRect = new Rectangle(tabRect);
    closeRect.x += tabRect.width - tabCloseButtonWidth - insets.right;
    closeRect.width = tabCloseButtonWidth;
    closeRect.y += insets.top;
    closeRect.height -= insets.top + insets.bottom;
    if (closeRect.height > tabCloseButtonWidth) {
      closeRect.y += (closeRect.height - tabCloseButtonWidth) / 2;
      closeRect.height = tabCloseButtonWidth;
    }
    return closeRect;
  }

  private boolean isTabCloseButtonVisible(int tabIndex) {
    JocTabbedPane pane = (JocTabbedPane) tabPane;
    return pane.getShowCloseButton() && pane.getShowCloseButtonOnTab()
            && pane.isTabClosable(tabIndex);
  }

  /**
   *
   * @param tabPlacement
   * @param metrics
   * @param tabIndex
   * @param title
   * @param icon
   * @param tabRect
   * @param iconRect
   * @param textRect
   * @param isSelected
   */
  @Override
  protected void layoutLabel(int tabPlacement, FontMetrics metrics,
          int tabIndex, String title, Icon icon, Rectangle tabRect,
          Rectangle iconRect, Rectangle textRect, boolean isSelected) {
    Rectangle tabRect2 = new Rectangle(tabRect);
    if (isTabCloseButtonVisible(tabIndex)) {
      Insets insets = getTabInsets(tabPlacement, tabIndex);
      tabRect2.width -= tabCloseButtonWidth + insets.right;
    }
    super.layoutLabel(tabPlacement, metrics, tabIndex, title, icon,
            tabRect2, iconRect, textRect, isSelected);
  }

  /**
   *
   * @param g
   * @param tabPlacement
   * @param tabIndex
   * @param x
   * @param y
   * @param w
   * @param h
   * @param isSelected
   */
  @Override
  protected void paintTabBorder(Graphics g, int tabPlacement,
          int tabIndex,
          int x, int y, int w, int h,
          boolean isSelected) {
    JocTabbedPane pane = (JocTabbedPane) tabPane;
    g.setColor(pane.getBorderColor() == null ? shadow : pane.getBorderColor());

    switch (tabPlacement) {
      case LEFT:
        g.drawLine(x + 2, y, x + w, y); // top
        g.drawLine(x + 1, y + 1, x + 1, y + 1); // top-left
        g.drawLine(x, y + 2, x, y + h - 2); // left
        g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1); // bottom-left
        g.drawLine(x + 2, y + h, x + w, y + h); // bottom
        break;
      case RIGHT:
        g.drawLine(x, y, x + w - 2, y); // top
        g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1); // top-right
        g.drawLine(x + w, y + 2, x + w, y + h - 2); // right
        g.drawLine(x + w - 1, y + h - 1, x + w - 1, y + h - 1); // bottom-right
        g.drawLine(x, y + h, x + w - 2, y + h); // bottom
        break;
      case BOTTOM:
        g.drawLine(x, y, x, y + h - 2); // left
        g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1); // bottom-left
        g.drawLine(x + 2, y + h, x + w - 2, y + h); // bottom
        g.drawLine(x + w - 1, y + h - 1, x + w - 1, y + h - 1); // bottom-right
        g.drawLine(x + w, y, x + w, y + h - 2); // right
        break;
      case TOP:
      default:
        g.drawLine(x, y + 2, x, y + h); // left
        g.drawLine(x + 1, y + 1, x + 1, y + 1); // top-left
        g.drawLine(x + 2, y, x + w - 2, y); // top
        g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1); // top-right
        g.drawLine(x + w, y + 2, x + w, y + h); // right dark-shadow
    }
  }

  /**
   *
   * @param g
   * @param tabPlacement
   * @param tabIndex
   * @param x
   * @param y
   * @param w
   * @param h
   * @param isSelected
   */
  @Override
  protected void paintTabBackground(Graphics g, int tabPlacement,
          int tabIndex,
          int x, int y, int w, int h,
          boolean isSelected) {
    JocTabbedPane pane = (JocTabbedPane) tabPane;
    Color selectedColor = pane.getSelectedColor();
    g.setColor(!isSelected || selectedColor == null
            ? tabPane.getBackgroundAt(tabIndex) : selectedColor);
    switch (tabPlacement) {
      case LEFT:
        g.fillRect(x + 1, y + 1, w - 1, h - 1);
        break;
      case RIGHT:
        g.fillRect(x, y + 1, w, h - 1);
        break;
      case BOTTOM:
        g.fillRect(x + 1, y, w - 1, h);
        break;
      case TOP:
      default:
        g.fillRect(x + 1, y + 1, w - 1, h - 1);
    }
  }

  /**
   *
   * @param g
   * @param tabPlacement
   * @param selectedIndex
   * @param x
   * @param y
   * @param w
   * @param h
   */
  @Override
  protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
          int selectedIndex, int x, int y, int w, int h) {
    JocTabbedPane pane = (JocTabbedPane) tabPane;
    Rectangle selRect = selectedIndex < 0 ? null
            : getTabBounds(selectedIndex, calcRect);
    g.setColor(pane.getBorderColor() == null ? shadow : pane.getBorderColor());
    if (tabPlacement != TOP || selectedIndex < 0
            || (selRect.y + selRect.height + 1 < y)
            || (selRect.x < x || selRect.x > x + w)) {
      g.drawLine(x, y, x + w - 2, y);
    } else {
      // Break line to show visual connection to selected tab
      g.drawLine(x, y, selRect.x - 1, y);
      if (selRect.x + selRect.width < x + w - 2) {
        g.drawLine(selRect.x + selRect.width, y,
                x + w - 2, y);
      } else {
        g.drawLine(x + w - 2, y, x + w - 2, y);
      }
    }
  }

  /**
   *
   * @param g
   * @param tabPlacement
   * @param selectedIndex
   * @param x
   * @param y
   * @param w
   * @param h
   */
  @Override
  protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
          int selectedIndex, int x, int y, int w, int h) {
    JocTabbedPane pane = (JocTabbedPane) tabPane;
    Rectangle selRect = selectedIndex < 0 ? null
            : getTabBounds(selectedIndex, calcRect);
    g.setColor(pane.getBorderColor() == null ? shadow : pane.getBorderColor());
    if (tabPlacement != LEFT || selectedIndex < 0
            || (selRect.x + selRect.width + 1 < x)
            || (selRect.y < y || selRect.y > y + h)) {
      g.drawLine(x, y, x, y + h - 2);
    } else {
      // Break line to show visual connection to selected tab
      g.drawLine(x, y, x, selRect.y - 1);
      if (selRect.y + selRect.height < y + h - 2) {
        g.drawLine(x, selRect.y + selRect.height,
                x, y + h - 2);
      }
    }
  }

  /**
   *
   * @param g
   * @param tabPlacement
   * @param selectedIndex
   * @param x
   * @param y
   * @param w
   * @param h
   */
  @Override
  protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
          int selectedIndex, int x, int y, int w, int h) {
    JocTabbedPane pane = (JocTabbedPane) tabPane;
    Rectangle selRect = selectedIndex < 0 ? null
            : getTabBounds(selectedIndex, calcRect);
    g.setColor(pane.getBorderColor() == null ? shadow : pane.getBorderColor());
    if (tabPlacement != BOTTOM || selectedIndex < 0
            || (selRect.y - 1 > h)
            || (selRect.x < x || selRect.x > x + w)) {
      g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
    } else {
      g.drawLine(x, y + h - 1, selRect.x - 1, y + h - 1);
      if (selRect.x + selRect.width < x + w - 2) {
        g.drawLine(selRect.x + selRect.width, y + h - 1, x + w - 1, y + h - 1);
      }
    }
  }

  /**
   *
   * @param g
   * @param tabPlacement
   * @param selectedIndex
   * @param x
   * @param y
   * @param w
   * @param h
   */
  @Override
  protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
          int selectedIndex, int x, int y, int w, int h) {
    JocTabbedPane pane = (JocTabbedPane) tabPane;
    Rectangle selRect = selectedIndex < 0 ? null
            : getTabBounds(selectedIndex, calcRect);
    g.setColor(pane.getBorderColor() == null ? shadow : pane.getBorderColor());
    if (tabPlacement != RIGHT || selectedIndex < 0
            || (selRect.x - 1 > w)
            || (selRect.y < y || selRect.y > y + h)) {
      g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
    } else {
      g.drawLine(x + w - 1, y, x + w - 1, selRect.y - 1);
      if (selRect.y + selRect.height < y + h - 2) {
        g.drawLine(x + w - 1, selRect.y + selRect.height,
                x + w - 1, y + h - 2);
      }
    }
  }

  /**
   * Repaints the specified tab.
   */
  protected void repaintTab(int index) {
    if (!isRunsDirty && index >= 0 && index < tabPane.getTabCount()) {
      tabPane.repaint(getTabBounds(tabPane, index));
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="select focus tab">
  /**
   *
   * @param current
   */
  @Override
  protected void selectNextTabInRun(int current) {
    int tabCount = tabPane.getTabCount();
    int tabIndex = getNextTabIndexInRun(tabCount, current);

    while (tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
      tabIndex = getNextTabIndexInRun(tabCount, tabIndex);
    }
    navigateTo(tabIndex);
  }

  /**
   *
   * @param current
   */
  @Override
  protected void selectPreviousTabInRun(int current) {
    int tabCount = tabPane.getTabCount();
    int tabIndex = getPreviousTabIndexInRun(tabCount, current);

    while (tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
      tabIndex = getPreviousTabIndexInRun(tabCount, tabIndex);
    }
    navigateTo(tabIndex);
  }

  /**
   *
   * @param current
   */
  @Override
  protected void selectNextTab(int current) {
    int tabIndex = getNextTabIndex(current);

    while (tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
      tabIndex = getNextTabIndex(tabIndex);
    }
    navigateTo(tabIndex);
  }

  /**
   *
   * @param current
   */
  @Override
  protected void selectPreviousTab(int current) {
    int tabIndex = getPreviousTabIndex(current);

    while (tabIndex != current && !tabPane.isEnabledAt(tabIndex)) {
      tabIndex = getPreviousTabIndex(tabIndex);
    }
    navigateTo(tabIndex);
  }

  /**
   *
   * @param tabPlacement
   * @param tabIndex
   * @param offset
   */
  @Override
  protected void selectAdjacentRunTab(int tabPlacement,
          int tabIndex, int offset) {
    if (runCount < 2) {
      return;
    }
    int newIndex;
    Rectangle r = rects[tabIndex];
    switch (tabPlacement) {
      case LEFT:
      case RIGHT:
        newIndex = tabForCoordinate(tabPane, r.x + r.width / 2 + offset,
                r.y + r.height / 2);
        break;
      case BOTTOM:
      case TOP:
      default:
        newIndex = tabForCoordinate(tabPane, r.x + r.width / 2,
                r.y + r.height / 2 + offset);
    }
    if (newIndex != -1) {
      while (!tabPane.isEnabledAt(newIndex) && newIndex != tabIndex) {
        newIndex = getNextTabIndex(newIndex);
      }
      navigateTo(newIndex);
    }
  }

  /**
   *
   * @param index
   */
  protected void navigateTo(int index) {
    Object selFocus = UIManager.get("TabbedPane.selectionFollowsFocus");
    if (!(selFocus instanceof Boolean) || ((Boolean) selFocus)) {
      tabPane.setSelectedIndex(index);
    } else {
      // Just move focus (not selection)
      setFocusIndex(index, true);
    }
  }

  /**
   *
   * @param index
   * @param repaint
   */
  protected void setFocusIndex(int index, boolean repaint) {
    if (repaint && !isRunsDirty) {
      repaintTab(focusIndex);
      focusIndex = index;
      repaintTab(focusIndex);
    } else {
      focusIndex = index;
    }
  }

  /**
   * Makes sure the focusIndex is valid.
   */
  protected void validateFocusIndex() {
    if (focusIndex >= tabPane.getTabCount()) {
      setFocusIndex(tabPane.getSelectedIndex(), false);
    }
  }

  /**
   *
   * @return
   */
  @Override
  protected int getFocusIndex() {
    return focusIndex;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="update tab mnemonics">
  private void updateMnemonics() {
    resetMnemonics();
    for (int counter = tabPane.getTabCount() - 1; counter >= 0;
            counter--) {
      int mnemonic = tabPane.getMnemonicAt(counter);

      if (mnemonic > 0) {
        addMnemonic(counter, mnemonic);
      }
    }
  }

  /**
   * Resets the mnemonics bindings to an empty state.
   */
  private void resetMnemonics() {
    if (mnemonicToIndexMap != null) {
      mnemonicToIndexMap.clear();
      mnemonicInputMap.clear();
    }
  }

  /**
   * Adds the specified mnemonic at the specified index.
   */
  private void addMnemonic(int index, int mnemonic) {
    if (mnemonicToIndexMap == null) {
      initMnemonics();
    }
    mnemonicInputMap.put(KeyStroke.getKeyStroke(mnemonic, Event.ALT_MASK),
            "setSelectedIndex");
    mnemonicToIndexMap.put(new Integer(mnemonic), new Integer(index));
  }

  /**
   * Installs the state needed for mnemonics.
   */
  private void initMnemonics() {
    mnemonicToIndexMap = new HashMap<Integer, Integer>();
    mnemonicInputMap = new ComponentInputMapUIResource(tabPane);
    mnemonicInputMap.setParent(SwingUtilities.getUIInputMap(tabPane,
            JComponent.WHEN_IN_FOCUSED_WINDOW));
    SwingUtilities.replaceUIInputMap(tabPane,
            JComponent.WHEN_IN_FOCUSED_WINDOW,
            mnemonicInputMap);
  }
  //</editor-fold>

  /**
   *
   * @return
   */
  protected int getRolloverTabClose() {
    return rolloverTabClose;
  }

  /**
   *
   * @param rolloverTabClose
   */
  protected void setRolloverTabClose(int rolloverTabClose) {
    int old = this.rolloverTabClose;
    if (old != rolloverTabClose) {
      this.rolloverTabClose = rolloverTabClose;
      repaintTab(old);
      repaintTab(rolloverTabClose);
    }
  }

  /**
   *
   * @return
   */
  protected int getPressedTabClose() {
    return pressedTabClose;
  }

  /**
   *
   * @param pressedTabClose
   */
  protected void setPressedTabClose(int pressedTabClose) {
    int old = this.pressedTabClose;
    this.pressedTabClose = pressedTabClose;
    repaintTab(old);
    if (old != pressedTabClose) {
      repaintTab(pressedTabClose);
    }
  }

  protected void closeTabAt(int tabIndex) {
    ((JocTabbedPane) tabPane).fireTabClosing(tabIndex);
  }

  /**
   *
   * @param tabIndex
   * @return
   */
  @Override
  protected View getTextViewForTab(int tabIndex) {
    if (htmlViews != null) {
      return htmlViews.get(tabIndex);
    }
    return null;
  }

  private ArrayList<View> createHTMLArrayList() {
    ArrayList<View> tmpViews = new ArrayList<View>();
    int count = tabPane.getTabCount();
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        String title = tabPane.getTitleAt(i);
        if (BasicHTML.isHTMLString(title)) {
          tmpViews.add(BasicHTML.createHTMLView(tabPane, title));
        } else {
          tmpViews.add(null);
        }
      }
    }
    return tmpViews;
  }

  private static class Actions extends AbstractAction {

    final static String SET_SELECTED = "setSelectedIndex";
    final static String SCROLL_FORWARD = "scrollTabsForwardAction";
    final static String SCROLL_BACKWARD = "scrollTabsBackwardAction";
    final static String LIST_TAB = "listTabAction";
    final static String CLOSE_TAB = "closeTabAction";

    Actions(String key) {
      super(key);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      String key = getValue(Action.NAME).toString();
      JTabbedPane pane = (JTabbedPane) e.getSource();
      TabbedPaneUI tpui = pane.getUI();
      if (!(tpui instanceof BasicOrchidTabbedPaneUI)) {
        return;
      }
      BasicOrchidTabbedPaneUI ui = (BasicOrchidTabbedPaneUI) tpui;
      if (key.equals(SET_SELECTED)) {
        String command = e.getActionCommand();

        if (command != null && command.length() > 0) {
          int mnemonic = (int) e.getActionCommand().charAt(0);
          if (mnemonic >= 'a' && mnemonic <= 'z') {
            mnemonic -= ('a' - 'A');
          }
          Integer index = (Integer) ui.mnemonicToIndexMap.
                  get(new Integer(mnemonic));
          if (index != null && pane.isEnabledAt(index.intValue())) {
            pane.setSelectedIndex(index.intValue());
          }
        }
      } else if (key.equals(SCROLL_FORWARD)) {
        if (ui.scrollableTabLayoutEnabled()) {
          ui.tabScroller.scrollForward(pane.getTabPlacement());
        }
      } else if (key.equals(SCROLL_BACKWARD)) {
        if (ui.scrollableTabLayoutEnabled()) {
          ui.tabScroller.scrollBackward(pane.getTabPlacement());
        }
      } else if (key.equals(LIST_TAB)) {
        ui.tabScroller.showTabListMenu();
      } else if (key.equals(CLOSE_TAB)) {
        ui.tabScroller.closeSelectedTab();
      }
    }
  }

  protected class TabbedPaneWrapLayout extends TabbedPaneLayout {

    @Override
    public void layoutContainer(Container parent) {
      setRolloverTab(-1);

      int tabPlacement = tabPane.getTabPlacement();
      Insets insets = tabPane.getInsets();
      int selectedIndex = tabPane.getSelectedIndex();
      Component visibleComponent = getVisibleComponent();

      calculateLayoutInfo();

      Component selectedComponent = null;
      if (selectedIndex < 0) {
        if (visibleComponent != null) {
          // The last tab was removed, so remove the component
          setVisibleComponent(null);
        }
      } else {
        selectedComponent = tabPane.getComponentAt(selectedIndex);
      }
      int cx, cy, cw, ch;
      int totalTabWidth = 0;
      int totalTabHeight = 0;
      Insets contentInsets = getContentBorderInsets(tabPlacement);

      boolean shouldChangeFocus = false;

      if (selectedComponent != null) {
        if (selectedComponent != visibleComponent
                && OrchidUtils.isFocusAncestor(visibleComponent)) {
          shouldChangeFocus = true;
        }
        setVisibleComponent(selectedComponent);
      }

      Rectangle bounds = tabPane.getBounds();
      int numChildren = tabPane.getComponentCount();

      if (numChildren > 0) {
        switch (tabPlacement) {
          case LEFT:
            totalTabWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
            cx = insets.left + totalTabWidth + contentInsets.left;
            cy = insets.top + contentInsets.top;
            break;
          case RIGHT:
            totalTabWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
            cx = insets.left + contentInsets.left;
            cy = insets.top + contentInsets.top;
            break;
          case BOTTOM:
            totalTabHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
            cx = insets.left + contentInsets.left;
            cy = insets.top + contentInsets.top;
            break;
          case TOP:
          default:
            totalTabHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
            cx = insets.left + contentInsets.left;
            cy = insets.top + totalTabHeight + contentInsets.top;
        }

        cw = bounds.width - totalTabWidth
                - insets.left - insets.right
                - contentInsets.left - contentInsets.right;
        ch = bounds.height - totalTabHeight
                - insets.top - insets.bottom
                - contentInsets.top - contentInsets.bottom;

        for (int i = 0; i < numChildren; i++) {
          Component child = tabPane.getComponent(i);
          if (child == tabContainer) {

            int tabContainerWidth = totalTabWidth == 0 ? bounds.width
                    : totalTabWidth + insets.left + insets.right
                    + contentInsets.left + contentInsets.right;
            int tabContainerHeight = totalTabHeight == 0 ? bounds.height
                    : totalTabHeight + insets.top + insets.bottom
                    + contentInsets.top + contentInsets.bottom;

            int tabContainerX = 0;
            int tabContainerY = 0;
            if (tabPlacement == BOTTOM) {
              tabContainerY = bounds.height - tabContainerHeight;
            } else if (tabPlacement == RIGHT) {
              tabContainerX = bounds.width - tabContainerWidth;
            }
            child.setBounds(tabContainerX, tabContainerY,
                    tabContainerWidth, tabContainerHeight);
          } else {
            child.setBounds(cx, cy, cw, ch);
          }
        }
      }
      layoutTabComponents();
      if (shouldChangeFocus) {
        tabPane.requestFocus();
      }
    }

    private void layoutTabComponents() {
      if (tabContainer == null) {
        return;
      }
      Rectangle rect = new Rectangle();
      Point delta = new Point(-tabContainer.getX(),
              -tabContainer.getY());
      if (scrollableTabLayoutEnabled()) {
        translatePointToTabPanel(0, 0, delta);
      }
      for (int i = 0; i < tabPane.getTabCount(); i++) {
        Component c = tabPane.getTabComponentAt(i);
        if (c == null) {
          continue;
        }
        getTabBounds(i, rect);
        Dimension preferredSize = c.getPreferredSize();
        Insets insets = getTabInsets(tabPane.getTabPlacement(), i);
        int outerX = rect.x + insets.left + delta.x;
        int outerY = rect.y + insets.top + delta.y;
        int outerWidth = rect.width - insets.left - insets.right;
        int outerHeight = rect.height - insets.top - insets.bottom;
        //centralize component
        int x = outerX + (outerWidth - preferredSize.width) / 2;
        int y = outerY + (outerHeight - preferredSize.height) / 2;
        int tabPlacement = tabPane.getTabPlacement();
        boolean isSeleceted = i == tabPane.getSelectedIndex();
        c.setBounds(x + getTabLabelShiftX(tabPlacement, i, isSeleceted),
                y + getTabLabelShiftY(tabPlacement, i, isSeleceted),
                preferredSize.width, preferredSize.height);
      }
    }

    @Override
    public void calculateLayoutInfo() {
      super.calculateLayoutInfo();
      isRunsDirty = false;
    }

    @Override
    protected void padSelectedTab(int tabPlacement, int selectedIndex) {
      // do not pad selected tab.
    }
  }

  protected class TabbedPaneScrollLayout extends TabbedPaneWrapLayout {

    @Override
    protected int preferredTabAreaHeight(int tabPlacement, int width) {
      return calculateMaxTabHeight(tabPlacement);
    }

    @Override
    protected int preferredTabAreaWidth(int tabPlacement, int height) {
      return calculateMaxTabWidth(tabPlacement);
    }

    @Override
    public void layoutContainer(Container parent) {
      setRolloverTab(-1);

      int tabPlacement = tabPane.getTabPlacement();
      int tabCount = tabPane.getTabCount();
      Insets insets = tabPane.getInsets();
      int selectedIndex = tabPane.getSelectedIndex();
      Component visibleComponent = getVisibleComponent();

      calculateLayoutInfo();

      Component selectedComponent = null;
      if (selectedIndex < 0) {
        if (visibleComponent != null) {
          // The last tab was removed, so remove the component
          setVisibleComponent(null);
        }
      } else {
        selectedComponent = tabPane.getComponentAt(selectedIndex);
      }

      if (tabPane.getTabCount() == 0) {
        tabScroller.tabButtonBar.setVisible(false);
        return;
      }
      tabScroller.updateButtonBarState();

      boolean shouldChangeFocus = false;

      if (selectedComponent != null) {
        if (selectedComponent != visibleComponent
                && OrchidUtils.isFocusAncestor(visibleComponent)) {
          shouldChangeFocus = true;
        }
        setVisibleComponent(selectedComponent);
      }
      int tx, ty, tw, th; // tab area bounds
      int cx, cy, cw, ch; // content area bounds
      Insets contentInsets = getContentBorderInsets(tabPlacement);
      Rectangle bounds = tabPane.getBounds();
      int numChildren = tabPane.getComponentCount();
      Dimension nbSize = tabScroller.tabButtonBar.getPreferredSize();
      Dimension sbSize = tabScroller.scrollForwardButton.getPreferredSize();

      if (numChildren > 0) {
        switch (tabPlacement) {
          case LEFT:
            // calculate tab area bounds
            if (tabScroller.scrollForwardButton.isVisible()) {
              nbSize.height -= sbSize.height * 2;
            }
            tw = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
            th = bounds.height - insets.top - insets.bottom - nbSize.height;
            tx = insets.left;
            ty = insets.top;

            // calculate content area bounds
            cx = tx + tw + contentInsets.left;
            cy = ty + contentInsets.top;
            cw = bounds.width - insets.left - insets.right - tw
                    - contentInsets.left - contentInsets.right;
            ch = bounds.height - insets.top - insets.bottom
                    - contentInsets.top - contentInsets.bottom;
            break;
          case RIGHT:
            // calculate tab area bounds
            if (tabScroller.scrollForwardButton.isVisible()) {
              nbSize.height -= sbSize.height * 2;
            }
            tw = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
            th = bounds.height - insets.top - insets.bottom - nbSize.height;
            tx = bounds.width - insets.right - tw;
            ty = insets.top;

            // calculate content area bounds
            cx = insets.left + contentInsets.left;
            cy = insets.top + contentInsets.top;
            cw = bounds.width - insets.left - insets.right - tw
                    - contentInsets.left - contentInsets.right;
            ch = bounds.height - insets.top - insets.bottom
                    - contentInsets.top - contentInsets.bottom;
            break;
          case BOTTOM:
            // calculate tab area bounds
            if (tabScroller.scrollForwardButton.isVisible()) {
              nbSize.width -= sbSize.width * 2;
            }
            tw = bounds.width - insets.left - insets.right - nbSize.width;
            th = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
            tx = insets.left;
            ty = bounds.height - insets.bottom - th;

            // calculate content area bounds
            cx = insets.left + contentInsets.left;
            cy = insets.top + contentInsets.top;
            cw = bounds.width - insets.left - insets.right
                    - contentInsets.left - contentInsets.right;
            ch = bounds.height - insets.top - insets.bottom - th
                    - contentInsets.top - contentInsets.bottom;
            break;
          case TOP:
          default:
            // calculate tab area bounds
            if (tabScroller.scrollForwardButton.isVisible()) {
              nbSize.width -= sbSize.width * 2;
            }
            tw = bounds.width - insets.left - insets.right - nbSize.width;
            th = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
            tx = insets.left;
            ty = insets.top;

            // calculate content area bounds
            cx = tx + contentInsets.left;
            cy = ty + th + contentInsets.top;
            cw = bounds.width - insets.left - insets.right
                    - contentInsets.left - contentInsets.right;
            ch = bounds.height - insets.top - insets.bottom - th
                    - contentInsets.top - contentInsets.bottom;
        }

        for (int i = 0; i < numChildren; i++) {
          Component child = tabPane.getComponent(i);

          if (child == tabScroller.tabViewport) {
            JViewport viewport = (JViewport) child;
            Rectangle viewRect = viewport.getViewRect();
            int vw = tw;
            int vh = th;
            switch (tabPlacement) {
              case LEFT:
              case RIGHT:
                int totalTabHeight = rects[tabCount - 1].y + rects[tabCount - 1].height;
                if (totalTabHeight > th) {
                  // Allow space for scrollbuttons
                  vh = (th > sbSize.height * 2) ? th - sbSize.height * 2 : 0;
                  if (totalTabHeight - viewRect.y <= vh) {
                    // Scrolled to the end, so ensure the viewport size is
                    // such that the scroll offset aligns with a tab
                    vh = totalTabHeight - viewRect.y;
                  }
                }
                break;
              case BOTTOM:
              case TOP:
              default:
                int totalTabWidth = rects[tabCount - 1].x + rects[tabCount - 1].width;
                if (totalTabWidth > tw) {
                  // Need to allow space for scrollbuttons
                  vw = (tw > sbSize.width * 2) ? tw - sbSize.width * 2 : 0;
                  if (totalTabWidth - viewRect.x <= vw) {
                    // Scrolled to the end, so ensure the viewport size is
                    // such that the scroll offset aligns with a tab
                    vw = totalTabWidth - viewRect.x;
                  }
                }
            }
            child.setBounds(tx, ty, vw, vh);
          } else if (child == tabScroller.tabButtonBar) {
            Component buttonBar = child;
            Dimension barSize = buttonBar.getPreferredSize();
            int bx, by;
            int bw = barSize.width;
            int bh = barSize.height;
            boolean visible;
            switch (tabPlacement) {
              case LEFT:
              case RIGHT:
                int totalTabHeight = rects[tabCount - 1].y + rects[tabCount - 1].height;
                bx = (tabPlacement == LEFT ? tx + tw - barSize.width - 2 : tx + 2);
                visible = totalTabHeight > th;
                if (visible) {
                  by = bounds.height - insets.bottom - barSize.height;
                } else {
                  by = bounds.height - insets.bottom - nbSize.height;
                }
                break;

              case BOTTOM:
              case TOP:
              default:
                int totalTabWidth = rects[tabCount - 1].x + rects[tabCount - 1].width;
                visible = totalTabWidth > tw;
                if (visible) {
                  bx = bounds.width - insets.left - barSize.width;
                } else {
                  bx = bounds.width - insets.left - nbSize.width;
                }
                by = (tabPlacement == TOP ? ty + th - barSize.height - 2 : ty + 2);
            }
            tabScroller.scrollForwardButton.setVisible(visible);
            tabScroller.scrollBackwardButton.setVisible(visible);
            child.setVisible(true);
            child.setBounds(bx, by, bw, bh);
          } else {
            // All content children...
            child.setBounds(cx, cy, cw, ch);
          }
        }
        super.layoutTabComponents();
        if (shouldChangeFocus) {
          tabPane.requestFocus();
        }
      }
    }

    @Override
    protected void calculateTabRects(int tabPlacement, int tabCount) {
      FontMetrics metrics = getFontMetrics();
      Dimension size = tabPane.getSize();
      Insets insets = tabPane.getInsets();
      Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
      int fontHeight = metrics.getHeight();
      int selectedIndex = tabPane.getSelectedIndex();
      int i;
      boolean verticalTabRuns = (tabPlacement == LEFT || tabPlacement == RIGHT);
      boolean leftToRight = tabPane.getComponentOrientation().isLeftToRight();
      int x = tabAreaInsets.left;
      int y = tabAreaInsets.top;
      int totalWidth = 0;
      int totalHeight = 0;

      //
      // Calculate bounds within which a tab run must fit
      //
      switch (tabPlacement) {
        case LEFT:
        case RIGHT:
          maxTabWidth = calculateMaxTabWidth(tabPlacement);
          break;
        case BOTTOM:
        case TOP:
        default:
          maxTabHeight = calculateMaxTabHeight(tabPlacement);
      }

      runCount = 0;
      selectedRun = -1;

      if (tabCount == 0) {
        return;
      }

      selectedRun = 0;
      runCount = 1;

      // Run through tabs and lay them out in a single run
      Rectangle rect;
      for (i = 0; i < tabCount; i++) {
        rect = rects[i];

        if (!verticalTabRuns) {
          // Tabs on TOP or BOTTOM....
          if (i > 0) {
            rect.x = rects[i - 1].x + rects[i - 1].width;
          } else {
            tabRuns[0] = 0;
            maxTabWidth = 0;
            totalHeight += maxTabHeight;
            rect.x = x;
          }
          rect.width = calculateTabWidth(tabPlacement, i, metrics);
          totalWidth = rect.x + rect.width;
          maxTabWidth = Math.max(maxTabWidth, rect.width);

          rect.y = y;
          rect.height = maxTabHeight/* - 2*/;

        } else {
          // Tabs on LEFT or RIGHT...
          if (i > 0) {
            rect.y = rects[i - 1].y + rects[i - 1].height;
          } else {
            tabRuns[0] = 0;
            maxTabHeight = 0;
            totalWidth = maxTabWidth;
            rect.y = y;
          }
          rect.height = calculateTabHeight(tabPlacement, i, fontHeight);
          totalHeight = rect.y + rect.height;
          maxTabHeight = Math.max(maxTabHeight, rect.height);

          rect.x = x;
          rect.width = maxTabWidth/* - 2*/;

        }
      }

      if (UIManager.getBoolean("TabbedPane.tabsOverlapBorder")) {
        // Pad the selected tab so that it appears raised in front
        padSelectedTab(tabPlacement, selectedIndex);
      }

      // if right to left and tab placement on the top or
      // the bottom, flip x positions and adjust by widths
      if (!leftToRight && !verticalTabRuns) {
        int rightMargin = size.width
                - (insets.right + tabAreaInsets.right);
        for (i = 0; i < tabCount; i++) {
          rects[i].x = rightMargin - rects[i].x - rects[i].width;
        }
      }
      tabScroller.tabPanel.setPreferredSize(
              new Dimension(totalWidth, totalHeight));
      tabScroller.tabViewport.invalidate();
    }
  }

  protected class ScrollableTabSupport implements ActionListener,
          ChangeListener, PopupMenuListener, ListSelectionListener {

    public JViewport tabViewport;
    public JPanel tabPanel;
    public JToolBar tabButtonBar;
    public JButton scrollForwardButton;
    public JButton scrollBackwardButton;
    public JButton listTabsButton;
    public JButton closeTabButton;
    public int leadingTabIndex;
    private JPopupMenu tabListPopup;
    private Point tabViewPosition = new Point(0, 0);

    @SuppressWarnings("LeakingThisInConstructor")
    public ScrollableTabSupport() {
      tabViewport = createTabViewport();
      tabPanel = createTabPanel();
      tabViewport.setView(tabPanel);
      tabButtonBar = createTabButtonBar();
      tabViewport.addChangeListener(this);
      updateButtonBarState();
    }

    protected JViewport createTabViewport() {
      return new ScrollableTabViewport();
    }

    protected JPanel createTabPanel() {
      return new ScrollableTabPanel();
    }

    protected JToolBar createTabButtonBar() {
      JToolBar toolBar = new TabButtonBar();
      scrollForwardButton = new JButton();
      scrollBackwardButton = new JButton();
      listTabsButton = new JButton("Test");
      closeTabButton = new JButton();
      listTabsButton.setUI(new TabButtonUI(TabButtonUI.LIST));
      closeTabButton.setUI(new TabButtonUI(TabButtonUI.CLOSE));
      toolBar.add(scrollBackwardButton);
      toolBar.add(scrollForwardButton);
      toolBar.add(listTabsButton);
      toolBar.add(closeTabButton);
      scrollBackwardButton.addActionListener(this);
      scrollForwardButton.addActionListener(this);
      listTabsButton.addActionListener(this);
      closeTabButton.addActionListener(this);
      return toolBar;
    }

    public void updateButtonBarState() {
      JocTabbedPane pane = (JocTabbedPane) tabPane;
      int placement = tabPane.getTabPlacement();
      if (placement == LEFT || placement == RIGHT) {
        tabButtonBar.setOrientation(VERTICAL);
        scrollForwardButton.setUI(new TabButtonUI(SOUTH));
        scrollBackwardButton.setUI(new TabButtonUI(NORTH));
      } else {
        tabButtonBar.setOrientation(HORIZONTAL);
        scrollForwardButton.setUI(new TabButtonUI(EAST));
        scrollBackwardButton.setUI(new TabButtonUI(WEST));
      }
      listTabsButton.setVisible(pane.getShowListButton());
      closeTabButton.setVisible(pane.getShowCloseButton()
              && !pane.getShowCloseButtonOnTab());
      closeTabButton.setEnabled(pane.isTabClosable(pane.getSelectedIndex()));
    }

    public void scrollForward(int tabPlacement) {
      Dimension viewSize = tabViewport.getViewSize();
      Rectangle viewRect = tabViewport.getViewRect();

      if (tabPlacement == TOP || tabPlacement == BOTTOM) {
        if (viewRect.width >= viewSize.width - viewRect.x) {
          return; // no room left to scroll
        }
      } else { // tabPlacement == LEFT || tabPlacement == RIGHT
        if (viewRect.height >= viewSize.height - viewRect.y) {
          return;
        }
      }
      setLeadingTabIndex(tabPlacement, leadingTabIndex + 1);
    }

    public void scrollBackward(int tabPlacement) {
      if (leadingTabIndex == 0) {
        return; // no room left to scroll
      }
      setLeadingTabIndex(tabPlacement, leadingTabIndex - 1);
    }

    public void setLeadingTabIndex(int tabPlacement, int index) {
      leadingTabIndex = index;
      Dimension viewSize = tabViewport.getViewSize();
      Rectangle viewRect = tabViewport.getViewRect();

      switch (tabPlacement) {
        case TOP:
        case BOTTOM:
          tabViewPosition.x = leadingTabIndex == 0 ? 0 : rects[leadingTabIndex].x;

          if ((viewSize.width - tabViewPosition.x) < viewRect.width) {
            // We've scrolled to the end, so adjust the viewport size
            // to ensure the view position remains aligned on a tab boundary
            Dimension extentSize = new Dimension(viewSize.width - tabViewPosition.x,
                    viewRect.height);
            tabViewport.setExtentSize(extentSize);
          }
          break;
        case LEFT:
        case RIGHT:
          tabViewPosition.y = leadingTabIndex == 0 ? 0 : rects[leadingTabIndex].y;

          if ((viewSize.height - tabViewPosition.y) < viewRect.height) {
            // We've scrolled to the end, so adjust the viewport size
            // to ensure the view position remains aligned on a tab boundary
            Dimension extentSize = new Dimension(viewRect.width,
                    viewSize.height - tabViewPosition.y);
            tabViewport.setExtentSize(extentSize);
          }
      }
      tabViewport.setViewPosition(tabViewPosition);
    }

    /**
     * ActionListener for the scroll buttons.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      ActionMap map = tabPane.getActionMap();

      if (map != null) {
        String actionKey;

        if (e.getSource() == scrollForwardButton) {
          actionKey = "scrollTabsForwardAction";
        } else if (e.getSource() == scrollBackwardButton) {
          actionKey = "scrollTabsBackwardAction";
        } else if (e.getSource() == listTabsButton) {
          actionKey = "listTabAction";
        } else {
          actionKey = "closeTabAction";
        }
        Action action = map.get(actionKey);
        if (action != null && action.isEnabled()) {
          action.actionPerformed(new ActionEvent(tabPane,
                  ActionEvent.ACTION_PERFORMED, null, e.getWhen(),
                  e.getModifiers()));
        }
      }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      updateView();
    }

    private void updateView() {
      int tabPlacement = tabPane.getTabPlacement();
      int tabCount = tabPane.getTabCount();
      Rectangle vpRect = tabViewport.getBounds();
      Dimension viewSize = tabViewport.getViewSize();
      Rectangle viewRect = tabViewport.getViewRect();

      leadingTabIndex = getClosestTab(viewRect.x, viewRect.y);

      // If the tab isn't right aligned, adjust it.
      if (leadingTabIndex + 1 < tabCount) {
        switch (tabPlacement) {
          case TOP:
          case BOTTOM:
            if (rects[leadingTabIndex].x < viewRect.x) {
              leadingTabIndex++;
            }
            break;
          case LEFT:
          case RIGHT:
            if (rects[leadingTabIndex].y < viewRect.y) {
              leadingTabIndex++;
            }
            break;
        }
      }
      Insets contentInsets = getContentBorderInsets(tabPlacement);
      switch (tabPlacement) {
        case LEFT:
          tabPane.repaint(vpRect.x + vpRect.width, vpRect.y,
                  contentInsets.left, vpRect.height);
          scrollBackwardButton.setEnabled(
                  viewRect.y > 0 && leadingTabIndex > 0);
          scrollForwardButton.setEnabled(
                  leadingTabIndex < tabCount - 1
                  && viewSize.height - viewRect.y > viewRect.height);
          break;
        case RIGHT:
          tabPane.repaint(vpRect.x - contentInsets.right, vpRect.y,
                  contentInsets.right, vpRect.height);
          scrollBackwardButton.setEnabled(
                  viewRect.y > 0 && leadingTabIndex > 0);
          scrollForwardButton.setEnabled(
                  leadingTabIndex < tabCount - 1
                  && viewSize.height - viewRect.y > viewRect.height);
          break;
        case BOTTOM:
          tabPane.repaint(vpRect.x, vpRect.y - contentInsets.bottom,
                  vpRect.width, contentInsets.bottom);
          scrollBackwardButton.setEnabled(
                  viewRect.x > 0 && leadingTabIndex > 0);
          scrollForwardButton.setEnabled(
                  leadingTabIndex < tabCount - 1
                  && viewSize.width - viewRect.x > viewRect.width);
          break;
        case TOP:
        default:
          tabPane.repaint(vpRect.x, vpRect.y + vpRect.height,
                  vpRect.width, contentInsets.top);
          scrollBackwardButton.setEnabled(
                  viewRect.x > 0 && leadingTabIndex > 0);
          scrollForwardButton.setEnabled(
                  leadingTabIndex < tabCount - 1
                  && viewSize.width - viewRect.x > viewRect.width);
      }
    }

    protected void showTabListMenu() {
      updateTabListMenu();
      OrchidUtils.showPopupMenu(tabListPopup, listTabsButton);
    }

    protected void closeSelectedTab() {
      closeTabAt(tabPane.getSelectedIndex());
    }

    private void updateTabListMenu() {
      if (tabListPopup == null) {
        tabListPopup = new JPopupMenu();
        tabListPopup.addPopupMenuListener(this);
      }
      tabListPopup.add(createTabsList());
    }

    private JList createTabsList() {
      String[] tabs = new String[tabPane.getTabCount()];
      for (int i = 0; i < tabPane.getTabCount(); i++) {
        tabs[i] = tabPane.getTitleAt(i);
      }
      JList tabsList = new JList(tabs);
      tabsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      tabsList.setSelectedIndex(tabPane.getSelectedIndex());
      tabsList.addListSelectionListener(this);
      return tabsList;
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      clearTabListPopup();
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }

    private void clearTabListPopup() {
      JList tabsList = (JList) tabListPopup.getComponent(0);
      tabsList.removeListSelectionListener(this);
      tabListPopup.remove(0);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
        JList tabsList = (JList) e.getSource();
        tabPane.setSelectedIndex(tabsList.getSelectedIndex());
        tabListPopup.setVisible(false);
      }
    }
  }

  private class ScrollableTabViewport extends JViewport implements UIResource {

    public ScrollableTabViewport() {
      super();
      setName("TabbedPane.scrollableViewport");
      setScrollMode(SIMPLE_SCROLL_MODE);
      setOpaque(tabPane.isOpaque());
      Color bgColor = UIManager.getColor("TabbedPane.tabAreaBackground");
      if (bgColor == null) {
        bgColor = tabPane.getBackground();
      }
      setBackground(bgColor);
    }
  }

  private class ScrollableTabPanel extends JPanel implements UIResource {

    public ScrollableTabPanel() {
      super(null);
      setOpaque(tabPane.isOpaque());
      Color bgColor = UIManager.getColor("TabbedPane.tabAreaBackground");
      if (bgColor == null) {
        bgColor = tabPane.getBackground();
      }
      setBackground(bgColor);
    }

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      BasicOrchidTabbedPaneUI.this.paintTabArea(g, tabPane.getTabPlacement(),
              tabPane.getSelectedIndex());
    }

    @Override
    public void doLayout() {
      if (getComponentCount() > 0) {
        getComponent(0).setBounds(0, 0, getWidth(), getHeight());
      }
    }
  }

  protected class TabContainer extends JPanel implements UIResource {

    public TabContainer() {
      super(null);
      setOpaque(false);
    }

    private void removeUnusedTabComponents() {
      for (Component c : getComponents()) {
        if (!(c instanceof UIResource)
                && tabPane.indexOfTabComponent(c) == -1) {
          super.remove(c);
        }
      }
    }

    @Override
    public void doLayout() {
      if (scrollableTabLayoutEnabled()) {
        tabScroller.tabPanel.repaint();
        tabScroller.updateView();
      } else {
        tabPane.repaint(getBounds());
      }
    }
  }

  private class TabButtonBar extends JToolBar implements UIResource {

    public TabButtonBar() {
      super();
      setFloatable(false);
    }
  }

  private class TabButtonUI extends BasicButtonUI {

    public static final int LIST = 101;
    public static final int CLOSE = 102;
    private int direction;

    public TabButtonUI(int direction) {
      this.direction = direction;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
      Graphics2D g2 = (Graphics2D) g;
      AbstractButton b = (AbstractButton) c;
      boolean enabled = b.isEnabled();
      boolean rollover = b.getModel().isRollover();
      boolean pressed = b.getModel().isPressed();
      Rectangle rect = new Rectangle(b.getWidth(), b.getHeight());
      if (direction == LIST) {
        paintListButton(g2, rect, enabled, rollover, pressed);
      } else if (direction == CLOSE) {
        paintCloseButton(g2, rect, enabled, rollover, pressed);
      } else {
        paintArrowButton(g2, direction, rect, enabled, rollover, pressed);
      }
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
      return new Dimension(16, 16);
    }
  }

  private class Handler implements ChangeListener, ContainerListener,
          FocusListener, MouseListener, MouseMotionListener,
          PropertyChangeListener {
    //
    // PropertyChangeListener
    //

    @Override
    public void propertyChange(PropertyChangeEvent e) {
      JTabbedPane pane = (JTabbedPane) e.getSource();
      String name = e.getPropertyName();
      boolean isScrollLayout = scrollableTabLayoutEnabled();
      if ("mnemonicAt".equals(name)) {
        updateMnemonics();
        pane.repaint();
      } else if ("displayedMnemonicIndexAt".equals(name)) {
        pane.repaint();
      } else if ("indexForTitle".equals(name)) {
        calculatedBaseline = false;
        Integer index = (Integer) e.getNewValue();
        // remove the current index
        // to let updateHtmlViews() insert the correct one
        if (htmlViews != null) {
          htmlViews.remove(index.intValue());
        }
        updateHtmlViews(index);
      } else if ("tabLayoutPolicy".equals(name)) {
        BasicOrchidTabbedPaneUI.this.uninstallUI(pane);
        BasicOrchidTabbedPaneUI.this.installUI(pane);
        calculatedBaseline = false;
      } else if ("tabPlacement".equals(name)) {
        calculatedBaseline = false;
      } else if ("opaque".equals(name) && isScrollLayout) {
        boolean newVal = ((Boolean) e.getNewValue()).booleanValue();
        tabScroller.tabPanel.setOpaque(newVal);
        tabScroller.tabViewport.setOpaque(newVal);
      } else if ("background".equals(name) && isScrollLayout) {
        Color newVal = (Color) e.getNewValue();
        tabScroller.tabPanel.setBackground(newVal);
        tabScroller.tabViewport.setBackground(newVal);
        tabScroller.scrollForwardButton.setBackground(newVal);
        tabScroller.scrollBackwardButton.setBackground(newVal);
      } else if ("indexForTabComponent".equals(name)) {
        if (tabContainer != null) {
          tabContainer.removeUnusedTabComponents();
        }
        Component c = tabPane.getTabComponentAt(
                (Integer) e.getNewValue());
        if (c != null) {
          if (tabContainer == null) {
            installTabContainer();
          } else {
            tabContainer.add(c);
          }
        }
        tabPane.revalidate();
        tabPane.repaint();
        calculatedBaseline = false;
      } else if ("indexForNullComponent".equals(name)) {
        isRunsDirty = true;
        updateHtmlViews((Integer) e.getNewValue());
      } else if ("font".equals(name)) {
        calculatedBaseline = false;
      } else if ("borderColor".equals(name)) {
        tabPane.repaint();
      } else if ("selectedColor".equals(name)) {
        repaintTab(tabPane.getSelectedIndex());
      } else if ("showListButton".equals(name)
              || "showCloseButton".equals(name)
              || "showCloseButtonOnTab".equals(name)
              || "tabClosable".equals(name)) {
        tabPane.revalidate();
        tabPane.repaint();
      }
    }

    private void updateHtmlViews(int index) {
      String title = tabPane.getTitleAt(index);
      boolean isHTML = BasicHTML.isHTMLString(title);
      if (isHTML) {
        if (htmlViews == null) {    // Initialize vector
          htmlViews = createHTMLArrayList();
        } else {                  // Vector already exists
          View v = BasicHTML.createHTMLView(tabPane, title);
          htmlViews.add(index, v);
        }
      } else {                             // Not HTML
        if (htmlViews != null) {           // Add placeholder
          htmlViews.add(index, null);
        }                                // else nada!
      }
      updateMnemonics();
    }

    //
    // ChangeListener
    //
    @Override
    public void stateChanged(ChangeEvent e) {
      JTabbedPane tabPane = (JTabbedPane) e.getSource();
      tabPane.revalidate();
      tabPane.repaint();

      setFocusIndex(tabPane.getSelectedIndex(), false);

      if (scrollableTabLayoutEnabled()) {
        ensureCurrentLayout();
        int index = tabPane.getSelectedIndex();
        if (index < rects.length && index != -1) {
          tabScroller.tabPanel.scrollRectToVisible(
                  (Rectangle) rects[index].clone());
        }
      }
    }

    //
    // MouseListener
    //
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      int tabIndex = tabForCoordinate(tabPane, e.getX(), e.getY());
      if (tabIndex != -1 && tabIndex == getPressedTabClose()) {
        closeTabAt(tabIndex);
      }
      updateRolloverTab(e);
      setPressedTabClose(-1);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      updateRolloverTab(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
      setRolloverTab(-1);
      setRolloverTabClose(-1);
    }

    @Override
    public void mousePressed(MouseEvent e) {
      if (!tabPane.isEnabled()) {
        return;
      }
      int tabIndex = tabForCoordinate(tabPane, e.getX(), e.getY());
      if (tabIndex >= 0 && tabPane.isEnabledAt(tabIndex)) {
        Point point = new Point(e.getPoint());
        if (scrollableTabLayoutEnabled()) {
          translatePointToTabPanel(e.getX(), e.getY(), point);
        }
        Rectangle rect = calculateTabCloseRect(tabPane.getTabPlacement(),
                tabIndex, rects[tabIndex]);
        if (isTabCloseButtonVisible(tabIndex) && rect.contains(point)) {
          setPressedTabClose(tabIndex);
        } else if (tabIndex != tabPane.getSelectedIndex()) {
          // Clicking on unselected tab, change selection, do NOT
          // request focus.
          // This will trigger the focusIndex to change by way
          // of stateChanged.
          tabPane.setSelectedIndex(tabIndex);
        } else if (tabPane.isRequestFocusEnabled()) {
          // Clicking on selected tab, try and give the tabbedpane
          // focus.  Repaint will occur in focusGained.
          tabPane.requestFocus();
        }
      }
    }

    //
    // MouseMotionListener
    //
    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      updateRolloverTab(e);
    }

    private void updateRolloverTab(MouseEvent e) {
      int tabIndex = tabForCoordinate(tabPane, e.getX(), e.getY(), false);
      setRolloverTab(tabIndex);
      if (!isTabCloseButtonVisible(tabIndex)) {
        setRolloverTabClose(-1);
      } else {
        Point point = new Point(e.getPoint());
        if (scrollableTabLayoutEnabled()) {
          translatePointToTabPanel(e.getX(), e.getY(), point);
        }
        Rectangle rect = calculateTabCloseRect(
                tabPane.getTabPlacement(), tabIndex, rects[tabIndex]);
        setRolloverTabClose(rect.contains(point) ? tabIndex : -1);
      }
    }

    //
    // FocusListener
    //
    @Override
    public void focusGained(FocusEvent e) {
      setFocusIndex(tabPane.getSelectedIndex(), true);
    }

    @Override
    public void focusLost(FocusEvent e) {
      repaintTab(focusIndex);
    }

    //
    // ContainerListener
    //
    /* GES 2/3/99:
     The container listener code was added to support HTML
     rendering of tab titles.

     Ideally, we would be able to listen for property changes
     when a tab is added or its text modified.  At the moment
     there are no such events because the Beans spec doesn't
     allow 'indexed' property changes (i.e. tab 2's text changed
     from A to B).

     In order to get around this, we listen for tabs to be added
     or removed by listening for the container events.  we then
     queue up a runnable (so the component has a chance to complete
     the add) which checks the tab title of the new component to see
     if it requires HTML rendering.

     The Views (one per tab title requiring HTML rendering) are
     stored in the tmpViews Vector, which is only allocated after
     the first time we run into an HTML tab.  Note that this vector
     is kept in step with the number of pages, and nulls are added
     for those pages whose tab title do not require HTML rendering.

     This makes it easy for the paint and layout code to tell
     whether to invoke the HTML engine without having to check
     the string during time-sensitive operations.

     When we have added a way to listen for tab additions and
     changes to tab text, this code should be removed and
     replaced by something which uses that.  */
    @Override
    public void componentAdded(ContainerEvent e) {
      JTabbedPane tp = (JTabbedPane) e.getContainer();
      Component child = e.getChild();
      if (child instanceof UIResource) {
        return;
      }
      isRunsDirty = true;
      updateHtmlViews(tp.indexOfComponent(child));
    }

    @Override
    public void componentRemoved(ContainerEvent e) {
      JTabbedPane tp = (JTabbedPane) e.getContainer();
      Component child = e.getChild();
      if (child instanceof UIResource) {
        return;
      }

      // NOTE 4/15/2002 (joutwate):
      // This fix is implemented using client properties since there is
      // currently no IndexPropertyChangeEvent.  Once
      // IndexPropertyChangeEvents have been added this code should be
      // modified to use it.
      Integer indexObj =
              (Integer) tp.getClientProperty("__index_to_remove__");
      if (indexObj != null) {
        int index = indexObj.intValue();
        if (htmlViews != null && htmlViews.size() > index) {
          htmlViews.remove(index);
        }
        tp.putClientProperty("__index_to_remove__", null);
      }
      isRunsDirty = true;
      updateMnemonics();
      if (getFocusIndex() >= tabPane.getTabCount()) {
        setFocusIndex(tabPane.getSelectedIndex(), false);
      }
    }
  }
}