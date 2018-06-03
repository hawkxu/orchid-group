/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.plaf.basic;

import com.xzq.osc.JocRangeField;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author zqxu
 */
public class BasicOrchidRangeFieldUI extends ComponentUI {

  private LayoutManager layoutManager;
  private PropertyChangeListener propertyHandler;
  private FocusListener focusHandler;

  public static ComponentUI createUI(JComponent c) {
    return new BasicOrchidRangeFieldUI();
  }

  @Override
  public void installUI(JComponent c) {
    c.setLayout(getLayoutManager());
    c.addFocusListener(getFocusHandler());
    c.addPropertyChangeListener("enabled", getPropertyHandler());
    c.addPropertyChangeListener("focusable", getPropertyHandler());
    c.addPropertyChangeListener("font", getPropertyHandler());
  }

  @Override
  public void uninstallUI(JComponent c) {
    c.setLayout(null);
    c.removeFocusListener(getFocusHandler());
    c.removePropertyChangeListener("enabled", getPropertyHandler());
    c.removePropertyChangeListener("focusable", getPropertyHandler());
    c.removePropertyChangeListener("font", getPropertyHandler());
  }

  private LayoutManager getLayoutManager() {
    if (layoutManager == null) {
      layoutManager = new RangeFieldLayout();
    }
    return layoutManager;
  }

  public FocusListener getFocusHandler() {
    if (focusHandler == null) {
      focusHandler = new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
          fieldFocusGained(e);
        }

        @Override
        public void focusLost(FocusEvent e) {
        }
      };
    }
    return focusHandler;
  }

  private void fieldFocusGained(FocusEvent e) {
    Object field = e.getSource();
    if (field instanceof JocRangeField) {
      ((JocRangeField) field).getLowField().requestFocus();
    }
  }

  public PropertyChangeListener getPropertyHandler() {
    if (propertyHandler == null) {
      propertyHandler = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          fieldPropertyChange(evt);
        }
      };
    }
    return propertyHandler;
  }

  private void fieldPropertyChange(PropertyChangeEvent evt) {
    if (!(evt.getSource() instanceof JocRangeField)) {
      return;
    }
    JocRangeField field = (JocRangeField) evt.getSource();
    String propertyName = evt.getPropertyName();
    if (propertyName.equals("enabled")) {
      boolean enabled = field.isEnabled();
      field.getLowField().setEnabled(enabled);
      field.getHighField().setEnabled(enabled);
      field.getMultipleButton().setEnabled(enabled);
    } else if (propertyName.equals("focusable")) {
      boolean focusable = field.isFocusable();
      field.getLowField().setFocusable(focusable);
      field.getHighField().setFocusable(focusable);
    } else if (propertyName.equals("font")) {
      Font font = field.getFont();
      field.getLowField().setFont(font);
      field.getToLabel().setFont(font);
      field.getHighField().setFont(font);
    }
  }

  @Override
  public int getBaseline(JComponent c, int width, int height) {
    JocRangeField rangeField = (JocRangeField) c;
    return rangeField.getLowField().getBaseline(width, height);
  }

  @Override
  public Dimension getPreferredSize(JComponent c) {
    JocRangeField rangeField = (JocRangeField) c;
    Insets insets = rangeField.getInsets();
    Dimension opSize = rangeField.getOptionLabel().getPreferredSize();
    int optionEditorGap = rangeField.getOptionFieldGap();
    Dimension edSize = getEditorPreferredSize(rangeField);
    int width = opSize.width + optionEditorGap + edSize.width;
    int height = Math.max(opSize.height, edSize.height);
    if (rangeField.getRangeInterval() || rangeField.isKeepHighSpace()) {
      int toLabelGap = rangeField.getToLabelGap();
      Dimension toSize = rangeField.getToLabel().getPreferredSize();
      width += toSize.width + toLabelGap * 2 + edSize.width;
      height = Math.max(height, toSize.height);
    }
    if (rangeField.getMultipleRange()) {
      Dimension mbSize = rangeField.getMultipleButton().getPreferredSize();
      width += rangeField.getMultipleButtonGap() + mbSize.width;
      height = Math.max(height, mbSize.height);
    }
    width += insets.left + insets.right;
    height += insets.top + insets.bottom;
    return new Dimension(width, height);
  }

  private Dimension getEditorPreferredSize(JocRangeField field) {
    Dimension lowSize = field.getLowField().getPreferredSize();
    Dimension highSize = field.getHighField().getPreferredSize();
    return new Dimension(Math.max(lowSize.width, highSize.width),
            Math.max(lowSize.height, highSize.height));
  }

  protected void layoutRangeField(JocRangeField rangeField) {
    Insets insets = rangeField.getInsets();
    Dimension fullSize = rangeField.getSize();
    fullSize.height -= insets.top + insets.bottom;
    JLabel optionLabel = rangeField.getOptionLabel();
    JComponent lowEditor = rangeField.getLowField();
    JLabel toLabel = rangeField.getToLabel();
    JComponent highEditor = rangeField.getHighField();
    JButton multipleButton = rangeField.getMultipleButton();

    int optionEditorGap = rangeField.getOptionFieldGap();
    int toLabelGap = rangeField.getToLabelGap();
    int multipleButtonGap = rangeField.getMultipleButtonGap();
    Dimension opSize = optionLabel.getPreferredSize();
    Dimension toSize = toLabel.getPreferredSize();
    Dimension mbSize = multipleButton.getPreferredSize();
    Dimension edSize = getEditorPreferredSize(rangeField);
    boolean rangeInterval = rangeField.getRangeInterval();
    boolean keepHighSpace = rangeField.isKeepHighSpace();

    int x = insets.left;
    optionLabel.setBounds(x, insets.top, opSize.width, fullSize.height);
    x += opSize.width + optionEditorGap;
    int editorsWidth = fullSize.width - x - insets.right;
    editorsWidth -= multipleButtonGap + mbSize.width;
    if (rangeInterval || keepHighSpace) {
      editorsWidth = (editorsWidth - toLabelGap * 2 - toSize.width) / 2;
    }
    if (rangeField.isFieldWidthExtended() || edSize.width > editorsWidth) {
      edSize.width = editorsWidth;
    }
    int y = insets.top + (fullSize.height - edSize.height) / 2;
    lowEditor.setBounds(x, y, edSize.width, edSize.height);
    toLabel.setVisible(rangeInterval);
    highEditor.setVisible(rangeInterval);
    if (rangeInterval || keepHighSpace) {
      x += editorsWidth + toLabelGap;
      toLabel.setBounds(x, insets.top, toSize.width, fullSize.height);
      x += toSize.width + toLabelGap;
      highEditor.setBounds(x, y, edSize.width, edSize.height);
    }
    multipleButton.setVisible(rangeField.getMultipleRange());
    x += editorsWidth + multipleButtonGap;
    y = insets.top + (fullSize.height - mbSize.height) / 2;
    multipleButton.setBounds(x, y, mbSize.width, mbSize.height);
  }

  private class RangeFieldLayout implements LayoutManager {

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
      return parent.getPreferredSize();
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
      return parent.getMinimumSize();
    }

    @Override
    public void layoutContainer(Container parent) {
      layoutRangeField((JocRangeField) parent);
    }
  }
}
