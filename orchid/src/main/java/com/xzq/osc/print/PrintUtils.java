/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.print;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.print.PrintService;
import javax.print.attribute.*;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 * Print Utilities
 *
 * @author zqxu
 */
public class PrintUtils implements SwingConstants {

  /**
   * Convert millimeter to print pixel, using 25.4 mm = 72 pixel.
   *
   * @param mm millimeter
   * @return print pixel
   */
  public static int MMtoPixel(double mm) {
    return (int) (mm * 72 / 25.4);
  }

  /**
   * Convert print pixel to millimeter, using 25.4 mm = 72 pixel.
   *
   * @param pixel print pixel
   * @return millimeter
   */
  public static double PixelToMM(double pixel) {
    return pixel * 25.4 / 72;
  }

  /**
   * Returns MediaSizeName object for specified size if found in provided
   * PrintService object, or null if no matched object found.
   *
   * @param printService PrintService object
   * @param width width of paper
   * @param height height of paper
   * @param units unit of length, MediaSize.MM or MediaSize.INCH
   * @return MediaSizeName object or null.
   */
  public static MediaSizeName getSupportedMedia(PrintService printService,
          double width, double height, int units) {
    Object values = printService.getSupportedAttributeValues(Media.class,
            null, null);
    if (values instanceof Media[]) {
      Media[] medias = (Media[]) values;
      for (Media medium : medias) {
        if (medium instanceof MediaSizeName) {
          MediaSize size = MediaSize.getMediaSizeForName((MediaSizeName) medium);
          if (size != null && size.getX(units) == width
                  && size.getY(units) == height) {
            return (MediaSizeName) medium;
          }
        }
      }
    }
    return null;
  }

  /**
   * Returns all PrintRequestAttribute in provided AttributeSet as a
   * PrintRequestAttributeSet object
   *
   * @param aset an AttributeSet
   * @return PrintRequestAttributeSet object contains all PrintRequestAttribute
   * in provided AttributeSet
   */
  public static PrintRequestAttributeSet getPrintRequestAttributeSet(
          AttributeSet aset) {
    PrintRequestAttributeSet pset = new HashPrintRequestAttributeSet();
    for (Attribute attr : aset.toArray()) {
      if (attr instanceof PrintRequestAttribute) {
        pset.add(attr);
      }
    }
    return pset;
  }

  /**
   * Returns all DocAttribute in provided AttributeSet as a DocAttributeSet
   * object
   *
   * @param aset an AttributeSet
   * @return DocAttributeSet object contains all DocAttribute in provided
   * AttributeSet
   */
  public static DocAttributeSet getDocAttributeSet(AttributeSet aset) {
    DocAttributeSet dset = new HashDocAttributeSet();
    for (Attribute attr : aset.toArray()) {
      if (attr instanceof DocAttribute) {
        dset.add(attr);
      }
    }
    return dset;
  }

  /**
   * paint a left-aligned single line text without border.
   *
   * @see #drawText(Graphics2D, String, Rectangle, TextBorder, boolean)
   */
  public static void drawText(Graphics2D g2, String text, Rectangle rect,
          boolean clip) {
    drawText(g2, text, rect, null, clip);
  }

  /**
   * paint a left-aligned single line text
   *
   * @see #drawText(Graphics2D, String, Rectangle, TextBorder, int, boolean)
   */
  public static void drawText(Graphics2D g2, String text, Rectangle rect,
          TextBorder border, boolean clip) {
    drawText(g2, text, rect, border, LEFT, clip);
  }

  /**
   * paint a left-aligned single line text, vertical alignment is CENTER.
   *
   * @see #drawText(Graphics2D, String, Rectangle, TextBorder, int, int,
   * boolean)
   */
  public static void drawText(Graphics2D g2, String text, Rectangle rect,
          TextBorder border, int hAlign, boolean clip) {
    drawText(g2, text, rect, border, hAlign, CENTER, clip);
  }

  /**
   * paint a single line text, with specified border and alignment.
   *
   * @param g2 Graphics device
   * @param text text to paint
   * @param rect paint rect
   * @param border text border, null for none-border
   * @param hAlign horizontal alignment, one of SwingContants.LEFT/CENTER/RIGHT,
   * defaults if LEFT
   * @param vAlign vertical alignment, one of SwingContants.TOP/CENTER/BOTTOM,
   * defaults is CENTER
   * @param clip true for clip text in rect or false else.
   */
  public static void drawText(Graphics2D g2, String text, Rectangle rect,
          TextBorder border, int hAlign, int vAlign, boolean clip) {
    Shape oldClip = null;
    rect = (Rectangle) rect.clone();
    adjustTextBorder(g2, rect, border);
    if (text == null || text.isEmpty()) {
      return;
    }
    if (clip) {
      oldClip = g2.getClip();
      g2.setClip(rect);
    }
    Point drawPoint = calcTextPoint(g2, text, rect, hAlign, vAlign);
    g2.drawString(text, drawPoint.x, drawPoint.y);
    if (clip) {
      g2.setClip(oldClip);
    }
  }

  //paint border and adjust paint rect
  private static void adjustTextBorder(Graphics2D g2, Rectangle rect,
          TextBorder border) {
    if (border != null) {
      border.paintTextBorder(g2, rect);
      Insets bi = border.getTotalInsets();
      rect.x += bi.left;
      rect.y += bi.top;
      rect.width -= bi.left + bi.right;
      rect.height -= bi.top + bi.bottom;
    }
  }

  //calculate text draw point
  private static Point calcTextPoint(Graphics2D g2, String text,
          Rectangle rect, int hAlign, int vAlign) {
    FontMetrics fm = g2.getFontMetrics();
    int width = fm.stringWidth(text);
    int height = fm.getHeight();
    Point point = new Point(rect.x,
            rect.y + fm.getAscent() + (rect.height - height) / 2);
    if (hAlign == CENTER) {
      point.x = rect.x + (rect.width - width) / 2;
    } else if (hAlign == RIGHT) {
      point.x = rect.x + rect.width - width;
    }
    if (vAlign == TOP) {
      point.y = rect.y + fm.getAscent();
    } else if (vAlign == BOTTOM) {
      point.y = rect.y + fm.getAscent() + rect.height - height;
    }
    return point;
  }

  /**
   * paint left-aligned multi-line text without border and first line indent
   *
   * @see #drawMultiLine(Graphics2D, String, Rectangle, TextBorder, boolean)
   */
  public static void drawMultiLine(Graphics2D g2, String text,
          Rectangle rect, boolean clip) {
    drawMultiLine(g2, text, rect, null, clip);
  }

  /**
   * paint left-aligned multi-line text without first line indent.
   *
   * @see #drawMultiLine(Graphics2D, String, Rectangle, TextBorder, int,
   * boolean)
   */
  public static void drawMultiLine(Graphics2D g2, String text,
          Rectangle rect, TextBorder border, boolean clip) {
    drawMultiLine(g2, text, rect, border, 0, clip);
  }

  /**
   * paint left-aligned multi-line text with specified border and first line
   * indent.
   *
   * @see #drawMultiLine(Graphics2D, String, Rectangle, TextBorder, int, int,
   * boolean)
   */
  public static void drawMultiLine(Graphics2D g2, String text,
          Rectangle rect, TextBorder border, int indentFirstLine,
          boolean clip) {
    drawMultiLine(g2, text, rect, border, indentFirstLine,
            LEFT, clip);
  }

  /**
   * paint multi-line text with vertical alignment TOP
   *
   * @see #drawMultiLine(Graphics2D, String, Rectangle, TextBorder, int, int,
   * int, boolean)
   */
  public static void drawMultiLine(Graphics2D g2, String text,
          Rectangle rect, TextBorder border, int identFirstLine,
          int hAlign, boolean clip) {
    drawMultiLine(g2, text, rect, border, identFirstLine, hAlign,
            TOP, clip);
  }

  /**
   * paint multi-line text with specified settings.
   *
   * @param g2 graphics device
   * @param text multi-line text in a String obejct
   * @param rect paint rect
   * @param border text border
   * @param indentFirstLine first line indent in pixel, greater than 0 for
   * normal indent, less than 0 for hanging indent.
   * @param hAlign horizontal alignment, one of SwingContants.LEFT/CENTER/RIGHT,
   * defaults if LEFT
   * @param vAlign vertical alignment, one of SwingContants.TOP/CENTER/BOTTOM,
   * defaults is TOP
   * @param clip true for clip text in rect or false else.
   */
  public static void drawMultiLine(Graphics2D g2, String text,
          Rectangle rect, TextBorder border, int indentFirstLine,
          int hAlign, int vAlign, boolean clip) {
    Shape oldClip = null;
    rect = (Rectangle) rect.clone();
    adjustTextBorder(g2, rect, border);
    if (text == null || text.isEmpty()) {
      return;
    }
    if (clip) {
      oldClip = g2.getClip();
      g2.setClip(rect);
    }
    int width = rect.width;
    int firstLineWidth = rect.width;
    if (indentFirstLine < 0) {
      width += indentFirstLine;
    } else {
      firstLineWidth -= indentFirstLine;
    }
    FontMetrics fm = g2.getFontMetrics();
    MultiLineText[] all = textToMultiLine(fm, text, firstLineWidth, width);
    int totalHeihgt = fm.getHeight() * all.length;
    Rectangle outRect = (Rectangle) rect.clone();
    if (vAlign == CENTER) {
      outRect.y += (rect.height - totalHeihgt) / 2;
    } else if (vAlign == BOTTOM) {
      outRect.y = outRect.height - totalHeihgt;
    }
    outRect.height = fm.getHeight();
    for (MultiLineText line : all) {
      if (line.isFirstLine()) {
        if (indentFirstLine > 0) {
          outRect.x = indentFirstLine;
        } else {
          outRect.x = rect.x;
        }
        outRect.width = firstLineWidth;
      } else {
        if (indentFirstLine < 0) {
          outRect.x = -indentFirstLine;
        } else {
          outRect.x = rect.x;
        }
        outRect.width = width;
      }
      drawText(g2, line.getText(), outRect, null, hAlign, clip);
      outRect.translate(0, outRect.height);
    }
    if (clip) {
      g2.setClip(oldClip);
    }
  }

  //separated multi-line text in a string to single line string list
  private static MultiLineText[] textToMultiLine(FontMetrics fm, String text,
          int firstLineWidth, int width) {
    List<String> list = new ArrayList<String>();
    List<MultiLineText> outList = new ArrayList<MultiLineText>();
    readMultiLine(list, text);
    for (String line : list) {
      separateByWidth(fm, line, firstLineWidth, width, outList);
    }
    return outList.toArray(new MultiLineText[0]);
  }

  // read multi-line text
  private static void readMultiLine(List<String> outList, String text) {
    String line;
    List<String> list = new ArrayList<String>();
    BufferedReader reader = new BufferedReader(new StringReader(text));
    try {
      while ((line = reader.readLine()) != null) {
        list.add(line);
      }
    } catch (IOException ex) {
      // this is impossible
      list.clear();
      list.add(text);
    }
    outList.addAll(list);
  }

  // separate a string to multi-line for specified width
  private static void separateByWidth(FontMetrics fm, String line,
          int firstLineWidth, int width, List<MultiLineText> outList) {
    boolean firstLine = true;
    int lineWidth = firstLineWidth;
    do {
      String sub = getSuitable(fm, line, lineWidth);
      outList.add(new MultiLineText(sub, firstLine));
      firstLine = false;
      lineWidth = width;
      line = line.substring(sub.length());
    } while (!line.isEmpty());
  }

  //get a sub-string for full-fill width from source string.
  private static String getSuitable(FontMetrics fm, String line, int width) {
    String sub;
    int len, prev;
    len = prev = line.length();
    do {
      sub = line.substring(0, len);
      if (fm.stringWidth(sub) <= width) {
        break;
      } else {
        prev = len;
        len = len / 2;
      }
    } while (true);
    for (;; prev--) {
      sub = line.substring(0, prev);
      if (fm.stringWidth(sub) <= width) {
        break;
      }
    }
    return sub;
  }

  /**
   * Draw icon, no zoom, no stretch, clip within rect.
   *
   * @see #drawIcon(java.awt.Graphics2D, java.awt.Component, javax.swing.Icon,
   * java.awt.Rectangle, int, int, boolean, boolean, boolean)
   */
  public static void drawIcon(Graphics2D g2, Component c, Icon icon, Rectangle rect) {
    drawIcon(g2, c, icon, rect, CENTER, CENTER, false, false, true);
  }

  /**
   * Draw icon
   *
   * @param g2 Graphics device
   * @param c Component
   * @param icon icon to drawing
   * @param rect drawing rect
   * @param hAlign horizontal alignment, SwingConstants.LEFT/CENTER/RIGHT,
   * default is CENTER
   * @param vAlign vertical alignment, SwingConstants.TOP/CENTER/BOTTOM, default
   * is CENTER
   * @param zoomToFit zoom icon to fit drawing rect width or height appropriate
   * @param stretch sretch icon to fit drawing rect both width and height
   * @param clip clip icon in drawing rect
   */
  public static void drawIcon(Graphics2D g2, Component c, Icon icon,
          Rectangle rect, int hAlign, int vAlign, boolean zoomToFit,
          boolean stretch, boolean clip) {
    drawImage(g2, c, iconToImage(c, icon), rect,
            hAlign, vAlign, zoomToFit, stretch, clip);
  }

  /**
   * Draw icon, no zoom, no stretch, clip within rect.
   *
   * @see #drawImage(java.awt.Graphics2D, java.awt.Component, java.awt.Image,
   * java.awt.Rectangle, int, int, boolean, boolean, boolean)
   */
  public static void drawImage(Graphics2D g2, Component c, Image image, Rectangle rect) {
    drawImage(g2, c, image, rect, CENTER, CENTER, false, false, true);
  }

  /**
   * Draw Image
   *
   * @param g2 Graphics device
   * @param c Component
   * @param image image to drawing
   * @param rect drawing rect
   * @param hAlign horizontal alignment, SwingConstants.LEFT/CENTER/RIGHT,
   * default is CENTER
   * @param vAlign vertical alignment, SwingConstants.TOP/CENTER/BOTTOM, default
   * is CENTER
   * @param zoomToFit zoom icon to fit drawing rect width or height appropriate
   * @param stretch sretch icon to fit drawing rect both width and height
   * @param clip clip icon in drawing rect
   */
  public static void drawImage(Graphics2D g2, Component c, Image image,
          Rectangle rect, int hAlign, int vAlign, boolean zoomToFit,
          boolean stretch, boolean clip) {
    Shape oldClip = null;
    if (clip) {
      oldClip = g2.getClip();
      g2.setClip(rect);
    }
    if (!stretch) {
      rect = getImageDrawRect(image, rect, hAlign, vAlign, zoomToFit);
    }
    if (stretch || zoomToFit) {
      g2.drawImage(image, rect.x, rect.y, rect.width, rect.height, c);
    } else {
      g2.drawImage(image, rect.x, rect.y, c);
    }
    if (clip) {
      g2.setClip(oldClip);
    }
  }

  private static Image iconToImage(Component c, Icon icon) {
    if (icon == null) {
      return null;
    }
    BufferedImage image = new BufferedImage(icon.getIconWidth(),
            icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    icon.paintIcon(c, g2, 0, 0);
    g2.dispose();
    return image;
  }

  private static Rectangle getImageDrawRect(Image image, Rectangle rect,
          int hAlign, int vAlign, boolean zoomToFit) {
    Rectangle drawRect = rect.getBounds();
    drawRect.setSize(image.getWidth(null), image.getHeight(null));
    if (zoomToFit) {
      drawRect.setSize(getZoomSize(drawRect.getSize(), rect));
    }
    if (hAlign == RIGHT) {
      drawRect.x = drawRect.x + rect.width - drawRect.width;
    } else if (hAlign == CENTER) {
      drawRect.x = drawRect.x + (rect.width - drawRect.width) / 2;
    }
    if (vAlign == BOTTOM) {
      drawRect.y = drawRect.y + rect.height - drawRect.height;
    } else if (vAlign == CENTER) {
      drawRect.y = drawRect.y + (rect.height - drawRect.height) / 2;
    }
    return drawRect;
  }

  private static Dimension getZoomSize(Dimension imageSize, Rectangle rect) {
    double zoomRate = Math.min(1.0 * rect.width / imageSize.width,
            1.0 * rect.height / imageSize.height);
    return new Dimension((int) (imageSize.width * zoomRate),
            (int) (imageSize.height * zoomRate));
  }

  private static class MultiLineText {

    private String text;
    private boolean firstLine;

    /**
     * Constructor for MultiLineText
     *
     * @param text text content
     * @param firstLine true if line is the first line
     */
    public MultiLineText(String text, boolean firstLine) {
      this.text = text;
      this.firstLine = firstLine;
    }

    public String getText() {
      return text;
    }

    public boolean isFirstLine() {
      return firstLine;
    }
  }
}
