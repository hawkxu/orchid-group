/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.JocModalDialog.ModalResult;
import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.field.AbstractValueModel;
import com.xzq.osc.field.OptionsPopupMenu;
import com.xzq.osc.field.Range;
import com.xzq.osc.field.Range.Option;
import com.xzq.osc.field.RangeEditorPane;
import com.xzq.osc.field.RangeList;
import com.xzq.osc.field.RangeModel;
import com.xzq.osc.field.RangeModelEvent;
import com.xzq.osc.field.RangeModelListener;
import com.xzq.osc.field.RangeUtils;
import com.xzq.osc.field.StringRangeModel;
import com.xzq.osc.field.ValuePickListener;
import com.xzq.osc.field.ValuePicker;
import com.xzq.osc.field.ValuePickerProvider;
import com.xzq.osc.plaf.LookAndFeelManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author zqxu
 */
public class JocRangeField extends JComponent implements OrchidAboutIntf {

  private static final String uiClassId = "OrchidRangeFieldUI";
  private JLabel optionLabel;
  private boolean optionLabelFrozen;
  private JocValueField lowField;
  private JLabel toLabel;
  private JocValueField highField;
  private JButton multipleButton;
  private OptionsPopupMenu optionsPopup;
  private int optionFieldGap;
  private int toLabelGap;
  private int multipleButtonGap;
  private boolean keepHighSpace;
  private boolean fieldWidthExtended;
  private RangeModel model;
  private RangeModelListener rangeModelHandler;
  private PropertyChangeListener modelPropertyHandler;
  private boolean ignoreWholeRangesChange;
  private boolean ignoreFieldsValueChange;
  private String inputMask;
  private CharCase charCase;
  private JocInputMask inputMasker;
  private Color savedLowFieldFG;
  private Color savedHighFieldFG;
  private boolean lowFieldInvalid;
  private boolean highFieldInvalid;
  private Color invalidForeground;
  private boolean showInvalidIndicator;

  public JocRangeField() {
    this(new StringRangeModel());
  }

  public JocRangeField(RangeModel model) {
    super();
    initializeLocalVars();
    setModel(model);
    updateUI();
  }

  @Override
  public String getUIClassID() {
    return uiClassId;
  }

  @Override
  public void updateUI() {
    LookAndFeelManager.installOrchidUI();
    setUI(UIManager.getUI(this));
  }

  /**
   * initialize local variables.
   */
  protected void initializeLocalVars() {
    optionFieldGap = 4;
    toLabelGap = 6;
    multipleButtonGap = 4;
    showInvalidIndicator = true;
    invalidForeground = Color.RED;
    add(optionLabel = createOptionLabel());
    add(lowField = createValueField());
    add(toLabel = createToLable());
    add(highField = createValueField());
    add(multipleButton = createMultipleButton());
    optionsPopup = createOptionsPopup();
  }

  /**
   * create option label and install listener.
   *
   * @return option label.
   */
  protected JLabel createOptionLabel() {
    Icon optIcon = RangeUtils.getOptionIcon(null, null);
    JLabel optLabel = new JLabel(optIcon);
    optLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        optionLabelMouseReleased(e);
      }
    });
    return optLabel;
  }

  /**
   * mouse released event on option label.
   *
   * @param evt
   */
  protected void optionLabelMouseReleased(MouseEvent evt) {
    if (isEnabled() && !getOptionLabelFrozen()) {
      optionsPopup.updateMenuItems(getFirstRange());
      OrchidUtils.showPopupMenu(optionsPopup, optionLabel);
    }
  }

  /**
   * create value field and install listener.
   *
   * @return value field.
   */
  protected JocValueField createValueField() {
    JocValueField field = new JocValueField(new ValueModel());
    field.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        resetInvalidFieldsValue();
      }
    });
    field.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        resetInvalidFieldsValue();
      }
    });
    field.addPropertyChangeListener("value", new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        JocValueField field = (JocValueField) evt.getSource();
        processFieldValueChange(field, evt.getOldValue(), evt.getNewValue());
      }
    });
    return field;
  }

  /**
   * create to label.
   *
   * @return to label
   */
  protected JLabel createToLable() {
    return new JLabel(OrchidLocale.getString("TO_LABEL_TEXT"));
  }

  /**
   * create multiple button and install listener.
   *
   * @return multiple button.
   */
  protected JButton createMultipleButton() {
    JButton button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
    button.setIcon(RangeUtils.getMultipleIcon(0));
    button.setFocusable(false);
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        multipleButtonActionPerformed(e);
      }
    });
    return button;
  }

  /**
   * action performed on multiple button.
   *
   * @param e action event object.
   */
  protected void multipleButtonActionPerformed(ActionEvent e) {
    requestFocus();
    Window window = OrchidUtils.getWindowOf(this);
    final JocModalDialog dialog = new JocModalDialog(window);
    RangeEditorPane pane = new RangeEditorPane();
    pane.updateEditorPane(model, inputMasker,
            getUseValuePicker(), getValuePicker(),
            getValuePickerKey(), lowField.getValuePickListeners());
    pane.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dialog.setModalResult(ModalResult.MR_OK);
      }
    });
    pane.setBorder(new EmptyBorder(8, 8, 8, 8));
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(pane, BorderLayout.CENTER);
    dialog.setTitle(OrchidLocale.getString("RANGE_DIALOG"));
    dialog.pack();
    if (dialog.showModal(true) == ModalResult.MR_OK) {
      setWholeRanges(pane.getWholeRanges());
    }
    pane.releaseEditorPane();
  }

  /**
   * create options pop up menu for choose option.
   *
   * @return options pop up menu.
   */
  protected OptionsPopupMenu createOptionsPopup() {
    OptionsPopupMenu popup = new OptionsPopupMenu();
    popup.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        optionsPopupActionPerformed(e);
      }
    });
    return popup;
  }

  /**
   * action performed on options pop up menu.
   *
   * @param e action event object.
   */
  protected void optionsPopupActionPerformed(ActionEvent e) {
    Range range = null;
    Option option = optionsPopup.getSelected();
    if (option != null) {
      range = getFirstRange();
      if (range == null) {
        range = new Range();
      }
      range.setOption(option);
    }
    setFirstRange(range);
  }

  /**
   * Returns option label.
   *
   * @return option label.
   */
  public JLabel getOptionLabel() {
    return optionLabel;
  }

  /**
   * Returns true if option label froze, otherwise false.
   *
   * @return true or false
   */
  public boolean getOptionLabelFrozen() {
    return optionLabelFrozen;
  }

  /**
   * Sets option label frozen or not, true for freeze option label, false not.
   *
   * @param optionLabelFrozen true or false.
   */
  public void setOptionLabelFrozen(boolean optionLabelFrozen) {
    this.optionLabelFrozen = optionLabelFrozen;
  }

  /**
   * Returns low value field.
   *
   * @return low value field.
   */
  public JocValueField getLowField() {
    return lowField;
  }

  /**
   * Returns to label.
   *
   * @return to label.
   */
  public JLabel getToLabel() {
    return toLabel;
  }

  /**
   * Returns high value field.
   *
   * @return high value field.
   */
  public JocValueField getHighField() {
    return highField;
  }

  /**
   * Returns multiple button.
   *
   * @return multiple button.
   */
  public JButton getMultipleButton() {
    return multipleButton;
  }

  /**
   * Returns gap width between option label and low field.
   *
   * @return gap width between option label and low field.
   */
  public int getOptionFieldGap() {
    return optionFieldGap;
  }

  /**
   * Sets gap width between option label and low field.
   *
   * @param optionFieldGap
   */
  public void setOptionFieldGap(int optionFieldGap) {
    int old = this.optionFieldGap;
    if (old != optionFieldGap) {
      this.optionFieldGap = optionFieldGap;
      invalidate();
      firePropertyChange("optionEditorGap", old, optionFieldGap);
    }
  }

  /**
   * Returns gap width of both side of to label.
   *
   * @return gap width of both side of to label.
   */
  public int getToLabelGap() {
    return toLabelGap;
  }

  /**
   * Sets gap width of both side of to label.
   *
   * @param toLabelGap
   */
  public void setToLabelGap(int toLabelGap) {
    int old = this.toLabelGap;
    if (old != toLabelGap) {
      this.toLabelGap = toLabelGap;
      invalidate();
      firePropertyChange("toLabelGap", old, toLabelGap);
    }
  }

  /**
   * Returns gap with between value field and multiple button.
   *
   * @return gap with between value field and multiple button.
   */
  public int getMultipleButtonGap() {
    return multipleButtonGap;
  }

  /**
   * Sets gap with between value field and multiple button.
   *
   * @param multipleButtonGap gap with between value field and multiple button.
   */
  public void setMultipleButtonGap(int multipleButtonGap) {
    int old = this.multipleButtonGap;
    if (old != multipleButtonGap) {
      this.multipleButtonGap = multipleButtonGap;
      invalidate();
      firePropertyChange("multipleButtonGap", old, multipleButtonGap);
    }
  }

  /**
   * Returns whether keep high value field space when model no range interval or
   * not.default is false.
   *
   * @return true or false.
   */
  public boolean isKeepHighSpace() {
    return keepHighSpace;
  }

  /**
   * Sets whether keep high value field space when model no range interval or
   * not.
   *
   * @param keepHighSpace true or false.
   */
  public void setKeepHighSpace(boolean keepHighSpace) {
    boolean old = this.keepHighSpace;
    if (old != keepHighSpace) {
      this.keepHighSpace = keepHighSpace;
      invalidate();
      firePropertyChange("keepHighSpace", old, keepHighSpace);
    }
  }

  /**
   * Returns whether extend value field width if total width large than
   * preferred with or not. default is false.
   *
   * @return true or false.
   */
  public boolean isFieldWidthExtended() {
    return fieldWidthExtended;
  }

  /**
   * Sets whether extend value field width if total width large than preferred
   * with or not.
   *
   * @param fieldWidthExtended true or false.
   */
  public void setFieldWidthExtended(boolean fieldWidthExtended) {
    boolean old = this.fieldWidthExtended;
    if (old != fieldWidthExtended) {
      this.fieldWidthExtended = fieldWidthExtended;
      invalidate();
      firePropertyChange("editorWidthExtended", old, fieldWidthExtended);
    }
  }

  /**
   * Returns range model
   *
   * @return range model.
   */
  public RangeModel getModel() {
    return model;
  }

  /**
   * Sets range model.
   *
   * @param model range model, can not be null.
   * @throws IllegalArgumentException if mode is null.
   */
  public void setModel(RangeModel model) {
    if (model == null) {
      throw new IllegalArgumentException("Can not set null model!");
    }
    if (model != this.model) {
      RangeModel old = this.model;
      if (old != null) {
        old.removeRangeModelListener(getRangeModelHandler());
        old.removePropertyChangeListener(getModelPropertyHandler());
      }
      this.model = model;
      this.model.addRangeModelListener(getRangeModelHandler());
      this.model.addPropertyChangeListener(getModelPropertyHandler());
      updateInputMasker();
      updateFieldsModel();
      updateMultipleButtonIcon();
      updateFieldsByFirstRange(true);
      invalidate();
      firePropertyChange("model", old, model);
    }
  }

  private void updateFieldsModel() {
    ((ValueModel) lowField.getModel()).setRangeModel(model);
    ((ValueModel) highField.getModel()).setRangeModel(model);
  }

  private RangeModelListener getRangeModelHandler() {
    if (rangeModelHandler == null) {
      rangeModelHandler = new RangeModelListener() {
        @Override
        public void wholeRangesChange(RangeModelEvent evt) {
          updateMultipleButtonIcon();
          updateFieldsByFirstRange(false);
        }
      };
    }
    return rangeModelHandler;
  }

  private PropertyChangeListener getModelPropertyHandler() {
    if (modelPropertyHandler == null) {
      modelPropertyHandler = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          modelPropertyChange(evt);
        }
      };
    }
    return modelPropertyHandler;
  }

  /**
   * model property change event.
   *
   * @param evt property change event object.
   */
  protected void modelPropertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();
    if (propertyName.equals("valueClass")) {
      lowField.updatePickButtonVisible();
      highField.updatePickButtonVisible();
    } else if (propertyName.equals("defaultMask")
            || propertyName.equals("defaultCase")) {
      updateInputMasker();
    } else if (propertyName.equals("multipleRange")
            || propertyName.equals("rangeInterval")) {
      invalidate();
    }
  }

  /**
   * Returns value class of model.
   *
   * @return value class of model.
   */
  public Class getValueClass() {
    return model.getValueClass();
  }

  /**
   * Returns multipleRange property of model.
   *
   * @return multipleRange property of model.
   */
  public boolean getMultipleRange() {
    return model.getMultipleRange();
  }

  /**
   * Returns rangeInterval property of model.
   *
   * @return
   */
  public boolean getRangeInterval() {
    return model.getRangeInterval();
  }

  /**
   * Returns first range of model.
   *
   * @return first range of model.
   * @see RangeModel#getFirstRange()
   */
  public Range getFirstRange() {
    return model.getFirstRange();
  }

  /**
   * Sets first range of model.
   *
   * @param range range
   * @see RangeModel#setFirstRange(com.xzq.osc.field.Range)
   */
  @SuppressWarnings("unchecked")
  public void setFirstRange(Range range) {
    model.setFirstRange(range);
  }

  /**
   * Set single range for model
   *
   * @param range range.
   * @see RangeModel#setSingleRange(com.xzq.osc.field.Range)
   */
  @SuppressWarnings("unchecked")
  public void setSingleRange(Range range) {
    model.setSingleRange(range);
  }

  /**
   * Returns whole ranges of model.
   *
   * @return whole ranges of model.
   */
  @SuppressWarnings("unchecked")
  public RangeList getWholeRanges() {
    return model.getWholeRanges();
  }

  /**
   * Sets whole ranges of model.
   *
   * @param wholeRanges whole ranges of model.
   */
  @SuppressWarnings("unchecked")
  public void setWholeRanges(RangeList wholeRanges) {
    model.setWholeRanges(wholeRanges);
  }

  /**
   * Returns first range under specified value class.
   *
   * @param <V>
   * @param valueClass value class, must be class or super class of valueClass
   * of model.
   * @return first range under specified value class.
   */
  @SuppressWarnings("unchecked")
  public <V> Range<V> getFirstRange(Class<V> valueClass) {
    checkValueClass(valueClass);
    return model.getFirstRange();
  }

  /**
   * Returns whole ranges of model under specified value class.
   *
   * @param <V>
   * @param valueClass value class, must be class or super class of valueClass
   * of model.
   * @return whole ranges of model under specified value class.
   */
  @SuppressWarnings("unchecked")
  public <V> RangeList<V> getWholeRanges(Class<V> valueClass) {
    checkValueClass(valueClass);
    return (RangeList<V>) model.getWholeRanges();
  }

  private void checkValueClass(Class<?> valueClass) {
    Class myClass = model.getValueClass();
    if (!valueClass.isAssignableFrom(myClass)) {
      throw new IllegalArgumentException(
              "Invalid value class, expect " + myClass.getName());
    }
  }

  private void updateFieldsByFirstRange(boolean forceUpdateText) {
    Range range = getFirstRange();
    if (!ignoreWholeRangesChange) {
      ignoreFieldsValueChange = true;
      lowField.setValue(range.getLowValue());
      highField.setValue(range.getHighValue());
      if (forceUpdateText) {
        ((ValueModel) lowField.getModel()).forceUpdateText();
        ((ValueModel) highField.getModel()).forceUpdateText();
      }
      ignoreFieldsValueChange = false;
    }
    optionLabel.setIcon(RangeUtils.getOptionIcon(
            range.getSign(), range.getOption()));
  }

  /**
   * process value of low field and high field change.
   *
   * @param field value field (may not be low field or high field)
   * @param oldValue old value of field.
   * @param newValue new value of field.
   */
  @SuppressWarnings("unchecked")
  protected void processFieldValueChange(JocValueField field,
          Object oldValue, Object newValue) {
    boolean fieldInvalid = false;
    if (!ignoreFieldsValueChange) {
      Range range = getFirstRange();
      try {
        if (field == lowField) {
          range.setLowValue(newValue);
        } else {
          range.setHighValue(newValue);
        }
        ignoreWholeRangesChange = true;
        setFirstRange(range);
        ignoreWholeRangesChange = false;
      } catch (Exception ex) {
        fieldInvalid = true;
      }
    }
    updateFieldInvalid(field, fieldInvalid);
  }

  private void updateMultipleButtonIcon() {
    multipleButton.setIcon(RangeUtils.getMultipleIcon(model.getRangeCount()));
  }

  private void resetInvalidFieldsValue() {
    if (lowFieldInvalid || highFieldInvalid) {
      updateFieldsByFirstRange(false);
    }
  }

  /**
   * Returns required property of low field.
   *
   * @return required property of low field..
   * @see JocValueField#isRequired()
   */
  public boolean isRequired() {
    return lowField.isRequired();
  }

  /**
   * Sets required property of low field.
   *
   * @param required required property of low field.
   */
  public void setRequired(boolean required) {
    lowField.setRequired(required);
  }

  /**
   * Returns columns of value field.
   *
   * @return columns of value field.
   * @see JocValueField#getColumns()
   */
  public int getColumns() {
    return lowField.getColumns();
  }

  /**
   * Sets columns of value field.
   *
   * @param columns columns of value field.
   */
  public void setColumns(int columns) {
    lowField.setColumns(columns);
    highField.setColumns(columns);
  }

  /**
   * Returns input mask. if the mask set by setInputMask was null, then the
   * default mask of model will be returned.
   *
   * @return input mask.
   */
  public String getInputMask() {
    if (inputMask == null && model != null) {
      return model.getDefaultMask();
    }
    return inputMask;
  }

  /**
   * Set input mask.
   *
   * @param inputMask input mask.
   */
  public void setInputMask(String inputMask) {
    if (!OrchidUtils.equals(inputMask, this.inputMask)) {
      String old = this.inputMask;
      this.inputMask = inputMask;
      updateInputMasker();
      firePropertyChange("inputMask", old, inputMask);
    }
  }

  /**
   * Returns char case. if the char case set by setCharCase was null, then the
   * default char case of model will be returned.
   *
   * @return char case.
   */
  public CharCase getCharCase() {
    if (charCase == null && model != null) {
      return model.getDefaultCase();
    }
    return charCase;
  }

  /**
   * Sets char case.
   *
   * @param charCase
   */
  public void setCharCase(CharCase charCase) {
    if (!OrchidUtils.equals(charCase, this.charCase)) {
      CharCase old = this.charCase;
      this.charCase = charCase;
      updateInputMasker();
      firePropertyChange("charCase", old, charCase);
    }

    this.charCase = charCase;
  }

  private void updateInputMasker() {
    String _mask = getInputMask();
    CharCase _case = getCharCase();
    if (OrchidUtils.isEmpty(_mask)
            && (_case == null || _case == CharCase.NOT_CARE)) {
      if (inputMasker != null) {
        inputMasker.unmaskTextComponent(lowField);
        inputMasker.unmaskTextComponent(highField);
      }
      inputMasker = null;
    } else {
      if (inputMasker == null) {
        inputMasker = new JocInputMask(_mask, _case);
      } else {
        inputMasker.setMask(_mask);
        inputMasker.setCharCase(_case);
      }
      inputMasker.maskTextComponent(lowField);
      inputMasker.maskTextComponent(highField);
    }
  }

  /**
   * Returns foreground color for value field when value of field can not assign
   * to range.
   *
   * @return color
   */
  public Color getInvalidForeground() {
    return invalidForeground;
  }

  /**
   * Sets foreground color for value field when value of field can not assign to
   * range.
   *
   * @param invalidForeground color
   */
  public void setInvalidForeground(Color invalidForeground) {
    Color old = this.invalidForeground;
    if (old != invalidForeground) {
      this.invalidForeground = invalidForeground;
      if (savedLowFieldFG != null) {
        lowField.setForeground(invalidForeground);
      }
      if (savedHighFieldFG != null) {
        highField.setForeground(invalidForeground);
      }
      firePropertyChange("invalidForeground", old, invalidForeground);
    }
  }

  /**
   * Returns whether show value field with foreground set by
   * setInvalidForeground when value of field can not assign to range or not.
   *
   * @return true or false. default is true.
   */
  public boolean isShowInvalidIndicator() {
    return showInvalidIndicator;
  }

  /**
   * Sets whether show value field with foreground set by setInvalidForeground
   * when value of field can not assign to range or not.
   *
   * @param showInvalidIndicator
   */
  public void setShowInvalidIndicator(boolean showInvalidIndicator) {
    boolean old = this.showInvalidIndicator;
    if (old != showInvalidIndicator) {
      this.showInvalidIndicator = showInvalidIndicator;
      updateFieldsInvalidState();
      firePropertyChange("showInvalidIndicator", old, showInvalidIndicator);
    }
  }

  private void updateFieldInvalid(JocValueField field, boolean invalid) {
    if (field == lowField && lowFieldInvalid != invalid) {
      lowFieldInvalid = invalid;
      updateFieldsInvalidState();
    } else if (field == highField && highFieldInvalid != invalid) {
      highFieldInvalid = invalid;
      updateFieldsInvalidState();
    }
  }

  /**
   * Returns true if current value of low field can not assign to range.
   *
   * @return true or false.
   */
  public boolean isLowFieldInvalid() {
    return lowFieldInvalid;
  }

  /**
   * Returns true if current value of high field can not assign to range.
   *
   * @return true or false.
   */
  public boolean isHighFieldInvalid() {
    return highFieldInvalid;
  }

  private void updateFieldsInvalidState() {
    boolean lowIndicator = lowFieldInvalid && showInvalidIndicator;
    boolean highIndicator = highFieldInvalid && showInvalidIndicator;
    if (lowIndicator && savedLowFieldFG == null) {
      savedLowFieldFG = lowField.getForeground();
      lowField.setForeground(invalidForeground);
    } else if (!lowIndicator && savedLowFieldFG != null) {
      lowField.setForeground(savedLowFieldFG);
      savedLowFieldFG = null;
    }
    if (highIndicator && savedHighFieldFG == null) {
      savedHighFieldFG = highField.getForeground();
      highField.setForeground(invalidForeground);
    } else if (!highIndicator && savedHighFieldFG != null) {
      highField.setForeground(savedHighFieldFG);
      savedHighFieldFG = null;
    }
  }

  /**
   * Returns whether can set value through value pick only or not.
   *
   * @return true or false
   * @see JocValueField#isValuePickOnly()
   */
  public boolean isValuePickOnly() {
    return lowField.isValuePickOnly();
  }

  /**
   * Sets whether can set value through value pick only or not.
   *
   * @param valuePickOnly true or false
   * @see JocValueField#setValuePickOnly(boolean)
   */
  public void setValuePickOnly(boolean valuePickOnly) {
    lowField.setValuePickOnly(true);
    highField.setValuePickOnly(true);
  }

  /**
   * Returns value picker.
   *
   * @return value picker.
   * @see JocValueField#getValuePicker()
   */
  public ValuePicker getValuePicker() {
    return lowField.getValuePicker();
  }

  /**
   * Sets value picker.
   *
   * @param valuePicker value picker
   * @see JocValueField#setValuePicker(com.xzq.osc.field.ValuePicker)
   */
  public void setValuePicker(ValuePicker valuePicker) {
    lowField.setValuePicker(valuePicker);
    highField.setValuePicker(valuePicker);
  }

  /**
   * Returns use value picker or not.
   *
   * @return true or false.
   * @see JocValueField#getUseValuePicker()
   */
  public boolean getUseValuePicker() {
    return lowField.getUseValuePicker();
  }

  /**
   * Sets use value picker or not.
   *
   * @param useValuePicker true or false.
   * @see JocValueField#setUseValuePicker(boolean)
   */
  public void setUseValuePicker(boolean useValuePicker) {
    lowField.setUseValuePicker(useValuePicker);
    highField.setUseValuePicker(useValuePicker);
  }

  /**
   * Returns value picker key
   *
   * @return value picker key
   * @see JocValueField#getValuePickerKey()
   */
  public String getValuePickerKey() {
    return lowField.getValuePickerKey();
  }

  /**
   * Sets value picker key
   *
   * @param valuePickerKey value picker key
   * @see JocValueField#setValuePickerKey(java.lang.String)
   */
  public void setValuePickerKey(String valuePickerKey) {
    lowField.setValuePickerKey(valuePickerKey);
    highField.setValuePickerKey(valuePickerKey);
  }

  /**
   * Returns value picker provider
   *
   * @return value picker provider
   * @see JocValueField#getValuePickerProvider()
   */
  public ValuePickerProvider getValuePickerProvider() {
    return lowField.getValuePickerProvider();
  }

  /**
   * Sets value picker provider.
   *
   * @param provider value picker provider
   * @see
   * JocValueField#setValuePickerProvider(com.xzq.osc.field.ValuePickerProvider)
   */
  public void setValuePickerProvider(ValuePickerProvider provider) {
    lowField.setValuePickerProvider(provider);
    highField.setValuePickerProvider(provider);
  }

  /**
   * Add value pick listener
   *
   * @param l value pick listener
   * @see
   * JocValueField#addValuePickListener(com.xzq.osc.field.ValuePickListener)
   */
  public void addValuePickListener(ValuePickListener l) {
    lowField.addValuePickListener(l);
    highField.addValuePickListener(l);
  }

  /**
   * remove value pick listener
   *
   * @param l value pick listener
   * @see
   * JocValueField#removeValuePickListener(com.xzq.osc.field.ValuePickListener)
   */
  public void removeValuePickListener(ValuePickListener l) {
    lowField.removeValuePickListener(l);
    highField.removeValuePickListener(l);
  }

  /**
   * Returns the about box dialog of JocBusyIcon
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
    // no content need.
  }

  private static class ValueModel extends AbstractValueModel<Object> {

    private RangeModel rangeModel;

    public ValueModel() {
      super(null, null, null);
    }

    public RangeModel getRangeModel() {
      return rangeModel;
    }

    public void setRangeModel(RangeModel rangeModel) {
      Class oClass = getValueClass();
      this.rangeModel = rangeModel;
      Class nClass = getValueClass();
      if (!OrchidUtils.equals(oClass, nClass)) {
        firePropertyChange("valueClass", oClass, nClass);
      }
    }

    public void forceUpdateText() {
      firePropertyChange("value", value, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends Object> getValueClass() {
      return rangeModel == null ? Object.class : rangeModel.getValueClass();
    }

    @Override
    public Object parseValue(String text) {
      return rangeModel == null ? text : rangeModel.parseValue(text);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String formatValue(Object value) {
      return rangeModel == null
              ? String.valueOf(value) : rangeModel.formatValue(value);
    }

    @Override
    public boolean parseSupported() {
      return rangeModel != null && rangeModel.parseSupported();
    }

    @Override
    public AbstractValueModel<Object> clone() {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}
