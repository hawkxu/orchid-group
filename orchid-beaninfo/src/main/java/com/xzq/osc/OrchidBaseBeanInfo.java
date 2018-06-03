/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyEditorSupport;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author zqxu
 */
public class OrchidBaseBeanInfo extends BeanInfoSupport {

  private Image beanImage;

  public OrchidBaseBeanInfo(Class<?> beanClass) {
    super(beanClass);
  }

  @Override
  protected void initialize() {
    beanImage = Icon2Image(DefaultOrchidAbout.getDefaultIcon(beanClass));
    getProperty("aboutBox").setValue("canEditAsText", Boolean.FALSE);
    super.setPreferred(true, "aboutBox");
    setPropertyEditor(AboutBoxViewer.class, "aboutBox");
  }

  public Image Icon2Image(Icon beanIcon) {
    if (beanIcon == null) {
      return null;
    } else if (beanIcon instanceof ImageIcon) {
      return ((ImageIcon) beanIcon).getImage();
    } else {
      BufferedImage bufImage = new BufferedImage(beanIcon.getIconWidth(),
              beanIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
      beanIcon.paintIcon(new JPanel(), bufImage.getGraphics(), 0, 0);
      return bufImage;
    }
  }

  @Override
  public java.awt.Image getIcon(int iconKind) {
    switch (iconKind) {
      case ICON_COLOR_16x16:
        return beanImage;
      default:
        return null;
    }
  }

  public static class AboutBoxViewer extends PropertyEditorSupport {

    private Class beanClass;

    public AboutBoxViewer() {
      super();
    }

    public AboutBoxViewer(Object source) {
      super(source);
      beanClass = source.getClass();
    }

    @Override
    public String getAsText() {
      return "Orchid About Box";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
      DefaultOrchidAbout.getDefaultAboutBox(beanClass).setVisible(true);
    }

    @Override
    public Component getCustomEditor() {
      return DefaultOrchidAbout.getDefaultAboutPane(beanClass);
    }

    @Override
    public boolean supportsCustomEditor() {
      return true;
    }
  }
}
