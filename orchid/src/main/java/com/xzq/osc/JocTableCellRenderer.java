/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.field.DateToStringConverter;
import com.xzq.osc.field.NumberToStringConverter;
import com.xzq.osc.plaf.LookAndFeelManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author zqxu
 */
public class JocTableCellRenderer implements TableCellRenderer,
        Serializable, SwingConstants, OrchidAboutIntf {

  private static final Border SAFE_NO_FOCUS_BORDER
          = new EmptyBorder(1, 1, 1, 1);
  private static final Border DEFAULT_NO_FOCUS_BORDER
          = new EmptyBorder(1, 1, 1, 1);
  private static final EmptyRenderer EMPTY_RENDERER
          = new EmptyRenderer();
  protected static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;
  protected JComponent rendererComponent;
  protected RendererDelegate rendererDelegate;
  private Color foreground;
  private Color background;
  private boolean emptyNullCell;
  private boolean emptyZeroNumber;
  private Icon icon;
  private String text;
  private Object trueValue;
  private boolean nullAsFalse;
  private int horizontalAlignment;
  private String numberPattern;
  private String dateTimePattern;

  /**
   * Constructor with default renderer component use
   * com.xzq.osc.JocTableCellRenderer.LabelRenderer.
   */
  public JocTableCellRenderer() {
    this(LabelRenderer.class);
  }

  /**
   * Constructor with renderer component class. see setRendererClass.
   *
   * @param rendererClass renderer component class.
   * @see #setRendererClass(java.lang.Class)
   */
  public JocTableCellRenderer(Class<? extends JComponent> rendererClass) {
    trueValue = Boolean.TRUE;
    nullAsFalse = true;
    LookAndFeelManager.installOrchidUI();
    setRendererClass(rendererClass);
    setHorizontalAlignment(OrchidConstants.SWING_DEFAULT);
  }

  private Border getNoFocusBorder() {
    Border border = UIManager.getBorder(rendererComponent,
            rendererComponent.getLocale());
    if (System.getSecurityManager() != null) {
      if (border != null) {
        return border;
      }
      return SAFE_NO_FOCUS_BORDER;
    } else if (border != null) {
      if (noFocusBorder == null || noFocusBorder == DEFAULT_NO_FOCUS_BORDER) {
        return border;
      }
    }
    return noFocusBorder;
  }

  /**
   * Returns current renderer component class.
   *
   * @return renderer component class.
   */
  public Class getRendererClass() {
    return rendererComponent == null ? null : rendererComponent.getClass();
  }

  /**
   * Set renderer component by class, three class support currently.
   * <ul><li>com.xzq.osc.JocTableCellRenderer.LabelRenderer</li>
   * <li>com.xzq.osc.JocTableCellRenderer.RadioRenderer</li>
   * <li>com.xzq.osc.JocTableCellRenderer.CheckRenderer</li></ul>for other
   * renderer component, please use setRendererComponent directly.
   *
   * @param rendererClass renderer component class
   */
  public void setRendererClass(Class<? extends JComponent> rendererClass) {
    if (rendererClass == getRendererClass()) {
      return;
    }
    if (rendererClass == LabelRenderer.class) {
      setRendererComponent(new LabelRenderer(), new LabelDelegate());
    } else if (rendererClass == PasswordRenderer.class) {
      setRendererComponent(new PasswordRenderer(), new PasswordDelegate());
    } else if (rendererClass == RadioRenderer.class) {
      setRendererComponent(new RadioRenderer(), new RadioDelegate());
    } else if (rendererClass == CheckRenderer.class) {
      setRendererComponent(new CheckRenderer(), new CheckDelegate());
    } else {
      throw new IllegalArgumentException(
              "Unkown class, please use setRendererComponent instead.");
    }
  }

  /**
   * Returns current renderer component.
   *
   * @return renderer component.
   */
  public JComponent getRendererComponent() {
    return rendererComponent;
  }

  /**
   * Returns current renderer delegate.
   *
   * @return renderer delegate.
   */
  public RendererDelegate getRendererDelegate() {
    return rendererDelegate;
  }

  /**
   * Set current renderer component.
   *
   * @param <V> Generic Class for value delegate
   * @param <T> Generic Class for renderer component
   * @param rendererComponent renderer component
   * @param rendererDelegate value delegate
   */
  public <V extends JComponent, T extends V> void setRendererComponent(
          T rendererComponent, RendererDelegate<V> rendererDelegate) {
    if (rendererComponent == null) {
      throw new IllegalArgumentException("rendererComponent can not be null.");
    }
    if (rendererDelegate == null) {
      throw new IllegalArgumentException("rendererDelegate can not be null.");
    }
    if (this.rendererDelegate != null) {
      this.rendererDelegate.installRenderer(null);
    }
    this.rendererComponent = rendererComponent;
    this.rendererDelegate = rendererDelegate;
    this.rendererDelegate.installRenderer(this);
  }

  /**
   * Returns renderer component current horizontal alignment, used for saving
   * default horizontal alignment.
   *
   * @param rendererComponent renderer component.
   * @return renderer component horizontal alignment.
   */
  protected int getRendererHorizontal(JComponent rendererComponent) {
    if (rendererComponent instanceof JLabel) {
      return ((JLabel) rendererComponent).getHorizontalAlignment();
    } else if (rendererComponent instanceof JRadioButton) {
      return ((JRadioButton) rendererComponent).getHorizontalAlignment();
    } else if (rendererComponent instanceof JCheckBox) {
      return ((JCheckBox) rendererComponent).getHorizontalAlignment();
    } else {
      return OrchidConstants.SWING_DEFAULT;
    }
  }

  /**
   * Returns foreground color use for renderer component.
   *
   * @return foreground color.
   */
  public Color getForeground() {
    return foreground;
  }

  /**
   * Set forground color use for renderer component.
   *
   * @param foreground foreground color.
   */
  public void setForeground(Color foreground) {
    if (rendererComponent != null) {
      rendererComponent.setForeground(foreground);
    }
    this.foreground = foreground;
  }

  /**
   * Returns background color use for renderer component.
   *
   * @return background color.
   */
  public Color getBackground() {
    return background;
  }

  /**
   * Set background color use for renderer component.
   *
   * @param background background color.
   */
  public void setBackground(Color background) {
    if (rendererComponent != null) {
      rendererComponent.setBackground(background);
    }
    this.background = background;
  }

  /**
   * Return icon use for renderer component. Only effect if renderer component
   * was instance of JLabel and table cell value not be icon.
   *
   * @return icon
   */
  public Icon getIcon() {
    return icon;
  }

  /**
   * Set icon use for renderer component. Only effect if renderer component was
   * instance of JLabel and table cell value not be icon.
   *
   * @param icon
   * @see #updateRendererComponentIcon()
   */
  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  /**
   * Return text use for renderer component. Only effect if renderer component
   * was instance of JLabel or JRadioButton or JCheckBox. if renderer component
   * was instance of JLabel and table cell value not be icon, this text will be
   * ignored.
   *
   * @return text
   */
  public String getText() {
    return text;
  }

  /**
   * Set text use for renderer component. Only effect if renderer component was
   * instance of JLabel or JRadioButton or JCheckBox. if renderer component was
   * instance of JLabel and table cell value not be icon, this text will be
   * ignored.
   *
   * @param text text
   * @see #updateRendererComponentText()
   */
  public void setText(String text) {
    this.text = text;
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
   * Returns horizontal alignment for renderer component. default is
   * OrchidConstants.SWING_DEFAULT. only support renderer component instance of
   * JLabel or JRadioButton or JCheckBox.
   *
   * @return horizontal alignment.
   * @see #setHorizontalAlignment(int)
   */
  public int getHorizontalAlignment() {
    return horizontalAlignment;
  }

  /**
   * Set horizontal alignment for renderer component. only support renderer
   * component instance of JLabel or JRadioButton or JCheckBox.
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
   * Returns date time format pattern of current date time format.
   *
   * @return date time format pattern
   */
  public String getDateTimePattern() {
    return dateTimePattern;
  }

  /**
   * Set date time format by the date time format pattern.
   *
   * @param dateTimePattern date time format pattern
   */
  public void setDateTimePattern(String dateTimePattern) {
    this.dateTimePattern = dateTimePattern;
  }

  /**
   * Returns indicator that show empty cell renderer when cell value is null or
   * not, default is false. usually used with CheckRenderer or RadioRenderer.
   *
   * @return true or false.
   */
  public boolean isEmptyNullCell() {
    return emptyNullCell;
  }

  /**
   * Sets emptyNullCell property value.
   *
   * @param emptyNullCell true or false.
   */
  public void setEmptyNullCell(boolean emptyNullCell) {
    this.emptyNullCell = emptyNullCell;
  }

  /**
   *
   * @return
   */
  public boolean isEmptyZeroNumber() {
    return emptyZeroNumber;
  }

  /**
   *
   * @param emptyZeroNumber
   */
  public void setEmptyZeroNumber(boolean emptyZeroNumber) {
    this.emptyZeroNumber = emptyZeroNumber;
  }

  /**
   * Returns the default table cell renderer.
   * <p>
   * During a printing operation, this method will be called with
   * <code>isSelected</code> and <code>hasFocus</code> values of
   * <code>false</code> to prevent selection and focus from appearing in the
   * printed output. To do other customization based on whether or not the table
   * is being printed, check the return value from
   * {@link javax.swing.JComponent#isPaintingForPrint()}.
   *
   * @param table the <code>JTable</code>
   * @param value the value to assign to the cell at <code>[row, column]</code>
   * @param isSelected true if cell is selected
   * @param hasFocus true if cell has focus
   * @param row the row of the cell to render
   * @param column the column of the cell to render
   * @return the default table cell renderer
   * @see javax.swing.JComponent#isPaintingForPrint()
   */
  @Override
  public Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected, boolean hasFocus,
          int row, int column) {
    Color fg = null;
    Color bg = null;
    JComponent retComponent;
    Locale locale = rendererComponent.getLocale();

    Class valueClass = table.getColumnClass(column);
    if (isEmptyNullCell() && value == null) {
      retComponent = EMPTY_RENDERER;
    } else if (isEmptyZeroNumber()
            && Number.class.isAssignableFrom(valueClass)
            && value instanceof Number
            && ((Number) value).doubleValue() == 0) {
      retComponent = EMPTY_RENDERER;
    } else {
      retComponent = rendererComponent;
    }

    JTable.DropLocation dropLocation = table.getDropLocation();
    if (dropLocation != null
            && !dropLocation.isInsertRow()
            && !dropLocation.isInsertColumn()
            && dropLocation.getRow() == row
            && dropLocation.getColumn() == column) {
      fg = UIManager.getColor("Table.dropCellForeground", locale);
      bg = UIManager.getColor("Table.dropCellBackground", locale);
      isSelected = true;
    }

    if (isSelected) {
      retComponent.setForeground(
              fg == null ? table.getSelectionForeground() : fg);
      retComponent.setBackground(
              bg == null ? table.getSelectionBackground() : bg);
    } else {
      Color backcolor = this.background != null
              ? this.background : table.getBackground();
      if (backcolor == null || backcolor instanceof UIResource) {
        Color alternateColor = UIManager.getColor(
                "Table.alternateRowColor", locale);
        if (alternateColor != null && row % 2 == 0) {
          backcolor = alternateColor;
        }
      }
      retComponent.setForeground(foreground != null
              ? foreground : table.getForeground());
      retComponent.setBackground(backcolor);
    }
    retComponent.setFont(table.getFont());

    if (hasFocus) {
      Border border = null;
      if (isSelected) {
        border = UIManager.getBorder(
                "Table.focusSelectedCellHighlightBorder", locale);
      }
      if (border == null) {
        border = UIManager.getBorder(
                "Table.focusCellHighlightBorder", locale);
      }
      retComponent.setBorder(border);
      if (!isSelected && table.isCellEditable(row, column)) {
        Color col;
        col = UIManager.getColor("Table.focusCellForeground", locale);
        if (col != null) {
          rendererComponent.setForeground(col);
        }
        col = UIManager.getColor("Table.focusCellBackground", locale);
        if (col != null) {
          retComponent.setBackground(col);
        }
      }
    } else {
      retComponent.setBorder(getNoFocusBorder());
    }
    if (retComponent == rendererComponent) {
      setRendererComponentValue(table, row, column, value);
    }
    updateRendererComponentIcon();
    updateRendererComponentText();
    updateRendererComponentAlignment(valueClass);
    return retComponent;
  }

  /**
   * Before set cell value to renderer component, update renderer component icon
   * to icon returns by getIcon, only effect if rendererComponent was instance
   * of JLabel, and maybe reset by cell value.
   */
  protected void updateRendererComponentIcon() {
    if (rendererComponent instanceof JLabel) {
      ((JLabel) rendererComponent).setIcon(getIcon());
    }
  }

  /**
   * Before set cell value to renderer component, update renderer component text
   * to text returns by getText, only effect if rendererComponent was instance
   * of JLabel or JRadioButton or JCheckBox, and maybe reset by cell value.
   */
  protected void updateRendererComponentText() {
    if (rendererComponent instanceof JLabel) {
      ((JLabel) rendererComponent).setText(getText());
    } else if (rendererComponent instanceof JRadioButton) {
      ((JRadioButton) rendererComponent).setText(getText());
    } else if (rendererComponent instanceof JCheckBox) {
      ((JCheckBox) rendererComponent).setText(getText());
    }
  }

  /**
   * Update renderer component horizontal alignment, be aware of
   * horizontalAlignment and defaultHorizontal both be
   * OrchidConstants.SWING_DEFAULT.
   */
  protected void updateRendererComponentAlignment(Class<?> columnClass) {
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
    if (rendererComponent instanceof JLabel) {
      ((JLabel) rendererComponent).setHorizontalAlignment(horizAlignment);
    } else if (rendererComponent instanceof JRadioButton) {
      ((JRadioButton) rendererComponent).setHorizontalAlignment(horizAlignment);
    } else if (rendererComponent instanceof JCheckBox) {
      ((JCheckBox) rendererComponent).setHorizontalAlignment(horizAlignment);
    }
  }

  /**
   * call RendererDelegate set renderer component value.
   *
   * @param table table
   * @param row row
   * @param column column
   * @param value value
   */
  @SuppressWarnings("unchecked")
  protected void setRendererComponentValue(JTable table, int row,
          int column, Object value) {
    String pattern = null;
    Class valueClass = table.getColumnClass(column);
    if (Number.class.isAssignableFrom(valueClass)) {
      pattern = getNumberPattern();
    } else if (Date.class.isAssignableFrom(valueClass)) {
      pattern = getDateTimePattern();
    }
    rendererDelegate.setValue(rendererComponent, valueClass, pattern, value);
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

  public static class LabelRenderer extends JLabel {

    public LabelRenderer() {
      super();
      setOpaque(true);
      setName("Table.cellRenderer");
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void invalidate() {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void validate() {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void revalidate() {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void repaint(Rectangle r) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void repaint() {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    protected void firePropertyChange(String propertyName,
            Object oldValue, Object newValue) {
      if ("text".equals(propertyName)
              || "labelFor".equals(propertyName)
              || "displayedMnemonic".equals(propertyName)
              || (("font".equals(propertyName)
              || "foreground".equals(propertyName))
              && oldValue != newValue
              && getClientProperty(BasicHTML.propertyKey) != null)) {

        super.firePropertyChange(propertyName, oldValue, newValue);
      }
    }

    /**
     * Overridden for performance reasons
     */
    @Override
    public void firePropertyChange(String propertyName,
            boolean oldValue, boolean newValue) {
    }
  }

  public static class PasswordRenderer extends LabelRenderer {

    private char echoChar;

    public PasswordRenderer() {
      super();
      Character echo = (Character) UIManager.get("PasswordField.echoChar");
      if (echo != null) {
        echoChar = echo;
      }
    }

    public PasswordRenderer(char echoChar) {
      super();
      this.echoChar = echoChar;
    }

    public char getEchoChar() {
      return echoChar;
    }

    public void setEchoChar(char echoChar) {
      this.echoChar = echoChar;
    }

    @Override
    public void setText(String text) {
      int length = text == null ? 0 : text.length();
      char[] password = new char[length];
      Arrays.fill(password, echoChar);
      super.setText(new String(password));
    }
  }

  public static class RadioRenderer extends JRadioButton {

    public RadioRenderer() {
      super();
      setBorderPainted(true);
      setHorizontalAlignment(CENTER);
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void invalidate() {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void validate() {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void revalidate() {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void repaint(Rectangle r) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void repaint() {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    protected void firePropertyChange(String propertyName,
            Object oldValue, Object newValue) {
      if ("text".equals(propertyName)
              || "labelFor".equals(propertyName)
              || "displayedMnemonic".equals(propertyName)
              || (("font".equals(propertyName)
              || "foreground".equals(propertyName))
              && oldValue != newValue
              && getClientProperty(BasicHTML.propertyKey) != null)) {

        super.firePropertyChange(propertyName, oldValue, newValue);
      }
    }

    /**
     * Overridden for performance reasons
     */
    @Override
    public void firePropertyChange(String propertyName,
            boolean oldValue, boolean newValue) {
    }
  }

  public static class CheckRenderer extends JCheckBox {

    public CheckRenderer() {
      super();
      setBorderPainted(true);
      setHorizontalAlignment(CENTER);
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void invalidate() {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void validate() {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void revalidate() {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void repaint(Rectangle r) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void repaint() {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    protected void firePropertyChange(String propertyName,
            Object oldValue, Object newValue) {
      if ("text".equals(propertyName)
              || "labelFor".equals(propertyName)
              || "displayedMnemonic".equals(propertyName)
              || (("font".equals(propertyName)
              || "foreground".equals(propertyName))
              && oldValue != newValue
              && getClientProperty(BasicHTML.propertyKey) != null)) {

        super.firePropertyChange(propertyName, oldValue, newValue);
      }
    }

    /**
     * Overridden for performance reasons
     */
    @Override
    public void firePropertyChange(String propertyName,
            boolean oldValue, boolean newValue) {
    }
  }

  private static class EmptyRenderer extends LabelRenderer {

    @Override
    public void setIcon(Icon icon) {
    }

    @Override
    public void setText(String text) {
    }
  }

  public static interface RendererDelegate<T extends JComponent> {

    /**
     * install JocTableCellRenderer, delegate should uninstall if the
     * cellRenderer is null.
     *
     * @param cellRenderer the renderer.
     */
    public void installRenderer(JocTableCellRenderer cellRenderer);

    /**
     * update table cell value to renderer component.
     *
     * @param renderer renderer component
     * @param valueClass column value class
     * @param pattern pattern to format number or date time
     * @param value value
     */
    public void setValue(T renderer, Class valueClass, String pattern, Object value);
  }

  public static class LabelDelegate implements RendererDelegate<JLabel> {

    private JocTableCellRenderer cellRenderer;

    @Override
    public void installRenderer(JocTableCellRenderer cellRenderer) {
      this.cellRenderer = cellRenderer;
    }

    @Override
    public void setValue(JLabel renderer, Class valueClass,
            String pattern, Object value) {
      if (Icon.class.isAssignableFrom(valueClass)) {
        cellRenderer.setIcon((Icon) value);
        setText(renderer, valueClass, pattern, null);
      } else {
        cellRenderer.setIcon(null);
        setText(renderer, valueClass, pattern, value);
      }
    }

    protected void setText(JLabel renderer, Class valueClass,
            String pattern, Object value) {
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
      cellRenderer.setText(text);
    }

    protected String formatNumber(String pattern, Number value) {
      return new NumberToStringConverter(pattern).convertForward(value);
    }

    protected String formatDateTime(String pattern, Date value) {
      return new DateToStringConverter(pattern).convertForward(value);
    }
  }

  public static class RadioDelegate
          implements RendererDelegate<JRadioButton> {

    private JocTableCellRenderer cellRenderer;

    @Override
    public void installRenderer(JocTableCellRenderer cellRenderer) {
      this.cellRenderer = cellRenderer;
    }

    @Override
    public void setValue(JRadioButton renderer, Class valueClass,
            String pattern, Object value) {
      renderer.setSelected(cellRenderer.convertToBoolean(value));
    }
  }

  public static class CheckDelegate implements RendererDelegate<JCheckBox> {

    private JocTableCellRenderer cellRenderer;

    @Override
    public void installRenderer(JocTableCellRenderer cellRenderer) {
      this.cellRenderer = cellRenderer;
    }

    @Override
    public void setValue(JCheckBox renderer, Class valueClass,
            String pattern, Object value) {
      renderer.setSelected(cellRenderer.convertToBoolean(value));
    }
  }

  public static class PasswordDelegate implements RendererDelegate<PasswordRenderer> {

    private JocTableCellRenderer cellRenderer;

    @Override
    public void installRenderer(JocTableCellRenderer cellRenderer) {
      this.cellRenderer = cellRenderer;
    }

    @Override
    public void setValue(PasswordRenderer renderer, Class valueClass,
            String pattern, Object value) {
      cellRenderer.setText(value == null ? "" : value.toString());
    }
  }
}
