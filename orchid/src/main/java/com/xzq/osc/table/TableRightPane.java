/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import com.xzq.osc.JocTable;
import com.xzq.osc.resource.Resource;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author zqxu
 */
public class TableRightPane extends TableSidePane {

  private final static String uiClassID = "ocTableRightPane";
  protected JLabel optionLabel;
  protected TableModel tableModel;
  protected JScrollBar scroller;
  private boolean ignoreScrollerChange;
  private boolean scrollerUpdating;
  protected Component fixedRowAdjuster;
  private AdjustmentListener vertScrollerListener;
  protected boolean fixedRowAllowed;
  protected MouseInputAdapter fixedRowAdjusterHandler;
  private MouseListener optionLabelHandler;

  static {
    UIManager.put(uiClassID, "com.xzq.osc.table.TableRightPaneUI");
  }
  private TableModelListener tableModelHandler;

  /**
   * initialize local vars.
   */
  @Override
  protected void initializeLocalVars() {
    fixedRowAllowed = true;
    setOptionLabel(createDefaultOptionLabel());
    setScroller(createDefaultScroller());
    setFixedRowAdjuster(createDefaultFixedRowAdjuster());
  }

  /**
   * install on table
   *
   * @param table
   */
  @Override
  protected void install(JocTable table) {
    updateOptionButton();
    registerTablePropertyListener("model");
    registerTablePropertyListener("verticalPosition");
    registerTablePropertyListener("optionPopupMenu");
    registerTablePropertyListener("optionPopupActive");
    updateTableModel(table.getModel());
  }

  @Override
  protected void uninstall(JocTable table) {
    super.uninstall(table);
    updateTableModel(null);
  }

  /**
   *
   * @return
   */
  @Override
  public String getUIClassID() {
    return uiClassID;
  }

  //<editor-fold defaultstate="collapsed" desc="option label support">
  public JLabel getOptionLabel() {
    return optionLabel;
  }

  public void setOptionLabel(JLabel optionLabel) {
    if (optionLabel == null) {
      throw new IllegalArgumentException("Can not set null option button!");
    }
    JLabel old = this.optionLabel;
    if (old != null) {
      remove(old);
      old.removeMouseListener(getOptionLabelHandler());
    }
    add(this.optionLabel = optionLabel);
    optionLabel.addMouseListener(getOptionLabelHandler());
    firePropertyChange("optionButton", old, optionLabel);
  }

  private MouseListener getOptionLabelHandler() {
    if (optionLabelHandler == null) {
      optionLabelHandler = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
          showOptionPopupMenu();
        }
      };
    }
    return optionLabelHandler;
  }

  private void showOptionPopupMenu() {
    Point point = new Point(0, optionLabel.getHeight());
    Dimension size = table.getOptionPopupMenu().getPreferredSize();
    point.x -= size.width;
    point = SwingUtilities.convertPoint(optionLabel, point, table);
    table.showOptionPopupMenu(point);
  }

  protected JLabel createDefaultOptionLabel() {
    return new JLabel(Resource.getOrchidIcon("table_gear.png"));
  }

  private void updateOptionButton() {
    optionLabel.setVisible(table.isOptionPopupActive()
            && table.getOptionPopupMenu() != null);
  }
  //</editor-fold>

  // <editor-fold defaultstate="collapsed" desc="fixed rows support">
  /**
   * Returns whether allow fixed rows or not. default is true.
   *
   * @return true for allow fixed rows or false not.
   */
  public boolean getFixedRowAllowed() {
    return fixedRowAllowed;
  }

  /**
   * Set whether allow fixed rows or not.
   *
   * @param fixedRowAllowed true for allow fixed rows or false not.
   */
  public void setFixedRowAllowed(boolean fixedRowAllowed) {
    if (this.fixedRowAllowed != fixedRowAllowed) {
      this.fixedRowAllowed = fixedRowAllowed;
      resizeAndRepaint();
    }
  }

  /**
   *
   * @return
   */
  public Component getFixedRowAdjuster() {
    return fixedRowAdjuster;
  }

  /**
   *
   * @param fixedRowAdjuster
   */
  public void setFixedRowAdjuster(Component fixedRowAdjuster) {
    if (fixedRowAdjuster == null) {
      throw new IllegalArgumentException("Can not set null adjuster!");
    }
    if (fixedRowAdjuster != this.fixedRowAdjuster) {
      Component old = this.fixedRowAdjuster;
      if (old != null) {
        old.removeMouseListener(getFixedRowAdjusterHandler());
        old.removeMouseMotionListener(getFixedRowAdjusterHandler());
        this.remove(old);
      }
      fixedRowAdjuster.addMouseListener(getFixedRowAdjusterHandler());
      fixedRowAdjuster.addMouseMotionListener(getFixedRowAdjusterHandler());
      this.add(this.fixedRowAdjuster = fixedRowAdjuster);
      firePropertyChange("fixedRowAdjuster", old, fixedRowAdjuster);
    }
  }

  /**
   *
   * @return
   */
  protected MouseInputAdapter getFixedRowAdjusterHandler() {
    if (fixedRowAdjusterHandler == null) {
      fixedRowAdjusterHandler = new FixedRowAdjusterHandler();
    }
    return fixedRowAdjusterHandler;
  }

  /**
   *
   * @return
   */
  protected Component createDefaultFixedRowAdjuster() {
    JComponent adjuster = new JLabel();
    adjuster.setPreferredSize(new Dimension(3, 16));
    adjuster.setCursor(getFixedRowAdjusterCursor());
    adjuster.setBackground(Color.BLACK);
    adjuster.setOpaque(true);
    return adjuster;
  }

  /**
   *
   * @return
   */
  protected Cursor getFixedRowAdjusterCursor() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    try {
      return toolkit.createCustomCursor(Resource.getOrchidImage("fix_row.png"),
              new Point(8, 8), "fixedRowAdjusterCursor");
    } catch (Exception ex) {
      return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
    }
  }

  /**
   *
   * @param pointOnScreen
   */
  protected void adjustFixedRow(Point pointOnScreen) {
    Point point = new Point(pointOnScreen);
    SwingUtilities.convertPointFromScreen(point, table);
    table.setFixedRowCount(table.rowAtPoint(point));
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="scroll bar support">
  /**
   * Returns scroll bar.
   *
   * @return scroll bar.
   */
  public JScrollBar getScroller() {
    return scroller;
  }

  /**
   * Set scroll bar.
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
        old.removeAdjustmentListener(getVertScrollerListener());
        remove(old);
      }
      this.scroller = scroller;
      scroller.addAdjustmentListener(getVertScrollerListener());
      add(scroller);
      resizeAndRepaint();
      firePropertyChange("scroller", old, scroller);
    }
  }

  /**
   * Create default scroll bar.
   *
   * @return default scroll bar.
   */
  protected JScrollBar createDefaultScroller() {
    return new JScrollBar();
  }

  private AdjustmentListener getVertScrollerListener() {
    if (vertScrollerListener == null) {
      vertScrollerListener = new VerticalScrollerListener();
    }
    return vertScrollerListener;
  }

  /**
   * scroll bar position changed.
   */
  protected void verticalScrollerValueChanged() {
    table.setVerticalPosition(scroller.getValue());
  }

  protected void updateTableModel(TableModel model) {
    if (tableModel != null) {
      tableModel.removeTableModelListener(getTableModelHandler());
    }
    tableModel = model;
    if (this.tableModel != null) {
      tableModel.addTableModelListener(getTableModelHandler());
    }
    updateScroller();
  }

  private TableModelListener getTableModelHandler() {
    if (tableModelHandler == null) {
      tableModelHandler = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
          if (e == null || e.getLastRow() == Integer.MAX_VALUE
                  || e.getFirstRow() == TableModelEvent.HEADER_ROW
                  || e.getType() != TableModelEvent.UPDATE) {
            updateScroller();
          }
        }
      };
    }
    return tableModelHandler;
  }

  /**
   *
   */
  public void updateScroller() {
    if (table != null) {
      scroller.setValueIsAdjusting(true);
      scrollerUpdating = true;
      int visibleAmount = table.getScrollRowsArea().height;
      int maximum = table.getTotalScrollRowsHeight();
      scroller.setMaximum(Math.max(0, maximum));
      scroller.setValue(table.getVerticalPosition());
      scroller.setVisibleAmount(visibleAmount);
      scroller.setUnitIncrement(visibleAmount / 5);
      scroller.setBlockIncrement(visibleAmount / 2);
      scrollerUpdating = false;
      scroller.setValueIsAdjusting(false);
    }
  }

  /**
   *
   * @param propertyName
   */
  @Override
  protected void tablePropertyChange(String propertyName) {
    if (propertyName.equals("model")) {
      updateTableModel(table.getModel());
    } else if (propertyName.equals("verticalPosition")) {
      ignoreScrollerChange = true;
      updateScroller();
      ignoreScrollerChange = false;
    } else if (propertyName.equals("optionPopupActive")
            || propertyName.equals("optionPopupMenu")) {
      updateOptionButton();
    }
  }
  // </editor-fold>

  private class FixedRowAdjusterHandler extends MouseInputAdapter {

    @Override
    public void mouseDragged(MouseEvent e) {
      adjustFixedRow(e.getLocationOnScreen());
    }

    @Override
    public void mousePressed(MouseEvent e) {
      if (fixedRowAllowed && e.getButton() == MouseEvent.BUTTON1) {
        table.setVerticalPosition(0);
        table.setFixedRowAdjusting(true);
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      table.setFixedRowAdjusting(false);
    }
  }

  private class VerticalScrollerListener implements AdjustmentListener {

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
      if (!ignoreScrollerChange && !scrollerUpdating) {
        verticalScrollerValueChanged();
      }
    }
  }
}