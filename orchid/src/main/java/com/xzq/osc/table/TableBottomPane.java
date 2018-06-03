/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import com.xzq.osc.JocTable;
import com.xzq.osc.OrchidLocale;
import com.xzq.osc.resource.Resource;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author zqxu
 */
public class TableBottomPane extends TableSidePane {

  private final static String uiClassID = "ocTableBottomPane";
  protected TableColumnModel columnModel;
  protected JScrollBar scroller;
  private boolean ignoreScrollerChange;
  private boolean scrollerUpdating;
  protected Component fixedColumnAdjuster;
  protected JLabel selectionLabel;
  protected TableModel tableModel;
  protected ListSelectionModel selectionModel;
  protected ListSelectionModel tickerModel;
  private TableModelListener tableModelHandler;
  private ListSelectionListener selectionHandler;
  protected boolean fixedColumnAllowed;
  protected MouseInputAdapter fixedColumnAdjusterHandler;
  private AdjustmentListener horzScrollerListener;
  private TableColumnModelListener columnModelHandler;

  static {
    UIManager.put(uiClassID, "com.xzq.osc.table.TableBottomPaneUI");
  }

  /**
   *
   */
  @Override
  protected void initializeLocalVars() {
    fixedColumnAllowed = true;
    setScroller(createDefaultScroller());
    setFixedColumnAdjuster(createDefaultFixedColumnAdjuster());
    setSelectionLabel(createDefaultSelectionLabel());
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
    registerTablePropertyListener("model");
    registerTablePropertyListener("selectionModel");
    registerTablePropertyListener("tickerModel");
    updateColumnModel(table.getColumnModel());
    updateTableModel(table.getModel());
    updateSelectionModel(table.getSelectionModel());
    updateTickerModel(table.getTickerModel());
  }

  @Override
  protected void uninstall(JocTable table) {
    super.uninstall(table);
    updateColumnModel(null);
    updateTableModel(null);
    updateSelectionModel(null);
    updateTickerModel(null);
  }

  /**
   *
   * @return
   */
  @Override
  public String getUIClassID() {
    return uiClassID;
  }

  // <editor-fold defaultstate="collapsed" desc="fixed columns support">
  /**
   * Returns whether allow change fixed columns count.
   *
   * @return true if allow change fixed column count or false not.
   */
  public boolean getFixedColumnAllowed() {
    return fixedColumnAllowed;
  }

  /**
   * Set whether allow change fixed columns count.
   *
   * @param fixedColumnAllowed true if allow change fixed columns count or false
   * not.
   */
  public void setFixedColumnAllowed(boolean fixedColumnAllowed) {
    if (this.fixedColumnAllowed != fixedColumnAllowed) {
      this.fixedColumnAllowed = fixedColumnAllowed;
      resizeAndRepaint();
    }
  }

  /**
   * Returns fixed columns adjuster component.
   *
   * @return fixed columns adjuster component.
   */
  public Component getFixedColumnAdjuster() {
    return fixedColumnAdjuster;
  }

  /**
   * Set fixed columns adjuster component.
   *
   * @param fixedColumnAdjuster fixed columns adjuster component.
   * @throws IllegalArgumentException if fixedColumnAdjuster is null.
   */
  public void setFixedColumnAdjuster(Component fixedColumnAdjuster) {
    if (fixedColumnAdjuster == null) {
      throw new IllegalArgumentException("Can not set null adjuster!");
    }
    if (fixedColumnAdjuster != this.fixedColumnAdjuster) {
      Component old = this.fixedColumnAdjuster;
      if (old != null) {
        old.removeMouseListener(getFixedColumnAdjusterHandler());
        old.removeMouseMotionListener(getFixedColumnAdjusterHandler());
        this.remove(old);
      }
      this.fixedColumnAdjuster = fixedColumnAdjuster;
      fixedColumnAdjuster.addMouseListener(getFixedColumnAdjusterHandler());
      fixedColumnAdjuster.addMouseMotionListener(getFixedColumnAdjusterHandler());
      this.add(fixedColumnAdjuster);
      firePropertyChange("fixedColumnAdjuster", old, fixedColumnAdjuster);
    }
  }

  /**
   * Returns MouseInpuAdapter for fixed columns adjuster.
   *
   * @return MouseInpuAdapter for fixed columns adjuster.
   */
  protected MouseInputAdapter getFixedColumnAdjusterHandler() {
    if (fixedColumnAdjusterHandler == null) {
      fixedColumnAdjusterHandler = new FixedColumnAdjusterHandler();
    }
    return fixedColumnAdjusterHandler;
  }

  /**
   * create default fixed columns adjuster.
   *
   * @return fixed columns adjuster.
   */
  protected Component createDefaultFixedColumnAdjuster() {
    JComponent adjuster = new JLabel();
    adjuster.setPreferredSize(new Dimension(3, 16));
    adjuster.setCursor(getFixedColumnAdjusterCursor());
    adjuster.setBackground(Color.BLACK);
    adjuster.setOpaque(true);
    return adjuster;
  }

  /**
   * Returns cursor for fixed columns adjuster.
   *
   * @return cursor for fixed columns adjuster.
   */
  protected Cursor getFixedColumnAdjusterCursor() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    try {
      return toolkit.createCustomCursor(
              Resource.getOrchidImage("fix_column.png"),
              new Point(8, 8), "fixedColumnAdjusterCursor");
    } catch (Exception ex) {
      return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
    }
  }

  /**
   * adjust fixed column count by point on scrren.
   *
   * @param pointOnScreen
   */
  protected void adjustFixedColumn(Point pointOnScreen) {
    Point point = new Point(pointOnScreen);
    SwingUtilities.convertPointFromScreen(point, table);
    table.setFixedColumnCount(table.columnAtPoint(point));
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="horizontal scroll support">
  /**
   * Returns scroll bar component.
   *
   * @return scroll bar.
   */
  public JScrollBar getScroller() {
    return scroller;
  }

  /**
   * Set scroll bar component.
   *
   * @param scroller scroll bar.
   * @throws IllegalArgumentException if scroller is null.
   */
  public void setScroller(JScrollBar scroller) {
    if (scroller == null) {
      throw new IllegalArgumentException("Can not set null scroller!");
    }
    if (scroller != this.scroller) {
      JScrollBar old = this.scroller;
      if (old != null) {
        old.removeAdjustmentListener(getHorzScrollerListener());
        remove(old);
      }
      this.scroller = scroller;
      scroller.addAdjustmentListener(getHorzScrollerListener());
      add(scroller);
      resizeAndRepaint();
      firePropertyChange("scroller", old, scroller);
    }
  }

  /**
   * create default scroll bar.
   *
   * @return scroll bar.
   */
  protected JScrollBar createDefaultScroller() {
    JScrollBar sb = new JScrollBar();
    sb.setOrientation(JScrollBar.HORIZONTAL);
    return sb;
  }

  private AdjustmentListener getHorzScrollerListener() {
    if (horzScrollerListener == null) {
      horzScrollerListener = new HorizontalScrollerListener();
    }
    return horzScrollerListener;
  }

  /**
   * process scroll bar positon change.
   */
  public void horizontalScrollerValueChanged() {
    table.setHorizontalPosition(scroller.getValue());
  }

  private void updateColumnModel(TableColumnModel columnModel) {
    if (this.columnModel != null) {
      this.columnModel.removeColumnModelListener(getColumnModelHandler());
    }
    this.columnModel = columnModel;
    if (this.columnModel != null) {
      this.columnModel.addColumnModelListener(getColumnModelHandler());
    }
    updateScroller();
  }

  private TableColumnModelListener getColumnModelHandler() {
    if (columnModelHandler == null) {
      columnModelHandler = new TableColumnModelListener() {
        @Override
        public void columnAdded(TableColumnModelEvent e) {
          updateScroller();
        }

        @Override
        public void columnRemoved(TableColumnModelEvent e) {
          updateScroller();
        }

        @Override
        public void columnMoved(TableColumnModelEvent e) {
        }

        @Override
        public void columnMarginChanged(ChangeEvent e) {
          updateScroller();
        }

        @Override
        public void columnSelectionChanged(ListSelectionEvent e) {
        }
      };
    }
    return columnModelHandler;
  }

  /**
   * update scroll bar properties to math table.
   */
  public void updateScroller() {
    if (table != null) {
      scroller.setValueIsAdjusting(true);
      scrollerUpdating = true;
      int visibleAmount = table.getScrollColumnsArea().width;
      int maximum = table.getTotalScrollColumnsWidth();
      scroller.setMaximum(Math.max(0, maximum));
      scroller.setValue(table.getHorizontalPosition());
      scroller.setVisibleAmount(visibleAmount);
      scroller.setUnitIncrement(visibleAmount / 5);
      scroller.setBlockIncrement(visibleAmount / 2);
      scrollerUpdating = false;
      scroller.setValueIsAdjusting(false);
    }
  }

  /**
   * update scroll bar when table horizontal position change.
   *
   * @param propertyName property name.
   */
  @Override
  protected void tablePropertyChange(String propertyName) {
    if (propertyName.equals("columnModel")) {
      updateColumnModel(table.getColumnModel());
    } else if (propertyName.equals("horizontalPosition")) {
      ignoreScrollerChange = true;
      updateScroller();
      ignoreScrollerChange = false;
    } else if (propertyName.equals("model")) {
      updateTableModel(table.getModel());
    } else if (propertyName.equals("selectionModel")) {
      updateSelectionModel(table.getSelectionModel());
    } else if (propertyName.equals("tickerModel")) {
      updateTickerModel(table.getTickerModel());
    }
  }
  // </editor-fold>

  //<editor-fold defaultstate="collapsed" desc="selection label">
  protected JLabel createDefaultSelectionLabel() {
    JLabel label = new JLabel();
    label.setHorizontalAlignment(JLabel.TRAILING);
    return label;
  }

  /**
   * Returns the label for display table selection information.
   *
   * @return the label
   */
  public JLabel getSelectionLabel() {
    return selectionLabel;
  }

  /**
   * Sets the label for display table selection information.
   *
   * @param selectionLabel the label
   * @throws IllegalArgumentException if the label was null.
   */
  public void setSelectionLabel(JLabel selectionLabel) {
    if (selectionLabel == null) {
      throw new IllegalArgumentException("Can not set null selection label!");
    }
    if (this.selectionLabel != selectionLabel) {
      JLabel old = this.selectionLabel;
      if (this.selectionLabel != null) {
        this.remove(this.selectionLabel);
      }
      this.add(this.selectionLabel = selectionLabel);
      updateSelectionLabel();
      firePropertyChange("selectionLabel", old, selectionLabel);
    }
  }

  /**
   * Returns visibility of the selection label.
   *
   * @return true or false.
   */
  public boolean getSelectionLabelVisible() {
    return getSelectionLabel().isVisible();
  }

  /**
   * Sets visibility of the selection label.
   *
   * @param selectionLabelVisible true or false.
   */
  public void setSelectionLabelVisible(boolean selectionLabelVisible) {
    getSelectionLabel().setVisible(selectionLabelVisible);
    updateSelectionLabel();
  }

  private void updateTableModel(TableModel model) {
    if (this.tableModel != null) {
      this.tableModel.removeTableModelListener(getTableModelHandler());
    }
    this.tableModel = model;
    if (this.tableModel != null) {
      this.tableModel.addTableModelListener(getTableModelHandler());
    }
    updateSelectionLabel();
  }

  private TableModelListener getTableModelHandler() {
    if (tableModelHandler == null) {
      tableModelHandler = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
          if (e == null || e.getLastRow() == Integer.MAX_VALUE
                  || e.getFirstRow() == TableModelEvent.HEADER_ROW
                  || e.getType() == TableModelEvent.INSERT
                  || e.getType() == TableModelEvent.DELETE) {
            updateSelectionLabel();
          }
        }
      };
    }
    return tableModelHandler;
  }

  private void updateSelectionModel(ListSelectionModel selectionModel) {
    if (this.selectionModel != null) {
      this.selectionModel.removeListSelectionListener(getSelectionHandler());
    }
    this.selectionModel = selectionModel;
    if (this.selectionModel != null) {
      this.selectionModel.addListSelectionListener(getSelectionHandler());
    }
    updateSelectionLabel();
  }

  private void updateTickerModel(ListSelectionModel tickerModel) {
    if (this.tickerModel != null) {
      this.tickerModel.removeListSelectionListener(getSelectionHandler());
    }
    this.tickerModel = tickerModel;
    if (this.tickerModel != null) {
      this.tickerModel.addListSelectionListener(getSelectionHandler());
    }
    updateSelectionLabel();
  }

  private ListSelectionListener getSelectionHandler() {
    if (selectionHandler == null) {
      selectionHandler = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            updateSelectionLabel();
          }
        }
      };
    }
    return selectionHandler;
  }

  private void updateSelectionLabel() {
    if (tableModel == null || selectionModel == null) {
      return;
    }
    int rowCount = table.getRowCount();
    int[] selected = table.getSelectedRows();
    int[] ticked = table.getTickedRows();
    String selection;
    if (selected.length == 0 && ticked.length == 0) {
      selection = String.format(OrchidLocale.getString("ROW_COUNT_LABEL"),
              rowCount);
    } else if (ticked.length > 0) {
      selection = String.format(OrchidLocale.getString("TICKED_ROWS_LABEL"),
              ticked.length, rowCount);
    } else {
      selection = String.format(OrchidLocale.getString("SELECTED_ROWS_LABEL"),
              selected.length, rowCount);
    }
    selectionLabel.setText(selection);
  }
  //</editor-fold>

  private class FixedColumnAdjusterHandler extends MouseInputAdapter {

    @Override
    public void mouseDragged(MouseEvent e) {
      adjustFixedColumn(e.getLocationOnScreen());
    }

    @Override
    public void mousePressed(MouseEvent e) {
      if (fixedColumnAllowed && e.getButton() == MouseEvent.BUTTON1) {
        table.setHorizontalPosition(0);
        table.setFixedColumnAdjusting(true);
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      table.setFixedColumnAdjusting(false);
    }
  }

  private class HorizontalScrollerListener implements AdjustmentListener {

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
      if (!ignoreScrollerChange && !scrollerUpdating) {
        horizontalScrollerValueChanged();
      }
    }
  }
}
