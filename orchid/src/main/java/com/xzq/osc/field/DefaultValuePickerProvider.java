/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import com.xzq.osc.JocValueField;
import com.xzq.osc.OrchidUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * Value picker provider support cache and find value picker by value picker key
 * or value class. use put method to cache value picker, then the provider
 * search value picker for JocValueField component, first by value picker key of
 * the component then value class of the component.
 *
 * @author zqxu
 */
public class DefaultValuePickerProvider implements ValuePickerProvider {

  private Map<Object, ValuePicker> pickerMap;

  public DefaultValuePickerProvider() {
    pickerMap = new HashMap<Object, ValuePicker>();
  }

  /**
   * Returns the value picker for the key, or null if no value picker for the
   * key.
   *
   * @param key key for value picker.
   * @return value picker
   */
  public ValuePicker getKeyValuePicker(String key) {
    return pickerMap.get(key);
  }

  /**
   * Return the value picker for the value class vClass, or null if no value
   * picker for the vClass.
   *
   * @param vClass value class
   * @return value picker.
   */
  public ValuePicker getClassValuePicker(Class vClass) {
    return pickerMap.get(vClass);
  }

  /**
   * Sets value picker for the key, if a value picker already exist, the new
   * value picker will replace the old one.
   *
   * @param key value picker key
   * @param valuePicker the new value picker
   */
  public void putKeyValuePicker(String key, ValuePicker valuePicker) {
    pickerMap.put(key, valuePicker);
  }

  /**
   * Sets value picker for the value class vClass, if a value picker already
   * exist, the new value picker will replace the old one.
   *
   * @param vClass value class
   * @param valuePicker the new value picker.
   */
  public void putClassValuePicker(Class vClass, ValuePicker valuePicker) {
    pickerMap.put(vClass, valuePicker);
  }

  /**
   * Remove value picker for the key, if no value picker for the key, do
   * nothing.
   *
   * @param key value picker key.
   */
  public void removeKeyValuePicker(String key) {
    pickerMap.remove(key);
  }

  /**
   * Remove value picker for the value class vClass, if no value picker for the
   * vClass, do nothing.
   *
   * @param vClass value class.
   */
  public void removeClassValuePicker(Class vClass) {
    pickerMap.remove(vClass);
  }

  /**
   * Returns value picker for the field, first search by value picker key of the
   * field, then search by value class of the field, if no value picker found,
   * return null.
   *
   * @param field value field component.
   * @return value picker.
   */
  @Override
  public ValuePicker findValuePicker(JocValueField field) {
    ValuePicker valuePicker = null;
    String key = field.getValuePickerKey();
    if (!OrchidUtils.isEmpty(key)) {
      valuePicker = pickerMap.get(key);
    }
    if (valuePicker == null
            && !(field.getModel() instanceof TimeValueModel)) {
      valuePicker = pickerMap.get(field.getValueClass());
    }
    return valuePicker;
  }
}
