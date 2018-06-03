/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import java.awt.*;
import java.io.Serializable;
import java.util.Arrays;
import javax.swing.JDialog;
import javax.swing.SwingConstants;

/**
 *
 * @author zqxu
 */
public class JocGridLayout implements LayoutManager, SwingConstants,
        Serializable, OrchidAboutIntf {

  private int rows;
  private int columns;
  private int hgap;
  private int vgap;
  private RowSizeMode rowSizeMode;
  private ColumnSizeMode columnSizeMode;
  private RowAlignment rowAlignment;
  private ColumnAlignment columnAlignment;
  private Orientation orientation;

  public JocGridLayout() {
    this(1, 0, 0, 0);
  }

  public JocGridLayout(int hgap, int vgap) {
    this(1, 0, hgap, vgap);
  }

  public JocGridLayout(int rows, int columns, int hgap, int vgap) {
    this.rows = rows;
    this.columns = columns;
    this.hgap = hgap;
    this.vgap = vgap;
    if (rows == 0 && columns == 0) {
      this.rows = 1;
    }
    rowSizeMode = RowSizeMode.ROW_UNIFIED;
    columnSizeMode = ColumnSizeMode.COLUMN_UNIFIED;
    rowAlignment = RowAlignment.CENTER;
    columnAlignment = ColumnAlignment.CENTER;
    orientation = Orientation.COLUMN_FIRST;
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    if ((rows == 0) && (this.columns == 0)) {
      throw new IllegalArgumentException("rows and columns cannot both be zero");
    }
    this.rows = rows;
  }

  public int getColumns() {
    return columns;
  }

  public void setColumns(int columns) {
    if ((columns == 0) && (this.rows == 0)) {
      throw new IllegalArgumentException("rows and columns cannot both be zero");
    }
    this.columns = columns;
  }

  public int getHgap() {
    return hgap;
  }

  public void setHgap(int hgap) {
    this.hgap = hgap;
  }

  public int getVgap() {
    return vgap;
  }

  public void setVgap(int vgap) {
    this.vgap = vgap;
  }

  public RowSizeMode getRowSizeMode() {
    return rowSizeMode;
  }

  public void setRowSizeMode(RowSizeMode rowSizeMode) {
    this.rowSizeMode = rowSizeMode;
  }

  public ColumnSizeMode getColumnSizeMode() {
    return columnSizeMode;
  }

  public void setColumnSizeMode(ColumnSizeMode columnSizeMode) {
    this.columnSizeMode = columnSizeMode;
  }

  public RowAlignment getRowAlignment() {
    return rowAlignment;
  }

  public void setRowAlignment(RowAlignment rowAlignment) {
    this.rowAlignment = rowAlignment;
  }

  public ColumnAlignment getColumnAlignment() {
    return columnAlignment;
  }

  public void setColumnAlignment(ColumnAlignment columnAlignment) {
    this.columnAlignment = columnAlignment;
  }

  public Orientation getOrientation() {
    return orientation;
  }

  public void setOrientation(Orientation orientation) {
    this.orientation = orientation;
  }

  @Override
  public void addLayoutComponent(String name, Component comp) {
  }

  @Override
  public void removeLayoutComponent(Component comp) {
  }

  @Override
  public Dimension preferredLayoutSize(Container parent) {
    return calcLayoutSize(parent, false);
  }

  @Override
  public Dimension minimumLayoutSize(Container parent) {
    return calcLayoutSize(parent, true);
  }

  private Dimension calcLayoutSize(Container parent, boolean minimum) {
    Dimension layoutSize = new Dimension();
    if (parent.getComponentCount() > 0) {
      RowColumnSizes sizes = new RowColumnSizes();
      calcRowColumnSizes(parent, sizes, minimum);
      layoutSize.width = calcTotalWidth(sizes.columnsWidth,
              sizes.maxColumnWidth, hgap, columnSizeMode);
      layoutSize.height = calcTotalHeight(sizes.rowsHeight,
              sizes.maxRowHeight, vgap, rowSizeMode);
    }
    Insets insets = parent.getInsets();
    layoutSize.width += insets.left + insets.right;
    layoutSize.height += insets.top + insets.bottom;
    return layoutSize;
  }

  private void calcRowColumnSizes(Container parent, RowColumnSizes sizes,
          boolean minimum) {
    int ncomponents = parent.getComponentCount();
    int nrows = rows > 0 ? rows : (ncomponents + columns - 1) / columns;
    int ncols = rows > 0 ? (ncomponents + rows - 1) / rows : columns;
    sizes.initilize(ncomponents, nrows, ncols);
    if (orientation == Orientation.ROW_FIRST) {
      calcRowFirstSizes(parent, nrows, ncols, sizes, minimum);
    } else {
      calcColumnFirstSizes(parent, nrows, ncols, sizes, minimum);
    }
  }

  private void calcRowFirstSizes(Container parent, int nrows, int ncols,
          RowColumnSizes sizes, boolean minimum) {
    for (int r = 0; r < nrows; r++) {
      for (int c = 0; c < ncols; c++) {
        int i = r * ncols + c;
        if (i == parent.getComponentCount()) {
          break;
        }
        Component t = parent.getComponent(i);
        Dimension size = minimum ? t.getMinimumSize() : t.getPreferredSize();
        sizes.calcRowColumnSize(r, c, size);
      }
    }
  }

  private void calcColumnFirstSizes(Container parent, int nrows, int ncols,
          RowColumnSizes sizes, boolean minimum) {
    for (int c = 0; c < ncols; c++) {
      for (int r = 0; r < nrows; r++) {
        int i = c * nrows + r;
        if (i == parent.getComponentCount()) {
          break;
        }
        Component t = parent.getComponent(i);
        Dimension size = minimum ? t.getMinimumSize() : t.getPreferredSize();
        sizes.calcRowColumnSize(r, c, size);
      }
    }
  }

  private int calcTotalWidth(int[] colsWidth, int maxWidth, int hgap,
          ColumnSizeMode sizeMode) {
    int totalWidth = 0;
    if (sizeMode == ColumnSizeMode.ALL_UNIFIED) {
      totalWidth = maxWidth * colsWidth.length + hgap * colsWidth.length;
    } else {
      for (int width : colsWidth) {
        totalWidth += width + hgap;
      }
    }
    return totalWidth - hgap;
  }

  private int calcTotalHeight(int[] rowsHeight, int maxHeight, int vgap,
          RowSizeMode sizeMode) {
    int totalHeight = 0;
    if (sizeMode == RowSizeMode.ALL_UNIFIED) {
      totalHeight = maxHeight * rowsHeight.length + vgap * rowsHeight.length;
    } else {
      for (int height : rowsHeight) {
        totalHeight += height + vgap;
      }
    }
    return totalHeight - vgap;
  }

  @Override
  public void layoutContainer(Container parent) {
    if (parent.getComponentCount() == 0) {
      return;
    }
    RowColumnSizes sizes = new RowColumnSizes();
    calcRowColumnSizes(parent, sizes, false);
    Insets insets = parent.getInsets();
    int width = parent.getWidth() - insets.left - insets.right;
    int height = parent.getHeight() - insets.top - insets.bottom;
    if (columnSizeMode == ColumnSizeMode.ALL_UNIFIED) {
      width -= (sizes.columnsWidth.length - 1) * hgap;
      Arrays.fill(sizes.columnsWidth, width / sizes.columnsWidth.length);
    }
    if (rowSizeMode == RowSizeMode.ALL_UNIFIED) {
      height -= (sizes.rowsHeight.length - 1) * vgap;
      Arrays.fill(sizes.rowsHeight, height / sizes.rowsHeight.length);
    }
    if (orientation == Orientation.ROW_FIRST) {
      layoutRowFirst(parent, sizes);
    } else {
      layoutColumnFirst(parent, sizes);
    }
  }

  private void layoutRowFirst(Container parent, RowColumnSizes sizes) {
    Insets insets = parent.getInsets();
    int lx, ly = insets.top;
    int width = parent.getWidth();
    boolean ltr = parent.getComponentOrientation().isLeftToRight();
    int factor = ltr ? 1 : -1;
    int ltrX = ltr ? insets.left
            : width - insets.right - sizes.columnsWidth[0];
    for (int r = 0; r < sizes.rowsHeight.length; r++) {
      lx = ltrX;
      for (int c = 0; c < sizes.columnsWidth.length; c++) {
        int i = r * sizes.columnsWidth.length + c;
        if (i == parent.getComponentCount()) {
          break;
        }
        Component t = parent.getComponent(i);
        layoutComponent(parent, t, lx, ly,
                sizes.columnsWidth[c], sizes.rowsHeight[r]);
        lx += factor * (sizes.columnsWidth[c] + hgap);
      }
      ly += sizes.rowsHeight[r] + vgap;
    }
  }

  private void layoutColumnFirst(Container parent, RowColumnSizes sizes) {
    Insets insets = parent.getInsets();
    int lx, ly, width = parent.getWidth();
    boolean ltr = parent.getComponentOrientation().isLeftToRight();
    int factor = ltr ? 1 : -1;
    lx = ltr ? insets.left : width - insets.right - sizes.columnsWidth[0];
    for (int c = 0; c < sizes.columnsWidth.length; c++) {
      ly = insets.top;
      for (int r = 0; r < sizes.rowsHeight.length; r++) {
        int i = c * sizes.rowsHeight.length + r;
        if (i == parent.getComponentCount()) {
          break;
        }
        Component t = parent.getComponent(i);
        layoutComponent(parent, t, lx, ly,
                sizes.columnsWidth[c], sizes.rowsHeight[r]);
        ly += sizes.rowsHeight[r] + vgap;
      }
      lx += factor * (sizes.columnsWidth[c] + hgap);
    }
  }

  private void layoutComponent(Container parent, Component t, int x, int y,
          int width, int height) {
    Dimension size = t.getPreferredSize();
    if (columnSizeMode != ColumnSizeMode.ALL_VARIOUS) {
      size.width = width;
    } else {
      size.width = Math.min(size.width, width);
    }
    if (rowSizeMode != RowSizeMode.ALL_VARIOUS) {
      size.height = height;
    } else {
      size.height = Math.min(size.height, height);
    }
    int hAlign = CENTER, vAlign = CENTER;
    if (columnAlignment != null) {
      hAlign = columnAlignment.intValue();
    }
    hAlign = OrchidUtils.getOrientedHorizontal(parent, hAlign);
    if (rowAlignment != null) {
      vAlign = rowAlignment.intValue();
    }
    x = getLayoutPos(x, width, size.width, hAlign);
    y = getLayoutPos(y, height, size.height, vAlign);
    t.setBounds(x, y, size.width, size.height);
  }

  private int getLayoutPos(int pos, int layoutSize, int componentSize,
          int alignment) {
    if (alignment == CENTER) {
      pos += (layoutSize - componentSize) / 2;
    } else if (alignment == RIGHT || alignment == BOTTOM) {
      pos += layoutSize - componentSize;
    }
    return pos;
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

  public static enum RowSizeMode {

    ROW_UNIFIED, ALL_UNIFIED, ALL_VARIOUS
  };

  public static enum ColumnSizeMode {

    COLUMN_UNIFIED, ALL_UNIFIED, ALL_VARIOUS
  };

  public static enum RowAlignment {

    TOP, CENTER, BOTTOM;

    public int intValue() {
      switch (this) {
        case TOP:
          return SwingConstants.TOP;
        case BOTTOM:
          return SwingConstants.BOTTOM;
        default:
          return SwingConstants.CENTER;
      }
    }
  }

  public static enum ColumnAlignment {

    LEADING, LEFT, CENTER, TRAILING, RIGHT;

    public int intValue() {
      switch (this) {
        case LEADING:
          return SwingConstants.LEADING;
        case LEFT:
          return SwingConstants.LEFT;
        case TRAILING:
          return SwingConstants.TRAILING;
        case RIGHT:
          return SwingConstants.RIGHT;
        default:
          return SwingConstants.CENTER;
      }
    }
  }

  public static enum Orientation {

    COLUMN_FIRST, ROW_FIRST
  }

  private static class RowColumnSizes {

    private int maxRowHeight;
    private int maxColumnWidth;
    private int[] rowsHeight;
    private int[] columnsWidth;

    public void initilize(int ncomponents, int nrows, int ncols) {
      maxRowHeight = 0;
      maxColumnWidth = 0;
      rowsHeight = new int[nrows];
      columnsWidth = new int[ncols];
      Arrays.fill(rowsHeight, 0);
      Arrays.fill(columnsWidth, 0);
    }

    public void calcRowColumnSize(int row, int column, Dimension size) {
      maxRowHeight = Math.max(maxRowHeight, size.height);
      maxColumnWidth = Math.max(maxColumnWidth, size.width);
      rowsHeight[row] = Math.max(rowsHeight[row], size.height);
      columnsWidth[column] = Math.max(columnsWidth[column], size.width);
    }
  }
}