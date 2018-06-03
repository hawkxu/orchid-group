/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.folderchooser.FolderFilter;
import com.xzq.osc.plaf.LookAndFeelManager;
import java.awt.*;
import java.io.File;
import javax.accessibility.AccessibleContext;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

/**
 * Use for select one or more folders, support create and rename folder.
 *
 * @author zqxu
 */
public class JocFolderChooser extends JFileChooser
        implements OrchidAboutIntf {

  private static final String uiClassID = "OrchidFolderChooserUI";
  public static final int BUTTON_ALL = 0xFFFFFFFF;
  public static final int BUTTON_CREATE = 1;
  public static final int BUTTON_RENAME = 2;
  public static final int BUTTON_DELETE = 4;
  public static final int BUTTON_REFRESH = 8;
  private boolean editable;
  private boolean navigationBarVisible;
  private FolderFilter folderFilter;
  private int availableButtons;
  private boolean skipNetworkNeighbor;

  public JocFolderChooser() {
    super();
  }

  public JocFolderChooser(String currentDirectoryPath) {
    super(currentDirectoryPath);
  }

  public JocFolderChooser(File currentDirectory) {
    super(currentDirectory);
  }

  public JocFolderChooser(FileSystemView fsv) {
    super(fsv);
  }

  public JocFolderChooser(String currentDirectoryPath, FileSystemView fsv) {
    super(currentDirectoryPath, fsv);
  }

  public JocFolderChooser(File currentDirectory, FileSystemView fsv) {
    super(currentDirectory, fsv);
  }

  @Override
  protected void setup(FileSystemView view) {
    editable = true;
    navigationBarVisible = true;
    availableButtons = BUTTON_ALL;
    skipNetworkNeighbor = true;
    super.setup(view);
  }

  @Override
  public String getUIClassID() {
    return uiClassID;
  }

  @Override
  public void updateUI() {
    LookAndFeelManager.installOrchidUI();
    super.updateUI();
  }

  public boolean isNavigationBarVisible() {
    return navigationBarVisible;
  }

  public void setNavigationBarVisible(boolean navigationBarVisible) {
    boolean old = this.navigationBarVisible;
    if (old != navigationBarVisible) {
      this.navigationBarVisible = navigationBarVisible;
      firePropertyChange("navigationBarVisible", old, navigationBarVisible);
    }
  }

  @Override
  protected JDialog createDialog(Component parent) throws HeadlessException {
    String title = getUI().getDialogTitle(this);
    putClientProperty(AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY,
            title);

    Window window = OrchidUtils.getWindowOf(parent);
    JocModalDialog dialog = new JocModalDialog(window);
    dialog.setTitle(title);
    dialog.setComponentOrientation(this.getComponentOrientation());
    Container contentPane = dialog.getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(this, BorderLayout.CENTER);
    if (JDialog.isDefaultLookAndFeelDecorated()) {
      boolean supportsWindowDecorations =
              UIManager.getLookAndFeel().getSupportsWindowDecorations();
      if (supportsWindowDecorations) {
        dialog.getRootPane().setWindowDecorationStyle(
                JRootPane.FILE_CHOOSER_DIALOG);
      }
    }

    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    return dialog;
  }

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    boolean old = this.editable;
    if (old != editable) {
      this.editable = editable;
      firePropertyChange("editable", old, editable);
    }
  }

  /**
   * Get the visibilities of each buttons on the title bar of dockable frame.
   *
   * @return the visibilities of each buttons. It's a bit wise OR of values
   * specified at BUTTON_XXX.
   */
  public int getAvailableButtons() {
    return availableButtons;
  }

  /**
   * Set the visibilities of each buttons on the title bar of dockable frame.
   *
   * @param availableButtons the visibilities of each buttons. It's a bit wise
   * OR of values specified at BUTTON_XXX.
   */
  public void setAvailableButtons(int availableButtons) {
    int old = this.availableButtons;
    if (old != availableButtons) {
      this.availableButtons = availableButtons;
      firePropertyChange("availableButtons", old, availableButtons);
    }
  }

  public FolderFilter getFolderFilter() {
    return folderFilter;
  }

  public void setFolderFilter(FolderFilter folderFilter) {
    FolderFilter old = this.folderFilter;
    if (old != folderFilter) {
      this.folderFilter = folderFilter;
      firePropertyChange("folderFilter", old, folderFilter);
    }
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
}
