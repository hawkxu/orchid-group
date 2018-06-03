package com.xzq.osc;

import java.util.GregorianCalendar;

/**
 * Use eleworld.com arithmetic.<br> Copyright (c) 2008-2010 by Mr. Xu Zi
 * Qiang<br> China Lunar Calendar - usable between year 1901 and 2100.
 *
 *
 * @author Xu Zi Qiang
 */
public class OrchidLunarCalendar extends GregorianCalendar {

  private static String[] stemNames = {"甲", "乙", "丙", "丁", "戊", "己",
    "庚", "辛", "壬", "癸"};
  private static String[] branchNames = {"子", "丑", "寅", "卯", "辰", "巳",
    "午", "未", "申", "酉", "戌", "亥"};
  private static String[] animalNames = {"鼠", "牛", "虎", "兔", "龙", "蛇",
    "马", "羊", "猴", "鸡", "狗", "猪"};
  private static String[] cMonthNames = {"正月", "二月", "三月", "四月",
    "五月", "六月", "七月", "八月", "九月", "十月", "冬月", "腊月"};
  private static String[] cDayNames = {"初一", "初二", "初三", "初四", "初五",
    "初六", "初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四",
    "十五", "十六", "十七", "十八", "十九", "廿十", "廿一", "廿二", "廿三",
    "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"};
  private static int[] chineseMonths = {
    // Lunar big/small month flag compact table, two bytes for one year.
    // first 4 digits for leap month and following 12 digits for 12 month.
    0x00, 0x04, 0xad, 0x08, 0x5a, 0x01, 0xd5, 0x54, 0xb4, 0x09, 0x64, 0x05,
    0x59, 0x45, 0x95, 0x0a, 0xa6, 0x04, 0x55, 0x24, 0xad, 0x08, 0x5a, 0x62,
    0xda, 0x04, 0xb4, 0x05, 0xb4, 0x55, 0x52, 0x0d, 0x94, 0x0a, 0x4a, 0x2a,
    0x56, 0x02, 0x6d, 0x71, 0x6d, 0x01, 0xda, 0x02, 0xd2, 0x52, 0xa9, 0x05,
    0x49, 0x0d, 0x2a, 0x45, 0x2b, 0x09, 0x56, 0x01, 0xb5, 0x20, 0x6d, 0x01,
    0x59, 0x69, 0xd4, 0x0a, 0xa8, 0x05, 0xa9, 0x56, 0xa5, 0x04, 0x2b, 0x09,
    0x9e, 0x38, 0xb6, 0x08, 0xec, 0x74, 0x6c, 0x05, 0xd4, 0x0a, 0xe4, 0x6a,
    0x52, 0x05, 0x95, 0x0a, 0x5a, 0x42, 0x5b, 0x04, 0xb6, 0x04, 0xb4, 0x22,
    0x6a, 0x05, 0x52, 0x75, 0xc9, 0x0a, 0x52, 0x05, 0x35, 0x55, 0x4d, 0x0a,
    0x5a, 0x02, 0x5d, 0x31, 0xb5, 0x02, 0x6a, 0x8a, 0x68, 0x05, 0xa9, 0x0a,
    0x8a, 0x6a, 0x2a, 0x05, 0x2d, 0x09, 0xaa, 0x48, 0x5a, 0x01, 0xb5, 0x09,
    0xb0, 0x39, 0x64, 0x05, 0x25, 0x75, 0x95, 0x0a, 0x96, 0x04, 0x4d, 0x54,
    0xad, 0x04, 0xda, 0x04, 0xd4, 0x44, 0xb4, 0x05, 0x54, 0x85, 0x52, 0x0d,
    0x92, 0x0a, 0x56, 0x6a, 0x56, 0x02, 0x6d, 0x02, 0x6a, 0x41, 0xda, 0x02,
    0xb2, 0xa1, 0xa9, 0x05, 0x49, 0x0d, 0x0a, 0x6d, 0x2a, 0x09, 0x56, 0x01,
    0xad, 0x50, 0x6d, 0x01, 0xd9, 0x02, 0xd1, 0x3a, 0xa8, 0x05, 0x29, 0x85,
    0xa5, 0x0c, 0x2a, 0x09, 0x96, 0x54, 0xb6, 0x08, 0x6c, 0x09, 0x64, 0x45,
    0xd4, 0x0a, 0xa4, 0x05, 0x51, 0x25, 0x95, 0x0a, 0x2a, 0x72, 0x5b, 0x04,
    0xb6, 0x04, 0xac, 0x52, 0x6a, 0x05, 0xd2, 0x0a, 0xa2, 0x4a, 0x4a, 0x05,
    0x55, 0x94, 0x2d, 0x0a, 0x5a, 0x02, 0x75, 0x61, 0xb5, 0x02, 0x6a, 0x03,
    0x61, 0x45, 0xa9, 0x0a, 0x4a, 0x05, 0x25, 0x25, 0x2d, 0x09, 0x9a, 0x68,
    0xda, 0x08, 0xb4, 0x09, 0xa8, 0x59, 0x54, 0x03, 0xa5, 0x0a, 0x91, 0x3a,
    0x96, 0x04, 0xad, 0xb0, 0xad, 0x04, 0xda, 0x04, 0xf4, 0x62, 0xb4, 0x05,
    0x54, 0x0b, 0x44, 0x5d, 0x52, 0x0a, 0x95, 0x04, 0x55, 0x22, 0x6d, 0x02,
    0x5a, 0x71, 0xda, 0x02, 0xaa, 0x05, 0xb2, 0x55, 0x49, 0x0b, 0x4a, 0x0a,
    0x2d, 0x39, 0x36, 0x01, 0x6d, 0x80, 0x6d, 0x01, 0xd9, 0x02, 0xe9, 0x6a,
    0xa8, 0x05, 0x29, 0x0b, 0x9a, 0x4c, 0xaa, 0x08, 0xb6, 0x08, 0xb4, 0x38,
    0x6c, 0x09, 0x54, 0x75, 0xd4, 0x0a, 0xa4, 0x05, 0x45, 0x55, 0x95, 0x0a,
    0x9a, 0x04, 0x55, 0x44, 0xb5, 0x04, 0x6a, 0x82, 0x6a, 0x05, 0xd2, 0x0a,
    0x92, 0x6a, 0x4a, 0x05, 0x55, 0x0a, 0x2a, 0x4a, 0x5a, 0x02, 0xb5, 0x02,
    0xb2, 0x31, 0x69, 0x03, 0x31, 0x73, 0xa9, 0x0a, 0x4a, 0x05, 0x2d, 0x55,
    0x2d, 0x09, 0x5a, 0x01, 0xd5, 0x48, 0xb4, 0x09, 0x68, 0x89, 0x54, 0x0b,
    0xa4, 0x0a, 0xa5, 0x6a, 0x95, 0x04, 0xad, 0x08, 0x6a, 0x44, 0xda, 0x04,
    0x74, 0x05, 0xb0, 0x25, 0x54, 0x03};
  // first valid calendar date
  // greg date 1901-01-01 refer to lunar date 4597-11-11
  private static int baseYear = 1901;
  private static int baseChineseYear = 4597;
  // array of year with big leap month
  private static int[] bigLeapMonthYears = {
    6, 14, 19, 25, 33, 36, 38, 41, 44, 52, 55, 79, 117, 136, 147, 150, 155,
    158, 185, 193};
  /**
   * flag for heavenly names, use in get and getName function
   */
  public final static int C_STEM = FIELD_COUNT;
  /**
   * flag for earthly names, use in get and getName function
   */
  public final static int C_BRANCH = FIELD_COUNT + 1;
  /**
   * flag for Chinese zodiac names, use in get and getName function
   */
  public final static int C_ANIMAL = FIELD_COUNT + 2;
  /**
   * flag for lunar year number, use in get and getName function
   */
  public final static int C_YEAR = FIELD_COUNT + 3;
  /**
   * flag for lunar month number, use in get getName add and set function
   */
  public final static int C_MONTH = FIELD_COUNT + 4;
  /**
   * flag for lunar day number, use in get getName add and set function
   */
  public final static int C_DAY = FIELD_COUNT + 5;
  /**
   * flag for the 24 solar terms , use in get and getName function
   */
  public final static int C_TERM = FIELD_COUNT + 6;
  /**
   * lunar date field count
   */
  public final static int C_FIELDCOUNT = 7;
  private int[] cFields = new int[C_FIELDCOUNT + FIELD_COUNT];
  private String[] cNames = new String[C_FIELDCOUNT + FIELD_COUNT];

  /**
   * Constructor for OrchidLunarCalendar with date today.
   */
  public OrchidLunarCalendar() {
    super();
    computeCFields();
  }

  /**
   * clone a lunar calendar object.
   *
   * @return A new OrchidLunarCalendar object.
   */
  @Override
  public Object clone() {
    OrchidLunarCalendar n = (OrchidLunarCalendar) super.clone();
    n.cFields = cFields.clone();
    n.cNames = cNames.clone();
    return n;
  }

  /**
   * caculate Chinese date fields.
   */
  @Override
  protected void computeFields() {
    super.computeFields();
    if (cFields != null) {
      computeCFields();
    }
  }

  /**
   * caculate Chinese date fields in add function.
   *
   * @see GregorianCalendar#add(int, int)
   */
  @Override
  public void add(int field, int amount) {
    if (field >= C_YEAR && field <= C_DAY) {
      switch (field) {
        case C_YEAR:
          setCNDate(cFields[C_YEAR] + amount, cFields[C_MONTH], cFields[C_DAY]);
          break;
        case C_MONTH:
          int cYear = cFields[C_YEAR];
          int cMonth = cFields[C_MONTH];
          int tmpMonth;
          while (amount != 0) {
            if (amount > 0) {
              amount--;
              tmpMonth = cMonth;
              cMonth = nextChineseMonth(cYear, cMonth);
              if (Math.abs(cMonth) < Math.abs(tmpMonth)) {
                cYear++;
              }
            } else {
              amount++;
              tmpMonth = cMonth;
              cMonth = prevChineseMonth(cYear, cMonth);
              if (Math.abs(cMonth) > Math.abs(tmpMonth)) {
                cYear--;
              }
            }
            setCNDate(cYear, cMonth, cFields[C_DAY]);
          }
          break;
        default:
          super.add(DAY_OF_MONTH, amount);
          break;
      }
    } else {
      super.add(field, amount);
    }
  }

  /**
   * override for return Chinese date field for get
   *
   * @see java.util.Calendar#get(int)
   */
  @Override
  public int get(int field) {
    complete();
    if (field >= C_STEM && field < C_TERM + C_FIELDCOUNT) {
      return cFields[field];
    } else {
      return super.get(field);
    }
  }

  /**
   * Returns Chinese date field value name.
   *
   * @param field date field.
   * @return Chinese date field value name.
   */
  public String getName(int field) {
    complete();
    if (field >= C_STEM && field < C_TERM + C_FIELDCOUNT) {
      return cNames[field];
    } else {
      return "";
    }
  }

  /**
   * Returns current date's Chinese date style.
   *
   * @return current date's Chinese date style.
   */
  public String getChineseDate() {
    complete();
    String cDate = cNames[C_STEM] + cNames[C_BRANCH] + cNames[C_ANIMAL] + "年"
            + cNames[C_MONTH] + cNames[C_DAY];
    if (cFields[C_TERM] != 0) {
      cDate = cDate + " " + cNames[C_TERM];
    }
    return cDate;
  }

  /**
   * Sets calendar date use Chinese date value.
   *
   * @param cYear Chinese year number
   * @param cMonth Chinse month number
   * @param cDay Chinses day number
   */
  public void setCNDate(int cYear, int cMonth, int cDay) {
    String in = String.format("%04d%02d%02d", cYear, Math.abs(cMonth), cDay);
    if (in.compareTo("19001111") < 0 || in.compareTo("21001201") > 0) {
      throw new IllegalArgumentException("SetCNDate out of range [1900-2100].");
    }
    if (cMonth < 0 && daysInChineseMonth(cYear + 2697, cMonth) == 0) {
      throw new IllegalArgumentException("Year:" + cYear + " no Leap month:"
              + Math.abs(cMonth));
    }
    cYear += 2697;
    int scYear = baseChineseYear, scMonth = 11, scDay = 11;
    GregorianCalendar startGregDate = new GregorianCalendar(baseYear, 0, 1);
    if (in.compareTo("46961125") >= 0) {
      scYear = 4696;
      scMonth = 11;
      scDay = 25;
      startGregDate.set(2000, 0, 1);
    }
    int totalDays = 0, tmpMonth;
    while (scYear < cYear || (scYear == cYear && scMonth < Math.abs(cMonth))) {
      totalDays += daysInChineseMonth(scYear, scMonth) - scDay;
      tmpMonth = scMonth;
      scMonth = nextChineseMonth(scYear, scMonth);
      scDay = 0;
      if (Math.abs(scMonth) < Math.abs(tmpMonth)) {
        scYear++;
      }
    }
    if (cMonth < 0) {
      totalDays += daysInChineseMonth(cYear, cMonth);
    }
    totalDays += cDay - scDay;
    startGregDate.add(DAY_OF_MONTH, totalDays);
    setTime(startGregDate.getTime());
  }

  /**
   *
   */
  protected void clearCFields() {
    for (int i = C_STEM; i < C_STEM + C_FIELDCOUNT; i++) {
      cFields[i] = 0;
      cNames[i] = "";
    }
  }

  /**
   * 
   */
  protected void computeCFields() {
    clearCFields();
    int gYear = get(YEAR);
    if (gYear < baseYear || gYear > 2100) {
      return;
    }

    // first valid calendar date
    // greg date 1901-01-01 refer to lunar date 4597-11-11
    GregorianCalendar startGregDate = new GregorianCalendar(baseYear, 0, 1);
    cFields[C_YEAR] = baseChineseYear;
    cFields[C_MONTH] = 11;
    cFields[C_DAY] = 11;

    // second mapped calendar date, for improve efficiency
    // greg date 2000-01-01 refre to lunar date 4696-11-25
    if (gYear >= 2000) {
      startGregDate.set(2000, 0, 1);
      cFields[C_YEAR] += 99;
      cFields[C_MONTH] = 11;
      cFields[C_DAY] = 25;
    }
    int daysDiff = OrchidUtils.daysBetween(startGregDate.getTime(), getTime());

    cFields[C_DAY] += daysDiff;
    int nextDayInMonth = daysInChineseMonth(cFields[C_YEAR], cFields[C_MONTH]);
    int nextMonth = nextChineseMonth(cFields[C_YEAR], cFields[C_MONTH]);
    while (cFields[C_DAY] > nextDayInMonth) {
      if (Math.abs(nextMonth) < Math.abs(cFields[C_MONTH])) {
        cFields[C_YEAR]++;
      }
      cFields[C_MONTH] = nextMonth;
      cFields[C_DAY] -= nextDayInMonth;
      nextDayInMonth = daysInChineseMonth(cFields[C_YEAR], cFields[C_MONTH]);
      nextMonth = nextChineseMonth(cFields[C_YEAR], cFields[C_MONTH]);
    }
    cFields[C_STEM] = (cFields[C_YEAR] - 1) % 10 + 1;
    cFields[C_BRANCH] = (cFields[C_YEAR] - 1) % 12 + 1;
    cFields[C_ANIMAL] = cFields[C_BRANCH];
    // use lunar year reduced 2697 refer to greg year
    cFields[C_YEAR] -= 2697;
    // the 24 solar terms is caculate base greg date
    if (get(DAY_OF_MONTH) == getFirstTermDay(get(YEAR), get(MONTH) + 1)) {
      cFields[C_TERM] = get(MONTH) * 2 + 1;
    } else if (get(DAY_OF_MONTH) == getSecondTermDay(get(YEAR), get(MONTH) + 1)) {
      cFields[C_TERM] = get(MONTH) * 2 + 2;
    }
    cNames[C_STEM] = stemNames[cFields[C_STEM] - 1];
    cNames[C_BRANCH] = branchNames[cFields[C_BRANCH] - 1];
    cNames[C_ANIMAL] = animalNames[cFields[C_ANIMAL] - 1];
    cNames[C_YEAR] = String.valueOf(cFields[C_YEAR]);
    cNames[C_MONTH] = getChineseMonthName(cFields[C_MONTH]);
    cNames[C_DAY] = getChineseDayName(cFields[C_DAY]);
    if (cFields[C_TERM] != 0) {
      cNames[C_TERM] = solarTermNames[cFields[C_TERM] - 1];
    }
  }

  // get day count in Chinese month
  private static int daysInChineseMonth(int cYear, int cMonth) {
    // note: leap month less than zero
    int index = cYear - baseChineseYear;
    int v, l, days = 30;
    if (cMonth >= 1 && cMonth <= 8) {
      v = chineseMonths[2 * index];
      l = cMonth - 1;
      if (((v >> l) & 0x01) == 1) {
        days = 29;
      }
    } else if (cMonth >= 9 && cMonth <= 12) {
      v = chineseMonths[2 * index + 1];
      l = cMonth - 9;
      if (((v >> l) & 0x01) == 1) {
        days = 29;
      }
    } else {
      v = chineseMonths[2 * index + 1];
      v = (v >> 4) & 0x0F;
      days = 0;
      if (v == Math.abs(cMonth)) {
        days = 29;
        for (int i = 0; i < bigLeapMonthYears.length; i++) {
          if (bigLeapMonthYears[i] == index) {
            days = 30;
          }
        }
      }
    }
    return days;
  }

  // get next Chinese month number
  private static int nextChineseMonth(int year, int month) {
    int n = Math.abs(month) + 1;
    if (month > 0) {
      int index = year - baseChineseYear;
      int v = chineseMonths[2 * index + 1];
      v = (v >> 4) & 0x0F;
      if (v == month) {
        n = -month;
      }
    }
    if (n == 13) {
      n = 1;
    }
    return n;
  }

  // get previous Chinese month number
  private static int prevChineseMonth(int year, int month) {
    int n = Math.abs(month) + 1;
    if (month > 0) {
      int index = year - baseChineseYear;
      int v = chineseMonths[2 * index - 1];
      v = (v >> 4) & 0x0F;
      if (v == month) {
        n = -month;
      }
    }
    if (n == 13) {
      n = 1;
    }
    return n;
  }
  private static byte[][] firstTermMap = {
    {7, 6, 6, 6, 6, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 5, 5, 5, 5,
      5, 4, 5, 5},
    {5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 4, 4, 3, 3, 4,
      4, 3, 3, 3},
    {6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5,
      4, 5, 5, 5, 5},
    {5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4, 4, 5,
      4, 4, 4, 4, 5},
    {6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5,
      4, 5, 5, 5, 5},
    {6, 6, 7, 7, 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6,
      5, 5, 5, 5, 4, 5, 5, 5, 5},
    {7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6, 7, 7,
      6, 6, 6, 7, 7},
    {8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7,
      6, 7, 7, 7, 6, 6, 7, 7, 7},
    {8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7,
      6, 7, 7, 7, 7},
    {9, 9, 9, 9, 8, 9, 9, 9, 8, 8, 9, 9, 8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8,
      7, 7, 8, 8, 8},
    {8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7,
      6, 6, 7, 7, 7},
    {7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6, 7, 7,
      6, 6, 6, 7, 7}};
  private static int[][] firstTermYear = {
    {13, 49, 85, 117, 149, 185, 201, 250, 250},
    {13, 45, 81, 117, 149, 185, 201, 250, 250},
    {13, 48, 84, 112, 148, 184, 200, 201, 250},
    {13, 45, 76, 108, 140, 172, 200, 201, 250},
    {13, 44, 72, 104, 132, 168, 200, 201, 250},
    {5, 33, 68, 96, 124, 152, 188, 200, 201},
    {29, 57, 85, 120, 148, 176, 200, 201, 250},
    {13, 48, 76, 104, 132, 168, 196, 200, 201},
    {25, 60, 88, 120, 148, 184, 200, 201, 250},
    {16, 44, 76, 108, 144, 172, 200, 201, 250},
    {28, 60, 92, 124, 160, 192, 200, 201, 250},
    {17, 53, 85, 124, 156, 188, 200, 201, 250}};
  private static byte[][] secondTermMap = {
    {21, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 20, 20,
      20, 20, 20, 19, 20, 20, 20, 19, 19, 20},
    {20, 19, 19, 20, 20, 19, 19, 19, 19, 19, 19, 19, 19, 18, 19, 19, 19, 18,
      18, 19, 19, 18, 18, 18, 18, 18, 18, 18},
    {21, 21, 21, 22, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 20, 20,
      20, 21, 20, 20, 20, 20, 19, 20, 20, 20, 20},
    {20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 20, 20, 20, 20, 19, 20,
      20, 20, 19, 19, 20, 20, 19, 19, 19, 20, 20},
    {21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21, 20, 21,
      21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 21},
    {22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22, 21, 21,
      21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 21},
    {23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23, 23, 23, 22, 22,
      23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 23},
    {23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23,
      23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23},
    {23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23,
      23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23},
    {24, 24, 24, 24, 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23,
      23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 23},
    {23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 22, 22,
      22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 22},
    {22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 21, 22, 22, 22, 21, 21,
      22, 22, 21, 21, 21, 22, 21, 21, 21, 21, 22}};
  private static int[][] secondTermYear = {{13, 45, 81, 113, 149, 185, 201},
    {21, 57, 93, 125, 161, 193, 201},
    {21, 56, 88, 120, 152, 188, 200, 201},
    {21, 49, 81, 116, 144, 176, 200, 201},
    {17, 49, 77, 112, 140, 168, 200, 201},
    {28, 60, 88, 116, 148, 180, 200, 201},
    {25, 53, 84, 112, 144, 172, 200, 201},
    {29, 57, 89, 120, 148, 180, 200, 201},
    {17, 45, 73, 108, 140, 168, 200, 201},
    {28, 60, 92, 124, 160, 192, 200, 201},
    {16, 44, 80, 112, 148, 180, 200, 201},
    {17, 53, 88, 120, 156, 188, 200, 201}};
  private static String[] solarTermNames = {"小寒", "大寒", "立春", "雨水",
    "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑",
    "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪",
    "大雪", "冬至"};

  // caculate solar term date in the first half of month
  private static int getFirstTermDay(int year, int month) {
    if (year < baseYear || year > 2100) {
      return 0;
    }
    int index = 0;
    int ry = year - baseYear + 1;
    while (ry >= firstTermYear[month - 1][index]) {
      index++;
    }
    int term = firstTermMap[month - 1][4 * index + ry % 4];
    if ((ry == 121) && (month == 4)) {
      term = 5;
    }
    if ((ry == 132) && (month == 4)) {
      term = 5;
    }
    if ((ry == 194) && (month == 6)) {
      term = 6;
    }
    return term;
  }

  // caculate solar term date in the second half of month
  private static int getSecondTermDay(int year, int month) {
    if (year < baseYear || year > 2100) {
      return 0;
    }
    int index = 0;
    int ry = year - baseYear + 1;
    while (ry >= secondTermYear[month - 1][index]) {
      index++;
    }
    int term = secondTermMap[month - 1][4 * index + ry % 4];
    if ((ry == 171) && (month == 3)) {
      term = 21;
    }
    if ((ry == 181) && (month == 5)) {
      term = 21;
    }
    return term;
  }

  /**
   * Returns solar term name in specified month.
   *
   * @param month month number
   * @param first true for the first half of month or false for the second half
   * of month.
   * @return solar term name
   */
  public static String getSolarTermName(int month, Boolean first) {
    if (first) {
      return solarTermNames[month * 2];
    } else {
      return solarTermNames[month * 2 + 1];
    }
  }

  /**
   * Returns Chinese day name for specified day.
   *
   * @param day day number
   * @return Chinese day name.
   */
  public static String getChineseDayName(int day) {
    return cDayNames[day - 1];
  }

  /**
   * Returns Chinese month name for specified month.
   *
   * @param month month number
   * @return Chinese month name.
   */
  public static String getChineseMonthName(int month) {
    return (month < 0 ? "闰" : "") + cMonthNames[Math.abs(month) - 1];
  }
}
