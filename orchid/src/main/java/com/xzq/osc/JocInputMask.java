/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JDialog;
import javax.swing.text.*;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.text.Position.Bias;

/**
 * <p><code>JocInputMask</code> is used to format and edit strings. The behavior
 * of a
 * <code>JocInputMask</code> is controlled by way of a String regex that
 * specifies the valid characters that can be contained at a particular location
 * in the
 * <code>Document</code> model. The String regex was combination by some regex
 * groups, one regex group has one placeholder and one quantifiers.<br> A
 * placeholder should be special formatting characters or fixed character.</p>
 *
 * <table border="1" width="500px" summary=""> <tbody> <tr> <th>Placeholder</th>
 * <th>Description</th> </tr> <tr> <td>'</td> <td>Escape character, used to
 * escape any of the special formatting characters as fixed character.</td>
 * </tr> <tr> <td>#</td> <td>Any valid number, like
 * <code>Character.isDigit</code>.</td> </tr> <tr> <td>?</td> <td>Any character
 * (
 * <code>Character.isLetter</code>).</td> </tr> <tr> <td>A</td> <td>Any
 * character or number (
 * <code>Character.isLetter</code> or
 * <code>Character.isDigit</code>)</td> </tr> <tr> <td>H</td> <td>Any hex
 * character (0-9, a-f or A-F).</td> </tr> <tr> <td>[abc]</td> <td><tt>a</tt>,
 * <tt>b</tt>, or <tt>c</tt>, like java.util.regex.Pattern. special formatting
 * character not supported.</td> </tr> <tr> <td>[a-zA-Z]</td> <td><tt>a</tt>
 * through <tt>z</tt> or <tt>A</tt> through <tt>Z</tt>, inclusive (range), like
 * java.util.regex.Pattern. special formatting character not supported.</td>
 * </tr> <tr> <td>*</td> <td>Anything.</td> </tr> </tbody> </table> <br>
 *
 *
 * <table border="1" width="500px" summary=""> <tbody> <tr> <th>Quantifiers</th>
 * <th>Description (like java.util.regex.Pattern)</th> </tr> <tr> <td>no
 * quantifiers</td> <td>same as <tt>{1}</tt></td> </tr> <tr>
 * <td><i>X</i><tt>{</tt><i>n</i><tt>}</tt></td> <td><i>X</i>, exactly <i>n</i>
 * times</td> </tr> <tr> <td><i>X</i><tt>{</tt><i>n</i><tt>,}</tt></td>
 * <td><i>X</i>, at least <i>n</i> times</td> </tr> <tr>
 * <td><i>X</i><tt>{</tt><i>n</i><tt>,</tt><i>m</i><tt>}</tt></td> <td><i>X</i>,
 * at least <i>n</i> but not more than <i>m</i> times</td> </tr> </tbody>
 * </table>
 *
 * <p>With autoFixedChar set to true, all fixed characters must have no
 * quantifiers or exactly <i>n</i> times.</p>
 *
 * <p>for example: <br> </p>
 *
 * <table border="1" width="500px" summary=""> <tbody> <tr> <th>regex</th>
 * <th>Description</th> </tr> <tr> <td>*{0,}</td> <td>A regex for no limit</td>
 * </tr> <tr> <td>[+-]{0,1}#{1,10}</td> <td>A closer regex for integer
 * number</td> </tr> </tbody> </table>
 *
 * <p></p>
 *
 * <p>JocInputMask can use with component inherited from JTextComponent. </p>
 */
public class JocInputMask implements OrchidAboutIntf {

  public static final char LITERAL_KEY = '\'';
  public static final char DIGIT_KEY = '#';
  public static final char CHARACTER_KEY = '?';
  public static final char ALPHA_NUMERIC_KEY = 'A';
  public static final char HEX_KEY = 'H';
  public static final char RANGE_KEY = '[';
  public static final char ANYTHING_KEY = '*';
  public static final char QUANTIF_KEY = '{';
  protected String mask;
  protected boolean autoFixedChar;
  protected CharCase charCase;
  protected boolean overwriteMode;
  private MaskRegex[] maskChars;
  private JTextComponent tc;
  private List<JTextComponent> tcList;
  private DocumentFilter docFilter;
  private NavigationFilter navFilter;
  private int caretPosBakup = -1;
  private PropertyChangeSupport changeSupport;
  //for use with data binding
  private Map<Document, DocumentFilter> underlyDocFilterMap;
  private Map<Document, NavigationFilter> underlyNavFilterMap;

  /**
   *
   */
  public JocInputMask() {
    this(null);
  }

  /**
   *
   * @param mask
   */
  public JocInputMask(String mask) {
    this(mask, false, null, false);
  }

  /**
   *
   * @param mask
   * @param autoFixedChar
   */
  public JocInputMask(String mask, boolean autoFixedChar) {
    this(mask, autoFixedChar, null, false);
  }

  /**
   *
   * @param mask
   * @param charCase
   */
  public JocInputMask(String mask, CharCase charCase) {
    this(mask, false, charCase, false);
  }

  /**
   *
   * @param mask
   * @param autoFixedChar
   * @param charCase
   * @param overwriteMode
   */
  public JocInputMask(String mask, boolean autoFixedChar,
          CharCase charCase, boolean overwriteMode) {
    tcList = new ArrayList<JTextComponent>();
    changeSupport = new PropertyChangeSupport(this);
    underlyDocFilterMap = new HashMap<Document, DocumentFilter>();
    underlyNavFilterMap = new HashMap<Document, NavigationFilter>();
    maskChars = new MaskRegex[0];
    setMask(mask);
    setAutoFixedChar(autoFixedChar);
    setCharCase(charCase);
    setOverwriteMode(overwriteMode);
  }

  /**
   *
   * @return
   */
  @Override
  protected Object clone() {
    JocInputMask masker = new JocInputMask();
    masker.setMask(mask);
    masker.setCharCase(charCase);
    masker.setOverwriteMode(overwriteMode);
    return masker;
  }

  // <editor-fold defaultstate="collapsed" desc="install/uninstall with text component">
  /**
   * Returns the text component assigned with setTextComponent or null if no
   * text component assigned.
   *
   *
   * @return text component.
   */
  public JTextComponent getTextComponent() {
    return tc;
  }

  /**
   * bind a text component will this regex, if the tc is null, then unbind the
   * text component already bound.
   *
   * @param tc text component.
   */
  public void setTextComponent(JTextComponent tc) {
    JTextComponent old = this.tc;
    if (old != tc) {
      unmaskTextComponent(old);
      maskTextComponent(this.tc = tc);
    }
  }

  /**
   * install this regex to the text component, install this regex to multiple
   * text component is suppoted.
   *
   * @param tc text component.
   */
  public void maskTextComponent(JTextComponent tc) {
    if (tc != null && !tcList.contains(tc)) {
      tcList.add(tc);
      installFilter(tc, getDocumentFilter(), getNavigationFilter());
    }
  }

  /**
   * uninstall this regex from the text component.
   *
   * @param tc text component.
   */
  public void unmaskTextComponent(JTextComponent tc) {
    if (tcList.contains(tc)) {
      if (tc == this.tc) {
        this.tc = null;
      }
      tcList.remove(tc);
      uninstallFilter(tc);
    }
  }

  /**
   * uninstall this regex from all bound text component.
   */
  public void unmaskAllComponents() {
    while (!tcList.isEmpty()) {
      unmaskTextComponent(tcList.get(0));
    }
  }

  /**
   * install filter in the text component.
   *
   * @param tc text component
   * @param docFilter document filter
   * @param navFilter navigate filter
   */
  protected void installFilter(JTextComponent tc, DocumentFilter docFilter,
          NavigationFilter navFilter) {
    Document document = tc.getDocument();
    if (document instanceof AbstractDocument) {
      AbstractDocument doc = (AbstractDocument) document;
      underlyNavFilterMap.put(doc, tc.getNavigationFilter());
      underlyDocFilterMap.put(doc, doc.getDocumentFilter());
      tc.setNavigationFilter(navFilter);
      doc.setDocumentFilter(docFilter);
    }
  }

  /**
   * uninstall filter from the text component.
   *
   * @param tc text component.
   */
  protected void uninstallFilter(JTextComponent tc) {
    Document document = tc.getDocument();
    if (document instanceof AbstractDocument) {
      AbstractDocument doc = (AbstractDocument) document;
      tc.setNavigationFilter(underlyNavFilterMap.get(doc));
      doc.setDocumentFilter(underlyDocFilterMap.get(doc));
      underlyNavFilterMap.remove(doc);
      underlyDocFilterMap.remove(doc);
    }
  }

  /**
   * Returns document filter of this regex.
   *
   * @return
   */
  protected DocumentFilter getDocumentFilter() {
    if (docFilter == null) {
      docFilter = new InputDocumentFilter();
    }
    return docFilter;
  }

  /**
   * Returns navigate filter of this regex.
   *
   * @return
   */
  protected NavigationFilter getNavigationFilter() {
    if (navFilter == null) {
      navFilter = new InputNavigationFilter();
    }
    return navFilter;
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="getter/setter of properties">
  /**
   * Returns regex string. default is null.
   *
   * @return regex
   */
  public String getMask() {
    return mask;
  }

  /**
   * Set mask string.
   *
   * @param mask mask string.
   */
  public void setMask(String mask) {
    String old = this.mask;
    if (!OrchidUtils.equals(old, mask)) {
      updateMask(mask, autoFixedChar);
      this.mask = mask;
      changeSupport.firePropertyChange("mask", old, mask);
    }
  }

  /**
   * Return true for auto place fixed character or false not.
   *
   * @return true or false
   */
  public boolean isAutoFixedChar() {
    return autoFixedChar;
  }

  /**
   * Set whether auto place fixed character or not. if this property set to
   * true, the fixed characters in mask will be auto placed, and all fixed
   * characters in mask must have no quantifiers or be exactly <tt>n</tt> times.
   *
   * @param autoFixedChar true or false
   */
  public void setAutoFixedChar(boolean autoFixedChar) {
    boolean old = this.autoFixedChar;
    if (old != autoFixedChar) {
      updateMask(mask, autoFixedChar);
      this.autoFixedChar = autoFixedChar;
      changeSupport.firePropertyChange("autoFixedChar", old, autoFixedChar);
    }
  }

  /**
   * Returns char case limit. the mask will auto convert user input to
   * appropriate case. default is null..
   *
   * @return char case limit
   */
  public CharCase getCharCase() {
    return charCase;
  }

  /**
   * Set char case limit.
   *
   * @param charCase char case limit.
   */
  public void setCharCase(CharCase charCase) {
    CharCase old = this.charCase;
    if (!OrchidUtils.equals(old, charCase)) {
      this.charCase = charCase;
      changeSupport.firePropertyChange("charCase", old, charCase);
    }
  }

  /**
   * Returns overwrite indicator, if this property is true, then use overwrite
   * mode in user input.
   *
   * @return true or false.
   */
  public boolean getOverwriteMode() {
    return overwriteMode;
  }

  /**
   * Sets overwrite indicator.
   *
   * @param overwriteMode true or false.
   */
  public void setOverwriteMode(boolean overwriteMode) {
    boolean old = this.overwriteMode;
    if (old != overwriteMode) {
      this.overwriteMode = overwriteMode;
      changeSupport.firePropertyChange("overwriteMode", old, overwriteMode);
    }
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="caculate masker array">
  /**
   * caculate masker array internal.
   */
  private void updateMask(String masker, boolean autoFixedChar) {
    List<MaskRegex> temp = new ArrayList<MaskRegex>();
    int start = 0, close = 0;
    int max = masker == null ? 0 : masker.length();
    while (start < max) {
      String regex = null;
      char fixedChar = '\0';
      int minLength = 1, maxLength = 1;
      char maskChar = masker.charAt(start);
      if (maskChar == LITERAL_KEY) {
        if (start == max - 1) {
          throw new IllegalArgumentException("no fixed char after \"'\"!");
        }
        fixedChar = masker.charAt(++close);
      } else if (maskChar == DIGIT_KEY) {
        regex = "\\d";
      } else if (maskChar == CHARACTER_KEY) {
        regex = "[a-zA-Z]";
      } else if (maskChar == ALPHA_NUMERIC_KEY) {
        regex = "\\w";
      } else if (maskChar == HEX_KEY) {
        regex = "[0-9a-fA-F]";
      } else if (maskChar == RANGE_KEY) {
        close = masker.indexOf(']', start);
        if (close == -1) {
          throw new IllegalArgumentException("close brackets ']' not found!");
        }
        regex = buildRangeRegex(masker.substring(start + 1, close));
      } else if (maskChar == ANYTHING_KEY) {
        regex = ".";
      } else {
        fixedChar = maskChar;
      }
      start = ++close;
      if (start < max && masker.charAt(start) == QUANTIF_KEY) {
        close = masker.indexOf('}', start);
        if (close == -1) {
          throw new IllegalArgumentException("close brackets '}' not found!");
        }
        String q = masker.substring(start + 1, close);
        String[] limits = q.split(",");
        minLength = Integer.valueOf(limits[0]);
        if (limits.length == 2) {
          maxLength = Integer.valueOf(limits[1]);
        } else if (q.contains(",")) {
          maxLength = -1;
        } else {
          maxLength = minLength;
        }
        start = ++close;
      }
      if (autoFixedChar && minLength != maxLength) {
        throw new IllegalArgumentException("With autoFixedChar true, all fixed"
                + " characters must have no quantifiers or exactly n times!");
      }
      if (regex != null) {
        temp.add(new MaskRegex(regex, minLength, maxLength));
      } else {
        temp.add(new MaskRegex(fixedChar, minLength, maxLength));
      }
    }
    maskChars = temp.toArray(new MaskRegex[0]);
  }

  private String buildRangeRegex(String masker) {
    String regex = "";
    for (String sub : masker.split("-")) {
      if (!sub.isEmpty()) {
        sub = "\\Q" + sub + "\\E";
      }
      regex += sub + "-";
    }
    if (masker.endsWith("-")) {
      regex += "-";
    }
    return "[" + regex.substring(0, regex.length() - 1) + "]";
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="parse input string">
  /**
   * replace substring by length from old with text. using mask to check whether
   * can be replace or not. if can not be replaced, return null.
   *
   * @param old old string
   * @param offset from position
   * @param length replace length
   * @param text string for replace
   * @return replaced string or null if can not be replaced.
   */
  public String replace(String old, int offset, int length, String text) {
    Object[] replace = doReplace(old, offset, length, text);
    text = (String) replace[0];
    if (text == null) {
      return null;
    }
    text = old.substring(0, offset) + text;
    offset += (Integer) replace[1];
    if (offset < old.length()) {
      text += old.substring(offset);
    }
    return text;
  }

  /**
   * process document change, make change text to match the mask or prevent
   * change if not match the mask.
   *
   * @param fb filter bypass
   * @param offset offset
   * @param length length
   * @param text text
   * @param attr attributes
   * @throws BadLocationException
   */
  protected void replace(FilterBypass fb, int offset, int length,
          String text, AttributeSet attr) throws BadLocationException {
    Document document = fb.getDocument();
    String tcText = document.getText(0, document.getLength());
    Object[] replace = doReplace(tcText, offset, length, text);
    text = (String) replace[0];
    if (text == null) {
      return;
    }
    length = (Integer) replace[1];
    caretPosBakup = (Integer) replace[2];
    DocumentFilter underly = underlyDocFilterMap.get(fb.getDocument());
    if (underly != null) {
      //for use with data binding
      underly.replace(fb, offset, length, text, attr);
    } else {
      fb.replace(offset, length, text, attr);
    }
  }

  private Object[] doReplace(String old, int offset, int length,
          String text) {
    Object[] results = new Object[]{null, 0, -1};
    text = text == null ? "" : matchToCharCase(text);
    if (!text.isEmpty() && overwriteMode) {
      length = text.length();
      if (length > old.length() - offset) {
        length = old.length() - offset;
      }
    }
    if (maskChars.length == 0) {
      results[0] = text;
      results[1] = length;
      return results;
    }
    // index array, first means text index, second means mask index.
    int[] textIndex = new int[]{0, 0};
    findMaskIndex(old, offset, textIndex);
    int[] tailIndex = Arrays.copyOf(textIndex, 2);
    String mText = offset == 0 ? "" : old.substring(0, offset);
    if (!text.isEmpty()) {
      text = canReplace(mText, textIndex, offset, text, true);
      if (text == null) {
        return results;
      }
    }
    String otail = old.substring(offset + length);
    if (!otail.isEmpty()) {
      int nlen = overwriteMode ? text.length() : length;
      String ntail = getClearTail(old, offset + nlen, tailIndex);
      mText += text;
      int offtmp = mText.length();
      ntail = canReplace(mText, textIndex, offtmp, ntail, false);
      if (ntail == null) {
        return results;
      }
      if (!ntail.equals(otail)) {
        results[2] = offset + text.length();
        text += ntail;
        length += otail.length();
      }
    }
    results[0] = text;
    results[1] = length;
    return results;
  }

  private String matchToCharCase(String text) {
    CharCase cc = getCharCase();
    if (cc == CharCase.LOWER) {
      return text.toLowerCase();
    }
    if (cc == CharCase.UPPER) {
      return text.toUpperCase();
    }
    return text;
  }

  private void findMaskIndex(String text, int offset, int[] indexes) {
    text = text.substring(indexes[0]);
    while (!text.isEmpty()) {
      Pattern p = maskChars[indexes[1]].getPattern();
      Matcher matcher = p.matcher(text);
      if (matcher.lookingAt()) {
        int end = matcher.end();
        if (indexes[0] + end >= offset
                || indexes[1] == maskChars.length - 1) {
          return;
        }
        indexes[1]++;
        indexes[0] += end;
        text = text.substring(end);
      } else {
        throw new RuntimeException("unexpected not matched text.");
      }
    }
  }

  private String getClearTail(String tcText, int offset, int[] indexes) {
    if (!autoFixedChar) {
      return tcText.substring(offset);
    }
    String tail = "";
    indexes = Arrays.copyOf(indexes, indexes.length);
    findMaskIndex(tcText, offset, indexes);
    tcText = tcText.substring(indexes[0]);
    offset = offset - indexes[0];
    while (!tcText.isEmpty()) {
      MaskRegex regex = maskChars[indexes[1]];
      Matcher matcher = regex.getPattern().matcher(tcText);
      if (!matcher.lookingAt()) {
        throw new RuntimeException("unexpected not matched text.");
      }
      if (regex.getFixedChar() == '\0') {
        tail += matcher.group().substring(offset);
      }
      offset = 0;
      indexes[1]++;
      tcText = tcText.substring(matcher.end());
    }
    return tail;
  }

  private String canReplace(String tcText, int[] indexes, int offset,
          String replace, boolean truncate) {
    int last = 0;
    String result = "", sub;
    tcText = (tcText + replace).substring(indexes[0]);
    offset = offset - indexes[0];
    while (!tcText.isEmpty()) {
      if (indexes[1] == maskChars.length) {
        break;
      }
      MaskRegex regex = maskChars[indexes[1]];
      Matcher matcher = regex.getPattern().matcher(tcText);
      char fixed = regex.getFixedChar();
      int minLen = regex.getMinLength();
      int textLen = tcText.length();
      if (matcher.lookingAt()
              && (matcher.end() >= minLen
              || matcher.end() == textLen
              || indexes[1] == maskChars.length-1)) {
        sub = matcher.group().substring(offset);
        indexes[0] += last = matcher.end();
        tcText = tcText.substring(matcher.end());
      } else if (autoFixedChar && fixed != '\0') {
        indexes[0] += last = minLen;
        sub = repeatChar(fixed, minLen).substring(offset);
      } else {
        return null; // can not replace
      }
      offset = 0;
      indexes[1]++;
      result += sub;
    }
    if (!tcText.isEmpty() && !truncate) {
      return null;
    }
    indexes[0] -= last;
    indexes[1]--;
    return result;
  }

  private String repeatChar(char c, int times) {
    char[] array = new char[times];
    Arrays.fill(array, c);
    return new String(array);
  }
  // </editor-fold>

  private static class MaskRegex {

    private Pattern pattern;
    private char fixedChar;
    private int minLength;
    private int maxLength;

    public MaskRegex(String regex, int minLength, int maxLength) {
      initializeMaskRegex(regex, minLength, maxLength);
    }

    public MaskRegex(char fixedChar, int minLength, int maxLength) {
      this.fixedChar = fixedChar;
      String regex = "\\Q" + fixedChar + "\\E";
      initializeMaskRegex(regex, minLength, maxLength);
    }

    private void initializeMaskRegex(String regex,
            int minLength, int maxLength) {
      this.minLength = minLength;
      this.maxLength = maxLength;
      if (maxLength != -1 && maxLength < 1) {
        throw new IllegalArgumentException(
                "maxLength can not less than zero!");
      }
      if (maxLength != -1 && maxLength < minLength) {
        throw new IllegalArgumentException(
                "maxLength can not less than minLength!");
      }
      regex += maxLength == -1 ? "*" : "{0," + maxLength + "}";
      this.pattern = Pattern.compile(regex);
    }

    public Pattern getPattern() {
      return pattern;
    }

    public char getFixedChar() {
      return fixedChar;
    }

    public int getMinLength() {
      return minLength;
    }

    public int getMaxLength() {
      return maxLength;
    }
  }

  private class InputDocumentFilter extends DocumentFilter {

    @Override
    public void insertString(FilterBypass fb, int offset, String text,
            AttributeSet attr) throws BadLocationException {
      this.replace(fb, offset, 0, text, attr);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {
      JocInputMask.this.replace(fb, offset, length, null, null);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text,
            AttributeSet attrs) throws BadLocationException {
      JocInputMask.this.replace(fb, offset, length, text, attrs);
    }
  }

  private class InputNavigationFilter extends NavigationFilter {

    private boolean restoreDot;

    @Override
    public int getNextVisualPositionFrom(JTextComponent text, int pos,
            Bias bias, int direction, Bias[] biasRet)
            throws BadLocationException {
      return super.getNextVisualPositionFrom(text, pos, bias, direction,
              biasRet);
    }

    @Override
    public void moveDot(FilterBypass fb, int dot, Bias bias) {
      super.moveDot(fb, dot, bias);
    }

    @Override
    public void setDot(FilterBypass fb, int dot, Bias bias) {
      if (restoreDot) {
        dot = caretPosBakup;
        caretPosBakup = -1;
        restoreDot = false;
      }
      if (caretPosBakup != -1) {
        restoreDot = true;
      }
      super.setDot(fb, dot, bias);
    }
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
}
