/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * JocCoverPane display an overlay pane sits over the top of components,
 * intercept mouse events and prevent covered component gain focus.
 *
 * @author zqxu
 */
public class JocCoverPane implements OrchidAboutIntf {

  private AlphaPane alphaPane;
  private JPanel glassPane;
  private JLabel messageLabel;
  private JProgressBar progressBar;
  private JocBusyIcon busyIcon;
  private boolean busyIconVisible;
  private boolean progressVisible;
  private Component savedGlassPane;
  private JComponent coveredComponent;
  private Window coveredWindow;
  private boolean focusSaved;
  private Component savingFocus;
  private ComponentListener componentListener;
  private PropertyChangeListener managerListener;
  private PropertyChangeSupport changeSupport;

  public JocCoverPane() {
    initializeOverlayPanes();
    setMessageLabel(new JLabel());
    setProgressBar(new JProgressBar());
    setBusyIcon(new JocBusyIcon());
  }

  private void initializeOverlayPanes() {
    alphaPane = new AlphaPane();
    glassPane = new JPanel(null);
    glassPane.setOpaque(false);
    glassPane.add(alphaPane);
    changeSupport = new PropertyChangeSupport(this);
    alphaPane.addMouseListener(new MouseAdapter() {
    });
  }

  /**
   * display an overlay pane sits over the top of the component, the overlay
   * pane's size same as the component and track the component's size.
   *
   * @param component the component should be covered. must in JRootPane.
   */
  public void coverComponent(JComponent component) {
    coverComponent(component, null);
  }

  /**
   * display an overlay pane sits over the top of the component in rectangle
   * relative to the component.
   *
   * @param component the component should be covered. must in JRootPane.
   * @param rect cover rectangle relative to the component.
   */
  public void coverComponent(JComponent component, Rectangle rect) {
    if (component.getRootPane() == null) {
      throw new IllegalArgumentException(
              "can not cover component without root pane.");
    }
    removeCoverPane();
    JRootPane root = component.getRootPane();
    savedGlassPane = root.getGlassPane();
    coveredComponent = component;
    coveredWindow = SwingUtilities.getWindowAncestor(component);
    savingFocus = coveredWindow.getFocusOwner();
    focusSaved = savingFocus != null;
    adjustAlphaPaneBounds(rect);
    if (rect == null) {
      component.addComponentListener(getComponentListener());
    }
    root.setGlassPane(glassPane);
    root.getGlassPane().setVisible(true);
    FocusManager.getCurrentManager().addPropertyChangeListener(
            "focusOwner", getFocusManagerListener());
    clearCoveredFocus();
  }

  private void adjustAlphaPaneBounds(Rectangle rect) {
    if (rect == null) {
      rect = new Rectangle(coveredComponent.getSize());
    }
    Point pt = SwingUtilities.convertPoint(coveredComponent,
            rect.getLocation(), coveredComponent.getRootPane());
    alphaPane.setBounds(pt.x, pt.y, rect.width, rect.height);
  }

  private ComponentListener getComponentListener() {
    if (componentListener == null) {
      componentListener = new ComponentListener() {
        @Override
        public void componentResized(ComponentEvent e) {
          adjustAlphaPaneBounds(null);
        }

        @Override
        public void componentMoved(ComponentEvent e) {
          adjustAlphaPaneBounds(null);
        }

        @Override
        public void componentShown(ComponentEvent e) {
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }
      };
    }
    return componentListener;
  }

  private PropertyChangeListener getFocusManagerListener() {
    if (managerListener == null) {
      managerListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          clearCoveredFocus();
        }
      };
    }
    return managerListener;
  }

  private void clearCoveredFocus() {
    FocusManager currentManager = FocusManager.getCurrentManager();
    Component focus = currentManager.getFocusOwner();
    if (focus == null || focus == alphaPane
            || SwingUtilities.getWindowAncestor(focus) != coveredWindow) {
      return;
    }
    if (!isCoveredComponent(focus)) {
      focusSaved = true;
      savingFocus = null;
      return;
    }
    if (!focusSaved) {
      focusSaved = true;
      savingFocus = focus;
    }
    Component from = focus, next = focus;
    do {
      focus = next;
      next = currentManager.getDefaultFocusTraversalPolicy().
              getComponentAfter(coveredWindow, focus);
    } while (next != from && isCoveredComponent(next));
    if (next == from) {
      alphaPane.requestFocusInWindow();
    } else {
      currentManager.focusNextComponent(focus);
    }
  }

  private boolean isCoveredComponent(Component component) {
    Rectangle rect = new Rectangle(component.getSize());
    rect.setLocation(SwingUtilities.convertPoint(
            component, rect.getLocation(), alphaPane));
    return new Rectangle(alphaPane.getSize()).contains(rect);
  }

  /**
   * remove the overlay pane sits over the top of component. set current value
   * of the progress bar to zero.
   */
  public void removeCoverPane() {
    if (coveredComponent != null) {
      boolean v = savedGlassPane.isVisible();
      JRootPane root = coveredComponent.getRootPane();
      root.setGlassPane(savedGlassPane);
      root.getGlassPane().setVisible(v);
      savedGlassPane = null;
      coveredWindow = null;
      setProgressValue(0);
      FocusManager.getCurrentManager().removePropertyChangeListener(
              "focusOwner", getFocusManagerListener());
      if (focusSaved && savingFocus != null) {
        savingFocus.requestFocusInWindow();
        focusSaved = false;
        savingFocus = null;
      }
      coveredComponent.removeComponentListener(getComponentListener());
      coveredComponent = null;
    }
  }

  /**
   * Returns background color of the overlay pane.
   *
   * @return background
   */
  public Color getBackground() {
    return alphaPane.getBackground();
  }

  /**
   * Sets background color of the overlay pane.
   *
   * @param background background
   */
  public void setBackground(Color background) {
    Color old = getBackground();
    if (!old.equals(background)) {
      alphaPane.setBackground(background);
      changeSupport.firePropertyChange("background", old, background);
    }
  }

  /**
   * Returns alpha blend value of the overlay pane. between 0 and 1.
   *
   * @return alpha blend value
   */
  public float getAlphaBlend() {
    return alphaPane.alphaBlend;
  }

  /**
   * Sets alpha blend value of the overlay pane.
   *
   * @param alphaBlend alpha blend value
   */
  public void setAlphaBlend(float alphaBlend) {
    float old = alphaPane.alphaBlend;
    if (old != alphaBlend) {
      alphaPane.alphaBlend = alphaBlend;
      if (coveredComponent != null) {
        alphaPane.repaint();
      }
      changeSupport.firePropertyChange("alphaBlend", old, alphaBlend);
    }
  }

  /**
   * Returns the message label in the overlay pane.
   *
   * @return message label
   */
  public JLabel getMessageLabel() {
    return messageLabel;
  }

  /**
   * Sets the message label in the overlay pane.
   *
   * @param messageLabel message label, can not be null.
   */
  public void setMessageLabel(JLabel messageLabel) {
    if (messageLabel == null) {
      throw new IllegalArgumentException("messageLabel can not be null!");
    }
    JLabel old = this.messageLabel;
    if (old != messageLabel) {
      this.messageLabel = messageLabel;
      if (old != null) {
        alphaPane.remove(old);
      }
      if (busyIconVisible) {
        messageLabel.setIcon(busyIcon);
      } else {
        messageLabel.setIcon(null);
      }
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(0, 0, 8, 0);
      alphaPane.add(messageLabel, gbc);
      changeSupport.firePropertyChange("messageLabel", old, messageLabel);
    }
  }

  /**
   * Returns text of the message label.
   *
   * @return text
   */
  public String getMessageText() {
    return messageLabel.getText();
  }

  /**
   * Sets text of the message label.
   *
   * @param messageText text
   */
  public void setMessageText(String messageText) {
    messageLabel.setText(messageText);
  }

  /**
   * Returns progress bar in the overlay pane.
   *
   * @return progress bar
   */
  public JProgressBar getProgressBar() {
    return progressBar;
  }

  /**
   * Sets progress bar in the overlay pane.
   *
   * @param progressBar progress bar. can not be null.
   */
  public void setProgressBar(JProgressBar progressBar) {
    if (progressBar == null) {
      throw new IllegalArgumentException("progressBar can not be null!");
    }
    JProgressBar old = this.progressBar;
    if (old != progressBar) {
      this.progressBar = progressBar;
      if (old != null) {
        alphaPane.remove(old);
      }
      progressBar.setVisible(progressVisible);
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridy = 1;
      alphaPane.add(progressBar, gbc);
      changeSupport.firePropertyChange("progressBar", old, progressBar);
    }
  }

  /**
   * Returns minimum value of the progress bar.
   *
   * @return minimum value
   */
  public int getProgressMinimum() {
    return progressBar.getMinimum();
  }

  /**
   * Sets minimum value of the progress bar.
   *
   * @param minimum minimum value
   */
  public void setProgressMinimum(int minimum) {
    progressBar.setMinimum(minimum);
  }

  /**
   * Returns maximum value of the progress bar.
   *
   * @return maximum value
   */
  public int getProgressMaximum() {
    return progressBar.getMaximum();
  }

  /**
   * Sets maximum value of the progress bar.
   *
   * @param maximum maximum value
   */
  public void setProgressMaximum(int maximum) {
    progressBar.setMaximum(maximum);
  }

  /**
   * Returns current value of the progress bar.
   *
   * @return current value.
   */
  public int getProgressValue() {
    return progressBar.getValue();
  }

  /**
   * Sets current value of the progress bar.
   *
   * @param value current value.
   */
  public void setProgressValue(int value) {
    progressBar.setValue(value);
  }

  /**
   * Returns the value of the indeterminate property of the progress bar.
   *
   * @return value of the indeterminate property
   */
  public boolean isProgressIndeterminate() {
    return progressBar.isIndeterminate();
  }

  /**
   * Sets the indeterminate property of the progress bar, which determines
   * whether the progress bar is in determinate or indeterminate mode.
   *
   * @param indeterminate true if the progress bar should change to
   * indeterminate mode; false if it should revert to normal.
   */
  public void setProgressIndeterminate(boolean indeterminate) {
    progressBar.setIndeterminate(indeterminate);
  }

  /**
   * Returns the busy icon in the overlay pane.
   *
   * @return busy icon
   */
  public JocBusyIcon getBusyIcon() {
    return busyIcon;
  }

  /**
   * Sets the busy icon in the overlay pane.
   *
   * @param busyIcon busy icon
   */
  public void setBusyIcon(JocBusyIcon busyIcon) {
    if (busyIcon == null) {
      throw new IllegalArgumentException("busyIcon can not be null!");
    }
    JocBusyIcon old = this.busyIcon;
    if (old != busyIcon) {
      this.busyIcon = busyIcon;
      if (busyIconVisible) {
        messageLabel.setIcon(busyIcon);
      }
      changeSupport.firePropertyChange("busyIcon", old, busyIcon);
    }
  }

  /**
   * Returns whether the busy icon should be visible.
   *
   * @return true for visible and false or invisible.
   */
  public boolean isBusyIconVisible() {
    return busyIconVisible;
  }

  /**
   * Sets whether the busy icon should be visible.
   *
   * @param busyIconVisible true for visible and false or invisible.
   */
  public void setBusyIconVisible(boolean busyIconVisible) {
    boolean old = this.busyIconVisible;
    if (old != busyIconVisible) {
      this.busyIconVisible = busyIconVisible;
      if (busyIconVisible) {
        busyIcon.setBusy(true);
        messageLabel.setIcon(busyIcon);
      } else {
        busyIcon.setBusy(false);
        messageLabel.setIcon(null);
      }
      changeSupport.firePropertyChange("busyIconVisible", old, busyIconVisible);
    }
  }

  /**
   * Returns whether the progress bar should be visible.
   *
   * @return true for visible and false or invisible.
   */
  public boolean isProgressVisible() {
    return progressVisible;
  }

  /**
   * Sets whether the progress bar should be visible.
   *
   * @param progressVisible true for visible and false or invisible.
   */
  public void setProgressVisible(boolean progressVisible) {
    boolean old = this.progressVisible;
    if (old != progressVisible) {
      this.progressVisible = progressVisible;
      progressBar.setVisible(progressVisible);
      changeSupport.firePropertyChange("progressVisible", old, progressVisible);
    }
  }

  /**
   * Returns the covered component, if no covered component, return null.
   *
   * @return the covered component
   */
  public JComponent getCoveredComponent() {
    return coveredComponent;
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

  private static class AlphaPane extends JComponent {

    private float alphaBlend = 0.6f;

    public AlphaPane() {
      super();
      setLayout(new GridBagLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      Composite savedComposite = g2.getComposite();
      g2.setColor(getBackground());
      g2.setComposite(AlphaComposite.getInstance(
              AlphaComposite.SRC_OVER, alphaBlend));
      g2.fillRect(0, 0, getWidth(), getHeight());
      g2.setComposite(savedComposite);
    }
  }
}
