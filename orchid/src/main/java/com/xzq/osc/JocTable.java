/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.JocTableCellRenderer.CheckRenderer;
import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.plaf.LookAndFeelManager;
import com.xzq.osc.plaf.OrchidDefaults;
import com.xzq.osc.table.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;

/**
 * JocTable is a extended table from JTable, to support fixable rows and
 * columns, multiline header and header cell can be combined, a column for
 * selection and a column to tick rows at left, cell layout model for support
 * cell color. <br> JocTable has built-in support for the scroll bar, so do not
 * put it into JScrollPane.<br> For keep data binding function, the cell layout
 * model is stand alone from data model, so be carefully with data model change,
 * for example: sort, moving column.<br> JocTable do not support
 * autoResizeMode, replace by method optimizeColumnWidth.
 *
 * @author zqxu
 */
public class JocTable extends JTable
        implements TableLayoutModelListener, OrchidAboutIntf {

  private static final String uiClassID = "OrchidTableUI";
  /**
   * table head pane.
   */
  protected TableHeadPane headPane;
  /**
   * table left pane.
   */
  protected TableLeftPane leftPane;
  /**
   * table right pane.
   */
  protected TableRightPane rightPane;
  /**
   * table bottom pane.
   */
  protected TableBottomPane bottomPane;
  protected int optimizeColumnDepth = 10000;
  protected boolean editable = true;
  protected TableLayoutModel tableLayoutModel;
  protected Color focusCellBackground;
  protected Color focusCellForeground;
  protected Color fixedLineColor;
  protected boolean fixedRowAdjusting;
  protected boolean fixedColumnAdjusting;
  private int horizontalPosition;
  private int verticalPosition;
  protected ListSelectionModel tickerModel;
  protected ListSelectionListener tickerHandler;
  private boolean ignoreTableRepaint;
  private boolean optionPopupActive;
  private JPopupMenu optionPopupMenu;
  private PopupMenuListener optionPopupHandler;
  private EventListenerList optionPopupListeners;
  private TableColumnsFactory columnsFactory;

  /**
   * Constructor
   *
   * @see JTable#JTable()
   */
  public JocTable() {
    super();
  }

  /**
   * Constructor
   *
   * @param dm
   * @see JTable#JTable(javax.swing.table.TableModel)
   */
  public JocTable(TableModel dm) {
    super(dm);
  }

  /**
   * Constructor
   *
   * @param dm
   * @param cm
   * @see JTable#JTable(javax.swing.table.TableModel,
   * javax.swing.table.TableColumnModel)
   */
  public JocTable(TableModel dm, TableColumnModel cm) {
    super(dm, cm);
  }

  /**
   * Constructor
   *
   * @param dm
   * @param cm
   * @param sm
   * @see JTable#JTable(javax.swing.table.TableModel,
   * javax.swing.table.TableColumnModel, javax.swing.ListSelectionModel)
   */
  public JocTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
    super(dm, cm, sm);
  }

  /**
   * Constructor
   *
   * @param numRows
   * @param numColumns
   * @see JTable#JTable(int, int)
   */
  public JocTable(int numRows, int numColumns) {
    super(numRows, numColumns);
  }

  /**
   * Constructor
   *
   * @param rowData
   * @param columnNames
   * @see JTable#JTable(java.lang.Object[][], java.lang.Object[])
   */
  public JocTable(Object[][] rowData, Object[] columnNames) {
    super(rowData, columnNames);
  }

  // <editor-fold defaultstate="collapsed" desc="override super method">
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
    for (Component sub : getComponents()) {
      SwingUtilities.updateComponentTreeUI(sub);
    }
    super.updateUI();
  }

  /**
   * initialize local vars.
   */
  @Override
  protected void initializeLocalVars() {
    super.initializeLocalVars();
    setRowHeight(20);
    setDoubleBuffered(true);
    setTickerModel(new DefaultListSelectionModel());
    setTableLayoutModel(createDefaultTableLayoutModel());
    setLeftPane(createDefaultLeftPane());
    setHeadPane(createDefaultHeadPane());
    setRightPane(createDefaultRightPane());
    setBottomPane(createDefaultBottomPane());
    setFixedLineColor(Color.BLACK);
    optionPopupActive = true;
    optionPopupListeners = new EventListenerList();
  }

  /**
   * Cancel default table header creation.
   *
   * @return null
   */
  @Override
  protected JTableHeader createDefaultTableHeader() {
    return null;
  }

  /**
   * Use JocTableCellRenderer as default cell renderer
   */
  @Override
  @SuppressWarnings("unchecked")
  protected void createDefaultRenderers() {
    defaultRenderersByColumnClass = new UIDefaults(2, 0.75f);
    defaultRenderersByColumnClass.put(
            Object.class, new JocTableCellRenderer());
    defaultRenderersByColumnClass.put(
            Boolean.class, new JocTableCellRenderer(CheckRenderer.class));
  }

  /**
   * Use JocTableCellEditor as default cell editor
   */
  @Override
  @SuppressWarnings("unchecked")
  protected void createDefaultEditors() {
    defaultEditorsByColumnClass = new UIDefaults(2, 0.75f);
    defaultEditorsByColumnClass.put(
            Object.class, new JocTableCellEditor());
    defaultEditorsByColumnClass.put(
            Boolean.class, new JocTableCellEditor(JCheckBox.class));
  }

  /**
   * Override, returns default cell renderer for Object.class if column class is
   * null.
   *
   * @param columnClass column class
   * @return default cell editor.
   */
  @Override
  public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
    if (columnClass == null) {
      return (TableCellRenderer) defaultRenderersByColumnClass.get(Object.class);
    } else {
      Object renderer = defaultRenderersByColumnClass.get(columnClass);
      if (renderer != null) {
        return (TableCellRenderer) renderer;
      } else {
        return getDefaultRenderer(columnClass.getSuperclass());
      }
    }
  }

  /**
   * Override, returns default cell editor for Object.class if column class is
   * null.
   *
   * @param columnClass column class
   * @return default cell editor.
   */
  @Override
  public TableCellEditor getDefaultEditor(Class<?> columnClass) {
    if (columnClass == null) {
      return (TableCellEditor) defaultEditorsByColumnClass.get(Object.class);
    } else {
      Object editor = defaultEditorsByColumnClass.get(columnClass);
      if (editor != null) {
        return (TableCellEditor) editor;
      } else {
        return getDefaultEditor(columnClass.getSuperclass());
      }
    }
  }

  /**
   * Always using LEFT_TO_RIGHT.
   *
   * @param o ignored.
   */
  @Override
  public void setComponentOrientation(ComponentOrientation o) {
    super.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
  }

  /**
   * Always return true.
   *
   * @return true
   */
  @Override
  public boolean isValidateRoot() {
    return true;
  }

  /**
   * layout this component.
   */
  @Override
  public void doLayout() {
    LayoutManager layoutMgr = getLayout();
    if (layoutMgr != null) {
      layoutMgr.layoutContainer(this);
    }
  }

  /**
   * resize and repaint component and it's sub-panes.
   */
  @Override
  protected void resizeAndRepaint() {
    super.resizeAndRepaint();
    if (leftPane != null) {
      leftPane.revalidate();
    }
    if (headPane != null) {
      headPane.revalidate();
    }
    if (rightPane != null) {
      rightPane.revalidate();
    }
    if (bottomPane != null) {
      bottomPane.revalidate();
    }
  }

  /**
   * Because JocTable do not support property autoResizeMode, this method always
   * return AUTO_RESIZE_OFF.
   *
   * @return AUTO_RESIZE_OFF
   * @see #optimizeColumnWidth(int)
   * @see #optimizeAllColumnWidth()
   */
  @Override
  public int getAutoResizeMode() {
    return AUTO_RESIZE_OFF;
  }

  /**
   * JocTable do not support autoResizeMode.
   *
   * @param mode ingored.
   * @deprecated JocTable do not support autoResizeMode.
   * @see #optimizeColumnWidth(int)
   * @see #optimizeAllColumnWidth()
   */
  @Deprecated
  @Override
  public void setAutoResizeMode(int mode) {
  }

  @Override
  public void tableChanged(TableModelEvent e) {
    //because JTable do not consider the TableSizePane size and fixed rows or
    //columns, so the dirty region calculated by JTable was incorrect.
    //ignore the repaint initiate by JTable
    ignoreTableRepaint = true;
    super.tableChanged(e);
    ignoreTableRepaint = false;
    //re-calculate dirty region and initiate repaint
    if (getRowSorter() == null) {
      repaintChangedRows(e);
    } else {
      repaintSortedChangedRows(e);
    }
  }

  private void repaintChangedRows(TableModelEvent e) {
    Rectangle dirty = getRendererBounds();
    int row = e == null ? 0 : e.getFirstRow();
    int col = e == null ? 0 : e.getColumn();
    if (e != null && row != TableModelEvent.HEADER_ROW) {
      Rectangle cell = getCellRect(row, 0, true);
      dirty.y = cell.y;
      if (e.getType() != TableModelEvent.INSERT
              && e.getType() != TableModelEvent.DELETE) {
        if (col != TableModelEvent.ALL_COLUMNS) {
          cell = getCellRect(row, col, true);
          dirty.x = cell.x;
          dirty.width = cell.width;
        }
        row = e.getLastRow();
        if (row != Integer.MAX_VALUE) {
          cell = getCellRect(row, 0, true);
          dirty.height = cell.y + cell.height - dirty.y;
        }
      }
    }
    repaint(dirty.x, dirty.y, dirty.width, dirty.height);
  }

  private void repaintSortedChangedRows(TableModelEvent e) {
    Rectangle dirty = getRendererBounds();
    int row = e == null ? 0 : e.getFirstRow();
    int end = e == null ? 0 : e.getLastRow();
    if (e == null || row == TableModelEvent.HEADER_ROW
            || row + 10 < end || end < 0
            || e.getType() == TableModelEvent.INSERT
            || e.getType() == TableModelEvent.DELETE) {
      repaint(dirty.x, dirty.y, dirty.width, dirty.height);
      return;
    }
    int col = e.getColumn();
    if (col != TableModelEvent.ALL_COLUMNS) {
      Rectangle cell = getCellRect(row, col, true);
      dirty.x = cell.x;
      dirty.width = cell.width;
    }
    for (int model = row; model <= end; model++) {
      int view = convertRowIndexToView(model);
      Rectangle cell = getCellRect(view, 0, true);
      repaint(dirty.x, cell.y, dirty.width, cell.height);
    }
  }

  @Override
  public void repaint(long tm, int x, int y, int width, int height) {
    if (ignoreTableRepaint) {
      return;
    }
    super.repaint(tm, x, y, width, height);
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="cell editing">
  /**
   * Returns indicator for cell editable for entire table.
   *
   * @return true for editable for entire table and false not.
   */
  public boolean isEditable() {
    return editable;
  }

  /**
   * Set wheter entire table be editable or not.
   *
   * @param editable true for editable and false not.
   */
  public void setEditable(boolean editable) {
    boolean old = this.editable;
    if (old != editable) {
      this.editable = editable;
      if (isEditing() && !editable) {
        getCellEditor().cancelCellEditing();
      }
      repaint();
      firePropertyChange("editable", old, editable);
    }
  }

  /**
   * Override to support editable property of JocTable, Returns true if both
   * entire table editable and data model editable.
   *
   * @param row row index.
   * @param column column index.
   * @return true if cell editable and false not.
   */
  @Override
  public boolean isCellEditable(int row, int column) {
    return isEditable() && super.isCellEditable(row, column);
  }

  /**
   * make all text selected if editor component is inherited from
   * JTextComponent.
   *
   * @param editor cell editor
   * @param row row index
   * @param column column index
   * @return cell editor component.
   * @see JTable#prepareEditor(javax.swing.table.TableCellEditor, int, int)
   */
  @Override
  public Component prepareEditor(TableCellEditor editor, int row, int column) {
    Component editComponent = super.prepareEditor(editor, row, column);
    if (editComponent instanceof JTextComponent) {
      ((JTextComponent) editComponent).selectAll();
    }
    return editComponent;
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="getter/setter of sub-panes">
  /**
   * Returns table left pane.
   *
   * @return table left pane.
   */
  public TableLeftPane getLeftPane() {
    return leftPane;
  }

  /**
   * Set table left pane.
   *
   * @param leftPane table left pane.
   */
  public void setLeftPane(TableLeftPane leftPane) {
    if (this.leftPane != leftPane) {
      TableLeftPane old = this.leftPane;
      if (old != null) {
        remove(old);
        old.setTable(null);
      }
      this.leftPane = leftPane;
      if (leftPane != null) {
        leftPane.setTable(this);
        add(leftPane);
      }
      resizeAndRepaint();
      firePropertyChange("leftPane", old, leftPane);
    }
  }

  /**
   * Returns table head pane.
   *
   * @return head pane.
   */
  public TableHeadPane getHeadPane() {
    return headPane;
  }

  /**
   * Set table head pane.
   *
   * @param headPane head pane.
   */
  public void setHeadPane(TableHeadPane headPane) {
    if (this.headPane != headPane) {
      TableHeadPane old = this.headPane;
      if (old != null) {
        remove(old);
        old.setTable(null);
      }
      this.headPane = headPane;
      if (headPane != null) {
        headPane.setTable(this);
        add(headPane);
      }
      resizeAndRepaint();
      firePropertyChange("headPane", old, headPane);
    }
  }

  /**
   * Returns table right pane.
   *
   * @return right pane.
   */
  public TableRightPane getRightPane() {
    return rightPane;
  }

  /**
   * Set table right pane.
   *
   * @param rightPane right pane.
   */
  public void setRightPane(TableRightPane rightPane) {
    if (this.rightPane != rightPane) {
      TableRightPane old = this.rightPane;
      if (old != null) {
        remove(old);
        old.setTable(null);
      }
      this.rightPane = rightPane;
      if (rightPane != null) {
        rightPane.setTable(this);
        add(rightPane);
      }
      resizeAndRepaint();
      firePropertyChange("rightPane", old, rightPane);
    }
  }

  /**
   * Returns table bottom pane.
   *
   * @return bottom pane.
   */
  public TableBottomPane getBottomPane() {
    return bottomPane;
  }

  /**
   * Set table bottom pane.
   *
   * @param bottomPane bottom pane.
   */
  public void setBottomPane(TableBottomPane bottomPane) {
    if (this.bottomPane != bottomPane) {
      TableBottomPane old = this.bottomPane;
      if (old != null) {
        old.setTable(null);
        remove(old);
      }
      this.bottomPane = bottomPane;
      if (bottomPane != null) {
        bottomPane.setTable(this);
        add(bottomPane);
      }
      resizeAndRepaint();
      firePropertyChange("bottomPane", old, bottomPane);
    }
  }

  /**
   * Create default table left pane.
   *
   * @return left pane.
   */
  protected TableLeftPane createDefaultLeftPane() {
    return new TableLeftPane();
  }

  /**
   * Create default table head pane.
   *
   * @return head pane.
   */
  protected TableHeadPane createDefaultHeadPane() {
    return new TableHeadPane();
  }

  /**
   * Create default table right pane.
   *
   * @return right pane.
   */
  protected TableRightPane createDefaultRightPane() {
    return new TableRightPane();
  }

  /**
   * Create default table bottom pane.
   *
   * @return bottom pane.
   */
  protected TableBottomPane createDefaultBottomPane() {
    return new TableBottomPane();
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="cell position and ranges">
  /**
   * Returns bounds of cell area relative to table top left corner.
   *
   * @return cell area bounds.
   */
  public Rectangle getRendererBounds() {
    Insets insets = getInsets();
    Rectangle rect = new Rectangle(insets.left, insets.top,
            getWidth() - insets.right, getHeight() - insets.bottom);
    rect.x += getLeftPane() == null ? 0 : getLeftPane().getWidth();
    rect.y += getHeadPane() == null ? 0 : getHeadPane().getHeight();
    rect.width = rect.width - rect.x
            - (getRightPane() == null ? 0 : getRightPane().getWidth());
    rect.height = rect.height - rect.y
            - (getBottomPane() == null ? 0 : getBottomPane().getHeight());
    return rect;
  }

  /**
   * Returns cell cell.
   *
   * @param row row index.
   * @param column column index.
   * @param includeSpacing true for include spacing and false not.
   * @return cell cell.
   */
  @Override
  public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
    Rectangle rect = super.getCellRect(row, column, includeSpacing);
    Rectangle rendererRect = getRendererBounds();
    rect.translate(rendererRect.x, rendererRect.y);
    if (row >= getFixedRowCount()) {
      rect.y -= getVerticalPosition();
    }
    if (isScrollColumn(column)) {
      rect.x -= getHorizontalPosition();
    }
    return rect;
  }

  /**
   * Returns fixed rows bounds relative to table top left corner.
   *
   * @return fixed rows bounds.
   */
  public Rectangle getFixedRowsArea() {
    Rectangle fRect = getRendererBounds();
    Rectangle cRect = super.getCellRect(getFixedRowCount() - 1, 0, true);
    fRect.height = cRect.y + cRect.height;
    return fRect;
  }

  /**
   * Returns fixed columns bounds relative to table top left corner.
   *
   * @return fixed columns bounds.
   */
  public Rectangle getFixedColumnsArea() {
    Rectangle fRect = getRendererBounds();
    int fcc = getFixedColumnCount();
    if (fcc > 0) {
      Rectangle cRect = super.getCellRect(0, fcc - 1, true);
      fRect.width = cRect.x + cRect.width;
    } else {
      fRect.width = 0;
    }
    return fRect;
  }

  /**
   * Returns scrollable rows area relative to table top left corner.
   *
   * @return scrollable rows area.
   */
  public Rectangle getScrollRowsArea() {
    Rectangle sRect = getRendererBounds();
    Rectangle rRect = getFixedRowsArea();
    sRect.y += rRect.height;
    sRect.height -= rRect.height;
    return sRect;
  }

  /**
   * Returns scrollable columns area relative to table top left corner.
   *
   * @return scrollable columns area.
   */
  public Rectangle getScrollColumnsArea() {
    Rectangle sRect = getRendererBounds();
    Rectangle cRect = getFixedColumnsArea();
    sRect.x += cRect.width;
    sRect.width -= cRect.width;
    return sRect;
  }

  /**
   * Returns the index of the row that point lies in, or -1 if the point out of
   * renderer area or result is not in the range [0, getRowCount()-1].
   *
   * @param point the location of interest
   * @return the index of the row that point lies in, or -1 if the point out of
   * renderer area or result is not in the range [0, getRowCount()-1].
   * @see #rowAtEntireTable(java.awt.Point)
   */
  @Override
  public int rowAtPoint(Point point) {
    Rectangle rRect = getRendererBounds();
    if (point.y < rRect.y || point.y > (int) rRect.getMaxY()) {
      return -1;
    }
    return rowAtEntireTable(point);
  }

  /**
   * Returns the index of the row that point lies in, include point out of
   * renderer area, or -1 if the result is not in the range [0,
   * getRowCount()-1].
   *
   * @param point the location of interest
   * @return the index of the row that point lies in,or -1 if the result is not
   * in the range [0, getRowCount()-1].
   */
  public int rowAtEntireTable(Point point) {
    point = new Point(point);
    point.y = convertYtoEntireTable(point.y);
    return super.rowAtPoint(point);
  }

  /**
   * Translate y to entire table coordinate system, if head pane exist,
   * subcontract head pane height from y, if y greater than fixed rows height,
   * then add invisible rows height.
   *
   * @param y y coordinate
   * @return y coordinate translated.
   */
  protected int convertYtoEntireTable(int y) {
    Rectangle rRect = getRendererBounds();
    Rectangle srRect = getScrollRowsArea();
    if (y >= srRect.y) {
      y += getVerticalPosition();
    }
    return y - rRect.y;
  }

  /**
   * Returns the index of the column that point lies in, or -1 if the point out
   * of renderer area or result is not in the range [0, getColumnCount()-1].
   *
   * @param point the location of interest
   * @return the index of the column that point lies in, or -1 if the point out
   * of renderer area or result is not in the range [0, getColumnCount()-1].
   * @see #columnAtEntireTable(java.awt.Point)
   */
  @Override
  public int columnAtPoint(Point point) {
    Rectangle rRect = getRendererBounds();
    if (point.x < rRect.x || point.x > (int) rRect.getMaxX()) {
      return -1;
    }
    return columnAtEntireTable(point);
  }

  /**
   * Returns the index of the column that point lies in, include point out of
   * renderer area, or -1 if the result is not in the range [0,
   * getColumnCount()-1].
   *
   * @param point the location of interest
   * @return the index of the column that point lies in, or -1 if the result is
   * not in the range [0, getColumnCount()-1].
   */
  public int columnAtEntireTable(Point point) {
    int x = convertXtoEntireTable(point.x);
    return getColumnModel().getColumnIndexAtX(x);
  }

  /**
   * Translate y to entire table coordinate system, if left pane exist,
   * subcontract left pane width from x, if x greater than fixed columns width,
   * than add invisible columns width.
   *
   * @param x x coordinate
   * @return x coordinate translated.
   */
  protected int convertXtoEntireTable(int x) {
    Rectangle rRect = getRendererBounds();
    Rectangle scRect = getScrollColumnsArea();
    if (x >= scRect.x) {
      x += getHorizontalPosition();
    }
    return x - rRect.x;
  }

  /**
   * Returns width of all scrollable columns.
   *
   * @return width of all scrollable columns.
   */
  public int getTotalScrollColumnsWidth() {
    return getColumnModel().getTotalColumnWidth() - getFixedColumnsArea().width;
  }

  /**
   * Returns height of all scrollable rows.
   *
   * @return height of all scrollable rows.
   */
  public int getTotalScrollRowsHeight() {
    if (getRowCount() == 0) {
      return 0;
    } else {
      Rectangle rect = super.getCellRect(getRowCount() - 1, 0, true);
      return rect.y + rect.height - getFixedRowsArea().height;
    }
  }

  /**
   * shortcut to getColumnModel().getColumn(column)
   *
   * @param column column index.
   * @return table column of the column index.
   */
  public TableColumn getColumn(int column) {
    return getColumnModel().getColumn(column);
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="cell layout methods">
  /**
   * Returns cell layout model.
   *
   * @return cell layout model.
   */
  public TableLayoutModel getTableLayoutModel() {
    return tableLayoutModel;
  }

  /**
   * Set cell layout model.
   *
   * @param tableLayoutModel cell layout model.
   * @throws IllegalArgumentException if tableLayoutModel is null.
   */
  public void setTableLayoutModel(TableLayoutModel tableLayoutModel) {
    if (tableLayoutModel == null) {
      throw new IllegalArgumentException("null tableLayoutModel.");
    }
    if (this.tableLayoutModel != tableLayoutModel) {
      TableLayoutModel old = this.tableLayoutModel;
      if (old != null) {
        old.removeTableLayoutModelListener(this);
      }
      this.tableLayoutModel = tableLayoutModel;
      tableLayoutModel.addTableLayoutModelListener(this);
      resizeAndRepaint();
      firePropertyChange("tableLayoutModel", old, tableLayoutModel);
    }
  }

  /**
   * Create default cell layout model.
   *
   * @return cell layout model.
   */
  protected TableLayoutModel createDefaultTableLayoutModel() {
    return new TableLayoutModel();
  }

  /**
   * Returns color for paint fix line.
   *
   * @return color for paint fix line.
   * @see #setFixedLineColor(java.awt.Color)
   */
  public Color getFixedLineColor() {
    return fixedLineColor;
  }

  /**
   * Set color for paint fix line.
   *
   * @param fixedLineColor color for paint fix line.
   */
  public void setFixedLineColor(Color fixedLineColor) {
    if (this.fixedLineColor != fixedLineColor) {
      Color old = this.fixedLineColor;
      this.fixedLineColor = fixedLineColor;
      repaint(getFixedRowsArea());
      repaint(getFixedColumnsArea());
      firePropertyChange("fixedLineColor", old, fixedLineColor);
    }
  }

  /**
   * Returns value of property fixedRowAdjusting.
   *
   * @return value of property fixedRowAdjusting.
   * @see #setFixedRowAdjusting(boolean)
   */
  public boolean getFixedRowAdjusting() {
    return fixedRowAdjusting;
  }

  /**
   * Set value of property fixedRowAdjusting. this property indicate that fixed
   * rows is adjusting.
   *
   * @param fixedRowAdjusting value of property fixedRowAdjusting.
   */
  public void setFixedRowAdjusting(boolean fixedRowAdjusting) {
    if (this.fixedRowAdjusting != fixedRowAdjusting) {
      boolean old = this.fixedRowAdjusting;
      this.fixedRowAdjusting = fixedRowAdjusting;
      firePropertyChange("fixedRowAdjusting", old, fixedRowAdjusting);
    }
  }

  /**
   * Returns count of fixed rows.
   *
   * @return count of fixed rows.
   * @see TableLayoutModel#getFixedRowCount()
   */
  public int getFixedRowCount() {
    return getTableLayoutModel().getFixedRowCount();
  }

  /**
   * Set count of fixed rows. this is a reference of
   * tableLayoutModel.setFixedRowCount(fixedRowCount).
   *
   * @param fixedRowCount count of fixed rows.
   * @see TableLayoutModel#setFixedRowCount(int)
   */
  public void setFixedRowCount(int fixedRowCount) {
    getTableLayoutModel().setFixedRowCount(fixedRowCount);
  }

  /**
   * Returns value of property fixedColumnAdjusting.
   *
   * @return value of property fixedColumnAdjusting.
   * @see #setFixedColumnAdjusting(boolean)
   */
  public boolean getFixedColumnAdjusting() {
    return fixedColumnAdjusting;
  }

  /**
   * Set value of property fixedColumnAdjusting. this property indicate that
   * fixed columns is adjusting.
   *
   * @param fixedColumnAdjusting value of property fixedColumnAdjusting.
   */
  public void setFixedColumnAdjusting(boolean fixedColumnAdjusting) {
    if (this.fixedColumnAdjusting != fixedColumnAdjusting) {
      boolean old = this.fixedColumnAdjusting;
      this.fixedColumnAdjusting = fixedColumnAdjusting;
      firePropertyChange("fixedColumnAdjusting", old, fixedColumnAdjusting);
    }
  }

  /**
   * Returns count of fixed columns.
   *
   * @return count of fixed columns.
   * @see TableLayoutModel#getFixedColumnCount()
   */
  public int getFixedColumnCount() {
    return getTableLayoutModel().getFixedColumnCount();
  }

  /**
   * Set count of fixed columns.this is a reference of
   * tableLayoutModel.setFixedColumnCount(fixedColumnCount).
   *
   * @param fixedColumnCount fixed columns count.
   * @see TableLayoutModel#setFixedColumnCount
   */
  public void setFixedColumnCount(int fixedColumnCount) {
    getTableLayoutModel().setFixedColumnCount(fixedColumnCount);
  }

  /**
   * cell layout changed event processing.
   *
   * @param row row index
   * @param column column index.
   */
  @Override
  public void cellLayoutChanged(int row, int column) {
    int refRow = row == -1 ? 0 : row;
    int refCol = column == -1 ? 0 : column;
    Rectangle bounds = getRendererBounds();
    Rectangle rect = getCellRect(refRow, refCol, true);
    rect.width = column == -1 ? bounds.width : rect.width;
    rect.height = row == -1 ? bounds.height : rect.height;
    repaint(rect);
  }

  /**
   * fixed row count changed event processing.
   */
  @Override
  public void fixedRowCountChanged() {
    repaint(getRendererBounds());
  }

  /**
   * fixed column count changed event processing.
   */
  @Override
  public void fixedColumnCountChanged() {
    repaint(getRendererBounds());
  }

  /**
   * Returns background color for focus cell, if not set or null set, returns
   * color of the key OrchidDefaults.TABLE_FOCUS_BACKGROUND in UIManager.
   *
   * @return background color for focus cell
   */
  public Color getFocusCellBackground() {
    if (focusCellBackground != null) {
      return focusCellBackground;
    }
    return UIManager.getColor(OrchidDefaults.TABLE_FOCUS_BACKGROUND);
  }

  /**
   * Sets background color for focus cell.
   *
   * @param focusCellBackground background color for focus cell
   */
  public void setFocusCellBackground(Color focusCellBackground) {
    Color old = this.focusCellBackground;
    if (!OrchidUtils.equals(old, focusCellBackground)) {
      this.focusCellBackground = focusCellBackground;
      repaintFocusCell();
      firePropertyChange("focusCellBackground", old, focusCellBackground);
    }
  }

  /**
   * Returns foreground color for focus cell, if not set or null set, returns
   * color of the key OrchidDefaults.TABLE_FOCUS_FOREGROUND in UIManager.
   *
   * @return foreground color for focus cell
   */
  public Color getFocusCellForeground() {
    if (focusCellForeground != null) {
      return focusCellForeground;
    }
    return UIManager.getColor(OrchidDefaults.TABLE_FOCUS_FOREGROUND);
  }

  /**
   * Sets foreground color for focus cell.
   *
   * @param focusCellForeground foreground color for focus cell
   */
  public void setFocusCellForeground(Color focusCellForeground) {
    Color old = this.focusCellForeground;
    if (!OrchidUtils.equals(old, focusCellForeground)) {
      this.focusCellForeground = focusCellForeground;
      repaintFocusCell();
      firePropertyChange("focusCellForeground", old, focusCellForeground);
    }
  }

  private void repaintFocusCell() {
    int row = getSelectionModel().getLeadSelectionIndex();
    int column = getColumnModel().getSelectionModel().getLeadSelectionIndex();
    if (row != -1 && column != -1) {
      repaint(getCellRect(row, column, true));
    }
  }

  /**
   * Override for support cell color and font.
   *
   * @param renderer cell renderer
   * @param row row index
   * @param column column index
   * @return cell renderer component.
   * @see JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
   */
  @Override
  public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
    Component component = super.prepareRenderer(renderer, row, column);
    TableLayoutModel tableLayout = getTableLayoutModel();
    Color bgColor = null, fgColor = null;
    if (isCellFocused(row, column)) {
      bgColor = getFocusCellBackground();
      fgColor = getFocusCellForeground();
    }
    if (!isCellSelected(row, column)) {
      if (bgColor == null) {
        bgColor = tableLayout.getCellBackground(this, row, column);
      }
      if (fgColor == null) {
        fgColor = tableLayout.getCellForeground(this, row, column);
      }
    }
    if (bgColor != null) {
      component.setBackground(bgColor);
    }
    if (fgColor != null) {
      component.setForeground(fgColor);
    }
    ((JComponent) component).setOpaque(true);
    return component;
  }

  private boolean isCellFocused(int row, int column) {
    return getSelectionModel().getLeadSelectionIndex() == row
            && getColumnModel().getSelectionModel().getLeadSelectionIndex() == column;
  }

  /**
   * Returns scan depth for auto size row or column, for example, actual scan
   * rows = optimizeDepth / columnCount. default is 10000.
   *
   * @return scan depth.
   */
  public int getOptimizeColumnDepth() {
    return optimizeColumnDepth;
  }

  /**
   * Set scan depth for auto size row or column, for example, actual scan rows =
   * optimizeDepth / columnCount.
   *
   * @param optimizeColumnDepth
   */
  public void setOptimizeColumnDepth(int optimizeColumnDepth) {
    int old = this.optimizeColumnDepth;
    if (old != optimizeColumnDepth) {
      this.optimizeColumnDepth = optimizeColumnDepth;
      firePropertyChange("optimizeDepth", old, optimizeColumnDepth);
    }
  }

  /**
   * Adjust column to optimize width, scan cell one by one, accordeing to
   * optimizeDepth.
   *
   * @param column column index.
   */
  public int optimizeColumnWidth(int column) {
    int width = 0, cMargin = getColumnModel().getColumnMargin();
    if (headPane != null) {
      width = headPane.getHeadColumnPreferredWidth(column);
    }
    int scanRowCount = getOptimizeColumnDepth() / getColumnCount();
    for (int row = 0; row < getRowCount() && row < scanRowCount; row++) {
      Component c = prepareRenderer(getCellRenderer(row, column), row, column);
      if (c != null) {
        width = Math.max(width, cMargin + c.getPreferredSize().width);
      }
    }
    getColumn(column).setPreferredWidth(width);
    return width;
  }

  /**
   * Adjust all columns to optimize width.
   */
  public void optimizeAllColumnWidth() {
    for (int col = 0; col < getColumnCount(); col++) {
      optimizeColumnWidth(col);
    }
  }

  /**
   * Extend width of the column to fit area of table.
   *
   * @param column column index, -1 for last column.
   */
  public void extendColumnToFillTable(int column) {
    if (getColumnCount() == 0) {
      return;
    }
    int freeWidth = getRendererBounds().width;
    freeWidth -= getColumnModel().getTotalColumnWidth();
    if (freeWidth > 0) {
      column = column != -1 ? column : getColumnCount() - 1;
      getColumn(column).setPreferredWidth(freeWidth
              + getColumn(column).getPreferredWidth());
    }
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="scrollable support">
  /**
   * Returns table column that adjusting width, or null if no column in
   * adjusting.
   *
   * @return table column that adjusting width, or null if no column in
   * adjusting.
   */
  protected TableColumn getResizingColumn() {
    return headPane == null ? null : headPane.getResizingColumn();
  }

  /**
   * update all columns width to preferred width.
   */
  protected void setWidthsFromPreferredWidths() {
    for (int i = 0; i < getColumnCount(); i++) {
      TableColumn column = getColumn(i);
      column.setWidth(column.getPreferredWidth());
    }
  }

  /**
   * Returns horizontal left position in scroll area.
   *
   * @return horizontal left position in scroll area.
   */
  public int getHorizontalPosition() {
    return horizontalPosition;
  }

  /**
   * Set horizontal left position in scroll area.
   *
   * @param position horizontal left position in scroll area.
   */
  public void setHorizontalPosition(int position) {
    int max = getTotalScrollColumnsWidth() - getScrollColumnsArea().width;
    position = position > max ? max : position;
    position = position < 0 ? 0 : position;
    int old = getHorizontalPosition();
    if (old != position) {
      horizontalPosition = position;
      repaintScrollColumnsArea();
      firePropertyChange("horizontalPosition", old, horizontalPosition);
    }
  }

  /**
   * repaint scroll columns area.
   */
  protected void repaintScrollColumnsArea() {
    repaint(getScrollColumnsArea());
  }

  /**
   * Returns vertical top position in scroll area.
   *
   * @return vertical top position in scroll area.
   */
  public int getVerticalPosition() {
    return verticalPosition;
  }

  /**
   * Set vertical top position in scroll area.
   *
   * @param position vertical top position in scroll area.
   */
  public void setVerticalPosition(int position) {
    int max = getTotalScrollRowsHeight() - getScrollRowsArea().height;
    position = position > max ? max : position;
    position = position < 0 ? 0 : position;
    int old = getVerticalPosition();
    if (old != position) {
      verticalPosition = position;
      repaintScrollRowsArea();
      firePropertyChange("verticalPosition", old, position);
    }
  }

  /**
   * repaint scroll rows area.
   */
  protected void repaintScrollRowsArea() {
    repaint(getScrollRowsArea());
  }

  /**
   * Override for update horizontal scroll bar.
   *
   * @param e event object.
   */
  @Override
  public void columnMarginChanged(ChangeEvent e) {
    if (isEditing()) {
      removeEditor();
    }
    TableColumn resizingColumn = getResizingColumn();
    if (resizingColumn != null) {
      resizingColumn.setPreferredWidth(resizingColumn.getWidth());
    } else {
      for (int i = 0; i < getColumnCount(); i++) {
        TableColumn column = getColumn(i);
        if (column.getWidth() != column.getPreferredWidth()) {
          column.setWidth(column.getPreferredWidth());
        }
      }
    }
    resizeAndRepaint();
  }

  /**
   * Returns whether the column is a scrollable column.
   *
   * @param column column index.
   * @return true if the column is scrollable, or false if column is fixed.
   */
  protected boolean isScrollColumn(int column) {
    return column >= getFixedColumnCount();
  }

  /**
   * Returns true if cell is fully visible, otherwise false.
   *
   * @param row row index
   * @param column column index
   * @return true if cell is fully visible, otherwise false.
   */
  public boolean isCellVisible(int row, int column) {
    boolean scrollRow = row >= getFixedRowCount();
    boolean scrollColumn = isScrollColumn(column);
    if (!(scrollRow || scrollColumn)) {
      return true;
    }
    Rectangle cRect = getCellRect(row, column, true);
    Rectangle srRect = getScrollRowsArea();
    Rectangle scRect = getScrollColumnsArea();
    if (scrollRow && !srRect.contains(cRect)) {
      return false;
    }
    if (scrollColumn && !scRect.contains(cRect)) {
      return false;
    }
    return true;
  }

  /**
   * scroll to make the cell fully visible.
   *
   * @param aRect the cell to make visbile.
   */
  @Override
  public void scrollRectToVisible(Rectangle aRect) {
    Rectangle frRect = getFixedRowsArea();
    Rectangle fcRect = getFixedColumnsArea();
    Rectangle saRect = getScrollRowsArea().intersection(getScrollColumnsArea());
    if (frRect.contains(aRect) || fcRect.contains(aRect)
            || saRect.contains(aRect)) {
      return;
    }
    int hPos = getHorizontalPosition();
    int vPos = getVerticalPosition();
    if (aRect.x - saRect.x < 0) {
      hPos += aRect.x - saRect.x;
    } else if (aRect.x + aRect.width > saRect.x + saRect.width) {
      hPos += aRect.x + aRect.width - saRect.x - saRect.width;
    }
    if (aRect.y - saRect.y < 0) {
      vPos += aRect.y - saRect.y;
    } else if (aRect.y + aRect.height > saRect.y + saRect.height) {
      vPos += aRect.y + aRect.height - saRect.y - saRect.height;
    }
    setHorizontalPosition(hPos);
    setVerticalPosition(vPos);
  }

  /**
   * scroll to make the cell fully visible.
   *
   * @param row row index
   * @param column column index.
   */
  public void scrollCellToVisible(int row, int column) {
    int hPos = getHorizontalPosition();
    int vPos = getVerticalPosition();
    Rectangle srRect = getScrollRowsArea();
    Rectangle scRect = getScrollColumnsArea();
    Rectangle aRect = getCellRect(row, column, true);
    if (row >= getFixedRowCount() && !srRect.contains(aRect)) {
      if (aRect.y - srRect.y < 0) {
        vPos += aRect.y - srRect.y;
      } else if (aRect.y + aRect.height > srRect.y + srRect.height) {
        vPos += aRect.y + aRect.height - srRect.y - srRect.height;
      }
    }
    if (isScrollColumn(column) && !scRect.contains(aRect)) {
      if (aRect.x - scRect.x < 0) {
        hPos += aRect.x - scRect.x;
      } else if (aRect.x + aRect.width > scRect.x + scRect.width) {
        hPos += aRect.x + aRect.width - scRect.x - scRect.width;
      }
    }
    setHorizontalPosition(hPos);
    setVerticalPosition(vPos);
  }

  /**
   * Returns true if the row is visble in scroll area, otherwise false.
   *
   * @param row row index.
   * @return true if the row is visble in scroll area, otherwise false.
   */
  public boolean isRowVisible(int row) {
    boolean scrollRow = row >= getFixedRowCount();
    Rectangle cRect = getCellRect(row, 0, true);
    Rectangle srRect = getScrollRowsArea();
    if (scrollRow
            && !(cRect.y >= srRect.y && cRect.getMaxY() <= srRect.getMaxY())) {
      return false;
    }
    return true;
  }

  /**
   * Vertical scroll to make the row visible.
   *
   * @param row row index.
   */
  public void scrollRowToVisible(int row) {
    int vPos = getVerticalPosition();
    Rectangle srRect = getScrollRowsArea();
    Rectangle aRect = getCellRect(row, 0, true);
    if (row >= getFixedRowCount()
            && !(aRect.y >= srRect.y && aRect.getMaxY() <= srRect.getMaxY())) {
      if (aRect.y - srRect.y < 0) {
        vPos += aRect.y - srRect.y;
      } else if (aRect.y + aRect.height > srRect.y + srRect.height) {
        vPos += aRect.y + aRect.height - srRect.y - srRect.height;
      }
    }
    setVerticalPosition(vPos);
  }

  /**
   * Returns true if the column is visble in scroll area, otherwise false.
   *
   * @param column column index.
   * @return true if the column is visble in scroll area, otherwise false.
   */
  public boolean isColumnVisible(int column) {
    Rectangle cRect = getCellRect(0, column, true);
    Rectangle scRect = getScrollColumnsArea();
    if (isScrollColumn(column)
            && !(cRect.x >= scRect.x && cRect.getMaxX() <= scRect.getMaxX())) {
      return false;
    }
    return true;
  }

  /**
   * Horizontal scroll to make the column visible.
   *
   * @param column column index.
   */
  public void scrollColumnToVisible(int column) {
    int hPos = getHorizontalPosition();
    Rectangle scRect = getScrollColumnsArea();
    Rectangle aRect = getCellRect(0, column, true);
    if (isScrollColumn(column)
            && !(aRect.x >= scRect.x && aRect.getMaxX() <= scRect.getMaxX())) {
      if (aRect.x - scRect.x < 0) {
        hPos += aRect.x - scRect.x;
      } else if (aRect.x + aRect.width > scRect.x + scRect.width) {
        hPos += aRect.x + aRect.width - scRect.x - scRect.width;
      }
    }
    setHorizontalPosition(hPos);
  }

  /**
   * Override to scroll selection cell to visible.
   *
   * @param rowIndex row index
   * @param columnIndex column index
   * @param toggle see JTable javadoc
   * @param extend see JTable javadoc
   * @see JTable#changeSelection(int, int, boolean, boolean)
   */
  @Override
  public void changeSelection(int rowIndex, int columnIndex,
          boolean toggle, boolean extend) {
    super.changeSelection(rowIndex, columnIndex, toggle, extend);
    scrollCellToVisible(rowIndex, columnIndex);
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="row tick and selection">
  /**
   * Returns row tick model.
   *
   * @return row tick model.
   */
  public ListSelectionModel getTickerModel() {
    return tickerModel;
  }

  /**
   * Set row tick model.
   *
   * @param tickerModel row tick model.
   */
  public void setTickerModel(ListSelectionModel tickerModel) {
    if (tickerModel == null) {
      throw new IllegalArgumentException("Cannot set a null TickerModel");
    }
    ListSelectionModel oldModel = this.tickerModel;
    if (tickerModel != oldModel) {
      this.tickerModel = tickerModel;
      firePropertyChange("tickerModel", oldModel, tickerModel);
    }
  }

  /**
   * Add rows to row tick model.
   *
   * @param index0 one end of the interval.
   * @param index1 other end of the interval
   * @see ListSelectionModel#addSelectionInterval(int, int)
   */
  public void addRowTickerInterval(int index0, int index1) {
    getTickerModel().addSelectionInterval(index0, index1);
  }

  /**
   * Set tick rows to the range.
   *
   * @param index0 one end of the interval.
   * @param index1 other end of the interval
   * @see ListSelectionModel#setSelectionInterval(int, int)
   */
  public void setRowTickerInterval(int index0, int index1) {
    getTickerModel().setSelectionInterval(index0, index1);
  }

  /**
   * Remove rows tick from row tick model.
   *
   * @param index0 one end of the interval.
   * @param index1 other end of the interval
   * @see ListSelectionModel#removeSelectionInterval(int, int)
   */
  public void removeRowTickerInterval(int index0, int index1) {
    getTickerModel().removeSelectionInterval(index0, index1);
  }

  /**
   * Returns true if the row in row tick model, otherwise false.
   *
   * @param row row index.
   * @return true if the row in row tick model, otherwise false.
   */
  public boolean isRowTicked(int row) {
    return getTickerModel().isSelectedIndex(row);
  }

  /**
   * Returns tick rows array.
   *
   * @return tick rows array.
   */
  public int[] getTickedRows() {
    int iMin = getTickerModel().getMinSelectionIndex();
    int iMax = getTickerModel().getMaxSelectionIndex();

    if ((iMin == -1) || (iMax == -1)) {
      return new int[0];
    }

    int[] rvTmp = new int[1 + (iMax - iMin)];
    int n = 0;
    for (int i = iMin; i <= iMax; i++) {
      if (getTickerModel().isSelectedIndex(i)) {
        rvTmp[n++] = i;
      }
    }
    int[] rv = new int[n];
    System.arraycopy(rvTmp, 0, rv, 0, n);
    return rv;
  }

  /**
   * Make all rows in row tick model.
   */
  public void tickAll() {
    setRowTickerInterval(0, getRowCount() - 1);
  }

  /**
   * Returns true if all rows ticked, otherwise false.
   *
   * @return true if all rows ticked, otherwise false.
   */
  public boolean isAllRowsTicked() {
    return getRowCount() > 0 && getTickedRows().length == getRowCount();
  }

  /**
   * Returns true if all rows selected, otherwise false.
   *
   * @return true if all rows selected, otherwise false.
   */
  public boolean isAllRowsSelected() {
    return getRowCount() > 0 && getSelectedRows().length == getRowCount();
  }

  /**
   * Clear ticked rows.
   */
  public void clearTicker() {
    getTickerModel().clearSelection();
  }

  /**
   * Returns row selection mode.
   *
   * @return row selection mode.
   * @see ListSelectionModel#getSelectionMode()
   */
  public int getRowSelectionMode() {
    return getSelectionModel().getSelectionMode();
  }

  /**
   * Set row selection mode.
   *
   * @param rowSelectionMode row selection mode.
   * @see ListSelectionModel#setSelectionMode(int)
   */
  public void setRowSelectionMode(int rowSelectionMode) {
    getSelectionModel().setSelectionMode(rowSelectionMode);
  }

  /**
   * Set selection to one row by the rowIndex.
   *
   * @param rowIndex row index.
   */
  public void setSelectedRow(int rowIndex) {
    getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
  }
  // </editor-fold>

  //<editor-fold defaultstate="collapsed" desc="option popup menu">
  /**
   * Returns whether option popup menu is active or not. if this property is
   * false, the showOptionPopupMenu will do nothing regardless the
   * optionPopupMenu set or not. default is true;
   *
   * @return true or false
   */
  public boolean isOptionPopupActive() {
    return optionPopupActive;
  }

  /**
   * Sets whether option popup menu is active or not.
   *
   * @param optionPopupActive true or false.
   */
  public void setOptionPopupActive(boolean optionPopupActive) {
    if (optionPopupActive != this.optionPopupActive) {
      boolean old = this.optionPopupActive;
      this.optionPopupActive = optionPopupActive;
      firePropertyChange("optionPopupActive", old, optionPopupActive);
    }
  }

  /**
   * Returns option popup menu, call showOptionPopupMenu to show the option
   * popup menu.
   *
   * @return option popup menu.
   */
  public JPopupMenu getOptionPopupMenu() {
    return optionPopupMenu;
  }

  /**
   * Set option actions of table. this will cause the option popup menu to be
   * rebuild.
   *
   * @param optionPopupMenu option popup menu
   */
  public void setOptionPopupMenu(JPopupMenu optionPopupMenu) {
    JPopupMenu old = this.optionPopupMenu;
    if (old != null && old.isVisible()) {
      old.setVisible(false);
    }
    this.optionPopupMenu = optionPopupMenu;
    firePropertyChange("optionPopupMenu", old, optionPopupMenu);
  }

  /**
   * show option popup menu with specified top right point.
   *
   * @param location option popup menu location
   */
  public void showOptionPopupMenu(Point location) {
    if (optionPopupActive && optionPopupMenu != null) {
      PopupMenuListener l = getOptionPopupHandler();
      optionPopupMenu.addPopupMenuListener(l);
      optionPopupMenu.show(this, location.x, location.y);
    }
  }

  private PopupMenuListener getOptionPopupHandler() {
    if (optionPopupHandler == null) {
      optionPopupHandler = new PopupMenuListener() {
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
          fireOptionPopupEvent(0, e);
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
          fireOptionPopupEvent(1, e);
          optionPopupMenu.removePopupMenuListener(optionPopupHandler);
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
          fireOptionPopupEvent(2, e);
        }
      };
    }
    return optionPopupHandler;
  }

  private void fireOptionPopupEvent(int event, PopupMenuEvent e) {
    for (OptionPopupListener l : getOptionPopupListeners()) {
      if (event == 0) {
        l.optionPopupWillBecomeVisible(e);
      } else if (event == 1) {
        l.optionPopupWillBecomeInvisible(e);
      } else {
        l.optionPopupCanceled(e);
      }
    }
  }

  private OptionPopupListener[] getOptionPopupListeners() {
    return optionPopupListeners.getListeners(OptionPopupListener.class);
  }

  /**
   * add option popup menu listener.
   *
   * @param l listener
   */
  public void addOptionPopupListener(OptionPopupListener l) {
    optionPopupListeners.add(OptionPopupListener.class, l);
  }

  /**
   * remove option popup menu listener.
   *
   * @param l listener
   */
  public void removeOptionPopupListener(OptionPopupListener l) {
    optionPopupListeners.remove(OptionPopupListener.class, l);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="column model factory">
  /**
   * Returns columns factory for the table, if autoCreateColumnsFromModel be
   * true, the table call columns factory to create columns when model structure
   * changed. default is null.
   *
   * @return columns factory
   * @see javax.swing.JTable#getAutoCreateColumnsFromModel()
   */
  public TableColumnsFactory getColumnsFactory() {
    return columnsFactory;
  }

  /**
   * Sets columns factory for the table.
   *
   * @param columnsFactory columns factory
   */
  public void setColumnsFactory(TableColumnsFactory columnsFactory) {
    if (columnsFactory != this.columnsFactory) {
      TableColumnsFactory old = this.columnsFactory;
      this.columnsFactory = columnsFactory;
      firePropertyChange("columnsFactory", old, columnsFactory);
    }
  }

  @Override
  public void createDefaultColumnsFromModel() {
    int savedHorizPos = horizontalPosition;
    TableColumnsFactory cf = getColumnsFactory();
    if (cf != null) {
      cf.createColumnsFromModel(this);
    } else {
      super.createDefaultColumnsFromModel();
    }
    if (getTableLayoutModel() != null) {
      setHorizontalPosition(savedHorizPos);
    }
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