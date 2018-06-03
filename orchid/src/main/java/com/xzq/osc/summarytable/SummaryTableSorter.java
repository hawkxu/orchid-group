/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.summarytable;

import com.xzq.osc.OrchidUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author zqxu
 */
public class SummaryTableSorter<M extends TableModel> extends TableRowSorter<M> {

  private List<Integer> summaryColumns;
  private boolean summaryCalculated;
  private boolean hideLastSummaryRow;
  private int summaryRowCount;
  private Row[] viewToModel;
  private int[] modelToView;
  protected EventListenerList listenerList;
  private boolean showAllKeysInSubtotal;

  /**
   *
   */
  public SummaryTableSorter() {
    this(null);
  }

  /**
   *
   * @param model
   */
  public SummaryTableSorter(M model) {
    super(model);
    setSortsOnUpdates(true);
    listenerList = new EventListenerList();
    summaryColumns = new ArrayList<Integer>();
  }

  @Override
  public void setModel(M model) {
    super.setModel(model);
    //calculate not call by super method while initialize
    //call it directly.
    calculateSummaryIfNeed();
  }

  /**
   *
   * @return
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<? extends SummarySortKey> getSortKeys() {
    return (List<? extends SummarySortKey>) super.getSortKeys();
  }

  /**
   * Translate sortKeys to list of SummarySortKey and transfer to
   * super.setSortKeys.
   *
   * @param sortKeys sortKeys
   * @see TableRowSorter#setSortKeys(java.util.List)
   */
  @Override
  public void setSortKeys(List<? extends SortKey> sortKeys) {
    List<SummarySortKey> summarySortKeys;
    summarySortKeys = new ArrayList<SummarySortKey>();
    if (sortKeys != null) {
      for (SortKey key : sortKeys) {
        summarySortKeys.add(convertSortKey(key));
      }
    }
    summaryCalculated = false;
    super.setSortKeys(summarySortKeys);
  }

  private SummarySortKey convertSortKey(SortKey sortKey) {
    if (sortKey instanceof SummarySortKey) {
      return (SummarySortKey) sortKey;
    } else {
      return new SummarySortKey(sortKey.getColumn(),
              sortKey.getSortOrder(), false);
    }
  }

  /**
   * Returns true if the column is a sort key column with sub-total caculation.
   *
   * @param column the column, in terms of the model.
   * @return true or false.
   */
  public boolean isSubtotalKeyColumn(int column) {
    SummarySortKey sortKey = findSortKey(column);
    return sortKey != null && sortKey.isCalculateSubtotal();
  }

  /**
   *
   *
   * @param column the column index, in terms of model.
   */
  @Override
  public void toggleSortOrder(int column) {
    checkColumn(column);
    if (isSortable(column)) {
      toggle(column, true, false);
    }
  }

  /**
   *
   * @param column the column index, in terms of model.
   */
  public void toggleCalculateSubtotal(int column) {
    checkColumn(column);
    if (isSortable(column)) {
      toggle(column, false, true);
    }
  }

  private void checkColumn(int column) {
    if (column < 0 || column >= getModelWrapper().getColumnCount()) {
      throw new IndexOutOfBoundsException(
              "column beyond range of TableModel");
    }
  }

  private void toggle(int column, boolean toggleSortOrder,
          boolean toggleCalculateSubtotal) {
    SummarySortKey sortKey = findSortKey(column);
    if (sortKey == null) {
      sortKey = new SummarySortKey(column, SortOrder.ASCENDING,
              toggleCalculateSubtotal);
    } else {
      sortKey = toggle(sortKey, toggleSortOrder, toggleCalculateSubtotal);
    }
    setSortKeys(Arrays.asList(sortKey));
  }

  /**
   *
   * @param column the column index, in terms of model.
   * @return
   */
  private SummarySortKey findSortKey(int column) {
    for (SummarySortKey key : getSortKeys()) {
      if (key.getColumn() == column) {
        return key;
      }
    }
    return null;
  }

  private SummarySortKey toggle(SummarySortKey key,
          boolean toggleSortOrder, boolean toggleCalculateSubtotal) {
    int column = key.getColumn();
    SortOrder sortOrder = key.getSortOrder();
    boolean calculateSubtotal = key.isCalculateSubtotal();
    if (toggleSortOrder) {
      sortOrder = sortOrder == SortOrder.ASCENDING
              ? SortOrder.DESCENDING : SortOrder.ASCENDING;
    }
    if (toggleCalculateSubtotal) {
      calculateSubtotal = !calculateSubtotal;
    }
    return new SummarySortKey(column, sortOrder, calculateSubtotal);
  }

  /**
   * Returns true if the column is summarized, otherwise false.
   *
   * @param column the column index, in terms of model.
   * @return true if the column is summarized, otherwise false.
   */
  public boolean isColumnSummarized(int column) {
    return summaryColumns.contains(column);
  }

  /**
   * Set whether the column is summarized or not.
   *
   * @param column the column index, in terms of model.
   * @param summarized true if the column is summarized or false not.
   * @throws IllegalArgumentException if the class of the column not Number.
   */
  public void setColumnSummarized(int column, boolean summarized) {
    checkColumn(column);
    if (!isColumnSummable(column)) {
      throw new IllegalArgumentException("The class of the column not Number.");
    }
    if (summarized != isColumnSummarized(column)) {
      if (summarized) {
        summaryColumns.add(column);
      } else {
        summaryColumns.remove(column);
      }
      summaryCalculated = false;
      fireRowSorterChanged(new int[0]);
    }
  }

  /**
   * Returns true if currently has any summarized column.
   *
   * @return
   */
  public boolean hasSummarizedColumn() {
    return !summaryColumns.isEmpty();
  }

  public boolean isHideLastSummaryRow() {
    return hideLastSummaryRow;
  }

  public void setHideLastSummaryRow(boolean hideLastSummaryRow) {
    if (this.hideLastSummaryRow != hideLastSummaryRow) {
      this.hideLastSummaryRow = hideLastSummaryRow;
      fireRowSorterChanged(new int[0]);
    }
  }

  public boolean isShowAllKeysInSubtotal() {
    return showAllKeysInSubtotal;
  }

  public void setShowAllKeysInSubtotal(boolean showAllKeysInSubtotal) {
    if (this.showAllKeysInSubtotal != showAllKeysInSubtotal) {
      this.showAllKeysInSubtotal = showAllKeysInSubtotal;
      fireRowSorterChanged(new int[0]);
    }
  }

  /**
   * Return true if the column if summary able.
   *
   * @param column the column, in terms of the model.
   * @return
   */
  public boolean isColumnSummable(int column) {
    return Number.class.isAssignableFrom(getModel().getColumnClass(column));
  }

  @Override
  public void modelStructureChanged() {
    summaryColumns.clear();
    summaryCalculated = false;
    super.modelStructureChanged();
  }

  @Override
  public void sort() {
    summaryCalculated = false;
    super.sort();
    calculateSummaryIfNeed();
  }

  @Override
  public void rowsInserted(int firstRow, int endRow) {
    summaryCalculated = false;
    super.rowsInserted(firstRow, endRow);
    calculateSummaryIfNeed();
  }

  @Override
  public void rowsDeleted(int firstRow, int endRow) {
    summaryCalculated = false;
    super.rowsDeleted(firstRow, endRow);
    calculateSummaryIfNeed();
  }

  @Override
  public void rowsUpdated(int firstRow, int endRow) {
    summaryCalculated = false;
    super.rowsUpdated(firstRow, endRow);
    calculateSummaryIfNeed();
  }

  private void calculateSummaryIfNeed() {
    if (!summaryCalculated) {
      fireRowSorterChanged(new int[0]);
    }
  }

  @Override
  protected void fireRowSorterChanged(int[] lastRowIndexToModel) {
    int[] oldRowToModel = getViewToModelAsInts();
    calculateSummaryRows();
    super.fireRowSorterChanged(oldRowToModel);
  }

  private int[] getViewToModelAsInts() {
    if (viewToModel != null) {
      int[] viewToModelI = new int[viewToModel.length];
      for (int i = viewToModel.length - 1; i >= 0; i--) {
        viewToModelI[i] = viewToModel[i].modelIndex;
      }
      return viewToModelI;
    }
    return new int[0];
  }

  public void calculateSummaryRows() {
    if (summaryCalculated) {
      return;
    }
    summaryRowCount = 0;
    int summaryColumnCount = getSummaryColumnCount();
    Map<SortKey, Row> summaryRows = createSummaryRows();
    List<Row> rowList = new ArrayList<Row>();
    int viewModelRowCount = super.getViewRowCount();
    for (int i = 0; i < viewModelRowCount; i++) {
      int modelIndex = super.convertRowIndexToModel(i);
      rowList.add(new Row(modelIndex));
      if (summaryColumnCount > 0) {
        calculateSummaryByRow(summaryRows, modelIndex);
        List<SortKey> changedKeys = null;
        if (summaryRows.size() > 0) {
          int nextIndex = i == (viewModelRowCount - 1)
                  ? -1 : super.convertRowIndexToModel(i + 1);
          changedKeys = getChangedKeys(modelIndex, nextIndex);
        }
        if (!OrchidUtils.isEmpty(changedKeys)) {
          updateSubtotalKeyValue(summaryRows, modelIndex);
          saveSubtotalRows(rowList, summaryRows, changedKeys);
        }
      }
    }
    if (summaryColumnCount > 0 && viewModelRowCount > 0) {
      Row summary = summaryRows.get(null);
      rowList.add(summary);
      summary.mappedIndex = getModelRowCount() + summaryRowCount++;
    }
    setModelFromRowList(rowList);
    summaryCalculated = true;
  }

  private int getSummaryColumnCount() {
    return summaryColumns == null ? 0 : summaryColumns.size();
  }

  private Map<SortKey, Row> createSummaryRows() {
    int columnCount = getModelWrapper().getColumnCount();
    Map<SortKey, Row> rows = new HashMap<SortKey, Row>();
    rows.put(null, new Row(new Object[columnCount]));
    List<? extends SummarySortKey> sortKeys = getSortKeys();
    for (int index = 0; index < sortKeys.size(); index++) {
      SummarySortKey key = sortKeys.get(index);
      if (key.isCalculateSubtotal()) {
        rows.put(key, new Row(new Object[columnCount]));
      }
    }
    return rows;
  }

  private void calculateSummaryByRow(Map<SortKey, Row> summaryRows,
          int modelIndex) {
    TableModel model = getModel();
    for (int column : summaryColumns) {
      Class columnClass = model.getColumnClass(column);
      Number value = (Number) model.getValueAt(modelIndex, column);
      for (Row row : summaryRows.values()) {
        Number summary = (Number) row.values[column];
        row.values[column] = summaryNumber(summary, columnClass, value);
      }
    }
  }

  private Number summaryNumber(Number summary, Class columnClass,
          Number value) {
    if (columnClass == BigInteger.class) {
      return summaryToBigInteger(summary, value);
    }
    if (isIntegerOnly(columnClass)) {
      return summaryToLong(summary, value);
    } else if (columnClass == BigDecimal.class) {
      return summaryToBigDecimal(summary, value);
    }
    return summaryToDouble(summary, value);
  }

  private boolean isIntegerOnly(Class columnClass) {
    return columnClass == Byte.class || columnClass == Short.class
            || columnClass == Integer.class || columnClass == Long.class
            || columnClass == BigInteger.class;
  }

  private BigInteger summaryToBigInteger(Number summary, Number value) {
    BigInteger intValue = BigInteger.ZERO;
    //To keep value properties, use value as first operator
    if (value instanceof BigInteger) {
      intValue = (BigInteger) value;
      value = null;
    } else if (summary instanceof BigInteger) {
      intValue = (BigInteger) summary;
      summary = null;
    }
    if (value != null) {
      intValue = intValue.add(BigInteger.valueOf(value.longValue()));
    }
    if (summary instanceof BigInteger) {
      intValue = intValue.add((BigInteger) summary);
    } else if (summary != null) {
      intValue = intValue.add(BigInteger.valueOf(summary.longValue()));
    }
    return intValue;
  }

  private Long summaryToLong(Number summary, Number value) {
    long longValue = 0l;
    if (summary != null) {
      longValue += summary.longValue();
    }
    if (value != null) {
      longValue += value.longValue();
    }
    return longValue;
  }

  private BigDecimal summaryToBigDecimal(Number summary, Number value) {
    BigDecimal decValue = BigDecimal.ZERO;
    //To keep value properties, use value as first operator
    if (value instanceof BigDecimal) {
      decValue = (BigDecimal) value;
      value = null;
    } else if (summary instanceof BigDecimal) {
      decValue = (BigDecimal) summary;
      summary = null;
    }
    if (value != null) {
      decValue = decValue.add(BigDecimal.valueOf(value.doubleValue()));
    }
    if (summary instanceof BigDecimal) {
      decValue = decValue.add((BigDecimal) summary);
    } else if (summary != null) {
      decValue = decValue.add(BigDecimal.valueOf(summary.doubleValue()));
    }
    return decValue;
  }

  private Double summaryToDouble(Number summary, Number value) {
    double doubleValue = 0;
    if (summary != null) {
      doubleValue += summary.doubleValue();
    }
    if (value != null) {
      doubleValue += value.doubleValue();
    }
    return doubleValue;
  }

  private List<SortKey> getChangedKeys(int currentRow, int nextRow) {
    int changedIndex = -1;
    List<? extends SummarySortKey> keys = getSortKeys();
    if (nextRow == -1) {
      changedIndex = 0;
    } else {
      ModelWrapper modelWrapper = getModelWrapper();
      for (int c = 0; c < keys.size(); c++) {
        int column = keys.get(c).getColumn();
        Object cValue = modelWrapper.getValueAt(currentRow, column);
        Object nValue = modelWrapper.getValueAt(nextRow, column);
        if (!OrchidUtils.equals(cValue, nValue)) {
          changedIndex = c;
          break;
        }
      }
    }
    List<SortKey> changedKeys = new ArrayList<SortKey>();
    if (changedIndex != -1) {
      for (int c = changedIndex; c < keys.size(); c++) {
        if (keys.get(c).isCalculateSubtotal()) {
          changedKeys.add(keys.get(c));
        }
      }
    }
    return changedKeys;
  }

  private void updateSubtotalKeyValue(Map<SortKey, Row> subtotalRows,
          int modelIndex) {
    List<? extends SummarySortKey> sortKeys = getSortKeys();
    for (SortKey key : subtotalRows.keySet()) {
      if (key == null) {
        continue;
      }
      Row row = subtotalRows.get(key);
      int sortIndex = sortKeys.indexOf(key);
      int fromKey = showAllKeysInSubtotal ? 0 : sortIndex;
      for (int index = fromKey; index <= sortIndex; index++) {
        int column = sortKeys.get(index).getColumn();
        row.values[column] = getModelWrapper().getValueAt(modelIndex, column);
      }
    }
  }

  private void saveSubtotalRows(List<Row> rowList,
          Map<SortKey, Row> subtotalRows, List<SortKey> changedKeys) {
    int columnCount = getModelWrapper().getColumnCount();
    for (int c = changedKeys.size() - 1; c >= 0; c--) {
      SortKey key = changedKeys.get(c);
      Row subtotalRow = subtotalRows.get(key);
      subtotalRow.mappedIndex = getModelRowCount() + summaryRowCount++;
      rowList.add(subtotalRow);
      subtotalRows.put(key, new Row(new Object[columnCount]));
    }
  }

  private void setModelFromRowList(List<Row> rowList) {
    viewToModel = rowList.toArray(new Row[0]);
    int modelRowCount = getModelRowCount();
    int totalRowCount = modelRowCount + summaryRowCount;
    modelToView = new int[totalRowCount];
    Arrays.fill(modelToView, -1);
    //for support JTable view row height, the convertRowIndexToModel can not
    //return -1, so map the summary row index to virtual model index.
    for (int i = 0; i < viewToModel.length; i++) {
      if (viewToModel[i].modelIndex != -1) {
        modelToView[viewToModel[i].modelIndex] = i;
      } else {
        modelToView[viewToModel[i].mappedIndex] = i;
      }
    }
  }

  @Override
  public int convertRowIndexToView(int index) {
    if (modelToView == null) {
      if (index < 0 || index >= getModelWrapper().getRowCount()) {
        throw new IndexOutOfBoundsException("Invalid index");
      }
      return index;
    }
    return modelToView[index];
  }

  @Override
  public int convertRowIndexToModel(int index) {
    if (OrchidUtils.isEmpty(viewToModel)) {
      if (index < 0 || index >= getModelRowCount()) {
        throw new IndexOutOfBoundsException("Invalid index");
      }
      return index;
    }
    if (viewToModel[index].mappedIndex != -1) {
      return viewToModel[index].modelIndex;
    } else {
      return viewToModel[index].mappedIndex;
    }
  }

  @Override
  public int getViewRowCount() {
    if (viewToModel == null) {
      return getModelRowCount();
    } else if (isLastRowHidden()) {
      return viewToModel.length - 1;
    } else {
      return viewToModel.length;
    }
  }

  private boolean isLastRowHidden() {
    return !OrchidUtils.isEmpty(viewToModel)
            && isHideLastSummaryRow() && isSummaryRow(viewToModel.length - 1);
  }

  /**
   * return value at cell
   *
   * @param row row index, in terms of the view
   * @param column column index, in terms of the model
   * @return
   */
  public Object getValueAt(int row, int column) {
    int modelRow = viewToModel[row].modelIndex;
    if (modelRow == -1) {
      return viewToModel[row].values[column];
    } else {
      return getModel().getValueAt(modelRow, column);
    }
  }

  /**
   *
   * @param row row index, in terms of the view
   * @param column column index, in terms of the model
   * @return
   */
  public boolean isCellEditable(int row, int column) {
    int modelRow = viewToModel[row].modelIndex;
    if (modelRow == -1) {
      return false;
    } else {
      return getModel().isCellEditable(modelRow, column);
    }
  }

  public boolean isSummaryRow(int row) {
    return viewToModel[row].modelIndex == -1;
  }

  /**
   * set value to model.
   *
   * @param row row index, in terms of the view
   * @param column column index, in terms of the model
   * @param value value to set
   */
  public void setValueAt(int row, int column, Object value) {
    int modelRow = viewToModel[row].modelIndex;
    if (modelRow != -1) {
      getModel().setValueAt(value, modelRow, column);
    }
  }

  private static class Row {

    public int modelIndex;
    //for support JTable view row height,
    //map summary row index to virtual model index.
    public int mappedIndex;
    public Object[] values;

    public Row(int modelIndex) {
      this.modelIndex = modelIndex;
    }

    public Row(Object[] values) {
      this.modelIndex = -1;
      this.values = values;
    }
  }

  public static class SummarySortKey extends SortKey {

    private boolean calculateSubtotal;

    /**
     * Creates a
     * <code>SortKey</code> for the specified column with the specified sort
     * order.
     *
     * @param column index of the column, in terms of the model
     * @param sortOrder the sorter order
     * @param calculateSubtotal calculate subtotal of summarized columns for the
     * sort key column.
     * @throws IllegalArgumentException if <code>sortOrder</code> is
     * <code>null</code>
     */
    public SummarySortKey(int column, SortOrder sortOrder,
            boolean calculateSubtotal) {
      super(column, sortOrder);
      this.calculateSubtotal = calculateSubtotal;
    }

    /**
     * Returns true if calculate subtotal of summarized columns for the sort key
     * column, otherwise false.
     *
     * @return true if calculate subtotal of summarized columns for the sort key
     * column, otherwise false.
     */
    public boolean isCalculateSubtotal() {
      return calculateSubtotal;
    }

    @Override
    public int hashCode() {
      return super.hashCode() + 5;
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof SummarySortKey
              && ((SummarySortKey) obj).getColumn() == getColumn()
              && ((SummarySortKey) obj).getSortOrder() == getSortOrder()
              && ((SummarySortKey) obj).isCalculateSubtotal() == isCalculateSubtotal();
    }
  }
}