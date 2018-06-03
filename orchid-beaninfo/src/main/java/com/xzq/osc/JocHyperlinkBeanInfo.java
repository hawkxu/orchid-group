/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.editors.LinkTargetEditor;

/**
 *
 * @author zqxu
 */
public class JocHyperlinkBeanInfo extends OrchidBaseBeanInfo {

  public JocHyperlinkBeanInfo() {
    super(JocHyperlink.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "unvisitColor", "rolloverColor", "activeColor",
            "visitedColor", "linkTarget");
    setPropertyEditor(LinkTargetEditor.class, "linkTarget");
  }
}
