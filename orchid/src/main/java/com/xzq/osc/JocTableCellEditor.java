/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.field.DateToStringConverter;
import com.xzq.osc.field.DateValueModel;
import com.xzq.osc.field.FileValueModel;
import com.xzq.osc.field.GenericValueModel;
import com.xzq.osc.field.NumberToStringConverter;
import com.xzq.osc.field.NumberValueModel;
import com.xzq.osc.field.StringValueModel;
import com.xzq.osc.field.ValueModel;
import com.xzq.osc.plaf.LookAndFeelManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author zqxu
 */
public class JocTableCellEditor extends AbstractCellEditor
        implements TableCellEditor, SwingConstants, OrchidAboutIntf {

  /**
   * The Swing component being edited.
   */
  protected JComponent editorComponent;
  /**
   * The delegate class which handles all methods sent from the
   * <code>CellEditor</code>.
   */
  protected EditorDelegate editorDelegate;
  /**
   * An integer specifying the number of clicks needed to start editing. Even if
   * <code>clickCountToStart</code> is defined as zero, it will not initiate
   * until a click occurs.
   */
  protected int clickCountToStart;
  private Object trueValue;
  private Object falseValue;
  private boolean nullAsFalse;
  private Class valueClass;
  private int horizontalAlignment;
  private String numberPattern;
  private String dateTimePattern;

  /**
   * Constructor use JocValueField as editor component.
   */
  public JocTableCellEditor() {
    this(JocValueField.class);
  }

  /**
   * Constructor with editor component class, see setEditorClass.
   *
   * @param editorClass editor component class
   * @see #setEditorClass(java.lang.Class)
   */
  public JocTableCellEditor(Class<? extends JComponent> editorClass) {
    trueValue = Boolean.TRUE;
    falseValue = Boolean.FALSE;
    nullAsFalse = true;
    LookAndFeelManager.installOrchidUI();
    setEditorClass(editorClass);
    setHorizontalAlignment(OrchidConstants.SWING_DEFAULT);
  }

  /**
   * Returns current editor component class.
   *
   * @return editor component class.
   */
  public Class getEditorClass() {
    return editorComponent == null ? null : editorComponent.getClass();
  }

  /**
   * Set editor component by class, only below class support currently.
   * <ul><li>javax.swing.JTextField</li> <li>javax.swing.JRadioButton</li>
   * <li>javax.swing.JCheckBox</li><li>javax.swing.JComboBox</li>
   * <li>com.osc.xzq.JocValueField</li> </ul>for other editor component class,
   * please use setEditorComponent directly.
   *
   * @param editorClass editor component class
   * @see #setEditorComponent(javax.swing.JComponent,
   * com.xzq.osc.JocTableCellEditor.EditorDelegate)
   */
  public void setEditorClass(Class<? extends JComponent> editorClass) {
    if (editorClass == getEditorClass()) {
      return;
    }
    if (JocValueField.class == editorClass) {
      setEditorComponent(new JocValueField(), new ValueFieldDelegate());
    } else if (JTextField.class == editorClass) {
      setEditorComponent(new JTextField(), new TextFieldDelegate());
    } else if (JPasswordField.class == editorClass) {
      setEditorComponent(new JPasswordField(), new TextFieldDelegate());
    } else if (JRadioButton.class == editorClass) {
      setEditorComponent(new JRadioButton(), new RadioDelegate());
    } else if (JCheckBox.class == editorClass) {
      setEditorComponent(new JCheckBox(), new CheckDelegate());
    } else if (JComboBox.class == editorClass) {
      setEditorComponent(new JComboBox(), new ComboBoxDelegate());
    } else {
      throw new IllegalArgumentException(
              "Unsupported class, please use setEditorComponent instead.");
    }
  }

  /**
   * Returns current editor component.
   *
   * @return editor component.
   */
  public JComponent getEditorComponent() {
    return editorComponent;
  }

  /**
   * Set editor compoennt, only accept component instance of JTextField,
   * JRadioButton, JCheckBox, JComboBox, for other editor component class,
   * please use setEditorComponent directly.
   *
   * @param editorComponent editor component
   * @see #setEditorComponent(javax.swing.JComponent,
   * com.xzq.osc.JocTableCellEditor.EditorDelegate)
   */
  public void setEditorComponent(JComponent editorComponent) {
    if (editorComponent instanceof JocValueField) {
      setEditorComponent(
              (JocValueField) editorComponent, new ValueFieldDelegate());
    } else if (editorComponent instanceof JTextField) {
      setEditorComponent((JTextField) editorComponent, new TextFieldDelegate());
    } else if (editorComponent instanceof JRadioButton) {
      setEditorComponent((JRadioButton) editorComponent, new RadioDelegate());
    } else if (editorComponent instanceof JCheckBox) {
      setEditorComponent((JCheckBox) editorComponent, new CheckDelegate());
    } else if (editorComponent instanceof JComboBox) {
      setEditorComponent((JComboBox) editorComponent, new ComboBoxDelegate());
    } else {
      throw new IllegalArgumentException(
              "Unsupported class, please use setEditorComponent instead.");
    }
  }

  /**
   * Returns current editor delegate.
   *
   * @return editor delegate.
   */
  public EditorDelegate getEditorDelegate() {
    return editorDelegate;
  }

  /**
   * Set current editor component.
   *
   * @param <V> Generic Class for value delegate
   * @param <T> Generic Class for editor component
   * @param editorComponent editor component
   * @param editorDelegate value delegate
   */
  @SuppressWarnings("unchecked")
  public <V extends JComponent, T extends V> void setEditorComponent(
          T editorComponent, EditorDelegate<V> editorDelegate) {
    if (editorComponent == null) {
      throw new IllegalArgumentException("editorComponent can not be null.");
    }
    if (editorDelegate == null) {
      throw new IllegalArgumentException("editorDelegate can not be null.");
    }
    if (this.editorComponent != null && this.editorDelegate != null) {
      this.editorDelegate.uninstallComponent(this.editorComponent);
      this.editorDelegate.installEditor(null);
    }
    this.editorComponent = editorComponent;
    this.editorDelegate = editorDelegate;
    this.editorDelegate.installEditor(this);
    this.editorDelegate.installComponent(this.editorComponent);
  }

  /**
   * Returns editor component current horizontal alignment, used for saving
   * default horizontal alignment.
   *
   * @param editorComponent editor component.
   * @return editor component horizontal alignment.
   */
  protected int getEditorHorizontal(JComponent editorComponent) {
    if (editorComponent instanceof JTextField) {
      return ((JTextField) editorComponent).getHorizontalAlignment();
    } else if (editorComponent instanceof JRadioButton) {
      return ((JRadioButton) editorComponent).getHorizontalAlignment();
    } else if (editorComponent instanceof JCheckBox) {
      return ((JCheckBox) editorComponent).getHorizontalAlignment();
    } else {
      return OrchidConstants.SWING_DEFAULT;
    }
  }

  /**
   * Returns the number of clicks needed to start editing.
   *
   * @return the number of clicks needed to start editing
   */
  public int getClickCountToStart() {
    if (clickCountToStart != 0) {
      return clickCountToStart;
    } else {
      return editorDelegate.getDefaultClickCountToStart();
    }
  }

  /**
   * Specifies the number of clicks needed to start editing.
   *
   * @param clickCountToStart an int specifying the number of clicks needed to
   * start editing
   * @see #getClickCountToStart
   */
  public void setClickCountToStart(int clickCountToStart) {
    if (clickCountToStart == editorDelegate.getDefaultClickCountToStart()) {
      this.clickCountToStart = 0;
    } else {
      this.clickCountToStart = clickCountToStart;
    }
  }

  /**
   * Returns the value that indicate Boolean.TRUE for Boolean editor use.
   * default is Boolean.TRUE;
   *
   * @return the value that indicate Boolean.TRUE for Boolean editor use.
   */
  public Object getTrueValue() {
    return trueValue;
  }

  /**
   * Set the value that indicate Boolean.TRUE for Boolean editor use.
   *
   * @param trueValue the value that indicate Boolean.TRUE for Boolean editor
   * use.
   * @throws IllegalArgumentException if the trueValue is null.
   */
  public void setTrueValue(Object trueValue) {
    if (trueValue == null) {
      throw new IllegalArgumentException("trueValue can not be null.");
    }
    this.trueValue = trueValue;
  }

  /**
   * Returns the value that indicate Boolean.FASLE for Boolean editor use.
   * default is Boolean.FALSE.
   *
   * @return the value that indicate Boolean.FASLE for Boolean editor use.
   */
  public Object getFalseValue() {
    return falseValue;
  }

  /**
   * Set the value that indicate Boolean.FASLE for Boolean editor use.
   *
   * @param falseValue the value that indicate Boolean.FASLE for Boolean editor
   * use.
   * @throws IllegalArgumentException if the falseValue is null.
   */
  public void setFalseValue(Object falseValue) {
    if (falseValue == null) {
      throw new IllegalArgumentException("falseValue can not be null.");
    }
    this.falseValue = falseValue;
  }

  /**
   * Returns whether simulate null as Boolean.FALSE or Boolean.TRUE, if true,
   * simulate null value AS Boolean.FALSE, otherwise Boolean.TRUE. default is
   * true.
   *
   * @return true for simulate null value AS Boolean.FALSE, otherwise
   * Boolean.TRUE
   */
  public boolean isNullAsFalse() {
    return nullAsFalse;
  }

  /**
   * Set whether simulate null as Boolean.FALSE or Boolean.TRUE, if true,
   * simulate null value AS Boolean.FALSE, otherwise Boolean.TRUE.
   *
   * @param nullAsFalse
   */
  public void setNullAsFalse(boolean nullAsFalse) {
    this.nullAsFalse = nullAsFalse;
  }

  /**
   * Convert generic value to Boolean by cell editor trueValue and nullAsFalse
   * property.
   *
   * @param value generic value
   * @return Boolean value
   */
  protected Boolean convertToBoolean(Object value) {
    if (value == null) {
      return isNullAsFalse()
              ? Boolean.FALSE : Boolean.TRUE;
    } else if (OrchidUtils.equals(value, getTrueValue())) {
      return Boolean.TRUE;
    } else {
      return Boolean.FALSE;
    }
  }

  /**
   * Convert Boolean value to generic value by cell editor trueValue and
   * falseValue and nullAsFalse property.
   *
   * @param value Boolean value.
   * @return Generic value.
   */
  protected Object convertFromBoolean(Boolean value) {
    if (value == null) {
      value = isNullAsFalse() ? Boolean.FALSE : Boolean.TRUE;
    }
    return value ? getTrueValue() : getFalseValue();
  }

  /**
   * Returns horizontal alignment for editor component. default is
   * OrchidConstants.SWING_DEFAULT. only support editor component instance of
   * JTextField or JRadioButton or JCheckBox.
   *
   * @return horizontal alignment.
   * @see #setHorizontalAlignment(int)
   */
  public int getHorizontalAlignment() {
    return horizontalAlignment;
  }

  /**
   * Set horizontal alignment for editor component. only support editor
   * component instance of JTextField or JRadioButton or JCheckBox.
   *
   * @param horizontalAlignment One of the following constants:
   * <ul><li>OrchidConstants.SWING_DEFAULT (default)</li>
   * <li>SwingConstants.LEFT</li><li>SwingConstants.CENTER</li>
   * <li>SwingConstants.RIGHT</li><li>SwingConstants.LEADING</li>
   * <li>SwingConstants.TRAILING</li></ul>.
   */
  public void setHorizontalAlignment(int horizontalAlignment) {
    this.horizontalAlignment = horizontalAlignment;
  }

  /**
   * Returns preferred pattern for number and string conversion.
   *
   * @return pattern
   */
  public String getNumberPattern() {
    return numberPattern;
  }

  /**
   * Sets preferred pattern for number and string conversion.
   *
   * @param numberPattern pattern
   */
  public void setNumberPattern(String numberPattern) {
    this.numberPattern = numberPattern;
  }

  /**
   * Returns preferred pattern for date and string conversion.
   *
   * @return pattern
   */
  public String getDateTimePattern() {
    return dateTimePattern;
  }

  /**
   * Sets preferred pattern for date and string conversion.
   *
   * @param dateTimePattern pattern
   */
  public void setDateTimePattern(String dateTimePattern) {
    this.dateTimePattern = dateTimePattern;
  }

  /**
   * Forwards the message from the
   * <code>CellEditor</code> to the
   * <code>delegate</code>.
   *
   * @see EditorDelegate#isCellEditable(EventObject)
   */
  @Override
  public boolean isCellEditable(EventObject anEvent) {
    return editorDelegate.isCellEditable(anEvent);
  }

  /**
   * Forwards the message from the
   * <code>CellEditor</code> to the
   * <code>delegate</code>.
   *
   * @see EditorDelegate#shouldSelectCell(EventObject)
   */
  @Override
  public boolean shouldSelectCell(EventObject anEvent) {
    return editorDelegate.shouldSelectCell(anEvent);
  }

  /**
   * Forwards the message from the
   * <code>CellEditor</code> to the
   * <code>delegate</code>.
   *
   * @see EditorDelegate#stopCellEditing
   */
  @Override
  public boolean stopCellEditing() {
    return editorDelegate.stopCellEditing();
  }

  /**
   * Forwards the message from the
   * <code>CellEditor</code> to the
   * <code>delegate</code>.
   *
   * @see EditorDelegate#cancelCellEditing
   */
  @Override
  public void cancelCellEditing() {
    editorDelegate.cancelCellEditing();
  }

  /**
   * Forwards the message from the
   * <code>CellEditor</code> to the
   * <code>editorDelegate</code>.
   *
   * @see EditorDelegate#getValue(javax.swing.JComponent, java.lang.Class,
   * java.lang.String)
   */
  @Override
  public Object getCellEditorValue() {
    try {
      return getEditorValue();
    } catch (Exception ex) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private Object getEditorValue() throws Exception {
    String pattern = getSuitablePattern(valueClass);
    return editorDelegate.getValue(editorComponent, valueClass, pattern);
  }

  /**
   * Implements the
   * <code>TableCellEditor</code> interface.
   */
  @Override
  @SuppressWarnings("unchecked")
  public Component getTableCellEditorComponent(JTable table, Object value,
          boolean isSelected, int row, int column) {
    updateEditorComponentOpaque(table, value, isSelected, row, column);
    updateEditorComponentAlignment(table.getColumnClass(column));
    valueClass = table.getColumnClass(column);
    String pattern = getSuitablePattern(valueClass);
    editorDelegate.setValue(editorComponent, valueClass, pattern, value);
    return editorComponent;
  }

  /**
   * update editor component opaque property for check box and radio button.
   *
   * @param table table
   * @param value value
   * @param isSelected selected
   * @param row row
   * @param column column
   */
  protected void updateEditorComponentOpaque(JTable table, Object value,
          boolean isSelected, int row, int column) {
    if (editorComponent instanceof JCheckBox
            || editorComponent instanceof JRadioButton) {
      TableCellRenderer renderer = table.getCellRenderer(row, column);
      Component c = renderer.getTableCellRendererComponent(table, value,
              isSelected, true, row, column);
      if (c != null) {
        editorComponent.setOpaque(true);
        editorComponent.setBackground(c.getBackground());
        if (c instanceof JComponent) {
          editorComponent.setBorder(((JComponent) c).getBorder());
        }
      } else {
        editorComponent.setOpaque(false);
      }
    }
  }

  /**
   * Update editor component horizontal alignment, be aware of
   * horizontalAlignment and defaultHorizontal both be
   * OrchidConstants.SWING_DEFAULT.
   */
  protected void updateEditorComponentAlignment(Class<?> columnClass) {
    int horizAlignment;
    if (horizontalAlignment != OrchidConstants.SWING_DEFAULT) {
      horizAlignment = horizontalAlignment;
    } else if (Number.class.isAssignableFrom(columnClass)
            || Date.class.isAssignableFrom(columnClass)) {
      horizAlignment = SwingConstants.TRAILING;
    } else if (Boolean.class.isAssignableFrom(columnClass)
            || Icon.class.isAssignableFrom(columnClass)) {
      horizAlignment = SwingConstants.CENTER;
    } else {
      horizAlignment = SwingConstants.LEADING;
    }
    if (editorComponent instanceof JTextField) {
      ((JTextField) editorComponent).setHorizontalAlignment(horizAlignment);
    } else if (editorComponent instanceof JRadioButton) {
      ((JRadioButton) editorComponent).setHorizontalAlignment(horizAlignment);
    } else if (editorComponent instanceof JCheckBox) {
      ((JCheckBox) editorComponent).setHorizontalAlignment(horizAlignment);
    }
  }

  /**
   * Returns suitable patterns for the valueClass, that numberPattern for Number
   * and it's sub-class, dateTimePattern for Date and it's sub-class, null for
   * others.
   *
   * @param valueClass value class
   * @return patterns
   */
  protected String getSuitablePattern(Class valueClass) {
    if (Number.class.isAssignableFrom(valueClass)) {
      return numberPattern;
    } else if (Date.class.isAssignableFrom(valueClass)) {
      return dateTimePattern;
    }
    return null;
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
    // no contents need.
  }

  public static abstract class EditorDelegate<T extends JComponent>
          implements ActionListener, ItemListener, Serializable {

    private JocTableCellEditor cellEditor;

    private void installEditor(JocTableCellEditor cellEditor) {
      this.cellEditor = cellEditor;
    }

    /**
     * Returns the cellEditor this delegate attatched to.
     *
     * @return cellEditor.
     */
    protected JocTableCellEditor getCellEditor() {
      return cellEditor;
    }

    /**
     * Returns default click count to start, cell editor should use this value
     * when the property clickCountToStart of cell editor is not set. default is
     * 1.
     *
     * @return default click count to start.
     */
    protected int getDefaultClickCountToStart() {
      return 1;
    }

    /**
     * Returns true if
     * <code>anEvent</code> is <b>not</b> a
     * <code>MouseEvent</code>. Otherwise, it returns true if the necessary
     * number of clicks have occurred, and returns false otherwise.
     *
     * @param anEvent the event
     * @return true if cell is ready for editing, false otherwise
     * @see #setClickCountToStart
     * @see #shouldSelectCell
     */
    public boolean isCellEditable(EventObject anEvent) {
      if (anEvent instanceof MouseEvent) {
        int clickCountToStart = getCellEditor().getClickCountToStart();
        return ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
      }
      return true;
    }

    /**
     * Returns true to indicate that the editing cell may be selected.
     *
     * @param anEvent the event
     * @return true
     * @see #isCellEditable
     */
    public boolean shouldSelectCell(EventObject anEvent) {
      return true;
    }

    /**
     * Stops editing and returns true to indicate that editing has stopped. This
     * method calls
     * <code>fireEditingStopped</code>.
     *
     * @return true
     */
    public boolean stopCellEditing() {
      getCellEditor().fireEditingStopped();
      return true;
    }

    /**
     * Cancels editing. This method calls
     * <code>fireEditingCanceled</code>.
     */
    public void cancelCellEditing() {
      getCellEditor().fireEditingCanceled();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      getCellEditor().stopCellEditing();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
      getCellEditor().stopCellEditing();
    }

    /**
     * call while editor component install, sub-class should install listener in
     * this method.
     *
     * @param editorComponent editor component
     */
    protected abstract void installComponent(T editorComponent);

    /**
     * call while editor component uninstall, sub-class should uninstall
     * listener in this method.
     *
     * @param editorComponent editor component
     */
    protected abstract void uninstallComponent(T editorComponent);

    /**
     *
     *
     * @param editor
     * @param valueClass
     * @param pattern
     * @return
     */
    public abstract Object getValue(T editor, Class<?> valueClass,
            String pattern) throws Exception;

    /**
     * update table cell value to editor component.
     *
     * @param editor editor component
     * @param valueClass column value class
     * @param value value
     */
    public abstract void setValue(T editor, Class<?> valueClass,
            String pattern, Object value);
  }

  public static class ValueFieldDelegate extends EditorDelegate<JocValueField> {

    @Override
    protected void installComponent(JocValueField editorComponent) {
      editorComponent.addActionListener(this);
      editorComponent.setName("Table.editor");
    }

    @Override
    protected void uninstallComponent(JocValueField editorComponent) {
      editorComponent.removeActionListener(this);
    }

    @Override
    protected int getDefaultClickCountToStart() {
      return 2;
    }

    @Override
    public boolean stopCellEditing() {
      JocValueField field = (JocValueField) getCellEditor()
              .getEditorComponent();
      if (field.isInputInvalid()) {
        field.setBorder(new LineBorder(Color.RED));
        return false;
      }
      return super.stopCellEditing();
    }

    @Override
    public Object getValue(JocValueField editor, Class<?> valueClass,
            String pattern) throws Exception {
      return editor.getValue();
    }

    @Override
    public void setValue(JocValueField editor, Class<?> valueClass,
            String pattern, Object value) {
      editor.setBorder(LineBorder.createBlackLineBorder());
      if (editor.getModel().getValueClass() != valueClass) {
        editor.setModel(createSuitableModel(valueClass));
      }
      ValueModel valueModel = editor.getModel();
      if (Number.class.isAssignableFrom(valueClass)
              && valueModel instanceof NumberValueModel) {
        ((NumberValueModel) valueModel).setPattern(pattern);
      } else if (Date.class.isAssignableFrom(valueClass)
              && valueModel instanceof DateValueModel) {
        ((DateValueModel) valueModel).setPattern(pattern);
      }
      editor.setValue(value);
    }

    @SuppressWarnings("unchecked")
    private ValueModel createSuitableModel(Class<?> valueClass) {
      if (valueClass == String.class) {
        return new StringValueModel(false, null);
      } else if (valueClass == Date.class) {
        return new DateValueModel();
      } else if (Number.class.isAssignableFrom(valueClass)) {
        return new NumberValueModel(
                (Class<? extends Number>) valueClass, null, null);
      } else if (valueClass == File.class) {
        return new FileValueModel();
      } else {
        return new GenericValueModel(valueClass);
      }
    }
  }

  public static class TextFieldDelegate extends EditorDelegate<JTextField> {

    private static Class[] argTypes = new Class[]{String.class};

    @Override
    protected void installComponent(JTextField editorComponent) {
      editorComponent.addActionListener(this);
      editorComponent.setName("Table.editor");
    }

    @Override
    protected void uninstallComponent(JTextField editorComponent) {
      editorComponent.removeActionListener(this);
    }

    @Override
    protected int getDefaultClickCountToStart() {
      return 2;
    }

    @Override
    public Object getValue(JTextField editor, Class<?> valueClass,
            String pattern) throws Exception {
      String text = editor.getText();
      if (Number.class.isAssignableFrom(valueClass)) {
        return parseNumber(valueClass, pattern, text);
      } else if (Date.class.isAssignableFrom(valueClass)) {
        return parseDateTime(valueClass, pattern, text);
      } else {
        return parseGenericValue(valueClass, text);
      }
    }

    @Override
    public void setValue(JTextField editor, Class<?> valueClass,
            String pattern, Object value) {
      editor.setBorder(LineBorder.createBlackLineBorder());
      String text = "";
      if (value != null) {
        if (Number.class.isAssignableFrom(valueClass)) {
          text = formatNumber(pattern, (Number) value);
        } else if (Date.class.isAssignableFrom(valueClass)) {
          text = formatDateTime(pattern, (Date) value);
        } else {
          text = value.toString();
        }
      }
      editor.setText(text);
    }

    protected String formatNumber(String pattern, Number value) {
      return new NumberToStringConverter(pattern).convertForward(value);
    }

    protected String formatDateTime(String pattern, Date value) {
      return new DateToStringConverter(pattern).convertForward(value);
    }

    @SuppressWarnings("unchecked")
    protected Object parseNumber(Class valueClass,
            String pattern, String text) throws Exception {
      try {
        return new NumberToStringConverter(pattern, valueClass)
                .convertReverse(text);
      } catch (RuntimeException ex) {
        //
      }
      return parseGenericValue(valueClass, text);
    }

    protected Object parseDateTime(Class valueClass,
            String pattern, String text) throws Exception {
      try {
        return new DateToStringConverter(pattern)
                .convertReverse(text);
      } catch (RuntimeException ex) {
        //
      }
      return parseGenericValue(valueClass, text);
    }

    @SuppressWarnings("unchecked")
    protected Object parseGenericValue(Class valueClass, String text)
            throws Exception {
      Class type = valueClass == Object.class ? String.class : valueClass;
      Constructor constructor = type.getConstructor(argTypes);
      if ("".equals(text)) {
        if (constructor.getDeclaringClass() == String.class) {
          return text;
        }
      }
      return constructor.newInstance(new Object[]{text});
    }
  }

  public static class RadioDelegate extends EditorDelegate<JRadioButton> {

    protected ButtonGroup oneRadioGroup = new ButtonGroup();

    @Override
    protected void installComponent(JRadioButton editorComponent) {
      oneRadioGroup.add(editorComponent);
      editorComponent.addActionListener(this);
      editorComponent.setRequestFocusEnabled(false);
      editorComponent.setHorizontalAlignment(CENTER);
    }

    @Override
    protected void uninstallComponent(JRadioButton editorComponent) {
      oneRadioGroup.remove(editorComponent);
      editorComponent.removeActionListener(this);
    }

    @Override
    public Object getValue(JRadioButton editor, Class<?> valueClass,
            String pattern) {
      return getCellEditor().convertFromBoolean(editor.isSelected());
    }

    @Override
    public void setValue(JRadioButton editor, Class<?> valueClass,
            String pattern, Object value) {
      editor.setSelected(getCellEditor().convertToBoolean(value));
    }
  }

  public static class CheckDelegate extends EditorDelegate<JCheckBox> {

    @Override
    protected void installComponent(JCheckBox editorComponent) {
      editorComponent.addActionListener(this);
      editorComponent.setRequestFocusEnabled(false);
      editorComponent.setHorizontalAlignment(CENTER);
    }

    @Override
    protected void uninstallComponent(JCheckBox editorComponent) {
      editorComponent.removeActionListener(this);
    }

    @Override
    public Object getValue(JCheckBox editor, Class<?> valueClass,
            String pattern) {
      return getCellEditor().convertFromBoolean(editor.isSelected());
    }

    @Override
    public void setValue(JCheckBox editor, Class<?> valueClass,
            String pattern, Object value) {
      editor.setSelected(getCellEditor().convertToBoolean(value));
    }
  }

  public static class ComboBoxDelegate extends EditorDelegate<JComboBox> {

    @Override
    protected void installComponent(JComboBox editorComponent) {
      editorComponent.addActionListener(this);
    }

    @Override
    protected void uninstallComponent(JComboBox editorComponent) {
      editorComponent.removeActionListener(this);
    }

    @Override
    public Object getValue(JComboBox editor, Class<?> valueClass,
            String pattern) {
      return editor.getSelectedItem();
    }

    @Override
    public void setValue(JComboBox editor, Class<?> valueClass,
            String pattern, Object value) {
      editor.setSelectedItem(value);
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
      if (anEvent instanceof MouseEvent) {
        MouseEvent e = (MouseEvent) anEvent;
        return e.getID() != MouseEvent.MOUSE_DRAGGED;
      }
      return true;
    }
  }
}
