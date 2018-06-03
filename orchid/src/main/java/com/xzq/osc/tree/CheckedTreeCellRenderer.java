/**
 * *****************************************************************************
 * 2013, All rights reserved.
 * *****************************************************************************
 */
package com.xzq.osc.tree;

import com.xzq.osc.CheckState;
import java.awt.Component;

import com.xzq.osc.plaf.AbstractCheckedCellRenderer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

/**
 * Description of CheckedTreeCellRenderer.
 *
 * @author zqxu
 */
public class CheckedTreeCellRenderer extends AbstractCheckedCellRenderer
        implements TreeCellRenderer {

  /**
   * Is the value currently selected.
   */
  protected boolean selected;
  /**
   * True if has focus.
   */
  protected boolean hasFocus;
  /**
   * True if draws focus border around icon as well.
   */
  private boolean drawsFocusBorderAroundIcon;
  /**
   * If true, a dashed line is drawn as the focus indicator.
   */
  private boolean drawDashedFocusIndicator;
  // Icons
  /**
   * Icon used to show non-leaf nodes that aren't expanded.
   */
  transient protected Icon closedIcon;
  /**
   * Icon used to show leaf nodes.
   */
  transient protected Icon leafIcon;
  /**
   * Icon used to show non-leaf nodes that are expanded.
   */
  transient protected Icon openIcon;
  // Colors
  /**
   * Color to use for the foreground for selected nodes.
   */
  protected Color textSelectionColor;
  /**
   * Color to use for the foreground for non-selected nodes.
   */
  protected Color textNonSelectionColor;
  /**
   * Color to use for the background when a node is selected.
   */
  protected Color backgroundSelectionColor;
  /**
   * Color to use for the background when the node isn't selected.
   */
  protected Color backgroundNonSelectionColor;
  /**
   * Color to use for the focus indicator when the node has focus.
   */
  protected Color borderSelectionColor;
  private boolean isDropCell;
  private boolean fillBackground = true;
  // If drawDashedFocusIndicator is true, the following are used.
  /**
   * Background color of the tree.
   */
  private Color treeBGColor;
  /**
   * Color to draw the focus indicator in, determined from the background.
   * color.
   */
  private Color focusBGColor;

  /**
   * The constructor.
   */
  public CheckedTreeCellRenderer() {
    super();
    initializeLocalVars();
    setName("Tree.cellRenderer");
  }

  protected void initializeLocalVars() {
    setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
    setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
    setOpenIcon(UIManager.getIcon("Tree.openIcon"));

    setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
    setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
    setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
    setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
    setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));

    drawsFocusBorderAroundIcon =
            UIManager.getBoolean("Tree.drawsFocusBorderAroundIcon");
    drawDashedFocusIndicator =
            UIManager.getBoolean("Tree.drawDashedFocusIndicator");
    if (UIManager.getDefaults().contains("Tree.rendererFillBackground")) {
      fillBackground = UIManager.getBoolean("Tree.rendererFillBackground");
    }
    Insets margins = UIManager.getInsets("Tree.rendererMargins");
    if (margins != null) {
      setBorder(new EmptyBorder(margins.top, margins.left,
              margins.bottom, margins.right));
    }
  }

  /**
   * Sets the icon used to represent non-leaf nodes that are expanded.
   */
  public void setOpenIcon(Icon newIcon) {
    openIcon = newIcon;
  }

  /**
   * Returns the icon used to represent non-leaf nodes that are expanded.
   */
  public Icon getOpenIcon() {
    return openIcon;
  }

  /**
   * Sets the icon used to represent non-leaf nodes that are not expanded.
   */
  public void setClosedIcon(Icon newIcon) {
    closedIcon = newIcon;
  }

  /**
   * Returns the icon used to represent non-leaf nodes that are not expanded.
   */
  public Icon getClosedIcon() {
    return closedIcon;
  }

  /**
   * Sets the icon used to represent leaf nodes.
   */
  public void setLeafIcon(Icon newIcon) {
    leafIcon = newIcon;
  }

  /**
   * Returns the icon used to represent leaf nodes.
   */
  public Icon getLeafIcon() {
    return leafIcon;
  }

  /**
   * Sets the color the text is drawn with when the node is selected.
   */
  public void setTextSelectionColor(Color newColor) {
    textSelectionColor = newColor;
  }

  /**
   * Returns the color the text is drawn with when the node is selected.
   */
  public Color getTextSelectionColor() {
    return textSelectionColor;
  }

  /**
   * Sets the color the text is drawn with when the node isn't selected.
   */
  public void setTextNonSelectionColor(Color newColor) {
    textNonSelectionColor = newColor;
  }

  /**
   * Returns the color the text is drawn with when the node isn't selected.
   */
  public Color getTextNonSelectionColor() {
    return textNonSelectionColor;
  }

  /**
   * Sets the color to use for the background if node is selected.
   */
  public void setBackgroundSelectionColor(Color newColor) {
    backgroundSelectionColor = newColor;
  }

  /**
   * Returns the color to use for the background if node is selected.
   */
  public Color getBackgroundSelectionColor() {
    return backgroundSelectionColor;
  }

  /**
   * Sets the background color to be used for non selected nodes.
   */
  public void setBackgroundNonSelectionColor(Color newColor) {
    backgroundNonSelectionColor = newColor;
  }

  /**
   * Returns the background color to be used for non selected nodes.
   */
  public Color getBackgroundNonSelectionColor() {
    return backgroundNonSelectionColor;
  }

  /**
   * Sets the color to use for the border.
   */
  public void setBorderSelectionColor(Color newColor) {
    borderSelectionColor = newColor;
  }

  /**
   * Returns the color the border is drawn.
   */
  public Color getBorderSelectionColor() {
    return borderSelectionColor;
  }

  /**
   * Overrides <i>JComponent.getPreferredSize</i> to return slightly wider
   * preferred size value.
   */
  @Override
  public Dimension getPreferredSize() {
    Dimension retDimension = super.getPreferredSize();
    if (retDimension != null) {
      retDimension.width += 3;
    }
    return retDimension;
  }

  /**
   * Configures the renderer based on the passed in components. The value is set
   * from messaging the tree with <i>convertValueToText</i>, which ultimately
   * invokes <i>toString</i> on <i>value</i>. The foreground color is set based
   * on the selection and the icon is set based on the <i>leaf</i> and
   * <i>expanded</i> parameters.
   */
  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value,
          boolean selected, boolean expanded, boolean leaf, int row,
          boolean hasFocus) {
    updateSelectionIcon(tree, row, selected);
    if (leaf) {
      contentLabel.setIcon(getLeafIcon());
    } else if (expanded) {
      contentLabel.setIcon(getOpenIcon());
    } else {
      contentLabel.setIcon(getClosedIcon());
    }
    String stringValue = tree.convertValueToText(
            value, selected, expanded, leaf, row, hasFocus);
    contentLabel.setText(stringValue);

    this.hasFocus = hasFocus;
    this.selected = selected;
    this.isDropCell = false;
    Color foreground;
    JTree.DropLocation dropLocation = tree.getDropLocation();
    if (dropLocation != null
            && dropLocation.getChildIndex() == -1
            && tree.getRowForPath(dropLocation.getPath()) == row) {
      Color col = UIManager.getColor("Tree.dropCellForeground");
      if (col != null) {
        foreground = col;
      } else {
        foreground = getTextSelectionColor();
      }
      this.isDropCell = true;
    } else if (selected) {
      foreground = getTextSelectionColor();
    } else {
      foreground = getTextNonSelectionColor();
    }
    setForeground(foreground);
    Color background;
    if (this.isDropCell) {
      background = UIManager.getColor("Tree.dropCellBackground");
      if (background == null) {
        background = getBackgroundSelectionColor();
      }
    } else if (selected) {
      background = getBackgroundSelectionColor();
    } else {
      background = getBackgroundNonSelectionColor();
      if (background == null) {
        background = getBackground();
      }
    }
    setBackground(background);
    setEnabled(tree.isEnabled());
    setComponentOrientation(tree.getComponentOrientation());
    return this;
  }

  private void updateSelectionIcon(JTree tree, int row, boolean selected) {
    TreeSelectionModel model = tree.getSelectionModel();
    if (model instanceof CheckedTreeSelectionModel) {
      updateCheckedIcon(
              ((CheckedTreeSelectionModel) model).getRowSelectionState(row));
    } else {
      updateCheckedIcon(selected ? CheckState.CHECKED : CheckState.UNCHECK);
    }
  }

  /**
   * Paints the value. The background is filled based on selected.
   */
  @Override
  public void paint(Graphics g) {
    Color bColor = getBackground();
    Rectangle rect = getContentPaintBounds();
    if (bColor != null && fillBackground) {
      g.setColor(bColor);
      g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    if (hasFocus) {
      if (drawsFocusBorderAroundIcon) {
        rect.width += rect.x;
        rect.x = 0;
      }
      paintFocus(g, rect.x, rect.y, rect.width, rect.height, bColor);
    }
    super.paint(g);
  }

  private void paintFocus(Graphics g, int x, int y, int w, int h,
          Color notColor) {
    Color bsColor = getBorderSelectionColor();
    if (bsColor != null && (selected || !drawDashedFocusIndicator)) {
      g.setColor(bsColor);
      g.drawRect(x, y, w - 1, h - 1);
    }
    if (drawDashedFocusIndicator && notColor != null) {
      if (treeBGColor != notColor) {
        treeBGColor = notColor;
        focusBGColor = new Color(~notColor.getRGB());
      }
      g.setColor(focusBGColor);
      BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
    }
  }

  private Rectangle getContentPaintBounds() {
    Rectangle bounds = contentLabel.getBounds();
    Icon icon = contentLabel.getIcon();
    int offset = icon == null ? 0
            : icon.getIconWidth() + contentLabel.getIconTextGap();
    if (getComponentOrientation().isLeftToRight()) {
      bounds.x += offset - 1;
      bounds.width -= offset - 1;
    } else {
      bounds.width -= offset - 1;
    }
    return new Rectangle(bounds.x, 0, bounds.width, getHeight());
  }
}
