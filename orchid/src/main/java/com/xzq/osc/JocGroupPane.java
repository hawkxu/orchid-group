/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.plaf.LookAndFeelManager;
import com.xzq.osc.resource.Resource;
import java.awt.*;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author zqxu
 */
public class JocGroupPane extends JPanel implements OrchidAboutIntf {

  private static final String uiClassID = "OrchidGroupPaneUI";
  private Icon icon;
  private String text;
  private Icon backgroundImage;
  private Icon collapseIcon;
  private Icon expandIcon;
  private boolean flexible = true;
  private boolean titleBarVisible = true;
  private boolean expanded = true;
  private Insets margin;
  private boolean drawBorder = true;
  private ExpansionDirection epDirection;
  private JocPaneGroup paneGroup;
  private boolean titleBarRollover = false;

  /**
   *
   */
  public JocGroupPane() {
    super();
    text = "JocGroupPane";
    margin = new Insets(0, 0, 0, 0);
    collapseIcon = Resource.getOrchidIcon("collapse.png");
    expandIcon = Resource.getOrchidIcon("expand.png");
    epDirection = ExpansionDirection.TOP_TO_BOTTOM;
  }

  /**
   *
   * @return
   */
  public Icon getIcon() {
    return icon;
  }

  /**
   *
   * @param icon
   */
  public void setIcon(Icon icon) {
    Icon old = this.icon;
    if (old != icon) {
      this.icon = icon;
      resizeAndRepaint();
      firePropertyChange("icon", old, icon);
    }
  }

  /**
   *
   * @return
   */
  public String getText() {
    return text;
  }

  /**
   *
   * @param text
   */
  public void setText(String text) {
    String old = this.text;
    if (!OrchidUtils.equals(old, text)) {
      this.text = text;
      resizeAndRepaint();
      firePropertyChange("text", old, text);
    }
  }

  /**
   *
   * @return
   */
  public Icon getBackgroundImage() {
    return backgroundImage;
  }

  /**
   *
   * @param backgroundImage
   */
  public void setBackgroundImage(Icon backgroundImage) {
    Icon old = this.backgroundImage;
    if (old != backgroundImage) {
      this.backgroundImage = backgroundImage;
      repaint();
      firePropertyChange("backgroundImage", old, backgroundImage);
    }
  }

  /**
   *
   * @return
   */
  public Icon getCollapseIcon() {
    return collapseIcon;
  }

  /**
   *
   * @param collapseIcon
   */
  public void setCollapseIcon(Icon collapseIcon) {
    Icon old = this.collapseIcon;
    if (old != collapseIcon) {
      this.collapseIcon = collapseIcon;
      resizeAndRepaint();
      firePropertyChange("collapseIcon", old, collapseIcon);
    }
  }

  /**
   *
   * @return
   */
  public Icon getExpandIcon() {
    return expandIcon;
  }

  /**
   *
   * @param expandIcon
   */
  public void setExpandIcon(Icon expandIcon) {
    Icon old = this.expandIcon;
    if (old != expandIcon) {
      this.expandIcon = expandIcon;
      resizeAndRepaint();
      firePropertyChange("expandIcon", old, expandIcon);
    }
  }

  /**
   *
   * @return
   */
  public boolean isFlexible() {
    return flexible;
  }

  /**
   *
   * @param flexible
   */
  public void setFlexible(boolean flexible) {
    boolean old = this.flexible;
    if (old != flexible) {
      this.flexible = flexible;
      resizeAndRepaint();
      firePropertyChange("flexible", old, flexible);
    }
  }

  /**
   *
   * @return
   */
  public boolean isTitleBarVisible() {
    return titleBarVisible;
  }

  /**
   *
   * @param titleBarVisible
   */
  public void setTitleBarVisible(boolean titleBarVisible) {
    boolean old = this.titleBarVisible;
    if (old != titleBarVisible) {
      this.titleBarVisible = titleBarVisible;
      resizeAndRepaint();
      firePropertyChange("titleBarVisible", old, titleBarVisible);
    }
  }

  /**
   *
   * @return
   */
  public boolean isExpanded() {
    return expanded;
  }

  /**
   *
   * @param expanded
   */
  public void setExpanded(boolean expanded) {
    boolean old = this.expanded;
    if (old != expanded) {
      this.expanded = expanded;
      resizeAndRepaint();
      firePropertyChange("expanded", old, expanded);
    }
  }

  /**
   *
   * @return
   */
  public Insets getMargin() {
    return margin;
  }

  /**
   *
   * @param margin
   */
  public void setMargin(Insets margin) {
    Insets old = this.margin;
    if (!OrchidUtils.equals(old, margin)) {
      this.margin = margin;
      resizeAndRepaint();
      firePropertyChange("margin", old, margin);
    }
  }

  /**
   *
   * @return
   */
  public boolean isDrawBorder() {
    return drawBorder;
  }

  /**
   *
   * @param drawBorder
   */
  public void setDrawBorder(boolean drawBorder) {
    boolean old = this.drawBorder;
    if (old != drawBorder) {
      this.drawBorder = drawBorder;
      repaint();
      firePropertyChange("drawBorder", old, drawBorder);
    }
  }

  /**
   *
   * @return
   */
  public ExpansionDirection getExpansionDirection() {
    return epDirection;
  }

  /**
   *
   * @param epDirection
   */
  public void setExpansionDirection(ExpansionDirection epDirection) {
    ExpansionDirection old = this.epDirection;
    if (old != epDirection) {
      this.epDirection = epDirection;
      resizeAndRepaint();
      firePropertyChange("expansionDirection", old, epDirection);
    }
  }

  /**
   *
   * @return
   */
  public boolean isVerticalExpansion() {
    ExpansionDirection epdir = getExpansionDirection();
    return epdir != ExpansionDirection.LEFT_TO_RIGHT
            && epdir != ExpansionDirection.RIGHT_TO_LEFT;
  }

  /**
   *
   * @return
   */
  public JocPaneGroup getPaneGroup() {
    return paneGroup;
  }

  /**
   *
   * @param paneGroup
   */
  public void setPaneGroup(JocPaneGroup paneGroup) {
    JocPaneGroup old = this.paneGroup;
    if (old != paneGroup) {
      if (old != null) {
        this.paneGroup = null;
        old.remove(this);
      }
      this.paneGroup = paneGroup;
      if (paneGroup != null) {
        paneGroup.add(this);
      }
      firePropertyChange("paneGroup", old, paneGroup);
    }
  }

  /**
   *
   * @return
   */
  public boolean isTitleBarRollover() {
    return titleBarRollover;
  }

  /**
   *
   * @param titleBarRollover
   */
  public void setTitleBarRollover(boolean titleBarRollover) {
    boolean old = this.titleBarRollover;
    if (old != titleBarRollover) {
      this.titleBarRollover = titleBarRollover;
      repaint();
      firePropertyChange("titleBarRollover", old, titleBarRollover);
    }
  }

  /**
   *
   * @return
   */
  @Override
  public Insets getInsets() {
    Insets insets = super.getInsets();
    ExpansionDirection epdir = getExpansionDirection();
    Dimension barSize = getTitleBarPreferredSize();
    if (epdir == ExpansionDirection.LEFT_TO_RIGHT && isExpanded()) {
      insets.right += barSize.width;
    } else if (epdir == ExpansionDirection.LEFT_TO_RIGHT
            || epdir == ExpansionDirection.RIGHT_TO_LEFT) {
      insets.left += barSize.width;
    } else {
      insets.top += getTitleBarPreferredSize().height;
    }
    return OrchidUtils.combineInsets(insets, getMargin());
  }

  private void resizeAndRepaint() {
    revalidate();
    repaint();
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
   *
   * @param g
   */
  @Override
  protected void paintChildren(Graphics g) {
    Shape clip = g.getClip();
    Rectangle old = g.getClipBounds();
    Insets insets = getInsets();
    Rectangle rect = new Rectangle();
    rect.x = insets.left;
    rect.y = insets.top;
    rect.width = getWidth() - insets.left - insets.right;
    rect.height = getHeight() - insets.top - insets.bottom;
    g.setClip(rect.intersection(old));
    super.paintChildren(g);
    g.setClip(clip);
  }

  /**
   *
   * @return
   */
  public Dimension getTitleBarPreferredSize() {
    int width = getTitleBarPreferredWidth();
    int height = getTitleBarPreferredHeight();
    if (isVerticalExpansion()) {
      return new Dimension(width, height);
    } else {
      return new Dimension(height, width);
    }
  }

  private int getTitleBarPreferredWidth() {
    if (!isTitleBarVisible()) {
      return 0;
    }
    Icon barIcon = getIcon();
    String barText = getText();
    Font font = getFont();
    int width = getFlexButtonWidth() + 6;
    if (barIcon != null) {
      width += barIcon.getIconWidth();
    }
    if (font != null && barText != null) {
      width += getFontMetrics(font).stringWidth(barText);
    }
    if (barIcon != null && barText != null) {
      width += 4;
    }
    return width;
  }

  private int getFlexButtonWidth() {
    int width = 0;
    Icon cIcon = getCollapseIcon();
    Icon eIcon = getExpandIcon();
    if (cIcon != null) {
      width = Math.max(width, cIcon.getIconWidth());
    }
    if (eIcon != null) {
      width = Math.max(width, eIcon.getIconWidth());
    }
    return width == 0 ? 15 : width + 4;
  }

  private int getTitleBarPreferredHeight() {
    if (!isTitleBarVisible()) {
      return 0;
    }
    int height = Math.max(0, getIconButtonHeight(getIcon()));
    if (isFlexible()) {
      height = Math.max(height, getIconButtonHeight(getCollapseIcon()));
      height = Math.max(height, getIconButtonHeight(getExpandIcon()));
    }
    Font font = getFont();
    if (font != null) {
      height = Math.max(height, getFontMetrics(font).getHeight());
    }
    return height + 4;
  }

  private int getIconButtonHeight(Icon icon) {
    return icon == null ? 0 : icon.getIconHeight() + 4;
  }

  /**
   *
   * @return
   */
  public Rectangle getTitleBarRect() {
    Rectangle rect = new Rectangle();
    if (isTitleBarVisible()) {
      Insets insets = getInsets();
      Insets superInsets = super.getInsets();
      Insets imargin = getMargin();
      int width = getWidth(), height = getHeight();
      ExpansionDirection epdir = getExpansionDirection();
      if (epdir == ExpansionDirection.LEFT_TO_RIGHT && isExpanded()) {
        rect.x = width - insets.right;
        rect.y = superInsets.top;
        rect.width = insets.right - superInsets.right;
        if (imargin != null) {
          rect.x += imargin.right;
          rect.width -= imargin.right;
        }
        rect.height = height - superInsets.top - superInsets.bottom;
      } else if (epdir == ExpansionDirection.LEFT_TO_RIGHT
              || epdir == ExpansionDirection.RIGHT_TO_LEFT) {
        rect.x = superInsets.left;
        rect.y = superInsets.top;
        rect.width = insets.left - superInsets.left;
        if (imargin != null) {
          rect.width -= imargin.left;
        }
        rect.height = height - superInsets.top - superInsets.bottom;
      } else {
        rect.x = superInsets.left;
        rect.y = superInsets.top;
        rect.height = insets.top - superInsets.top;
        if (imargin != null) {
          rect.height -= imargin.top;
        }
        rect.width = width - superInsets.left - superInsets.right;
      }
    }
    return rect;
  }

  /**
   *
   * @param titleBarRect
   * @return
   */
  public Rectangle getFlexButtonRect(Rectangle titleBarRect) {
    Rectangle rect = new Rectangle();
    if (isTitleBarVisible() && isFlexible()) {
      if (isVerticalExpansion()) {
        rect.y = titleBarRect.y + 2;
        rect.height = titleBarRect.height - 4;
        rect.width = getFlexButtonWidth();
        rect.x = titleBarRect.x + titleBarRect.width - rect.width - 4;
      } else {
        rect.x = titleBarRect.x + 2;
        rect.y = titleBarRect.y + 2;
        rect.width = titleBarRect.width - 4;
        rect.height = getFlexButtonWidth();
      }
    }
    return rect;
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

  public static enum ExpansionDirection {

    TOP_TO_BOTTOM, RIGHT_TO_LEFT, BOTTOM_TO_TOP, LEFT_TO_RIGHT
  }
}
