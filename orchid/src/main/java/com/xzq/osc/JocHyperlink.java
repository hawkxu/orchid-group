/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.plaf.LookAndFeelManager;
import java.awt.Color;
import java.net.URI;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * Component to display a hyper link .
 *
 * @author zqxu
 */
public class JocHyperlink extends JButton implements OrchidAboutIntf {

  private static final String uiClassID = "OrchidHyperLinkUI";
  private Color unvisitColor = new Color(0, 50, 200);
  private Color rolloverColor = new Color(255, 50, 50);
  private Color activeColor = new Color(200, 50, 50);
  private Color visitedColor = new Color(150, 0, 50);

  /**
   *
   */
  public JocHyperlink() {
    super();
  }

  /**
   *
   * @param action
   */
  public JocHyperlink(Action action) {
    super(action);
  }

  /**
   * Returns color for state active. defaults is [200, 50, 50].
   *
   * @return color for state active.
   */
  public Color getActiveColor() {
    return activeColor;
  }

  /**
   * Set color for state active.
   *
   * @param activeColor color for state active.
   */
  public void setActiveColor(Color activeColor) {
    Color old = this.activeColor;
    if (!OrchidUtils.equals(old, activeColor)) {
      this.activeColor = activeColor;
      repaint();
      firePropertyChange("activeColor", old, activeColor);
    }
  }

  /**
   * Returns color for state rollover. defaults is [255, 50, 50].
   *
   * @return color for state rollover.
   */
  public Color getRolloverColor() {
    return rolloverColor;
  }

  /**
   * Set color for state rollover.
   *
   * @param rolloverColor color for state rollover.
   */
  public void setRolloverColor(Color rolloverColor) {
    Color old = this.rolloverColor;
    if (!OrchidUtils.equals(old, rolloverColor)) {
      this.rolloverColor = rolloverColor;
      repaint();
      firePropertyChange("rolloverColor", old, rolloverColor);
    }
  }

  /**
   * Returns color for state unvisit. defaults is [0, 50, 200].
   *
   * @return color for state unvisit.
   */
  public Color getUnvisitColor() {
    return unvisitColor;
  }

  /**
   * Set color for state unvisit.
   *
   * @param unvisitColor color for state unvisit.
   */
  public void setUnvisitColor(Color unvisitColor) {
    Color old = this.unvisitColor;
    if (!OrchidUtils.equals(old, unvisitColor)) {
      this.unvisitColor = unvisitColor;
      repaint();
      firePropertyChange("unvisitColor", old, unvisitColor);
    }
  }

  /**
   * Returns color for state visited. defaults is [150, 0, 50].
   *
   * @return color for state visited.
   */
  public Color getVisitedColor() {
    return visitedColor;
  }

  /**
   * Set color for state visited.
   *
   * @param visitedColor color for state visited.
   */
  public void setVisitedColor(Color visitedColor) {
    Color old = this.visitedColor;
    if (!OrchidUtils.equals(old, visitedColor)) {
      this.visitedColor = visitedColor;
      repaint();
      firePropertyChange("visitedColor", old, visitedColor);
    }
  }

  /**
   * Returns link target of associated action.
   *
   * @return link target.
   */
  public URI getLinkTarget() {
    Action action = getAction();
    return action instanceof JocHyperlinkAction
            ? ((JocHyperlinkAction) action).getLinkTarget() : null;
  }

  /**
   * Set link target of associated action. if no action associated, then a new
   * action will be created and associated.
   *
   * @param linkTarget link target.
   */
  public void setLinkTarget(URI linkTarget) {
    Action action = getAction();
    if (action instanceof JocHyperlinkAction) {
      ((JocHyperlinkAction) action).setLinkTarget(linkTarget);
    } else {
      setAction(new JocHyperlinkAction(linkTarget, getText()));
    }
  }

  /**
   * Returns link visit state.
   *
   * @return visit state.
   */
  public boolean isVisited() {
    Action action = getAction();
    return action instanceof JocHyperlinkAction
            && ((JocHyperlinkAction) action).isVisited();
  }

  /**
   *
   * @return
   */
  @Override
  public Color getForeground() {
    if (model.isArmed() && model.isPressed()) {
      return getActiveColor();
    } else if (model.isRollover()) {
      return getRolloverColor();
    } else if (isVisited()) {
      return getVisitedColor();
    } else {
      return getUnvisitColor();
    }
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
   */
  @Override
  public void updateUI() {
    LookAndFeelManager.installOrchidUI();
    super.updateUI();
  }

  /**
   *
   * @param a
   */
  @Override
  protected void configurePropertiesFromAction(Action a) {
    super.configurePropertiesFromAction(a);
    if (a instanceof JocHyperlinkAction) {
      setEnabled(((JocHyperlinkAction) a).isEnabled()
              && ((JocHyperlinkAction) a).isTargetAccessible());
    }
  }

  /**
   *
   * @param a
   * @param propertyName
   */
  @Override
  protected void actionPropertyChanged(Action a, String propertyName) {
    super.actionPropertyChanged(a, propertyName);
    if (a instanceof JocHyperlinkAction) {
      if (propertyName.equals(JocHyperlinkAction.LINK_VISITED)) {
        repaint();
      }
      setEnabled(((JocHyperlinkAction) a).isEnabled()
              && ((JocHyperlinkAction) a).isTargetAccessible());
    }
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
