/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.print;

import java.util.ArrayList;
import java.util.Arrays;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;

/**
 * extended MediaSizeName to support custom paper size. If you want to use
 * custom paper size as default, you should use
 * <code>MediaSizeName.registerMediaSizeName</code> to get a registered
 * MediaSizeName object for the custom paper size, then put it into an
 * AttributeSet and send to printer driver. If you want to use a custom paper
 * size with width length more than height length as default paper, then this
 * class can not help you, you should use Java2D print mechanism and let user
 * choose the required paper.
 *
 * @author zqxu
 */
public class CustomMediaSizeName extends MediaSizeName {

  private static final ArrayList<String> nameList;
  private static final ArrayList<EnumSyntax> mediaList;

  //initilize with MediaSizeName settings
  static {
    CustomMediaSizeName tmpName;
    tmpName = new CustomMediaSizeName(0);
    nameList = new ArrayList<String>();
    nameList.addAll(Arrays.asList(tmpName.getSuperNameTable()));
    mediaList = new ArrayList<EnumSyntax>();
    mediaList.addAll(Arrays.asList(tmpName.getSuperMediaTable()));
  }

  /**
   * Constructor for internal use
   *
   * @param value Integer value.
   */
  protected CustomMediaSizeName(int value) {
    super(value);
  }

  // get MediaSizeName name table
  private String[] getSuperNameTable() {
    return super.getStringTable();
  }

  // get MediaSizeName media table
  private EnumSyntax[] getSuperMediaTable() {
    return super.getEnumValueTable();
  }

  /**
   * register custom paper with specified size and return a MediaSizeName
   * object.
   *
   * @param x length of width
   * @param y length of height
   * @param units unit of length, Size2DSyntax.INCH or Size2DSyntax.MM
   * @param sizeName custom paper size name
   * @return registered MediaSizeName object
   * @exception IllegalArgumentException if width or height less than 0, or
   * width more then height, or sizeName already registered.
   */
  public static MediaSizeName registerMediaSizeName(int x, int y,
          int units, String sizeName) {
    return registerMediaSizeName((float) x, (float) y, units, sizeName);
  }

  /**
   * register custom paper with specified size and return a MediaSizeName
   * object.
   *
   * @param x length of width
   * @param y length of height
   * @param units unit of length, Size2DSyntax.INCH or Size2DSyntax.MM
   * @param sizeName custom paper size name
   * @return registered MediaSizeName object
   * @exception IllegalArgumentException if width or height less than 0, or
   * width more then height, or sizeName already registered.
   */
  public static MediaSizeName registerMediaSizeName(float x, float y,
          int units, String sizeName) {
    sizeName = sizeName.toLowerCase();
    if (nameList.contains(sizeName)) {
      throw new IllegalArgumentException(
              "sizeName: " + sizeName + "already registered!");
    }
    int index = nameList.size();
    nameList.add(sizeName);
    CustomMediaSizeName customName = new CustomMediaSizeName(index);
    mediaList.add(customName);
    MediaSize customSize = new MediaSize(x, y, units, customName);
    return customSize.getMediaSizeName();
  }

  /**
   * Find MediaSizeName object by sizeName.
   *
   * @param sizeName paper size name.
   * @return A MediaSizeName object if found or null for sizeName not found.
   */
  public static MediaSizeName findByName(String sizeName) {
    for (EnumSyntax medium : mediaList) {
      if (medium instanceof MediaSizeName
              && sizeName.toLowerCase().equals(medium.toString())) {
        return (MediaSizeName) medium;
      }
    }
    return null;
  }

  /**
   *
   * @return
   */
  @Override
  protected String[] getStringTable() {
    return nameList.toArray(new String[0]);
  }

  /**
   *
   * @return
   */
  @Override
  protected EnumSyntax[] getEnumValueTable() {
    return mediaList.toArray(new EnumSyntax[0]);
  }

  /**
   * for check custom size name.
   */
  @Override
  public boolean equals(Object object) {
    if (super.equals(object)) {
      return true;
    } else {
      return (object != null && object instanceof Media
              && (object.toString().equalsIgnoreCase(toString())));
    }
  }

  /**
   *
   * @return
   */
  @Override
  public int hashCode() {
    int hash = 5;
    return hash;
  }
}
