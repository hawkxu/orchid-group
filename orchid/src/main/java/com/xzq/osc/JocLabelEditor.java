/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.plaf.LookAndFeelManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

/**
 * A container of label and editor, for layout use. JocLabelEditor accept one
 * JLabel component and vary other components, JLabel object mapped as label and
 * other object mapped as editor, only first visibility editor be displayed.
 *
 * @author zqxu
 */
public class JocLabelEditor extends JComponent implements SwingConstants,
        OrchidAboutIntf {

  private static final String uiClassId = "OrchidLabelEditorUI";
  private JLabel label;
  private EditorContainer editorContainer;
  private int labelPlacement = LEFT;
  private int labelEditorGap = 4;
  private boolean stretchEditorToFill = false;
  private boolean focusOnClickLabel = true;
  private MouseListener labelClickAction;
  private boolean drawGuideLine = true;
  private int columnsLeftRoot = 0;
  private int columnWidth = 0;
  private boolean alignToRootPane;
  private JComponent alignmentRoot = this;

  // <editor-fold defaultstate="collapsed" desc="Constructor and Initializer...">
  /**
   * Constructor of JocLabelEditor
   */
  public JocLabelEditor() {
    super();
    initLocalVars();
    updateUI();
  }

  /**
   * initialize label and editor container.
   */
  protected void initLocalVars() {
    add(label = new JLabel("Label"));
    add(editorContainer = new EditorContainer());
  }

  /**
   * @see JComponent#getUIClassID()
   * @return UI Class ID
   */
  @Override
  public String getUIClassID() {
    return uiClassId;
  }

  /**
   * @see JComponent#updateUI()
   */
  @Override
  public void updateUI() {
    LookAndFeelManager.installOrchidUI();
    setUI(UIManager.getUI(this));
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Manage Label and Editor...">
  /**
   * Returns current label component, if current no label, then create a JLabel
   * with text <b>Label</b> and return.
   *
   * @return label component
   */
  public JLabel getLabel() {
    return label;
  }

  /**
   * Return container for editor component, this is container delegate for using
   * in NetBeans or using for manual add editor component.
   *
   * @return container for editor component.
   */
  public Container getEditorContainer() {
    return editorContainer;
  }

  /**
   * Returns current editor component, if current no editor, return null.
   *
   * @return editor component.
   */
  public Component getEditor() {
    return editorContainer.getEditor();
  }

  // process label component
  private void configureLabel() {
    Component editor = getEditor();
    if (editor != null) {
      label.setLabelFor(getEditor());
      label.addMouseListener(getLabelClickAction());
    } else {
      label.setLabelFor(null);
      label.removeMouseListener(getLabelClickAction());
    }
  }

  // return label click action
  private MouseListener getLabelClickAction() {
    if (labelClickAction == null) {
      labelClickAction = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          labelClicked(e);
        }
      };
    }
    return labelClickAction;
  }

  /**
   * Returns flag of label click process, true for focus editor and false for no
   * action, defaults is true.
   *
   * @return flag of label click process.
   */
  public boolean getFocusOnClickLabel() {
    return focusOnClickLabel;
  }

  /**
   * Sets action when label clicked.
   *
   * @param focusOnClickLabel true for focus editor and false no action.
   */
  public void setFocusOnClickLabel(boolean focusOnClickLabel) {
    this.focusOnClickLabel = focusOnClickLabel;
  }

  //process label click event
  private void labelClicked(MouseEvent e) {
    Component editor = getEditor();
    if (getFocusOnClickLabel() && editor != null
            && editor.isVisible() && editor.isEnabled()) {
      editor.requestFocus();
    }
  }

  /**
   * When parent is not JViewport, return true to prevent validate message
   * transmit to parent.
   */
  @Override
  public boolean isValidateRoot() {
    Component parent = getParent();
    if (parent instanceof JViewport) {
      return false;
    }
    return true;
  }

  /**
   * Sets label and editor enabled state to match component state.
   *
   * @param enabled
   */
  @Override
  public void setEnabled(boolean enabled) {
    getLabel().setEnabled(enabled);
    if (getEditor() != null) {
      getEditor().setEnabled(enabled);
    }
    super.setEnabled(enabled);
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="layout control...">
  /**
   * Returns label placement, SwingConstants.LEFT or SwingConstants.TOP,
   * defaults is SwingConstants.Left.
   *
   * @return label placement
   */
  public int getLabelPlacement() {
    return labelPlacement;
  }

  /**
   * Sets label placement, SwingConstants.LEFT or SwingConstants.TOP, any other
   * value was mapped as SwingConstants.LEFT.
   *
   * @param labelPlacement label placement
   */
  public void setLabelPlacement(int labelPlacement) {
    if (labelPlacement != LEFT && labelPlacement != TOP) {
      throw new IllegalArgumentException("labelPlacement");
    }
    int old = this.labelPlacement;
    if (old != labelPlacement) {
      this.labelPlacement = labelPlacement;
      revalidate();
      firePropertyChange("labelPlacement", old, labelPlacement);
    }
  }

  /**
   * Returns space between label and editor, the actual space for display
   * affected by columnsLeftRoot property also.
   *
   * @return space between label and editor
   */
  public int getLabelEditorGap() {
    return labelEditorGap;
  }

  /**
   * Sets sapce between label and editor
   *
   * @param labelEditorGap space between label and editor
   */
  public void setLabelEditorGap(int labelEditorGap) {
    int old = this.labelEditorGap;
    if (old != labelEditorGap) {
      this.labelEditorGap = labelEditorGap;
      revalidate();
      firePropertyChange("labelEditorGap", old, labelEditorGap);
    }
  }

  /**
   * Returns true if strech editor to fill component or false not, defaults is
   * false.
   *
   * @return true for strech editor or else not.
   */
  public boolean isStretchEditorToFill() {
    return stretchEditorToFill;
  }

  /**
   * Sets whether strech editor to fill component area or not.
   *
   * @param stretchEditorToFill true for strech editor or else not.
   */
  public void setStretchEditorToFill(boolean stretchEditorToFill) {
    boolean old = this.stretchEditorToFill;
    if (old != stretchEditorToFill) {
      this.stretchEditorToFill = stretchEditorToFill;
      revalidate();
      firePropertyChange("stretchEditorToFill", old, stretchEditorToFill);
    }
  }

  /**
   * Returns true for draw guide line from label to editor or false not,
   * defaults is true.
   *
   * @return for for draw guide line or false not.
   */
  public boolean isDrawGuideLine() {
    return drawGuideLine;
  }

  /**
   * Sets whether draw guide line from label to editor or false not.
   *
   * @param drawGuideLine true for draw guide line or false not.
   */
  public void setDrawGuideLine(boolean drawGuideLine) {
    boolean old = this.drawGuideLine;
    if (old != drawGuideLine) {
      this.drawGuideLine = drawGuideLine;
      repaint();
      firePropertyChange("drawGuideLine", old, drawGuideLine);
    }
  }

  /**
   * Returns columns between editor and alignment root compoent (usually be
   * container), if this parameter value is not zero, editor should be placed at
   * specified location and label width should be shrink, defaults is 0.
   *
   * @return columns between editor and alignment root component.
   */
  public int getColumnsLeftRoot() {
    return columnsLeftRoot;
  }

  /**
   * Sets columns between editor and alignment root compoent.
   *
   * @param columnsLeftRoot columns between editor and alignment root component.
   */
  public void setColumnsLeftRoot(int columnsLeftRoot) {
    int old = this.columnsLeftRoot;
    if (old != columnsLeftRoot) {
      this.columnsLeftRoot = columnsLeftRoot;
      revalidate();
      firePropertyChange("columnsLeftRoot", old, columnsLeftRoot);
    }
  }

  /**
   * Returns true if use rootPane as editor alignment root or false use
   * alignmentRoot property linked component as alignment root, defaults is
   * false.
   *
   * @return true for use rootPane or false not.
   */
  public boolean isAlignToRootPane() {
    return alignToRootPane;
  }

  /**
   * Sets whether use rootPane as editor alignment root or false not.
   *
   * @param alignToRootPane true for use rootPane or false not.
   */
  public void setAlignToRootPane(boolean alignToRootPane) {
    boolean old = this.alignToRootPane;
    if (old != alignToRootPane) {
      this.alignToRootPane = alignToRootPane;
      if (getColumnsLeftRoot() != 0) {
        revalidate();
      }
      firePropertyChange("alignToRootPane", old, alignToRootPane);
    }
  }

  /**
   * Returns editor alignment root component, if this property is null, should
   * use container as alignment root, defaults is null.
   *
   * @return editor alignment root component.
   */
  public JComponent getAlignmentRoot() {
    return alignmentRoot;
  }

  /**
   * Sets editor alignment root component.
   *
   * @param alignmentRoot editor alignment root component.
   */
  public void setAlignmentRoot(JComponent alignmentRoot) {
    JComponent old = this.alignmentRoot;
    if (old != alignmentRoot) {
      this.alignmentRoot = alignmentRoot;
      revalidate();
      firePropertyChange("alignmentRoot", old, alignmentRoot);
    }
  }

  /**
   * Returns column width for editor alignment while columnsLeftRoot property is
   * not zero. If columnWidth is zero, then return character <b>m</b> width
   * using current FontMetrics, defaults is zero.
   *
   * @return column width
   */
  public int getColumnWidth() {
    if (columnWidth == 0) {
      FontMetrics fm = getFontMetrics(getFont());
      columnWidth = fm.stringWidth("m");
    }
    return columnWidth;
  }

  /**
   * Sets column width for editor alignment while columnsLeftRoot property is
   * not zero.
   *
   * @param columnWidth column width
   */
  public void setColumnWidth(int columnWidth) {
    int old = this.columnWidth;
    if (old != columnWidth) {
      this.columnWidth = columnWidth;
      revalidate();
      firePropertyChange("columnWidth", old, columnWidth);
    }
  }

  /**
   * Sets columnWidth to zero when new font specified for auto caculate column
   * width.
   *
   * @see JComponent#setFont(java.awt.Font)
   * @param font new font.
   */
  @Override
  public void setFont(Font font) {
    super.setFont(font);
    setColumnWidth(0);
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="label function shortcuts...">
  /**
   * Returns icon of label
   *
   * @see JLabel#getIcon()
   * @return icon of label
   */
  public Icon getLabelIcon() {
    return getLabel().getIcon();
  }

  /**
   * Sets icon of label
   *
   * @see JLabel#setIcon(javax.swing.Icon)
   * @param icon icon of label
   */
  public void setLabelIcon(Icon icon) {
    getLabel().setIcon(icon);
  }

  /**
   * Returns text of label
   *
   * @see JLabel#getText()
   * @return text of label
   */
  public String getLabelText() {
    return getLabel().getText();
  }

  /**
   * Sets text of label
   *
   * @see JLabel#setText(java.lang.String)
   * @param text text of label
   */
  public void setLabelText(String text) {
    getLabel().setText(text);
  }

  /**
   * Returns space between icon and text in label.
   *
   * @see JLabel#getIconTextGap()
   * @return space between icon and text in label.
   */
  public int getLabelIconTextGap() {
    return getLabel().getIconTextGap();
  }

  /**
   * Sets space between icon and text in label.
   *
   * @see JLabel#setIconTextGap(int)
   * @param iconTextGap space between icon and text in label.
   */
  public void setLabelIconTextGap(int iconTextGap) {
    getLabel().setIconTextGap(iconTextGap);
  }

  /**
   * Returns mnemonic char for label
   *
   * @see JLabel#getDisplayedMnemonic()
   * @return mnemonic char for label
   */
  public char getLabelDisplayedMnemonic() {
    return (char) getLabel().getDisplayedMnemonic();
  }

  /**
   * Sets mnemonic char for label
   *
   * @see JLabel#setDisplayedMnemonic(char)
   * @param aChar mnemonic char for label
   */
  public void setLabelDisplayedMnemonic(char aChar) {
    getLabel().setDisplayedMnemonic(aChar);
  }
  // </editor-fold>

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

  private static class EditorContainer extends JPanel {

    public EditorContainer() {
      super();
    }

    public Component getEditor() {
      Component editor = null;
      for (Component child : getComponents()) {
        if (child.isVisible()) {
          editor = child;
          break;
        }
      }
      return editor;
    }

    @Override
    public Dimension getPreferredSize() {
      Component editor = getEditor();
      return editor == null ? new Dimension(50, 20)
              : editor.getPreferredSize();
    }

    @Override
    public void doLayout() {
      ((JocLabelEditor) getParent()).configureLabel();
      Component editor = getEditor();
      if (editor == null) {
        return;
      }
      for (Component child : getComponents()) {
        child.setBounds(0, 0, 0, 0);
      }
      editor.setBounds(0, 0, getWidth(), getHeight());
    }
  }
}