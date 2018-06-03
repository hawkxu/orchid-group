/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import com.xzq.osc.JocTable;
import com.xzq.osc.JocTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author zqxu
 */
public class TableLeftPane extends TableSidePane {

  private final static String uiClassID = "ocTableLeftPane";
  public static final int HEADER_ROW = -2;
  /**
   * width of row ticker column
   */
  private int tickerColumnWidth;
  /**
   * width of row selection column
   */
  private int selectionColumnWidth;
  /**
   * indicator for show row ticker column.
   */
  private boolean showTickerColumn;
  /**
   * indicator for show row selection column.
   */
  private boolean showSelectionColumn;
  private TableModelListener tableModelHandler;
  private ListSelectionListener tickerHandler;
  private ListSelectionListener selectionHandler;
  private TableCellRenderer tickerColumnRenderer;
  private TableCellRenderer selectionColumnRenderer;
  /**
   * table model
   */
  protected TableModel tableModel;
  /**
   * row ticker model.
   */
  protected ListSelectionModel tickerModel;
  /**
   * row selection model.
   */
  protected ListSelectionModel selectionModel;

  static {
    UIManager.put(uiClassID, "com.xzq.osc.table.TableLeftPaneUI");
  }

  public TableLeftPane() {
    this(false, true);
  }

  public TableLeftPane(boolean showTickerColumn,
          boolean showSelectionColumn) {
    super();
    setShowTickerColumn(showTickerColumn);
    setShowSelectionColumn(showSelectionColumn);
  }

  /**
   * initialize
   */
  @Override
  protected void initializeLocalVars() {
    tickerColumnWidth = 22;
    selectionColumnWidth = 22;
    showTickerColumn = false;
    showSelectionColumn = true;
    setBackground(Color.LIGHT_GRAY);
  }

  /**
   * install on table.
   *
   * @param table table component.
   */
  @Override
  protected void install(JocTable table) {
    registerTablePropertyListener("model");
    registerTablePropertyListener("tickerModel");
    registerTablePropertyListener("selectionModel");
    registerTablePropertyListener("verticalPosition");
    updateTableModel(table.getModel());
    updateTickerModel(table.getTickerModel());
    updateSelectionModel(table.getSelectionModel());
  }

  @Override
  protected void uninstall(JocTable table) {
    super.uninstall(table);
    updateTableModel(null);
    updateTickerModel(null);
    updateSelectionModel(null);
  }

  private void updateTableModel(TableModel model) {
    if (this.tableModel != null) {
      this.tableModel.removeTableModelListener(getTableModelHandler());
    }
    this.tableModel = model;
    if (this.tableModel != null) {
      this.tableModel.addTableModelListener(getTableModelHandler());
    }
    repaint();
  }

  /**
   * update row ticker model.
   *
   * @param tickerModel row ticker model.
   */
  protected void updateTickerModel(ListSelectionModel tickerModel) {
    if (this.tickerModel != null) {
      this.tickerModel.removeListSelectionListener(getTickerHandler());
    }
    this.tickerModel = tickerModel;
    if (this.tickerModel != null) {
      tickerModel.addListSelectionListener(getTickerHandler());
    }
    repaint();
  }

  /**
   * update row selection model.
   *
   * @param selectionModel row selection model.
   */
  protected void updateSelectionModel(ListSelectionModel selectionModel) {
    if (this.selectionModel != null) {
      this.selectionModel.removeListSelectionListener(getSelectionHandler());
    }
    this.selectionModel = selectionModel;
    if (this.selectionModel != null) {
      this.selectionModel.addListSelectionListener(getSelectionHandler());
    }
    repaint();
  }

  /**
   * Returns whether show row selection column or not, default is true.
   *
   * @return true for show row selection column and false hide.
   */
  public boolean getShowSelectionColumn() {
    return showSelectionColumn;
  }

  /**
   * Set whether show row selection column or not.
   *
   * @param showSelectionColumn true for show row selection column and false
   * hide.
   */
  public void setShowSelectionColumn(boolean showSelectionColumn) {
    if (this.showSelectionColumn != showSelectionColumn) {
      this.showSelectionColumn = showSelectionColumn;
      resizeAndRepaint();
    }
  }

  /**
   * Returns whether show row tick column or not, default is true.
   *
   * @return true for show row tick column and false hide.
   */
  public boolean getShowTickerColumn() {
    return showTickerColumn;
  }

  /**
   * Set whether show row tick column or not.
   *
   * @param showTickerColumn true for show row tick column and false hide.
   */
  public void setShowTickerColumn(boolean showTickerColumn) {
    if (this.showTickerColumn != showTickerColumn) {
      this.showTickerColumn = showTickerColumn;
      resizeAndRepaint();
    }
  }

  /**
   * Returns column count on the pane. shoud in range [0-2].
   *
   * @return column count on the pane
   */
  public int getColumnCount() {
    return (showTickerColumn ? 1 : 0) + (showSelectionColumn ? 1 : 0);
  }

  /**
   * Returns width of row selection column.
   *
   * @return width of row selection column.
   */
  public int getSelectionColumnWidth() {
    return selectionColumnWidth;
  }

  /**
   * Set width of row selection column.
   *
   * @param selectionColumnWidth width of row selection column.
   */
  public void setSelectionColumnWidth(int selectionColumnWidth) {
    if (this.selectionColumnWidth != selectionColumnWidth) {
      this.selectionColumnWidth = selectionColumnWidth;
      resizeAndRepaint();
    }
  }

  /**
   * Returns width of row tick column.
   *
   * @return width of row tick column.
   */
  public int getTickerColumnWidth() {
    return tickerColumnWidth;
  }

  /**
   * Set width of row tick column.
   *
   * @param tickerColumnWidth width of row tick column.
   */
  public void setTickerColumnWidth(int tickerColumnWidth) {
    if (this.tickerColumnWidth != tickerColumnWidth) {
      this.tickerColumnWidth = tickerColumnWidth;
      resizeAndRepaint();
    }
  }

  /**
   * Returns width of the column on the pane.
   *
   * @param column column index.
   * @return width of the column.
   */
  public int getColumnWidth(int column) {
    if (column == 0 && getShowTickerColumn()) {
      return getTickerColumnWidth();
    } else {
      return getSelectionColumnWidth();
    }
  }

  /**
   * Return area of the cell on the pane.
   *
   * @param row row index, row index on the header is HEADER_ROW, and row index
   * for table data from 0.
   * @param column column index.
   * @return area of the cell on the pane.
   */
  public Rectangle getCellRect(int row, int column) {
    Rectangle rect = new Rectangle();
    if (column == 0 && getShowTickerColumn()) {
      rect.width = getTickerColumnWidth();
    } else {
      if (getShowTickerColumn()) {
        rect.x = getTickerColumnWidth();
      }
      rect.width = getSelectionColumnWidth();
    }
    if (row == HEADER_ROW) {
      rect.height = getTableHeadHeight();
    } else {
      Rectangle rRect = getTable().getCellRect(row, 0, true);
      rect.y = rRect.y - getBounds().y;
      rect.height = rRect.height;
    }
    return rect;
  }

  /**
   * Returns head pane height of associated table.
   *
   * @return head pane height of associated table.
   */
  protected int getTableHeadHeight() {
    TableSidePane head = getTable().getHeadPane();
    return head == null ? 0 : head.getHeight();
  }

  /**
   * Returns column index on the point.
   *
   * @param point point.
   * @return column index on the point.
   */
  public int columnAtPoint(Point point) {
    if (point.x < 0 || point.x >= getWidth()) {
      return -1;
    }
    if (getShowTickerColumn() && point.x >= getTickerColumnWidth()) {
      return 1;
    } else {
      return 0;
    }
  }

  /**
   * Returns row index on the point.
   *
   * @param point point
   * @return row index on the point.
   */
  public int rowAtPoint(Point point) {
    if (point.y < 0) {
      return -1;
    } else if (point.y < getTableHeadHeight()) {
      return HEADER_ROW;
    } else {
      point = new Point(point.x, point.y + getY());
      return getTable().rowAtEntireTable(point);
    }
  }

  /**
   * Return cell renderer for the row selection column.
   *
   * @return cell renderer for the row selection column.
   */
  public TableCellRenderer getSelectionColumnRenderer() {
    if (selectionColumnRenderer == null) {
      selectionColumnRenderer = createDefaultSelectionRenderer();
    }
    return selectionColumnRenderer;
  }

  /**
   * Set cell renderer for the row selection column.
   *
   * @param selectionColumnRenderer cell renderer for the row selection column.
   */
  public void setSelectionColumnRenderer(
          TableCellRenderer selectionColumnRenderer) {
    this.selectionColumnRenderer = selectionColumnRenderer;
    repaint();
  }

  /**
   * Returns cell renderer for the row tick column.
   *
   * @return cell renderer for the row tick column.
   */
  public TableCellRenderer getTickerColumnRenderer() {
    if (tickerColumnRenderer == null) {
      tickerColumnRenderer = createDefaultTickerRenderer();
    }
    return tickerColumnRenderer;
  }

  /**
   * Set cell renderer for the row tick column.
   *
   * @param tickerColumnRenderer cell renderer for the row tick column.
   */
  public void setTickerColumnRenderer(
          TableCellRenderer tickerColumnRenderer) {
    this.tickerColumnRenderer = tickerColumnRenderer;
    repaint();
  }

  /**
   * Returns cell renderer component for the cell on the pane.
   *
   * @param row row index
   * @param column column index
   * @return cell renderer component for the cell on the pane.
   */
  public Component prepareCellRenderer(int row, int column) {
    boolean selected;
    TableCellRenderer renderer;
    if (column == 0 && getShowTickerColumn()) {
      if (row == HEADER_ROW) {
        selected = getTable().isAllRowsTicked();
      } else {
        selected = getTable().isRowTicked(row);
      }
      renderer = getTickerColumnRenderer();
    } else {
      if (row == HEADER_ROW) {
        selected = false;
      } else {
        selected = getTable().getRowSelectionAllowed()
                && getTable().isRowSelected(row);
      }
      renderer = getSelectionColumnRenderer();
    }
    return renderer.getTableCellRendererComponent(getTable(), selected,
            selected, false, row, column);
  }

  /**
   * Create default row selection cell renderer.
   *
   * @return row selection cell renderer.
   */
  protected TableCellRenderer createDefaultSelectionRenderer() {
    return new SelectionCellRenderer();
  }

  /**
   * Create default row tick cell renderer.
   *
   * @return row tick cell renderer.
   */
  protected TableCellRenderer createDefaultTickerRenderer() {
    return new TickerCellRenderer();
  }

  /**
   * Returns area of fixed rows.
   *
   * @return area of fixed rows.
   */
  public Rectangle getFixedRowsArea() {
    Rectangle rect = getBounds();
    rect.x = rect.y = 0;
    Rectangle frRect = getTable().getFixedRowsArea();
    rect.height = getTableHeadHeight() + frRect.height;
    return rect;
  }

  /**
   * Returns area of scrollable rows.
   *
   * @return area of scrollable rows.
   */
  public Rectangle getScrollRowsArea() {
    Rectangle rect = getBounds();
    rect.x = rect.y = 0;
    Rectangle frRect = getFixedRowsArea();
    rect.y += frRect.height;
    rect.height -= frRect.height;
    return rect;
  }

  /**
   * repaint scrollable rows area.
   */
  public void repaintScrollRowsArea() {
    repaint(getScrollRowsArea());
  }

  /**
   * repaint row selection column from row0 to row1.
   *
   * @param row0 one row .
   * @param row1 another row.
   */
  protected void repaintSelection(int row0, int row1) {
    if (getShowSelectionColumn()) {
      int column = getShowTickerColumn() ? 1 : 0;
      Rectangle rect0 = getCellRect(row0, column);
      Rectangle rect1 = getCellRect(row1, column);
      rect0.height = rect1.y + rect1.height - rect0.y;
      repaint(rect0);
      repaint(getCellRect(HEADER_ROW, column));
    }
  }

  /**
   * repaint row tick column from row0 to row1.
   *
   * @param row0 one row.
   * @param row1 another row.
   */
  protected void repaintTicker(int row0, int row1) {
    if (getShowTickerColumn()) {
      int column = 0;
      Rectangle rect0 = getCellRect(row0, column);
      Rectangle rect1 = getCellRect(row1, column);
      rect0.height = rect1.y + rect1.height - rect0.y;
      repaint(rect0);
      repaint(getCellRect(HEADER_ROW, column));
    }
  }

  /**
   * Returns height of the row.
   *
   * @param row row index.
   * @return height of the row.
   */
  public int getRowHeight(int row) {
    if (row == HEADER_ROW) {
      return getTableHeadHeight();
    } else {
      return getTable().getRowHeight(row);
    }
  }

  private TableModelListener getTableModelHandler() {
    if (tableModelHandler == null) {
      tableModelHandler = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
          if (e == null || e.getLastRow() == Integer.MAX_VALUE
                  || e.getFirstRow() == TableModelEvent.HEADER_ROW
                  || e.getType() != TableModelEvent.UPDATE) {
            repaint();
          }
        }
      };
    }
    return tableModelHandler;
  }

  /**
   * Returns row selection listener.
   *
   * @return row selection listener.
   */
  protected ListSelectionListener getSelectionHandler() {
    if (selectionHandler == null) {
      selectionHandler = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            repaintSelection(e.getFirstIndex(), e.getLastIndex());
          }
        }
      };
    }
    return selectionHandler;
  }

  /**
   * Returns row tick listener
   *
   * @return row tick listener
   */
  protected ListSelectionListener getTickerHandler() {
    if (tickerHandler == null) {
      tickerHandler = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            repaintTicker(e.getFirstIndex(), e.getLastIndex());
          }
        }
      };
    }
    return tickerHandler;
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
    if (propertyName.equals("model")) {
      updateTableModel(table.getModel());
    } else if (propertyName.equals("tickerModel")) {
      updateTickerModel(table.getTickerModel());
    } else if (propertyName.equals("selectionModel")) {
      updateSelectionModel(table.getSelectionModel());
    } else if (propertyName.equals("verticalPosition")) {
      repaintScrollRowsArea();
    }
  }

  private class SelectionCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
      if (isSelected) {
        setBackground(getTable().getSelectionBackground());
        setBorder(new CellRendererBorder(table.getGridColor(), Color.WHITE));
      } else {
        setBackground(TableLeftPane.this.getBackground());
        setBorder(new CellRendererBorder(Color.WHITE, table.getGridColor()));
      }
      return this;
    }
  }

  private class TickerCellRenderer extends JocTableCellRenderer {

    public TickerCellRenderer() {
      super(CheckRenderer.class);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
      CheckRenderer renderer = (CheckRenderer) getRendererComponent();
      renderer.setBackground(TableLeftPane.this.getBackground());
      renderer.setSelected(isSelected);
      renderer.setBorder(
              new CellRendererBorder(Color.WHITE, table.getGridColor()));
      return renderer;
    }
  }

  private class CellRendererBorder implements Border {

    private Color leftTop;
    private Color rightBottom;

    public CellRendererBorder(Color leftTop, Color rightBottom) {
      this.leftTop = leftTop;
      this.rightBottom = rightBottom;
    }

    @Override
    public Insets getBorderInsets(Component c) {
      return new Insets(1, 1, 1, 1);
    }

    @Override
    public boolean isBorderOpaque() {
      return true;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y,
            int width, int height) {
      g.translate(x, y);
      g.setColor(leftTop);
      g.drawLine(0, 0, 0, height - 1);
      g.drawLine(0, 0, width - 1, 0);
      g.setColor(rightBottom);
      g.drawLine(width - 1, 0, width - 1, height - 1);
      g.drawLine(0, height - 1, width - 1, height - 1);
      g.translate(-x, -y);
    }
  }
}
