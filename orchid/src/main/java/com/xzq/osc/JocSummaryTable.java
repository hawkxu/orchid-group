/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.summarytable.DefaultSummaryRowRenderer;
import com.xzq.osc.summarytable.SummaryTableSorter;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author zqxu
 */
public class JocSummaryTable extends JocTable {

  private TableCellRenderer summaryRowRenderer;

  /**
   *
   */
  public JocSummaryTable() {
    super();
  }

  /**
   *
   * @param dm
   */
  public JocSummaryTable(TableModel dm) {
    super(dm);
  }

  /**
   *
   * @param dm
   * @param cm
   */
  public JocSummaryTable(TableModel dm, TableColumnModel cm) {
    super(dm, cm);
  }

  /**
   *
   * @param dm
   * @param cm
   * @param sm
   */
  public JocSummaryTable(TableModel dm, TableColumnModel cm,
          ListSelectionModel sm) {
    super(dm, cm, sm);
  }

  /**
   *
   * @param numRows
   * @param numColumns
   */
  public JocSummaryTable(int numRows, int numColumns) {
    super(numRows, numColumns);
  }

  /**
   *
   * @param rowData
   * @param columnNames
   */
  public JocSummaryTable(Object[][] rowData, Object[] columnNames) {
    super(rowData, columnNames);
  }

  /**
   *
   */
  @Override
  protected void initializeLocalVars() {
    super.initializeLocalVars();
    setSummaryRowRenderer(new DefaultSummaryRowRenderer());
    setSummarySorter(new SummaryTableSorter<TableModel>(getModel()));
  }

  /**
   * JocSummaryTable always use SummaryTableSorter as row sorter, to prevent
   * JTable create TableSorter, this method was deprecated. do nothing.
   *
   * @param autoCreateRowSorter not use
   * @deprecated to prevent JTable create TableSorter
   */
  @Deprecated
  @Override
  public void setAutoCreateRowSorter(boolean autoCreateRowSorter) {
  }

  /**
   * JocSummaryTable must use SummaryTableSorter, please use setSummarySorter
   * instead.
   *
   * @param sorter row sorter must be SummaryTableSorter
   * @throws IllegalArgumentException the sorter not instance of
   * SummaryTableSorter
   */
  @Deprecated
  @Override
  public void setRowSorter(RowSorter<? extends TableModel> sorter) {
    if (sorter instanceof SummaryTableSorter) {
      setSummarySorter((SummaryTableSorter<? extends TableModel>) sorter);
    } else {
      throw new IllegalArgumentException("only accept SummaryTableSorter.");
    }
  }

  /**
   *
   * @return
   */
  @SuppressWarnings("unchecked")
  public SummaryTableSorter<? extends TableModel> getSummarySorter() {
    return (SummaryTableSorter<?>) super.getRowSorter();
  }

  /**
   * Set summary sorter to table.
   *
   * @param sorter
   * @throws IllegalArgumentException if sorter is null.
   */
  @SuppressWarnings("unchecked")
  public void setSummarySorter(SummaryTableSorter<? extends TableModel> sorter) {
    if (sorter == null) {
      throw new IllegalArgumentException("Can not set null sorter!");
    }
    super.setRowSorter(sorter);
  }

  /**
   *
   * @param dataModel
   */
  @SuppressWarnings("unchecked")
  @Override
  public void setModel(TableModel dataModel) {
    super.setModel(dataModel);
    if (getRowSorter() != null) {
      ((SummaryTableSorter) getSummarySorter()).setModel(dataModel);
    }
  }

  /**
   *
   * @param row
   * @return
   */
  public boolean isSummaryRow(int row) {
    return getSummarySorter().isSummaryRow(row);
  }

  /**
   *
   * @param row
   * @param column
   * @return
   */
  @Override
  public boolean isCellEditable(int row, int column) {
    column = convertColumnIndexToModel(column);
    return isEditable() && getSummarySorter().isCellEditable(row, column);
  }

  /**
   *
   * @param row
   * @param column
   * @return
   */
  @Override
  public Object getValueAt(int row, int column) {
    column = convertColumnIndexToModel(column);
    return getSummarySorter().getValueAt(row, column);
  }

  /**
   *
   * @param aValue
   * @param row
   * @param column
   */
  @Override
  public void setValueAt(Object aValue, int row, int column) {
    column = convertColumnIndexToModel(column);
    getSummarySorter().setValueAt(row, column, aValue);
  }

  /**
   * Returns cell renderer for summary row.
   *
   * @return
   */
  public TableCellRenderer getSummaryRowRenderer() {
    return summaryRowRenderer;
  }

  /**
   * Sets cell renderer for summary row.
   *
   * @param summaryRowRenderer
   */
  public void setSummaryRowRenderer(TableCellRenderer summaryRowRenderer) {
    this.summaryRowRenderer = summaryRowRenderer;
  }

  /**
   *
   * @param row
   * @param column
   * @return
   */
  @Override
  public TableCellRenderer getCellRenderer(int row, int column) {
    TableCellRenderer renderer = null;
    if (isSummaryRow(row)) {
      renderer = getSummaryRowRenderer();
    }
    if (renderer != null) {
      return renderer;
    }
    return super.getCellRenderer(row, column);
  }

  @Override
  public void tableChanged(TableModelEvent e) {
    super.tableChanged(e);
    SummaryTableSorter<? extends TableModel> sorter = getSummarySorter();
    if (e.getType() == TableModelEvent.UPDATE
            && sorter != null && sorter.hasSummarizedColumn()) {
      if (e.getColumn() != TableModelEvent.ALL_COLUMNS) {
        int modelColumn = convertColumnIndexToModel(e.getColumn());
        if (!sorter.isColumnSummarized(modelColumn)
                && !sorter.isSubtotalKeyColumn(modelColumn)) {
          return;
        }
      }
      repaint(getRendererBounds());
    }
  }
}