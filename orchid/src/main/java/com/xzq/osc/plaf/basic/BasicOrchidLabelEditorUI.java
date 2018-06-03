/*
 * To change this template, choose Tools | Templates
 * and open the template in the container.
 */
package com.xzq.osc.plaf.basic;

import com.xzq.osc.JocLabelEditor;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author zqxu
 */
public class BasicOrchidLabelEditorUI extends ComponentUI {

  protected JocLabelEditor labelEditor;
  protected LayoutManager layout;
  protected FocusListener focusListener;

  public static BasicOrchidLabelEditorUI createUI(JComponent c) {
    return new BasicOrchidLabelEditorUI();
  }

  /**
   *
   * @param c
   * @param width
   * @param height
   * @return
   */
  @Override
  public int getBaseline(JComponent c, int width, int height) {
    if (labelEditor.getEditor() != null) {
      return labelEditor.getEditor().getBaseline(width, height);
    } else {
      return labelEditor.getLabel().getBaseline(width, height);
    }
  }

  /**
   * paint component
   *
   * @param g graphic object
   * @param c component
   */
  @Override
  public void paint(Graphics g, JComponent c) {
    //g.clearRect(0, 0, labelEditor.getWidth(), labelEditor.getHeight());
    Component editor = labelEditor.getEditor();
    if (editor == null || !labelEditor.isDrawGuideLine()
            || labelEditor.getLabelPlacement() == SwingConstants.TOP) {
      return;
    }
    Component container = labelEditor.getEditorContainer();
    Insets insets = labelEditor.getInsets();
    int y = container.getY() + container.getHeight() - 1;
    g.setColor(Color.LIGHT_GRAY);
    g.drawLine(insets.left, y, container.getX(), y);
  }

  /**
   * install ui for component
   *
   * @param c component
   */
  @Override
  public void installUI(JComponent c) {
    LookAndFeel.installColorsAndFont(c, "Panel.background",
            "Panel.foreground", "Panel.font");
    labelEditor = (JocLabelEditor) c;
    labelEditor.setLayout(getLayout());
    labelEditor.addFocusListener(getFocusListener());
  }

  private LayoutManager getLayout() {
    if (layout == null) {
      layout = new LabelEditorLayout();
    }
    return layout;
  }

  private FocusListener getFocusListener() {
    return focusListener == null
            ? focusListener = new LabelEditorFocusListener() : focusListener;
  }

  /**
   * Returns preferred size for component
   *
   * @param c component
   * @return preferred size
   */
  @Override
  public Dimension getPreferredSize(JComponent c) {
    return getContainerSize(true);
  }

  /**
   * Returns minimum size by label and container minimum size.
   *
   * @param c component
   * @return minimum size
   */
  @Override
  public Dimension getMinimumSize(JComponent c) {
    return getContainerSize(false);
  }

  // caculate labelEditor preferred size when preferred is true or minimum size else.
  private Dimension getContainerSize(boolean preferred) {
    Insets insets = labelEditor.getInsets();
    Dimension size = new Dimension(getEditorLeft(preferred) + insets.right,
            insets.top + insets.bottom);
    Dimension labelSize = preferred ? labelEditor.getLabel().getPreferredSize()
            : labelEditor.getLabel().getMinimumSize();
    Component container = labelEditor.getEditorContainer();
    Dimension editorSize = preferred
            ? container.getPreferredSize() : container.getMinimumSize();
    if (labelEditor.getLabelPlacement() == SwingConstants.TOP) {
      size.width += Math.max(labelSize.width, editorSize.width);
      size.height += labelSize.height + labelEditor.getLabelEditorGap()
              + editorSize.height;
    } else {
      size.width += editorSize.width;
      size.height += editorSize.height;
      if (editorSize.height == 0) {
        size.height += labelSize.height;
      }
    }
    return size;
  }

  /**
   * caculate space between container component and labelEditor left size.
   *
   * @param preferred use preferred size when true of minimum size when false.
   * @return space left to container component
   */
  protected int getEditorLeft(boolean preferred) {
    Insets insets = labelEditor.getInsets();
    JComponent root = getAlignmentRoot();
    int columns = labelEditor.getColumnsLeftRoot();
    if (columns == 0) {
      if (labelEditor.getLabelPlacement() == SwingConstants.TOP) {
        return insets.left;
      } else {
        return insets.left + labelEditor.getLabelEditorGap()
                + (preferred ? labelEditor.getLabel().getPreferredSize().width
                : labelEditor.getLabel().getMinimumSize().width);
      }
    } else {
      int width = columns * labelEditor.getColumnWidth();
      Point pt = SwingUtilities.convertPoint(labelEditor, 0, 0, root);
      pt.x = width - pt.x;
      return pt.x > insets.left ? pt.x : insets.left;
    }
  }

  //return alignment root component
  private JComponent getAlignmentRoot() {
    JComponent root;
    if (labelEditor.isAlignToRootPane()) {
      root = labelEditor.getRootPane();
    } else {
      root = labelEditor.getAlignmentRoot();
    }
    return root == null || !root.isAncestorOf(labelEditor) ? labelEditor : root;
  }

  /**
   * uninstall UI for component c
   *
   * @param c component
   */
  @Override
  public void uninstallUI(JComponent c) {
    labelEditor.setLayout(null);
    labelEditor = null;
  }

  /**
   * layout label and container component
   */
  protected void layoutLabelAndEditor() {
    Insets insets = labelEditor.getInsets();
    int x = getEditorLeft(true), y, height;
    height = labelEditor.getHeight() - insets.top - insets.bottom;
    Dimension labelSize = labelEditor.getLabel().getPreferredSize();
    if (labelEditor.getLabelPlacement() == SwingConstants.TOP) {
      y = insets.top;
      labelSize.width = labelEditor.getWidth() - insets.left - insets.right;
    } else {
      y = insets.top + (height - labelSize.height) / 2;
      labelSize.width = x - labelEditor.getLabelEditorGap();
    }
    layoutComponent(labelEditor.getLabel(), insets.left, y, labelSize, false);
    Component container = labelEditor.getEditorContainer();
    Dimension editorSize = container.getPreferredSize();
    if (labelEditor.getLabelPlacement() == SwingConstants.TOP) {
      y += labelSize.height + labelEditor.getLabelEditorGap();
    } else {
      y = insets.top + (height - editorSize.height) / 2;
    }
    layoutComponent(container, x, y, editorSize,
            labelEditor.isStretchEditorToFill());
  }

  /**
   * layout component according specified x,y and size
   *
   * @param c component to layout
   * @param x layout x
   * @param y layout y
   * @param size layout size
   * @param stretch true for stretch component or false not
   */
  protected void layoutComponent(Component c, int x, int y, Dimension size,
          boolean stretch) {
    Insets insets = labelEditor.getInsets();
    int width = labelEditor.getWidth() - x - insets.right;
    int height = labelEditor.getHeight() - y - insets.bottom;
    if (size.width > width || stretch) {
      size.width = width;
    }
    if (size.height > height) {
      size.height = height;
    }
    c.setBounds(x, y, size.width, size.height);
  }

  private class LabelEditorLayout implements LayoutManager {

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
      layoutLabelAndEditor();
    }
  }

  private class LabelEditorFocusListener implements FocusListener {

    @Override
    public void focusGained(FocusEvent e) {
      if (labelEditor.getEditor() != null) {
        labelEditor.getEditor().requestFocus();
      }
    }

    @Override
    public void focusLost(FocusEvent e) {
    }
  }
}
