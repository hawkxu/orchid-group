/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.plaf.basic;

import com.xzq.osc.JocFolderChooser;
import com.xzq.osc.OrchidLocale;
import com.xzq.osc.OrchidUtils;
import com.xzq.osc.folderchooser.FolderFilter;
import com.xzq.osc.resource.Resource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.tree.*;
import javax.swing.tree.DefaultTreeCellEditor.DefaultTextField;

/**
 *
 * @author zqxu
 */
public class BasicOrchidFolderChooserUI extends BasicFileChooserUI {

  private JPanel containerPanel;
  private JPanel navigationPanel;
  private JPanel buttonPanel;
  private JScrollPane scpFolderTree;
  private ApproveSelectionAction approveAction;
  private CreateFolderAction createFolderAction;
  private RenameFolderAction renameFolderAction;
  private DeleteFolderAction deleteFolderAction;
  private RefreshFolderAction refreshFolderAction;
  private FolderCellRenderer folderCellRenderer;
  private FolderCellEditor folderCellEditor;
  private CellEditorListener folderEditorListener;
  private BasicFolderTreeNode rootFolderNode;
  private Cursor savedCursor;
  protected JTree folderTree;
  protected JTextField folderField;
  protected JButton createButton;
  protected JButton renameButton;
  protected JButton deleteButton;
  protected JButton refreshButton;
  protected JButton approveButton;
  protected JButton cancelButton;

  public static ComponentUI createUI(JComponent c) {
    return new BasicOrchidFolderChooserUI((JFileChooser) c);
  }

  /**
   *
   * @param chooser
   */
  public BasicOrchidFolderChooserUI(JFileChooser chooser) {
    super(chooser);
  }

  /**
   *
   * @return
   */
  @Override
  public JocFolderChooser getFileChooser() {
    return (JocFolderChooser) super.getFileChooser();
  }

  /**
   *
   * @param fileChooser
   */
  @Override
  public void installComponents(JFileChooser fileChooser) {
    JocFolderChooser fc = (JocFolderChooser) fileChooser;
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fc.setLayout(new BorderLayout());
    fc.add(getContainerPanel(fc), BorderLayout.CENTER);
    updateFolderChooserView(fc);
  }

  private JPanel getContainerPanel(JocFolderChooser fc) {
    if (containerPanel == null) {
      containerPanel = createContainerPanel(fc);
    }
    return containerPanel;
  }

  private JPanel createContainerPanel(JocFolderChooser fc) {
    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    panel.setLayout(new BorderLayout(0, 5));
    panel.add(getNavigationPanel(fc), BorderLayout.NORTH);
    panel.add(getButtonPanel(fc), BorderLayout.SOUTH);
    panel.add(getFolderTreeScrollPane(fc), BorderLayout.CENTER);
    return panel;
  }

  private JPanel getNavigationPanel(JocFolderChooser fc) {
    if (navigationPanel == null) {
      navigationPanel = createNavigationPanel(fc);
    }
    return navigationPanel;
  }

  protected JPanel createNavigationPanel(JocFolderChooser fc) {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    folderField = new JTextField();
    folderField.setEditable(false);
    folderField.setBackground(Color.LIGHT_GRAY);
    panel.add(folderField, BorderLayout.CENTER);
    JToolBar navToolBar = new JToolBar();
    navToolBar.setFloatable(false);
    createButton = new JButton();
    createButton.setAction(getCreateFolderAction());
    renameButton = new JButton();
    renameButton.setAction(getRenameFolderAction());
    deleteButton = new JButton();
    deleteButton.setAction(getRemoveFolderAction());
    refreshButton = new JButton();
    refreshButton.setAction(getRefreshFolderAction());
    navToolBar.add(createButton);
    navToolBar.add(renameButton);
    navToolBar.add(deleteButton);
    navToolBar.add(refreshButton);
    panel.add(navToolBar, BorderLayout.EAST);
    return panel;
  }

  /**
   *
   * @return
   */
  public Action getCreateFolderAction() {
    if (createFolderAction == null) {
      createFolderAction = new CreateFolderAction();
    }
    return createFolderAction;
  }

  /**
   *
   * @return
   */
  public Action getRenameFolderAction() {
    if (renameFolderAction == null) {
      renameFolderAction = new RenameFolderAction();
    }
    return renameFolderAction;
  }

  /**
   *
   * @return
   */
  public Action getRemoveFolderAction() {
    if (deleteFolderAction == null) {
      deleteFolderAction = new DeleteFolderAction();
    }
    return deleteFolderAction;
  }

  /**
   *
   * @return
   */
  public Action getRefreshFolderAction() {
    if (refreshFolderAction == null) {
      refreshFolderAction = new RefreshFolderAction();
    }
    return refreshFolderAction;
  }

  private JPanel getButtonPanel(JocFolderChooser fc) {
    if (buttonPanel == null) {
      buttonPanel = createButtonPanel(fc);
    }
    return buttonPanel;
  }

  /**
   *
   * @param fc
   * @return
   */
  protected JPanel createButtonPanel(JocFolderChooser fc) {
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.TRAILING, 6, 0));
    approveButton = new JButton();
    approveButton.setAction(getApproveSelectionAction());
    panel.add(approveButton);
    cancelButton = new JButton();
    cancelButton.setAction(getCancelSelectionAction());
    cancelButton.setText(
            UIManager.getString("FileChooser.cancelButtonText"));
    cancelButton.setMnemonic(
            UIManager.getInt("FileChooser.cancelButtonMnemonic"));
    panel.add(cancelButton);
    return panel;
  }

  /**
   *
   * @return
   */
  @Override
  public Action getApproveSelectionAction() {
    if (approveAction == null) {
      approveAction = new ApproveSelectionAction();
    }
    return approveAction;
  }

  private JScrollPane getFolderTreeScrollPane(JocFolderChooser fc) {
    if (scpFolderTree == null) {
      scpFolderTree = createFolderTreeScrollPane(fc);
    }
    return scpFolderTree;
  }

  /**
   *
   * @param fc
   * @return
   */
  protected JScrollPane createFolderTreeScrollPane(JocFolderChooser fc) {
    JScrollPane pane = new JScrollPane();
    folderTree = new JTree(createRootFolderNode(fc));
    folderTree.setCellRenderer(getFolderCellRenderer());
    folderTree.setCellEditor(getFolderCellEditor());
    folderTree.expandRow(0);
    folderTree.setRootVisible(false);
    folderTree.setShowsRootHandles(true);
    folderTree.setInvokesStopCellEditing(true);
    FolderTreeHandler handler = new FolderTreeHandler();
    folderTree.addTreeSelectionListener(handler);
    folderTree.addTreeWillExpandListener(handler);
    folderTree.addTreeExpansionListener(handler);
    updateMultiSelectionEnabled(fc);
    pane.setViewportView(folderTree);
    pane.setPreferredSize(new Dimension(300, 300));
    return pane;
  }

  /**
   *
   * @param fc
   * @return
   */
  protected BasicFolderTreeNode createRootFolderNode(JocFolderChooser fc) {
    if (rootFolderNode == null) {
      rootFolderNode = new BasicFolderTreeNode(fc);
    }
    return rootFolderNode;
  }

  /**
   *
   * @return
   */
  protected DefaultTreeCellRenderer getFolderCellRenderer() {
    if (folderCellRenderer == null) {
      folderCellRenderer = new FolderCellRenderer();
    }
    return folderCellRenderer;
  }

  /**
   *
   * @return
   */
  protected DefaultTreeCellEditor getFolderCellEditor() {
    if (folderCellEditor == null) {
      folderCellEditor = new FolderCellEditor(
              folderTree, getFolderCellRenderer());
      folderCellEditor.addCellEditorListener(getFolderEditorListener());
    }
    return folderCellEditor;
  }

  private CellEditorListener getFolderEditorListener() {
    if (folderEditorListener == null) {
      folderEditorListener = new FolderEditorListener();
    }
    return folderEditorListener;
  }

  private void updateMultiSelectionEnabled(JFileChooser fc) {
    if (fc.isMultiSelectionEnabled()) {
      folderTree.getSelectionModel().setSelectionMode(
              TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    } else {
      folderTree.getSelectionModel().setSelectionMode(
              TreeSelectionModel.SINGLE_TREE_SELECTION);
    }
  }

  private void updateFolderChooserView(JocFolderChooser fc) {
    approveButton.setText(getApproveButtonText(fc));
    approveButton.setMnemonic(getApproveButtonMnemonic(fc));
    buttonPanel.setVisible(fc.getControlButtonsAreShown());
    navigationPanel.setVisible(fc.isNavigationBarVisible());
    folderTree.setEditable(fc.isEditable());
    createFolderAction.setEnabled(fc.isEditable());
    renameFolderAction.setEnabled(fc.isEditable());
    deleteFolderAction.setEnabled(fc.isEditable());
  }

  private void updateAvailableButtons(JocFolderChooser fc) {
    int flags = fc.getAvailableButtons();
    createButton.setVisible(hasBit(flags, JocFolderChooser.BUTTON_CREATE));
    renameButton.setVisible(hasBit(flags, JocFolderChooser.BUTTON_RENAME));
    deleteButton.setVisible(hasBit(flags, JocFolderChooser.BUTTON_DELETE));
    refreshButton.setVisible(hasBit(flags, JocFolderChooser.BUTTON_REFRESH));
  }

  private boolean hasBit(int flags, int bit) {
    return (flags & bit) != 0;
  }

  /**
   *
   * @param fc
   * @param file
   */
  @Override
  public void ensureFileIsVisible(JFileChooser fc, File file) {
    if (isFileCurrentSelected(file)) {
      return;
    }
    final TreePath path;
    if (file == null) {
      file = fc.getFileSystemView().getHomeDirectory();
    }
    path = getTreePathForFile(file);
    if (path != null) {
      folderTree.setSelectionPath(path);
//      folderTree.expandPath(path);
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          folderTree.scrollPathToVisible(path);
        }
      };
      SwingUtilities.invokeLater(runnable);
    }
  }

  /**
   *
   * @param fc
   * @param files
   */
  public void ensureFilesVisible(JocFolderChooser fc, File[] files) {
    for (File file : files) {
      if (file == null || isFileCurrentSelected(file)) {
        continue;
      }
      TreePath path = getTreePathForFile(file);
      if (path != null) {
        folderTree.addSelectionPath(path);
      }
    }
  }

  private boolean isFileCurrentSelected(File file) {
    TreePath[] selections = folderTree.getSelectionPaths();
    if (selections == null || selections.length == 0) {
      return false;
    }
    for (TreePath path : selections) {
      Object node = path.getLastPathComponent();
      if (((BasicFolderTreeNode) node).getFile().equals(file)) {
        return true;
      }
    }
    return false;
  }

  private TreePath getTreePathForFile(File file) {
    if (!file.isDirectory()) {
      return null;
    }
    ArrayList<File> filePath = new ArrayList<File>();
    JocFolderChooser fc = getFileChooser();
    FileSystemView fsv = fc.getFileSystemView();
    while (file != null) {
      filePath.add(0, file);
      file = fsv.getParentDirectory(file);
    }
    Object rootNode = folderTree.getModel().getRoot();
    ArrayList<TreeNode> nodeList = new ArrayList<TreeNode>();
    BasicFolderTreeNode node = (BasicFolderTreeNode) rootNode;
    nodeList.add(node);
    for (File dir : filePath) {
      node = getChildFileNode(node, dir);
      if (node == null) {
        return null;
      } else {
        nodeList.add(node);
      }
    }
    return new TreePath(nodeList.toArray());
  }

  private BasicFolderTreeNode getChildFileNode(BasicFolderTreeNode parent,
          File child) {
    for (int i = 0; i < parent.getChildCount(); i++) {
      BasicFolderTreeNode node = (BasicFolderTreeNode) parent.getChildAt(i);
      if (node.getFile().equals(child)) {
        return node;
      }
    }
    return null;
  }

  /**
   *
   * @param node
   */
  protected void refreshNode(BasicFolderTreeNode node) {
    File file = node.getFile();
    if (!node.isInCreate() && (file == null || file.isDirectory())) {
      node.clear();
      DefaultTreeModel model = (DefaultTreeModel) folderTree.getModel();
      model.nodeStructureChanged(node);
    }
  }

  /**
   *
   * @param fc
   */
  protected void setWaitCursor(JocFolderChooser fc) {
    if (fc.getCursor().getType() != Cursor.WAIT_CURSOR) {
      savedCursor = fc.getCursor();
      fc.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
  }

  /**
   *
   * @param fc
   */
  protected void restoreCursor(JocFolderChooser fc) {
    if (savedCursor != null) {
      fc.setCursor(savedCursor);
    }
  }

  private File[] getNodesFiles(TreePath[] nodes) {
    if (nodes == null || nodes.length == 0) {
      return null;
    }
    ArrayList<File> fileList = new ArrayList<File>();
    for (int i = 0; i < nodes.length; i++) {
      Object node = nodes[i].getLastPathComponent();
      fileList.add(((BasicFolderTreeNode) node).getFile());
    }
    return fileList.toArray(new File[0]);
  }

  /**
   *
   * @param fc
   * @return
   */
  @Override
  public PropertyChangeListener createPropertyChangeListener(
          JFileChooser fc) {
    return new FolderChooserPropertyChangeListener();
  }

  private class FolderChooserPropertyChangeListener
          implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      JocFolderChooser fc = (JocFolderChooser) evt.getSource();
      String propertyName = evt.getPropertyName();
      if (JFileChooser.APPROVE_BUTTON_TEXT_CHANGED_PROPERTY.equals(
              propertyName)) {
        updateFolderChooserView(fc);
      } else if (JFileChooser.DIALOG_TYPE_CHANGED_PROPERTY.equals(
              propertyName)) {
        updateFolderChooserView(fc);
      } else if (JFileChooser.MULTI_SELECTION_ENABLED_CHANGED_PROPERTY.equals(
              propertyName)) {
        updateMultiSelectionEnabled(fc);
      } else if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(
              propertyName)) {
        if (fc.getSelectedFile() == null) {
          ensureFileIsVisible(fc, fc.getCurrentDirectory());
        }
      } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(
              propertyName)) {
        File selected = fc.getSelectedFile();
        if (selected != null) {
          ensureFileIsVisible(fc, selected);
        } else {
          ensureFileIsVisible(fc, fc.getCurrentDirectory());
        }
      } else if (JFileChooser.SELECTED_FILES_CHANGED_PROPERTY.equals(
              propertyName)) {
        ensureFilesVisible(fc, fc.getSelectedFiles());
      } else if (JFileChooser.CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY.equals(
              propertyName)) {
        updateFolderChooserView(fc);
      } else if (JFileChooser.FILE_SELECTION_MODE_CHANGED_PROPERTY.equals(
              propertyName)) {
        refreshRootNode(fc);
      } else if ("navigationBarVisible".equals(propertyName)) {
        updateFolderChooserView(fc);
      } else if ("editable".equals(propertyName)) {
        updateFolderChooserView(fc);
      } else if ("folderFilter".equals(propertyName)) {
        refreshRootNode(fc);
      } else if ("availableButtons".equals(propertyName)) {
        updateAvailableButtons(fc);
      } else if ("skipNetworkNeighbor".equals(propertyName)) {
        refreshRootNode(fc);
      }
    }

    private void refreshRootNode(JocFolderChooser fc) {
      setWaitCursor(fc);
      File selected = getSelectedFile();
      File[] selections = getNodesFiles(folderTree.getSelectionPaths());
      Object rootNode = folderTree.getModel().getRoot();
      refreshNode((BasicFolderTreeNode) rootNode);
      if (selected != null) {
        ensureFileIsVisible(fc, selected);
      }
      if (selections != null && selections.length > 1) {
        ensureFilesVisible(fc, selections);
      }
      restoreCursor(fc);
    }

    private File getSelectedFile() {
      TreePath path = folderTree.getLeadSelectionPath();
      return path == null ? null
              : ((BasicFolderTreeNode) path.getLastPathComponent()).getFile();
    }
  }

  private class ApproveSelectionAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
      JocFolderChooser fc = getFileChooser();
      File[] selections = getNodesFiles(folderTree.getSelectionPaths());
      if (selections == null) {
        fc.setSelectedFile(null);
      }
      fc.setSelectedFiles(selections);
      fc.approveSelection();
    }
  }

  private class CreateFolderAction extends AbstractAction {

    public CreateFolderAction() {
      putValue(SMALL_ICON, UIManager.getIcon("FileChooser.newFolderIcon"));
      putValue(SHORT_DESCRIPTION,
              UIManager.getString("FileChooser.newFolderToolTipText"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      TreePath parentPath = folderTree.getLeadSelectionPath();
      if (parentPath == null) {
        return;
      }
      BasicFolderTreeNode parentNode =
              (BasicFolderTreeNode) parentPath.getLastPathComponent();
      BasicFolderTreeNode childNode = parentNode.createChildFolder();
      DefaultTreeModel treeModel = (DefaultTreeModel) folderTree.getModel();
      treeModel.nodeStructureChanged((BasicFolderTreeNode) parentNode);
      TreePath newPath = parentPath.pathByAddingChild(childNode);
      folderTree.setSelectionPath(newPath);
      folderTree.scrollPathToVisible(newPath);
      folderTree.startEditingAtPath(newPath);
    }
  }

  private class RenameFolderAction extends AbstractAction {

    public RenameFolderAction() {
      putValue(SMALL_ICON, Resource.getOrchidIcon("folder_rename.png"));
      putValue(SHORT_DESCRIPTION, OrchidLocale.getString("FOLDER_RENAME_TOOLTIP"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      TreePath folderPath = folderTree.getLeadSelectionPath();
      if (folderPath == null) {
        return;
      }
      folderTree.startEditingAtPath(folderPath);
    }
  }

  private class DeleteFolderAction extends AbstractAction {

    public DeleteFolderAction() {
      putValue(SMALL_ICON, Resource.getOrchidIcon("folder_delete.png"));
      putValue(SHORT_DESCRIPTION, OrchidLocale.getString("FOLDER_DELETE_TOOLTIP"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      TreePath[] selections = folderTree.getSelectionPaths();
      if (selections == null || selections.length == 0) {
        return;
      }
      JocFolderChooser fc = getFileChooser();
      String warning = OrchidLocale.getString("FOLDER_DELETE_WARNING");
      if (!OrchidUtils.confirmWarnYes(fc, warning)) {
        return;
      }
      setWaitCursor(fc);
      File[] fileArray = getNodesFiles(selections);
      ArrayList<File> fileList = new ArrayList<File>();
      boolean success = true;
      for (File file : fileArray) {
        success = OrchidUtils.recursiveDeleteFile(file) && success;
        if (file.exists()) {
          fileList.add(file);
        }
      }
      for (TreePath path : selections) {
        path = path.getParentPath();
        refreshNode((BasicFolderTreeNode) path.getLastPathComponent());
      }
      if (!success) {
        OrchidUtils.msgError(fc, OrchidLocale.getString("FOLDER_DELETE_ERROR"));
      }
      if (!fileList.isEmpty()) {
        ensureFilesVisible(fc, fileList.toArray(new File[0]));
      }
      restoreCursor(fc);
    }
  }

  private class RefreshFolderAction extends AbstractAction {

    public RefreshFolderAction() {
      putValue(SMALL_ICON, Resource.getOrchidIcon("folder_refresh.png"));
      putValue(SHORT_DESCRIPTION, OrchidLocale.getString("FOLDER_UPDATE_TOOLTIP"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      TreePath[] selections = folderTree.getSelectionPaths();
      if (selections == null || selections.length == 0) {
        return;
      }
      JocFolderChooser fc = getFileChooser();
      setWaitCursor(fc);
      for (TreePath path : selections) {
        refreshNode((BasicFolderTreeNode) path.getLastPathComponent());
      }
      restoreCursor(fc);
    }
  }

  private class FolderTreeHandler implements TreeSelectionListener,
          TreeExpansionListener, TreeWillExpandListener {

    @Override
    public void valueChanged(TreeSelectionEvent e) {
      File file = null;
      TreePath path = e.getNewLeadSelectionPath();
      if (path != null) {
        file = ((BasicFolderTreeNode) path.getLastPathComponent()).getFile();
      }
      if (file == null) {
        folderField.setText("");
      } else {
        folderField.setText(file.getAbsolutePath());
        folderField.setCaretPosition(0);
      }
      JocFolderChooser fc = getFileChooser();
      boolean accept = isFileAccept(fc, file);
      if (accept) {
        fc.setSelectedFile(file);
        File[] selections = getNodesFiles(folderTree.getSelectionPaths());
        if (selections != null && selections.length > 0) {
          for (File selected : selections) {
            if (!selected.equals(file)) {
              accept = accept && isFileAccept(fc, selected);
            }
            if (!accept) {
              break;
            }
          }
        }
      }
      getApproveSelectionAction().setEnabled(accept);
    }

    private boolean isFileAccept(JocFolderChooser fc, File file) {
      int mode = fc.getFileSelectionMode();
      if (file == null
              || (file.isFile() && mode == JFileChooser.DIRECTORIES_ONLY)
              || (file.isDirectory() && mode == JFileChooser.FILES_ONLY)) {
        return false;
      }
      FolderFilter filter = fc.getFolderFilter();
      return filter == null || filter.accept(file);
    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event) {
      setWaitCursor(getFileChooser());
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) {
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
      restoreCursor(getFileChooser());
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
    }
  }

  private class FolderEditorListener implements CellEditorListener {

    @Override
    public void editingStopped(ChangeEvent e) {
      TreePath childPath = folderTree.getLeadSelectionPath();
      TreeNode childNode = (TreeNode) childPath.getLastPathComponent();
      TreePath parentPath = childPath.getParentPath();
      TreeNode parentNode = childNode.getParent();
      ((BasicFolderTreeNode) parentNode).clear();
      DefaultTreeModel model = (DefaultTreeModel) folderTree.getModel();
      model.nodeStructureChanged(parentNode);
      int index = parentNode.getIndex(childNode);
      if (index != -1) {
        childNode = parentNode.getChildAt(index);
        childPath = parentPath.pathByAddingChild(childNode);
        folderTree.setSelectionPath(childPath);
      }
    }

    @Override
    public void editingCanceled(ChangeEvent e) {
      TreePath childPath = folderTree.getLeadSelectionPath();
      BasicFolderTreeNode childNode =
              (BasicFolderTreeNode) childPath.getLastPathComponent();
      if (!childNode.isInCreate()) {
        return;
      }
      TreePath parentPath = childPath.getParentPath();
      BasicFolderTreeNode parentNode =
              (BasicFolderTreeNode) parentPath.getLastPathComponent();
      parentNode.remove(childNode);
      DefaultTreeModel model = (DefaultTreeModel) folderTree.getModel();
      model.nodeStructureChanged(parentNode);
      folderTree.setSelectionPath(parentPath);
      folderTree.scrollPathToVisible(parentPath);
    }
  }

  private static class FolderCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
      Component renderer = super.getTreeCellRendererComponent(tree, value, sel,
              expanded, leaf, row, hasFocus);
      if (value instanceof BasicFolderTreeNode) {
        ((JLabel) renderer).setIcon(((BasicFolderTreeNode) value).getIcon());
      }
      return renderer;
    }
  }

  private static class FolderCellEditor extends DefaultTreeCellEditor {

    private static final Border normBorder =
            BorderFactory.createLineBorder(Color.BLACK);
    private static final Border errorBorder =
            BorderFactory.createLineBorder(Color.RED);

    public FolderCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
      super(tree, renderer);
    }

    @Override
    protected TreeCellEditor createTreeCellEditor() {
      DefaultCellEditor editor = new DefaultCellEditor(
              new DefaultTextField(normBorder)) {
        @Override
        public boolean stopCellEditing() {
          TreePath path = tree.getEditingPath();
          Object o = path.getLastPathComponent();
          BasicFolderTreeNode node = (BasicFolderTreeNode) o;
          String newName = (String) getCellEditorValue();
          if (OrchidUtils.isEmpty(newName)) {
            cancelCellEditing();
            return false;
          }
          boolean success = node.renameFile(newName);
          if (!success) {
            editorComponent.setBorder(errorBorder);
          } else {
            editorComponent.setBorder(normBorder);
          }
          return success && super.stopCellEditing();
        }

        @Override
        public void cancelCellEditing() {
          super.cancelCellEditing();
          editorComponent.setBorder(normBorder);
        }
      };
      // One click to edit.
      editor.setClickCountToStart(1);
      return editor;
    }

    @Override
    protected void determineOffset(JTree tree, Object value,
            boolean isSelected, boolean expanded, boolean leaf, int row) {
      BasicFolderTreeNode node = (BasicFolderTreeNode) value;
      editingIcon = node.getIcon();
      offset = renderer.getIconTextGap() + editingIcon.getIconWidth();
    }
  }
}
