/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.plaf.basic;

import com.xzq.osc.JocLabel;
import com.xzq.osc.OrchidUtils;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.text.View;

/**
 *
 * @author zqxu
 */
public class BasicOrchidLabelUI extends BasicLabelUI {

  public static ComponentUI createUI(JComponent c) {
    return new BasicOrchidLabelUI();
  }

  /**
   *
   * @param c
   */
  @Override
  public void installUI(JComponent c) {
    super.installUI(c);
    ((JocLabel) c).setHorizontalTextPosition(JocLabel.LEADING);
  }

  /**
   *
   * @param c
   * @return
   */
  @Override
  public Dimension getPreferredSize(JComponent c) {
    JocLabel label = (JocLabel) c;
    Dimension preferredSize = getNotRotatePreferredSize(label);
    return getRotateSize(preferredSize, label.getRotateDegrees());
  }

  private Dimension getNotRotatePreferredSize(JocLabel label) {
    String text = label.getText();
    Icon icon = label.isEnabled() ? label.getIcon() : label.getDisabledIcon();
    Insets insets = label.getInsets();
    Font font = label.getFont();
    Dimension preferredSize = new Dimension();
    Dimension iconSize = getIconPreferredSize(label, icon);
    Dimension textSize = getTextPreferredSize(label, text, font);
    int iconTextGap = label.getIconTextGap();
    int placement = label.getTextPlacement();
    if (placement == JocLabel.LEFT || placement == JocLabel.RIGHT) {
      if (text != null && icon != null) {
        preferredSize.width += iconTextGap;
      }
      preferredSize.width += iconSize.width + textSize.width;
      preferredSize.height = Math.max(iconSize.height, textSize.height);
    } else {
      preferredSize.width = Math.max(iconSize.width, textSize.width);
      if (text != null && icon != null) {
        preferredSize.height += iconTextGap;
      }
      preferredSize.height += iconSize.height + textSize.height;
    }
    preferredSize.width += insets.left + insets.right;
    preferredSize.height += insets.top + insets.bottom;
    return preferredSize;
  }

  private Dimension getIconPreferredSize(JocLabel label, Icon icon) {
    Dimension iconSize = new Dimension();
    if (icon != null) {
      iconSize.width = icon.getIconWidth();
      iconSize.height = icon.getIconHeight();
      int zoomStyle = label.getIconZoomStyle();
      Dimension zoomSize = label.getIconZoomSize();
      if (zoomStyle == JocLabel.ZOOM_RATE) {
        iconSize.width *= label.getIconZoomRate();
        iconSize.height *= label.getIconZoomRate();
      } else if (zoomStyle == JocLabel.ZOOM_SIZE && zoomSize != null) {
        iconSize.width = zoomSize.width;
        iconSize.height = zoomSize.height;
      }
    }
    return iconSize;
  }

  private Dimension getTextPreferredSize(JocLabel label, String text,
          Font font) {
    Dimension textSize = new Dimension();
    if (!OrchidUtils.isEmpty(text) && font != null) {
      FontMetrics fm = label.getFontMetrics(font);
      textSize.setSize(fm.stringWidth(text), fm.getHeight());
    }
    return textSize;
  }

  private Dimension getRotateSize(Dimension size, int degrees) {
    degrees = degrees > 0 ? degrees : 360 - degrees % 360;
    if (degrees % 180 == 0) {
      return size;
    } else if (degrees % 90 == 0) {
      return new Dimension(size.height, size.width);
    } else {
      int d90 = degrees / 90;
      degrees = degrees % 90;
      double radians;
      if (d90 % 2 == 0) {
        radians = Math.toRadians(degrees);
      } else {
        radians = Math.toRadians(90 - degrees);
      }
      double width = size.width * Math.cos(radians)
              + size.height * Math.sin(radians);
      double height = size.height * Math.cos(radians)
              + size.width * Math.sin(radians);
      return new Dimension((int) Math.ceil(width), (int) Math.ceil(height));
    }
  }

  /**
   *
   * @param g
   * @param c
   */
  @Override
  public void paint(Graphics g, JComponent c) {
    JocLabel label = (JocLabel) c;
    String text = label.getText();
    Icon icon = label.isEnabled() ? label.getIcon() : label.getDisabledIcon();
    if (OrchidUtils.isEmpty(text) && icon == null) {
      return;
    }

    int degrees = label.getRotateDegrees();
    AffineTransform at = new AffineTransform();
    at.translate(label.getWidth() / 2, label.getHeight() / 2);
    at.rotate(Math.toRadians(degrees));

    Rectangle paintRect = new Rectangle();
    paintRect.setSize(getNotRotateSize(label, icon, text, degrees));
    Graphics2D g2 = (Graphics2D) g;
    AffineTransform saveXform = g2.getTransform();
    AffineTransform toCenterAt = new AffineTransform();
    toCenterAt.concatenate(at);
    toCenterAt.translate(-(paintRect.width / 2), -(paintRect.height / 2));
    g2.transform(toCenterAt);

    if (label.isAntialias()) {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
              RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      g2.setRenderingHint(RenderingHints.KEY_RENDERING,
              RenderingHints.VALUE_RENDER_QUALITY);
    }
    Rectangle iconRect = new Rectangle();
    Rectangle textRect = new Rectangle();
    Insets insets = label.getInsets();
    paintRect.translate(insets.left, insets.top);
    paintRect.width -= insets.left + insets.right;
    paintRect.height -= insets.top + insets.bottom;
    String clippedText = layoutLabel(label, icon, text, paintRect,
            iconRect, textRect);
    if (icon != null) {
      paintIcon(label, g2, icon, iconRect);
    }
    if (text != null) {
      paintText(label, g2, clippedText, textRect);
    }

    g2.setTransform(saveXform);
  }

  private Dimension getNotRotateSize(JocLabel label,
          Icon icon, String text, int degrees) {
    degrees = degrees > 0 ? degrees : 360 - degrees % 360;
    Dimension size = label.getSize();
    if (degrees % 180 == 0) {
      return size;
    } else if (degrees % 90 == 0) {
      return new Dimension(size.height, size.width);
    } else {
      int height = getSuitableHeight(label, icon, text, size, degrees);
      return getNotRotateSize(size, height, degrees);
    }
  }

  private int getSuitableHeight(JocLabel label, Icon icon,
          String text, Dimension labelSize, int degrees) {
    Dimension textSize = getTextPreferredSize(label, text, label.getFont());
    if (icon == null) {
      return textSize.height;
    }
    int zoomStyle = label.getIconZoomStyle();
    Dimension iconSize = getMinIconPaintSize(label, icon);
    if (zoomStyle == JocLabel.ZOOM_ORIGINAL
            || zoomStyle == JocLabel.ZOOM_SIZE) {
      return Math.max(iconSize.height, textSize.height);
    }
    int d90 = degrees / 90;
    degrees = degrees % 90;
    degrees = d90 % 2 == 0 ? degrees : 90 - degrees;
    double radians = Math.toRadians(degrees);
    double rate = icon.getIconHeight() / icon.getIconWidth();
    double val1 = labelSize.width / Math.cos(radians) - textSize.width;
    double height1 = val1 / (1 + rate * Math.tan(radians));
    double val2 = labelSize.height / Math.sin(radians) - textSize.width;
    double height2 = val2 / (1 + rate / Math.tan(radians));
    return (int) Math.ceil(Math.min(height1, height2));
  }

  private Dimension getNotRotateSize(Dimension labelSize,
          int notRotateHeight, int degrees) {
    int d90 = degrees / 90;
    degrees = degrees % 90;
    degrees = d90 % 2 == 0 ? degrees : 90 - degrees;
    double radians = Math.toRadians(degrees);
    double w2 = labelSize.width - notRotateHeight * Math.sin(radians);
    double width1 = w2 / Math.cos(radians);
    double h2 = labelSize.height - notRotateHeight * Math.cos(radians);
    double width2 = h2 / Math.sin(radians);
    int notRotateWidth = (int) Math.ceil(Math.min(width1, width2));
    return new Dimension(notRotateWidth, notRotateHeight);
  }

  private String layoutLabel(JocLabel label, Icon icon, String text,
          Rectangle paintRect, Rectangle iconRect, Rectangle textRect) {
    Dimension iconAreaSize = new Dimension();
    Dimension textAreaSize = new Dimension();
    if (icon == null) {
      textRect.setRect(paintRect);
      textAreaSize.setSize(paintRect.getSize());
    } else if (OrchidUtils.isEmpty(text)) {
      iconRect.setRect(paintRect);
      iconAreaSize.setSize(paintRect.getSize());
    } else {
      calcIconTextAreaSize(label, icon, text, paintRect,
              iconAreaSize, textAreaSize);
    }
    return layoutAlignment(label, icon, text, paintRect,
            iconAreaSize, textAreaSize, iconRect, textRect);
  }

  private void calcIconTextAreaSize(JocLabel label, Icon icon, String text,
          Rectangle paintRect, Dimension iconAreaSize, Dimension textAreaSize) {
    Dimension minIconSize = getMinIconPaintSize(label, icon);
    Dimension prefTextSize = getTextPreferredSize(label, text, label.getFont());
    int gap = label.getIconTextGap();
    int placement = label.getTextPlacement();
    int iconZoomStyle = label.getIconZoomStyle();
    if (placement == JocLabel.LEFT || placement == JocLabel.RIGHT) {
      iconAreaSize.height = paintRect.height;
      if (iconZoomStyle == JocLabel.ZOOM_ORIGINAL
              || iconZoomStyle == JocLabel.ZOOM_SIZE) {
        iconAreaSize.width = minIconSize.width;
      } else {
        iconAreaSize.width = paintRect.width - gap - prefTextSize.width;
      }
    } else {
      iconAreaSize.width = paintRect.width;
      if (iconZoomStyle == JocLabel.ZOOM_ORIGINAL
              || iconZoomStyle == JocLabel.ZOOM_SIZE) {
        iconAreaSize.height = minIconSize.height;
      } else {
        iconAreaSize.height = paintRect.height - gap - prefTextSize.height;
      }
    }
    iconAreaSize.width = Math.max(iconAreaSize.width, minIconSize.width);
    iconAreaSize.height = Math.max(iconAreaSize.height, minIconSize.height);
    iconAreaSize.width = Math.min(iconAreaSize.width, paintRect.width);
    iconAreaSize.height = Math.min(iconAreaSize.height, paintRect.height);
    if (placement == JocLabel.LEFT || placement == JocLabel.RIGHT) {
      textAreaSize.height = paintRect.height;
      textAreaSize.width = paintRect.width - gap - iconAreaSize.width;
    } else {
      textAreaSize.width = paintRect.width;
      textAreaSize.height = paintRect.height - gap - iconAreaSize.height;
    }
    textAreaSize.width = Math.max(0, textAreaSize.width);
    textAreaSize.height = Math.max(0, textAreaSize.height);
  }

  private Dimension getMinIconPaintSize(JocLabel label, Icon icon) {
    int width = icon.getIconWidth();
    int height = icon.getIconHeight();
    switch (label.getIconZoomStyle()) {
      case JocLabel.ZOOM_ORIGINAL:
      case JocLabel.ZOOM_OUT:
        return new Dimension(width, height);
      case JocLabel.ZOOM_RATE:
        double rate = label.getIconZoomRate();
        return new Dimension((int) (width * rate), (int) (height * rate));
      case JocLabel.ZOOM_SIZE:
        Dimension zoomSize = label.getIconZoomSize();
        return zoomSize != null ? zoomSize : new Dimension(width, height);
      default:
        return new Dimension();
    }
  }

  private String layoutAlignment(JocLabel label, Icon icon, String text,
          Rectangle paintRect, Dimension iconAreaSize, Dimension textAreaSize,
          Rectangle iconRect, Rectangle textRect) {
    Dimension iconLayoutSize = new Dimension();
    Dimension textLayoutSize = new Dimension();
    String clippedText = clipLayout(label, icon, text,
            iconAreaSize, textAreaSize, iconLayoutSize, textLayoutSize);

    int hIconPos = OrchidUtils.getOrientedHorizontal(label,
            label.getHorizontalIconPosition());
    int vIconPos = label.getVerticalIconPosition();
    int hTextPos = OrchidUtils.getOrientedHorizontal(label,
            label.getHorizontalTextPosition());
    int vTextPos = label.getVerticalTextPosition();
    Dimension layoutSize = getLayoutSize(label, iconLayoutSize, textLayoutSize);
    Rectangle layoutRect = new Rectangle(paintRect);
    int placement = label.getTextPlacement();
    if (label.getHorizontalAlignment() == JocLabel.CENTER) {
      layoutRect.width = layoutSize.width;
      layoutRect.x += (paintRect.width - layoutSize.width) / 2;
      hIconPos = hTextPos = JocLabel.CENTER;
      if (placement == JocLabel.LEFT || placement == JocLabel.RIGHT) {
        iconAreaSize.width = iconLayoutSize.width;
        textAreaSize.width = textLayoutSize.width;
      } else {
        iconAreaSize.width = textAreaSize.width = layoutRect.width;
      }
    }
    if (label.getVerticalAlignment() == JocLabel.CENTER) {
      layoutRect.height = layoutSize.height;
      layoutRect.y += (paintRect.height - layoutSize.height) / 2;
      vIconPos = vTextPos = JocLabel.CENTER;
      if (placement == JocLabel.LEFT || placement == JocLabel.RIGHT) {
        iconAreaSize.height = textAreaSize.height = layoutRect.height;
      } else {
        iconAreaSize.height = iconLayoutSize.height;
        textAreaSize.height = textLayoutSize.height;
      }
    }
    calcIconTextLayoutRect(label, icon, text, layoutRect,
            iconAreaSize, textAreaSize, iconLayoutSize, textLayoutSize,
            hIconPos, vIconPos, hTextPos, vTextPos, iconRect, textRect);
    return clippedText;
  }

  private String clipLayout(JocLabel label, Icon icon, String text,
          Dimension iconAreaSize, Dimension textAreaSize,
          Dimension iconLayoutSize, Dimension textLayoutSize) {
    Rectangle iconLR = new Rectangle();
    Rectangle textLR = new Rectangle();
    Rectangle textVR = new Rectangle(textAreaSize);
    Rectangle iconVR = new Rectangle(iconAreaSize);
    FontMetrics fm = label.getFontMetrics(label.getFont());
    String clippedText = SwingUtilities.layoutCompoundLabel(
            label, fm, text, null,
            label.getVerticalIconPosition(),
            label.getHorizontalIconPosition(),
            label.getVerticalIconPosition(),
            label.getHorizontalTextPosition(),
            textVR, iconLR, textLR, 0);
    textLayoutSize.setSize(textLR.getSize());
    if (icon != null) {
      iconLayoutSize.setSize(getIconZoomSize(label, icon, iconVR));
    }
    return clippedText;
  }

  private Dimension getIconZoomSize(JocLabel label,
          Icon icon, Rectangle iconPaintRect) {
    int zoomStyle = label.getIconZoomStyle();
    int iconWidth = icon.getIconWidth();
    int iconHeight = icon.getIconHeight();
    int paintWidth = iconPaintRect.width;
    int paintHeight = iconPaintRect.height;
    Dimension zoomSize = label.getIconZoomSize();
    if ((zoomStyle == JocLabel.ZOOM_FIT)
            || (zoomStyle == JocLabel.ZOOM_IN
            && (iconWidth > paintWidth || iconHeight > paintHeight))
            || (zoomStyle == JocLabel.ZOOM_OUT
            && iconWidth < paintWidth && iconHeight < paintHeight)) {
      double rate = Math.min(1.0 * paintWidth / iconWidth,
              1.0 * paintHeight / iconHeight);
      return new Dimension((int) (iconWidth * rate), (int) (iconHeight * rate));
    } else if (zoomStyle == JocLabel.ZOOM_RATE) {
      double rate = label.getIconZoomRate();
      return new Dimension((int) (iconWidth * rate), (int) (iconHeight * rate));
    } else if (zoomStyle == JocLabel.ZOOM_SIZE && zoomSize != null) {
      return new Dimension(zoomSize);
    } else if (zoomStyle == JocLabel.ZOOM_STRETCH) {
      return new Dimension(paintWidth, paintHeight);
    } else {
      return new Dimension(iconWidth, iconHeight);
    }
  }

  private Dimension getLayoutSize(JocLabel label, Dimension iconLayoutSize,
          Dimension textLayoutSize) {
    int placement = label.getTextPlacement();
    int iconTextGap = label.getIconTextGap();
    Icon icon = label.getIcon();
    String text = label.getText();
    Dimension size = new Dimension();
    if (placement == JocLabel.LEFT || placement == JocLabel.RIGHT) {
      size.width = iconLayoutSize.width + textLayoutSize.width;
      if (icon != null && text != null) {
        size.width += iconTextGap;
      }
      size.height = Math.max(iconLayoutSize.height, textLayoutSize.height);
    } else {
      size.height = iconLayoutSize.height + textLayoutSize.height;
      if (icon != null && text != null) {
        size.height += iconTextGap;
      }
      size.width = Math.max(iconLayoutSize.width, textLayoutSize.width);
    }
    return size;
  }

  private void calcIconTextLayoutRect(JocLabel label,
          Icon icon, String text, Rectangle layoutRect,
          Dimension iconAreaSize, Dimension textAreaSize,
          Dimension iconLayoutSize, Dimension textLayoutSize,
          int horizontalIconPosition, int verticalIconPosition,
          int horizontalTextPosition, int verticalTextPosition,
          Rectangle iconRect, Rectangle textRect) {
    int iconTextGap = 0;
    if (icon != null && !OrchidUtils.isEmpty(text)) {
      iconTextGap = label.getIconTextGap();
    }


    switch (label.getTextPlacement()) {
      case JocLabel.LEFT:
        iconRect.x = layoutRect.x + layoutRect.width - iconAreaSize.width;
        textRect.x = iconRect.x - iconTextGap - textAreaSize.width;
        textRect.y = iconRect.y = layoutRect.y;
        break;
      case JocLabel.TOP:
        iconRect.y = layoutRect.y + layoutRect.height - iconAreaSize.height;
        textRect.y = iconRect.y - iconTextGap - textAreaSize.height;
        textRect.x = iconRect.x = layoutRect.x;
        break;
      case JocLabel.BOTTOM:
        iconRect.y = layoutRect.y;
        textRect.y = iconRect.y + iconTextGap + iconAreaSize.height;
        textRect.x = iconRect.x = layoutRect.x;
        break;
      default:
        iconRect.x = layoutRect.x;
        textRect.x = iconRect.x + iconTextGap + iconAreaSize.width;
        textRect.y = iconRect.y = layoutRect.y;
    }
    iconRect.setSize(iconLayoutSize);
    switch (horizontalIconPosition) {
      case JocLabel.CENTER:
        iconRect.x += (iconAreaSize.width - iconLayoutSize.width) / 2;
        break;
      case JocLabel.RIGHT:
        iconRect.x += iconAreaSize.width - iconLayoutSize.width;
    }
    switch (verticalIconPosition) {
      case JocLabel.CENTER:
        iconRect.y += (iconAreaSize.height - iconLayoutSize.height) / 2;
        break;
      case JocLabel.BOTTOM:
        iconRect.y += iconAreaSize.height - iconLayoutSize.height;
    }
    textRect.setSize(textLayoutSize);
    switch (horizontalTextPosition) {
      case JocLabel.CENTER:
        textRect.x += (textAreaSize.width - textLayoutSize.width) / 2;
        break;
      case JocLabel.RIGHT:
        textRect.x += textAreaSize.width - textLayoutSize.width;
    }
    switch (verticalTextPosition) {
      case JocLabel.CENTER:
        textRect.y += (textAreaSize.height - textLayoutSize.height) / 2;
        break;
      case JocLabel.BOTTOM:
        textRect.y += textAreaSize.height - textLayoutSize.height;
    }
  }

  private void paintIcon(JocLabel label, Graphics2D g2,
          Icon icon, Rectangle iconRect) {
    Image image = iconToImage(label, icon);
    g2.drawImage(image, iconRect.x, iconRect.y,
            iconRect.width, iconRect.height, label);
  }

  private Image iconToImage(JocLabel label, Icon icon) {
    BufferedImage image = new BufferedImage(icon.getIconWidth(),
            icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    icon.paintIcon(label, g2, 0, 0);
    g2.dispose();
    return image;
  }

  private void paintText(JocLabel label, Graphics2D g2,
          String text, Rectangle textRect) {
    View v = (View) label.getClientProperty(BasicHTML.propertyKey);
    if (v != null) {
      v.paint(g2, textRect);
    } else {
      FontMetrics fm = label.getFontMetrics(label.getFont());
      int textX = textRect.x;
      int textY = textRect.y + fm.getAscent();
      if (label.isEnabled()) {
        paintEnabledText(label, g2, text, textX, textY);
      } else {
        paintDisabledText(label, g2, text, textX, textY);
      }
    }
  }

  /**
   *
   * @param l
   * @param g
   * @param s
   * @param textX
   * @param textY
   */
  @Override
  protected void paintDisabledText(
          JLabel l, Graphics g, String s, int textX, int textY) {
    int accChar = l.getDisplayedMnemonicIndex();
    Color background = l.getBackground();
    g.setColor(background.brighter());
    BasicGraphicsUtils.drawStringUnderlineCharAt(g, s, accChar,
            textX + 1, textY + 1);
    g.setColor(background.darker());
    BasicGraphicsUtils.drawStringUnderlineCharAt(g, s, accChar,
            textX, textY);
  }

  /**
   * 
   * @param l
   * @param g
   * @param s
   * @param textX
   * @param textY
   */
  @Override
  protected void paintEnabledText(
          JLabel l, Graphics g, String s, int textX, int textY) {
    int mnemIndex = l.getDisplayedMnemonicIndex();
    g.setColor(l.getForeground());
    BasicGraphicsUtils.drawStringUnderlineCharAt(g, s, mnemIndex,
            textX, textY);
  }
}
