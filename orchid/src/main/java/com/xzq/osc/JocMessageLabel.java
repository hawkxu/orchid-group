/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import com.xzq.osc.resource.Resource;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * A label to display message with icon and text,
 *
 * @author zqxu
 */
public class JocMessageLabel extends JLabel implements OrchidAboutIntf {

  private static final Icon defaultIdleIcon = new JocBusyIcon();
  private static final Icon defaultBusyIcon = new JocBusyIcon(true);
  private static final Icon defaultOKIcon = Resource.getOrchidIcon("info_ok.png");
  private static final Icon defaultWarnIcon = Resource.getOrchidIcon("info_warn.png");
  private static final Icon defaultErrorIcon = Resource.getOrchidIcon("info_error.png");

  /**
   * Message type enumeration
   */
  public static enum Type {

    /**
     * Idle or normal message
     */
    MT_IDLE,
    /**
     * Busy message
     */
    MT_BUSY,
    /**
     * OK message
     */
    MT_OK,
    /**
     * Warning message
     */
    MT_WARNING,
    /**
     * error message
     */
    MT_ERROR;
  }
  private Timer timer;
  private Map<Type, Icon> iconMap;
  private Map<Type, Color> foregroundMap;
  private Type messageType;

  /**
   * Constructor of JocMessageLabel with default settings.
   */
  public JocMessageLabel() {
    super();
    iconMap = new EnumMap<Type, Icon>(Type.class);
    iconMap.put(Type.MT_IDLE, defaultIdleIcon);
    iconMap.put(Type.MT_BUSY, defaultBusyIcon);
    iconMap.put(Type.MT_OK, defaultOKIcon);
    iconMap.put(Type.MT_WARNING, defaultWarnIcon);
    iconMap.put(Type.MT_ERROR, defaultErrorIcon);
    foregroundMap = new EnumMap<Type, Color>(Type.class);
    foregroundMap.put(Type.MT_IDLE, Color.BLACK);
    foregroundMap.put(Type.MT_BUSY, Color.BLUE);
    foregroundMap.put(Type.MT_OK, new Color(0, 150, 0));
    foregroundMap.put(Type.MT_WARNING, new Color(250, 120, 0));
    foregroundMap.put(Type.MT_ERROR, Color.RED);
    setIcon(defaultIdleIcon);
    initMessageTimer(15000);
  }

  // init clearing timer
  private void initMessageTimer(int timeout) {
    timer = new Timer(timeout, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clearMessage();
      }
    });
    timer.setRepeats(false);
  }

  /**
   * Returns idle state icon, defaults is a JocBusyIcon with idle state.
   *
   * @return idle state icon
   */
  public Icon getIdleIcon() {
    return iconMap.get(Type.MT_IDLE);
  }

  /**
   * Sets idle state icon
   *
   * @param idleIcon idle state icon
   */
  public void setIdleIcon(Icon idleIcon) {
    Icon old = getIdleIcon();
    if (!OrchidUtils.equals(old, idleIcon)) {
      iconMap.put(Type.MT_IDLE, idleIcon);
      firePropertyChange("idleIcon", old, idleIcon);
    }
  }

  /**
   * Return busy state icon, defaults is a JocBusy icon with busy state.
   *
   * @return busy state icon
   */
  public Icon getBusyIcon() {
    return iconMap.get(Type.MT_BUSY);
  }

  /**
   * Sets busy state icon.
   *
   * @param busyIcon busy state icon.
   */
  public void setBusyIcon(Icon busyIcon) {
    Icon old = getBusyIcon();
    if (!OrchidUtils.equals(old, busyIcon)) {
      iconMap.put(Type.MT_BUSY, busyIcon);
      firePropertyChange("busyIcon", old, busyIcon);
    }
  }

  /**
   * Returns OK state icon, defaults is an icon with a tick.
   *
   * @return OK state icon.
   */
  public Icon getOkIcon() {
    return iconMap.get(Type.MT_OK);
  }

  /**
   * Sets OK state icon.
   *
   * @param okIcon OK state icon
   */
  public void setOkIcon(Icon okIcon) {
    Icon old = getOkIcon();
    if (!OrchidUtils.equals(old, okIcon)) {
      iconMap.put(Type.MT_OK, okIcon);
      firePropertyChange("okIcon", old, okIcon);
    }
  }

  /**
   * Returns warning state icon, defaults is an icon with yellow exclamation
   * mark.
   *
   * @return warning state icon.
   */
  public Icon getWarningIcon() {
    return iconMap.get(Type.MT_WARNING);
  }

  /**
   * Sets warning state icon.
   *
   * @param warningIcon warning state icon.
   */
  public void setWarningIcon(Icon warningIcon) {
    Icon old = getWarningIcon();
    if (!OrchidUtils.equals(old, warningIcon)) {
      iconMap.put(Type.MT_WARNING, warningIcon);
      firePropertyChange("warningIcon", old, warningIcon);
    }
  }

  /**
   * Returns error state icon, defaults is an icon with read exclamation mark.
   *
   * @return error state icon.
   */
  public Icon getErrorIcon() {
    return iconMap.get(Type.MT_ERROR);
  }

  /**
   * Sets error state icon.
   *
   * @param errorIcon error state icon.
   */
  public void setErrorIcon(Icon errorIcon) {
    Icon old = getErrorIcon();
    if (!OrchidUtils.equals(old, errorIcon)) {
      iconMap.put(Type.MT_ERROR, errorIcon);
      firePropertyChange("errorIcon", old, errorIcon);
    }
  }

  /**
   * Returns foreground color for idle message.
   *
   * @return foreground color for idle message.
   */
  public Color getIdleForeground() {
    return foregroundMap.get(Type.MT_IDLE);
  }

  /**
   * Sets foreground color for idle message.
   *
   * @param idleForeground foreground color for idle message.
   */
  public void setIdleForeground(Color idleForeground) {
    Color old = getIdleForeground();
    if (!OrchidUtils.equals(old, idleForeground)) {
      foregroundMap.put(Type.MT_IDLE, idleForeground);
      firePropertyChange("idleForeground", old, idleForeground);
    }
  }

  /**
   * Returns foreground color for busy message.
   *
   * @return foreground color for busy message.
   */
  public Color getBusyForeground() {
    return foregroundMap.get(Type.MT_BUSY);
  }

  /**
   * Sets foreground color for busy message.
   *
   * @param busyForeground foreground color for busy message.
   */
  public void setBusyForeground(Color busyForeground) {
    Color old = getIdleForeground();
    if (!OrchidUtils.equals(old, busyForeground)) {
      foregroundMap.put(Type.MT_BUSY, busyForeground);
      firePropertyChange("busyForeground", old, busyForeground);
    }
  }

  /**
   * Returns foreground color for OK message.
   *
   * @return foreground color for OK message.
   */
  public Color getOkForeground() {
    return foregroundMap.get(Type.MT_OK);
  }

  /**
   * Sets foreground color for OK message.
   *
   * @param okForeground foreground color for OK message.
   */
  public void setOkForeground(Color okForeground) {
    Color old = getIdleForeground();
    if (!OrchidUtils.equals(old, okForeground)) {
      foregroundMap.put(Type.MT_OK, okForeground);
      firePropertyChange("okForeground", old, okForeground);
    }
  }

  /**
   * Returns foreground color for warning message.
   *
   * @return foreground color for warning message.
   */
  public Color getWarningForeground() {
    return foregroundMap.get(Type.MT_WARNING);
  }

  /**
   * Sets foreground color for warning message.
   *
   * @param warningForeground foreground color for warning message.
   */
  public void setWarningForeground(Color warningForeground) {
    Color old = getIdleForeground();
    if (!OrchidUtils.equals(old, warningForeground)) {
      foregroundMap.put(Type.MT_WARNING, warningForeground);
      firePropertyChange("warningForeground", old, warningForeground);
    }
  }

  /**
   * Returns foreground color for error message.
   *
   * @return foreground color for error message.
   */
  public Color getErrorForeground() {
    return foregroundMap.get(Type.MT_ERROR);
  }

  /**
   * Sets foreground color for error message.
   *
   * @param errorForeground foreground color for error message.
   */
  public void setErrorForeground(Color errorForeground) {
    Color old = getIdleForeground();
    if (!OrchidUtils.equals(old, errorForeground)) {
      foregroundMap.put(Type.MT_ERROR, errorForeground);
      firePropertyChange("errorForeground", old, errorForeground);
    }
  }

  /**
   * Returns message clearing delay time in millisecond, if this value is zero,
   * then message not be clear, defaults is 15000.
   *
   * @return message clearing delay time.
   */
  public int getTimeout() {
    return timer.getDelay();
  }

  /**
   * Sets message clearing delay time.
   *
   * @param timeout message clearing delay time, use 15000 if less than zero.
   */
  public void setTimeout(int timeout) {
    timeout = timeout < 0 ? 15000 : timeout;
    timer.setDelay(timeout);
    timer.setInitialDelay(timeout);
  }

  /**
   * Sets message text with message type MT_IDLE
   *
   * @param message message text
   * @see #setMessage(java.lang.String, com.xzq.osc.JocMessageLabel.Type)
   */
  public void idleMessage(String message) {
    setMessage(message, Type.MT_IDLE);
  }

  /**
   * Sets message text with message type MT_BUSY
   *
   * @param message message text
   * @see #setMessage(java.lang.String, com.xzq.osc.JocMessageLabel.Type)
   */
  public void busyMessage(String message) {
    setMessage(message, Type.MT_BUSY);
  }

  /**
   * Sets message text with message type MT_OK
   *
   * @param message message text
   * @see #setMessage(java.lang.String, com.xzq.osc.JocMessageLabel.Type)
   */
  public void okMessage(String message) {
    setMessage(message, Type.MT_OK);
  }

  /**
   * Sets message text with message type MT_WARNING
   *
   * @param message message text
   * @see #setMessage(java.lang.String, com.xzq.osc.JocMessageLabel.Type)
   */
  public void warningMessage(String message) {
    setMessage(message, Type.MT_WARNING);
  }

  /**
   * Sets message text with message type MT_ERROR
   *
   * @param message message text
   * @see #setMessage(java.lang.String, com.xzq.osc.JocMessageLabel.Type)
   */
  public void errorMessage(String message) {
    setMessage(message, Type.MT_ERROR);
  }

  /**
   * Sets message with text and type, immediately update component to display
   * message, this function is not thread safe.
   *
   * @param message message text
   * @param type message type
   */
  public void setMessage(String message, Type type) {
    if (type != getMessageType()) {
      setIcon(iconMap.get(type));
      setForeground(foregroundMap.get(type));
      messageType = type;
    }
    setText(message == null ? "" : message);
    if (type == Type.MT_BUSY) {
      timer.stop();
    } else if (getTimeout() > 0) {
      timer.restart();
    }
  }

  /**
   * Returns last message type.
   *
   * @return last message type.
   */
  public Type getMessageType() {
    return messageType;
  }

  /**
   * Immediately clear message and set state to <b>Type.MT_IDLE</b>. This
   * function is not thread safe.
   */
  public void clearMessage() {
    timer.stop();
    setMessage("", Type.MT_IDLE);
  }

  /**
   * Returns about box dialog
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
}
