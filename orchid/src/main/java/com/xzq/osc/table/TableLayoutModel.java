/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import com.xzq.osc.OrchidUtils;
import com.xzq.osc.plaf.OrchidDefaults;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;

/**
 * TableLayoutModel provide table layout data, include fixed rows and columns
 * and cell color. <p> cell color (background and foreground) determined by the
 * sequence of: </p><ul><li>CellLayoutListener</li><li>Color of Column</li>
 * <li>Color of Row</li></ul>
 *
 * @author zqxu
 */
public class TableLayoutModel {

  private static final Integer ODD_INDEX = Integer.valueOf(-1);
  private static final Integer EVEN_INDEX = Integer.valueOf(-2);
  protected EventListenerList listenerList;
  /**
   * fixed row count
   */
  protected int fixedRowCount;
  /**
   * fixed column count
   */
  protected int fixedColumnCount;
  private Map<Integer, Color> rowBackgroundMap;
  private Map<Integer, Color> rowForegroundMap;
  private Map<Integer, Color> colBackgroundMap;
  private Map<Integer, Color> colForegroundMap;
  private Color readonlyBackground;
  private Color readonlyForeground;

  /**
   * Constructor
   */
  public TableLayoutModel() {
    listenerList = new EventListenerList();
    rowBackgroundMap = new HashMap<Integer, Color>();
    rowForegroundMap = new HashMap<Integer, Color>();
    colBackgroundMap = new HashMap<Integer, Color>();
    colForegroundMap = new HashMap<Integer, Color>();
  }

  // <editor-fold defaultstate="collapsed" desc="fixed rows and columns">
  /**
   * Returns fixed row count. default is 0.
   *
   * @return fixed row count
   * @see #setFixedRowCount(int)
   */
  public int getFixedRowCount() {
    return fixedRowCount;
  }

  /**
   * Set fixed row count.
   *
   * @param fixedRowCount fixed row count.
   */
  public void setFixedRowCount(int fixedRowCount) {
    if (this.fixedRowCount != fixedRowCount) {
      this.fixedRowCount = fixedRowCount;
      fireFixedRowCountChanged();
    }
  }

  /**
   * Returns fixed column count.
   *
   * @return fixed column count.
   * @see #setFixedColumnCount(int)
   */
  public int getFixedColumnCount() {
    return fixedColumnCount;
  }

  /**
   * Set fixed column count.
   *
   * @param fixedColumnCount fixed column count.
   */
  public void setFixedColumnCount(int fixedColumnCount) {
    if (this.fixedColumnCount != fixedColumnCount) {
      this.fixedColumnCount = fixedColumnCount;
      fireFixedColumnCountChanged();
    }
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="row color getter/setter">
  /**
   * Returns background color for odd row, default is null.
   *
   * @return background color for odd row.
   */
  public Color getOddRowBackground() {
    return rowBackgroundMap.get(ODD_INDEX);
  }

  /**
   * Sets background color for odd row.
   *
   * @param oddRowBackground background color for odd row.
   */
  public void setOddRowBackground(Color oddRowBackground) {
    Color old = getOddRowBackground();
    if (!OrchidUtils.equals(old, oddRowBackground)) {
      rowBackgroundMap.put(ODD_INDEX, oddRowBackground);
      fireCellLayoutChanged(-1, -1);
    }
  }

  /**
   * Returns foreground color for odd row. default is null.
   *
   * @return foreground color for odd row.
   */
  public Color getOddRowForeground() {
    return rowForegroundMap.get(ODD_INDEX);
  }

  /**
   * Set foreground color for odd row.
   *
   * @param oddRowForeground foreground color for odd row.
   */
  public void setOddRowForeground(Color oddRowForeground) {
    Color old = getOddRowForeground();
    if (!OrchidUtils.equals(old, oddRowForeground)) {
      rowForegroundMap.put(ODD_INDEX, oddRowForeground);
      fireCellLayoutChanged(-1, -1);
    }
  }

  /**
   * Returns background color for even row, default is null.
   *
   * @return background color for even row.
   */
  public Color getEvenRowBackground() {
    return rowBackgroundMap.get(EVEN_INDEX);
  }

  /**
   * Set background color for even row.
   *
   * @param evenRowBackground background color for even row.
   */
  public void setEvenRowBackground(Color evenRowBackground) {
    Color old = getEvenRowBackground();
    if (!OrchidUtils.equals(old, evenRowBackground)) {
      rowBackgroundMap.put(EVEN_INDEX, evenRowBackground);
      fireCellLayoutChanged(-1, -1);
    }
  }

  /**
   * Returns foreground color for even row. default is null.
   *
   * @return foreground color for even row.
   */
  public Color getEvenRowForeground() {
    return rowForegroundMap.get(EVEN_INDEX);
  }

  /**
   * Set foreground color for even row.
   *
   * @param evenRowForeground foreground color for even row.
   */
  public void setEvenRowForeground(Color evenRowForeground) {
    Color old = getEvenRowForeground();
    if (!OrchidUtils.equals(old, evenRowForeground)) {
      rowForegroundMap.put(EVEN_INDEX, evenRowForeground);
      fireCellLayoutChanged(-1, -1);
    }
  }

  /**
   * Returns background color for the row, this value has high priority than
   * color for odd/even row. default is null.
   *
   * @param row the row index.
   * @return background color for the row
   */
  public Color getRowBackground(int row) {
    if (row < 0) {
      throw new IllegalArgumentException("row can not less than 0.");
    }
    return rowBackgroundMap.get(row);
  }

  /**
   * Set background color for the row.
   *
   * @param row the row index.
   * @param rowBackground background color for the row
   */
  public void setRowBackground(int row, Color rowBackground) {
    if (row < 0) {
      throw new IllegalArgumentException("row can not less than 0.");
    }
    Color old = getRowBackground(row);
    if (!OrchidUtils.equals(old, rowBackground)) {
      rowBackgroundMap.put(row, rowBackground);
      fireCellLayoutChanged(row, -1);
    }
  }

  /**
   * Returns foreground color for the row, this value has high priority than
   * color for odd/even row. default is null.
   *
   * @param row the row index
   * @return foreground color for the row
   */
  public Color getRowForeground(int row) {
    if (row < 0) {
      throw new IllegalArgumentException("row can not less than 0.");
    }
    return rowForegroundMap.get(row);
  }

  /**
   * Set foreground color for the row.
   *
   * @param row the row index
   * @param rowForeground foreground color for the row
   */
  public void setRowForeground(int row, Color rowForeground) {
    if (row < 0) {
      throw new IllegalArgumentException("row can not less than 0.");
    }
    Color old = getRowForeground(row);
    if (!OrchidUtils.equals(old, rowForeground)) {
      rowBackgroundMap.put(row, rowForeground);
      fireCellLayoutChanged(row, -1);
    }
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="column color getter/setter">
  /**
   * Returns background color for odd column, default is null.
   *
   * @return background color for odd column.
   */
  public Color getOddColumnBackground() {
    return colBackgroundMap.get(ODD_INDEX);
  }

  /**
   * Sets background color for odd column.
   *
   * @param oddColumnBackground background color for odd column.
   */
  public void setOddColumnBackground(Color oddColumnBackground) {
    Color old = getOddColumnBackground();
    if (!OrchidUtils.equals(old, oddColumnBackground)) {
      colBackgroundMap.put(ODD_INDEX, oddColumnBackground);
      fireCellLayoutChanged(-1, -1);
    }
  }

  /**
   * Returns foreground color for odd column. default is null.
   *
   * @return foreground color for odd column.
   */
  public Color getOddColumnForeground() {
    return colForegroundMap.get(ODD_INDEX);
  }

  /**
   * Set foreground color for odd column.
   *
   * @param oddColumnForeground foreground color for odd column.
   */
  public void setOddColumnForeground(Color oddColumnForeground) {
    Color old = getOddColumnForeground();
    if (!OrchidUtils.equals(old, oddColumnForeground)) {
      colForegroundMap.put(ODD_INDEX, oddColumnForeground);
      fireCellLayoutChanged(-1, -1);
    }
  }

  /**
   * Returns background color for even column, default is null.
   *
   * @return background color for even column.
   */
  public Color getEvenColumnBackground() {
    return colBackgroundMap.get(EVEN_INDEX);
  }

  /**
   * Set background color for even column.
   *
   * @param evenColumnBackground background color for even column.
   */
  public void setEvenColumnBackground(Color evenColumnBackground) {
    Color old = getEvenColumnBackground();
    if (!OrchidUtils.equals(old, evenColumnBackground)) {
      colBackgroundMap.put(EVEN_INDEX, evenColumnBackground);
      fireCellLayoutChanged(-1, -1);
    }
  }

  /**
   * Returns foreground color for even column. default is null.
   *
   * @return foreground color for even column.
   */
  public Color getEvenColumnForeground() {
    return colForegroundMap.get(EVEN_INDEX);
  }

  /**
   * Set foreground color for even column.
   *
   * @param evenColumnForeground foreground color for even column.
   */
  public void setEvenColumnForeground(Color evenColumnForeground) {
    Color old = getEvenColumnForeground();
    if (!OrchidUtils.equals(old, evenColumnForeground)) {
      colForegroundMap.put(EVEN_INDEX, evenColumnForeground);
      fireCellLayoutChanged(-1, -1);
    }
  }

  /**
   * Returns background color for the column, this value has high priority than
   * color for odd/even column. default is null.
   *
   * @param column the column index
   * @return background color for the column
   */
  public Color getColumnBackground(int column) {
    if (column < 0) {
      throw new IllegalArgumentException("row can not less than 0.");
    }
    return colBackgroundMap.get(column);
  }

  /**
   * Set background color for the column
   *
   * @param column the column index
   * @param columnBackground background color for the column
   */
  public void setColumnBackground(int column, Color columnBackground) {
    if (column < 0) {
      throw new IllegalArgumentException("row can not less than 0.");
    }
    Color old = getColumnBackground(column);
    if (!OrchidUtils.equals(old, columnBackground)) {
      colBackgroundMap.put(column, columnBackground);
      fireCellLayoutChanged(-1, column);
    }
  }

  /**
   * Returns foreground color for the column, this value has high priority than
   * color for odd/even column. default is null.
   *
   * @param column the column index
   * @return foreground color for the column
   */
  public Color getColumnForeground(int column) {
    if (column < 0) {
      throw new IllegalArgumentException("row can not less than 0.");
    }
    return colForegroundMap.get(column);
  }

  /**
   * Set foreground color for the column.
   *
   * @param column the column index
   * @param columnForeground foreground color for the column
   */
  public void setColumnForeground(int column, Color columnForeground) {
    if (column < 0) {
      throw new IllegalArgumentException("row can not less than 0.");
    }
    Color old = getColumnForeground(column);
    if (!OrchidUtils.equals(old, columnForeground)) {
      colForegroundMap.put(column, columnForeground);
      fireCellLayoutChanged(-1, column);
    }
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="cell color getter/setter">
  /**
   * Returns background color for readonly cell. if null, then use value of the
   * key OrchidDefaults.TABLE_READONLY_BACKGROUND in UIManager.
   *
   * @return background color for readonly cell
   */
  public Color getReadonlyBackground() {
    if (readonlyBackground != null) {
      return readonlyBackground;
    }
    return UIManager.getColor(OrchidDefaults.TABLE_READONLY_BACKGROUND);
  }

  /**
   * Sets background color for readonly cell, null for no color specified.
   *
   * @param readonlyBackground background color for readonly cell
   */
  public void setReadonlyBackground(Color readonlyBackground) {
    Color old = this.readonlyBackground;
    if (!OrchidUtils.equals(old, readonlyBackground)) {
      this.readonlyBackground = readonlyBackground;
      fireCellLayoutChanged(-1, -1);
    }
  }

  /**
   * Returns foreground color for readonly cell. if null, then use value of the
   * key OrchidDefaults.TABLE_READONLY_FOREGROUND in UIManager.
   *
   * @return foreground color for readonly cell
   */
  public Color getReadonlyForeground() {
    if (readonlyForeground != null) {
      return readonlyForeground;
    }
    return UIManager.getColor(OrchidDefaults.TABLE_READONLY_FOREGROUND);
  }

  /**
   * Sets foreground color for readonly cell, null for no color specified.
   *
   * @param readonlyForeground foreground color for readonly cell
   */
  public void setReadonlyForeground(Color readonlyForeground) {
    Color old = this.readonlyForeground;
    if (!OrchidUtils.equals(old, readonlyForeground)) {
      this.readonlyForeground = readonlyForeground;
      fireCellLayoutChanged(-1, -1);
    }
  }

  /**
   * Returns cell background color, determined by the sequence of: <ul>
   * <li>CellLayoutListener</li> <li>background color for the column</li>
   * <li>background color for odd/even column</li> <li>background color for the
   * row</li> <li>background color for odd/even row</li><li>background for
   * readonly cell.</li></ul>
   *
   * @param table table
   * @param row row index
   * @param column column index
   * @return cell background color.
   */
  public Color getCellBackground(JTable table, int row, int column) {
    Color color = getFromListener(table, row, column, true);
    if (color == null) {
      color = getBackgroundFromColumn(column);
    }
    if (color == null) {
      color = getBackgroundFromRow(row);
    }
    if (color == null && !table.isCellEditable(row, column)) {
      color = getReadonlyBackground();
    }
    return color;
  }

  /**
   * Returns cell foreground color, determined by the sequence of: <ul>
   * <li>CellLayoutListener</li> <li>foreground color for the column</li>
   * <li>foreground color for odd/even column</li> <li>foreground color for the
   * row</li> <li>foreground color for odd/even row</li><li>foreground for
   * readonly cell.</li></ul>
   *
   * @param table table
   * @param row row index
   * @param column column index
   * @return cell foreground color.
   */
  public Color getCellForeground(JTable table, int row, int column) {
    Color color = getFromListener(table, row, column, false);
    if (color == null) {
      color = getForegroundFromColumn(column);
    }
    if (color == null) {
      color = getForegroundFromRow(row);
    }
    if (color == null && !table.isCellEditable(row, column)) {
      color = getReadonlyForeground();
    }
    return color;
  }

  private Color getFromListener(JTable table, int row, int column,
          boolean background) {
    CellLayoutEvent evt = new CellLayoutEvent(table, row, column);
    if (background) {
      return fireGetCellBackground(evt);
    } else {
      return fireGetCellForeground(evt);
    }
  }

  private Color getBackgroundFromColumn(int column) {
    Color color = getColumnBackground(column);
    if (color != null) {
      return color;
    } else if (column % 2 == 0) {
      return getOddColumnBackground();
    } else {
      return getEvenColumnBackground();
    }
  }

  private Color getBackgroundFromRow(int row) {
    Color color = getRowBackground(row);
    if (color != null) {
      return color;
    } else if (row % 2 == 0) {
      return getOddRowBackground();
    } else {
      return getEvenRowBackground();
    }
  }

  private Color getForegroundFromColumn(int column) {
    Color color = getColumnForeground(column);
    if (color != null) {
      return color;
    } else if (column % 2 == 0) {
      return getOddColumnForeground();
    } else {
      return getEvenColumnForeground();
    }
  }

  private Color getForegroundFromRow(int row) {
    Color color = getRowForeground(row);
    if (color != null) {
      return color;
    } else if (row % 2 == 0) {
      return getOddRowForeground();
    } else {
      return getEvenRowForeground();
    }
  }
  //</editor-fold>

  // <editor-fold defaultstate="collapsed" desc="cell layout event">
  /**
   * fire cell layout change event.
   *
   * @param row row index
   * @param column column index.
   */
  protected void fireCellLayoutChanged(int row, int column) {
    TableLayoutModelListener[] list = listenerList.getListeners(
            TableLayoutModelListener.class);
    for (TableLayoutModelListener l : list) {
      l.cellLayoutChanged(row, column);
    }
  }

  /**
   * fire fixed row count change event.
   */
  protected void fireFixedRowCountChanged() {
    TableLayoutModelListener[] list = listenerList.getListeners(
            TableLayoutModelListener.class);
    for (TableLayoutModelListener l : list) {
      l.fixedRowCountChanged();
    }
  }

  /**
   * fire fixed column count change event.
   */
  protected void fireFixedColumnCountChanged() {
    TableLayoutModelListener[] list = listenerList.getListeners(
            TableLayoutModelListener.class);
    for (TableLayoutModelListener l : list) {
      l.fixedColumnCountChanged();
    }
  }

  /**
   * add cell layout listener.
   *
   * @param l cell layout listener.
   */
  public void addTableLayoutModelListener(TableLayoutModelListener l) {
    listenerList.add(TableLayoutModelListener.class, l);
  }

  /**
   * remove cell layout listener.
   *
   * @param l cell layout listener.
   */
  public void removeTableLayoutModelListener(TableLayoutModelListener l) {
    listenerList.remove(TableLayoutModelListener.class, l);
  }

  /**
   *
   * @param evt
   * @return
   */
  protected Color fireGetCellBackground(CellLayoutEvent evt) {
    Color color = null;
    CellLayoutListener[] list = listenerList.getListeners(
            CellLayoutListener.class);
    for (CellLayoutListener l : list) {
      color = l.getCellBackground(evt);
      if (evt.isConsumed()) {
        break;
      }
    }
    return color;
  }

  /**
   *
   * @param evt
   * @return
   */
  protected Color fireGetCellForeground(CellLayoutEvent evt) {
    Color color = null;
    CellLayoutListener[] list = listenerList.getListeners(
            CellLayoutListener.class);
    for (CellLayoutListener l : list) {
      color = l.getCellForeground(evt);
      if (evt.isConsumed()) {
        break;
      }
    }
    return color;
  }

  /**
   *
   * @param l
   */
  public void addCellLayoutListener(CellLayoutListener l) {
    listenerList.add(CellLayoutListener.class, l);
    fireCellLayoutChanged(-1, -1);
  }

  /**
   *
   * @param l
   */
  public void removeCellLayoutListener(CellLayoutListener l) {
    listenerList.remove(CellLayoutListener.class, l);
    fireCellLayoutChanged(-1, -1);
  }
  // </editor-fold>
}
