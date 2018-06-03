/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocFolderChooserBeanInfo extends OrchidBaseBeanInfo {

  public JocFolderChooserBeanInfo() {
    super(JocFolderChooser.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "editable", "folderFilter", "navigationBarVisible",
            "availableButtons");
  }
}
