/**
 * *****************************************************************************
 * 2013, All rights reserved.
 * *****************************************************************************
 */
package com.xzq.osc.tree;

import com.xzq.osc.CheckState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
// Start of user code (user defined imports)

// End of user code
/**
 * Description of CheckedTreeSelectionModel.
 *
 * @author zqxu
 */
public class CheckedTreeSelectionModel extends DefaultTreeSelectionModel {

  /**
   * Constant used for <i>branchSelection</i> property that means that selection
   * state of branch node only decided by the node itself.
   */
  public final static int BRANCH_SELF = 21;
  /**
   * Constant used for <i>branchSelection</i> property that means that selection
   * state of branch node decided by the node and all of its children.
   */
  public final static int MIX_CHILDREN = 22;
  /**
   * Constant used for <i>branchSelection</i> property that means that selection
   * state of branch node not decided by the node itself but children of the
   * node.
   */
  public final static int RELY_CHILDREN = 23;
  private JTree tree;
  private int branchSelection = MIX_CHILDREN;
  private boolean branchThreeStates;
  private boolean extendAddSelection;
  private boolean extendRemoveSelection;
  private List<TreePath> partialList = new ArrayList<TreePath>();

  /**
   * The constructor.
   */
  public CheckedTreeSelectionModel(JTree tree) {
    // Start of user code constructor for CheckedTreeSelectionModel)
    super();
    this.tree = tree;
    // End of user code
  }

  public boolean isBranchThreeStates() {
    return branchThreeStates;
  }

  public void setBranchThreeStates(boolean branchThreeStates) {
    if (this.branchThreeStates != branchThreeStates) {
      this.branchThreeStates = branchThreeStates;
      if (!branchThreeStates) {
        partialList.clear();
      } else if (branchSelection == BRANCH_SELF) {
        setBranchSelection(MIX_CHILDREN);
      }
      tree.repaint(tree.getVisibleRect());
    }
  }

  /**
   * Returns branchSelection. one of the three constants: BRANCH_SELF,
   * MIX_CHILDREN, RELY_CHILDREN. default is BRANCH_SELF.
   *
   * @return branchSelection
   */
  public int getBranchSelection() {
    return this.branchSelection;
  }

  /**
   * Sets a value to attribute branchSelection. one of the three constants:
   * BRANCH_SELF, MIX_CHILDREN, RELY_CHILDREN.
   *
   * @param branchSelection
   */
  public void setBranchSelection(int branchSelection) {
    if (branchThreeStates && branchSelection == BRANCH_SELF) {
      throw new IllegalArgumentException(
              "Can not use BRANCH_SELF in branch three states.");
    }
    if (this.branchSelection != branchSelection) {
      this.branchSelection = branchSelection;
      if (getSelectionPaths() != null)
      setSelectionPaths(getSelectionPaths());
    }
  }

  /**
   * Returns extendAddSelection. Indicate that while branch node selection,
   * whether extends selection state of the node to its children or not. default
   * is false.
   *
   * @return extendAddSelection
   */
  public boolean isExtendAddSelection() {
    return this.extendAddSelection;
  }

  /**
   * Sets a value to attribute extendAddSelection.
   *
   * @param extendAddSelection
   */
  public void setExtendAddSelection(boolean extendAddSelection) {
    this.extendAddSelection = extendAddSelection;
  }

  public boolean isExtendRemoveSelection() {
    return extendRemoveSelection;
  }

  public void setExtendRemoveSelection(boolean extendRemoveSelection) {
    this.extendRemoveSelection = extendRemoveSelection;
  }

  public CheckState getRowSelectionState(int row) {
    return getPathSelectionState(tree.getPathForRow(row));
  }

  public CheckState getPathSelectionState(TreePath path) {
    if (partialList.contains(path)) {
      return CheckState.PARTIAL;
    }
    return isPathSelected(path) ? CheckState.CHECKED : CheckState.UNCHECK;
  }

  @Override
  public void addSelectionPaths(TreePath[] paths) {
    if (isExtendAddSelection()) {
      paths = includeDescendantPaths(paths, true);
    }
    if (branchSelection != BRANCH_SELF) {
      paths = includeAncestorPaths(paths);
    }
    if (branchThreeStates) {
      List<TreePath> pathList = new ArrayList<TreePath>();
      pathList.addAll(Arrays.asList(paths));
      TreePath[] selected = getSelectionPaths();
      if (selected != null) {
        pathList.addAll(Arrays.asList(selected));
      }
      parsePartialFromPathList(pathList);
    }
    super.addSelectionPaths(paths);
    tree.repaint(tree.getVisibleRect());
  }

  @Override
  public void setSelectionPaths(TreePath[] paths) {
    if (isExtendAddSelection()) {
      paths = includeDescendantPaths(paths, true);
    }
    if (branchSelection != BRANCH_SELF) {
      paths = includeAncestorPaths(paths);
    }
    if (branchThreeStates) {
      parsePartialFromPathList(Arrays.asList(paths));
    }
    super.setSelectionPaths(paths);
    tree.repaint(tree.getVisibleRect());
  }

  protected TreePath[] includeDescendantPaths(TreePath[] paths,
          boolean forAdd) {
    List<TreePath> pathList = new ArrayList<TreePath>();
    pathList.addAll(Arrays.asList(paths));
    for (TreePath path : paths) {
      includeChildren(path, forAdd, pathList);
    }
    return pathList.toArray(new TreePath[0]);
  }

  private void includeChildren(TreePath path, boolean forAdd,
          List<TreePath> pathList) {
    TreeModel model = tree.getModel();
    Object parent = path.getLastPathComponent();
    if (model.isLeaf(parent)) {
      return;
    }
    for (int row = 0; row < model.getChildCount(parent); row++) {
      Object node = model.getChild(parent, row);
      TreePath child = path.pathByAddingChild(node);
      if (!pathList.contains(child)) {
        if ((forAdd && !isPathSelected(child))
                || (!forAdd && isPathSelected(child))) {
          pathList.add(child);
        }
      }
      includeChildren(child, forAdd, pathList);
    }
  }

  protected TreePath[] includeAncestorPaths(TreePath[] paths) {
    List<TreePath> pathList = new ArrayList<TreePath>();
    TreeModel model = tree.getModel();
    for (TreePath path : paths) {
      boolean leaf = model.isLeaf(path.getLastPathComponent());
      if (branchSelection == RELY_CHILDREN && !leaf) {
        continue;
      }
      TreePath parent = path.getParentPath();
      while (parent != null) {
        if (!pathList.contains(parent) && !isPathSelected(parent)) {
          pathList.add(parent);
        }
        parent = parent.getParentPath();
      }
      if (!isPathSelected(path)) {
        pathList.add(path);
      }
    }
    return pathList.toArray(new TreePath[0]);
  }

  private void parsePartialFromPathList(List<TreePath> pathList) {
    TreeModel model = tree.getModel();
    Map<TreePath, Integer> counterMap = new HashMap<TreePath, Integer>();
    partialList.clear();
    for (TreePath path : pathList) {
      Object node = path.getLastPathComponent();
      if (model.isLeaf(node)) {
        continue;
      }
      int count = model.getChildCount(node);
      int selected = getChildrenSelected(path, model, pathList, counterMap);
      if (selected < count) {
        partialList.add(path);
      }
    }
    counterMap.clear();
  }

  private int getChildrenSelected(TreePath parent, TreeModel model,
          List<TreePath> selectedList, Map<TreePath, Integer> counterMap) {
    if (counterMap.containsKey(parent)) {
      return counterMap.get(parent);
    }
    int selectedCount = 0;
    Object parentNode = parent.getLastPathComponent();
    int count = model.getChildCount(parentNode);
    for (int index = 0; index < count; index++) {
      Object childNode = model.getChild(parentNode, index);
      TreePath child = parent.pathByAddingChild(childNode);
      if (model.isLeaf(childNode)) {
        if (selectedList.contains(child)) {
          selectedCount++;
        }
      } else {
        int childCount = model.getChildCount(childNode);
        if (childCount == getChildrenSelected(child, model,
                selectedList, counterMap)) {
          selectedCount++;
        }
      }
    }
    counterMap.put(parent, selectedCount);
    return selectedCount;
  }

  @Override
  public void removeSelectionPaths(TreePath[] paths) {
    if (isExtendRemoveSelection()) {
      paths = includeDescendantPaths(paths, false);
    }
    if (branchSelection != BRANCH_SELF) {
      paths = excludeAncestorPaths(paths);
    }
    super.removeSelectionPaths(paths);
    tree.repaint(tree.getVisibleRect());
  }

  private TreePath[] excludeAncestorPaths(TreePath[] paths) {
    List<TreePath> pathList = new ArrayList<TreePath>();
    //map contains selected count of branch node and its children
    Map<TreePath, Integer> branchMap = new HashMap<TreePath, Integer>();
    TreeModel model = tree.getModel();
    for (TreePath path : paths) {
      TreePath branch = null;
      if (model.isLeaf(path.getLastPathComponent())) {
        pathList.add(path);
        branch = path.getParentPath();
        if (branchThreeStates) {
          includeAncestorsPartial(branch);
        }
      } else if (branchSelection == MIX_CHILDREN) {
        branch = path;
      }
      checkWhetherExcludeBranch(branch, branchMap, pathList);
    }
    return pathList.toArray(new TreePath[0]);
  }

  private void includeAncestorsPartial(TreePath branch) {
    TreePath parent = branch;
    while (parent != null) {
      if (partialList.contains(parent)) {
        break;
      }
      partialList.add(parent);
      parent = parent.getParentPath();
    }
  }

  private void checkWhetherExcludeBranch(TreePath branch,
          Map<TreePath, Integer> branchMap, List<TreePath> pathList) {
    if (branch == null) {
      return;
    }
    if (decAndGetSelectedCount(branch, branchMap) == 0) {
      pathList.add(branch);
      partialList.remove(branch);
      checkWhetherExcludeBranch(branch.getParentPath(), branchMap, pathList);
    }
  }

  private int decAndGetSelectedCount(TreePath branch,
          Map<TreePath, Integer> branchMap) {
    int selectedCount = 0;
    if (branchMap.containsKey(branch)) {
      selectedCount = branchMap.get(branch);
    } else {
      if (branchSelection == MIX_CHILDREN && isPathSelected(branch)) {
        selectedCount++;
      }
      TreeModel model = tree.getModel();
      Object node = branch.getLastPathComponent();
      int childCount = model.getChildCount(node);
      for (int index = 0; index < childCount; index++) {
        Object child = model.getChild(node, index);
        if (isPathSelected(branch.pathByAddingChild(child))) {
          selectedCount++;
        }
      }
    }
    branchMap.put(branch, --selectedCount);
    return selectedCount;
  }
}
