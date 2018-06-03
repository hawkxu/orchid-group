/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.OrchidUtils;
import com.xzq.osc.plaf.OrchidDefaults;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicHTML;

/**
 *
 * @author zqxu
 */
public class ListValuePicker extends DropValuePicker implements Cloneable {

  /**
   * Use the value in UIDefaults with key OrchidDefaults
   */
  public static final int UI_DEFAULTS = 0;
  public static final int KEY_ONLY = 1;
  public static final int TEXT_ONLY = 2;
  public static final int KEY_AND_TEXT = 3;
  protected JList pickListComponent;
  private ValueListModel listModel;
  private int visibleRowCount;
  private Renderer entryRenderer;
  private int displayFormat;
  private Converter<Object, String> converter;
  private Converter<Object, String> innerConverter;
  
  public static ListValuePicker from(Object... values) {
    List<ValuePickEntry> list = new ArrayList<ValuePickEntry>();
    for (Object value: values) {
      list.add(new ValuePickEntry(value));
    }
    return new ListValuePicker(list);
  }

  public ListValuePicker() {
    this(UI_DEFAULTS, (List<ValuePickEntry>) null);
  }

  public ListValuePicker(ValuePickEntry... valuePickEntries) {
    this(UI_DEFAULTS, valuePickEntries);
  }

  public ListValuePicker(int displayFormat,
          ValuePickEntry... valuePickEntries) {
    this(displayFormat, Arrays.asList(valuePickEntries));
  }

  public ListValuePicker(List<ValuePickEntry> valuePickList) {
    this(UI_DEFAULTS, valuePickList);
  }

  public ListValuePicker(int displayFormat,
          List<ValuePickEntry> valuePickList) {
    super();
    this.displayFormat = displayFormat;
    pickListComponent = new JList(listModel = new ValueListModel());
    listModel.setValuePickList(valuePickList);
    pickListComponent.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        pickListKeyReleased(e);
      }
    });
    pickListComponent.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        pickListMouseReleased(e);
      }
    });
    pickListComponent.setCellRenderer(entryRenderer = new Renderer());
    pickListComponent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  /**
   * Returns value pick list of the picker.
   *
   * @return value pick list.
   */
  public List<ValuePickEntry> getValuePickList() {
    return listModel.getValuePickList();
  }

  /**
   * Sets value pick list of the picker.
   *
   * @param valuePickList value pick list.
   */
  public void setValuePickList(List<ValuePickEntry> valuePickList) {
    listModel.setValuePickList(valuePickList);
  }

  /**
   * Returns visible row count in list value picker.
   *
   * @return visible row count.
   */
  public int getVisibleRowCount() {
    if (visibleRowCount > 0) {
      return visibleRowCount;
    }
    return UIManager.getInt(OrchidDefaults.LIST_PICKER_ROWS);
  }

  /**
   * Sets visible row count int list value picker.
   *
   * @param visibleRowCount visible row count.
   */
  public void setVisibleRowCount(int visibleRowCount) {
    this.visibleRowCount = visibleRowCount;
  }

  /**
   * Returns display format of the list value picker, default is UI_DEFAULTS.
   *
   * @return display format.
   */
  public int getDisplayFormat() {
    return displayFormat;
  }

  /**
   * Sets display format of the list value picker, one of these constants:
   * UI_DEFAULTS, KEY_ONLY, TEXT_ONLY, KEY_AND_TEXT.
   *
   * @param displayFormat
   */
  public void setDisplayFormat(int displayFormat) {
    this.displayFormat = displayFormat;
  }

  /**
   * Returns using display format, retrieve value from UIDefaults if need.
   *
   * @return using display format.
   */
  protected int getUsingDisplayFormat() {
    int format = getDisplayFormat();
    if (format == UI_DEFAULTS) {
      format = UIManager.getInt(OrchidDefaults.LIST_PICKER_FORMAT);
    }
    return format == KEY_ONLY || format == TEXT_ONLY ? format : KEY_AND_TEXT;
  }

  @Override
  public Converter<Object, String> getConverter() {
    if (converter == null) {
      converter = new ListValueConverter();
    }
    return converter;
  }

  /**
   * Returns inner converter to convert the key value to string. default is
   * null.
   *
   * @return inner converter
   */
  public Converter<Object, String> getInnerConverter() {
    return innerConverter;
  }

  /**
   * Sets inner converter to convert the key value to string. if no inner
   * converter, the toString method of the key value will be used.
   *
   * @param innerConverter
   */
  public void setInnerConverter(Converter<Object, String> innerConverter) {
    this.innerConverter = innerConverter;
  }

  /**
   * convert key value to String. use inner converter, if inner converter is
   * null, the toString method of the key value will be used.
   *
   * @param value the key value
   * @return string.
   */
  protected String convertKeyValueToString(Object value) {
    try {
      if (innerConverter != null) {
        return innerConverter.convertForward(value);
      }
    } catch (RuntimeException ex) {
      // ignore the exception
    }
    return value == null ? "" : value.toString();
  }

  protected void pickListKeyReleased(KeyEvent e) {
    if (e.getModifiers() == 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
      if (pickListComponent.getSelectedIndex() != -1) {
        processPickupEvent(e);
      }
    }
  }

  protected void pickListMouseReleased(MouseEvent e) {
    if (e.getModifiers() == MouseEvent.BUTTON1_MASK && e.getClickCount() == 1) {
      int index = pickListComponent.locationToIndex(e.getPoint());
      if (index != -1 && index == pickListComponent.getSelectedIndex()) {
        processPickupEvent(e);
      }
    }
  }

  protected void processPickupEvent(InputEvent e) {
    Object selected = pickListComponent.getSelectedValue();
    commitValuePicker(((ValuePickEntry) selected).getKey());
  }

  @Override
  public Component preparePickComponent(Object value) {
    int index = listModel.indexOfValue(value);
    if (index != -1) {
      pickListComponent.setSelectedIndex(index);
    }
    pickListComponent.setEnabled(field.isEnabled());
    pickListComponent.setFont(field.getFont());
    pickListComponent.setForeground(field.getForeground());
    pickListComponent.setBackground(field.getBackground());
    int visibRowCount = Math.min(
            listModel.getSize(), getVisibleRowCount());
    pickListComponent.setVisibleRowCount(visibRowCount);
    //force pikcListComponent to reset its preferred size.
    pickListComponent.setCellRenderer(null);
    entryRenderer.setDisplayFormat(getUsingDisplayFormat());
    int keyWidth = 0;
    for (int row = 0; row < listModel.getSize(); row++) {
      entryRenderer.getListCellRendererComponent(pickListComponent,
              listModel.getElementAt(row), row, false, false);
      keyWidth = Math.max(keyWidth, entryRenderer.getKeyValueWidth());
    }
    entryRenderer.setKeyValueDisplayWidth(keyWidth);
    pickListComponent.setCellRenderer(entryRenderer);
    return pickListComponent;
  }

  private class ListValueConverter implements Converter<Object, String> {

    @Override
    public String convertForward(Object value) {
      return getDisplayText(value);
    }

    @Override
    public Object convertReverse(String value) {
      List<ValuePickEntry> pickList = listModel.getValuePickList();
      boolean empty = OrchidUtils.isEmpty(value);
      if (pickList != null) {
        for (ValuePickEntry entry : pickList) {
          String key = convertKeyValueToString(entry.getKey());
          if (OrchidUtils.equals(value, key)
                  || (!empty && OrchidUtils.equals(value, entry.getText()))
                  || OrchidUtils.equals(value, getDisplayText(entry))) {
            return entry.getKey();
          }
        }
      }
      if (empty) {
        return null;
      }
      throw new IllegalArgumentException("can not convert " + value);
    }

    @Override
    public boolean reverseSupported() {
      return true;
    }

    private String getDisplayText(Object value) {
      int index = listModel.indexOfValue(value);
      if (index == -1) {
        return convertKeyValueToString(value);
      } else {
        return getDisplayText(listModel.getElementAt(index));
      }
    }

    private String getDisplayText(ValuePickEntry entry) {
      String keyString = convertKeyValueToString(entry.getKey());
      int format = getUsingDisplayFormat();
      if (format == KEY_ONLY) {
        return keyString;
      } else if (format == TEXT_ONLY) {
        return entry.getText();
      } else {
        if (OrchidUtils.isEmpty(entry.getText())) {
          return keyString;
        }
        return keyString + " " + entry.getText();
      }
    }
  }

  private static class ValueListModel extends AbstractListModel {

    private List<ValuePickEntry> valuePickList;

    public List<ValuePickEntry> getValuePickList() {
      if (valuePickList == null) {
        return null;
      }
      return Collections.unmodifiableList(valuePickList);
    }

    public void setValuePickList(List<ValuePickEntry> valuePickList) {
      if (getSize() > 0) {
        fireIntervalRemoved(this, 0, getSize() - 1);
      }
      this.valuePickList = valuePickList;
      if (getSize() > 0) {
        fireIntervalAdded(this, 0, getSize() - 1);
      }
    }

    public int indexOfValue(Object value) {
      if (valuePickList == null) {
        return -1;
      }
      return valuePickList.indexOf(new ValuePickEntry(value));
    }

    @Override
    public int getSize() {
      return valuePickList == null ? 0 : valuePickList.size();
    }

    @Override
    public ValuePickEntry getElementAt(int index) {
      return valuePickList.get(index);
    }
  }

  private static class KeyLabel extends JLabel {

    private Dimension preferredSize;

    @Override
    public Dimension getPreferredSize() {
      if (preferredSize != null) {
        return preferredSize;
      }
      return super.getPreferredSize();
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
      this.preferredSize = preferredSize;
    }

    public Dimension getDefaultSize() {
      return super.getPreferredSize();
    }
  }

  private static class Renderer extends JPanel implements ListCellRenderer {

    private static final Border NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
    private KeyLabel keyValueLabel;
    private JLabel displayTextLabel;

    public Renderer() {
      super();
      setLayout(new BorderLayout(8, 0));
      add(getKeyValueLabel(), BorderLayout.LINE_START);
      add(getDisplayTextLabel(), BorderLayout.CENTER);
    }

    private KeyLabel getKeyValueLabel() {
      if (keyValueLabel == null) {
        keyValueLabel = new KeyLabel();
        keyValueLabel.setOpaque(false);
      }
      return keyValueLabel;
    }

    private JLabel getDisplayTextLabel() {
      if (displayTextLabel == null) {
        displayTextLabel = new JLabel();
        displayTextLabel.setOpaque(false);
      }
      return displayTextLabel;
    }

    public void setDisplayFormat(int format) {
      getKeyValueLabel().setVisible((format & KEY_ONLY) == KEY_ONLY);
      getDisplayTextLabel().setVisible((format & TEXT_ONLY) == TEXT_ONLY);
    }

    public int getKeyValueWidth() {
      return getKeyValueLabel().getDefaultSize().width;
    }

    public void setKeyValueDisplayWidth(int width) {
      Dimension size = getKeyValueLabel().getDefaultSize();
      getKeyValueLabel().setPreferredSize(new Dimension(width, size.height));
    }

    @Override
    public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      getKeyValueLabel().setEnabled(enabled);
      getDisplayTextLabel().setEnabled(enabled);
    }

    @Override
    public void setForeground(Color fg) {
      getKeyValueLabel().setForeground(fg);
      getDisplayTextLabel().setForeground(fg);
    }

    @Override
    public void setFont(Font font) {
      super.setFont(font);
      getKeyValueLabel().setFont(font);
      getDisplayTextLabel().setFont(font);
    }

    @Override
    public void setComponentOrientation(ComponentOrientation o) {
      super.setComponentOrientation(o);
      getKeyValueLabel().setComponentOrientation(o);
      getDisplayTextLabel().setComponentOrientation(o);
    }

    /**
     * Always return insets as [2, 2, 2, 2]
     */
    @Override
    public Insets getInsets() {
      return new Insets(2, 2, 2, 2);
    }

    /**
     * Always return insets as [2, 2, 2, 2]
     */
    @Override
    public Insets getInsets(Insets insets) {
      return new Insets(2, 2, 2, 2);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean selected, boolean cellHasFocus) {
      updateCellContentLabel((ValuePickEntry) value);
      Color bg = null;
      Color fg = null;
      JList.DropLocation dropLocation = list.getDropLocation();
      if (dropLocation != null
              && !dropLocation.isInsert()
              && dropLocation.getIndex() == index) {
        bg = UIManager.getColor("List.dropCellBackground");
        fg = UIManager.getColor("List.dropCellForeground");
        selected = true;
      }
      if (selected) {
        setBackground(bg == null ? list.getSelectionBackground() : bg);
        setForeground(fg == null ? list.getSelectionForeground() : fg);
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }
      setEnabled(list.isEnabled());
      setFont(list.getFont());
      Border border = null;
      if (cellHasFocus) {
        if (selected) {
          border = UIManager.getBorder("List.focusSelectedCellHighlightBorder");
        }
        if (border == null) {
          border = UIManager.getBorder("List.focusCellHighlightBorder");
        }
      } else {
        border = UIManager.getBorder("List.cellNoFocusBorder");
        if (border == null) {
          border = NO_FOCUS_BORDER;
        }
      }
      setBorder(border);
      setComponentOrientation(list.getComponentOrientation());
      return this;
    }

    private void updateCellContentLabel(ValuePickEntry entry) {
      Object key = entry == null ? null : entry.getKey();
      String text = entry == null ? "" : entry.getText();
      getKeyValueLabel().setText(key == null ? "" : key.toString());
      getDisplayTextLabel().setText(text == null ? "" : text);
    }

    @Override
    public boolean isOpaque() {
      Color back = getBackground();
      Component p = getParent();
      if (p != null) {
        p = p.getParent();
      }
      // p should now be the JList.
      boolean colorMatch = (back != null) && (p != null)
              && back.equals(p.getBackground())
              && p.isOpaque();
      return !colorMatch && super.isOpaque();
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public boolean isValidateRoot() {
      return true;
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
    protected void firePropertyChange(String propertyName,
            Object oldValue, Object newValue) {
      // Strings get interned...
      if (("text".equals(propertyName) || ("font".equals(propertyName)
              || "foreground".equals(propertyName)) && oldValue != newValue
              && getClientProperty(BasicHTML.propertyKey) != null)) {
        super.firePropertyChange(propertyName, oldValue, newValue);
      }
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void firePropertyChange(String propertyName,
            byte oldValue, byte newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void firePropertyChange(String propertyName,
            char oldValue, char newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void firePropertyChange(String propertyName,
            short oldValue, short newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void firePropertyChange(String propertyName,
            int oldValue, int newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void firePropertyChange(String propertyName,
            long oldValue, long newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void firePropertyChange(String propertyName,
            float oldValue, float newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void firePropertyChange(String propertyName,
            double oldValue, double newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void firePropertyChange(String propertyName,
            boolean oldValue, boolean newValue) {
    }
  }
}