/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.plaf.basic;

import com.xzq.osc.JocFolderChooser;
import com.xzq.osc.OrchidUtils;
import com.xzq.osc.folderchooser.FolderFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author zqxu
 */
public class BasicFolderTreeNode extends DefaultMutableTreeNode
        implements Comparable<BasicFolderTreeNode> {
  protected JocFolderChooser fc;
  protected File file;
  protected Icon icon;
  protected boolean loaded;
  protected boolean inCreate;

  /**
   *
   * @param fc
   */
  public BasicFolderTreeNode(JocFolderChooser fc) {
    this(fc, null);
  }

  /**
   *
   * @param fc
   * @param file
   */
  protected BasicFolderTreeNode(JocFolderChooser fc, File file) {
    this.fc = fc;
    this.file = file;
    loaded = file != null && file.isFile();
  }

  /**
   *
   * @param newName
   * @return
   */
  public boolean renameFile(String newName) {
    BasicFolderTreeNode parentNode = (BasicFolderTreeNode) getParent();
    boolean success;
    File renameTo;
    if (inCreate) {
      renameTo = new File(parentNode.getFile(), newName);
      success = renameTo.mkdir();
    } else {
      renameTo = new File(file.getParentFile(), newName);
      success = file.renameTo(renameTo);
    }
    if (success) {
      inCreate = false;
      file = renameTo;
    }
    return success;
  }

  /**
   *
   * @return
   */
  public BasicFolderTreeNode createChildFolder() {
    if (inCreate) {
      throw new IllegalStateException("The folder itself was in create!");
    }
    BasicFolderTreeNode node = new BasicFolderTreeNode(fc);
    node.loaded = true;
    node.inCreate = true;
    add(node);
    return node;
  }

  @Override
  public boolean isLeaf() {
    return file != null && file.isFile();
  }

  /**
   *
   * @return
   */
  @Override
  public int getChildCount() {
    synchronized (this) {
      if (!loaded) {
        loaded = true;
        initChildren();
      }
    }
    return super.getChildCount();
  }

  /**
   *
   * @param aChild
   * @return
   */
  @Override
  public int getIndex(TreeNode aChild) {
    if (aChild == null) {
      throw new IllegalArgumentException("argument is null");
    }
    return getChildCount() == 0 ? -1 : children.indexOf(aChild);
  }

  /**
   *
   * @param obj
   * @return
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof BasicFolderTreeNode
            && OrchidUtils.equals(file, ((BasicFolderTreeNode) obj).file);
  }

  /**
   *
   * @return
   */
  @Override
  public int hashCode() {
    int hash = 5;
    hash = 61 * hash + (this.file != null ? this.file.hashCode() : 0);
    return hash;
  }

  /**
   *
   */
  public void clear() {
    if (inCreate || (file != null && file.isFile())) {
      return;
    }
    removeAllChildren();
    loaded = false;
  }

  /**
   *
   * @return
   */
  public File getFile() {
    return file;
  }

  /**
   *
   * @return
   */
  public Icon getIcon() {
    if (inCreate) {
      return UIManager.getIcon("FileChooser.newFolderIcon");
    } else {
      return icon != null ? icon : fc.getFileSystemView().getSystemIcon(file);
    }
  }

  /**
   *
   * @param icon
   */
  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  /**
   *
   * @return
   */
  public String getName() {
    if (file == null) {
      return inCreate ? "" : "/";
    } else {
      return fc.getFileSystemView().getSystemDisplayName(file);
    }
  }

  /**
   *
   * @return
   */
  public boolean isInCreate() {
    return inCreate;
  }

  /**
   *
   * @return
   */
  @Override
  public String toString() {
    return getName();
  }

  private void initChildren() {
    FolderFilter filter = fc.getFolderFilter();
    FileSystemView fsv = fc.getFileSystemView();
    File[] fileArray = listChildren(file, fsv, filter);
    int selectionMode = fc.getFileSelectionMode();
    List<BasicFolderTreeNode> childs = new ArrayList<BasicFolderTreeNode>();
    for (File child : fileArray) {
      if ((selectionMode == JFileChooser.DIRECTORIES_ONLY && child.isFile())
              || (filter != null && !filter.listFile(child))) {
        continue;
      }
      BasicFolderTreeNode node = new BasicFolderTreeNode(fc, child);
      if (filter != null) {
        node.setIcon(filter.getFileIcon(child));
      }
      childs.add(node);
    }
    Collections.sort(childs);
    for (BasicFolderTreeNode node : childs) {
      add(node);
    }
  }

  private File[] listChildren(File file, FileSystemView fsv,
          FolderFilter filter) {
    File[] fileArray = null;
    if (file == null) {
      if (!inCreate) {
        fileArray = fsv.getRoots();
      }
    } else if (file.isDirectory()) {
      if (filter == null || filter.listChildren(file)) {
        fileArray = fsv.getFiles(file, fc.isFileHidingEnabled());
      }
    }
    return fileArray == null ? new File[0] : fileArray;
  }

  /**
   *
   * @param o
   * @return
   */
  @Override
  public int compareTo(BasicFolderTreeNode o) {
    if (!(o instanceof BasicFolderTreeNode)) {
      return 0;
    }
    File source = getFile();
    File target = ((BasicFolderTreeNode) o).getFile();
    if (source.isDirectory() && target.isFile()) {
      return -1;
    } else if (source.isFile() && target.isDirectory()) {
      return 1;
    } else {
      return source.compareTo(target);
    }
  }
}
