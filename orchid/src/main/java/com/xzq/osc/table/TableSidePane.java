/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

import com.xzq.osc.JocTable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.UIManager;

/**
 *
 * @author zqxu
 */
public abstract class TableSidePane extends JComponent {

  protected JocTable table;
  private List<String> registeredProperties;
  private PropertyChangeListener tablePropertyListener;

  /**
   *
   */
  public TableSidePane() {
    super();
    initializeLocalVars();
    updateUI();
  }

  /**
   *
   */
  protected void initializeLocalVars() {
  }

  /**
   *
   * @return
   */
  public JocTable getTable() {
    return table;
  }

  /**
   *
   * @param table
   */
  public void setTable(JocTable table) {
    JocTable old = getTable();
    if (old != table) {
      if (old != null) {
        uninstall(old);
      }
      this.table = table;
      if (this.table != null) {
        install(table);
      }
      firePropertyChange("table", old, table);
    }
  }

  /**
   *
   * @param table
   */
  protected void install(JocTable table) {
  }

  /**
   *
   * @param table
   */
  protected void uninstall(JocTable table) {
    if (table != null && registeredProperties != null) {
      PropertyChangeListener l = getTablePropertyListener();
      for (String propertyName : registeredProperties) {
        table.removePropertyChangeListener(propertyName, l);
      }
      registeredProperties.clear();
      registeredProperties = null;
    }
  }

  @Override
  public boolean isValidateRoot() {
    return true;
  }

  /**
   *
   * @param propertyName
   */
  protected void registerTablePropertyListener(String propertyName) {
    if (table != null) {
      if (registeredProperties == null) {
        registeredProperties = new ArrayList<String>();
      }
      registeredProperties.add(propertyName);
      table.addPropertyChangeListener(propertyName, getTablePropertyListener());
    }
  }

  private PropertyChangeListener getTablePropertyListener() {
    if (tablePropertyListener == null) {
      tablePropertyListener = new TablePropertyListener();
    }
    return tablePropertyListener;
  }

  /**
   *
   */
  protected void resizeAndRepaint() {
    revalidate();
    repaint();
  }

  /**
   *
   * @param propertyName
   */
  protected void tablePropertyChange(String propertyName) {
  }

  /**
   *
   */
  @Override
  public void updateUI() {
    setUI(UIManager.getUI(this));
  }

  private class TablePropertyListener implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      tablePropertyChange(evt.getPropertyName());
    }
  }
}
