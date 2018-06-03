/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 * Use to display a modal dialog, default use ModalityType.DOCUMENT_MODAL.
 * <br>Use showModal function to display dialog rather than setVisible.
 *
 * @author zqxu
 */
public class JocModalDialog extends JDialog implements OrchidAboutIntf {

  private InputMap inputMap;
  private Object escKeyAction;
  private Object enterKeyAction;
  private ModalResult modalResult;
  private ModalResult defaultCloseResult;

  /**
   * Constructor of JocModalDialog without owner and title.
   */
  public JocModalDialog() {
    this(null, null);
  }

  /**
   * Constructor of JocModalDialog with owner.
   *
   * @param owner owner window.
   */
  public JocModalDialog(Window owner) {
    this(owner, null);
  }

  /**
   * Constructor of JocModalDialog with owner and title.
   *
   * @param owner owner window.
   * @param title title text.
   */
  public JocModalDialog(Window owner, String title) {
    super(owner, title);
    initializeLocalVars();
    setModalityType(ModalityType.DOCUMENT_MODAL);
    setDefaultCloseResult(ModalResult.MR_CANCEL);
  }

  /**
   *
   */
  protected void initializeLocalVars() {
    initEscEnterKeyAction();
  }

  //init enter key action
  private void initEscEnterKeyAction() {
    escKeyAction = new Object();
    enterKeyAction = new Object();
    ActionMap actionMap = getRootPane().getActionMap();
    inputMap = getRootPane().getInputMap(
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    actionMap.put(escKeyAction, new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        escPressed();
      }
    });
    actionMap.put(enterKeyAction, new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Component c = getFocusOwner();
        if (c instanceof JButton) {
          ((JButton) c).doClick();
        } else {
          enterPressed();
        }
      }
    });
    setListenToEscKey(true);
    setListenToEnterKey(true);
  }

  /**
   * Returns dialog running result.
   *
   * @return dialog running result.
   */
  public ModalResult getModalResult() {
    return modalResult;
  }

  /**
   * Sets dialog running result and close dialog immediately.
   *
   * @param modalResult dialog running result.
   */
  public void setModalResult(ModalResult modalResult) {
    setModalResult(modalResult, true);
  }

  /**
   * Sets dialog running result and specific whether close or not.
   *
   * @param modalResult dialog running result.
   * @param closeWindow true for close dialog or false not.
   */
  public void setModalResult(ModalResult modalResult, boolean closeWindow) {
    this.modalResult = modalResult;
    if (closeWindow) {
      doDefaultCloseAction();
    }
  }

  /**
   * Returns default result when user click dialog close button, defaults is
   * ModalResult.MR_CANCEL.
   *
   * @return default result when user click dialog close button.
   */
  public ModalResult getDefaultCloseResult() {
    return defaultCloseResult;
  }

  /**
   * Sets default resutl when user click dialog close button.
   *
   * @param defaultCloseResult default resutl when user click dialog close
   * button.
   */
  public void setDefaultCloseResult(ModalResult defaultCloseResult) {
    this.defaultCloseResult = defaultCloseResult;
  }

  /**
   * Execute default close operation.
   *
   * @see JDialog#getDefaultCloseOperation()
   */
  public void doDefaultCloseAction() {
    processWindowEvent(new WindowEvent(JocModalDialog.this,
            WindowEvent.WINDOW_CLOSING));
  }

  /**
   * Returns whether listen to ESC key in dialog or not, defaults is true, when
   * user press ESC, escPressed function will be execute.
   *
   * @return true for listen to ESC key or false not.
   */
  public boolean getListenToEscKey() {
    KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
    return escKeyAction == inputMap.get(ks);
  }

  /**
   * Sets whether listen to ESC key in dialog or not.
   *
   * @param listen true for listen to ESC key or false not.
   */
  public void setListenToEscKey(boolean listen) {
    KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
    if (listen) {
      inputMap.put(ks, escKeyAction);
    } else {
      inputMap.remove(ks);
    }
  }

  /**
   * When listenToEscKey set to true, this function will be excute when user
   * press ESC key, default action is close dialog.
   */
  protected void escPressed() {
    doDefaultCloseAction();
  }

  /**
   * Returns whether listen to Enter key in dialog or not, defaults is true,
   * when user press Enter, enterPressed function will be execute.
   *
   * @return true for listen to Enter key or false not.
   */
  public boolean getListenToEnterKey() {
    KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    return enterKeyAction == inputMap.get(ks);
  }

  /**
   * Sets whether listen to Enter key in dialog or not.
   *
   * @param listen true for listen to Enter key or false not.
   */
  public void setListenToEnterKey(boolean listen) {
    KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    if (listen) {
      inputMap.put(ks, enterKeyAction);
    } else {
      inputMap.remove(ks);
    }
  }

  /**
   * When listenToEnterKey set to true, this function will be excute when user
   * press Enter key, default is no action.
   */
  protected void enterPressed() {
  }

  /**
   * Display this dialog and return result.
   *
   * @param centerInOwner true for display center in owner or false not.
   * @return dialog result.
   */
  public ModalResult showModal(boolean centerInOwner) {
    modalResult = getDefaultCloseResult();
    if (centerInOwner) {
      setLocationRelativeTo(getOwner());
    } else if (getLocation().equals(new Point())) {
      setLocationRelativeTo(null);
    }
    setVisible(true);
    return modalResult;
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

  /**
   * enum for modal dialog result
   */
  public static enum ModalResult {

    /**
     * user clicked OK button
     */
    MR_OK,
    /**
     * user clicked Cancel button
     */
    MR_CANCEL,
    /**
     * user clicked Yes button
     */
    MR_YES,
    /**
     * user clicked No button
     */
    MR_NO,
    /**
     * user clicked Yes To All button
     */
    MR_YESTOALL,
    /**
     * user clicked No To All button
     */
    MR_NOTOALL,
    /**
     * user clicked Retry button
     */
    MR_RETRY;
  }
}
