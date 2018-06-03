/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import com.xzq.osc.JocTable;
import com.xzq.osc.OrchidUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.SizeSequence;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author zqxu
 */
public class TableHeadPane extends TableSidePane
        implements TableColumnModelListener {

  private static final String uiClassID = "ocTableHeadPane";
  protected TableColumnModel columnModel;
  protected int rowCount;
  protected int rowHeight;
  protected SizeSequence heightModel;
  protected ArrayList<ColumnHead> headList;
  protected TableHeadRenderer defaultRenderer;
  private boolean resizingAllowed;
  private boolean reorderingAllowed;
  protected int draggedTarget = -1;
  private ColumnHeaderPoint draggedHeader;
  private ColumnHeaderPoint rolloverHeader;
  protected TableColumn resizingColumn;
  private boolean doubleClickOptimizeWidth;
  private boolean clickToggleSort;

  static {
    UIManager.put(uiClassID, "com.xzq.osc.table.TableHeadPaneUI");
  }

  // <editor-fold defaultstate="collapsed" desc="Constructor and initialize">
  /**
   * initialize local vars.
   */
  @Override
  protected void initializeLocalVars() {
    rowCount = 1;
    rowHeight = 22;
    resizingAllowed = true;
    reorderingAllowed = true;
    doubleClickOptimizeWidth = true;
    setBackground(Color.LIGHT_GRAY);
    headList = new ArrayList<ColumnHead>();
  }

  /**
   * install on table.
   *
   * @param table table component.
   */
  @Override
  protected void install(JocTable table) {
    registerTablePropertyListener("columnModel");
    registerTablePropertyListener("horizontalPosition");
    updateColumnModel(table.getColumnModel());
  }

  @Override
  protected void uninstall(JocTable table) {
    super.uninstall(table);
    updateColumnModel(null);
  }

  /**
   * update associated table column model.
   *
   * @param columnModel column model.
   */
  protected void updateColumnModel(TableColumnModel columnModel) {
    TableColumnModel old = this.columnModel;
    if (old != null) {
      old.removeColumnModelListener(this);
    }
    this.columnModel = columnModel;
    if (columnModel != null) {
      columnModel.addColumnModelListener(this);
    }
    repaint();
    firePropertyChange("columnModel", old, columnModel);
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
   * @param propertyName
   */
  @Override
  protected void tablePropertyChange(String propertyName) {
    if ("columnModel".equals(propertyName)) {
      updateColumnModel(table.getColumnModel());
    } else if ("horizontalPosition".equals(propertyName)) {
      repaintScrollColumns();
    }
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="header row count and height">
  /**
   * Returns column header row count.
   *
   * @return column header row count.
   */
  public int getRowCount() {
    return rowCount;
  }

  /**
   * Set column header row count.
   *
   * @param rowCount column header row count.
   */
  public void setRowCount(int rowCount) {
    if (this.rowCount != rowCount) {
      int old = this.rowCount;
      this.rowCount = rowCount;
      if (heightModel != null) {
        int diff = rowCount - old;
        if (diff > 0) {
          heightModel.insertEntries(rowCount, diff, getRowHeight());
        } else if (diff < 0) {
          heightModel.removeEntries(rowCount + diff, -diff);
        }
      }
      resizeAndRepaint();
      firePropertyChange("rowCount", old, rowCount);
    }
  }

  /**
   * Returns column header row height model.
   *
   * @return column header row height model.
   */
  protected SizeSequence getHeightModel() {
    if (heightModel == null) {
      heightModel = new SizeSequence(rowCount, getRowHeight());
    }
    return heightModel;
  }

  /**
   * Returns default column header row height.
   *
   * @return default column header row height.
   */
  public int getRowHeight() {
    return rowHeight;
  }

  /**
   * Set default column header row height.
   *
   * @param rowHeight default column header row height.
   */
  public void setRowHeight(int rowHeight) {
    if (this.rowHeight != rowHeight) {
      this.rowHeight = rowHeight;
      heightModel = null;
      resizeAndRepaint();
    }
  }

  /**
   * Returns height for the row of column header.
   *
   * @param rowIndex row index of column header.
   * @return height for the row of column header.
   */
  public int getRowHeight(int rowIndex) {
    return heightModel == null ? rowHeight : heightModel.getSize(rowIndex);
  }

  /**
   * Set height for the row of column header.
   *
   * @param rowIndex row index of column header.
   * @param rowHeight height for the row of column header.
   */
  public void setRowHeight(int rowIndex, int rowHeight) {
    if (getHeightModel().getSize(rowIndex) != rowHeight) {
      getHeightModel().setSize(rowIndex, rowHeight);
      resizeAndRepaint();
    }
  }

  /**
   * Returns position of the row of column header.
   *
   * @param rowIndex row index of column header..
   * @return position of the row of column header.
   */
  public int getRowPosition(int rowIndex) {
    return heightModel == null ? rowIndex * getRowHeight()
            : heightModel.getPosition(rowIndex);
  }

  /**
   * Returns row index of column header on the position.
   *
   * @param position position.
   * @return row index of column header on the position.
   */
  public int getRowIndex(int position) {
    return (heightModel == null) ? position / getRowHeight()
            : heightModel.getIndex(position);
  }

  /**
   * Returns total height of all rows of column header.
   *
   * @return total height of all rows of column header.
   */
  public int getTotalHeadHeight() {
    return heightModel == null ? rowHeight * getRowCount()
            : heightModel.getPosition(getRowCount() - 1)
            + heightModel.getSize(getRowCount() - 1);
  }

  @Override
  public Rectangle getVisibleRect() {
    Rectangle rect = super.getVisibleRect();
    int height = table.getHeight();
    TableBottomPane bottomPane = table.getBottomPane();
    if (bottomPane != null && bottomPane.isVisible()) {
      height -= bottomPane.getHeight();
    }
    rect.height = Math.min(height, rect.height);
    return rect;
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="header icon and text">
  /**
   * Returns ColumnHead object of the model index in columnModel and row index
   * of columnModelIndex header.
   *
   * @param columnModelIndex columnModelIndex model index in columnModel.
   * @param rowIndex row index of columnModelIndex header.
   * @return ColumnHead object.
   */
  protected ColumnHead getColumnHead(int columnModelIndex, int rowIndex) {
    ColumnHead head = new ColumnHead(columnModelIndex, rowIndex);
    int index = headList.indexOf(head);
    return index == -1 ? null : headList.get(index);
  }

  /**
   * Create ColumnHead object if currently no ColumnHead object for the column
   * header cell.
   *
   * @param columnModelIndex column model index int columnModel.
   * @param rowIndex row index of column header.
   * @return ColumnHead object.
   */
  protected ColumnHead createColumnHead(int columnModelIndex, int rowIndex) {
    ColumnHead head = getColumnHead(columnModelIndex, rowIndex);
    if (head == null) {
      head = new ColumnHead(columnModelIndex, rowIndex);
      headList.add(head);
    }
    return head;
  }

  /**
   * Returns icon for the column header cell, or null if no specified.
   *
   * @param columnModelIndex column model index in columnModel
   * @param rowIndex row index of column header.
   * @return icon for the column header cell.
   */
  public Icon getHeadIcon(int columnModelIndex, int rowIndex) {
    ColumnHead head = getColumnHead(columnModelIndex, rowIndex);
    return head == null ? null : head.aIcon;
  }

  /**
   * Set icon for the column header cell.
   *
   * @param columnModelIndex column model index in columnModel
   * @param rowIndex row index of column header.
   * @param aIcon icon for the column header cell.
   */
  public void setHeadIcon(int columnModelIndex, int rowIndex, Icon aIcon) {
    createColumnHead(columnModelIndex, rowIndex).aIcon = aIcon;
    int columnIndex = convertColumnIndexToView(columnModelIndex);
    repaintColumnHead(columnIndex, rowIndex);
  }

  /**
   * Returns value of the column header cell.
   *
   * @param columnModelIndex column model index in columnModel
   * @param rowIndex row index of column header.
   * @return value of the column header cell.
   */
  public Object getHeadValue(int columnModelIndex, int rowIndex) {
    ColumnHead head = getColumnHead(columnModelIndex, rowIndex);
    return head == null ? null : head.aValue;
  }

  /**
   * Set value of the column header cell.
   *
   * @param columnModelIndex column model index in columnModel
   * @param rowIndex row index of column header.
   * @param aValue value of the column header cell.
   */
  public void setHeadValue(int columnModelIndex, int rowIndex, Object aValue) {
    createColumnHead(columnModelIndex, rowIndex).aValue = aValue;
    int columnIndex = convertColumnIndexToView(columnModelIndex);
    repaintColumnHead(columnIndex, rowIndex);
  }

  /**
   * clear all column header cell.
   */
  public void clearColumnHead() {
    headList.clear();
    repaint();
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="header cell combine">
  /**
   * Returns combine column count of the column header cell.
   *
   * @param columnModelIndex column model index in columnModel
   * @param rowIndex row index of column header.
   * @return combine column count of the column header cell.
   */
  public int getHeadColumnSpan(int columnModelIndex, int rowIndex) {
    ColumnHead head = getColumnHead(columnModelIndex, rowIndex);
    return head == null ? 1 : head.columnSpan;
  }

  /**
   * Set combine column count of the column header cell.
   *
   * @param columnModelIndex column model index in columnModel
   * @param rowIndex row index of column header.
   * @param columnSpan combine column count of the column header cell.
   */
  public void setHeadColumnSpan(int columnModelIndex, int rowIndex,
          int columnSpan) {
    if (columnSpan < 1) {
      throw new IllegalArgumentException("Cannot set column span less than 1");
    }
    ColumnHead head = createColumnHead(columnModelIndex, rowIndex);
    int oldSpan = head.columnSpan;
    if (columnSpan == oldSpan) {
      return;
    }
    if (columnSpan > oldSpan) {
      for (int m = columnModelIndex + oldSpan;
              m < columnModelIndex + columnSpan; m++) {
        if (getHeadColumnSpan(m, rowIndex) != 1) {
          throw new IllegalStateException("head " + m + "," + rowIndex
                  + " already in span");
        }
      }
    }
    head.columnSpan = columnSpan;
    for (int m = columnModelIndex + 1;
            m < columnModelIndex + columnSpan; m++) {
      createColumnHead(m, rowIndex).columnSpan = -1;
    }
    if (oldSpan > columnSpan) {
      for (int m = columnModelIndex + columnSpan;
              m < columnModelIndex + oldSpan; m++) {
        createColumnHead(m, rowIndex).columnSpan = 1;
      }
    }
    repaint();
  }

  /**
   * Returns combine row count of the column header cell.
   *
   * @param columnModelIndex column model index in columnModel
   * @param rowIndex row index of column header.
   * @return combine row count of the column header cell.
   */
  public int getHeadRowSpan(int columnModelIndex, int rowIndex) {
    ColumnHead head = getColumnHead(columnModelIndex, rowIndex);
    return head == null ? 1 : head.rowSpan;
  }

  /**
   * Set combine row count of the column header cell.
   *
   * @param columnModelIndex column model index in columnModel
   * @param rowIndex row index of column header.
   * @param rowSpan combine row count of the column header cell.
   */
  public void setHeadRowSpan(int columnModelIndex, int rowIndex,
          int rowSpan) {
    if (rowSpan < 1) {
      throw new IllegalArgumentException("Cannot set row span less than 1");
    }
    ColumnHead head = createColumnHead(columnModelIndex, rowIndex);
    int oldSpan = head.rowSpan;
    if (rowSpan == oldSpan) {
      return;
    }
    if (rowSpan > oldSpan) {
      for (int l = rowIndex + oldSpan; l < rowIndex + rowSpan; l++) {
        if (getHeadColumnSpan(columnModelIndex, l) != 1) {
          throw new IllegalStateException("head " + columnModelIndex + "," + l
                  + " already in span");
        }
      }
    }
    head.rowSpan = rowSpan;
    for (int l = rowIndex + 1; l < rowIndex + rowSpan; l++) {
      createColumnHead(columnModelIndex, l).rowSpan = -1;
    }
    if (oldSpan > rowSpan) {
      for (int l = rowIndex + rowSpan; l < rowIndex + oldSpan; l++) {
        createColumnHead(columnModelIndex, l).rowSpan = 1;
      }
    }
    repaint();
  }

  /**
   * Returns combine column count for the column header cell for view.
   *
   * @param columnViewIndex column view index in table.
   * @param rowIndex row index of column header.
   * @return combine column count for the column header cell for view.
   */
  public int getHeadColumnSpanForView(int columnViewIndex, int rowIndex) {
    int modelIndex = convertColumnIndexToModel(columnViewIndex);
    int span = getHeadColumnSpan(modelIndex, rowIndex);
    if (span < 1) {
      if (!checkColumnCoveredValidForView(columnViewIndex, rowIndex)) {
        span = 1;
      }
    } else if (span > 1) {
      span = getValidColumnSpanForView(columnViewIndex, rowIndex);
    }
    return span;
  }

  /**
   *
   * @param columnViewIndex
   * @param rowIndex
   * @return
   */
  protected boolean checkColumnCoveredValidForView(int columnViewIndex,
          int rowIndex) {
    int iModel, iView, covered = 1;
    int modelIndex = convertColumnIndexToModel(columnViewIndex);
    for (int m = 1;; m++) {
      iModel = modelIndex - m;
      if (iModel < 0) {
        return false;
      }
      iView = convertColumnIndexToView(iModel);
      if (columnViewIndex - iView != m) {
        return false;
      }
      covered++;
      if (getHeadColumnSpan(iModel, rowIndex) != -1) {
        break;
      }
    }
    return covered == getHeadColumnSpan(iModel, rowIndex);
  }

  /**
   *
   * @param columnViewIndex
   * @param rowIndex
   * @return
   */
  protected int getValidColumnSpanForView(int columnViewIndex,
          int rowIndex) {
    int span = 1;
    int iModel, iView;
    int modelIndex = convertColumnIndexToModel(columnViewIndex);
    for (int m = 1;; m++) {
      iModel = modelIndex + m;
      if (iModel >= table.getModel().getColumnCount()) {
        break;
      }
      if (getHeadColumnSpan(iModel, rowIndex) != -1) {
        break;
      }
      iView = convertColumnIndexToView(iModel);
      if (iView - columnViewIndex != m) {
        break;
      }
      span++;
    }
    return span;
  }

  /**
   *
   * @param columnViewIndex
   * @param rowIndex
   * @return
   */
  public int getHeadRowSpanForView(int columnViewIndex, int rowIndex) {
    int modelIndex = convertColumnIndexToModel(columnViewIndex);
    int span = getHeadRowSpan(modelIndex, rowIndex);
    if (span + rowIndex > getRowCount()) {
      span = getRowCount() - rowIndex;
    }
    return span;
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="header cell renderer">
  /**
   * Returns default column header cell renderer
   *
   * @return default column header cell renderer
   */
  public TableHeadRenderer getDefaultRenderer() {
    if (defaultRenderer == null) {
      defaultRenderer = new DefaultTableHeadRenderer();
    }
    return defaultRenderer;
  }

  /**
   * Set default column header cell renderer.
   *
   * @param renderer default column header cell renderer
   */
  public void setDefaultRenderer(TableHeadRenderer renderer) {
    this.defaultRenderer = renderer;
    repaint();
  }

  /**
   * Returns cell renderer for the columnModelIndex header cell.
   *
   * @param columnModelIndex columnModelIndex view index of table.
   * @param rowIndex row index of columnModelIndex header.
   * @return
   */
  public Component getHeadRenderer(int columnModelIndex, int rowIndex) {
    ColumnHead head = getColumnHead(columnModelIndex, rowIndex);
    int columnViewIndex = convertColumnIndexToView(columnModelIndex);
    Icon headIcon = head == null ? null : head.aIcon;
    Object headValue = head != null && head.aValue != null ? head.aValue
            : getColumn(columnViewIndex).getHeaderValue();
    TableHeadRenderer renderer = head == null || head.renderer == null
            ? getDefaultRenderer() : head.renderer;
    boolean dragged = draggedHeader != null
            && draggedHeader.modelIndex == columnModelIndex
            && draggedHeader.rowIndex == rowIndex;
    boolean rollover = rolloverHeader != null
            && rolloverHeader.modelIndex == columnModelIndex
            && rolloverHeader.rowIndex == rowIndex;
    return renderer.getTableHeadRendererComponent(getTable(), headIcon,
            headValue, dragged, rollover, columnModelIndex, rowIndex);
  }

  /**
   * Set cell renderer for the column header cell.
   *
   * @param columnModelIndex column model index in columnModel.
   * @param rowIndex row index of column header.
   * @param renderer cell renderer.
   */
  public void setHeadRenderer(int columnModelIndex, int rowIndex,
          TableHeadRenderer renderer) {
    createColumnHead(columnModelIndex, rowIndex).renderer = renderer;
    int columnIndex = convertColumnIndexToView(columnModelIndex);
    repaintColumnHead(columnIndex, rowIndex);
  }

  /**
   * Returns preferred width for the column.
   *
   * @param columnViewIndex column view index of table.
   * @return preferred width for the column.
   */
  public int getHeadColumnPreferredWidth(int columnViewIndex) {
    int width = 0;
    int modelIndex = convertColumnIndexToModel(columnViewIndex);
    for (int i = 0; i < rowCount; i++) {
      width = Math.max(width,
              getHeadRenderer(modelIndex, i).getPreferredSize().width);
    }
    return width;
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="scroll and paint header">
  /**
   * Returns fixed columns area.
   *
   * @return fixed columns area.
   */
  public Rectangle getFixedColumnsArea() {
    Rectangle rect = getBounds();
    rect.x = rect.y = 0;
    rect.width = getTable().getFixedColumnsArea().width;
    return rect;
  }

  /**
   * Returns scrollable columns area.
   *
   * @return scrollable columns area.
   */
  public Rectangle getScrollColumnsArea() {
    Rectangle rect = getBounds();
    rect.x = rect.y = 0;
    Rectangle fcRect = getFixedColumnsArea();
    rect.x += fcRect.width;
    rect.width -= fcRect.width;
    return rect;
  }

  /**
   * Returns area for the columnViewIndex header cell.
   *
   * @param columnViewIndex columnViewIndex index.
   * @param rowIndex row index.
   * @return area for the columnViewIndex header cell.
   */
  public Rectangle getHeadRect(int columnViewIndex, int rowIndex) {
    // ------------- process cell combination -----------------
    int columnSpan = getHeadColumnSpanForView(columnViewIndex, rowIndex);
    int rowSpan = getHeadRowSpanForView(columnViewIndex, rowIndex);
    if (columnSpan == -1 || rowSpan == -1) {
      return new Rectangle();
    }
    // ---------------------------------------------------
    Rectangle rect = getTable().getCellRect(0, columnViewIndex, true);
    rect.y = getRowPosition(rowIndex);
    rect.height = getRowHeight(rowIndex);
    rect.x -= getLocation().x;
    // ------------- process cell combination -----------------
    if (columnSpan > 1) {
      for (int i = 1; i < columnSpan; i++) {
        rect.width += getColumnWidth(columnViewIndex + i);
      }
    }
    if (rowSpan > 1) {
      for (int i = 1; i < rowSpan; i++) {
        rect.height += getRowHeight(rowIndex + i);
      }
    }
    // ---------------------------------------------------
    return rect;
  }

  /**
   * Returns column index on the point, not consider cell combination.
   *
   * @param point point.
   * @return column index on the point.
   */
  public int columnAtPointNoSpan(Point point) {
    point = new Point(point.x + getLocation().x, point.y);
    return table.columnAtPoint(point);
  }

  /**
   * Returns column index on the point, consider cell combination.
   *
   * @param point point.
   * @return column index on the point.
   */
  public int columnAtPoint(Point point) {
    point = new Point(point.x + getLocation().x, point.y);
    int column = table.columnAtPoint(point);
    // ------------- process cell combination -----------------
    int row = rowAtPoint(point);
    while (getHeadColumnSpanForView(column, row) == -1) {
      column--;
    }
    // ---------------------------------------------------
    return column;
  }

  /**
   * Returns row index on the point, not consider cell combination.
   *
   * @param point point.
   * @return row index on the point.
   */
  public int rowAtPointNoSpan(Point point) {
    int row = getRowIndex(point.y);
    return row < 0 || row >= getRowCount() ? -1 : row;
  }

  /**
   * Returns row index on the point, consider cell combination.
   *
   * @param point point.
   * @return row index on the point.
   */
  public int rowAtPoint(Point point) {
    int row = getRowIndex(point.y);
    row = row < 0 || row >= getRowCount() ? -1 : row;
    // ------------- process cell combination -----------------
    point = new Point(point.x + getLocation().x, point.y);
    int column = table.columnAtPoint(point);
    while (getHeadRowSpanForView(column, row) == -1) {
      row--;
    }
    // ---------------------------------------------------
    return row;
  }

  /**
   * Returns position of the column.
   *
   * @param column column index.
   * @return position of the column.
   */
  public int getColumnPosition(int column) {
    Rectangle rect = getTable().getCellRect(0, column, true);
    return rect.x - getLocation().x;
  }

  /**
   * Returns table column of the column index.
   *
   * @param column column index.
   * @return table column of the column index.
   */
  public TableColumn getColumn(int column) {
    return getTable().getColumn(column);
  }

  /**
   * Returns column count of table.
   *
   * @return column count of table.
   */
  public int getColumnCount() {
    return getTable().getColumnCount();
  }

  /**
   * Returns width of the column.
   *
   * @param column column index
   * @return width of the column.
   */
  public int getColumnWidth(int column) {
    return getTable().getColumn(column).getWidth();
  }

  /**
   * repaint scrollable area.
   */
  public void repaintScrollColumns() {
    repaint(getScrollColumnsArea());
  }

  /**
   * repaint the column header cell.
   *
   * @param column column index.
   * @param rowIndex row index.
   */
  public void repaintColumnHead(int column, int rowIndex) {
    repaint(getHeadRect(column, rowIndex));
  }

  /**
   * repaint column header of the column.
   *
   * @param column column index.
   */
  public void repaintColumn(int column) {
    Rectangle rect = getHeadRect(column, 0);
    rect.height = getHeight();
    repaint(rect);
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="move and adjust column">
  /**
   * convert column model index to view index.
   *
   * @param columnModelIndex column model index.
   * @return column view index.
   * @see javax.swing.JTable#convertColumnIndexToView(int)
   */
  public int convertColumnIndexToView(int columnModelIndex) {
    return getTable().convertColumnIndexToView(columnModelIndex);
  }

  /**
   * convert column view index to model index.
   *
   * @param columnViewIndex column model index.
   * @return column view index.
   * @see javax.swing.JTable#convertColumnIndexToModel(int)
   */
  public int convertColumnIndexToModel(int columnViewIndex) {
    return getTable().convertColumnIndexToModel(columnViewIndex);
  }

  /**
   * Returns whether allow resize column or not. default is true.
   *
   * @return true for allow resize column and false not.
   */
  public boolean getResizingAllowed() {
    return resizingAllowed;
  }

  /**
   * Set whether allow resize column or not.
   *
   * @param resizingAllowed true for allow resize column and false not.
   */
  public void setResizingAllowed(boolean resizingAllowed) {
    boolean old = this.resizingAllowed;
    this.resizingAllowed = resizingAllowed;
    firePropertyChange("resizingAllowed", old, resizingAllowed);
  }

  /**
   * Set rollover column header cell.
   *
   * @param columnViewIndex column index.
   * @param rowIndex row index of column header.
   */
  public void setRolloverHead(int columnViewIndex, int rowIndex) {
    ColumnHeaderPoint old = rolloverHeader;
    if (columnViewIndex != -1 && rowIndex != -1) {
      int modelIndex = convertColumnIndexToModel(columnViewIndex);
      rolloverHeader = new ColumnHeaderPoint(modelIndex, rowIndex);
    } else {
      rolloverHeader = null;
    }
    if (!OrchidUtils.equals(old, rolloverHeader)) {
      if (rolloverHeader != null) {
        repaintColumnHead(columnViewIndex, rowIndex);
      }
      if (old != null) {
        columnViewIndex = table.convertColumnIndexToView(old.modelIndex);
        repaintColumnHead(columnViewIndex, old.rowIndex);
      }
      firePropertyChange("rolloverHeader", old, rolloverHeader);
    }
  }

  /**
   * Returns whether allow reorder column or not. default is false.
   *
   * @return true for allow reorder column and false not.
   */
  public boolean getReorderingAllowed() {
    return reorderingAllowed;
  }

  /**
   * Set whether allow reorder column or not.
   *
   * @param reorderingAllowed true for allow reorder column and false not.
   */
  public void setReorderingAllowed(boolean reorderingAllowed) {
    boolean old = this.reorderingAllowed;
    if (this.reorderingAllowed != reorderingAllowed) {
      this.reorderingAllowed = reorderingAllowed;
      firePropertyChange("reorderingAllowed", old, reorderingAllowed);
    }
  }

  /**
   * Check whether the columnViewIndex can dragg or not.
   *
   * @param columnViewIndex columnViewIndex index.
   * @return true for draggable and false not.
   */
  protected boolean canDragged(int columnViewIndex) {
    return getReorderingAllowed()
            && columnViewIndex >= getTable().getFixedColumnCount();
  }

  /**
   * Returns currently draaged column index, -1 if no column dragged.
   *
   * @return currently draaged column index, -1 if no column dragged.
   */
  public int getDraggedColumn() {
    return draggedHeader == null ? -1
            : convertColumnIndexToView(draggedHeader.modelIndex);
  }

  /**
   * Set currently draaged columnViewIndex header cell.
   *
   * @param columnViewIndex columnViewIndex index.
   * @param rowIndex row index.
   */
  public void setDraggedHeader(int columnViewIndex, int rowIndex) {
    ColumnHeaderPoint old = draggedHeader;
    if (columnViewIndex != -1 && rowIndex != -1) {
      int modelIndex = convertColumnIndexToModel(columnViewIndex);
      draggedHeader = new ColumnHeaderPoint(modelIndex, rowIndex);
    } else {
      draggedHeader = null;
    }
    if (!OrchidUtils.equals(old, draggedHeader)) {
      if (draggedHeader != null) {
        repaintColumnHead(columnViewIndex, rowIndex);
      }
      if (old != null) {
        columnViewIndex = convertColumnIndexToView(old.modelIndex);
        repaintColumnHead(columnViewIndex, old.rowIndex);
      }
      firePropertyChange("draggedHeader", old, draggedHeader);
    }
  }

  /**
   * Returns target column index of drag action.
   *
   * @return target column index of drag action.
   */
  public int getDraggedTarget() {
    return draggedTarget;
  }

  /**
   * Set target column index of drag action.
   *
   * @param draggedTarget target column index of drag action.
   */
  public void setDraggedTarget(int draggedTarget) {
    int old = this.draggedTarget;
    if (old != draggedTarget) {
      this.draggedTarget = draggedTarget;
      if (old != -1) {
        repaintColumn(old);
      }
      if (draggedTarget != -1) {
        if (!getTable().isColumnVisible(draggedTarget)) {
          getTable().scrollColumnToVisible(draggedTarget);
          try {
            Thread.sleep(200);
          } catch (InterruptedException ex) {
          }
        }
        repaintColumn(draggedTarget);
      }
      firePropertyChange("draggedTarget", old, draggedTarget);
    }
  }

  /**
   * Returns column index currently resizing.
   *
   * @return column index currently resizing.
   */
  public TableColumn getResizingColumn() {
    return resizingColumn;
  }

  /**
   * Set column index currently resizing.
   *
   * @param resizingColumn column index currently resizing.
   */
  public void setResizingColumn(TableColumn resizingColumn) {
    this.resizingColumn = resizingColumn;
  }

  /**
   * Returns whether optimize column with on double click on column right side
   * or not. true for optimize and false not. default is true.
   *
   * @return true or false.
   */
  public boolean isDoubleClickOptimizeWidth() {
    return doubleClickOptimizeWidth;
  }

  /**
   * Set whether optimize column with on double click on column right side or
   * not.
   *
   * @param doubleClickOptimizeWidth true for optimize or false not.
   */
  public void setDoubleClickOptimizeWidth(boolean doubleClickOptimizeWidth) {
    boolean old = this.doubleClickOptimizeWidth;
    if (old != doubleClickOptimizeWidth) {
      this.doubleClickOptimizeWidth = doubleClickOptimizeWidth;
      firePropertyChange("doubleClickOptimizeWidth", old, doubleClickOptimizeWidth);
    }
  }

  /**
   * Return true indicate that mouse click should toggle column sort, otherwise
   * not. default is true.
   *
   * @return true for mouse click toggle column and false not.
   */
  public boolean isClickToggleSort() {
    return clickToggleSort;
  }

  /**
   * Set whether mouse click should toggle column sort or not.
   *
   * @param clickToggleSort true for mouse click toggle column and false not.
   */
  public void setClickToggleSort(boolean clickToggleSort) {
    this.clickToggleSort = clickToggleSort;
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="column model event process">
  /**
   *
   * @param e
   */
  @Override
  public void columnAdded(TableColumnModelEvent e) {
    repaint();
  }

  /**
   *
   * @param e
   */
  @Override
  public void columnRemoved(TableColumnModelEvent e) {
    repaint();
  }

  /**
   *
   * @param e
   */
  @Override
  public void columnMoved(TableColumnModelEvent e) {
    repaint();
  }

  /**
   *
   * @param e
   */
  @Override
  public void columnMarginChanged(ChangeEvent e) {
    repaint();
  }

  /**
   *
   * @param e
   */
  @Override
  public void columnSelectionChanged(ListSelectionEvent e) {
  }
  //</editor-fold>

  /**
   * Class for store column header cell properties.
   */
  protected static class ColumnHead {

    /**
     * column model index.
     */
    int modelIndex;
    /**
     * row index of column header.
     */
    int rowIndex;
    /**
     * column header cell icon.
     */
    Icon aIcon;
    /**
     * column header cell value.
     */
    Object aValue;
    /**
     * combine column count of column header cell.
     */
    int columnSpan;
    /**
     * combine row count of column header cell.
     */
    int rowSpan;
    /**
     * cell renderer of column header cell.
     */
    TableHeadRenderer renderer;

    /**
     *
     * @param modelIndex
     * @param rowIndex
     */
    public ColumnHead(int modelIndex, int rowIndex) {
      this(modelIndex, rowIndex, null);
    }

    /**
     *
     * @param modelIndex
     * @param rowIndex
     * @param aValue
     */
    public ColumnHead(int modelIndex, int rowIndex, Object aValue) {
      this.modelIndex = modelIndex;
      this.rowIndex = rowIndex;
      this.aValue = aValue;
      this.columnSpan = 1;
      this.rowSpan = 1;
    }

    /**
     *
     * @return
     */
    public boolean isCoveredBySpan() {
      return columnSpan == -1 || rowSpan == -1;
    }

    @Override
    public boolean equals(Object obj) {
      return obj != null && obj instanceof ColumnHead
              && ((ColumnHead) obj).modelIndex == modelIndex
              && ((ColumnHead) obj).rowIndex == rowIndex;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 83 * hash + this.modelIndex;
      hash = 83 * hash + this.rowIndex;
      return hash;
    }
  }
}
