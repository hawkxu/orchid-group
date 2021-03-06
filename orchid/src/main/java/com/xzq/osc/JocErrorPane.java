/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JocErrorPane.java
 *
 * Created on 2010-6-4, 11:29:07
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.plaf.LookAndFeelManager;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.*;

/**
 *
 * @author zqxu
 */
public class JocErrorPane extends JPanel implements OrchidAboutIntf {

  private Throwable cause;

  /**
   * Constructor for JocErrorPane
   */
  public JocErrorPane() {
    initComponents();
    lbIcon.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
  }

  @Override
  public void updateUI() {
    LookAndFeelManager.installOrchidUI();
    super.updateUI();
  }

  /**
   * Show a modal dialog with given error object message.
   *
   * @param parent component for determine parent window
   * @param title error dialog title.
   * @param error an error obecjt.
   */
  public static void showErrorDialog(Component parent, String title,
          Throwable error) {
    JocErrorPane pane = new JocErrorPane();
    pane.setCause(error);
    pane.showErrorDialog(OrchidUtils.getWindowOf(parent), title);
  }

  //create dialog
  private JocModalDialog createDialog(Window parent, String title) {
    JocModalDialog dialog = new JocModalDialog(parent) {
      private JButton btnOK;

      @Override
      protected void initializeLocalVars() {
        super.initializeLocalVars();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        getContentPane().setLayout(new BorderLayout(0, 8));
        getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            btnOK.requestFocus();
          }
        });
      }

      private JPanel createButtonPanel() {
        btnOK = new JButton();
        btnOK.setIcon(new ImageIcon(getClass().getResource("img/ok.png")));
        btnOK.setText(UIManager.getString("OptionPane.okButtonText"));
        btnOK.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            doDefaultCloseAction();
          }
        });
        JPanel pnButton = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        pnButton.add(btnOK);
        return pnButton;
      }

      @Override
      protected void enterPressed() {
        doDefaultCloseAction();
      }
    };
    dialog.setTitle(title);
    dialog.getContentPane().add(this, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    return dialog;
  }

  /**
   * Display a modal dialog for current error object (if any).
   *
   * @param parent parent window
   * @param title error dialog title.
   */
  public void showErrorDialog(Component parent, String title) {
    createDialog(OrchidUtils.getWindowOf(parent), title).showModal(true);
  }

  /**
   * Returns current error object
   *
   * @return a throwable object
   */
  public Throwable getCause() {
    return cause;
  }

  /**
   * Sets current error object
   *
   * @param cause a throwable object
   */
  public void setCause(Throwable cause) {
    this.cause = cause;
    String messagesText = "";
    StringWriter detailedWriter = new StringWriter();
    PrintWriter detailedPrinter = new PrintWriter(detailedWriter);
    while (cause != null) {
      String message = String.valueOf(cause.getMessage());
      if (!messagesText.endsWith(message)) {
        messagesText += messagesText.isEmpty() ? message : "\n" + message;
      }
      cause.printStackTrace(detailedPrinter);
      cause = cause.getCause();
    }
    txaMessages.setText(messagesText);
    txaMessages.setCaretPosition(0);
    txaDetails.setText(detailedWriter.toString());
    detailedPrinter.close();
    txaDetails.setCaretPosition(0);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel2 = new javax.swing.JPanel();
    pnMessages = new javax.swing.JPanel();
    scpMessages = new javax.swing.JScrollPane();
    txaMessages = new javax.swing.JTextArea();
    lbIcon = new javax.swing.JLabel();
    pnDetails = new com.xzq.osc.JocGroupPane();
    scpDetails = new javax.swing.JScrollPane();
    txaDetails = new javax.swing.JTextArea();

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    );

    setLayout(new java.awt.BorderLayout(0, 8));

    pnMessages.setPreferredSize(new java.awt.Dimension(438, 90));
    pnMessages.setRequestFocusEnabled(false);

    scpMessages.setBorder(null);

    txaMessages.setEditable(false);
    txaMessages.setFont(lbIcon.getFont());
    txaMessages.setLineWrap(true);
    txaMessages.setOpaque(false);
    scpMessages.setViewportView(txaMessages);

    javax.swing.GroupLayout pnMessagesLayout = new javax.swing.GroupLayout(pnMessages);
    pnMessages.setLayout(pnMessagesLayout);
    pnMessagesLayout.setHorizontalGroup(
      pnMessagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(pnMessagesLayout.createSequentialGroup()
        .addComponent(lbIcon)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(scpMessages))
    );
    pnMessagesLayout.setVerticalGroup(
      pnMessagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(lbIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
      .addComponent(scpMessages)
    );

    add(pnMessages, java.awt.BorderLayout.PAGE_START);

    pnDetails.setExpanded(false);
    pnDetails.setText("Detailed Information");
    pnDetails.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(java.beans.PropertyChangeEvent evt) {
        pnDetailsPropertyChange(evt);
      }
    });
    pnDetails.setLayout(new java.awt.BorderLayout());

    scpDetails.setPreferredSize(new java.awt.Dimension(600, 350));

    txaDetails.setEditable(false);
    txaDetails.setColumns(20);
    txaDetails.setFont(lbIcon.getFont());
    txaDetails.setRows(5);
    scpDetails.setViewportView(txaDetails);

    pnDetails.add(scpDetails, java.awt.BorderLayout.CENTER);

    add(pnDetails, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

  private void pnDetailsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_pnDetailsPropertyChange
    if (evt.getPropertyName().equals("expanded")) {
      Window ancestor = OrchidUtils.getWindowOf(this);
      if (ancestor != null) {
        ancestor.pack();
        ancestor.setLocationRelativeTo(ancestor.getParent());
      }
    }
  }//GEN-LAST:event_pnDetailsPropertyChange
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel jPanel2;
  private javax.swing.JLabel lbIcon;
  private com.xzq.osc.JocGroupPane pnDetails;
  private javax.swing.JPanel pnMessages;
  private javax.swing.JScrollPane scpDetails;
  private javax.swing.JScrollPane scpMessages;
  private javax.swing.JTextArea txaDetails;
  private javax.swing.JTextArea txaMessages;
  // End of variables declaration//GEN-END:variables

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
