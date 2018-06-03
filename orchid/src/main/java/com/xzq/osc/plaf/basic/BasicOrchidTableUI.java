/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.plaf.basic;

import com.xzq.osc.JocTable;
import com.xzq.osc.plaf.OrchidDefaults;
import com.xzq.osc.table.TableColumnsFactory;
import com.xzq.osc.table.TableSidePane;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.plaf.TableUI;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author zqxu
 */
public class BasicOrchidTableUI extends BasicTableUI {

  private static final LineBorder defaultBorder = new LineBorder(
          new Color(100, 100, 255));
  private JocTable ocTable;
  private TableLayout layout;
  private JComponent rbCorner;
  private MouseWheelHandler mouseWheelHandler;
  private MouseMotionHandler mouseMotionHandler;

  /**
   * create UI.
   *
   * @param c table component.
   * @return new UI.
   */
  public static TableUI createUI(JComponent c) {
    return new BasicOrchidTableUI();
  }

  /**
   * install ui to table.
   *
   * @param c table component.
   */
  @Override
  public void installUI(JComponent c) {
    super.installUI(c);
    ocTable = (JocTable) c;
    ocTable.setColumnsFactory((TableColumnsFactory) UIManager
            .get(OrchidDefaults.TABLE_COLUMNS_FACTORY));
    ocTable.setOptionPopupMenu((JPopupMenu) UIManager
            .get(OrchidDefaults.TABLE_OPTION_POPUP));
    ocTable.setAutoCreateRowSorter(UIManager
            .getBoolean(OrchidDefaults.TABLE_AUTO_ROWSORTER));
    ocTable.add(getRightBottomCorner());
    ocTable.setBorder(defaultBorder);
    ocTable.setLayout(getLayout());
    ocTable.addMouseWheelListener(getMouseWheelHandler());
    ocTable.addMouseMotionListener(getMouseMotionHandler());
  }

  private TableLayout getLayout() {
    if (layout == null) {
      layout = new TableLayout();
    }
    return layout;
  }

  /**
   * Returns mouse wheel listener.
   *
   * @return mouse wheel listener.
   */
  protected MouseWheelListener getMouseWheelHandler() {
    if (mouseWheelHandler == null) {
      mouseWheelHandler = new MouseWheelHandler();
    }
    return mouseWheelHandler;
  }

  /**
   * Returns mouse motion listener.
   *
   * @return mouse motion listener.
   */
  protected MouseMotionListener getMouseMotionHandler() {
    if (mouseMotionHandler == null) {
      mouseMotionHandler = new MouseMotionHandler();
    }
    return mouseMotionHandler;
  }

  private JComponent getRightBottomCorner() {
    if (rbCorner == null) {
      rbCorner = new JPanel();
    }
    return rbCorner;
  }

  /**
   * uninstall ui from table.
   *
   * @param c table component.
   */
  @Override
  public void uninstallUI(JComponent c) {
    super.uninstallUI(c);
    ocTable.remove(rbCorner);
    rbCorner = null;
    ocTable.removeMouseWheelListener(getMouseWheelHandler());
    ocTable.removeMouseMotionListener(getMouseMotionHandler());
    ocTable.setLayout(null);
    ocTable.setBorder(null);
    ocTable = null;
  }

  /**
   * Override to set minimum size to [0,0].
   *
   * @param c table component.
   * @return dimension with size [0, 0]
   */
  @Override
  public Dimension getMinimumSize(JComponent c) {
    return new Dimension();
  }

  /**
   * Calculate and return preferred size for table.
   *
   * @param c table component.
   * @return preferred size for table
   */
  @Override
  public Dimension getPreferredSize(JComponent c) {
    Insets insets = ocTable.getInsets();
    int width = insets.left + insets.right;
    int height = insets.top + insets.bottom;
    TableColumnModel colModel = ocTable.getColumnModel();
    if (colModel != null) {
      width += colModel.getColumnCount() * colModel.getColumnMargin();
      for (int col = 0; col < colModel.getColumnCount(); col++) {
        width += colModel.getColumn(col).getPreferredWidth();
      }
    }
    if (ocTable.getLeftPane() != null) {
      width += ocTable.getLeftPane().getPreferredSize().width;
    }
    if (ocTable.getRightPane() != null) {
      width += ocTable.getRightPane().getPreferredSize().width;
    }
    if (ocTable.getHeadPane() != null) {
      height += ocTable.getHeadPane().getPreferredSize().height;
    }
    if (ocTable.getBottomPane() != null) {
      height += ocTable.getBottomPane().getPreferredSize().height;
    }
    height += ocTable.getRowHeight() * 8;
    return new Dimension(width, height);
  }

  // <editor-fold defaultstate="collapsed" desc="paint table methods">
  @Override
  public void paint(Graphics g, JComponent c) {
    Rectangle drawRect;
    Rectangle clipRect = g.getClipBounds();
    // get four area reference to scrollabe area
    Rectangle srRect = ocTable.getScrollRowsArea();
    Rectangle scRect = ocTable.getScrollColumnsArea();
    Rectangle frRect = ocTable.getFixedRowsArea();
    Rectangle fcRect = ocTable.getFixedColumnsArea();

    //paint scroll aread
    drawRect = srRect.intersection(scRect).intersection(clipRect);
    if (!drawRect.isEmpty()) {
      g.setClip(drawRect);
      super.paint(g, c);
    }
    // paint fixed rows area
    drawRect = scRect.intersection(frRect).intersection(clipRect);
    if (!drawRect.isEmpty()) {
      // accord to BasicTableUI paint method, decrease height from area.
      if (drawRect.y + drawRect.height == frRect.y + frRect.height) {
        drawRect.height--;
      }
      g.setClip(drawRect);
      super.paint(g, c);
    }
    // paint fixed columns area
    drawRect = srRect.intersection(fcRect).intersection(clipRect);
    if (!drawRect.isEmpty()) {
      g.setClip(drawRect);
      super.paint(g, c);
    }
    // paint fully fixed area (top left corner)
    drawRect = frRect.intersection(fcRect).intersection(clipRect);
    if (!drawRect.isEmpty()) {
      // accord to BasicTableUI paint method, decrease height from area.
      if (drawRect.y + drawRect.height == frRect.y + frRect.height) {
        drawRect.height--;
      }
      g.setClip(drawRect);
      super.paint(g, c);
    }
    // paint fixed line
    g.setClip(clipRect);
    drawFixedLines(g, frRect, fcRect);
  }

  /**
   * paint fixed line
   *
   * @param g graphics device
   * @param fixedRowsRect fixed rows area
   * @param fixedColumnsRect fixed columns area.
   */
  protected void drawFixedLines(Graphics g, Rectangle fixedRowsRect,
          Rectangle fixedColumnsRect) {
    g.setColor(ocTable.getFixedLineColor());
    if (!fixedRowsRect.isEmpty()) {
      g.drawLine(fixedRowsRect.x,
              fixedRowsRect.y + fixedRowsRect.height - 1,
              fixedRowsRect.x + fixedRowsRect.width,
              fixedRowsRect.y + fixedRowsRect.height - 1);
    }
    if (!fixedColumnsRect.isEmpty()) {
      g.drawLine(fixedColumnsRect.x + fixedColumnsRect.width - 1,
              fixedColumnsRect.y,
              fixedColumnsRect.x + fixedColumnsRect.width - 1,
              fixedColumnsRect.y + fixedColumnsRect.height);
    }
  }
  // </editor-fold>

  private class MouseWheelHandler implements MouseWheelListener {

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
      int vPos = ocTable.getVerticalPosition();
      if (e.getUnitsToScroll() < 0) {
        vPos -= ocTable.getScrollRowsArea().height / 5;
      } else {
        vPos += ocTable.getScrollRowsArea().height / 5;
      }
      ocTable.setVerticalPosition(vPos);
    }
  }

  private class MouseMotionHandler implements MouseMotionListener {

    @Override
    public void mouseDragged(MouseEvent e) {
      // auto scroll when drag mouse to border
      if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
        Point point = e.getPoint();
        Rectangle srRect = ocTable.getScrollRowsArea();
        Rectangle scRect = ocTable.getScrollColumnsArea();
        Rectangle csRect = srRect.intersection(scRect);
        if (csRect.contains(point)) {
          return;
        }
        int row = ocTable.rowAtEntireTable(point);
        int col = ocTable.columnAtEntireTable(point);
        if (point.x < csRect.x) {
          int sCol = ocTable.columnAtPoint(csRect.getLocation());
          col = sCol > ocTable.getFixedColumnCount() ? sCol - 1
                  : col == -1 ? 0 : col;
        } else {
          col = col == -1 ? ocTable.getColumnCount() - 1 : col;
        }
        if (point.y < csRect.y) {
          int sRow = ocTable.rowAtPoint(csRect.getLocation());
          row = sRow > ocTable.getFixedRowCount() ? sRow - 1
                  : row == -1 ? 0 : row;
        } else {
          row = row == -1 ? ocTable.getRowCount() - 1 : row;
        }
        if (row != -1 && col != -1 && !ocTable.isCellVisible(row, col)) {
          try {
            Thread.sleep(200);
          } catch (InterruptedException ex) {
          }
          ocTable.scrollCellToVisible(row, col);
          if (!ocTable.isCellSelected(row, col)) {
            ocTable.changeSelection(row, col, e.isControlDown(), true);
          }
        }
      }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
  }

  private class TableLayout implements LayoutManager {

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
      return new Dimension(400, 300);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
      return new Dimension();
    }

    @Override
    public void layoutContainer(Container parent) {
      Insets insets = ocTable.getInsets();
      int right = ocTable.getWidth() - insets.right;
      int width = right - insets.left;
      int bottom = ocTable.getHeight() - insets.bottom;
      int height = bottom - insets.top;
      TableSidePane ltPane = ocTable.getLeftPane();
      TableSidePane hdPane = ocTable.getHeadPane();
      TableSidePane rtPane = ocTable.getRightPane();
      TableSidePane btPane = ocTable.getBottomPane();
      int wLeft = ltPane == null ? 0 : ltPane.getPreferredSize().width;
      int hHead = hdPane == null ? 0 : hdPane.getPreferredSize().height;
      int wRight = rtPane == null ? 0 : rtPane.getPreferredSize().width;
      int hBottom = btPane == null ? 0 : btPane.getPreferredSize().height;
      if (ltPane != null) {
        ltPane.setBounds(insets.left, insets.top, wLeft,
                height - hBottom);
      }
      if (hdPane != null) {
        hdPane.setBounds(insets.left + wLeft, insets.top,
                width - wLeft - wRight, hHead);
      }
      if (rtPane != null) {
        rtPane.setBounds(right - wRight, insets.top, wRight,
                height - hBottom);
      }
      if (btPane != null) {
        btPane.setBounds(insets.left, bottom - hBottom,
                width - wRight, hBottom);
      }
      rbCorner.setBounds(right - wRight, bottom - hBottom,
              wRight, hBottom);
    }
  }
}
