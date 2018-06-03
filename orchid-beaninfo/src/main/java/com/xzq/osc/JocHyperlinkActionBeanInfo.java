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
public class JocHyperlinkActionBeanInfo extends OrchidBaseBeanInfo {

  public JocHyperlinkActionBeanInfo() {
    super(JocHyperlinkAction.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "linkTarget", "visited");
    setPropertyEditor(LinkTargetEditor.class, "linkTarget");
  }
}
