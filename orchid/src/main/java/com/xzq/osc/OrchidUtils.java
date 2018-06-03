package com.xzq.osc;

import com.xzq.osc.field.Range;
import com.xzq.osc.field.Range.Sign;
import com.xzq.osc.field.RangeList;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.*;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class OrchidUtils implements SwingConstants {

  /**
   * Check whether the two objects is equivalent, return true if two objects are
   * equal and false for other side. Two objects with both null was considered
   * as equivalent. Use <b>equals</b> for object compare.
   *
   * @param o one object
   * @param p another object
   * @return true if two objects are equal and false for other side.
   */
  public static boolean equals(Object o, Object p) {
    return o == p || (o != null && o.equals(p));
  }

  // <editor-fold defaultstate="collapsed" desc="String Relative Functions...">
  /**
   * Check whether the value is empty. return true if object was null, or an
   * empty string, zero length array, empty collection, empty map, any else
   * return false.
   *
   * @param value value to check
   * @return true if object was empty or false else.
   */
  public static boolean isEmpty(Object value) {
    return value == null
            || (value instanceof String && ((String) value).isEmpty())
            || (value.getClass().isArray() && Array.getLength(value) == 0)
            || (value instanceof Collection && ((Collection) value).isEmpty())
            || (value instanceof Map && ((Map) value).isEmpty());
  }

  /**
   * Returns a string contains duplicate char for specified times.
   *
   * @param c the char for repeat
   * @param times repeat count
   * @return a string
   */
  public static String repeatChar(char c, int times) {
    char[] charArr = new char[times];
    Arrays.fill(charArr, c);
    return String.valueOf(charArr);
  }

  /**
   * Returns a string contains repeatedly specified string.
   *
   * @param s the string for repeast
   * @param times repeat count
   * @return a string
   */
  public static String repeatString(String s, int times) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < times; i++) {
      sb.append(s);
    }
    return sb.toString();
  }

  /**
   * Returns a string remove specified character at leading and trailer.
   *
   * @param s source string
   * @param trimChar the character want to remove
   * @return a string
   */
  public static String trim(String s, char trimChar) {
    return trimRight(trimLeft(s, trimChar), trimChar);
  }

  /**
   * Returns a string remove specified character at leading
   *
   * @param s source string
   * @param trimChar the character want to remove
   * @return a string
   */
  public static String trimLeft(String s, char trimChar) {
    int i = 0;
    for (; i < s.length(); i++) {
      if (s.charAt(i) != trimChar) {
        break;
      }
    }
    return i == s.length() ? "" : s.substring(i);
  }

  /**
   * Returns a string remove specified character at trailer
   *
   * @param s source string
   * @param trimChar the character want to remove
   * @return a string
   */
  public static String trimRight(String s, char trimChar) {
    int i = s.length() - 1;
    for (; i > -1; i--) {
      if (s.charAt(i) != trimChar) {
        break;
      }
    }
    return i == -1 ? "" : s.substring(0, i + 1);
  }

  /**
   * if length of source string less than targetLen, Returns a new string left
   * side filling with fillChar from source string, otherwise returns source
   * string.
   *
   * @param s source string
   * @param targetLen target length
   * @param fillChar char to fill in left side.
   * @return String.
   */
  public static String fillLeft(String s, int targetLen, char fillChar) {
    if (s.length() >= targetLen) {
      return s;
    } else {
      return repeatChar(fillChar, targetLen - s.length()) + s;
    }
  }

  /**
   * if length of source string less than targetLen, Returns a new string right
   * side filling with fillChar from source string, otherwise returns source
   * string.
   *
   * @param s source string
   * @param targetLen target length
   * @param fillChar char to fill in right side.
   * @return String.
   */
  public static String fillRight(String s, int targetLen, char fillChar) {
    if (s.length() >= targetLen) {
      return s;
    } else {
      return s + repeatChar(fillChar, targetLen - s.length());
    }
  }

  /**
   * try convert string to URI object, if string is not a valid URI statement,
   * then returns null, no exception throws.
   *
   * @param text String contains URI statement.
   * @return URI object or null.
   */
  public static URI tryStringToURI(String text) {
    try {
      return new URI(text);
    } catch (URISyntaxException ex) {
      return null;
    }
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="File Relative Functions">
  /**
   * Return the root path of running java application.
   *
   * @param clApp Any class of running application self package.
   * @return File object of root path
   */
  public static File getAppRoot(Class clApp) {
    String root = clApp.getProtectionDomain().getCodeSource().
            getLocation().getPath();
    File fRoot;
    try {
      fRoot = new File(URLDecoder.decode(root, "UTF-8"));
    } catch (UnsupportedEncodingException ex) {
      fRoot = new File(root);   // exception is impossible
    }
    return fRoot.isDirectory() ? fRoot : fRoot.getParentFile();
  }

  /**
   * Delete file object from underly system (and all its sub-files if file is a
   * directory).
   *
   * @param file file or directory
   * @return true if file and all its sub-files are removed, otherwise return
   * false. if file is null or file not exists, return true.
   */
  public static boolean recursiveDeleteFile(File file) {
    if (file == null || !file.exists()) {
      return true;
    }
    File[] fileArray = null;
    if (file.isDirectory()) {
      fileArray = file.listFiles();
    }
    if (fileArray != null) {
      for (File sub : fileArray) {
        if (!recursiveDeleteFile(sub)) {
          return false;
        }
      }
    }
    return file.delete();
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="GUI Relative Functions...">
  /**
   * Show popup menu at lower edge of the invoker component, if no enough screen
   * area for display then auto adjust popup menu location.
   *
   * @param popup popup menu
   * @param invoker invoker component
   */
  public static void showPopupMenu(JPopupMenu popup, Component invoker) {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screen = toolkit.getScreenSize();
    Dimension popSize = popup.getPreferredSize();
    int y = invoker.getHeight();
    if (y + invoker.getLocationOnScreen().y + popSize.height > screen.height) {
      y = -popup.getPreferredSize().height;
    }
    popup.show(invoker, 0, y);
  }

  /**
   * combine two insets, if insets1 and inset2 both be null, then return an
   * insets [0, 0, 0, 0].
   *
   * @param insets1 insets 1.
   * @param insets2 insets 2.
   * @return insets.
   */
  public static Insets combineInsets(Insets insets1, Insets insets2) {
    Insets out = new Insets(0, 0, 0, 0);
    if (insets1 != null) {
      addInsets(out, insets1, 1);
    }
    if (insets2 != null) {
      addInsets(out, insets2, 1);
    }
    return out;
  }

  /**
   * subtract insets minuend from insets source.
   *
   * @param source source insets.
   * @param minuend minuend insets.
   * @return insets.
   */
  public static Insets subtractInsets(Insets source, Insets minuend) {
    Insets out = new Insets(source.top, source.left, source.bottom,
            source.right);
    if (minuend != null) {
      addInsets(out, minuend, -1);
    }
    return out;
  }

  private static void addInsets(Insets src, Insets addend, int factor) {
    src.left += factor * addend.left;
    src.top += factor * addend.top;
    src.bottom += factor * addend.bottom;
    src.right += factor * addend.right;
  }

  /**
   * include margin to rectangle.
   *
   * @param rect rectangle.
   * @param margin margin.
   */
  public static void includeMargin(Rectangle rect, Insets margin) {
    if (margin != null) {
      rect.translate(-margin.left, -margin.top);
      rect.width += margin.left + margin.right;
      rect.height += margin.top + margin.bottom;
    }
  }

  /**
   * exclude margin to rectangle.
   *
   * @param rect rectangle.
   * @param margin margin.
   */
  public static void excludeMargin(Rectangle rect, Insets margin) {
    if (margin != null) {
      rect.translate(margin.left, margin.top);
      rect.width -= margin.left + margin.right;
      rect.height -= margin.top + margin.bottom;
    }
  }

  /**
   * Returns current text contents in clipboard, null if currently no valid
   * contents in clipboard.
   *
   * @return a string
   */
  public static String getClipboardString() {
    try {
      Transferable transfer = Toolkit.getDefaultToolkit().
              getSystemClipboard().getContents(null);
      if (transfer != null) {
        if (transfer.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          return (String) transfer.getTransferData(DataFlavor.stringFlavor);
        }
      }
    } catch (Exception ex) {
    }
    return null;
  }

  /**
   * If c is null or c self instance of <b>Window</b>, return c itself, else
   * return the first <b>Window</b> ancestor of <b>c</b> or null if <b>c</b> is
   * not contained inside a <b>Window</b>.
   *
   * @param c component to determine window of.
   * @return window
   */
  public static Window getWindowOf(Component c) {
    if (c == null || c instanceof Window) {
      return (Window) c;
    } else {
      return SwingUtilities.getWindowAncestor(c);
    }
  }

  /**
   * Verify that horizontalKey is a legal value for the horizontal alignment
   * properties.
   *
   * @param horizontalKey the property value to check
   * @param propertyName the property name
   * @exception IllegalArgumentException if key isn't LEFT, CENTER, RIGHT,
   * LEADING or TRAILING.
   */
  public static int checkHorizontalKey(int horizontalKey,
          String propertyName) {
    if ((horizontalKey == LEFT) || (horizontalKey == CENTER)
            || (horizontalKey == RIGHT) || (horizontalKey == LEADING)
            || (horizontalKey == TRAILING)) {
      return horizontalKey;
    } else {
      throw new IllegalArgumentException(propertyName);
    }
  }

  /**
   * Verify that verticalKey is a legal value for the vertical alignment
   * properties.
   *
   * @param verticalKey the property value to check
   * @param propertyName the property name
   * @exception IllegalArgumentException if verticalKey isn't TOP, CENTER, or
   * BOTTOM.
   */
  public static int checkVerticalKey(int verticalKey, String propertyName) {
    if ((verticalKey == TOP) || (verticalKey == CENTER)
            || (verticalKey == BOTTOM)) {
      return verticalKey;
    } else {
      throw new IllegalArgumentException(propertyName);
    }
  }

  /**
   * translate horizontal alignment key to match component orientation. for
   * LEADING and TRAILNG key, if component orientation is left to right, returns
   * LEFT and RIGHT, otherwise returns RIGHT and LEFT, for other keys, returns
   * the key itself.
   *
   * @param c component
   * @param horizontal horizontal key alignment.
   * @return translated horizaontal alignment key.
   */
  public static int getOrientedHorizontal(Component c, int horizontal) {
    boolean isLeftToRight = c.getComponentOrientation().isLeftToRight();
    switch (horizontal) {
      case JocLabel.LEADING:
        return isLeftToRight ? LEFT : RIGHT;
      case JocLabel.TRAILING:
        return isLeftToRight ? RIGHT : LEFT;
      default:
        return horizontal;
    }
  }

  /**
   * Return true if the c contains focus or it's sub-c tontains focus.
   *
   * @param c the c
   * @return true or false.
   */
  public static boolean isFocusAncestor(Component c) {
    Component f = KeyboardFocusManager.
            getCurrentKeyboardFocusManager().getFocusOwner();
    return c == f
            || (c instanceof Container && ((Container) c).isAncestorOf(f));
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Date Relative Functions...">
  /**
   * Returns a date object without time.
   *
   * @param date a date object
   * @return a date object without time
   * @exception NullPointerException if date parameter was null.
   */
  public static Date clearTime(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.clear(Calendar.MINUTE);
    calendar.clear(Calendar.SECOND);
    calendar.clear(Calendar.MILLISECOND);
    return calendar.getTime();
  }

  /**
   * Returns a date object without date and milliseconds, that means only time
   * and no milliseconds.
   *
   * @param date a date object
   * @return a date object with only time
   * @exception NullPointerException if date parameter was null
   */
  public static Date clearDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.clear(Calendar.YEAR);
    calendar.clear(Calendar.MONTH);
    calendar.clear(Calendar.DATE);
    calendar.clear(Calendar.MILLISECOND);
    return calendar.getTime();
  }

  /**
   * Returns a date object contains date part of parameter date and time part of
   * parameter time.
   *
   * @param date a date object contains date value
   * @param time a date object contains time value
   * @return a date object
   */
  public static Date combineDateTime(Date date, Date time) {
    Calendar cDate = Calendar.getInstance();
    cDate.setTime(date);
    Calendar cTime = new GregorianCalendar();
    cTime.setTime(time);
    cDate.set(Calendar.HOUR_OF_DAY, cTime.get(Calendar.HOUR_OF_DAY));
    cDate.set(Calendar.MINUTE, cTime.get(Calendar.MINUTE));
    cDate.set(Calendar.SECOND, cTime.get(Calendar.SECOND));
    cDate.set(Calendar.MILLISECOND, cTime.get(Calendar.MILLISECOND));
    return cDate.getTime();
  }

  /**
   * Caculate month count between two date value, return 0 if two date in same
   * month. if date <b>then</b> after date <b>from</b>, return value is
   * positive, else return value is negative.
   *
   * @param from date from
   * @param then date then
   * @return month count between from and then
   */
  public static int monthsBetween(Date from, Date then) {
    Calendar cFrom = Calendar.getInstance();
    cFrom.setTime(from);
    Calendar cThen = new GregorianCalendar();
    cThen.setTime(then);
    return (cThen.get(Calendar.YEAR) - cFrom.get(Calendar.YEAR)) * 12
            + cThen.get(Calendar.MONTH) - cFrom.get(Calendar.MONTH);
  }

  /**
   * Caculate week count between two date value, return 0 if two date in same
   * week, if date <b>then</b> after date <b>from</b>, return value is positive,
   * else return value is negative.
   *
   * @param from date from
   * @param then date then
   * @return week count between from and then
   * @exception NullPointerException if parameter from or then was null
   */
  public static int weeksBetween(Date from, Date then) {
    Calendar cFrom = Calendar.getInstance();
    cFrom.setTime(from);
    cFrom.set(Calendar.DAY_OF_WEEK, cFrom.getFirstDayOfWeek());
    Calendar cThen = new GregorianCalendar();
    cThen.setTime(then);
    cThen.set(Calendar.DAY_OF_WEEK, cThen.getFirstDayOfWeek());
    from = clearTime(cFrom.getTime());
    then = clearTime(cThen.getTime());
    return (int) ((then.getTime() - from.getTime()) / 604800000);
  }

  /**
   * Caculate day count between two date value, return 0 if two date in same
   * day, if date <b>then</b> after date <b>from</b>, return value is positive,
   * else return value is negative.
   *
   * @param from date from
   * @param then date then
   * @return day count between two date value
   * @exception NullPointerException if parameter from or then was null
   */
  public static int daysBetween(Date from, Date then) {
    from = clearTime(from);
    then = clearTime(then);
    return (int) ((then.getTime() - from.getTime()) / 86400000);
  }

  /**
   * Caculate hour count between two date value, return 0 if two date in same
   * day, if date <b>then</b> after date <b>from</b>, return value is positive,
   * else return value is negative.
   *
   * @param from date from
   * @param then date then
   * @return day count between two date value
   * @exception NullPointerException if parameter from or then was null
   */
  public static int hoursBetween(Date from, Date then) {
    Calendar cFrom = Calendar.getInstance();
    cFrom.setTime(from);
    cFrom.clear(Calendar.MINUTE);
    cFrom.clear(Calendar.SECOND);
    cFrom.clear(Calendar.MILLISECOND);
    Calendar cThen = new GregorianCalendar();
    cThen.setTime(then);
    cThen.clear(Calendar.MINUTE);
    cThen.clear(Calendar.SECOND);
    cThen.clear(Calendar.MILLISECOND);
    return (int) ((cThen.getTimeInMillis() - cFrom.getTimeInMillis()) / 3600000);
  }

  /**
   * Caculate minute count between two date value, return 0 if two date in same
   * minute, if date <b>then</b> after date <b>from</b>, return value is
   * positive, else return value is negative.
   *
   * @param from date from
   * @param then date then
   * @return day count between two date value
   * @exception NullPointerException if parameter from or then was null
   */
  public static long minutesBetween(Date from, Date then) {
    Calendar cFrom = Calendar.getInstance();
    cFrom.setTime(from);
    cFrom.clear(Calendar.SECOND);
    cFrom.clear(Calendar.MILLISECOND);
    Calendar cThen = new GregorianCalendar();
    cThen.setTime(then);
    cThen.clear(Calendar.SECOND);
    cThen.clear(Calendar.MILLISECOND);
    return (cThen.getTimeInMillis() - cFrom.getTimeInMillis()) / 60000;
  }

  /**
   * Caculate second count between two date value, return 0 if two date in same
   * second, if date <b>then</b> after date <b>from</b>, return value is
   * positive, else return value is negative.
   *
   * @param from date from
   * @param then date then
   * @return day count between two date value
   * @exception NullPointerException if parameter from or then was null
   */
  public static long secondsBetween(Date from, Date then) {
    Calendar cFrom = Calendar.getInstance();
    cFrom.setTime(from);
    cFrom.clear(Calendar.MILLISECOND);
    Calendar cThen = new GregorianCalendar();
    cThen.setTime(then);
    cThen.clear(Calendar.MILLISECOND);
    return (cThen.getTimeInMillis() - cFrom.getTimeInMillis()) / 1000;
  }

  /**
   * Returns specified part of the date
   *
   * @param date a date object.
   * @param field part for retrieve, for example Calendar.Year
   * @return an int contains part value of the date
   */
  public static int getDateField(Date date, int field) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(field);
  }

  /**
   * Set part of date to specified value, and retun a date object contains
   * changed date value.
   *
   * @param date a date object
   * @param field part of date want to change, for example Calendar.YEAR
   * @param value value of part want to change
   * @return a date object contains changed date value.
   */
  public static Date setDateField(Date date, int field, int value) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(field, value);
    return calendar.getTime();
  }

  /**
   * Add value to field of the date, and retun a date object contains changed
   * date value.
   *
   * @param date date for add value to field.
   * @param field field for add value to.
   * @param value value to add.
   * @return changed date.
   */
  public static Date addDateField(Date date, int field, int value) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(field, value);
    return calendar.getTime();
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Message Box Releative...">
  /**
   * Shortcut to JOptionPane.showMessageDialog with message type
   * JOptionPane.INFORMATION_MESSAGE
   *
   * @see JOptionPane#showMessageDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int)
   */
  public static void msgInfo(Component parent, Object message) {
    String title = OrchidLocale.getString("DEFAULT_INFO_TITLE");
    msgDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Shortcut to JOptionPane.showMessageDialog with message type
   * JOptionPane.INFORMATION_MESSAGE
   *
   * @see JOptionPane#showMessageDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int)
   */
  public static void msgInfo(Component parent, Object message, String title) {
    msgDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Shortcut to JOptionPane.showMessageDialog with message type
   * JOptionPane.WARNING_MESSAGE
   *
   * @see JOptionPane#showMessageDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int)
   */
  public static void msgWarn(Component parent, Object message) {
    String title = OrchidLocale.getString("DEFAULT_WARN_TITLE");
    msgDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
  }

  /**
   * Shortcut to JOptionPane.showMessageDialog with message type
   * JOptionPane.WARNING_MESSAGE
   *
   * @see JOptionPane#showMessageDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int)
   */
  public static void msgWarn(Component parent, Object message, String title) {
    msgDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
  }

  /**
   * Shortcut to JOptionPane.showMessageDialog with message type
   * JOptionPane.ERROR_MESSAGE
   *
   * @see JOptionPane#showMessageDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int)
   */
  public static void msgError(Component parent, Object message) {
    String title = OrchidLocale.getString("DEFAULT_ERROR_TITLE");
    msgDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Shortcut to JOptionPane.showMessageDialog with message type
   * JOptionPane.ERROR_MESSAGE
   *
   * @see JOptionPane#showMessageDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int)
   */
  public static void msgError(Component parent, Object message, String title) {
    msgDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Shortcut to JOptionPane.showMessageDialog
   *
   * @see JOptionPane#showMessageDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int)
   */
  public static void msgDialog(Component parent, Object message,
          String title, int messageType) {
    JOptionPane.showMessageDialog(parent, message, title, messageType);
  }

  /**
   * Shortcut to JOptionPane.showConfirmDialog with option type
   * JOptionPane.OK_CANCEL_OPTION and message type
   * JOptionPane.INFORMATION_MESSAGE
   *
   * @return true for use clicked OK button or flase else.
   */
  public static boolean confirmOK(Component parent, Object message) {
    String title = OrchidLocale.getString("DEFAULT_CHOOSE_TITLE");
    return confirmDialog(parent, message, title, JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_OPTION);
  }

  /**
   * Shortcut to JOptionPane.showConfirmDialog with option type
   * JOptionPane.OK_CANCEL_OPTION and message type
   * JOptionPane.INFORMATION_MESSAGE
   *
   * @return true for use clicked OK button or flase else.
   */
  public static boolean confirmOK(Component parent, Object message,
          String title) {
    return confirmDialog(parent, message, title, JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_OPTION);
  }

  /**
   * Shortcut to JOptionPane.showConfirmDialog with option type
   * JOptionPane.YES_NO_OPTION and message type JOptionPane.INFORMATION_MESSAGE
   *
   * @return true for use clicked Yes button or flase else.
   */
  public static boolean confirmYes(Component parent, Object message) {
    String title = OrchidLocale.getString("DEFAULT_CHOOSE_TITLE");
    return confirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_OPTION);
  }

  /**
   * Shortcut to JOptionPane.showConfirmDialog with option type
   * JOptionPane.YES_NO_OPTION and message type JOptionPane.INFORMATION_MESSAGE
   *
   * @return true for use clicked Yes button or flase else.
   */
  public static boolean confirmYes(Component parent, Object message,
          String title) {
    return confirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_OPTION);
  }

  /**
   * Shortcut to JOptionPane.showConfirmDialog with option type
   * JOptionPane.OK_CANCEL_OPTION and message type JOptionPane.WARNING_MESSAGE
   *
   * @return true for use clicked OK button or flase else.
   */
  public static boolean confirmWarnOK(Component parent, Object message) {
    String title = OrchidLocale.getString("DEFAULT_WARN_TITLE");
    return confirmDialog(parent, message, title, JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
  }

  /**
   * Shortcut to JOptionPane.showConfirmDialog with option type
   * JOptionPane.OK_CANCEL_OPTION and message type JOptionPane.WARNING_MESSAGE
   *
   * @return true for use clicked OK button or flase else.
   */
  public static boolean confirmWarnOK(Component parent, Object message,
          String title) {
    return confirmDialog(parent, message, title, JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
  }

  /**
   * Shortcut to JOptionPane.showConfirmDialog with option type
   * JOptionPane.YES_NO_OPTION and message type JOptionPane.WARNING_MESSAGE
   *
   * @return true for use clicked Yes button or flase else.
   */
  public static boolean confirmWarnYes(Component parent, Object message) {
    String title = OrchidLocale.getString("DEFAULT_WARN_TITLE");
    return confirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE, JOptionPane.YES_OPTION);
  }

  /**
   * Shortcut to JOptionPane.showConfirmDialog with option type
   * JOptionPane.YES_NO_OPTION and message type JOptionPane.WARNING_MESSAGE
   *
   * @return true for use clicked Yes button or flase else.
   */
  public static boolean confirmWarnYes(Component parent, Object message,
          String title) {
    return confirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE, JOptionPane.YES_OPTION);
  }

  /**
   * Shortcut to JOptionPane.showConfirmDialog.
   *
   * @return true for use clicked trueOption button or flase else.
   */
  public static boolean confirmDialog(Component parent, Object message,
          String title, int optionType, int messageType, int trueOption) {
    return JOptionPane.showConfirmDialog(parent,
            message, title, optionType, messageType) == trueOption;
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Input Box Relative...">
  /**
   * Shortcut to JOptionPane.showInputDialog with message type
   * JOptionPane.INFORMATION_MESSAGE, user input will be auto-trimed.
   *
   * @see JOptionPane#showInputDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int, javax.swing.Icon, java.lang.Object[],
   * java.lang.Object)
   */
  public static String msgInput(Component parent, Object message) {
    Object value = msgInput(parent, message, null);
    return value == null ? null : value.toString();
  }

  /**
   * Shortcut to JOptionPane.showInputDialog with message type
   * JOptionPane.WARNING_MESSAGE, user input will be auto-trimed.
   *
   * @see JOptionPane#showInputDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int, javax.swing.Icon, java.lang.Object[],
   * java.lang.Object)
   */
  public static String msgInputWarn(Component parent, Object message) {
    Object value = msgInputWarn(parent, message, null);
    return value == null ? null : value.toString();
  }

  /**
   * Shortcut to JOptionPane.showInputDialog with message type
   * JOptionPane.ERROR_MESSAGE, user input will be auto-trimed.
   *
   * @see JOptionPane#showInputDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int, javax.swing.Icon, java.lang.Object[],
   * java.lang.Object)
   */
  public static String msgInputError(Component parent, Object message) {
    Object value = msgInputError(parent, message, null);
    return value == null ? null : value.toString();
  }

  /**
   * Shortcut to JOptionPane.showInputDialog with message type
   * JOptionPane.INFORMATION_MESSAGE, user input will be auto-trimed.
   *
   * @see JOptionPane#showInputDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int, javax.swing.Icon, java.lang.Object[],
   * java.lang.Object)
   */
  public static String msgInput(Component parent,
          Object message, Object initValue) {
    return msgInput(parent, message, null, initValue);
  }

  /**
   * Shortcut to JOptionPane.showInputDialog with message type
   * JOptionPane.WARNING_MESSAGE, user input will be auto-trimed.
   *
   * @see JOptionPane#showInputDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int, javax.swing.Icon, java.lang.Object[],
   * java.lang.Object)
   */
  public static String msgInputWarn(Component parent,
          Object message, Object initValue) {
    return msgInputWarn(parent, message, null, initValue);
  }

  /**
   * Shortcut to JOptionPane.showInputDialog with message type
   * JOptionPane.ERROR_MESSAGE, user input will be auto-trimed.
   *
   * @see JOptionPane#showInputDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int, javax.swing.Icon, java.lang.Object[],
   * java.lang.Object)
   */
  public static String msgInputError(Component parent,
          Object message, Object initValue) {
    return msgInputError(parent, message, null, initValue);
  }

  /**
   * Shortcut to JOptionPane.showInputDialog with message type
   * JOptionPane.INFORMATION_MESSAGE, user input will be auto-trimed.
   *
   * @see JOptionPane#showInputDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int, javax.swing.Icon, java.lang.Object[],
   * java.lang.Object)
   */
  public static String msgInput(Component parent, Object message,
          Object[] valueList, Object initValue) {
    return msgInput(parent, message,
            valueList, initValue, JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Shortcut to JOptionPane.showInputDialog with message type
   * JOptionPane.WARNING_MESSAGE, user input will be auto-trimed.
   *
   * @see JOptionPane#showInputDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int, javax.swing.Icon, java.lang.Object[],
   * java.lang.Object)
   */
  public static String msgInputWarn(Component parent, Object message,
          Object[] valueList, Object initValue) {
    return msgInput(parent, message,
            valueList, initValue, JOptionPane.WARNING_MESSAGE);
  }

  /**
   * Shortcut to JOptionPane.showInputDialog with message type
   * JOptionPane.ERROR_MESSAGE, user input will be auto-trimed.
   *
   * @see JOptionPane#showInputDialog(java.awt.Component, java.lang.Object,
   * java.lang.String, int, javax.swing.Icon, java.lang.Object[],
   * java.lang.Object)
   */
  public static String msgInputError(Component parent, Object message,
          Object[] valueList, Object initValue) {
    return msgInput(parent, message,
            valueList, initValue, JOptionPane.ERROR_MESSAGE);
  }

  private static String msgInput(Component parent, Object message,
          Object[] valueList, Object initValue, int messageType) {
    String title = OrchidLocale.getString("DEFAULT_INPUT_TITLE");
    Object value = JOptionPane.showInputDialog(parent, message, title,
            messageType, null, valueList, initValue);
    return value == null ? null : value.toString().trim();
  }
  // </editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Image and Color">
  /**
   * Returns new color from the source with density revised.
   *
   * @param source source color
   * @param density density value to revise.
   * @return a new color, it's r/g/b subtract with density value.
   */
  public static Color riseDensity(Color source, int density) {
    return riseDensity(source, density, density, density);
  }

  /**
   * Returns new color from the source with r/g/b density revised.
   *
   * @param source source color
   * @param r density value to revise for red.
   * @param g density value to revise for green.
   * @param b density value to revise for blue.
   * @return a new color, it's r/g/b subtract with the r/g/b parameter value.
   */
  public static Color riseDensity(Color source, int r, int g, int b) {
    return new Color(
            source.getRed() - r, source.getGreen() - g, source.getBlue() - b);
  }

  /**
   * Returns a Image object with same contents as the icon include width and
   * height.
   *
   * @param icon the icon
   * @return a Image object with same contents as the icon include width and
   * height.
   */
  public static Image getImageFromIcon(Icon icon) {
    BufferedImage image = new BufferedImage(icon.getIconWidth(),
            icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics g = image.createGraphics();
    try {
      icon.paintIcon(null, g, 0, 0);
    } finally {
      g.dispose();
    }
    return image;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="range utility methods...">
  /**
   * Check whether the value match the range or not. <br>If the range is null
   * or empty, always return false, regardless of the value.<br>For null value,
   * any option in the range other than Option.EQ and Option.NE will always
   * return false.<br> If the type
   * <code>&lt;T&gt;</code> does not implement
   * <code>Comparable</code>, any option in the range other than Option.EQ and
   * Option.NE will always return false. <br> If the option property of the
   * range is Option.LK or Option.NL, this method explain the character in
   * lowValue of range equals to
   * <code>Range.wildcardSingleChar</code> as explicit one character, and the
   * character in lowValue of range equals to
   * <code>Range.wildcardStringChar</code> as none or more characters.
   *
   * @param <T>
   * @param value value to check
   * @param range range for checking
   * @return true if the value match the range, otherwise false.
   */
  public static <T> boolean match(T value, Range<T> range) {
    if (range == null || range.isEmpty()) {
      return false;
    }
    boolean match;
    try {
      match = matchNoSign(value, range);
    } catch (Exception ex) {
      return false;
    }
    Sign sign = range.getSign();
    return (match && sign == Sign.I) || (!match && sign == Sign.E);
  }

  /**
   * Check whether the value match the range list or not.<br>If the rangeList
   * is null or empty, always return false, regardless of the value.<br>If the
   * value not match any range with sign Sign.E in the rangeList, return
   * false.<br>If the value match any range with sign Sign.I in the rangeList,
   * return true.
   *
   * @see #match(java.lang.Object, com.xzq.osc.field.Range)
   *
   * @param <T>
   * @param value value to check
   * @param rangeList range list for checking.
   * @return true if the value match the range list, otherwise false.
   */
  public static <T> boolean match(T value, RangeList<T> rangeList) {
    if (rangeList == null || rangeList.isEmpty()) {
      return false;
    }
    boolean match = false;
    RangeList<T> copyList = sortCopy(rangeList);
    for (Range<T> range : copyList) {
      match = match(value, range);
      if (match && range.getSign() == Sign.I) {
        break;
      } else if (!match && range.getSign() == Sign.E) {
        break;
      }
    }
    return match;
  }

  @SuppressWarnings("unchecked")
  private static <T> boolean matchNoSign(T value, Range<T> range) {
    switch (range.getOption()) {
      case EQ:
        return equals(value, range.getLowValue());
      case NE:
        return !equals(value, range.getLowValue());
      case LT:
        return ((Comparable) value).compareTo(range.getLowValue()) < 0;
      case LE:
        return ((Comparable) value).compareTo(range.getLowValue()) <= 0;
      case GT:
        return ((Comparable) value).compareTo(range.getLowValue()) > 0;
      case GE:
        return ((Comparable) value).compareTo(range.getLowValue()) >= 0;
      case LK:
        return likeRange((String) value, (String) range.getLowValue());
      case NL:
        return !likeRange((String) value, (String) range.getLowValue());
      case BT:
        return ((Comparable) value).compareTo(range.getLowValue()) >= 0
                && ((Comparable) value).compareTo(range.getHighValue()) <= 0;
      case NB:
        return ((Comparable) value).compareTo(range.getLowValue()) < 0
                || ((Comparable) value).compareTo(range.getHighValue()) > 0;
      default:
        return false;
    }
  }

  private static boolean likeRange(String value, String pattern) {
    String singleRegex = "\\Q" + Range.wildcardSingleChar + "\\E";
    String stringRegex = "\\Q" + Range.wildcardStringChar + "\\E";
    return value.matches(pattern.replaceAll(singleRegex, "\\\\E.\\\\Q")
            .replaceAll(stringRegex, "\\\\E.*\\\\Q")
            .replaceAll("^", "\\\\Q").replaceAll("$", "\\\\E"));
  }

  /**
   * Check whether the value match the range or not. <br>If the range is null
   * or empty, always return false, regardless of the value.<br>For null value,
   * any option in the range other than Option.EQ and Option.NE will always
   * return false.<br>First use
   * <code>Comparable.compareTo</code>, if
   * <code>Comparable.compareTo</code> fails then use
   * <code>Double.compareTo</code>.
   *
   * @param value value to check
   * @param range range for checking
   * @return true if the value match the range, otherwise false.
   */
  public static boolean matchNumber(
          Number value, Range<? extends Number> range) {
    if (range == null || range.isEmpty()) {
      return false;
    }
    boolean match = false;
    try {
      switch (range.getOption()) {
        case EQ:
          match = compareNumber(value, range.getLowValue()) == 0;
          break;
        case NE:
          match = compareNumber(value, range.getLowValue()) != 0;
          break;
        case LT:
          match = compareNumber(value, range.getLowValue()) < 0;
          break;
        case LE:
          match = compareNumber(value, range.getLowValue()) <= 0;
          break;
        case GT:
          match = compareNumber(value, range.getLowValue()) > 0;
          break;
        case GE:
          match = compareNumber(value, range.getLowValue()) == 0;
          break;
        case BT:
          match = compareNumber(value, range.getLowValue()) >= 0
                  && compareNumber(value, range.getHighValue()) <= 0;
          break;
        case NB:
          match = compareNumber(value, range.getLowValue()) < 0
                  || compareNumber(value, range.getHighValue()) > 0;
          break;
      }
    } catch (Exception ex) {
      return false;
    }
    Sign sign = range.getSign();
    return (match && sign == Sign.I) || (!match && sign == Sign.E);
  }

  /**
   * Check whether the value match the range list or not.<br>If the rangeList
   * is null or empty, always return false, regardless of the value.<br> If the
   * value not match any range with sign Sign.E in the rangeList, return
   * false.<br>If the value match any range with sign Sign.I in the rangeList,
   * return true.
   *
   * @see #matchNumber(java.lang.Number, com.xzq.osc.field.Range)
   *
   * @param value value to check
   * @param rangeList range list for checking.
   * @return true if the value match the range list, otherwise false.
   */
  public static boolean matchNumber(
          Number value, RangeList<? extends Number> rangeList) {
    if (rangeList == null || rangeList.isEmpty()) {
      return false;
    }
    boolean match = false;
    RangeList<? extends Number> copyList = sortCopy(rangeList);
    for (Range<? extends Number> range : copyList) {
      match = matchNumber(value, range);
      if (match && range.getSign() == Sign.I) {
        break;
      } else if (!match && range.getSign() == Sign.E) {
        break;
      }
    }
    return match;
  }

  @SuppressWarnings("unchecked")
  private static int compareNumber(Number n1, Number n2) {
    try {
      return n1 == n2 ? 0 : ((Comparable) n1).compareTo(n2);
    } catch (Exception ex) {
      return Double.compare(n1.doubleValue(), n2.doubleValue());
    }
  }

  private static <T> RangeList<T> sortCopy(RangeList<T> rangeList) {
    RangeList<T> copyList = new RangeList<T>(rangeList);
    Collections.sort(copyList, new Comparator<Range<T>>() {
      @Override
      public int compare(Range<T> o1, Range<T> o2) {
        if (o1.getSign() == o2.getSign()) {
          return 0;
        } else {
          return o1.getSign() == Sign.E ? -1 : 1;
        }
      }
    });
    return copyList;
  }

  /**
   * Returns the table if an component was belong to a popup menu invoked by
   * JocTable or null if the component not belong to.
   *
   * @param source the source component.
   * @return the table.
   * @throws NullPointerException if the source belong to a popup menu that not
   * invoked.
   */
  public static JocTable getInvokedTable(Component source) {
    while (source != null) {
      if (source instanceof JPopupMenu) {
        source = ((JPopupMenu) source).getInvoker();
        if (source instanceof JocTable) {
          break;
        }
      }
      source = source.getParent();
    }
    return (JocTable) source;
  }
}