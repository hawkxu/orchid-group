/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.list.CheckedListCellRenderer;
import com.xzq.osc.plaf.LookAndFeelManager;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

/**
 * List that every item with a check box, and the selection operation
 * changed.<br> mouse click and drag switch the item selected state, not change
 * selected index to the item. <br>space key switch the item selected
 * state.<br> direction keys only change the lead selected index, did not
 * change the item selected state.<br>
 * <code>JocCheckedList</code> used
 * <code>com.xzq.osc.list.CheckedListCellRenderer</code> as default cell
 * renderer, as an alternative, you can use JList and set cell renderer to
 * <code>com.xzq.osc.list.CheckedListCellRenderer</code> as you like.
 *
 * @author zqxu
 */
public class JocCheckedList extends JList implements OrchidAboutIntf {

  private boolean dragging;
  private int dragFromIndex;
  private int dragLastIndex;

  public JocCheckedList() {
    this(new AbstractListModel() {
      @Override
      public int getSize() {
        return 0;
      }

      @Override
      public Object getElementAt(int index) {
        return null;
      }
    });
  }

  public JocCheckedList(final Object[] listData) {
    this(new AbstractListModel() {
      @Override
      public int getSize() {
        return listData.length;
      }

      @Override
      public Object getElementAt(int index) {
        return listData[index];
      }
    });
  }

  public JocCheckedList(ListModel dataModel) {
    super(dataModel);
    initializeLocalVars();
  }

  /**
   * Set cell renderer.<br> Adjust action map to redefined the operation of
   * selection.
   */
  protected void initializeLocalVars() {
    setCellRenderer(new CheckedListCellRenderer());
    ActionMap actionMap = getActionMap();
    actionMap.put("selectNextRow",
            actionMap.get("selectNextRowChangeLead"));
    actionMap.put("selectPreviousRow",
            actionMap.get("selectPreviousRowChangeLead"));
    actionMap.put("addToSelection",
            actionMap.get("toggleAndAnchor"));
    actionMap.put("selectLastRow",
            actionMap.get("selectLastRowChangeLead"));
    actionMap.put("scrollUp",
            actionMap.get("scrollUpChangeLead"));
    actionMap.put("scrollDown",
            actionMap.get("scrollDownChangeLead"));
    actionMap.put("selectFirstRow",
            actionMap.get("selectFirstRowChangeLead"));
  }

  /**
   * Override to redefined the operation of selection.
   */
  @Override
  protected void processMouseEvent(MouseEvent e) {
    if (e.getID() == MouseEvent.MOUSE_RELEASED) {
      dragging = false;
    }
    if (e.getID() == MouseEvent.MOUSE_PRESSED) {
      adjustSelection(e);
    }
    super.processMouseEvent(e);
  }

  /**
   * Override to redefined the operation of selection.
   */
  @Override
  protected void processMouseMotionEvent(MouseEvent e) {
    dragging = e.getID() == MouseEvent.MOUSE_DRAGGED;
    if (dragging && !getDragEnabled()) {
      adjustSelection(e);
    }
    super.processMouseMotionEvent(e);
  }

  private void adjustSelection(MouseEvent e) {
    if (!SwingUtilities.isLeftMouseButton(e)
            || !isEnabled() || e.isConsumed()
            || e.isShiftDown() || e.isControlDown()) {
      return;
    }
    e.consume();
    if (!isFocusOwner() && isFocusable()) {
      requestFocusInWindow();
    }
    Point point = e.getPoint();
    int row = locationToIndex(point);
    if (row == -1) {
      return;
    }
    if (e.getID() == MouseEvent.MOUSE_PRESSED) {
      pressedSelection(row);
    } else if (getVisibleRect().contains(point)) {
      draggedSelection(row);
    }
  }

  private void pressedSelection(int row) {
    dragFromIndex = row;
    dragLastIndex = row;
    setValueIsAdjusting(true);
    reverseSelected(row);
  }

  private void draggedSelection(int row) {
    if (row == dragLastIndex) {
      return;
    }
    int sa = Integer.signum(row - dragFromIndex);
    int sb = Integer.signum(row - dragLastIndex);
    int from = sa == sb ? dragLastIndex : row;
    int last = sa == sb ? row : dragLastIndex;
    // if drag last and row not step over drag from
    // all items between last and row need reversed
    if (sa == Integer.signum(
            dragLastIndex - dragFromIndex)) {
      from += sa;
    }
    dragLastIndex = row;
    row = Math.min(from, last);
    last = Math.max(from, last);
    for (; row <= last; row++) {
      if (row != dragFromIndex) {
        reverseSelected(row);
      }
    }
    makeLeadSelected(dragLastIndex);
  }

  private void reverseSelected(int row) {
    if (isSelectedIndex(row)) {
      removeSelectionInterval(row, row);
    } else {
      addSelectionInterval(row, row);
    }
  }

  private void makeLeadSelected(int row) {
    if (isSelectedIndex(row)) {
      addSelectionInterval(row, row);
    } else {
      removeSelectionInterval(row, row);
    }
  }

  /**
   * Override to redefined the operation of selection in dragging.
   */
  @Override
  public void setSelectionInterval(int anchor, int lead) {
    if (dragging) {
      draggedSelection(lead);
    } else {
      super.setSelectionInterval(anchor, lead);
    }
  }

  @Override
  public void updateUI() {
    LookAndFeelManager.installOrchidUI();
    super.updateUI();
  }

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
