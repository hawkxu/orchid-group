/*
 * An implementation for default about information of Orchid components.
 * Author: Xu Zi Qiang
 */
package com.xzq.osc.about;

import com.xzq.osc.JocModalDialog;
import com.xzq.osc.resource.Resource;
import java.awt.BorderLayout;
import java.awt.Window;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author zqxu
 */
public class DefaultOrchidAbout {

  /**
   * Returns default icon of the component.
   *
   * @param componentClass Class of the component.
   * @return An icon of the component.
   */
  public static Icon getDefaultIcon(Class componentClass) {
    return Resource.getOrchidIcon(
            componentClass.getSimpleName().toLowerCase() + "16.png");
  }

  /**
   * Gets default component about pane.
   *
   * @param componentClass component class
   * @return An about pane of the component
   */
  public static JPanel getDefaultAboutPane(Class componentClass) {
    return new OrchidAboutPane(componentClass);
  }

  /**
   * get default about dialog of the component.
   *
   * @param componentClass Class of the component.
   * @return default about dialog
   */
  public static JDialog getDefaultAboutBox(Class componentClass) {
    return new AboutDialog(null, getDefaultAboutPane(componentClass));
  }

  private static class AboutDialog extends JocModalDialog {

    public AboutDialog(Window owner, JPanel aboutPane) {
      super(owner);
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(aboutPane, BorderLayout.CENTER);
      setTitle(Resource.getString("PROJECT_NAME"));
      setIconImage(Resource.getOrchidImage("orchid16.png"));
      setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      setModalityType(ModalityType.APPLICATION_MODAL);
      pack();
      setLocationRelativeTo(owner);
    }

    @Override
    protected void enterPressed() {
      doDefaultCloseAction();
    }
  }
}
