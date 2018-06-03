/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import com.xzq.osc.field.ValueModel;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.editors.XMLPropertyEditor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author zqxu
 */
public class ValueModelEditor extends PropertyEditorSupport
        implements ExPropertyEditor, XMLPropertyEditor {

  private ValueModelPane pane;
  private PropertyEnv env;
  private PropertyChangeListener envHandler;

  @Override
  public Component getCustomEditor() {
    pane = new ValueModelPane();
    env.addPropertyChangeListener(getEnvHandler());
    env.getFeatureDescriptor().setValue("changeImmediate", Boolean.FALSE);
    pane.setValueModel((ValueModel) getValue());
    return pane;
  }

  @Override
  public boolean supportsCustomEditor() {
    return true;
  }

  @Override
  public void attachEnv(PropertyEnv pe) {
    env = pe;
    env.getFeatureDescriptor().setValue(
            "canEditAsText", Boolean.FALSE);
  }

  @Override
  public void readFromXML(Node node) throws IOException {
    setValue(ValueModelXML.readFromXML(node));
  }

  @Override
  public Node storeToXML(Document dcmnt) {
    return ValueModelXML.storeToXML(dcmnt, (ValueModel) getValue());
  }

  @Override
  public String getJavaInitializationString() {
    return ValueModelCode.getInitCode((ValueModel) getValue());
  }

  private PropertyChangeListener getEnvHandler() {
    if (envHandler == null) {
      envHandler = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          envPropertyChange(evt);
        }
      };
    }
    return envHandler;
  }

  private void envPropertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals(PropertyEnv.PROP_STATE)
            && evt.getNewValue().equals(PropertyEnv.STATE_VALID)) {
      setValue(pane.getValueModel());
    }
  }
}
