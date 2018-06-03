/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.table.TableBottomPane;
import com.xzq.osc.treetable.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author zqxu
 */
public class JocTreeTable extends JocTable {

  protected TreeTableModel treeTableModel;
  protected TreeColumnCellRenderer treeColumnCellRenderer;
  protected int treeColumnHorizontal;
  private PropertyChangeListener showsRootHandlesListener;
  private boolean consumeOnRelease;
  private boolean expansionChangedFlag;
  private TreeExpansionHandler treeExpansionHandler;

  /**
   * Constructor
   */
  public JocTreeTable() {
    this(null);
  }

  /**
   *
   * @param treeTableModel
   */
  public JocTreeTable(TreeTableModel treeTableModel) {
    super();
    setTreeTableModel(treeTableModel);
  }

  /**
   *
   */
  @Override
  protected void initializeLocalVars() {
    super.initializeLocalVars();
    setTreeColumnCellRenderer(createDefaultTreeColumnCellRenderer());
  }

  /**
   *
   */
  @Override
  protected TableBottomPane createDefaultBottomPane() {
    return new TreeTableBottomPane();
  }

  /**
   *
   * @param row
   * @param column
   * @return
   */
  @Override
  public TableCellRenderer getCellRenderer(int row, int column) {
    if (column == getHierarchicalColumn()) {
      return getTreeColumnCellRenderer();
    } else {
      return super.getCellRenderer(row, column);
    }
  }

  /**
   *
   * @return
   */
  @Override
  protected TableModel createDefaultDataModel() {
    return new TreeTableModelAdapter();
  }

  /**
   *
   * @return
   */
  @Override
  public TreeTableModelAdapter getModel() {
    return (TreeTableModelAdapter) super.getModel();
  }

  /**
   *
   * @param dataModel
   */
  @Override
  public void setModel(TableModel dataModel) {
    if (!(dataModel instanceof TreeTableModelAdapter)) {
      throw new IllegalArgumentException("Only accept TreeTableModelAdapter!");
    }
    TreeTableModelAdapter old = getModel();
    if (old != null) {
      old.unbind();
    }
    ((TreeTableModelAdapter) dataModel).bind(this);
    super.setModel(dataModel);
  }

  @Override
  protected void resizeAndRepaint() {
    super.resizeAndRepaint();
    adjustTreeRendererBounds();
  }

  private void adjustTreeRendererBounds() {
    if (treeColumnCellRenderer != null && treeTableModel != null) {
      treeColumnCellRenderer.setBounds(0, 0, 0, 0);
    }
  }

  /**
   *
   * @return
   */
  public TreeTableModel getTreeTableModel() {
    return treeTableModel;
  }

  /**
   *
   * @param treeTableModel
   */
  public void setTreeTableModel(TreeTableModel treeTableModel) {
    Object old = this.treeTableModel;
    this.treeTableModel = treeTableModel;
    firePropertyChange("treeTableModel", old, treeTableModel);
  }

  /**
   *
   * @return
   */
  public TreeColumnCellRenderer getTreeColumnCellRenderer() {
    return treeColumnCellRenderer;
  }

  /**
   *
   * @param treeColumnCellRenderer
   */
  public void setTreeColumnCellRenderer(
          TreeColumnCellRenderer treeColumnCellRenderer) {
    if (treeColumnCellRenderer == null) {
      throw new IllegalArgumentException(
              "Can not set null treeColumnCellRenderer");
    }
    TreeExpansionHandler l = getTreeExpansionHandler();
    TreeColumnCellRenderer old = this.treeColumnCellRenderer;
    if (old != null) {
      old.unbind();
      old.removeTreeExpansionListener(l);
      old.removePropertyChangeListener("showsRootHandles",
              getShowsRootHandlesListener());
    }
    this.treeColumnCellRenderer = treeColumnCellRenderer;
    treeColumnCellRenderer.bind(this);
    treeColumnCellRenderer.addTreeExpansionListener(l);
    treeColumnCellRenderer.addPropertyChangeListener("showsRootHandles",
            getShowsRootHandlesListener());
    // for prevent double click expand/collapse node
    treeColumnCellRenderer.setToggleClickCount(0);
    adjustTreeRendererBounds();
    firePropertyChange("treeColumnCellRenderer", old, treeColumnCellRenderer);
  }

  private PropertyChangeListener getShowsRootHandlesListener() {
    if (showsRootHandlesListener == null) {
      showsRootHandlesListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          resizeAndRepaint();
        }
      };
    }
    return showsRootHandlesListener;
  }

  private TreeColumnCellRenderer createDefaultTreeColumnCellRenderer() {
    return new TreeColumnCellRenderer();
  }

  /**
   *
   * @return
   */
  public int getHierarchicalColumn() {
    return treeTableModel == null ? -1
            : convertColumnIndexToView(treeTableModel.getHierarchicalColumn());
  }

  /**
   *
   * @return
   */
  public int getTreeColumnHorizontal() {
    return treeColumnHorizontal;
  }

  /**
   *
   * @param treeColumnHorizontal
   */
  public void setTreeColumnHorizontal(int treeColumnHorizontal) {
    int old = this.treeColumnHorizontal;
    if (old != treeColumnHorizontal) {
      this.treeColumnHorizontal = treeColumnHorizontal;
      repaintTreeColumn();
      firePropertyChange("treeColumnHorizontal", old, treeColumnHorizontal);
    }
  }

  /**
   *
   */
  public void repaintTreeColumn() {
    int column = getHierarchicalColumn();
    Rectangle rect = getCellRect(0, column, false);
    Rectangle rBounds = getRendererBounds();
    rect.y = rBounds.y;
    rect.height = rBounds.height;
    repaint(rect);
  }

  /**
   *
   * @param rowHeight
   */
  @Override
  public void setRowHeight(int rowHeight) {
    super.setRowHeight(rowHeight);
    adjustTreeRowHeight(getRowHeight());
  }

  /**
   *
   * @param tableRowHeight
   */
  protected void adjustTreeRowHeight(int tableRowHeight) {
    if (treeColumnCellRenderer != null
            && treeColumnCellRenderer.getRowHeight() != tableRowHeight) {
      treeColumnCellRenderer.setRowHeight(tableRowHeight);
    }
  }

  /**
   * Unsupport in JocTreeTable, do nothing.
   *
   * @param row
   * @param rowHeight
   * @deprecated
   */
  @Deprecated
  @Override
  public final void setRowHeight(int row, int rowHeight) {
  }

  /**
   * Unsupport in JocTreeTable, do nothing.
   *
   * @param autoCreateRowSorter
   * @deprecated
   */
  @Deprecated
  @Override
  public void setAutoCreateRowSorter(boolean autoCreateRowSorter) {
  }

  /**
   * Unsupport in JocTreeTable, do nothing.
   *
   * @param sorter
   * @deprecated
   */
  @Deprecated
  @Override
  public void setRowSorter(RowSorter<? extends TableModel> sorter) {
  }

  // <editor-fold defaultstate="collapsed" desc="tree collapse and expansion">
  /**
   *
   * @param e
   */
  @Override
  protected void processMouseEvent(MouseEvent e) {
    // Tree column renderer (JTree) must disable double click toggle
    // expand state to leave double click for cell edit.
    // setToggleClickCount to a value not be 1 and 2.
    if ((e.getID() == MouseEvent.MOUSE_RELEASED) && consumeOnRelease) {
      consumeOnRelease = false;
      e.consume();
    } else if (expandOrCollapseNode(e)) {
      consumeOnRelease = true;
      e.consume();
    } else {
      consumeOnRelease = false;
      super.processMouseEvent(e);
    }
  }

  /**
   *
   * @param e
   * @return
   */
  protected boolean expandOrCollapseNode(MouseEvent e) {
    int column = columnAtPoint(e.getPoint());
    if (column != getHierarchicalColumn()
            || !SwingUtilities.isLeftMouseButton(e)
            || e.getID() != MouseEvent.MOUSE_PRESSED
            || (e.getModifiers() != 0
            && e.getModifiers() != MouseEvent.BUTTON1_MASK)) {
      return false;
    }
    expansionChangedFlag = false;
    TreeColumnCellRenderer renderer = getTreeColumnCellRenderer();
    int halfMargin = getColumnModel().getColumnMargin() / 2;
    int x = e.getX() - getCellRect(0, column, true).x
            - halfMargin + getTreeColumnHorizontal();
    int y = convertYtoEntireTable(e.getY());
    MouseEvent pressed = new MouseEvent(renderer, e.getID(), e.getWhen(),
            e.getModifiers(), x, y, e.getClickCount(), e.isPopupTrigger());
    renderer.dispatchEvent(pressed);
    MouseEvent released = new MouseEvent(renderer,
            java.awt.event.MouseEvent.MOUSE_RELEASED, pressed.getWhen(),
            pressed.getModifiers(), pressed.getX(), pressed.getY(),
            pressed.getClickCount(), pressed.isPopupTrigger());
    renderer.dispatchEvent(released);
    return expansionChangedFlag;
  }

  private TreeExpansionHandler getTreeExpansionHandler() {
    if (treeExpansionHandler == null) {
      return new TreeExpansionHandler();
    }
    return treeExpansionHandler;
  }

  /**
   *
   * @param evt
   */
  protected void TreeRendererExpanded(TreeExpansionEvent evt) {
    expansionChangedFlag = true;
  }

  /**
   *
   * @param evt
   */
  protected void TreeRendererCollapsed(TreeExpansionEvent evt) {
    expansionChangedFlag = true;
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="tree function support">
  /**
   *
   */
  public void expandAll() {
    for (int i = 0; i < treeColumnCellRenderer.getRowCount(); i++) {
      treeColumnCellRenderer.expandRow(i);
    }
  }

  /**
   *
   */
  public void collapseAll() {
    for (int i = treeColumnCellRenderer.getRowCount() - 1; i >= 0; i--) {
      treeColumnCellRenderer.collapseRow(i);
    }
  }

  /**
   *
   * @param row
   */
  public void expandRow(int row) {
    treeColumnCellRenderer.expandRow(row);
  }

  /**
   *
   * @param row
   */
  public void collapseRow(int row) {
    treeColumnCellRenderer.collapseRow(row);
  }

  /**
   *
   * @param path
   */
  public void expandPath(TreePath path) {
    treeColumnCellRenderer.expandPath(path);
  }

  /**
   *
   * @param path
   */
  public void collapsePath(TreePath path) {
    treeColumnCellRenderer.collapsePath(path);
  }

  /**
   *
   * @param x
   * @param y
   * @return
   */
  public TreePath getPathForLocation(int x, int y) {
    int row = rowAtPoint(new Point(x, y));
    if (row == -1) {
      return null;
    }
    return treeColumnCellRenderer.getPathForRow(row);
  }

  /**
   *
   * @param row
   * @return
   */
  public TreePath getPathForRow(int row) {
    return treeColumnCellRenderer.getPathForRow(row);
  }

  /**
   *
   * @param path
   * @return
   */
  public int getRowForPath(TreePath path) {
    return treeColumnCellRenderer.getRowForPath(path);
  }

  /**
   *
   * @param row
   * @return
   */
  public Object getNodeForRow(int row) {
    return treeColumnCellRenderer.nodeForRow(row);
  }

  /**
   *
   * @param visible
   */
  public void setRootVisible(boolean visible) {
    treeColumnCellRenderer.setRootVisible(visible);
  }

  /**
   *
   * @return
   */
  public boolean isRootVisible() {
    return treeColumnCellRenderer.isRootVisible();
  }

  /**
   *
   * @param visible
   */
  public void setShowsRootHandles(boolean visible) {
    treeColumnCellRenderer.setShowsRootHandles(visible);
  }

  /**
   *
   * @return
   */
  public boolean getShowsRootHandles() {
    return treeColumnCellRenderer.getShowsRootHandles();
  }
  // </editor-fold>

  private class TreeExpansionHandler implements TreeExpansionListener {

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
      TreeRendererExpanded(event);
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
      TreeRendererCollapsed(event);
    }
  }
}
