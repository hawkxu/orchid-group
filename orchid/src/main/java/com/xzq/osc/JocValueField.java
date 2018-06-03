/*
 *
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.field.*;
import com.xzq.osc.plaf.LookAndFeelManager;
import com.xzq.osc.plaf.OrchidDefaults;
import com.xzq.osc.resource.Resource;
import java.awt.*;
import java.awt.event.*;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

/**
 * A Input box component, extends from JTextField.
 *
 * The component has a ValueModel, the model manager value and do convert
 * between the value and String.<br>
 * The component can do value pick through ValuePickListener or ValuePicker, if
 * the component has value pick listener, the the value picker will be ignored.
 * <br>if the no value pick listener, and the value picker has a converter, the
 * component will use the converter of value picker.
 *
 * @author zqxu
 */
public class JocValueField extends JTextField implements OrchidAboutIntf {

  private static final Action[] defaultActions = {new NotifyAction()};
  private boolean required;
  private JButton pickButton;
  private Icon pickButtonIcon;
  private int pickButtonWidth;
  private Dimension pickDownSize;
  private ValueModel model;
  private PropertyChangeListener modelPropertyHandler;
  private String inputMask;
  private CharCase charCase;
  private JocInputMask inputMasker;
  private boolean ignoreModelChange;
  private boolean ignoreDocumentChange;
  private boolean inputInvalid;
  private boolean showInvalidIndicator;
  private DocumentListener documentHandler;
  private PropertyChangeListener documentChangeHandler;
  private boolean mousePressed;
  private boolean selectionOnFocus;
  private boolean valuePickOnly;
  private ValuePicker valuePicker;
  private int valuePickKeyCode;
  private boolean useValuePicker;
  private String valuePickerKey;
  private ValuePickerProvider valuePickerProvider;

  /**
   *
   */
  public JocValueField() {
    this(new StringValueModel());
  }

  /**
   *
   * @param model
   */
  public JocValueField(ValueModel model) {
    super(null, null, 0);
    initializeLocalVars();
    setModel(model);
  }

  @Override
  public void updateUI() {
    LookAndFeelManager.installOrchidUI();
    super.updateUI();
  }

  //<editor-fold defaultstate="collapsed" desc="initialization...">
  /**
   * initialize local variables
   */
  protected void initializeLocalVars() {
    initializePickButton();
    installDocumentHandler();
    selectionOnFocus = true;
    showInvalidIndicator = true;
    useValuePicker = true;
    getActionMap().put(notifyAction, defaultActions[0]);
  }

  /**
   * initialize pick value button
   */
  private void initializePickButton() {
    pickButton = new JButton(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        pickButtonActionPerformed();
      }
    });
    pickButton.setVisible(false);
    pickButton.setOpaque(false);
    pickButton.setFocusable(false);
    pickButton.setCursor(Cursor.getDefaultCursor());
    pickButton.setMargin(new Insets(0, 0, 0, 0));
    this.add(pickButton);
    this.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if ("enabled,editable".contains(propertyName)) {
          pickButton.setEnabled(isEditable() && isEnabled());
        }
      }
    });
  }

  private void installDocumentHandler() {
    getDocument().addDocumentListener(getDocumentHandler());
    addPropertyChangeListener("document", getDocumentChangeHandler());
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="model and input mask...">
  /**
   * Returns value model.
   *
   * @return value model.
   */
  public ValueModel getModel() {
    return model;
  }

  /**
   * Set new value model.
   *
   * @param model value model. can not be null.
   * @throws IllegalArgumentException if model is null.
   */
  public void setModel(ValueModel model) {
    if (model == null) {
      throw new IllegalArgumentException("Can not set null model!");
    }
    if (model != this.model) {
      ValueModel old = this.model;
      if (old != null) {
        old.removePropertyChangeListener(getModelPropertyHandler());
      }
      this.model = model;
      this.model.addPropertyChangeListener(getModelPropertyHandler());
      updateInputMasker();
      updateTextFromModel();
      updatePickButtonVisible();
      invalidate();
      firePropertyChange("model", old, model);
    }
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
   * event of model property change.
   *
   * @param evt property change event object.
   */
  protected void modelPropertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();
    if (propertyName.equals("valueClass")) {
      updatePickButtonVisible();
    } else if (propertyName.equals("defaultMask")
            || propertyName.equals("defaultCase")) {
      updateInputMasker();
    } else if (propertyName.equals("value")) {
      modelValueChange(evt.getOldValue(), evt.getNewValue());
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
   * Returns value in model.
   *
   * @return value.
   */
  public Object getValue() {
    return model.getValue();
  }

  /**
   * Sets value in model. a property change event with name "value" will be
   * fired after value of model change.
   *
   * @param value value.
   */
  @SuppressWarnings("unchecked")
  public void setValue(Object value) {
    model.setValue(value);
  }

  /**
   * Returns value in model under specified type, the valueClass must be class
   * or super class of valueClass in current model.
   *
   * @param <T>
   * @param valueClass value class
   * @return value under type valueClass
   */
  @SuppressWarnings("unchecked")
  public <T> T getValue(Class<T> valueClass) {
    checkValueClass(valueClass);
    return (T) model.getValue();
  }

  @SuppressWarnings("unchecked")
  private void checkValueClass(Class valueClass) {
    Class myClass = model.getValueClass();
    if (!valueClass.isAssignableFrom(myClass)) {
      throw new IllegalArgumentException(
              "Invalid value class, expect " + myClass.getName());
    }
  }

  private void modelValueChange(Object oldValue, Object newValue) {
    if (!ignoreModelChange) {
      updateTextFromModel();
    }
    firePropertyChange("value", oldValue, newValue);
  }

  private void updateTextFromModel() {
    String oldText = getText();
    String newText = formatValue(getValue());
    if (!OrchidUtils.equals(oldText, newText)) {
      ignoreDocumentChange = true;
      setText(newText);
      ignoreDocumentChange = false;
    }
  }

  @SuppressWarnings("unchecked")
  private String formatValue(Object value) {
    Converter<Object, String> converter = getValuePickerConverter();
    return converter != null
            ? converter.convertForward(value) : model.formatValue(value);
  }

  /**
   * Returns input mask. if current input mask set by setInputMask was null,
   * then default mask of value model will be returned.
   *
   * @return input mask
   * @see JocInputMask
   */
  public String getInputMask() {
    if (inputMask == null && model != null) {
      return model.getDefaultMask();
    }
    return inputMask;
  }

  /**
   * Set input mask. default is null.
   *
   * @param inputMask input mask.
   * @see JocInputMask
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
   * Returns char case. if current char case set by setCharCase was null, then
   * default char case of value model will be returned.
   *
   * @return char case
   * @see JocInputMask
   */
  public CharCase getCharCase() {
    if (charCase == null && model != null) {
      return model.getDefaultCase();
    }
    return charCase;
  }

  /**
   * Set char case. default is null.
   *
   * @param charCase char case
   * @see JocInputMask
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
        inputMasker.unmaskTextComponent(this);
      }
    } else {
      if (inputMasker == null) {
        inputMasker = new JocInputMask(_mask, _case);
      } else {
        inputMasker.setMask(_mask);
        inputMasker.setCharCase(_case);
      }
      inputMasker.maskTextComponent(this);
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="document handler...">
  private DocumentListener getDocumentHandler() {
    if (documentHandler == null) {
      documentHandler = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
          updateValueFromDocument();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          updateValueFromDocument();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          updateValueFromDocument();
        }
      };
    }
    return documentHandler;
  }

  private PropertyChangeListener getDocumentChangeHandler() {
    if (documentChangeHandler == null) {
      documentChangeHandler = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          Document oDocument = (Document) evt.getOldValue();
          Document nDocument = (Document) evt.getNewValue();
          if (oDocument != null) {
            oDocument.removeDocumentListener(getDocumentHandler());
          }
          nDocument.addDocumentListener(getDocumentHandler());
          updateTextFromModel();
        }
      };
    }
    return documentChangeHandler;
  }

  @SuppressWarnings("unchecked")
  private void updateValueFromDocument() {
    if (!isParseSupported()) {
      return;
    }
    ignoreModelChange = true;
    boolean invalid = false;
    try {
      if (!ignoreDocumentChange) {
        model.setValue(parseValue(getText()));
      }
    } catch (Exception ex) {
      invalid = true;
      OrchidLogger.info("invalid input.", ex);
    }
    updateInputInvalid(invalid);
    ignoreModelChange = false;
  }

  private boolean isParseSupported() {
    Converter<Object, String> converter = getValuePickerConverter();
    return converter != null
            ? converter.reverseSupported() : model.parseSupported();
  }

  private Object parseValue(String text) {
    Converter<Object, String> converter = getValuePickerConverter();
    return converter != null
            ? converter.convertReverse(text) : model.parseValue(text);
  }

  private Converter<Object, String> getValuePickerConverter() {
    if (getValuePickListeners().length > 0) {
      return null;
    }
    ValuePicker picker = getAvailableValuePicker();
    return picker == null ? null : picker.getConverter();
  }

  private void updateInputInvalid(boolean inputInvalid) {
    if (this.inputInvalid != inputInvalid) {
      this.inputInvalid = inputInvalid;
      if (showInvalidIndicator) {
        repaint();
      }
    }
  }

  public boolean isInputInvalid() {
    return inputInvalid;
  }

  /**
   * return show invalid indicator, if user input can not convert to model value
   * and this property be true, then a red box will shown. default is true.
   *
   * @return true or false.
   */
  public boolean isShowInvalidIndicator() {
    return showInvalidIndicator;
  }

  /**
   * Set show invalid indicator.
   *
   * @param showInvalidIndicator
   */
  public void setShowInvalidIndicator(boolean showInvalidIndicator) {
    boolean old = this.showInvalidIndicator;
    if (old != showInvalidIndicator) {
      this.showInvalidIndicator = showInvalidIndicator;
      if (inputInvalid) {
        repaint();
      }
      firePropertyChange("showInvalidIndicator", old, showInvalidIndicator);
    }
  }
  //</editor-fold>

  // <editor-fold defaultstate="collapsed" desc="pick button and layout...">
  /**
   * Returns required indicator. if true, the input field will paint a
   * requirement indicator when the input field has empty text.
   *
   * @return true or false.
   */
  public boolean isRequired() {
    return required;
  }

  /**
   * Set requirement indicator.
   *
   * @param required true or false
   */
  public void setRequired(boolean required) {
    boolean old = this.required;
    if (old != required) {
      this.required = required;
      repaint();
      firePropertyChange("required", old, required);
    }
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension size = super.getPreferredSize();
    size.width += getPickButtonWidth(size.height) + 2;
    return size;
  }

  @Override
  public Rectangle getBounds() {
    Dimension size = getSize();
    if (!Beans.isDesignTime() && pickButton.isVisible()) {
      size.width -= getPickButtonWidth(size.height) + 2;
    }
    return new Rectangle(getLocation(), size);
  }

  @Override
  public void doLayout() {
    super.doLayout();
    if (pickButton.isVisible()) {
      int pickWidth = getPickButtonWidth(getHeight());
      int x = getWidth() - pickWidth - 2;
      pickButton.setBounds(x, 2, pickWidth, getHeight() - 4);
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (isEditable() && isEnabled()
            && isRequired() && getText().isEmpty()) {
      paintRequiredIcon(g);
    }
  }

  /**
   * paint required input prompt icon.
   *
   * @param g graphics device.
   */
  protected void paintRequiredIcon(Graphics g) {
    Image img = Resource.getOrchidImage("required.png");
    int y = (getHeight() - img.getHeight(null)) / 2;
    g.drawImage(img, getInsets().left, y, null);
  }

  /**
   *
   * @param g
   */
  @Override
  protected void paintBorder(Graphics g) {
    super.paintBorder(g);
    if (inputInvalid && showInvalidIndicator) {
      paintInvalidIndicator(g);
    }
  }

  /**
   * draw error border
   *
   * @param g graphics device
   */
  protected void paintInvalidIndicator(Graphics g) {
    Color color = g.getColor();
    g.setColor(Color.RED);
    g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    g.setColor(color);
  }

  /**
   * Returns guide value for pick value button width.
   *
   * @return guide value for pick value button width
   */
  public int getPickButtonWidth() {
    return pickButtonWidth;
  }

  /**
   * Returns actually width for pick value button.
   *
   * @param height component hight
   * @return actually width for pick value button
   */
  protected int getPickButtonWidth(int height) {
    return pickButtonWidth > 0 ? pickButtonWidth : height - 4;
  }

  /**
   * Set guide value for pick value button width.
   *
   * @param pickButtonWidth guide value for pick value button width, 0 use
   * default value.
   */
  public void setPickButtonWidth(int pickButtonWidth) {
    int old = this.pickButtonWidth;
    if (old != pickButtonWidth) {
      this.pickButtonWidth = pickButtonWidth;
      validate();
      firePropertyChange("pickButtonWidth", old, pickButtonWidth);
    }
  }

  /**
   * Return icon for pick value button.
   *
   * @return icon for pick value button.
   */
  public Icon getPickButtonIcon() {
    return pickButtonIcon;
  }

  /**
   * Set icon for pick value button, preferred icon size was 11x11.
   *
   * @param pickButtonIcon icon for pick value button.
   */
  public void setPickButtonIcon(Icon pickButtonIcon) {
    Icon old = this.pickButtonIcon;
    if (old != pickButtonIcon) {
      this.pickButtonIcon = pickButtonIcon;
      pickButton.setIcon(pickButtonIcon);
      firePropertyChange("pickButtonIcon", old, pickButtonIcon);
    }
  }

  /**
   * Update pick value button visibility.
   */
  protected void updatePickButtonVisible() {
    ValuePicker availableValuePicker = null;
    ValuePickListener[] listeners = getValuePickListeners();
    boolean pickButtonVisible = listeners.length > 0;
    if (!pickButtonVisible) {
      availableValuePicker = getAvailableValuePicker();
      pickButtonVisible = availableValuePicker != null;
    }
    Icon pickerIcon = getPickButtonIcon();
    if (pickerIcon == null && availableValuePicker != null) {
      pickerIcon = availableValuePicker.getPickButtonIcon();
    }
    if (pickerIcon == null) {
      pickerIcon = UIManager.getIcon(OrchidDefaults.DEFAULT_PICKUP_ICON);
    }
    pickButton.setIcon(pickerIcon);
    pickButton.setVisible(pickButtonVisible);
  }

  /**
   * get available value picker. if useValuePicker is false, always return null.
   * otherwise, first use getValuePicker, then find value picker from value
   * picker provider. if no available value picker, return null.
   *
   * @return value picker.
   */
  protected ValuePicker getAvailableValuePicker() {
    ValuePicker picker = null;
    if (getUseValuePicker()) {
      picker = getValuePicker();
      if (picker == null) {
        picker = getValuePickerFromProvider();
      }
    }
    return picker;
  }

  private ValuePicker getValuePickerFromProvider() {
    ValuePickerProvider provider = getValuePickerProvider();
    if (provider == null) {
      provider = (ValuePickerProvider) UIManager.get(
              OrchidDefaults.DEFAULT_PICKER_PROVIDER);
    }
    return provider == null ? null : provider.findValuePicker(this);
  }

  /**
   * Returns keyboard key code for invoke value picker. default use value in
   * UIDefaults with key OrchidDefaults.FIELD_PICK_KEYCODE.
   *
   * @return keyboard key code for invoke value picker.
   */
  public int getValuePickKeyCode() {
    if (valuePickKeyCode != -1) {
      return valuePickKeyCode;
    }
    return UIManager.getInt(OrchidDefaults.FIELD_PICK_KEYCODE);
  }

  /**
   * Set keyboard key code for invoke value picker, user click the key will
   * invoke value picker.
   *
   * @param valuePickKeyCode keyboard key code for invoke value picker. use -1
   * to reset to UIDefaults
   */
  public void setValuePickKeyCode(int valuePickKeyCode) {
    if (valuePickKeyCode != this.valuePickKeyCode) {
      int old = this.valuePickKeyCode;
      this.valuePickKeyCode = valuePickKeyCode;
      firePropertyChange("valuePickKeyCode", old, valuePickKeyCode);
    }
  }

  /**
   * Returns keyboard key code for invoke pick value action. defaut is VK_F4.
   *
   * @return keyboard key code for invoke pick value action.
   */
  @Deprecated
  public int getPickActionKeyCode2() {
    return getValuePickKeyCode();
  }

  /**
   * Set keyboard key code for invoke pick value action, user click the key will
   * invoke pick value action.
   *
   * @param pickActionKeyCode key code
   */
  @Deprecated
  public void setPickActionKeyCode2(int pickActionKeyCode) {
    setValuePickKeyCode(pickActionKeyCode);
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="picker getter/setter...">
  /**
   * Returns value picker of the field. if the value picker is null, the field
   * find picker from value picker provider if avaible. default is null.
   *
   * @return value picker.
   */
  public ValuePicker getValuePicker() {
    return valuePicker;
  }

  /**
   * Sets value picker of the field.
   *
   * @param valuePicker value picker.
   */
  public void setValuePicker(ValuePicker valuePicker) {
    if (valuePicker != this.valuePicker) {
      ValuePicker old = this.valuePicker;
      this.valuePicker = valuePicker;
      updatePickButtonVisible();
      firePropertyChange("valuePicker", old, valuePicker);
    }
  }

  /**
   * Returns true if value picker (include value picker provider) should be
   * used, false not, do not affect value pick listener. default is true.
   *
   * @return true or false
   */
  public boolean getUseValuePicker() {
    return useValuePicker;
  }

  /**
   * Sets value picker (include value picker provider) should be used or not.
   *
   * @param useValuePicker true or false.
   */
  public void setUseValuePicker(boolean useValuePicker) {
    if (useValuePicker != this.useValuePicker) {
      this.useValuePicker = useValuePicker;
      updatePickButtonVisible();
      firePropertyChange("useValuePicker", !useValuePicker, useValuePicker);
    }
  }

  /**
   * Returns value picker key of the field. the value picker provider of the
   * field should use this key to search avaible value picker. default is null.
   *
   * @return
   */
  public String getValuePickerKey() {
    return valuePickerKey;
  }

  /**
   * Sets value picker key of the field.
   *
   * @param valuePickerKey value picker key.
   */
  public void setValuePickerKey(String valuePickerKey) {
    if (!OrchidUtils.equals(valuePickerKey, this.valuePicker)) {
      String old = this.valuePickerKey;
      this.valuePickerKey = valuePickerKey;
      updatePickButtonVisible();
      firePropertyChange("valuePickerKey", old, valuePickerKey);
    }
  }

  /**
   * Returns value picker provider of the field. if no ValuePickerListener in
   * the field and no value picker in the field, then the field use this value
   * picker provider to find avaible value picker. if the value picker provider
   * is null, then default value picker provider in UI defaults with key
   * OrchidDefaults.DEFAULT_PICKER_PROVIDER will be used. default is null.
   *
   * @return value picker provider
   */
  public ValuePickerProvider getValuePickerProvider() {
    return valuePickerProvider;
  }

  /**
   * Sets value picker provider of the field.
   *
   * @param pickerProvider value picker provider.
   */
  public void setValuePickerProvider(ValuePickerProvider pickerProvider) {
    if (pickerProvider != this.valuePickerProvider) {
      ValuePickerProvider old = this.valuePickerProvider;
      this.valuePickerProvider = pickerProvider;
      updatePickButtonVisible();
      firePropertyChange("valuePickerProvider", old, pickerProvider);
    }
  }

  /**
   * add value pick listener to the field. the field will use listener to pickup
   * value and ignore value picker.
   *
   * @param l value pick listener.
   */
  public void addValuePickListener(ValuePickListener l) {
    listenerList.add(ValuePickListener.class, l);
    updatePickButtonVisible();
  }

  /**
   * remove value pick listener from the field. if all value pick listener has
   * been removed, the field will use value picker to pickup value.
   *
   * @param l value pick listener.
   */
  public void removeValuePickListener(ValuePickListener l) {
    listenerList.remove(ValuePickListener.class, l);
    updatePickButtonVisible();
  }

  /**
   * Returns all registered value pick listeners.
   *
   * @return value pick listeners
   */
  protected ValuePickListener[] getValuePickListeners() {
    return listenerList.getListeners(ValuePickListener.class);
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="pick action process...">
  /**
   * Pick action handle method, process pick action event.
   */
  protected void pickButtonActionPerformed() {
    requestFocus();
    ValuePickListener[] listeners = getValuePickListeners();
    if (listeners.length > 0) {
      invokeValuePickListeners(listeners);
    } else {
      invokeValuePicker(getAvailableValuePicker());
    }
  }

  private void invokeValuePickListeners(ValuePickListener[] listeners) {
    Object value = getValue();
    ValuePickEvent evt = new ValuePickEvent(this, value);
    for (ValuePickListener l : listeners) {
      l.pickupValue(evt);
      if (evt.isConsumed()) {
        break;
      }
    }
  }

  private void invokeValuePicker(ValuePicker valuePicker) {
    if (valuePicker != null) {
      valuePicker.pickupValue(this, getValue());
    }
  }

  /**
   * Returns indicator for whether value can set only through pick or not. if
   * this property is true, then user can not input value through keyboard.
   * default is false.
   *
   * @return
   */
  public boolean isValuePickOnly() {
    return valuePickOnly;
  }

  /**
   * Sets whether value can set only through pick or not.
   *
   * @param valuePickOnly true or false.
   */
  public void setValuePickOnly(boolean valuePickOnly) {
    if (valuePickOnly != this.valuePickOnly) {
      boolean old = this.valuePickOnly;
      this.valuePickOnly = valuePickOnly;
      firePropertyChange("valuePickOnly", old, valuePickOnly);
    }
  }

  /**
   * check if user can manual input, by the property pickValueOnly and
   * valueClass and Converters.
   *
   * @return true for user can manual input and false not.
   */
  protected boolean canUserManualInput() {
    return !valuePickOnly && isParseSupported();
  }

  /**
   * process keyboard event.
   *
   * @param e keyboard event
   */
  @Override
  @SuppressWarnings("unchecked")
  protected void processKeyEvent(KeyEvent e) {
    int key = e.getKeyCode();
    int code = (byte) e.getKeyChar();
    if (canUserManualInput() || code == -1 || code == KeyEvent.VK_ENTER
            || code == KeyEvent.VK_ESCAPE) {
      super.processKeyEvent(e);
    }
    if (e.isConsumed() && (e.getKeyCode() == KeyEvent.VK_ENTER
            || e.getKeyCode() == KeyEvent.VK_ESCAPE)) {
      dispatchEventToModalDialog(e);
    }
    if (e.isConsumed() || e.getModifiers() != 0
            || e.getID() != KeyEvent.KEY_PRESSED) {
      return;
    }
    if ((key == KeyEvent.VK_DELETE || key == KeyEvent.VK_BACK_SPACE)
            && !canUserManualInput()) {
      model.setValue(null);
    } else if ((key == getValuePickKeyCode() || key == KeyEvent.VK_DOWN)
            && pickButton.isVisible() && pickButton.isEnabled()) {
      pickButton.doClick();
    }
  }

  private void dispatchEventToModalDialog(KeyEvent e) {
    Window dialog = SwingUtilities.getWindowAncestor(this);
    if (dialog instanceof JocModalDialog) {
      dialog.dispatchEvent(new KeyEvent(dialog, e.getID(), e.getWhen(),
              e.getModifiers(), e.getKeyCode(), e.getKeyChar()));
    }
  }

  /**
   * Return whether select content when the input field get focus or not.
   * default is true. if the input field get focus through mouse, then this
   * property
   *
   * @return true or false.
   */
  public boolean getSelectionOnFocus() {
    return selectionOnFocus;
  }

  /**
   * Set whether select content when the input field get focus or not.
   *
   * @param selectionOnFocus true or false.
   */
  public void setSelectionOnFocus(boolean selectionOnFocus) {
    boolean old = this.selectionOnFocus;
    if (old != selectionOnFocus) {
      this.selectionOnFocus = selectionOnFocus;
      firePropertyChange("selectionOnFocus", old, selectionOnFocus);
    }
  }

  /**
   * process mouse event.
   *
   * @param e mouse event.
   */
  @Override
  protected void processMouseEvent(MouseEvent e) {
    super.processMouseEvent(e);
    mousePressed = e.getID() == MouseEvent.MOUSE_PRESSED;
  }

  /**
   * process focus event.
   *
   * @param e focus event.
   */
  @Override
  protected void processFocusEvent(FocusEvent e) {
    if (!e.isTemporary()) {
      if (e.getID() == FocusEvent.FOCUS_LOST) {
        mousePressed = false;
        updateTextFromModel();
      } else if (e.getID() == FocusEvent.FOCUS_GAINED
              && getSelectionOnFocus() && !mousePressed) {
        selectAll();
      }
    }
    super.processFocusEvent(e);
  }

  /**
   * {@inheritDoc }
   *
   * @return the command list
   */
  @Override
  public Action[] getActions() {
    return TextAction.augmentList(super.getActions(), defaultActions);
  }

  @Override
  public void postActionEvent() {
    updateTextFromModel();
    super.postActionEvent();
  }
  // </editor-fold>

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

  private static class NotifyAction extends TextAction {

    NotifyAction() {
      super(notifyAction);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent target = getFocusedComponent();
      if (target instanceof JTextField) {
        JTextField field = (JTextField) target;
        field.postActionEvent();
      }
    }
  }
}
