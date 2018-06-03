/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import java.beans.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copy from SwingX package.
 *
 * @author rbair, Jan Stola
 */
public abstract class BeanInfoSupport extends SimpleBeanInfo {

  private static final Logger LOG = Logger.getLogger(BeanInfoSupport.class.getName());
  /**
   * Indicates whether I am introspecting state for the give class. This helps
   * prevent infinite loops
   */
  private static Map<Class<?>, Boolean> introspectingState = new HashMap<Class<?>, Boolean>();
  /**
   * The class of the bean that this BeanInfoSupport is for
   */
  protected Class<?> beanClass;
  /**
   * @see BeanInfo
   */
  private int defaultPropertyIndex = -1;
  /**
   * @see BeanInfo
   */
  private int defaultEventIndex = -1;
  private BeanDescriptor beanDescriptor;
  private Map<String, PropertyDescriptor> properties = new TreeMap<String, PropertyDescriptor>();
  private Map<String, EventSetDescriptor> events = new TreeMap<String, EventSetDescriptor>();
  private Map<String, MethodDescriptor> methods = new TreeMap<String, MethodDescriptor>();

  /**
   * Creates a new instance of BeanInfoSupport.
   *
   * @param beanClass class of the bean.
   */
  public BeanInfoSupport(Class<?> beanClass) {
    this.beanClass = beanClass;
    if (!isIntrospecting()) {
      introspectingState.put(beanClass, Boolean.TRUE);
      try {
        Class<?> superClass = beanClass.getSuperclass();
        while (superClass != null) {
          Introspector.flushFromCaches(superClass);
          superClass = superClass.getSuperclass();
        }
        BeanInfo info = Introspector.getBeanInfo(beanClass);
        beanDescriptor = info.getBeanDescriptor();
        if (beanDescriptor != null) {
          Class<?> customizerClass = getCustomizerClass();
          beanDescriptor = new BeanDescriptor(beanDescriptor.getBeanClass(),
                  customizerClass == null ? beanDescriptor.getCustomizerClass()
                  : customizerClass);
        } else {
          beanDescriptor = new BeanDescriptor(beanClass, getCustomizerClass());
        }
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
          properties.put(pd.getName(), pd);
        }
        for (EventSetDescriptor esd : info.getEventSetDescriptors()) {
          events.put(esd.getName(), esd);
        }
        for (MethodDescriptor md : info.getMethodDescriptors()) {
          methods.put(md.getName(), md);
        }

        defaultPropertyIndex = info.getDefaultPropertyIndex();
        defaultEventIndex = info.getDefaultEventIndex();
      } catch (Exception ex) {
        LOG.log(Level.SEVERE, "Error in initialize.", ex);
      }
      initialize();
      introspectingState.put(beanClass, Boolean.FALSE);
    }
  }

  private boolean isIntrospecting() {
    Boolean b = introspectingState.get(beanClass);
    return b == null ? false : b.booleanValue();
  }

  /**
   * Called by the constructor during the proper time so that subclasses can
   * override the settings/values for the various beaninfo properties. For
   * example, you could call setDisplayName("Foo Name", "foo") to change the foo
   * properties display name
   */
  protected abstract void initialize();

  /**
   * Override this method if you want to return a custom customizer class for
   * the bean
   *
   * @return <code>null</code>.
   */
  protected Class<?> getCustomizerClass() {
    return null;
  }

  protected PropertyDescriptor getProperty(String propertyName) {
    return properties.get(propertyName);
  }

  /**
   * Changes the display name of the given named property. Property names are
   * always listed last to allow for varargs
   *
   * @param displayName display name of the property.
   * @param propertyName name of the property.
   */
  protected void setDisplayName(String displayName, String propertyName) {
    PropertyDescriptor pd = properties.get(propertyName);
    if (pd != null) {
      pd.setDisplayName(displayName);
    } else {
      LOG.log(Level.WARNING, "Failed to set display name for "
              + "property {0}. No such property was found", propertyName);
    }
  }

  /**
   * Sets the given named properties to be "hidden".
   *
   * @param hidden determines whether the properties should be marked as hidden
   * or not.
   * @param propertyNames name of properties.
   * @see PropertyDescriptor
   */
  protected void setHidden(boolean hidden, String... propertyNames) {
    for (String propertyName : propertyNames) {
      PropertyDescriptor pd = properties.get(propertyName);
      if (pd != null) {
        pd.setHidden(hidden);
      } else {
        LOG.log(Level.WARNING, "Failed to set hidden attribute for "
                + "property {0}. No such property was found", propertyName);
      }
    }
  }

  protected void setExpert(boolean expert, String... propertyNames) {
    for (String propertyName : propertyNames) {
      PropertyDescriptor pd = properties.get(propertyName);
      if (pd != null) {
        pd.setExpert(expert);
      } else {
        LOG.log(Level.WARNING, "Failed to set expert attribute for "
                + "property {0}. No such property was found", propertyName);
      }
    }
  }

  protected void setPreferred(boolean preferred, String... propertyNames) {
    for (String propertyName : propertyNames) {
      PropertyDescriptor pd = properties.get(propertyName);
      if (pd != null) {
        pd.setPreferred(preferred);
      } else {
        LOG.log(Level.WARNING, "Failed to set preferred attribute for "
                + "property {0}. No such property was found", propertyName);
      }
    }
  }

  protected void setBound(boolean bound, String... propertyNames) {
    for (String propertyName : propertyNames) {
      PropertyDescriptor pd = properties.get(propertyName);
      if (pd != null) {
        pd.setBound(bound);
      } else {
        LOG.log(Level.WARNING, "Failed to set bound attribute for "
                + "property {0}. No such property was found", propertyName);
      }
    }
  }

  protected void setConstrained(boolean constrained, String... propertyNames) {
    for (String propertyName : propertyNames) {
      PropertyDescriptor pd = properties.get(propertyName);
      if (pd != null) {
        pd.setConstrained(constrained);
      } else {
        LOG.log(Level.WARNING, "Failed to set constrained attribute for "
                + "property {0}. No such property was found", propertyName);
      }
    }
  }

  protected void setCategory(String categoryName, String... propertyNames) {
    for (String propertyName : propertyNames) {
      PropertyDescriptor pd = properties.get(propertyName);
      if (pd != null) {
        pd.setValue("category", categoryName);
      } else {
        LOG.log(Level.WARNING, "Failed to set category for property '{0}"
                + "'. No such property was found", propertyName);
      }
    }
  }

  protected void setPropertyEditor(Class<?> editorClass, String... propertyNames) {
    for (String propertyName : propertyNames) {
      PropertyDescriptor pd = properties.get(propertyName);
      if (pd != null) {
        pd.setPropertyEditorClass(editorClass);
      } else {
        LOG.log(Level.WARNING, "Failed to set property editor for "
                + "property {0}. No such property was found", propertyName);
      }
    }
  }

  protected void setEnumerationValues(EnumerationValue[] values,
          String... propertyNames) {
    if (values == null) {
      return;
    }

    Object[] enumValues = new Object[values.length * 3];
    int index = 0;
    for (EnumerationValue ev : values) {
      enumValues[index++] = ev.getName();
      enumValues[index++] = ev.getValue();
      enumValues[index++] = ev.getJavaInitializationString();
    }

    for (String propertyName : propertyNames) {
      PropertyDescriptor pd = properties.get(propertyName);
      if (pd != null) {
        pd.setValue("enumerationValues", enumValues);
      } else {
        LOG.log(Level.WARNING, "Failed to set enumeration values for "
                + "property {0}. No such property was found", propertyName);
      }
    }
  }

  //----------------------------------------------------- BeanInfo methods
  /**
   * Gets the bean's
   * <code>BeanDescriptor</code>s.
   *
   * @return BeanDescriptor describing the editable properties of this bean. May
   * return null if the information should be obtained by automatic analysis.
   */
  @Override
  public BeanDescriptor getBeanDescriptor() {
    return isIntrospecting() ? null : beanDescriptor;
  }

  /**
   * Gets the bean's
   * <code>PropertyDescriptor</code>s.
   *
   * @return An array of PropertyDescriptors describing the editable properties
   * supported by this bean. May return null if the information should be
   * obtained by automatic analysis. <p> If a property is indexed, then its
   * entry in the result array will belong to the IndexedPropertyDescriptor
   * subclass of PropertyDescriptor. A client of getPropertyDescriptors can use
   * "instanceof" to check if a given PropertyDescriptor is an
   * IndexedPropertyDescriptor.
   */
  @Override
  public PropertyDescriptor[] getPropertyDescriptors() {
    return isIntrospecting()
            ? null
            : properties.values().toArray(new PropertyDescriptor[0]);
  }

  /**
   * Gets the bean's
   * <code>EventSetDescriptor</code>s.
   *
   * @return An array of EventSetDescriptors describing the kinds of events
   * fired by this bean. May return null if the information should be obtained
   * by automatic analysis.
   */
  @Override
  public EventSetDescriptor[] getEventSetDescriptors() {
    return isIntrospecting()
            ? null
            : events.values().toArray(new EventSetDescriptor[0]);
  }

  /**
   * Gets the bean's
   * <code>MethodDescriptor</code>s.
   *
   * @return An array of MethodDescriptors describing the methods implemented by
   * this bean. May return null if the information should be obtained by
   * automatic analysis.
   */
  @Override
  public MethodDescriptor[] getMethodDescriptors() {
    return isIntrospecting()
            ? null
            : methods.values().toArray(new MethodDescriptor[0]);
  }

  /**
   * A bean may have a "default" property that is the property that will mostly
   * commonly be initially chosen for update by human's who are customizing the
   * bean.
   *
   * @return Index of default property in the PropertyDescriptor array returned
   * by getPropertyDescriptors. <P>	Returns -1 if there is no default property.
   */
  @Override
  public int getDefaultPropertyIndex() {
    return isIntrospecting() ? -1 : defaultPropertyIndex;
  }

  /**
   * A bean may have a "default" event that is the event that will mostly
   * commonly be used by human's when using the bean.
   *
   * @return Index of default event in the EventSetDescriptor array returned by
   * getEventSetDescriptors. <P>	Returns -1 if there is no default event.
   */
  @Override
  public int getDefaultEventIndex() {
    return isIntrospecting() ? -1 : defaultEventIndex;
  }
}
