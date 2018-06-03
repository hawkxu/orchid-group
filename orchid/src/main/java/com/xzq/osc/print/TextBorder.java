/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.print;

import java.awt.*;

/**
 * Use to paint string with border
 *
 * @author zqxu
 */
public class TextBorder {

  private Insets insets;
  private Color leftColor;
  private BasicStroke leftLine;
  private boolean drawLeft;
  private Color topColor;
  private BasicStroke topLine;
  private boolean drawTop;
  private Color rightColor;
  private BasicStroke rightLine;
  private boolean drawRight;
  private Color bottomColor;
  private BasicStroke bottomLine;
  private boolean drawBottom;

  /**
   * Constructor for TextBorder, all border line width is 1.
   */
  public TextBorder() {
    this(1, true);
  }

  /**
   * Constructor for TextBorder, all border line is visible.
   *
   * @param width border line width
   */
  public TextBorder(float width) {
    this(width, true);
  }

  /**
   * Constructor for TextBorder, with border line width and border line
   * visibility.
   *
   * @param width border line width
   * @param drawLine border line visibility
   */
  public TextBorder(float width, boolean drawLine) {
    insets = new Insets(1, 2, 1, 2);
    leftColor = topColor = rightColor = bottomColor = Color.BLACK;
    setAll(drawLine);
    if (width >= 0) {
      setAll(new BasicStroke(width));
    }
  }

  /**
   * Returns insets between border line and text (no border line width)
   *
   * @return insets
   */
  public Insets getInsets() {
    return insets;
  }

  /**
   * Returns text border insets (include border line width)
   *
   * @return insets
   */
  public Insets getTotalInsets() {
    Insets total = (Insets) insets.clone();
    total.left += leftLine == null ? 0 : leftLine.getLineWidth();
    total.top += topLine == null ? 0 : topLine.getLineWidth();
    total.right += rightLine == null ? 0 : rightLine.getLineWidth();
    total.bottom += bottomLine == null ? 0 : bottomLine.getLineWidth();
    return total;
  }

  /**
   * Set insets between border line and text (no border line width)
   *
   * @param insets insets
   */
  public void setInsets(Insets insets) {
    this.insets = insets;
  }

  /**
   * Resturns left border line color.
   *
   * @return left border line color.
   */
  public Color getLeftColor() {
    return leftColor;
  }

  /**
   * Set left border line color.
   *
   * @param leftColor left border line color.
   */
  public void setLeftColor(Color leftColor) {
    this.leftColor = leftColor != null ? leftColor : Color.BLACK;
  }

  /**
   * Returns left border line pen stroke.
   *
   * @return left border line pen stroke.
   */
  public BasicStroke getLeftLine() {
    return leftLine;
  }

  /**
   * Set left border line pen stroke, null for no left border line.
   *
   * @param leftLine
   */
  public void setLeftLine(BasicStroke leftLine) {
    this.leftLine = leftLine;
  }

  /**
   * Returns true if draw left border line or false not.
   *
   * @return true or false
   */
  public boolean getDrawLeft() {
    return drawLeft;
  }

  /**
   * Set whether draw left border line or not.
   *
   * @param drawLeft true or false
   */
  public void setDrawLeft(boolean drawLeft) {
    this.drawLeft = drawLeft;
  }

  /**
   * Resturns top border line color.
   *
   * @return top border line color.
   */
  public Color getTopColor() {
    return topColor;
  }

  /**
   * Set top border line color.
   *
   * @param topColor top border line color.
   */
  public void setTopColor(Color topColor) {
    this.topColor = topColor != null ? topColor : Color.BLACK;
  }

  /**
   * Returns top border line pen stroke.
   *
   * @return top border line pen stroke.
   */
  public BasicStroke getTopLine() {
    return topLine;
  }

  /**
   * Set top border line pen stroke, null for no top border line.
   *
   * @param topLine top border line pen stroke.
   */
  public void setTopLine(BasicStroke topLine) {
    this.topLine = topLine;
  }

  /**
   * Returns true if draw top border line or false not.
   *
   * @return true or false
   */
  public boolean getDrawTop() {
    return drawTop;
  }

  /**
   * Set whether draw top border line or not.
   *
   * @param drawTop true or false
   */
  public void setDrawTop(boolean drawTop) {
    this.drawTop = drawTop;
  }

  /**
   * Returns right border line color.
   *
   * @return right border line color.
   */
  public Color getRightColor() {
    return rightColor;
  }

  /**
   * Set right border line color.
   *
   * @param rightColor right border line color.
   */
  public void setRightColor(Color rightColor) {
    this.rightColor = rightColor != null ? rightColor : Color.BLACK;
  }

  /**
   * Returns right border line pen stroke.
   *
   * @return right border line pen stroke.
   */
  public BasicStroke getRightLine() {
    return rightLine;
  }

  /**
   * Set right border line pen stroke, null for no right border line.
   *
   * @param rightLine right border line pen stroke.
   */
  public void setRightLine(BasicStroke rightLine) {
    this.rightLine = rightLine;
  }

  /**
   * Returns true if draw right border line or false not.
   *
   * @return true or false
   */
  public boolean getDrawRight() {
    return drawRight;
  }

  /**
   * Set whether draw right border line or not.
   *
   * @param drawRight true or false
   */
  public void setDrawRight(boolean drawRight) {
    this.drawRight = drawRight;
  }

  /**
   * Returns bottom border line color.
   *
   * @return bottom border line color.
   */
  public Color getBottomColor() {
    return bottomColor;
  }

  /**
   * Set bottom border line color.
   *
   * @param bottomColor bottom border line color.
   */
  public void setBottomColor(Color bottomColor) {
    this.bottomColor = bottomColor != null ? bottomColor : Color.BLACK;
  }

  /**
   * Returns bottom border line pen stroke.
   *
   * @return bottom border line pen stroke.
   */
  public BasicStroke getBottomLine() {
    return bottomLine;
  }

  /**
   * Set bottom border line pen stroke, null for no bottom border line.
   *
   * @param bottomLine bottom border line pen stroke.
   */
  public void setBottomLine(BasicStroke bottomLine) {
    this.bottomLine = bottomLine;
  }

  /**
   * Returns true if draw bottom border line or false not.
   *
   * @return true or false
   */
  public boolean getDrawBottom() {
    return drawBottom;
  }

  /**
   * Set whether draw bottom border line or not.
   *
   * @param drawBottom true or false
   */
  public void setDrawBottom(boolean drawBottom) {
    this.drawBottom = drawBottom;
  }

  /**
   * Set border line color for both left and top.
   *
   * @param color color
   */
  public void setLeftTop(Color color) {
    setTopColor(color);
    setLeftColor(color);
  }

  /**
   * Set border line pen stroke for both left and top, null for no border line.
   *
   * @param stroke stroke
   */
  public void setLeftTop(BasicStroke stroke) {
    setTopLine(stroke);
    setLeftLine(stroke);
  }

  /**
   * Set whether draw border line or not for both left and top.
   *
   * @param drawBorder true or false
   */
  public void setLeftTop(boolean drawBorder) {
    setDrawTop(drawBorder);
    setDrawLeft(drawBorder);
  }

  /**
   * Set border line color for both right and bottom.
   *
   * @param color color, Color.BLACK if null.
   */
  public void setRightBottom(Color color) {
    setRightColor(color);
    setBottomColor(color);
  }

  /**
   * Set border line pen stroke for both right and bottom, null for no border
   * line.
   *
   * @param stroke stroke
   */
  public void setRightBottom(BasicStroke stroke) {
    setRightLine(stroke);
    setBottomLine(stroke);
  }

  /**
   * Set whether draw border line or not for both right and bottom.
   *
   * @param drawBorder true or false
   */
  public void setRightBottom(boolean drawBorder) {
    setDrawRight(drawBorder);
    setDrawBottom(drawBorder);
  }

  /**
   * Set color for all border line.
   *
   * @param color color, Color.BLACK if null.
   */
  public void setAll(Color color) {
    setLeftTop(color);
    setRightBottom(color);
  }

  /**
   * Set pen stroke for all border line, null for no border line.
   *
   * @param stroke stroke
   */
  public void setAll(BasicStroke stroke) {
    setLeftTop(stroke);
    setRightBottom(stroke);
  }

  /**
   * Set whether draw border line or not for all border line.
   *
   * @param drawBorder true or false
   */
  public void setAll(boolean drawBorder) {
    setLeftTop(drawBorder);
    setRightBottom(drawBorder);
  }

  /**
   * paint boder in graphics device.
   *
   * @param g2 graphics device
   * @param rect paint rect.
   */
  public void paintTextBorder(Graphics2D g2, Rectangle rect) {
    rect = (Rectangle) rect.clone();
    Shape oldClip = g2.getClip();
    Paint oldPaint = g2.getPaint();
    Stroke oldStroke = g2.getStroke();
    try {
      rect.setSize(rect.width - 1, rect.height - 1);
      if (drawTop && topLine != null) {
        drawTopLine(g2, rect);
      }
      if (drawLeft && leftLine != null) {
        drawLeftLine(g2, rect);
      }
      if (drawRight && rightLine != null) {
        drawRightLine(g2, rect);
      }
      if (drawBottom && bottomLine != null) {
        drawBottomLine(g2, rect);
      }
    } finally {
      g2.setClip(oldClip);
      g2.setPaint(oldPaint);
      g2.setStroke(oldStroke);
    }
  }

  private void drawTopLine(Graphics2D g2, Rectangle rect) {
    rect = (Rectangle) rect.clone();
    g2.setPaint(topColor);
    g2.setStroke(topLine);
    rect.x += (int) topLine.getLineWidth() / 2;
    rect.y += (int) topLine.getLineWidth() / 2;
    rect.width -= (int) topLine.getLineWidth() - 1;
    g2.drawLine(rect.x, rect.y, rect.x + rect.width, rect.y);
  }

  private void drawLeftLine(Graphics2D g2, Rectangle rect) {
    rect = (Rectangle) rect.clone();
    g2.setPaint(leftColor);
    g2.setStroke(leftLine);
    rect.x += (int) leftLine.getLineWidth() / 2;
    rect.y += (int) leftLine.getLineWidth() / 2;
    rect.height -= (int) leftLine.getLineWidth() - 1;
    g2.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height);
  }

  private void drawRightLine(Graphics2D g2, Rectangle rect) {
    rect = (Rectangle) rect.clone();
    g2.setPaint(rightColor);
    g2.setStroke(rightLine);
    rect.x -= ((int) rightLine.getLineWidth() - 1) / 2;
    rect.y += (int) rightLine.getLineWidth() / 2;
    rect.height -= (int) rightLine.getLineWidth() - 1;
    g2.drawLine(rect.x + rect.width, rect.y,
            rect.x + rect.width, rect.y + rect.height);
  }

  private void drawBottomLine(Graphics2D g2, Rectangle rect) {
    rect = (Rectangle) rect.clone();
    g2.setPaint(bottomColor);
    g2.setStroke(bottomLine);
    rect.x += (int) bottomLine.getLineWidth() / 2;
    rect.width -= (int) bottomLine.getLineWidth() - 1;
    rect.y -= ((int) bottomLine.getLineWidth() - 1) / 2;
    g2.drawLine(rect.x, rect.y + rect.height,
            rect.x + rect.width, rect.y + rect.height);
  }
}
