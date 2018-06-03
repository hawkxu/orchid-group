/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.plaf.LookAndFeelManager;
import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * JocLabel using to display icon and text, support rotate and zoom.<br> For
 * some reason, JocLabel layout icon and text center in vertical if property
 * verticalAlignment is CENTER, the verticalIconPosition and
 * verticalTextPosition property will be ignored. On the other side, if
 * verticalAlignment is not CENTER, then JocLabel layout icon and text
 * vertically according to property verticalTextIconPosition and
 * verticalTextPosition, the property verticalAlignment will be ignored.
 *
 * @author zqxu
 */
public class JocLabel extends JLabel implements OrchidAboutIntf {

  private static final String uiClassID = "OrchidLabelUI";
  /**
   * keep icon original size, do not zomm it.
   */
  public static final int ZOOM_ORIGINAL = 0;
  /**
   * if icon size less than icon area of the label, then zoom icon in, otherwise
   * keep icon original size.
   */
  public static final int ZOOM_IN = 1;
  /**
   * if icon size large than icon area of the label, then zoom icon out,
   * otherwise keep icon original size.
   */
  public static final int ZOOM_OUT = 2;
  /**
   * smooth zoom icon to fit icon area of the label.
   */
  public static final int ZOOM_FIT = 3;
  /**
   * stretch icon to fit both width and height of icon area of the label.
   */
  public static final int ZOOM_STRETCH = 4;
  /**
   * zoom icon using the rate specified by iconZoomRate property.
   */
  public static final int ZOOM_RATE = 5;
  /**
   * zoom icon to the size specified by iconZoomSize property.
   */
  public static final int ZOOM_SIZE = 6;
  private int rotateDegrees = 0;
  private int textPlacement = RIGHT;
  private int iconZoomStyle = ZOOM_ORIGINAL;
  private double iconZoomRate = 1.0;
  private Dimension iconZoomSize = null;
  private int verticalIconPosition = CENTER;
  private int horizontalIconPosition = CENTER;
  private boolean antialias = false;

  /**
   *
   */
  public JocLabel() {
    super();
  }

  /**
   *
   * @param text
   */
  public JocLabel(String text) {
    super(text);
  }

  /**
   *
   * @param image
   */
  public JocLabel(Icon image) {
    super(image);
  }

  /**
   *
   * @param text
   * @param horizontalAlignment
   */
  public JocLabel(String text, int horizontalAlignment) {
    super(text, horizontalAlignment);
  }

  /**
   *
   * @param image
   * @param horizontalAlignment
   */
  public JocLabel(Icon image, int horizontalAlignment) {
    super(image, horizontalAlignment);
  }

  /**
   *
   * @param text
   * @param icon
   * @param horizontalAlignment
   */
  public JocLabel(String text, Icon icon, int horizontalAlignment) {
    super(text, icon, horizontalAlignment);
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
   * Returns the rotate angle of the label in degrees.
   *
   * @return rotate angle.
   */
  public int getRotateDegrees() {
    return rotateDegrees;
  }

  /**
   * Sets the rotate angle of the label in degrees.
   *
   * @param rotateDegrees rotate angle.
   */
  public void setRotateDegrees(int rotateDegrees) {
    int old = this.rotateDegrees;
    if (old != rotateDegrees) {
      this.rotateDegrees = rotateDegrees;
      revalidate();
      repaint();
      firePropertyChange("rotateDegrees", old, rotateDegrees);
    }
  }

  /**
   * Returns text placement of the label.
   *
   * @return The value of the textPlacement property, one of the following
   * constants defined in SwingConstants: <code>LEFT</code>, <code>TOP</code>,
   * <code>RIGHT</code>, or <code>BOTTOM</code>.
   */
  public int getTextPlacement() {
    return textPlacement;
  }

  /**
   * Sets the text placement of the label, default is RIGHT.
   *
   * @param textPlacement One of the following constants defined in
   * SwingConstants: <code>LEFT</code>, <code>TOP</code>, <code>RIGHT</code>, or
   * <code>BOTTOM</code>.
   */
  public void setTextPlacement(int textPlacement) {
    int old = this.textPlacement;
    if (old != textPlacement) {
      this.textPlacement = checkTextPlacement(textPlacement);
      revalidate();
      repaint();
      firePropertyChange("textPlacement", old, textPlacement);
    }
  }

  private int checkTextPlacement(int placement) {
    if (placement == LEFT || placement == TOP
            || placement == RIGHT || placement == BOTTOM) {
      return placement;
    } else {
      throw new IllegalArgumentException("textPlacement");
    }
  }

  /**
   * Return icon zoom style of the label.
   *
   * @return The value of the iconZoomStyle property, one of the following
   * constants defined in JocLabel: <code>ZOOM_ORIGINAL</code>,
   * <code>ZOOM_IN</code>, <code>ZOOM_OUT</code>, <code>ZOOM_FIT</code>,
   * <code>ZOOM_STRETCH</code>, <code>ZOOM_RATE</code>, or
   * <code>ZOOM_SIZE</code>.
   */
  public int getIconZoomStyle() {
    return iconZoomStyle;
  }

  /**
   * Sets the icon zoom style of the label, default is ZOOM_ORIGINAL.
   *
   * @param iconZoomStyle One of the following constants defined in JocLabel:
   * <code>ZOOM_ORIGINAL</code>, <code>ZOOM_IN</code>, <code>ZOOM_OUT</code>,
   * <code>ZOOM_FIT</code>, <code>ZOOM_STRETCH</code>, <code>ZOOM_RATE</code>,
   * or <code>ZOOM_SIZE</code>.
   */
  public void setIconZoomStyle(int iconZoomStyle) {
    int old = this.iconZoomStyle;
    if (old != iconZoomStyle) {
      this.iconZoomStyle = checkIconZoomStyle(iconZoomStyle);
      repaint();
      firePropertyChange("iconZoomStyle", old, iconZoomStyle);
    }
  }

  private int checkIconZoomStyle(int iconZoomStyle) {
    if (iconZoomStyle == ZOOM_ORIGINAL || iconZoomStyle == ZOOM_IN
            || iconZoomStyle == ZOOM_OUT || iconZoomStyle == ZOOM_FIT
            || iconZoomStyle == ZOOM_STRETCH || iconZoomStyle == ZOOM_RATE
            || iconZoomStyle == ZOOM_SIZE) {
      return iconZoomStyle;
    } else {
      throw new IllegalArgumentException("iconZoomStyle");
    }
  }

  /**
   * Returns the zoom rate for icon zoom when iconZoomStyle property set to
   * ZOOM_RATE.
   *
   * @return The value of the property iconZoomRate.
   */
  public double getIconZoomRate() {
    return iconZoomRate;
  }

  /**
   * Sets the zoom rate for icon zoom, default is 1.
   *
   * @param iconZoomRate zoom rate for icon zoom.
   */
  public void setIconZoomRate(double iconZoomRate) {
    double old = this.iconZoomRate;
    if (old != iconZoomRate) {
      this.iconZoomRate = iconZoomRate;
      repaint();
      firePropertyChange("iconZoomRate", old, iconZoomRate);
    }
  }

  /**
   * Returns the size for icon zoom when iconZoomStyle property set to
   * ZOOM_SIZE.
   *
   * @return The value of the property iconZoomSize.
   */
  public Dimension getIconZoomSize() {
    return iconZoomSize;
  }

  /**
   * Sets the size for icon zoom, default is null, that means use icon original
   * size.
   *
   * @param iconZoomSize The size for icon zoom.
   */
  public void setIconZoomSize(Dimension iconZoomSize) {
    Dimension old = this.iconZoomSize;
    if (!OrchidUtils.equals(old, iconZoomSize)) {
      this.iconZoomSize = iconZoomSize;
      repaint();
      firePropertyChange("iconZoomSize", old, iconZoomSize);
    }
  }

  /**
   * Returns the horizontal position of the label's icon, relative to its icon
   * area.
   *
   * @return One of the following constants defined in SwingConstants:
   * <code>LEFT</code>, <code>CENTER</code>, or <code>RIGHT</code>,
   */
  public int getHorizontalIconPosition() {
    return horizontalIconPosition;
  }

  /**
   * Sets the horizontal position of the label's icon, relative to its icon
   * area, default is CENTER.
   *
   * @param horizontalIconPosition One of the following constants defined in
   * SwingConstants: <code>LEFT</code>, <code>CENTER</code>, or
   * <code>RIGHT</code>
   */
  public void setHorizontalIconPosition(int horizontalIconPosition) {
    int old = this.horizontalIconPosition;
    if (old != horizontalIconPosition) {
      this.horizontalIconPosition = horizontalIconPosition;
      repaint();
      firePropertyChange("horizontalIconPosition", old, horizontalIconPosition);
    }
  }

  /**
   * Returns the vertical position of the label's icon, relative to its icon
   * area.
   *
   * @return One of the following constants defined in
   * <code>SwingConstants</code>: <code>TOP</code>, <code>CENTER</code>, or
   * <code>BOTTOM</code>.
   */
  public int getVerticalIconPosition() {
    return verticalIconPosition;
  }

  /**
   * Sets the vertical position of the label's icon, relative to its icon area,
   * default is CENTER.
   *
   * @param verticalIconPosition One of the following constants defined in
   * <code>SwingConstants</code>: <code>TOP</code>, <code>CENTER</code>, or
   * <code>BOTTOM</code>.
   */
  public void setVerticalIconPosition(int verticalIconPosition) {
    int old = this.verticalIconPosition;
    if (old != verticalIconPosition) {
      this.verticalIconPosition = verticalIconPosition;
      repaint();
      firePropertyChange("verticalIconPosition", old, verticalIconPosition);
    }
  }

  /**
   * Returns true if antialias is on, else returns false.
   *
   * @return true of false.
   */
  public boolean isAntialias() {
    return antialias;
  }

  /**
   * Sets the antialias state of the lable.
   *
   * @param antialias true for antialias on, false off.
   */
  public void setAntialias(boolean antialias) {
    boolean old = this.antialias;
    if (old != antialias) {
      this.antialias = antialias;
      repaint();
      firePropertyChange("antialias", old, antialias);
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
