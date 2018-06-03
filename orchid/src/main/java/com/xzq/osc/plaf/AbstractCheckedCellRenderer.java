/**
 * *****************************************************************************
 * 2013, All rights reserved.
 * *****************************************************************************
 */
package com.xzq.osc.plaf;

import com.xzq.osc.CheckState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicHTML;

/**
 * Description of AbstractCheckedCellRenderer.
 *
 * @author zqxu
 */
public abstract class AbstractCheckedCellRenderer extends JComponent {

  private Icon noneCheckedIcon = null;
  private Icon fullCheckedIcon = null;
  private Icon grayCheckedIcon = null;
  /**
   * label for display checked state icon.
   */
  protected JLabel checkedLabel = null;
  /**
   * label for display cell content.
   */
  protected JLabel contentLabel = null;

  /**
   * The constructor.
   */
  public AbstractCheckedCellRenderer() {
    super();
    setLayout(new BorderLayout(4, 0));
    checkedLabel = new JLabel();
    checkedLabel.setOpaque(false);
    contentLabel = new JLabel();
    contentLabel.setOpaque(false);
    add(checkedLabel, BorderLayout.LINE_START);
    add(contentLabel, BorderLayout.CENTER);
  }

  /**
   * Returns noneCheckedIcon.
   *
   * @return
   */
  public Icon getNoneCheckedIcon() {
    return noneCheckedIcon;
  }

  /**
   * Sets a value to attribute noneCheckedIcon.
   *
   * @param noneCheckedIcon
   */
  public void setNoneCheckedIcon(Icon noneCheckedIcon) {
    this.noneCheckedIcon = noneCheckedIcon;
  }

  /**
   * Returns fullCheckedIcon.
   *
   * @return
   */
  public Icon getFullCheckedIcon() {
    return fullCheckedIcon;
  }

  /**
   * Sets a value to attribute fullCheckedIcon.
   *
   * @param fullCheckedIcon
   */
  public void setFullCheckedIcon(Icon fullCheckedIcon) {
    this.fullCheckedIcon = fullCheckedIcon;
  }

  /**
   * Returns grayCheckedIcon.
   *
   * @return grayCheckedIcon
   */
  public Icon getGrayCheckedIcon() {
    return this.grayCheckedIcon;
  }

  /**
   * Sets a value to attribute grayCheckedIcon.
   *
   * @param grayCheckedIcon
   */
  public void setGrayCheckedIcon(Icon grayCheckedIcon) {
    this.grayCheckedIcon = grayCheckedIcon;
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    checkedLabel.setEnabled(enabled);
    contentLabel.setEnabled(enabled);
  }

  @Override
  public void setForeground(Color fg) {
    checkedLabel.setForeground(fg);
    contentLabel.setForeground(fg);
  }

  @Override
  public void setFont(Font font) {
    super.setFont(font);
    checkedLabel.setFont(font);
    contentLabel.setFont(font);
  }

  @Override
  public void setComponentOrientation(ComponentOrientation o) {
    super.setComponentOrientation(o);
    checkedLabel.setComponentOrientation(o);
    contentLabel.setComponentOrientation(o);
  }

  public Icon getCheckedIcon() {
    return checkedLabel.getIcon();
  }

  public Icon getContentIcon() {
    return contentLabel.getIcon();
  }

  public int getContentIconOffset() {
    int offset;
    Rectangle checked = checkedLabel.getBounds();
    Rectangle content = contentLabel.getBounds();
    if (getComponentOrientation().isLeftToRight()) {
      offset = content.x;
    } else {
      offset = checked.x + checked.width - content.width;
    }
    return offset;
  }

  public int getContentTextOffset() {
    int offset = getContentIconOffset();
    Icon icon = contentLabel.getIcon();
    return icon == null ? offset : offset + icon.getIconWidth()
            + contentLabel.getIconTextGap();
  }

  /**
   * Overridden for performance reasons.
   */
  @Override
  public boolean isValidateRoot() {
    return true;
  }

  /**
   * Overridden for performance reasons.
   */
  @Override
  public void repaint() {
  }

  /**
   * Overridden for performance reasons.
   */
  @Override
  public void repaint(long tm, int x, int y, int width, int height) {
  }

  /**
   * Overridden for performance reasons.
   */
  @Override
  public void repaint(Rectangle r) {
  }

  /**
   * Overridden for performance reasons.
   */
  @Override
  protected void firePropertyChange(String propertyName,
          Object oldValue, Object newValue) {
    // Strings get interned...
    if (("text".equals(propertyName) || ("font".equals(propertyName)
            || "foreground".equals(propertyName)) && oldValue != newValue
            && getClientProperty(BasicHTML.propertyKey) != null)) {
      super.firePropertyChange(propertyName, oldValue, newValue);
    }
  }

  /**
   * Overridden for performance reasons.
   */
  @Override
  public void firePropertyChange(String propertyName,
          byte oldValue, byte newValue) {
  }

  /**
   * Overridden for performance reasons.
   */
  @Override
  public void firePropertyChange(String propertyName,
          char oldValue, char newValue) {
  }

  /**
   * Overridden for performance reasons.
   */
  @Override
  public void firePropertyChange(String propertyName,
          short oldValue, short newValue) {
  }

  /**
   * Overridden for performance reasons.
   */
  @Override
  public void firePropertyChange(String propertyName,
          int oldValue, int newValue) {
  }

  /**
   * Overridden for performance reasons.
   */
  @Override
  public void firePropertyChange(String propertyName,
          long oldValue, long newValue) {
  }

  /**
   * Overridden for performance reasons.
   */
  @Override
  public void firePropertyChange(String propertyName,
          float oldValue, float newValue) {
  }

  /**
   * Overridden for performance reasons.
   */
  @Override
  public void firePropertyChange(String propertyName,
          double oldValue, double newValue) {
  }

  /**
   * Overridden for performance reasons.
   */
  @Override
  public void firePropertyChange(String propertyName,
          boolean oldValue, boolean newValue) {
  }

  /**
   * update selected icon
   *
   * @param state
   */
  protected void updateCheckedIcon(CheckState state) {
    Icon icon = getNoneCheckedIcon();
    if (state == CheckState.CHECKED) {
      icon = getFullCheckedIcon();
      if (icon == null) {
        icon = UIManager.getIcon(OrchidDefaults.FULL_CHECKED_ICON);
      }
    } else if (state == CheckState.PARTIAL) {
      icon = getGrayCheckedIcon();
      if (icon == null) {
        icon = UIManager.getIcon(OrchidDefaults.GRAY_CHECKED_ICON);
      }
    } else if (icon == null) {
      icon = UIManager.getIcon(OrchidDefaults.NONE_CHECKED_ICON);
    }
    checkedLabel.setIcon(icon);
  }
}
