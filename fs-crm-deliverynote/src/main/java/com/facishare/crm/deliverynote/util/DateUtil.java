package com.facishare.crm.deliverynote.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    System.out.println("当天24点时间：" + getTimesTodayEndTime().toLocaleString());
    System.out.println("当前时间：" + new Date().toLocaleString());
    System.out.println("当天0点时间：" + getTimesTodayStartTime().toLocaleString());
    System.out.println("昨天0点时间：" + getYesterdayStartTime().toLocaleString());
    System.out.println("近7天时间：" + getWeekFromNow().toLocaleString());
    System.out.println("本周周一0点时间：" + getTimesThisWeekStartTime().toLocaleString());
    System.out.println("本周周日24点时间：" + getTimesThisWeekEndTime().toLocaleString());
    System.out.println("本月初0点时间：" + getTimesThisMonthStartTime().toLocaleString());
    System.out.println("本月未24点时间：" + getTimesThisMonthEndTime().toLocaleString());
    System.out.println("上月初0点时间：" + getLastMonthStartTime().toLocaleString());
    System.out.println("本季度开始点时间：" + getCurrentQuarterStartTime().toLocaleString());
    System.out.println("本季度结束点时间：" + getCurrentQuarterEndTime().toLocaleString());
    System.out.println("本年开始点时间：" + getCurrentYearStartTime().toLocaleString());
    System.out.println("本年结束点时间：" + getCurrentYearEndTime().toLocaleString());
    System.out.println("上年开始点时间：" + getLastYearStartTime().toLocaleString());
  }

  /**
   * 获得当天0点时间
   */
  public static Date getTimesTodayStartTime() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }

  /**
   * 获得昨天0点时间
   */
  public static Date getYesterdayStartTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(getTimesTodayStartTime().getTime() - 3600 * 24 * 1000);
    return cal.getTime();
  }

  /**
   * 获得昨天解决时间
   */
  public static Date getYesterdayEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(getTimesTodayStartTime().getTime() - 1);
    return cal.getTime();
  }

  /**
   * 获得明天0点时间
   */
  public static Date getTomorrowStartTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(getTimesTodayStartTime().getTime() + 3600 * 24 * 1000);
    return cal.getTime();
  }

  /**
   * 获得明天24点时间
   */
  public static Date getTomorrowEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(getTimesTodayStartTime().getTime() + 3600 * 24 * 1000 * 2 - 1);
    return cal.getTime();
  }

  /**
   * 获得当天近7天时间
   */
  public static Date getWeekFromNow() {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(getTimesTodayStartTime().getTime() - 3600 * 24 * 1000 * 7);
    return cal.getTime();
  }

  /**
   * 获得当天24点时间
   */
  public static Date getTimesTodayEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.MILLISECOND, 999);
    return cal.getTime();
  }

  /**
   * 获得本周一0点时间
   */
  public static Date getTimesThisWeekStartTime() {
    //    Calendar cal = Calendar.getInstance();
    //    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    //    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    //    return cal.getTime();
    Date date = new Date();
    if (date == null) {
      return null;
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
    if (dayofweek == 1) {
      dayofweek += 7;
    }
    cal.add(Calendar.DATE, 2 - dayofweek);
    return getDayStartTime(cal.getTime());
  }

  /**
   * 获得本周日24点时间
   */
  public static Date getTimesThisWeekEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getTimesThisWeekStartTime());
    cal.add(Calendar.DAY_OF_WEEK, 6);
    Date weekEndSta = cal.getTime();
    return getDayEndTime(weekEndSta);
  }

  /**
   * 获得上周一0点时间
   */
  public static Date getTimesLastWeekStartTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(getTimesThisWeekStartTime().getTime() - 3600 * 24 * 1000 * 7);
    return cal.getTime();
  }

  /**
   * 获得上周日24点时间
   */
  public static Date getTimesLastWeekEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getTimesLastWeekStartTime());
    cal.add(Calendar.DAY_OF_WEEK, 6);
    Date weekEndSta = cal.getTime();
    return getDayEndTime(weekEndSta);
  }

  /**
   * 获得下周一0点时间
   */
  public static Date getTimesNextWeekStartTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(getTimesThisWeekStartTime().getTime() + 3600 * 24 * 1000 * 7);
    return cal.getTime();
  }

  /**
   * 获得下周日24点时间
   */
  public static Date getTimesNextWeekEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getTimesNextWeekStartTime());
    cal.add(Calendar.DAY_OF_WEEK, 6);
    Date weekEndSta = cal.getTime();
    return getDayEndTime(weekEndSta);
  }

  /**
   * 获得本月第一天0点时间
   */
  public static Date getTimesThisMonthStartTime() {
    Calendar cal = Calendar.getInstance();
    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
    return cal.getTime();
  }

  /**
   * 获得本月最后一天24点时间
   */
  public static Date getTimesThisMonthEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    cal.set(Calendar.HOUR_OF_DAY, 24);
    return cal.getTime();
  }

  /**
   * 获得上月第一天0点时间
   */
  public static Date getLastMonthStartTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getTimesThisMonthStartTime());
    cal.add(Calendar.MONTH, -1);
    return cal.getTime();
  }

  /**
   * 获得上月最后一天24点时间
   */
  public static Date getLastMonthEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getTimesThisMonthStartTime());
    cal.add(Calendar.DATE, -1);
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.MILLISECOND, 999);
    return cal.getTime();
  }

  /**
   * 获得下月第一天0点时间
   */
  public static Date getNextMonthStartTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getTimesThisMonthStartTime());
    cal.add(Calendar.MONTH, +1);
    return cal.getTime();
  }

  /**
   * 获得下月最后一天24点时间
   */
  public static Date getNextMonthEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getTimesThisMonthStartTime());
    cal.add(Calendar.MONTH, +2);
    return cal.getTime();
  }

  /**
   * 当前季度的开始时间
   */
  public static Date getCurrentQuarterStartTime() {
    Calendar c = Calendar.getInstance();
    int currentMonth = c.get(Calendar.MONTH) + 1;
    SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
    Date now = null;
    try {
      if (currentMonth >= 1 && currentMonth <= 3) {
        c.set(Calendar.MONTH, 0);
      } else if (currentMonth >= 4 && currentMonth <= 6) {
        c.set(Calendar.MONTH, 3);
      } else if (currentMonth >= 7 && currentMonth <= 9) {
        c.set(Calendar.MONTH, 4);
      } else if (currentMonth >= 10 && currentMonth <= 12) {
        c.set(Calendar.MONTH, 9);
      }
      c.set(Calendar.DATE, 1);
      now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return now;
  }

  /**
   * 当前季度的结束时间
   */
  public static Date getCurrentQuarterEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getCurrentQuarterStartTime());
    cal.add(Calendar.MONTH, 2);
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);
    return cal.getTime();
  }

  /**
   * 上季度开始时间
   */
  public static Date getLastQuarterStartTime() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.MONTH, ((int) cal.get(Calendar.MONTH) / 3 - 1) * 3);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }

  /**
   * 上季度结束时间
   */
  public static Date getLastQuarterEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MONTH, -2);
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);
    return cal.getTime();
  }

  /**
   * 当下季度的开始时间
   */
  public static Date getNextQuarterStartTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getCurrentQuarterStartTime());
    cal.add(Calendar.MONTH, 6);
    return cal.getTime();
  }

  /**
   * 当下季度的结束时间
   */
  public static Date getNextQuarterEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getCurrentQuarterStartTime());
    cal.add(Calendar.MONTH, 8);
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);
    return cal.getTime();
  }

  /**
   * 本年开始时间
   */
  public static Date getCurrentYearStartTime() {
    Calendar cal = Calendar.getInstance();
    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    cal.set(Calendar.MONTH, Calendar.JANUARY);
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.YEAR));
    return cal.getTime();
  }

  /**
   * 本年结束时间
   */
  public static Date getCurrentYearEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, getNowYear());
    cal.set(Calendar.MONTH, Calendar.DECEMBER);
    cal.set(Calendar.DATE, 31);
    return getDayEndTime(cal.getTime());
  }

  /**
   * 去年开始时间
   */
  public static Date getLastYearStartTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getCurrentYearStartTime());
    cal.add(Calendar.YEAR, -1);
    return cal.getTime();
  }

  /**
   * 去年结束时间
   */
  public static Date getLastYearEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getCurrentYearStartTime());
    cal.add(Calendar.YEAR, -1);
    cal.set(Calendar.MONTH, Calendar.DECEMBER);
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);
    return cal.getTime();
  }

  /**
   * 下一年开始时间
   */
  public static Date getNextYearStartTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getCurrentYearStartTime());
    cal.add(Calendar.YEAR, 1);
    return cal.getTime();
  }

  /**
   * 下一年结束时间
   */
  public static Date getNextYearEndTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getCurrentYearStartTime());
    cal.add(Calendar.YEAR, 1);
    cal.set(Calendar.MONTH, Calendar.DECEMBER);
    cal.set(Calendar.DATE, 31);
    return getDayEndTime(cal.getTime());
  }

  //获取某个日期的开始时间
  public static Timestamp getDayStartTime(Date d) {
    Calendar calendar = Calendar.getInstance();
    if (null != d) {
      calendar.setTime(d);
    }
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return new Timestamp(calendar.getTimeInMillis());
  }

  //获取某个日期的结束时间
  public static Timestamp getDayEndTime(Date d) {
    Calendar calendar = Calendar.getInstance();
    if (null != d) {
      calendar.setTime(d);
    }
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return new Timestamp(calendar.getTimeInMillis());
  }

  //获取今年是哪一年
  public static Integer getNowYear() {
    Date date = new Date();
    GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
    gc.setTime(date);
    return Integer.valueOf(gc.get(1));
  }

  //获取本月是哪一月
  public static int getNowMonth() {
    Date date = new Date();
    GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
    gc.setTime(date);
    return gc.get(2) + 1;
  }

  //两个日期相减得到的天数
  public static int getDiffDays(Date beginDate, Date endDate) {

    if (beginDate == null || endDate == null) {
      throw new IllegalArgumentException("getDiffDays param is null!");
    }

    long diff = (endDate.getTime() - beginDate.getTime()) / (1000 * 60 * 60 * 24);

    int days = new Long(diff).intValue();

    return days;
  }

  //两个日期相减得到的毫秒数
  public static long dateDiff(Date beginDate, Date endDate) {
    long date1ms = beginDate.getTime();
    long date2ms = endDate.getTime();
    return date2ms - date1ms;
  }

  //获取两个日期中的最大日期
  public static Date max(Date beginDate, Date endDate) {
    if (beginDate == null) {
      return endDate;
    }
    if (endDate == null) {
      return beginDate;
    }
    if (beginDate.after(endDate)) {
      return beginDate;
    }
    return endDate;
  }

  //获取两个日期中的最小日期
  public static Date min(Date beginDate, Date endDate) {
    if (beginDate == null) {
      return endDate;
    }
    if (endDate == null) {
      return beginDate;
    }
    if (beginDate.after(endDate)) {
      return endDate;
    }
    return beginDate;
  }

  //返回某月该季度的第一个月
  public static Date getFirstSeasonDate(Date date) {
    final int[] SEASON = {1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4};
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int sean = SEASON[cal.get(Calendar.MONTH)];
    cal.set(Calendar.MONTH, sean * 3 - 3);
    return cal.getTime();
  }

  //返回某个日期下几天的日期
  public static Date getNextDay(Date date, int i) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    cal.set(Calendar.DATE, cal.get(Calendar.DATE) + i);
    return cal.getTime();
  }

  //返回某个日期前几天的日期
  public static Date getFrontDay(Date date, int i) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    cal.set(Calendar.DATE, cal.get(Calendar.DATE) - i);
    return cal.getTime();
  }

  //获取某年某月到某年某月按天的切片日期集合（间隔天数的集合）
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static List getTimeList(int beginYear, int beginMonth, int endYear, int endMonth, int k) {
    List list = new ArrayList();
    if (beginYear == endYear) {
      for (int j = beginMonth; j <= endMonth; j++) {
        list.add(getTimeList(beginYear, j, k));

      }
    } else {
      {
        for (int j = beginMonth; j < 12; j++) {
          list.add(getTimeList(beginYear, j, k));
        }

        for (int i = beginYear + 1; i < endYear; i++) {
          for (int j = 0; j < 12; j++) {
            list.add(getTimeList(i, j, k));
          }
        }
        for (int j = 0; j <= endMonth; j++) {
          list.add(getTimeList(endYear, j, k));
        }
      }
    }
    return list;
  }

  //获取某年某月按天切片日期集合（某个月间隔多少天的日期集合）
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static List getTimeList(int beginYear, int beginMonth, int k) {
    List list = new ArrayList();
    Calendar begincal = new GregorianCalendar(beginYear, beginMonth, 1);
    int max = begincal.getActualMaximum(Calendar.DATE);
    for (int i = 1; i < max; i = i + k) {
      list.add(begincal.getTime());
      begincal.add(Calendar.DATE, k);
    }
    begincal = new GregorianCalendar(beginYear, beginMonth, max);
    list.add(begincal.getTime());
    return list;
  }

}  