/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.nbm;

import java.beans.PropertyEditorManager;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

  @Override
  public void restored() {
    PropertyEditorManager.setEditorSearchPath(new String[]{
              "com.xzq.osc.editors"});
  }
}
