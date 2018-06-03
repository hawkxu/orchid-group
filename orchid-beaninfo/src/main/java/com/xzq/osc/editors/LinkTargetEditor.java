/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import java.beans.PropertyEditorSupport;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author zqxu
 */
public class LinkTargetEditor extends PropertyEditorSupport {

  @Override
  public String getAsText() {
    URI value = (URI) getValue();
    return value == null ? "null" : value.toString();
  }

  @Override
  public String getJavaInitializationString() {
    String value = getAsText();
    return "null".equals(value) ? "null" : String.format(
            "com.xzq.osc.OrchidUtils.tryStringToURI(\"%s\")", value);
  }

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    try {
      if ("null".equals(text)) {
        setValue(null);
      } else {
        setValue(new URI(text));
      }
    } catch (URISyntaxException ex) {
      throw new IllegalArgumentException("uri syntax invalid!", ex);
    }
  }
}