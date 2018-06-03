/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.folderchooser;

import java.io.File;
import javax.swing.Icon;

/**
 * Filter class for use in JocFolderChooser
 *
 * @author zqxu
 */
public abstract class FolderFilter {

  /**
   * Returns custom icon for the file, return null for use system icon. default
   * is null.
   *
   * @param file file object
   * @return icon object.
   */
  public Icon getFileIcon(File file) {
    return null;
  }

  /**
   * Returns true for display the file in folder chooser tree view, false for
   * hide the file. default is true;
   *
   * @param file file object
   * @return true for list the file or false hide the file..
   */
  public boolean listFile(File file) {
    return true;
  }

  /**
   * Returns true for list children of the folder in folder chooser tree view,
   * fase for hide children of the folder. default is true.
   *
   * @param folder the folder
   * @return true for list children and false for hide children.
   */
  public boolean listChildren(File folder) {
    return true;
  }

  /**
   * Returns true for accept the file that folder chooser with aceept the file
   * as selected, false for reject the file. default is true.
   *
   * @param file file object
   * @return true for accept and false for reject.
   */
  public boolean accept(File file) {
    return true;
  }
}
