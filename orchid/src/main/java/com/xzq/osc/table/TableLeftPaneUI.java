/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author zqxu
 */
public class TableLeftPaneUI extends TableSidePaneUI {

  protected TableLeftPane pane;
  protected CellRendererPane rendererPane;
  private Handler handler;
  private int dragStop;
  private int dragStart;
  private int dragColumn;
  private boolean dragOption;

  public static TableLeftPaneUI createUI(JComponent c) {
    return new TableLeftPaneUI();
  }

  /**
   *
   * @param c
   */
  @Override
  public void installUI(JComponent c) {
    super.installUI(c);
    pane = (TableLeftPane) c;
    rendererPane = new CellRendererPane();
    pane.add(rendererPane);
    installListeners();
    pane.setLayout(getHandler());
  }

  /**
   *
   */
  protected void installListeners() {
    pane.addMouseListener(getHandler());
    pane.addMouseMotionListener(getHandler());
  }

  /**
   *
   * @param c
   */
  @Override
  public void uninstallUI(JComponent c) {
    pane.remove(rendererPane);
    rendererPane = null;
    uninstallListeners();
    pane.setLayout(null);
    pane = null;
  }

  /**
   *
   */
  protected void uninstallListeners() {
    pane.removeMouseListener(getHandler());
    pane.removeMouseMotionListener(getHandler());
  }

  private Handler getHandler() {
    if (handler == null) {
      handler = new Handler();
    }
    return handler;
  }

  // <editor-fold defaultstate="collapsed" desc="mouse event process">
  /**
   * process mouse click event.
   *
   * @param row row index.
   * @param column column index.
   */
  protected void clickAtCell(int row, int column) {
    if (row == -1 || column == -1) {
      return;
    }
    dragColumn = column;
    if (column == 0 && pane.getShowTickerColumn()) {
      clickAtTicker(row);
    } else if (pane.getTable().getRowSelectionAllowed()) {
      clickAtSelection(row);
    }
  }

  /**
   * process mouse click on row tick column.
   *
   * @param row row index.
   */
  protected void clickAtTicker(int row) {
    if (row == TableLeftPane.HEADER_ROW) {
      int mode = pane.getTable().getTickerModel().getSelectionMode();
      if (mode == ListSelectionModel.SINGLE_SELECTION) {
        return;
      }
      if (pane.getTable().isAllRowsTicked()) {
        pane.getTable().clearTicker();
      } else {
        pane.getTable().tickAll();
      }
    } else {
      dragOption = pane.getTable().isRowTicked(row);
      if (dragOption) {
        pane.getTable().removeRowTickerInterval(row, row);
      } else {
        pane.getTable().addRowTickerInterval(row, row);
      }
      dragOption = !dragOption;
      dragStart = dragStop = row;
    }
  }

  /**
   * process mouse click on row selection column.
   *
   * @param row row index.
   */
  protected void clickAtSelection(int row) {
    if (row == TableLeftPane.HEADER_ROW) {
      int mode = pane.getTable().getSelectionModel().getSelectionMode();
      if (mode == ListSelectionModel.SINGLE_SELECTION) {
        return;
      }
      if (pane.getTable().isAllRowsSelected()) {
        pane.getTable().clearSelection();
      } else {
        pane.getTable().selectAll();
      }
    } else {
      dragOption = pane.getTable().isRowSelected(row);
      if (dragOption) {
        pane.getTable().removeRowSelectionInterval(row, row);
      } else {
        pane.getTable().addRowSelectionInterval(row, row);
      }
      dragOption = !dragOption;
      dragStart = dragStop = row;
    }
  }

  /**
   * Returns drag stop row index on the point, consider scroll position.
   *
   * @param point point
   * @return drag stop row index on the point.
   */
  protected int getDragStop(Point point) {
    Rectangle srRect = pane.getScrollRowsArea();
    int row = pane.rowAtPoint(point);
    if (row < 0) {
      row = point.y < srRect.y ? 0 : pane.getTable().getRowCount() - 1;
    }
    int fixedRowCount = pane.getTable().getFixedRowCount();
    int cross = getDragCrossFixedRow(row, fixedRowCount);
    if (cross < 0) {
      int sRow = pane.rowAtPoint(srRect.getLocation());
      if (sRow > fixedRowCount) {
        row = sRow - 1;
      }
    } else if (cross > 0) {
      int sRow = pane.rowAtPoint(srRect.getLocation());
      if (sRow > fixedRowCount) {
        row = fixedRowCount;
      }
    }
    return row;
  }

  /**
   * Returns -1 when mouse drag up and cross fixed row, 1 when mouse drag down
   * and cross fixed row, otherwise return 0.
   *
   * @return -1 when mouse drag up and cross fixed row, 1 when mouse drag down
   * and cross fixed row, otherwise return 0.
   */
  protected int getDragCrossFixedRow(int row, int fixedRowCount) {
    if (row < dragStop && row < fixedRowCount && dragStop >= fixedRowCount) {
      return -1;
    } else if (row > dragStop && row >= fixedRowCount
            && dragStop < fixedRowCount) {
      return 1;
    } else {
      return 0;
    }
  }

  /**
   * Set drag stop row.
   *
   * @param row row index.
   */
  protected void setDragStop(int row) {
    if (row == dragStop) {
      return;
    }
    if (!pane.getTable().isRowVisible(row)) {
      pane.getTable().scrollRowToVisible(row);
      try {
        Thread.sleep(200);
      } catch (InterruptedException ex) {
      }
    }
    boolean option = dragOption;
    int rMin = row > dragStart ? dragStart : row;
    int rMax = row > dragStart ? row : dragStart;
    if (dragStop < rMin || dragStop > rMax) {
      option = !dragOption;
      int oMin = rMin;
      rMin = dragStop < rMin ? dragStop : rMax + 1;
      rMax = dragStop > rMax ? dragStop : oMin - 1;
    }
    changeDragRange(rMin, rMax, option);
    dragStop = row;
  }

  /**
   * change row tick or selection through mouse drag.
   */
  protected void changeDragRange(int row0, int row1, boolean option) {
    if (dragColumn == 0 && pane.getShowTickerColumn()) {
      if (option) {
        pane.getTable().addRowTickerInterval(row0, row1);
      } else {
        pane.getTable().removeRowTickerInterval(row0, row1);
      }
    } else {
      int mode = pane.getTable().getSelectionModel().getSelectionMode();
      if (mode == ListSelectionModel.SINGLE_SELECTION) {
        return;
      }
      if (option) {
        pane.getTable().addRowSelectionInterval(row0, row1);
      } else {
        pane.getTable().removeRowSelectionInterval(row0, row1);
      }
    }
  }

  /**
   * Returns true if mouse event should be ignored, otherwise false.
   *
   * @param evt mouse event.
   * @return true if mouse event should be ignored, otherwise false.
   */
  protected boolean shouldIgnore(MouseEvent evt) {
    return !(pane.isEnabled() && pane.getTable().isEnabled());
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="paint pane">
  @Override
  public void paint(Graphics g, JComponent c) {
    Rectangle clipRect = g.getClipBounds();
    Rectangle frRect = pane.getFixedRowsArea();
    Rectangle srRect = pane.getScrollRowsArea();

    Rectangle drawRect = frRect.intersection(clipRect);
    if (!drawRect.isEmpty()) {
      paintClipArea(g, drawRect);
    }
    drawRect = srRect.intersection(clipRect);
    if (!drawRect.isEmpty()) {
      paintClipArea(g, drawRect);
    }
    if (pane.getTable().getFixedRowCount() > 0) {
      g.setClip(clipRect);
      paintFixedLine(g);
    }
    rendererPane.removeAll();
  }

  private void paintClipArea(Graphics g, Rectangle drawRect) {
    g.setClip(drawRect);
    int rMin, rMax, cMin, cMax;
    Point lowerRight = new Point(drawRect.x + drawRect.width - 1,
            drawRect.y + drawRect.height - 1);
    rMin = pane.rowAtPoint(drawRect.getLocation());
    rMax = pane.rowAtPoint(lowerRight);
    cMin = pane.columnAtPoint(drawRect.getLocation());
    cMax = pane.columnAtPoint(lowerRight);
    cMin = cMin == -1 ? 0 : cMin;
    cMax = cMax == -1 ? pane.getColumnCount() - 1 : cMax;
    rMax = rMax == -1 ? pane.getTable().getRowCount() - 1 : rMax;
    Rectangle rect = pane.getCellRect(rMin, cMin);
    for (int row = rMin; row <= rMax; row++) {
      if (row == -1) {
        continue;
      }
      rect.height = pane.getRowHeight(row);
      for (int col = cMin; col <= cMax; col++) {
        rect.x = col == 0 ? 0 : pane.getTickerColumnWidth();
        rect.width = pane.getColumnWidth(col);
        Component c = pane.prepareCellRenderer(row, col);
        rendererPane.paintComponent(g, c, pane, rect.x, rect.y, rect.width,
                rect.height, true);
      }
      rect.y += rect.height;
    }
  }

  private void paintFixedLine(Graphics g) {
    Rectangle frRect = pane.getFixedRowsArea();
    g.setColor(pane.getTable().getFixedLineColor());
    g.drawLine(0, frRect.height - 1, frRect.width - 1, frRect.height - 1);
  }
  // </editor-fold>

  private class Handler implements LayoutManager, MouseInputListener {

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
      Dimension size = new Dimension();
      if (pane.getShowSelectionColumn()) {
        size.width += pane.getSelectionColumnWidth();
      }
      if (pane.getShowTickerColumn()) {
        size.width += pane.getTickerColumnWidth();
      }
      return size;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
      return preferredLayoutSize(parent);
    }

    @Override
    public void layoutContainer(Container parent) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
      if (shouldIgnore(e)) {
        return;
      }
      dragStart = -1;
      if (e.getButton() == MouseEvent.BUTTON1) {
        int row = pane.rowAtPoint(e.getPoint());
        int column = pane.columnAtPoint(e.getPoint());
        clickAtCell(row, column);
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      if (shouldIgnore(e)) {
        return;
      }
      dragStart = -1;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (shouldIgnore(e)) {
        return;
      }
      if (dragStart != -1) {
        setDragStop(getDragStop(e.getPoint()));
      }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
  }
}
