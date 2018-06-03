/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import com.xzq.osc.JocTable;
import com.xzq.osc.plaf.OrchidDefaults;
import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.table.TableColumn;

/**
 *
 * @author zqxu
 */
public class TableHeadPaneUI extends TableSidePaneUI {

  private static final Cursor resizeCursor;
  private Handler handler;
  private Cursor savedCursor;
  protected TableHeadPane pane;
  protected CellRendererPane rendererPane;

  static {
    resizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
  }

  public static TableHeadPaneUI createUI(JComponent c) {
    return new TableHeadPaneUI();
  }

  /**
   * install ui
   *
   * @param c head pane
   */
  @Override
  public void installUI(JComponent c) {
    super.installUI(c);
    pane = (TableHeadPane) c;
    rendererPane = new CellRendererPane();
    pane.add(rendererPane);
    installListeners();
    pane.setLayout(getHandler());
    pane.setClickToggleSort(
            UIManager.getBoolean(OrchidDefaults.TABLE_TITLE_SORTER));
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
    uninstallListeners();
    pane.remove(rendererPane);
    rendererPane = null;
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

  /**
   *
   * @param column
   * @return
   */
  protected boolean canResize(TableColumn column) {
    return (column != null) && pane.getResizingAllowed()
            && column.getResizable();
  }

  /**
   *
   * @param point
   * @return
   */
  protected TableColumn getResizingColumn(Point point) {
    int column = pane.columnAtPoint(point);
    int index = pane.rowAtPoint(point);
    if (column != -1 && index != -1) {
      Rectangle rect = pane.getHeadRect(column, index);
      rect.grow(-3, 0);
      if (!rect.contains(point)) {
        if (point.x < rect.x + rect.width / 2) {
          column = column - 1;
        }
        if (column >= 0) {
          return pane.getColumn(column);
        }
      }
    }
    return null;
  }

  /**
   *
   * @param point
   * @return
   */
  protected int getDraggedTarget(Point point) {
    Rectangle scRect = pane.getScrollColumnsArea();
    int column = pane.columnAtPoint(point);
    int fixedColumnCount = pane.getTable().getFixedColumnCount();
    if (column == -1) {
      column = point.x < scRect.x ? 0 : pane.getColumnCount() - 1;
    }
    int sCol = pane.columnAtPoint(scRect.getLocation());
    if (column < sCol && sCol > fixedColumnCount) {
      column = sCol - 1;
    }
    if (column < fixedColumnCount) {
      column = fixedColumnCount;
    }
    return column;
  }

  private void swapCursor(boolean resizing) {
    if (resizing) {
      savedCursor = pane.getCursor();
      pane.setCursor(resizeCursor);
    } else {
      pane.setCursor(savedCursor);
    }
  }

  /**
   *
   * @param column
   * @return
   */
  protected int viewIndexForColumn(TableColumn column) {
    for (int i = 0; i < pane.getColumnCount(); i++) {
      if (pane.getColumn(i) == column) {
        return i;
      }
    }
    return -1;
  }

  /**
   *
   * @param evt
   * @return
   */
  protected boolean shouldIgnore(MouseEvent evt) {
    return !(pane.isEnabled() && pane.getTable().isEnabled());
  }

  // <editor-fold defaultstate="collapsed" desc="paint column header">
  /**
   *
   * @param g
   * @param c
   */
  @Override
  public void paint(Graphics g, JComponent c) {
    Rectangle clipRect = g.getClipBounds().intersection(pane.getVisibleRect());
    Rectangle drawRect = clipRect.intersection(pane.getFixedColumnsArea());
    if (!drawRect.isEmpty()) {
      paintClipArea(g, drawRect);
    }
    drawRect = clipRect.intersection(pane.getScrollColumnsArea());
    if (!drawRect.isEmpty()) {
      paintClipArea(g, drawRect);
    }
    g.setClip(clipRect);
    paintDraggedTarget(g);
    if (pane.getTable().getFixedColumnCount() > 0) {
      paintFixedLine(g);
    }
    rendererPane.removeAll();
  }

  /**
   *
   * @param g
   * @param clipRect
   */
  protected void paintClipArea(Graphics g, Rectangle clipRect) {
    g.setClip(clipRect);
    Point upperLeft = clipRect.getLocation();
    Point lowerRight = new Point(clipRect.x + clipRect.width - 1,
            clipRect.y + clipRect.height - 1);
    int cMin = pane.columnAtPoint(upperLeft);
    int cMax = pane.columnAtPointNoSpan(lowerRight);
    cMin = cMin < 0 ? 0 : cMin;
    cMax = cMax < 0 ? pane.getColumnCount() - 1 : cMax;
    int iMin = pane.rowAtPoint(upperLeft);
    int iMax = pane.rowAtPointNoSpan(lowerRight);
    for (int column = cMin; column <= cMax; column++) {
      for (int line = iMin; line <= iMax; line++) {
        paintColumnHead(g, column, line);
      }
    }
  }

  /**
   *
   * @param g
   * @param column
   * @param line
   */
  protected void paintColumnHead(Graphics g, int column, int line) {
    Rectangle cellRect = pane.getHeadRect(column, line);
    // ------------- process cell combination -----------------
    while (cellRect.isEmpty()) {
      column--;
      if (pane.getTable().isColumnVisible(column)) {
        return;
      }
      cellRect = pane.getHeadRect(column, line);
    }
    // ---------------------------------------------------
    int modelIndex = pane.convertColumnIndexToModel(column);
    Component component = pane.getHeadRenderer(modelIndex, line);
    rendererPane.paintComponent(g, component, pane, cellRect.x, cellRect.y,
            cellRect.width, cellRect.height, true);
  }

  /**
   *
   * @param g
   */
  protected void paintDraggedTarget(Graphics g) {
    int column = pane.getDraggedColumn();
    int target = pane.getDraggedTarget();
    if (column != -1 && target != -1 && column != target) {
      int position = pane.getColumnPosition(target);
      if (target > column) {
        position = position + pane.getColumnWidth(target) - 3;
      }
      g.setColor(Color.BLACK);
      g.fillRect(position, 1, 3, pane.getHeight() - 2);
    }
  }

  /**
   *
   * @param g
   */
  protected void paintFixedLine(Graphics g) {
    Rectangle scRect = pane.getFixedColumnsArea();
    g.setColor(pane.getTable().getFixedLineColor());
    g.drawLine(scRect.width - 1, 0, scRect.width - 1, scRect.height - 1);
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
      return new Dimension(400, pane.getTotalHeadHeight());
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
      return preferredLayoutSize(parent);
    }

    @Override
    public void layoutContainer(Container parent) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void mouseClicked(MouseEvent e) {
      if (shouldIgnore(e) || !SwingUtilities.isLeftMouseButton(e)) {
        return;
      }
      int columnIndex = pane.columnAtPoint(e.getPoint());
      if (columnIndex == -1) {
        return;
      }
      TableColumn resizing = getResizingColumn(e.getPoint());
      if (resizing != null) {
        if (pane.isDoubleClickOptimizeWidth()
                && e.getClickCount() == 2) {
          columnIndex = pane.convertColumnIndexToView(
                  resizing.getModelIndex());
          pane.getTable().optimizeColumnWidth(columnIndex);
        }
      } else if (e.getClickCount() == 1) {
        JocTable table = pane.getTable();
        RowSorter sorter;
        if (table != null && (sorter = table.getRowSorter()) != null
                && pane.isClickToggleSort()) {
          columnIndex = table.convertColumnIndexToModel(
                  columnIndex);
          if (e.isControlDown()) {
            sorter.setSortKeys(null);
          } else {
            sorter.toggleSortOrder(columnIndex);
          }
        }
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
      if (shouldIgnore(e)) {
        return;
      }
      pane.setResizingColumn(null);
      pane.setDraggedHeader(-1, -1);

      int column = pane.columnAtPoint(e.getPoint());
      if (column != -1) {
        TableColumn resizingColumn = getResizingColumn(e.getPoint());
        if (canResize(resizingColumn)) {
          pane.setResizingColumn(resizingColumn);
        } else {
          int index = pane.rowAtPoint(e.getPoint());
          pane.setDraggedHeader(column, index);
        }
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      if (shouldIgnore(e)) {
        return;
      }
      int draggedColumn = pane.getDraggedColumn();
      int draggedTarget = pane.getDraggedTarget();
      if (draggedColumn != draggedTarget
              && draggedColumn != -1 && draggedTarget != -1) {
        pane.getTable().getColumnModel().moveColumn(draggedColumn,
                pane.getDraggedTarget());
      }
      pane.setResizingColumn(null);
      pane.setDraggedHeader(-1, -1);
      pane.setDraggedTarget(-1);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      mouseMoved(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
      if (shouldIgnore(e)) {
        return;
      }
      pane.setRolloverHead(-1, -1);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (shouldIgnore(e)) {
        return;
      }
      int column = pane.columnAtPoint(e.getPoint());
      int index = pane.rowAtPoint(e.getPoint());
      pane.setRolloverHead(column, index);
      TableColumn resizingColumn = pane.getResizingColumn();
      int draggedColumn = pane.getDraggedColumn();
      if (resizingColumn != null) {
        column = viewIndexForColumn(resizingColumn);
        int position = pane.getColumnPosition(column);
        int width = e.getPoint().x - position;
        resizingColumn.setWidth(width);
      } else if (pane.canDragged(draggedColumn)) {
        int draggedTarget = getDraggedTarget(e.getPoint());
        pane.setDraggedTarget(draggedTarget);
      }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      if (shouldIgnore(e)) {
        return;
      }
      int column = pane.columnAtPoint(e.getPoint());
      int index = pane.rowAtPoint(e.getPoint());
      pane.setRolloverHead(column, index);
      boolean resizing = canResize(getResizingColumn(e.getPoint()));
      if (resizing != (pane.getCursor() == resizeCursor)) {
        swapCursor(resizing);
      }
    }
  }
}
