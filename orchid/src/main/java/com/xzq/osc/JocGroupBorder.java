/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.print.PrintUtils;
import java.awt.*;
import javax.swing.JDialog;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author zqxu
 */
public class JocGroupBorder extends AbstractBorder
        implements OrchidAboutIntf, SwingConstants {

  private Insets innerMargin;
  private Insets outerMargin;
  private Insets titleMargin;
  private Color borderColor;
  private Color titleForeground;
  private Paint titlePaint;
  private int titleAlignment = LEADING;
  private String groupTitle;

  /**
   * Constructor of JocGroupBorder with all default properties.
   */
  public JocGroupBorder() {
    this("");
  }

  /**
   * Constructor of JocGroupBorder with specified title and all other default
   * properties.
   *
   * @param groupTitle title of group border.
   */
  public JocGroupBorder(String groupTitle) {
    titleMargin = new Insets(2, 3, 2, 3);
    borderColor = new Color(80, 80, 255);
    titleForeground = Color.BLACK;
    this.groupTitle = groupTitle;
  }

  /**
   * Returns inner space between border line and component, defaults is null.
   *
   * @return an insets object
   */
  public Insets getInnerMargin() {
    return innerMargin;
  }

  /**
   * Sets inner space between border and component.
   *
   * @param innerMargin an insets object
   */
  public void setInnerMargin(Insets innerMargin) {
    this.innerMargin = innerMargin;
  }

  /**
   * Returns outer space between border line and container, defaults is null.
   *
   * @return an insets object
   */
  public Insets getOuterMargin() {
    return outerMargin;
  }

  /**
   * Sets outer space between border line and container.
   *
   * @param outerMargin an insets object
   */
  public void setOuterMargin(Insets outerMargin) {
    this.outerMargin = outerMargin;
  }

  /**
   * Returns space between title text and title border, defaults is [2,3,2,3].
   *
   * @return an insets object
   */
  public Insets getTitleMargin() {
    return titleMargin;
  }

  /**
   * Sets space between title text and title border.
   *
   * @param titleMargin an insets object
   */
  public void setTitleMargin(Insets titleMargin) {
    this.titleMargin = titleMargin;
  }

  /**
   * Returns border line color, defaults is RGB 80, 80, 255
   *
   * @return border line color.
   */
  public Color getBorderColor() {
    return borderColor;
  }

  /**
   * Sets border line color.
   *
   * @param borderColor border line color.
   */
  public void setBorderColor(Color borderColor) {
    if (borderColor == null) {
      throw new IllegalArgumentException("Can not set null borderColor");
    }
    this.borderColor = borderColor;
  }

  /**
   * Returns foreground color for group title bar, defaults is black.
   *
   * @return color
   */
  public Color getTitleForeground() {
    return titleForeground;
  }

  /**
   * Sets foreground color for group title bar.
   *
   * @param titleForeground color.
   */
  public void setTitleForeground(Color titleForeground) {
    this.titleForeground = titleForeground;
  }

  /**
   * Returns title background painter, defaults is GradientPaint with Color
   * RGB(230,230,255) to RGB(130,130,255) and from point [0,0] to [0,20].
   *
   * @return title painter
   */
  public Paint getTitlePaint() {
    return titlePaint;
  }

  /**
   * Sets title backgroup painter
   *
   * @param titlePaint title painter
   */
  public void setTitlePaint(Paint titlePaint) {
    this.titlePaint = titlePaint;
  }

  /**
   * Returns the alignment of the border title along the X axis.
   *
   * @return The value of the titleAlignment property, one of the following
   * constants defined in
   * <code>SwingConstants</code>:
   * <code>LEFT</code>,
   * <code>CENTER</code>,
   * <code>RIGHT</code>,
   * <code>LEADING</code> or
   * <code>TRAILING</code>.
   */
  public int getTitleAlignment() {
    return titleAlignment;
  }

  /**
   * Sets the alignment of the border title along the X axis.
   *
   * @param titleAlignment One of the following constants defined in
   * <code>SwingConstants</code>:
   * <code>LEFT</code>,
   * <code>CENTER</code> (the default for image-only labels),
   * <code>RIGHT</code>,
   * <code>LEADING</code> (the default for text-only labels) or
   * <code>TRAILING</code>.
   */
  public void setTitleAlignment(int titleAlignment) {
    this.titleAlignment = OrchidUtils.checkHorizontalKey(titleAlignment,
            "titleAlignment");
  }

  /**
   * Returns title text
   *
   * @return title text
   */
  public String getGroupTitle() {
    return groupTitle;
  }

  /**
   * Sets title text
   *
   * @param groupTitle title text
   */
  public void setGroupTitle(String groupTitle) {
    this.groupTitle = groupTitle;
  }

  /**
   * Returns boder insets for component.
   *
   * @see AbstractBorder#getBorderInsets(java.awt.Component)
   * @param c component
   * @return border insets
   */
  @Override
  public Insets getBorderInsets(Component c) {
    Insets insets = new Insets(2, 1, 1, 1);
    insets = OrchidUtils.combineInsets(insets, getInnerMargin());
    insets = OrchidUtils.combineInsets(insets, getOuterMargin());
    insets.top += getTitleHeight(c);
    return insets;
  }

  //caculate and return title height
  private int getTitleHeight(Component c) {
    FontMetrics fm = c.getFontMetrics(c.getFont());
    int height = fm.getHeight();
    Insets margin = getTitleMargin();
    if (margin == null) {
      return height;
    } else {
      return height + margin.top + margin.bottom;
    }
  }

  /**
   * @see AbstractBorder#getBorderInsets(java.awt.Component, java.awt.Insets)
   * @param c component
   * @param insets border insets
   * @return border insets
   */
  @Override
  public Insets getBorderInsets(Component c, Insets insets) {
    Insets bi = getBorderInsets(c);
    insets.set(bi.top, bi.left, bi.bottom, bi.right);
    return insets;
  }

  /**
   * paint border for component.
   */
  @Override
  public void paintBorder(Component c, Graphics g, int x, int y,
          int width, int height) {
    Graphics2D g2 = (Graphics2D) g;
    Rectangle titleRect = new Rectangle(x, y, width - 1, height - 1);
    OrchidUtils.excludeMargin(titleRect, getOuterMargin());
    g2.setPaint(getBorderColor());
    g2.draw(titleRect);
    titleRect.height = getTitleHeight(c) + 1;
    g2.draw(titleRect);
    OrchidUtils.excludeMargin(titleRect, new Insets(1, 1, 0, 0));
    g2.setPaint(getPaintForTitle(titleRect.height));
    g2.fill(titleRect);
    OrchidUtils.excludeMargin(titleRect, getTitleMargin());
    g2.setPaint(getTitleForeground());
    int titleAlign = OrchidUtils.getOrientedHorizontal(c, titleAlignment);
    PrintUtils.drawText(g2, groupTitle, titleRect, null, titleAlign, true);
  }

  private Paint getPaintForTitle(int titleHeight) {
    Paint paint = getTitlePaint();
    return paint != null ? paint : createDefaultTitlePaint(titleHeight);
  }

  /**
   * Returns default paint for title area.
   *
   * @param titleHeight height of title area.
   * @return paint
   */
  protected Paint createDefaultTitlePaint(int titleHeight) {
    return new GradientPaint(0, 0, new Color(230, 230, 255),
            0, titleHeight, new Color(130, 130, 255));
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
