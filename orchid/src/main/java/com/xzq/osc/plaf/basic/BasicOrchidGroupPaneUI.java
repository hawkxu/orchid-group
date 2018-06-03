/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.plaf.basic;

import com.xzq.osc.JocGroupPane;
import com.xzq.osc.JocGroupPane.ExpansionDirection;
import com.xzq.osc.OrchidUtils;
import com.xzq.osc.print.PrintUtils;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 *
 * @author zqxu
 */
public class BasicOrchidGroupPaneUI extends BasicPanelUI {

  private Handler handler;

  public static ComponentUI createUI(JComponent c) {
    return new BasicOrchidGroupPaneUI();
  }

  /**
   *
   * @param p
   */
  @Override
  protected void installDefaults(JPanel p) {
    super.installDefaults(p);
    p.addMouseListener(getHandler());
    p.addMouseMotionListener(getHandler());
    LookAndFeel.installProperty(p, "opaque", Boolean.FALSE);
  }

  /**
   *
   * @param p
   */
  @Override
  protected void uninstallDefaults(JPanel p) {
    super.uninstallDefaults(p);
    p.removeMouseListener(getHandler());
    p.removeMouseMotionListener(getHandler());
  }

  private Handler getHandler() {
    if (handler == null) {
      handler = new Handler();
    }
    return handler;
  }

  /**
   *
   * @param c
   * @return
   */
  @Override
  public Dimension getMinimumSize(JComponent c) {
    Dimension size = super.getMinimumSize(c);
    JocGroupPane pane = (JocGroupPane) c;
    Dimension barSize = pane.getTitleBarPreferredSize();
    ExpansionDirection epdir = pane.getExpansionDirection();
    if (epdir == ExpansionDirection.LEFT_TO_RIGHT
            || epdir == ExpansionDirection.RIGHT_TO_LEFT) {
      size.width = Math.max(size.width, barSize.width);
    } else {
      size.height = Math.max(size.height, barSize.height);
    }
    return size;
  }

  /**
   *
   * @param c
   * @return
   */
  @Override
  public Dimension getPreferredSize(JComponent c) {
    JocGroupPane pane = (JocGroupPane) c;
    Insets insets = c.getInsets();
    Dimension barSize = pane.getTitleBarPreferredSize();
    if (pane.isVerticalExpansion()) {
      barSize.width += insets.left + insets.right;
      barSize.height = insets.top + insets.bottom;
    } else {
      barSize.width = insets.left + insets.right;
      barSize.height += insets.top + insets.bottom;
    }
    LayoutManager lymr = pane.getLayout();
    if (lymr != null) {
      Dimension lymrSize = lymr.preferredLayoutSize(c);
      barSize.width = Math.max(barSize.width, lymrSize.width);
      barSize.height = Math.max(barSize.height, lymrSize.height);
    }
    if (!pane.isExpanded()) {
      insets = OrchidUtils.subtractInsets(insets, pane.getMargin());
      if (pane.isVerticalExpansion()) {
        barSize.height = insets.top + insets.bottom;
      } else {
        barSize.width = insets.left + insets.right;
      }
    }
    return barSize;
  }

  /**
   *
   * @param g
   * @param c
   */
  @Override
  public void paint(Graphics g, JComponent c) {
    JocGroupPane pane = (JocGroupPane) c;
    Graphics2D g2 = (Graphics2D) g;
    paintBackgroundImage(pane, g2);
    if (pane.isTitleBarVisible()) {
      paintTitleBar(pane, g2);
    }
    if (pane.isDrawBorder()) {
      paintPaneBorder(pane, g2);
    }
  }

  /**
   *
   * @param pane
   * @param g2
   */
  protected void paintBackgroundImage(JocGroupPane pane, Graphics2D g2) {
    Icon backIcon = pane.getBackgroundImage();
    if (backIcon != null) {
      Insets insets = pane.getInsets();
      Insets margin = pane.getMargin();
      if (margin != null) {
        insets = OrchidUtils.subtractInsets(insets, margin);
      }
      Image image = getBackgroundImage(backIcon);
      g2.drawImage(image, insets.left, insets.top, pane);
    }
  }

  private Image getBackgroundImage(Icon icon) {
    BufferedImage image = new BufferedImage(icon.getIconWidth(),
            icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics g = image.createGraphics();
    icon.paintIcon(null, g, 0, 0);
    g.dispose();
    return image;
  }

  /**
   *
   * @param pane
   * @param g2
   */
  protected void paintTitleBar(JocGroupPane pane, Graphics2D g2) {
    Rectangle barRect = pane.getTitleBarRect();
    paintBarBackground(pane, g2, barRect);
    Rectangle flexRect = pane.getFlexButtonRect(barRect);
    Rectangle titleRect = new Rectangle();
    if (pane.isVerticalExpansion()) {
      titleRect.x = barRect.x + 2;
      titleRect.y = barRect.y + 2;
      titleRect.height = barRect.height - 4;
      titleRect.width = barRect.width - flexRect.width - 6;
    } else {
      titleRect.x = barRect.x + 2;
      titleRect.y = barRect.y + flexRect.height + 4;
      titleRect.width = barRect.width - 4;
      titleRect.height = barRect.height - flexRect.height - 6;
    }
    g2.setColor(pane.getForeground());
    paintTitle(pane, g2, titleRect);
    if (pane.isFlexible()) {
      paintFlexButton(pane, g2, flexRect);
    }
  }

  private void paintBarBackground(JocGroupPane pane, Graphics2D g2,
          Rectangle barRect) {
    Polygon shape = new Polygon();
    Point start = new Point(), end = new Point();
    int x = barRect.x, y = barRect.y,
            rx = barRect.x + barRect.width,
            by = barRect.y + barRect.height;
    ExpansionDirection epdir = pane.getExpansionDirection();
    if (epdir == ExpansionDirection.LEFT_TO_RIGHT) {
      start.y = by;
      shape.addPoint(x, y);
      shape.addPoint(rx - 2, y);
      shape.addPoint(rx, y + 2);
      shape.addPoint(rx, by - 2);
      shape.addPoint(rx - 2, by);
      shape.addPoint(x, by);
    } else if (epdir == ExpansionDirection.RIGHT_TO_LEFT) {
      start.y = by;
      shape.addPoint(x + 2, by);
      shape.addPoint(x, by - 2);
      shape.addPoint(x, y + 2);
      shape.addPoint(x + 2, y);
      shape.addPoint(rx, y);
      shape.addPoint(rx, by);
    } else {
      end.x = rx;
      shape.addPoint(x, y + 2);
      shape.addPoint(x + 2, y);
      shape.addPoint(rx - 2, y);
      shape.addPoint(rx, y + 2);
      shape.addPoint(rx, by);
      shape.addPoint(x, by);
    }
    Color light = new Color(210, 210, 210);
    Color darker = new Color(150, 150, 255);
    if (pane.isFlexible() && pane.isTitleBarRollover()) {
      light = Color.WHITE;
    }
    Paint originPaint = g2.getPaint();
    g2.setPaint(new GradientPaint(start, light, end, darker));
    g2.fill(shape);
    g2.setPaint(originPaint);
  }

  private void paintTitle(JocGroupPane pane,
          Graphics2D g2, Rectangle titleRect) {
    FontMetrics fm = pane.getFontMetrics(pane.getFont());
    Icon picon = pane.getIcon();
    String ptext = pane.getText();
    Rectangle viewRect = new Rectangle();
    Rectangle iconRect = new Rectangle();
    Rectangle textRect = new Rectangle();
    boolean vertical = pane.isVerticalExpansion();
    if (vertical) {
      viewRect.setRect(titleRect);
    } else {
      viewRect.setSize(titleRect.height, titleRect.width);
    }
    ptext = SwingUtilities.layoutCompoundLabel(pane, fm, ptext, picon,
            SwingConstants.CENTER, SwingConstants.LEADING,
            SwingConstants.CENTER, SwingConstants.TRAILING,
            viewRect, iconRect, textRect, 4);
    AffineTransform saveXform = g2.getTransform();
    if (!vertical) {
      g2.translate(titleRect.x, titleRect.getMaxY());
      g2.rotate(Math.toRadians(-90));
    }
    if (picon != null) {
      picon.paintIcon(pane, g2, iconRect.x, iconRect.y);
    }
    if (ptext != null) {
      g2.drawString(ptext, textRect.x, textRect.y + fm.getAscent());
    }
    g2.setTransform(saveXform);
  }

  private void paintFlexButton(JocGroupPane pane,
          Graphics2D g2, Rectangle flexRect) {
    Icon flexIcon = pane.isExpanded()
            ? pane.getCollapseIcon() : pane.getExpandIcon();
    if (flexIcon != null) {
      AffineTransform saveXform = g2.getTransform();
      Rectangle paintRect = new Rectangle();
      ExpansionDirection epdir = pane.getExpansionDirection();
      if (epdir == null || epdir == ExpansionDirection.TOP_TO_BOTTOM) {
        paintRect.setBounds(flexRect);
      } else {
        if (epdir == ExpansionDirection.RIGHT_TO_LEFT) {
          g2.translate(flexRect.getMaxX(), flexRect.y);
          g2.rotate(Math.toRadians(90));
        } else if (epdir == ExpansionDirection.LEFT_TO_RIGHT) {
          g2.translate(flexRect.x, flexRect.getMaxY());
          g2.rotate(Math.toRadians(-90));
        } else {
          g2.translate(flexRect.getMaxX(), flexRect.getMaxY());
          g2.rotate(Math.toRadians(180));
        }
        paintRect.setSize(flexRect.height, flexRect.width);
      }
      PrintUtils.drawIcon((Graphics2D) g2, pane, flexIcon, paintRect,
              SwingConstants.CENTER, SwingConstants.CENTER, false, false, true);
      g2.setTransform(saveXform);
    }
  }

  private void paintPaneBorder(JocGroupPane pane, Graphics2D g2) {
    Insets insets = pane.getInsets();
    insets = OrchidUtils.subtractInsets(insets, pane.getMargin());
    int x = insets.left, y = insets.top,
            rx = pane.getWidth() - insets.right - 1,
            by = pane.getHeight() - insets.bottom - 1;
    if (rx < x || by < y) {
      return;
    }
    ExpansionDirection epdir = pane.getExpansionDirection();
    g2.setColor(new Color(150, 150, 255));
    if (epdir == ExpansionDirection.LEFT_TO_RIGHT) {
      g2.drawLine(rx, y, x, y);
      g2.drawLine(x, y, x, by);
      g2.drawLine(x, by, rx, by);
    } else if (epdir == ExpansionDirection.RIGHT_TO_LEFT) {
      g2.drawLine(x, y, rx, y);
      g2.drawLine(rx, y, rx, by);
      g2.drawLine(rx, by, x, by);
    } else {
      g2.drawLine(x, y, x, by);
      g2.drawLine(x, by, rx, by);
      g2.drawLine(rx, by, rx, y);
    }
  }

  private static class Handler extends MouseAdapter {

    @Override
    public void mouseClicked(MouseEvent e) {
      if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
        JocGroupPane pane = (JocGroupPane) e.getSource();
        Rectangle barRect = pane.getTitleBarRect();
        if (pane.isFlexible() && barRect.contains(e.getPoint())) {
          pane.setExpanded(!pane.isExpanded());
        }
      }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      JocGroupPane pane = (JocGroupPane) e.getSource();
      Rectangle barRect = pane.getTitleBarRect();
      if (pane.isFlexible() && barRect.contains(e.getPoint())) {
        if (!pane.isTitleBarRollover()) {
          pane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          pane.setTitleBarRollover(true);
        }
      } else if (pane.isTitleBarRollover()) {
        pane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        pane.setTitleBarRollover(false);
      }
    }

    @Override
    public void mouseExited(MouseEvent e) {
      JocGroupPane pane = (JocGroupPane) e.getSource();
      if (pane.isFlexible() && pane.isTitleBarVisible()) {
        pane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        pane.setTitleBarRollover(false);
      }
    }
  }
}
