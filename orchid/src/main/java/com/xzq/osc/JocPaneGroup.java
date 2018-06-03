/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;

/**
 *
 * @author zqxu
 */
public class JocPaneGroup implements PropertyChangeListener, OrchidAboutIntf {

  private List<JocGroupPane> panes;
  private JocGroupPane expanedPane;
  private boolean allCollapseAllowed;

  /**
   *
   */
  public JocPaneGroup() {
    panes = new ArrayList<JocGroupPane>();
  }

  /**
   *
   * @param pane
   */
  public void add(JocGroupPane pane) {
    if (pane == null || panes.contains(pane)) {
      return;
    }
    panes.add(pane);
    pane.setPaneGroup(this);
    pane.addPropertyChangeListener("expanded", this);
    if (expanedPane == null && !allCollapseAllowed) {
      if (pane.isExpanded()) {
        paneStateChanged(pane, true);
      } else {
        pane.setExpanded(true);
      }
    } else if (expanedPane != null) {
      pane.setExpanded(false);
    }
  }

  /**
   *
   * @param pane
   */
  public void remove(JocGroupPane pane) {
    if (panes.contains(pane)) {
      panes.remove(pane);
      pane.setPaneGroup(null);
      pane.setFlexible(true);
      pane.removePropertyChangeListener("expanded", this);
      paneStateChanged(pane, false);
    }
  }

  /**
   * Remove all panes managed.
   */
  public void removeAll() {
    for (int i = panes.size(); i > 0;) {
      remove(panes.get(--i));
    }
  }

  /**
   *
   * @return
   */
  public JocGroupPane getExpanedPane() {
    return expanedPane;
  }

  /**
   *
   * @param pane
   */
  public void setExpanedPane(JocGroupPane pane) {
    if (pane == null) {
      if (!allCollapseAllowed) {
        if (expanedPane == null
                && panes.size() > 0) {
          panes.get(0).setExpanded(true);
        }
      } else if (expanedPane != null) {
        expanedPane.setExpanded(false);
      }
    } else {
      if (!panes.contains(pane)) {
        add(pane);
      }
      pane.setExpanded(true);
    }
  }

  private void paneStateChanged(JocGroupPane pane, Boolean expanded) {
    if (expanded) {
      JocGroupPane oldPane = this.expanedPane;
      this.expanedPane = pane;
      if (oldPane != null) {
        oldPane.setFlexible(true);
        oldPane.setExpanded(false);
      }
      pane.setFlexible(allCollapseAllowed);
    } else if (pane == expanedPane) {
      expanedPane = null;
      if (!allCollapseAllowed
              && panes.size() > 0) {
        panes.get(0).setExpanded(true);
      }
    }
  }

  public boolean isAllCollapseAllowed() {
    return allCollapseAllowed;
  }

  public void setAllCollapseAllowed(boolean allCollapseAllowed) {
    this.allCollapseAllowed = allCollapseAllowed;
    setExpanedPane(getExpanedPane());
    if (expanedPane != null) {
      expanedPane.setFlexible(this.allCollapseAllowed);
    }
  }

  /**
   *
   * @param evt
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    JocGroupPane pane = (JocGroupPane) evt.getSource();
    Boolean expanded = (Boolean) evt.getNewValue();
    paneStateChanged(pane, expanded);
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