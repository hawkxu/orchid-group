/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import com.xzq.osc.BeanInfoResource;
import com.xzq.osc.CharCase;
import com.xzq.osc.JocValueField;
import com.xzq.osc.field.AbstractValueModel;
import com.xzq.osc.field.DateValueModel;
import com.xzq.osc.field.FileValueModel;
import com.xzq.osc.field.GenericValueModel;
import com.xzq.osc.field.ListValuePicker;
import com.xzq.osc.field.NumberTypeUtils;
import com.xzq.osc.field.NumberValueModel;
import com.xzq.osc.field.StringValueModel;
import com.xzq.osc.field.TimeValueModel;
import com.xzq.osc.field.ValueModel;
import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author zqxu
 */
public class ValueModelPane extends javax.swing.JPanel {

  private static final List<Class> modelList = Arrays.asList(
          new Class[]{
            StringValueModel.class,
            DateValueModel.class,
            TimeValueModel.class,
            NumberValueModel.class,
            FileValueModel.class,
            GenericValueModel.class
          });

  /**
   * Creates new form ValueModelPane
   */
  public ValueModelPane() {
    initComponents();
    customInitialize();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel3 = new JPanel();
    jScrollPane1 = new JScrollPane();
    lstModelType = new JList();
    lbValue = new JLabel();
    vlfValue = new JocValueField();
    vlfDefaultCase = new JocValueField();
    vlfDefaultMask = new JocValueField();
    lbDefaultMask = new JLabel();
    lbDefaultCase = new JLabel();
    ckbAutoTrim = new JCheckBox();
    lbPattern = new JLabel();
    vlfPattern = new JocValueField();
    lbRealType = new JLabel();
    vlfRealType = new JocValueField();
    lbUnsupported = new JLabel();
    lbUnsupportedModel = new JLabel();

    GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 163, Short.MAX_VALUE)
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 35, Short.MAX_VALUE)
    );

    lstModelType.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    lstModelType.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent evt) {
        lstModelTypeValueChanged(evt);
      }
    });
    jScrollPane1.setViewportView(lstModelType);

    lbValue.setText(BeanInfoResource.getString("FILE_VALUE_LABEL")); // NOI18N

    vlfValue.setColumns(25);

    vlfDefaultCase.setColumns(25);
    vlfDefaultCase.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        vlfDefaultCasePropertyChange(evt);
      }
    });

    vlfDefaultMask.setColumns(25);
    vlfDefaultMask.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        vlfDefaultMaskPropertyChange(evt);
      }
    });

    lbDefaultMask.setText(BeanInfoResource.getString("DEFAULT_MASK_LABEL")); // NOI18N

    lbDefaultCase.setText(BeanInfoResource.getString("DEFAULT_CASE_LABEL")); // NOI18N

    ckbAutoTrim.setText(BeanInfoResource.getString("AUTO_TRIM_LABEL")); // NOI18N

    lbPattern.setText(BeanInfoResource.getString("NUMBER_PATTERN_LABEL")); // NOI18N

    vlfPattern.setColumns(25);
    vlfPattern.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        vlfPatternPropertyChange(evt);
      }
    });

    lbRealType.setText(BeanInfoResource.getString("NUMBER_TYPE_LABEL")); // NOI18N

    vlfRealType.setColumns(25);
    vlfRealType.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        vlfRealTypePropertyChange(evt);
      }
    });

    lbUnsupported.setForeground(Color.red);
    lbUnsupported.setText(BeanInfoResource.getString("UNSUPPORTED_MODEL")); // NOI18N

    lbUnsupportedModel.setForeground(Color.red);
    lbUnsupportedModel.setText("jLabel2");

    GroupLayout layout = new GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(Alignment.LEADING)
              .addComponent(lbDefaultMask)
              .addComponent(lbDefaultCase)
              .addComponent(lbValue)
              .addComponent(lbPattern)
              .addComponent(lbRealType))
            .addGap(10, 10, 10)
            .addGroup(layout.createParallelGroup(Alignment.LEADING)
              .addComponent(ckbAutoTrim)
              .addComponent(vlfPattern, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(vlfRealType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(vlfValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(vlfDefaultMask, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(vlfDefaultCase, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
          .addComponent(lbUnsupported)
          .addGroup(layout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(lbUnsupportedModel)))
        .addContainerGap(40, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(ckbAutoTrim)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(Alignment.BASELINE)
              .addComponent(lbPattern)
              .addComponent(vlfPattern, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(Alignment.BASELINE)
              .addComponent(lbRealType)
              .addComponent(vlfRealType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(Alignment.BASELINE)
              .addComponent(lbValue)
              .addComponent(vlfValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(Alignment.BASELINE)
              .addComponent(lbDefaultMask)
              .addComponent(vlfDefaultMask, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(Alignment.BASELINE)
              .addComponent(lbDefaultCase)
              .addComponent(vlfDefaultCase, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGap(18, 18, 18)
            .addComponent(lbUnsupported)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(lbUnsupportedModel)
            .addGap(0, 105, Short.MAX_VALUE))
          .addComponent(jScrollPane1))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  private void lstModelTypeValueChanged(ListSelectionEvent evt) {//GEN-FIRST:event_lstModelTypeValueChanged
    if (!evt.getValueIsAdjusting()) {
      updateCurrentValueModel(lstModelType.getSelectedIndex());
    }
  }//GEN-LAST:event_lstModelTypeValueChanged

  private void vlfPatternPropertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_vlfPatternPropertyChange
    if (ignoreFieldChange || !evt.getPropertyName().equals("value")) {
      return;
    }
    String pattern = (String) evt.getNewValue();
    if (pattern != null && pattern.isEmpty()) {
      pattern = null;
    }
    if (valueModel instanceof DateValueModel) {
      ((DateValueModel) valueModel).setPattern(pattern);
    } else if (valueModel instanceof NumberValueModel) {
      ((NumberValueModel) valueModel).setPattern(pattern);
    }
  }//GEN-LAST:event_vlfPatternPropertyChange

  @SuppressWarnings("unchecked")
  private void vlfRealTypePropertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_vlfRealTypePropertyChange
    if (ignoreFieldChange || !evt.getPropertyName().equals("value")) {
      return;
    }
    if (valueModel instanceof NumberValueModel) {
      ((NumberValueModel) valueModel).setNumberType(
              (Class<? extends Number>) evt.getNewValue());
      updateFieldValue(valueModel);
    }
  }//GEN-LAST:event_vlfRealTypePropertyChange

  private void vlfDefaultMaskPropertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_vlfDefaultMaskPropertyChange
    if (ignoreFieldChange || !evt.getPropertyName().equals("value")) {
      return;
    }
    String mask = (String) evt.getNewValue();
    if (mask != null && mask.isEmpty()) {
      mask = null;
    }
    if (valueModel instanceof AbstractValueModel) {
      ((AbstractValueModel) valueModel).setDefaultMask(mask);
    }
  }//GEN-LAST:event_vlfDefaultMaskPropertyChange

  private void vlfDefaultCasePropertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_vlfDefaultCasePropertyChange
    if (ignoreFieldChange || !evt.getPropertyName().equals("value")) {
      return;
    }
    CharCase charCase = (CharCase) evt.getNewValue();
    if (valueModel instanceof AbstractValueModel) {
      ((AbstractValueModel) valueModel).setDefaultCase(charCase);
    }
  }//GEN-LAST:event_vlfDefaultCasePropertyChange
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JCheckBox ckbAutoTrim;
  private JPanel jPanel3;
  private JScrollPane jScrollPane1;
  private JLabel lbDefaultCase;
  private JLabel lbDefaultMask;
  private JLabel lbPattern;
  private JLabel lbRealType;
  private JLabel lbUnsupported;
  private JLabel lbUnsupportedModel;
  private JLabel lbValue;
  private JList lstModelType;
  private JocValueField vlfDefaultCase;
  private JocValueField vlfDefaultMask;
  private JocValueField vlfPattern;
  private JocValueField vlfRealType;
  private JocValueField vlfValue;
  // End of variables declaration//GEN-END:variables
  private ValueModel valueModel;
  private Map<Class, ValueModel> modelMap;
  private boolean ignoreFieldChange;

  private void customInitialize() {
    modelMap = new HashMap<Class, ValueModel>();
    lstModelType.setModel(new ValueModelModel());
    lstModelType.setCellRenderer(new ClassRenderer());
    vlfRealType.setModel(new ClassValueModel());
    vlfRealType.setValuePicker(ListValuePicker.from(
            (Object[]) NumberTypeUtils.supportedTypes));
    vlfDefaultMask.setModel(new MaskValueModel());
    vlfDefaultCase.setModel(new CharCaseValueModel());
    List<CharCase> caseList = new ArrayList<CharCase>();
    caseList.add(null);
    caseList.addAll(Arrays.asList(CharCase.values()));
    vlfDefaultCase.setValuePicker(ListValuePicker.from(
            (Object[]) caseList.toArray()));
    lbUnsupported.setVisible(false);
    lbUnsupportedModel.setVisible(false);
  }

  public ValueModel getValueModel() {
    return valueModel;
  }

  public void setValueModel(ValueModel valueModel) {
    if (valueModel == null) {
      valueModel = new StringValueModel();
    }
    this.valueModel = valueModel;
    Class modelClass = valueModel.getClass();
    int modelIndex = modelList.indexOf(modelClass);
    lbUnsupported.setVisible(modelIndex == -1);
    lbUnsupportedModel.setVisible(modelIndex == -1);
    if (modelIndex != -1) {
      modelMap.put(modelClass, valueModel.clone());
    }
    if (modelIndex == lstModelType.getSelectedIndex()) {
      updateCurrentValueModel(modelIndex);
    } else {
      lstModelType.setSelectedIndex(modelIndex);
    }
    lbUnsupportedModel.setText(modelClass.getName());
  }

  private void updateCurrentValueModel(int modelIndex) {
    if (modelIndex != -1) {
      Class modelClass = modelList.get(modelIndex);
      ValueModel model = modelMap.get(modelClass);
      if (model == null) {
        try {
          model = (ValueModel) modelClass.newInstance();
          modelMap.put(modelClass, model);
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      }
      valueModel = model;
    }
    updateFieldState(modelIndex);
  }

  private void updateFieldState(int modelIndex) {
    updateFieldValue(valueModel);
    Class modelClass = null;
    if (modelIndex != -1) {
      modelClass = modelList.get(modelIndex);
    }
    updateFieldEnabled(modelClass);
    updateFieldLabelText(modelClass);
  }

  private void updateFieldValue(ValueModel valueModel) {
    ignoreFieldChange = true;
    vlfValue.setModel(valueModel);
    vlfDefaultMask.setValue(valueModel.getDefaultMask());
    vlfDefaultCase.setValue(valueModel.getDefaultCase());
    if (valueModel instanceof StringValueModel) {
      ckbAutoTrim.setSelected(((StringValueModel) valueModel).isAutoTrim());
    } else if (valueModel instanceof DateValueModel) {
      vlfPattern.setValue(((DateValueModel) valueModel).getPattern());
    } else if (valueModel instanceof NumberValueModel) {
      vlfPattern.setValue(((NumberValueModel) valueModel).getPattern());
      vlfRealType.setValue(((NumberValueModel) valueModel).getNumberType());
    }
    ignoreFieldChange = false;
  }

  private void updateFieldEnabled(Class modelClass) {
    ckbAutoTrim.setEnabled(modelClass == StringValueModel.class);
    vlfPattern.setEnabled(modelClass == DateValueModel.class
            || modelClass == TimeValueModel.class
            || modelClass == NumberValueModel.class);
    lbPattern.setEnabled(vlfPattern.isEnabled());
    vlfRealType.setEnabled(modelClass == NumberValueModel.class);
    lbRealType.setEnabled(vlfRealType.isEnabled());
    vlfValue.setEnabled(modelClass != null
            && modelClass != GenericValueModel.class);
    lbValue.setEnabled(vlfValue.isEnabled());
    vlfDefaultMask.setEnabled(modelClass != null);
    lbDefaultMask.setEnabled(vlfDefaultMask.isEnabled());
    vlfDefaultCase.setEnabled(modelClass != null);
    lbDefaultCase.setEnabled(vlfDefaultCase.isEnabled());
  }

  private void updateFieldLabelText(Class modelClass) {
    if (modelClass == StringValueModel.class) {
      lbValue.setText(BeanInfoResource.getString("STRING_VALUE_LABEL"));
    } else if (modelClass == DateValueModel.class) {
      lbPattern.setText(BeanInfoResource.getString("DATE_PATTERN_LABEL"));
      lbValue.setText(BeanInfoResource.getString("DATE_VALUE_LABEL"));
    } else if (modelClass == TimeValueModel.class) {
      lbPattern.setText(BeanInfoResource.getString("TIME_PATTERN_LABEL"));
      lbValue.setText(BeanInfoResource.getString("TIME_VALUE_LABEL"));
    } else if (modelClass == NumberValueModel.class) {
      lbPattern.setText(BeanInfoResource.getString("NUMBER_PATTERN_LABEL"));
      lbValue.setText(BeanInfoResource.getString("NUMBER_VALUE_LABEL"));
    } else if (modelClass == FileValueModel.class) {
      lbValue.setText(BeanInfoResource.getString("FILE_VALUE_LABEL"));
    }
  }

  private static class ValueModelModel extends AbstractListModel {

    @Override
    public int getSize() {
      return modelList.size();
    }

    @Override
    public Object getElementAt(int index) {
      return modelList.get(index);
    }
  }

  private static class ClassRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
      Component rendererComponent = super.getListCellRendererComponent(
              list, value, index, isSelected, cellHasFocus);
      setText(((Class) value).getSimpleName());
      return rendererComponent;
    }
  }
}
