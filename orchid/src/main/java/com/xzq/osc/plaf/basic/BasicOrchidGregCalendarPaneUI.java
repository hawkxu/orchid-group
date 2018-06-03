/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.plaf.basic;

import com.xzq.osc.JocGregCalendarPane;
import com.xzq.osc.OrchidLocale;
import com.xzq.osc.OrchidUtils;
import com.xzq.osc.plaf.LazyActionMap;
import com.xzq.osc.print.PrintUtils;
import com.xzq.osc.print.TextBorder;
import com.xzq.osc.resource.Resource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.*;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author zqxu
 */
public class BasicOrchidGregCalendarPaneUI extends ComponentUI {

  private static final int ROLL_PREV = -999997;
  private static final int ROLL_NEXT = -999998;
  private static final int ROLL_NONE = -999999;
  private static final String weekNumberName = OrchidLocale.getString("WEEK_TITLE");
  private static final String[] weekdayNames = OrchidLocale.getString("WEEKDAY_NAMES").split("\\,");
  private static final int PREV_MONTH = 21;
  private static final int GOTO_TODAY = 22;
  private static final int NEXT_MONTH = 23;
  protected JocGregCalendarPane pane;
  private JToolBar tlbGoto;
  private JButton btnPrev;
  private JButton btnToday;
  private JButton btnNext;
  private JPanel pnTitle;
  private JSpinner spnYear;
  private JSpinner spnMonth;
  private Handler handler;
  protected TextBorder cellBorder;
  protected Rectangle paintRect;
  protected Rectangle titleRect;
  protected Rectangle leftTopCorner;
  protected Dimension dayCellSize;

  public static ComponentUI createUI(JComponent c) {
    return new BasicOrchidGregCalendarPaneUI();
  }

  // <editor-fold defaultstate="collapsed" desc="install/remove UI...">
  /**
   * install UI for component c
   *
   * @param c component
   */
  @Override
  public void installUI(JComponent c) {
    pane = (JocGregCalendarPane) c;
    installDefauts();
    installKeyboardActions();
    pane.add(getGotoToolbar());
    pane.add(getTitlePane());
    pane.setLayout(getHandler());
    pane.addMouseListener(getHandler());
    pane.addMouseMotionListener(getHandler());
    pane.addPropertyChangeListener(getHandler());
  }

  // install component defaults
  private void installDefauts() {
    LookAndFeel.installColorsAndFont(pane, "Panel.background",
            "Panel.foreground", "Panel.font");
    LookAndFeel.installProperty(pane, "opaque", Boolean.TRUE);
    pane.setMinimumYear(1901);
    pane.setMaximumYear(2100);
    pane.setRolloverDate(new Date());
    pane.setCellBorderColor(Color.GRAY);
    pane.setTitleBackground(new Color(16750335)); //255,150,255
    pane.setSelectionForeground(Color.WHITE);
    pane.setSelectionBackground(new Color(5263615)); //80,80,255
    pane.setWeekendForeground(Color.RED);
    pane.setWeekNumberBackground(new Color(16777110)); //255,255,150
    pane.setWeekdayNameBackground(new Color(9895830)); //150,255,150
    pane.setRolloverBackground(Color.PINK);
    pane.setOutMonthForeground(Color.LIGHT_GRAY);
  }

  //install keyboard default actions
  private void installKeyboardActions() {
    InputMap inputMap = getInputMap();
    SwingUtilities.replaceUIInputMap(pane, JComponent.WHEN_FOCUSED,
            inputMap);
    SwingUtilities.replaceUIActionMap(pane, getActionMap());
  }

  private InputMap getInputMap() {
    InputMap inputMap = new InputMap();
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false),
            UIAction.ROLL_PREV_DAY);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false),
            UIAction.ROLL_NEXT_DAY);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false),
            UIAction.ROLL_PREV_WEEK);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false),
            UIAction.ROLL_NEXT_WEEK);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false),
            UIAction.SELECT_ROLL_DAY);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
            UIAction.SELECT_ROLL_DAY);
    return inputMap;
  }

  private ActionMap getActionMap() {
    LazyActionMap actionMap = new LazyActionMap();
    actionMap.put(new UIAction(UIAction.ROLL_PREV_DAY));
    actionMap.put(new UIAction(UIAction.ROLL_NEXT_DAY));
    actionMap.put(new UIAction(UIAction.ROLL_PREV_WEEK));
    actionMap.put(new UIAction(UIAction.ROLL_NEXT_WEEK));
    actionMap.put(new UIAction(UIAction.SELECT_ROLL_DAY));
    return actionMap;
  }

  /**
   * uninstall UI for component c
   *
   * @param c component
   */
  @Override
  public void uninstallUI(JComponent c) {
    pane.setLayout(handler = null);
    pane.remove(getGotoToolbar());
    pane.remove(getTitlePane());
    uninstallKeyboardActions();
    pane.removeMouseListener(getHandler());
    pane.removeMouseMotionListener(getHandler());
    pane.removePropertyChangeListener(getHandler());
    pane = null;
    tlbGoto = null;
    pnTitle = null;
    spnYear = spnMonth = null;
    btnPrev = btnToday = btnNext = null;
  }

  private void uninstallKeyboardActions() {
    SwingUtilities.replaceUIActionMap(pane, null);
    SwingUtilities.replaceUIInputMap(pane, JComponent.WHEN_FOCUSED, null);
  }

  private Handler getHandler() {
    if (handler == null) {
      handler = new Handler();
    }
    return handler;
  }

  /**
   * Returns preferred size for calendar pane.
   *
   * @param c calendar pane
   * @return preferred size
   */
  @Override
  public Dimension getPreferredSize(JComponent c) {
    Insets insets = pane.getInsets();
    Dimension size = new Dimension();
    size.width = insets.left + insets.right;
    size.height = insets.top + insets.bottom + calcTitleHeight();

    Insets bi = getCellBorder().getTotalInsets();
    FontMetrics fm = pane.getFontMetrics(pane.getFont());
    int cellWidth = fm.stringWidth("999") + bi.left + bi.right;
    int cellHeight = fm.getHeight() + bi.top + bi.bottom;
    Dimension preferredDayCellSize = pane.getPreferredDayCellSize();
    if (preferredDayCellSize != null) {
      cellWidth = preferredDayCellSize.width;
      cellHeight = preferredDayCellSize.height;
    }
    size.width += cellWidth * 8;
    size.height += cellHeight * 7;
    return size;
  }

  // caculate paint rects
  private void calcPaintRects() {
    Insets insets = pane.getInsets();
    Insets bi = getCellBorder().getTotalInsets();
    FontMetrics fm = pane.getFontMetrics(pane.getFont());
    paintRect = new Rectangle(insets.left, insets.top,
            pane.getWidth() - insets.left - insets.right,
            pane.getHeight() - insets.top - insets.bottom);
    titleRect = new Rectangle(paintRect.x, paintRect.y, paintRect.width,
            calcTitleHeight());
    leftTopCorner = (Rectangle) titleRect.clone();
    leftTopCorner.translate(0, leftTopCorner.height);
    leftTopCorner.width = fm.stringWidth(weekNumberName) + bi.left + bi.right;
    leftTopCorner.height = fm.getHeight() + bi.top + bi.bottom;
    dayCellSize = new Dimension();
    dayCellSize.width = (paintRect.width - leftTopCorner.width) / 7;
    dayCellSize.height = (paintRect.height - titleRect.height
            - leftTopCorner.height) / 6;
    paintRect.width = titleRect.width = leftTopCorner.width
            + dayCellSize.width * 7;
    paintRect.height = titleRect.height + leftTopCorner.height
            + dayCellSize.height * 6;
  }

  // returns cell border
  private TextBorder getCellBorder() {
    if (cellBorder == null) {
      cellBorder = new TextBorder(1);
      cellBorder.setLeftTop(false);
      cellBorder.setAll(pane.getCellBorderColor());
      cellBorder.setInsets(new Insets(2, 3, 2, 3));
    }
    return cellBorder;
  }

  /**
   * caculate title area height.
   *
   * @return title area height
   */
  protected int calcTitleHeight() {
    Insets bi = getCellBorder().getTotalInsets();
    return Math.max(getGotoToolbar().getPreferredSize().height,
            getTitlePane().getPreferredSize().height) + bi.top + bi.bottom;
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="create function buttons...">
  // return goto toolbar
  private JToolBar getGotoToolbar() {
    if (tlbGoto == null) {
      tlbGoto = new JToolBar();
      tlbGoto.setOpaque(false);
      tlbGoto.setFloatable(false);
      tlbGoto.add(getMonthPrevButton());
      tlbGoto.add(getGotoTodayButton());
      tlbGoto.add(getMonthNextButton());
    }
    return tlbGoto;
  }

  private JButton getMonthPrevButton() {
    if (btnPrev == null) {
      btnPrev = new JButton(Resource.getOrchidIcon("month_prev.png"));
      btnPrev.setOpaque(false);
      btnPrev.setFocusable(false);
      btnPrev.setToolTipText(OrchidLocale.getString("PREV_MONTH_TIP"));
      btnPrev.addActionListener(new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          gotoMonth(PREV_MONTH);
        }
      });
    }
    return btnPrev;
  }

  private JButton getGotoTodayButton() {
    if (btnToday == null) {
      btnToday = new JButton(Resource.getOrchidIcon("goto_today.png"));
      btnToday.setOpaque(false);
      btnToday.setFocusable(false);
      btnToday.setToolTipText(OrchidLocale.getString("GOTO_TODAY_TIP"));
      btnToday.addActionListener(new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          gotoMonth(GOTO_TODAY);
        }
      });
    }
    return btnToday;
  }

  private JButton getMonthNextButton() {
    if (btnNext == null) {
      btnNext = new JButton(Resource.getOrchidIcon("month_next.png"));
      btnNext.setOpaque(false);
      btnNext.setFocusable(false);
      btnNext.setToolTipText(OrchidLocale.getString("NEXT_MONTH_TIP"));
      btnNext.addActionListener(new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          gotoMonth(NEXT_MONTH);
        }
      });
    }
    return btnNext;
  }

  /**
   *
   * @param month
   */
  protected void gotoMonth(int month) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(pane.getRolloverDate());
    if (month == PREV_MONTH) {
      calendar.add(Calendar.MONTH, -1);
    } else if (month == GOTO_TODAY) {
      calendar.setTime(getToday());
    } else if (month == NEXT_MONTH) {
      calendar.add(Calendar.MONTH, 1);
    }
    pane.setRolloverDate(calendar.getTime());
  }

  private Date getToday() {
    return OrchidUtils.combineDateTime(new Date(), pane.getRolloverDate());
  }

  private JPanel getTitlePane() {
    if (pnTitle == null) {
      pnTitle = new JPanel();
      pnTitle.setOpaque(false);
      pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      pnTitle.add(getYearSpinner());
      pnTitle.add(new JLabel(OrchidLocale.getString("YEAR_TITLE")));
      pnTitle.add(getMonthSpinner());
      pnTitle.add(new JLabel(OrchidLocale.getString("MONTH_TITLE")));
    }
    return pnTitle;
  }

  private JSpinner getYearSpinner() {
    if (spnYear == null) {
      spnYear = new JSpinner();
      int mininum = pane.getMinimumYear();
      int maximum = pane.getMaximumYear();
      int current = OrchidUtils.getDateField(pane.getRolloverDate(), Calendar.YEAR);
      spnYear.setModel(new SpinnerNumberModel(current, mininum, maximum, 1));
      NumberEditor neYear = new NumberEditor(spnYear, "####");
      neYear.getTextField().setColumns(4);
      spnYear.setEditor(neYear);
      spnYear.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          int year = (Integer) spnYear.getValue();
          rollDate(year, ROLL_NONE, ROLL_NONE, ROLL_NONE);
        }
      });
    }
    return spnYear;
  }

  private JSpinner getMonthSpinner() {
    if (spnMonth == null) {
      spnMonth = new JSpinner();
      int current = OrchidUtils.getDateField(pane.getRolloverDate(), Calendar.MONTH);
      spnMonth.setModel(new SpinnerNumberModel(current + 1, 1, 12, 1));
      NumberEditor neMonth = new NumberEditor(spnMonth, "##");
      neMonth.getTextField().setColumns(2);
      spnMonth.setEditor(neMonth);
      spnMonth.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          int month = (Integer) spnMonth.getValue() - 1;
          rollDate(ROLL_NONE, month, ROLL_NONE, ROLL_NONE);
        }
      });
    }
    return spnMonth;
  }

  private void updateTitle(Date rollover) {
    Calendar nRollover = new GregorianCalendar();
    nRollover.setTime(rollover);
    spnYear.setValue(nRollover.get(Calendar.YEAR));
    spnMonth.setValue(nRollover.get(Calendar.MONTH) + 1);
  }

  // update spinner minimum to match calendar pane minimum year limit
  private void updateMinimumYear(int minimumYear) {
    SpinnerModel model = getYearSpinner().getModel();
    if (model instanceof SpinnerNumberModel) {
      ((SpinnerNumberModel) model).setMinimum(minimumYear);
    }
  }

  // update spinner maximum to match calendar pane maximum year limit
  private void updateMaximumYear(int maximumYear) {
    SpinnerModel model = getYearSpinner().getModel();
    if (model instanceof SpinnerNumberModel) {
      ((SpinnerNumberModel) model).setMaximum(maximumYear);
    }
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Paint Relative...">
  /**
   * paint calendar pane
   */
  @Override
  public void paint(Graphics g, JComponent c) {
    Graphics2D g2 = (Graphics2D) g;
    Calendar rollover = new GregorianCalendar();
    rollover.setTime(pane.getRolloverDate());
    Calendar selected = null;
    if (pane.getSelectedDate() != null) {
      selected = new GregorianCalendar();
      selected.setTime(pane.getSelectedDate());
    }
    Calendar first = (Calendar) rollover.clone();
    first.set(Calendar.WEEK_OF_MONTH, 1);
    first.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

    Point lt = new Point(), rb = new Point();
    calcLeftTopAndRigthBottomPosition(g2, lt, rb);
    for (int row = lt.y; row <= rb.y; row++) {
      for (int col = lt.x; col <= rb.x; col++) {
        Calendar calendar = (Calendar) first.clone();
        if (row > 0) {
          calendar.add(Calendar.WEEK_OF_YEAR, row);
        }
        if (col > 0) {
          calendar.set(Calendar.DAY_OF_WEEK, col + 1);
        }
        boolean isRollover = isSameDate(calendar, rollover)
                && row > -1 && col > -1;
        boolean isSelected = isSameDate(calendar, selected)
                && row > -1 && col > -1;
        boolean isOutMonth = !isSameMonth(calendar, rollover)
                && row > -1 && col > -1;
        boolean isWeekend = isWeekend(calendar) && col > -1;
        Color foreground = getCellForeground(row, col, isRollover, isSelected,
                isOutMonth, isWeekend);
        Color background = getCellBackground(row, col, isRollover, isSelected,
                isOutMonth, isWeekend);
        Rectangle rect = getCellRect(row, col);
        String cellText = getCellText(row, col, calendar);
        paintCalendarCell(g2, rect, foreground, background, cellText);
      }
    }
    paintCalendarPaneBorder(g2);
  }

  /**
   * caculate left top corner and right bottom corner of clip area, target value
   * range [-1,-1] to [6,5].
   *
   * @param g2 graphic object
   * @param lt left top corner
   * @param rb right bottom corner
   */
  protected void calcLeftTopAndRigthBottomPosition(Graphics2D g2,
          Point lt, Point rb) {
    Rectangle clip = g2.getClipBounds();
    clip.x -= leftTopCorner.x + leftTopCorner.width;
    clip.y -= leftTopCorner.y + leftTopCorner.height;
    lt.x = clip.x < 0 ? -1 : clip.x / dayCellSize.width;
    lt.y = clip.y < 0 ? -1 : clip.y / dayCellSize.height;
    rb.x = Math.min(6, (clip.x + clip.width) / dayCellSize.width);
    rb.y = Math.min(5, (clip.y + clip.height) / dayCellSize.height);
  }

  //check is same date
  private boolean isSameDate(Calendar d1, Calendar d2) {
    return d1 != null && d2 != null
            && d1.get(Calendar.DAY_OF_MONTH) == d2.get(Calendar.DAY_OF_MONTH)
            && d1.get(Calendar.MONTH) == d2.get(Calendar.MONTH)
            && d1.get(Calendar.YEAR) == d2.get(Calendar.YEAR);
  }

  //check is same month
  private boolean isSameMonth(Calendar d1, Calendar d2) {
    return d1 != null && d2 != null
            && d1.get(Calendar.MONTH) == d2.get(Calendar.MONTH)
            && d1.get(Calendar.YEAR) == d2.get(Calendar.YEAR);
  }

  //check is weekend day
  private boolean isWeekend(Calendar calendar) {
    return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
            || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
  }

  /**
   * Returns cell foreground color.
   *
   * @param row row index, value between -1 to 5
   * @param col col index, value between -1 to 6
   * @param isRollover is rollover cell
   * @param isSelected is selected cell
   * @param isOutMonth is day of cell out of month
   * @param isWeekend is day of cell is weekend
   * @return cell foreground color
   */
  protected Color getCellForeground(int row, int col, boolean isRollover,
          boolean isSelected, boolean isOutMonth, boolean isWeekend) {
    Color foreground = null;
    if (isSelected) {
      foreground = pane.getSelectionForeground();
    } else if (isOutMonth) {
      foreground = pane.getOutMonthForeground();
    } else if (isWeekend) {
      foreground = pane.getWeekendForeground();
    }
    return foreground != null ? foreground : pane.getForeground();
  }

  /**
   * Returns cell background color
   *
   * @param row row index, value between -1 to 5
   * @param col col index, value between -1 to 6
   * @param isRollover is rollover cell
   * @param isSelected is selected cell
   * @param isOutMonth is day of cell out of month
   * @param isWeekend is day of cell is weekend
   * @return cell background color
   */
  protected Color getCellBackground(int row, int col, boolean isRollover,
          boolean isSelected, boolean isOutMonth, boolean isWeekend) {
    Color background = null;
    if (col == -1) {
      background = pane.getWeekNumberBackground();
    } else if (row == -1) {
      background = pane.getWeekdayNameBackground();
    } else if (isSelected) {
      background = pane.getSelectionBackground();
    } else if (isRollover) {
      background = pane.getRolloverBackground();
    }
    return background != null ? background : pane.getBackground();
  }

  /**
   * Returns cell rect
   *
   * @param row row index, value between -1 to 5
   * @param col col index, value between -1 to 6
   * @return cell rect
   */
  protected Rectangle getCellRect(int row, int col) {
    Rectangle rect = (Rectangle) leftTopCorner.clone();
    if (col != -1) {
      rect.x += rect.width + col * dayCellSize.width;
      rect.width = dayCellSize.width;
    }
    if (row != -1) {
      rect.y += rect.height + row * dayCellSize.height;
      rect.height = dayCellSize.height;
    }
    return rect;
  }

  /**
   * Returns cell text.
   *
   * @param row row index, value between -1 to 5
   * @param col col index, value between -1 to 6
   * @param calendar date of cell
   * @return cell text
   */
  protected String getCellText(int row, int col, Calendar calendar) {
    if (row == -1 && col == -1) {
      return weekNumberName;
    } else if (row == -1) {
      return weekdayNames[col];
    } else if (col == -1) {
      return String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
    } else {
      return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }
  }

  /**
   * paint cell
   *
   * @param g2 graphic object
   * @param rect cell rect
   * @param foreground foreground color
   * @param background background color
   * @param cellText cell text
   */
  protected void paintCalendarCell(Graphics2D g2, Rectangle rect,
          Color foreground, Color background, String cellText) {
    g2.setPaint(background);
    g2.fillRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
    g2.setPaint(foreground);
    PrintUtils.drawText(g2, cellText, rect, getCellBorder(),
            SwingConstants.CENTER, true);
  }

  /**
   * paint calendar pane border
   *
   * @param g2 graphic object
   */
  protected void paintCalendarPaneBorder(Graphics2D g2) {
    g2.setPaint(pane.getTitleBackground());
    g2.fill(titleRect);
    g2.setPaint(pane.getCellBorderColor());
    g2.drawRect(paintRect.x, paintRect.y,
            paintRect.width - 1, paintRect.height - 1);
    g2.drawRect(titleRect.x, titleRect.y,
            titleRect.width - 1, titleRect.height - 1);
  }
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="event handler relative...">
  /**
   * Returns date at point in pixel relative to pane letf top point.
   *
   * @param point pixel point
   * @return date at point or null if point no refer to valid date
   */
  protected Date dateAtPoint(Point point) {
    Rectangle rect = (Rectangle) leftTopCorner.clone();
    rect.translate(rect.width, rect.height);
    rect.setSize(dayCellSize.width * 7, dayCellSize.height * 6);
    if (rect.contains(point)) {
      int row = (point.y - rect.y) / dayCellSize.height;
      int col = (point.x - rect.x) / dayCellSize.width;
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(pane.getRolloverDate());
      calendar.set(Calendar.WEEK_OF_MONTH, 1);
      calendar.add(Calendar.WEEK_OF_YEAR, row);
      calendar.set(Calendar.DAY_OF_WEEK, col + 1);
      return calendar.getTime();
    }
    return null;
  }

  /**
   * return row and column as a Point object of specified date
   *
   * @param date date
   * @return row and column as Point object, or null if date invisible
   */
  protected Point positionOfDate(Date date) {
    Point pos = new Point();
    Calendar first = new GregorianCalendar();
    first.setTime(pane.getRolloverDate());
    first.set(Calendar.WEEK_OF_MONTH, 1);
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    pos.x = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    pos.y = OrchidUtils.weeksBetween(first.getTime(), date);
    if (pos.x >= 0 && pos.x < 7 && pos.y >= 0 && pos.y < 6) {
      return pos;
    } else {
      return null;
    }
  }

  /**
   * process rollover date change event
   *
   * @param oldRollover old rollover
   * @param newRollover new rollover
   */
  protected void rolloverChanged(Date oldRollover, Date newRollover) {
    if (oldRollover != null
            && OrchidUtils.monthsBetween(oldRollover, newRollover) == 0) {
      repaintDate(oldRollover);
      repaintDate(newRollover);
    } else {
      updateTitle(newRollover);
      pane.repaint();
    }
  }

  /**
   * process selected date change event
   *
   * @param oldSelected old selected date
   * @param newSelected new selected date
   */
  protected void selectedChanged(Date oldSelected, Date newSelected) {
    if (newSelected == null) {
      newSelected = new Date();
    }
    if (oldSelected != null
            && OrchidUtils.monthsBetween(oldSelected, newSelected) == 0) {
      repaintDate(oldSelected);
      repaintDate(newSelected);
    } else {
      updateTitle(newSelected);
      pane.repaint();
    }
  }

  /**
   * repaint specified date, no-op if date is null or invisible,
   *
   * @param date date that need to repaiint
   */
  protected void repaintDate(Date date) {
    if (date == null || leftTopCorner == null) {
      return;
    }
    Point pos = positionOfDate(date);
    if (pos != null) {
      pane.repaint(getCellRect(pos.y, pos.x));
    }
  }

  /*
   * change rollover date.
   *
   * @param year one of ROLL_PREV, ROLL_NEXT, ROLL_NONE or specified year value
   * @param month one of ROLL_PREV, ROLL_NEXT ROLL_NONE or specified month value
   * @param week one of ROLL_PREV、ROLL_NEXT、ROLL_NONE or specified week of year
   * @param day one of ROLL_PREV、ROLL_NEXT、ROLL_NONE orspecified day of month
   */
  private void rollDate(int year, int month, int week, int day) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(pane.getRolloverDate());
    rollDateField(calendar, Calendar.YEAR, year);
    rollDateField(calendar, Calendar.MONTH, month);
    rollDateField(calendar, Calendar.WEEK_OF_YEAR, week);
    rollDateField(calendar, Calendar.DAY_OF_MONTH, day);
    pane.setRolloverDate(calendar.getTime());
  }

  private void rollDateField(Calendar calendar, int field, int value) {
    if (value == ROLL_PREV) {
      calendar.add(field, -1);
    } else if (value == ROLL_NEXT) {
      calendar.add(field, 1);
    } else if (value != ROLL_NONE) {
      calendar.set(field, value);
    }
  }
  // </editor-fold>

  private class UIAction extends AbstractAction {

    private static final String ROLL_PREV_DAY = "rollPrevDay";
    private static final String ROLL_NEXT_DAY = "rollNextDay";
    private static final String ROLL_PREV_WEEK = "rollPrevWeek";
    private static final String ROLL_NEXT_WEEK = "rollNextWeek";
    private static final String SELECT_ROLL_DAY = "selectRollDay";

    public UIAction(String name) {
      super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      Object name = getValue(Action.NAME).toString();
      if (name == ROLL_PREV_DAY) {
        rollDate(ROLL_NONE, ROLL_NONE, ROLL_NONE, ROLL_PREV);
      } else if (name == ROLL_NEXT_DAY) {
        rollDate(ROLL_NONE, ROLL_NONE, ROLL_NONE, ROLL_NEXT);
      } else if (name == ROLL_PREV_WEEK) {
        rollDate(ROLL_NONE, ROLL_NONE, ROLL_PREV, ROLL_NONE);
      } else if (name == ROLL_NEXT_WEEK) {
        rollDate(ROLL_NONE, ROLL_NONE, ROLL_NEXT, ROLL_NONE);
      } else if (name == SELECT_ROLL_DAY) {
        pane.setSelectedDate(pane.getRolloverDate());
      }
    }
  }

  private class Handler implements PropertyChangeListener, MouseInputListener,
          KeyListener, LayoutManager {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      String pName = evt.getPropertyName();
      if (pName.equals("rolloverDate")) {
        rolloverChanged((Date) evt.getOldValue(), (Date) evt.getNewValue());
      } else if (pName.equals("selectedDate")) {
        selectedChanged((Date) evt.getOldValue(), (Date) evt.getNewValue());
      } else if (pName.equals("minimumYear")) {
        updateMinimumYear((Integer) evt.getNewValue());
      } else if (pName.equals("maximumYear")) {
        updateMaximumYear((Integer) evt.getNewValue());
      }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      if (e.getModifiers() != MouseEvent.BUTTON1_MASK) {
        return;
      }
      Date old = pane.getRolloverDate();
      Date date = dateAtPoint(e.getPoint());
      if (date != null && (OrchidUtils.monthsBetween(old, date) == 0
              || e.getClickCount() == 2)) {
        pane.setSelectedDate(date);
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(pane.getRolloverDate());
      Date date = dateAtPoint(e.getPoint());
      if (date != null) {
        Calendar rollover = new GregorianCalendar();
        rollover.setTime(date);
        if (isSameMonth(calendar, rollover)) {
          pane.setRolloverDate(date);
        }
      }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
      if (e.getModifiers() == 0) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
          rollDate(ROLL_NONE, ROLL_NONE, ROLL_NONE, ROLL_PREV);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
          rollDate(ROLL_NONE, ROLL_NONE, ROLL_NONE, ROLL_NEXT);
        }
      }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
      return null;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
      return null;
    }

    @Override
    public void layoutContainer(Container parent) {
      Insets insets = pane.getInsets();
      Insets bi = getCellBorder().getTotalInsets();
      Dimension gSize = tlbGoto.getPreferredSize();
      int height = calcTitleHeight();
      int x = insets.left;
      int y = insets.top + (height - gSize.height) / 2;
      tlbGoto.setBounds(x, y, gSize.width, gSize.height);
      Dimension tSize = pnTitle.getPreferredSize();
      x = (pane.getWidth() - tSize.width) / 2;
      x = Math.max(tlbGoto.getX() + gSize.width, x);
      y = insets.top + (height - tSize.height) / 2;
      pnTitle.setBounds(x, y, tSize.width, tSize.height);
      calcPaintRects();
    }
  }
}
