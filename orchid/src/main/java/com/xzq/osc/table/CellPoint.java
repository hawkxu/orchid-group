/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.table;

/**
 *
 * @author zqxu
 */
public class CellPoint {

  public int row;
  public int column;

  /**
   * Constructor with coordinate [0,0]
   */
  public CellPoint() {
    this(0, 0);
  }

  /**
   * Constructor with a copy of the cell
   */
  public CellPoint(CellPoint cell) {
    this(cell.row, cell.column);
  }

  /**
   * Constructor with row and column index.
   *
   * @param row row index
   * @param column column index
   */
  public CellPoint(int row, int column) {
    this.row = row;
    this.column = column;
  }

  /**
   * Returns a new object contains same point.
   *
   * @return a new object contains same point.
   */
  public CellPoint getLocation() {
    return new CellPoint(row, column);
  }

  /**
   * Set point to the cell.
   *
   * @param cell cell point.
   */
  public void setLocation(CellPoint cell) {
    setLocation(cell.row, cell.column);
  }

  /**
   * Set point to the row and column index.
   *
   * @param row row index
   * @param column column index.
   */
  public void setLocation(int row, int column) {
    this.row = row;
    this.column = column;
  }

  /**
   * offset current point, new point is (row + dRow, column + dColumn)
   *
   * @param dRow rows to offset, accept nagative value.
   * @param dColumn column to offset, accept nagative value.
   */
  public void translate(int dRow, int dColumn) {
    this.row += dRow;
    this.column += dColumn;
  }

  /**
   * Returns a new object contains point relative to current.
   *
   * @param dRow rows to offset, accept nagative value.
   * @param dColumn column to offset, accept nagative value.
   * @return a new object contains point relative to current.
   */
  public CellPoint getTranslate(int dRow, int dColumn) {
    return new CellPoint(row + dRow, column + dColumn);
  }

  /**
   *
   * @param obj
   * @return
   */
  @Override
  public boolean equals(Object obj) {
    return obj != null && obj instanceof CellPoint
            && ((CellPoint) obj).row == row
            && ((CellPoint) obj).column == column;
  }

  /**
   *
   * @return
   */
  @Override
  public int hashCode() {
    int hash = 3;
    hash = 29 * hash + this.row;
    hash = 29 * hash + this.column;
    return hash;
  }

  /**
   * 
   * @return
   */
  @Override
  public String toString() {
    return getClass().getName() + "[row=" + row + ",column=" + column + "]";
  }
}
