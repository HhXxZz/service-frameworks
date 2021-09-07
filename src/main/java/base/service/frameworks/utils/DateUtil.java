package base.service.frameworks.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static java.time.temporal.ChronoUnit.*;

/**
 * Created by someone on 2017-02-23.
 * <p>
 * 日期工具类
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DateUtil {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final DateTimeFormatter SF_DateTime               = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SF_DateTimeMills          = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter SF_Date                   = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter SF_Date_CN                = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    private static final DateTimeFormatter SF_Month_CN               = DateTimeFormatter.ofPattern("yyyy年MM月");
    private static final DateTimeFormatter SF_Date_Hour_Minute_CN    = DateTimeFormatter.ofPattern("yyyy年M月d日HH:mm");
    private static final DateTimeFormatter SF_Month                  = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter SF_DateTimeNoSeparator    = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter SF_Hour                   = DateTimeFormatter.ofPattern("HH");
    private static final DateTimeFormatter SF_Time                   = DateTimeFormatter.ofPattern("HH:mm:ss");
    // iso 8601
    private static final DateTimeFormatter SF_DateTime_iso8601       = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final DateTimeFormatter SF_DateTime_iso8601_Mills = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * 比较两个日期大小，日期格式需要yyyy-MM-dd格式
     *
     * @param pDateA 日期A
     * @param pDateB 日期B
     * @return 1 dateA < dataB; 0 dateA == dateB; -1 dateA > dateB
     */
    public static int compareDate(String pDateA, String pDateB) {
        if (!isDate(pDateA) || !isDate(pDateB)) {
            throw new RuntimeException("时间格式不正确，需要yyyy-MM-dd格式");
        }
        long tsA = getTimestamp(pDateA);
        long tsB = getTimestamp(pDateB);
        return Long.compare(tsB, tsA);
    }

    /**
     * 比较两个时间大小，日期格式需要yyyy-MM-dd hh:mm:ss格式
     *
     * @param pDateTimeA 时间A
     * @param pDateTimeB 时间B
     * @return 1 pDateTimeA < pDateTimeB; 0 pDateTimeA == pDateTimeB; -1 pDateTimeA > pDateTimeB
     */
    public static int compareDateTime(String pDateTimeA, String pDateTimeB) {
        if (!isDateTime(pDateTimeA) || !isDateTime(pDateTimeB)) {
            throw new RuntimeException("时间格式不正确，需要yyyy-MM-dd格式");
        }
        long tsA = getTimestamp(pDateTimeA);
        long tsB = getTimestamp(pDateTimeB);
        return Long.compare(tsB, tsA);
    }

    /**
     * 格式化日期
     *
     * @param pDate   日期long
     * @param pFormat 格式化
     * @return 格式化日期字符串
     */
    public static String formatDate(long pDate, String pFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pFormat);
        return formatter.format(Instant.ofEpochMilli(pDate).atZone(ZoneId.systemDefault()));
    }

    /**
     * 获取时间字符串
     *
     * @return yyyyMMddHHmmss
     */
    public static String getDateTimeStringNoSeparator() {
        return SF_DateTimeNoSeparator.format(LocalDateTime.now());
    }

    /**
     * 获取时间字符串
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getDateTimeStringISO8601() {
        return SF_DateTime_iso8601.format(LocalDateTime.now());
    }

    /**
     * 获取时间字符串
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getDateTimeString() {
        return SF_DateTime.format(LocalDateTime.now());
    }

    /**
     * 获取时间字符串
     *
     * @param pDateTime 时间对象
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getDateTimeString(LocalDateTime pDateTime) {
        return SF_DateTime.format(pDateTime);
    }

    /**
     * 获取时间字符串
     *
     * @param pTime 时间戳
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getDateTimeString(Long pTime) {
        return SF_DateTime.format(Instant.ofEpochMilli(pTime).atZone(ZoneId.systemDefault()));
    }

    /**
     * 获取时间字符串
     *
     * @param pOffsetDays 当前日期的偏移天数
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getDateTimeString(int pOffsetDays) {
        return SF_DateTime.format(LocalDateTime.now().plusDays(pOffsetDays));
    }

    /**
     * 获取时间字符串
     *
     * @return yyyy-MM-dd
     */
    public static String getDateString() {
        return SF_Date.format(LocalDate.now());
    }

    /**
     * 获取时间字符串
     *
     * @param pTime 时间戳
     * @return yyyy-MM-dd
     */
    public static String getDateString(Long pTime) {
        return SF_Date.format(Instant.ofEpochMilli(pTime).atZone(ZoneId.systemDefault()));
    }

    /**
     * 获取时间字符串
     *
     * @param pDate 时间对象
     * @return yyyy-MM-dd
     */
    public static String getDateString(LocalDate pDate) {
        return SF_Date.format(pDate);
    }

    /**
     * 获取时间字符串
     *
     * @param pOffsetDays 加减天数值
     * @return yyyy-MM-dd
     */
    public static String getDateString(int pOffsetDays) {
        return SF_Date.format(LocalDate.now().plusDays(pOffsetDays));
    }

    /**
     * 获取时间字符串
     *
     * @return yyyy年MM月dd日
     */
    public static String getDateStringCN() {
        return SF_Date_CN.format(LocalDate.now());
    }

    /**
     * 获取时间字符串
     *
     * @param pTime 时间戳
     * @return yyyy年MM月dd日
     */
    public static String getDateStringCN(Long pTime) {
        return SF_Date_CN.format(Instant.ofEpochMilli(pTime).atZone(ZoneId.systemDefault()));
    }

    /**
     * 获取时间字符串
     *
     * @param pDate 时间对象
     * @return yyyy年MM月dd日
     */
    public static String getDateStringCN(LocalDate pDate) {
        return SF_Date_CN.format(pDate);
    }

    /**
     * 获取时间字符串
     *
     * @param pOffsetDays 加减天数值
     * @return yyyy年MM月dd日
     */
    public static String getDateStringCN(int pOffsetDays) {
        return SF_Date_CN.format(LocalDate.now().plusDays(pOffsetDays));
    }

    /**
     * 分钟偏移
     *
     * @param pOffsetMinutes 加减分钟数值
     * @return yyyy-MM-dd hh:mm:ss
     */
    public static String getDateTimeString(String pDateTime, int pOffsetMinutes) {
        return SF_DateTime.format(LocalDateTime.parse(pDateTime, SF_DateTime).plusMinutes(pOffsetMinutes));
    }

    /**
     * 获取时间字符串
     *
     * @param pTimestamp 时间戳
     * @return yyyy-MM-dd HH:mm:ss.SSS
     */
    public static String getDateTimeMills(long pTimestamp) {
        return SF_DateTimeMills.format(Instant.ofEpochMilli(pTimestamp).atZone(ZoneId.systemDefault()));
    }

    /**
     * 获取时间字符串
     *
     * @return yyyy-MM-dd HH:mm:ss.SSS
     */
    public static String getDateTimeMills() {
        return SF_DateTimeMills.format(LocalDateTime.now());
    }

    /**
     * 获取月份字符串
     *
     * @return yyyy-MM
     */
    public static String getMonthString() {
        return SF_Month.format(LocalDate.now());
    }

    /**
     * 获取月份字符串
     *
     * @return yyyy年MM月
     */
    public static String getMonthCNString() {
        return SF_Month_CN.format(LocalDate.now());
    }

    /**
     * 获取月份字符串
     *
     * @param date 时间对象
     * @return yyyy年MM月
     */
    public static String getMonthCNString(LocalDate date) {
        return SF_Month_CN.format(date);
    }

    /**
     * 获取月份字符串
     *
     * @return yyyy年M月d日HH:mm
     */
    public static String getDateHourMinuteCNString() {
        return SF_Date_Hour_Minute_CN.format(LocalDateTime.now());
    }

    /**
     * 获取月份字符串
     *
     * @param date 时间对象
     * @return yyyy年M月d日HH:mm
     */
    public static String getDateHourMinuteCNString(LocalDateTime date) {
        return SF_Date_Hour_Minute_CN.format(date);
    }

    /**
     * 获取月份字符串
     *
     * @param pTimestamp 时间戳
     * @return yyyy年M月d日HH:mm
     */
    public static String getDateHourMinuteCNString(long pTimestamp) {
        return SF_Date_Hour_Minute_CN.format(Instant.ofEpochMilli(pTimestamp).atZone(ZoneId.systemDefault()));
    }

    /**
     * 获取月份字符串
     *
     * @param date 时间对象
     * @return yyyy-MM
     */
    public static String getMonthString(LocalDate date) {
        return SF_Month.format(date);
    }

    /**
     * 获取月份字符串
     *
     * @param pOffsetMonths 月份偏移量
     * @return yyyy年MM月
     */
    public static String getMonthCNString(int pOffsetMonths) {
        return SF_Month_CN.format(LocalDate.now().plusMonths(pOffsetMonths));
    }

    /**
     * 获取月份字符串
     *
     * @param pOffsetMonths 月份偏移量
     * @return yyyy-MM
     */
    public static String getMonthString(int pOffsetMonths) {
        return SF_Month.format(LocalDate.now().plusMonths(pOffsetMonths));
    }

    /**
     * 获取月份第一天
     *
     * @param pOffset 月份偏移量
     * @return yyyy-MM-01
     */
    public static String getMonthStart(int pOffset) {
        LocalDate date = LocalDate.now().plusMonths(pOffset);
        return SF_Date.format(date.withDayOfMonth(1));
    }

    /**
     * 获取月份最后一天
     *
     * @param pOffset 月份偏移量
     * @return yyyy-MM-lastDay
     */
    public static String getMonthEnd(int pOffset) {
        LocalDate date = LocalDate.now().plusMonths(pOffset);
        return SF_Date.format(date.withDayOfMonth(date.lengthOfMonth()));
    }

    /**
     * 获取所给日期(yyyy-MM-dd)所属月份的第一天
     * 若所给参数不是合法日期，则返回当前月第一天
     *
     * @param pDate          给定日期
     * @param pDefaultOffset 月份偏移量
     * @return yyyy-MM-01
     */
    public static String getMonthStart(String pDate, int pDefaultOffset) {
        if (isDate(pDate)) {
            try {
                LocalDate time = LocalDate.parse(pDate, SF_Date);
                return SF_Date.format(time.withDayOfMonth(1));
            } catch (Exception ignored) {
            }
        }
        return getMonthStart(pDefaultOffset);
    }

    /**
     * 获取所给日期(yyyy-MM-dd)所属月份的最后一天
     * 若所给参数不是合法日期，则返回当前月最后一天
     *
     * @param pDate          给定日期
     * @param pDefaultOffset 月份偏移量
     * @return yyyy-MM-lastDay
     */
    public static String getMonthEnd(String pDate, int pDefaultOffset) {
        if (isDate(pDate)) {
            try {
                LocalDate time = LocalDate.parse(pDate, SF_Date);
                return SF_Date.format(time.withDayOfMonth(time.lengthOfMonth()));
            } catch (Exception ignored) {
            }
        }
        return getMonthEnd(pDefaultOffset);
    }

    /**
     * 增加分钟
     *
     * @param pDateTime 指定日期(yyyy-MM-dd HH:mm:ss)
     * @param pMinutes    增减的分钟
     * @return yyyy-MM-dd
     */
    public static String addMinutes(String pDateTime, int pMinutes) {
        if (isDateTime(pDateTime)) {
            try {
                LocalDateTime time = LocalDateTime.parse(pDateTime, SF_DateTime);
                return SF_DateTime.format(time.plus(pMinutes, MINUTES));
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    /**
     * 增加小时
     *
     * @param pDateTime 指定日期(yyyy-MM-dd HH:mm:ss)
     * @param pHours    增减的小时
     * @return yyyy-MM-dd
     */
    public static String addHours(String pDateTime, int pHours) {
        if (isDateTime(pDateTime)) {
            try {
                LocalDateTime time = LocalDateTime.parse(pDateTime, SF_DateTime);
                return SF_DateTime.format(time.plus(pHours, ChronoUnit.HOURS));
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    /**
     * 增加天数
     *
     * @param pDate 指定日期(yyyy-MM-dd)
     * @param pDays 增减的天数
     * @return yyyy-MM-dd,
     */
    public static String addDate(String pDate, int pDays) {
        if (isDate(pDate)) {
            try {
                LocalDate time = LocalDate.parse(pDate, SF_Date);
                return SF_Date.format(time.plusDays(pDays));
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    /**
     * 增加月数
     *
     * @param pDate  指定日期(yyyy-MM-dd)
     * @param pMonth 增减的月数
     * @return yyyy-MM-dd
     */
    public static String addMonth(String pDate, int pMonth) {
        if (isDate(pDate)) {
            try {
                LocalDate time = LocalDate.parse(pDate, SF_Date);
                return SF_Date.format(time.plusMonths(pMonth));
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    /**
     * 判断字符串str是否为yyyy-MM-dd HH:mm:ss.SSS格式
     *
     * @param pString 日期字符串
     * @return 是否为yyyy-MM-dd HH:mm:ss.SSS格式
     */
    public static boolean isDateTimeMills(String pString) {
        boolean isDataTimeMills;
        try {
            LocalDateTime.parse(pString, SF_DateTimeMills);
            isDataTimeMills = true;
        } catch (Exception e) {
            isDataTimeMills = false;
        }
        return isDataTimeMills;
    }

    /**
     * 判断字符串str是否为yyyy-MM-dd'T'HH:mm:ss.SSS'Z'格式
     *
     * @param pString 日期字符串
     * @return 是否为yyyy-MM-dd'T'HH:mm:ss.SSS'Z'格式
     */
    public static boolean isDateTimeMillsISO8601(String pString) {
        boolean isDataTimeMills;
        try {
            LocalDateTime.parse(pString, SF_DateTime_iso8601_Mills);
            isDataTimeMills = true;
        } catch (Exception e) {
            isDataTimeMills = false;
        }
        return isDataTimeMills;
    }

    /**
     * 判断字符串str是否为yyyy-MM-dd'T'HH:mm:ss'Z'格式
     *
     * @param pString 日期字符串
     * @return 是否为yyyy-MM-dd'T'HH:mm:ss'Z'格式
     */
    public static boolean isDateTimeISO8601(String pString) {
        boolean isDataTime;
        try {
            LocalDateTime.parse(pString, SF_DateTime_iso8601);
            isDataTime = true;
        } catch (Exception e) {
            isDataTime = false;
        }
        return isDataTime;
    }

    /**
     * 判断字符串str是否为yyyy-MM-dd HH:mm:ss格式
     *
     * @param pString 日期字符串
     * @return 是否为yyyy-MM-dd HH:mm:ss格式
     */
    public static boolean isDateTime(String pString) {
        boolean isDataTime;
        try {
            LocalDateTime.parse(pString, SF_DateTime);
            isDataTime = true;
        } catch (Exception e) {
            isDataTime = false;
        }
        return isDataTime;
    }

    /**
     * 判断字符串str是否为yyyyMMddHHmmss格式
     *
     * @param pString 日期字符串
     * @return 是否为yyyy-MM-dd HH:mm:ss格式
     */
    public static boolean isDateTimeNoSeparator(String pString) {
        boolean isDataTime;
        try {
            LocalDateTime.parse(pString, SF_DateTimeNoSeparator);
            isDataTime = true;
        } catch (Exception e) {
            isDataTime = false;
        }
        return isDataTime;
    }

    /**
     * 判断字符串str是否为yyyy-MM-dd格式
     *
     * @param pString 日期字符串
     * @return 是否为yyyy-MM-dd格式
     */
    public static boolean isDate(String pString) {
        boolean isData;
        try {
            LocalDate.parse(pString, SF_Date);
            isData = true;
        } catch (Exception e) {
            isData = false;
        }
        return isData;
    }

    /**
     * 判断字符串str是否为HH格式
     *
     * @param pString 小时字符串
     * @return 是否为HH格式
     */
    public static boolean isHour(String pString) {
        boolean isHour;
        try {
            LocalTime.parse(pString, SF_Hour);
            isHour = true;
        } catch (Exception e) {
            isHour = false;
        }
        return isHour;
    }

    /**
     * 判断字符串str是否为HH:mm:ss格式
     *
     * @param pString 时间字符串
     * @return 是否为HH:mm:ss格式
     */
    public static boolean isTime(String pString) {
        boolean isTime;
        try {
            LocalTime.parse(pString, SF_Time);
            isTime = true;
        } catch (Exception e) {
            isTime = false;
        }
        return isTime;
    }

    /**
     * 获取当前时间字符串的时间戳
     *
     * @param pDateTime 日期字符串
     * @return 时间戳
     */
    public static long getTimestamp(String pDateTime) {
        if (isDateTime(pDateTime)) {
            try {
                return LocalDateTime.parse(pDateTime, SF_DateTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } catch (Exception ignored) {
            }
        } else if (isDate(pDateTime)) {
            try {
                return LocalDate.parse(pDateTime, SF_Date).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } catch (Exception ignored) {
            }
        } else if (isDateTimeMills(pDateTime)) {
            try {
                return LocalDateTime.parse(pDateTime, SF_DateTimeMills).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } catch (Exception ignored) {
            }
        }
        return 0;
    }

    /**
     * 获取当前时间对象的时间戳
     *
     * @param pDateTime 时间对象
     * @return 时间戳
     */
    public static long getTimestamp(LocalDateTime pDateTime) {
        return pDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取当前时间字符串的时间戳
     *
     * @param pDateTime 无分隔符时间字符串 yyyyMMddHHmmss
     * @return 时间戳
     */
    public static long getTimestampNoSeparator(String pDateTime) {
        try {
            return LocalDateTime.parse(pDateTime, SF_DateTimeNoSeparator).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception ignored) {
        }
        return 0;
    }

    /**
     * 获取当前时间字符串的时间戳
     *
     * @param pDateTime iso8601时间字符串 yyyy-MM-ddTHH:mm:ssZ
     * @return 时间戳
     */
    public static long getTimestampISO8601(String pDateTime) {
        try {
            return LocalDateTime.parse(pDateTime, SF_DateTime_iso8601).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception ignored) {
        }
        return 0;
    }

    /**
     * 获取当前时间字符串的时间戳
     *
     * @param pDateTime iso8601时间字符串 yyyy-MM-ddTHH:mm:ss.SSSZ
     * @return 时间戳
     */
    public static long getTimestampISO8601WithMills(String pDateTime) {
        try {
            return LocalDateTime.parse(pDateTime, SF_DateTime_iso8601_Mills).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception ignored) {
        }
        return 0;
    }

    /**
     * 求两个时间天数差
     *
     * @param pBeginTime 开始时间 yyyy-MM-dd
     * @param pEndTime   结束时间 yyyy-MM-dd
     * @return 时间差 n
     */
    public static long daysBetweenDates(String pBeginTime, String pEndTime) {
        return DAYS.between(LocalDate.parse(pBeginTime), LocalDate.parse(pEndTime));
    }

    @Deprecated
    public static long daysBetween(String pBeginTime, String pEndTime) {
        return daysBetweenDates(pBeginTime, pEndTime);
    }

    /**
     * 求两个时间天数差
     *
     * @param pBeginTime 开始时间 long
     * @param pEndTime   结束时间 long
     * @return 时间差 n
     */
    public static long daysBetweenTimestamps(long pBeginTime, long pEndTime) {
        return DAYS.between(Instant.ofEpochMilli(pBeginTime), Instant.ofEpochMilli(pEndTime));
    }

    @Deprecated
    public static long daysBetween(long pBeginTime, long pEndTime) {
        return daysBetweenTimestamps(pBeginTime, pEndTime);
    }

    /**
     * 求两个时间天数差
     *
     * @param pBeginTime 开始时间 yyyy-MM-dd hh:mm:ss
     * @param pEndTime   结束时间 yyyy-MM-dd hh:mm:ss
     * @return 时间差 n
     */
    public static long daysBetweenDateTimes(String pBeginTime, String pEndTime) {
        return DAYS.between(LocalDateTime.parse(pBeginTime, SF_DateTime), LocalDateTime.parse(pEndTime, SF_DateTime));
    }

    @Deprecated
    public static long dateTimesBetween(String pBeginTime, String pEndTime) {
        return daysBetweenDateTimes(pBeginTime, pEndTime);
    }

    /**
     * 求两个时间分钟差
     *
     * @param pBeginTime 开始时间 yyyy-MM-dd hh:mm:ss
     * @param pEndTime   结束时间 yyyy-MM-dd hh:mm:ss
     * @return 时间差 minutes
     */
    public static long minutesBetweenDateTimes(String pBeginTime, String pEndTime) {
        return MINUTES.between(LocalDateTime.parse(pBeginTime, SF_DateTime), LocalDateTime.parse(pEndTime, SF_DateTime));
    }

    @Deprecated
    public static long minutesBetween(String pBeginTime, String pEndTime) {
        return minutesBetweenDateTimes(pBeginTime, pEndTime);
    }

    /**
     * 求两个时间秒钟差
     *
     * @param pBeginTime 开始时间 yyyy-MM-dd hh:mm:ss
     * @param pEndTime   结束时间 yyyy-MM-dd hh:mm:ss
     * @return 时间差 minutes
     */
    public static long secondsBetweenDateTimes(String pBeginTime, String pEndTime) {
        return SECONDS.between(LocalDateTime.parse(pBeginTime, SF_DateTime), LocalDateTime.parse(pEndTime, SF_DateTime));
    }

    @Deprecated
    public static long secondsBetween(String pBeginTime, String pEndTime) {
        return secondsBetweenDateTimes(pBeginTime, pEndTime);
    }

    /**
     * 将字符串转化为Date对象
     *
     * @param pDateTime        接受(yyyy-MM-dd HH:mm:ss)格式的字符串
     * @param pDefaultDateTime 默认值
     * @return Date对象
     */
    public static LocalDateTime parseDateTime(String pDateTime, LocalDateTime pDefaultDateTime) {
        try {
            return LocalDateTime.parse(pDateTime, SF_DateTime);
        } catch (Exception e3) {
            return pDefaultDateTime;
        }
    }

    /**
     * 将字符串转化为Date对象
     *
     * @param pDateTime        接受(yyyyMMddHHmmss)格式的字符串
     * @param pDefaultDateTime 默认值
     * @return Date对象
     */
    public static LocalDateTime parseNoSeparatorDateTime(String pDateTime, LocalDateTime pDefaultDateTime) {
        try {
            return LocalDateTime.parse(pDateTime, SF_DateTimeNoSeparator);
        } catch (Exception e3) {
            return pDefaultDateTime;
        }
    }

    /**
     * 将字符串转化为Date对象
     *
     * @param pDate        接受(yyyy-MM-dd)格式的字符串
     * @param pDefaultDate 默认值
     * @return Date对象
     */
    public static LocalDate parseDate(String pDate, LocalDate pDefaultDate) {
        try {
            return LocalDate.parse(pDate, SF_Date);
        } catch (Exception e3) {
            return pDefaultDate;
        }
    }

    /**
     * 根据日期获得周几
     *
     * @param pDate 日期 2018-09-20
     * @return 周几
     */
    public static String getWeekOfDate(String pDate) {
        return DayOfWeek.from(LocalDate.parse(pDate, SF_Date)).getDisplayName(TextStyle.SHORT, Locale.CHINESE);
    }

    /**
     * 获取日期小时 0-23
     *
     * @param pDateTime yyyy-MM-dd HH:mm:ss
     * @return HH
     */
    public static int getHourOfDateTime(String pDateTime) {
        return LocalDateTime.parse(pDateTime, SF_DateTime).getHour();
    }

    @Deprecated
    public static int getHourOfDay(String pDateTime) {
        return getHourOfDateTime(pDateTime);
    }


    public static void main(String[] args) {
        System.out.println("compareDate(\"2020-05-09\", \"2020-05-09\") = " + compareDate("2020-05-09", "2020-05-09"));
        System.out.println("compareDate(\"2020-05-08\", \"2020-05-09\") = " + compareDate("2020-05-08", "2020-05-09"));
        System.out.println("compareDate(\"2020-05-10\", \"2020-05-09\") = " + compareDate("2020-05-10", "2020-05-09"));
        System.out.println("compareDateTime(\"2020-05-09 01:00:00\", \"2020-05-09 01:00:00\") = " + compareDateTime("2020-05-09 01:00:00", "2020-05-09 01:00:00"));
        System.out.println("compareDateTime(\"2020-05-08 00:59:59\", \"2020-05-09 01:00:00\") = " + compareDateTime("2020-05-09 00:59:59", "2020-05-09 01:00:00"));
        System.out.println("compareDateTime(\"2020-05-10 01:00:01\", \"2020-05-09 01:00:00\") = " + compareDateTime("2020-05-09 01:00:01", "2020-05-09 01:00:00"));
        System.out.println("format \"yyyy-MM-dd HH:mm:ss.SSS\" = " + formatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
        System.out.println("format \"yyyy-MM-dd\" = " + formatDate(System.currentTimeMillis(), "yyyy-MM-dd"));
        System.out.println("getDateTimeStringNoSeparator() = " + getDateTimeStringNoSeparator());
        System.out.println("getDateTimeStringISO8601() = " + getDateTimeStringISO8601());
        System.out.println("getDateTimeString() = " + getDateTimeString());
        System.out.println("getDateTimeString(System.currentTimeMillis()) = " + getDateTimeString(System.currentTimeMillis()));
        System.out.println("getDateTimeString(now +7 days) = " + getDateTimeString(7));
        System.out.println("getDateTimeString(now -30 days) = " + getDateTimeString(-30));
        System.out.println("getDateString() = " + getDateString());
        System.out.println("getDateString(System.currentTimeMillis()) = " + getDateString(System.currentTimeMillis()));
        System.out.println("getDateString(now +7 days) = " + getDateString(7));
        System.out.println("getDateString(now -30 days) = " + getDateString(-30));
        System.out.println("getDateStringCN() = " + getDateStringCN());
        System.out.println("getDateStringCN(System.currentTimeMillis()) = " + getDateStringCN(System.currentTimeMillis()));
        System.out.println("getDateStringCN(now +7 days) = " + getDateStringCN(7));
        System.out.println("getDateStringCN(now -30 days) = " + getDateStringCN(-30));
        System.out.println("getDateTimeString(2020-05-09 12:00:00 +117 minutes) = " + getDateTimeString("2020-05-09 12:00:00", 117));
        System.out.println("getDateTimeString(2020-05-09 12:00:00 -306 minutes) = " + getDateTimeString("2020-05-09 12:00:00", -306));
        System.out.println("getDateTimeMills(now -30 days) = " + getDateTimeMills(System.currentTimeMillis()));
        System.out.println("getDateTimeMills() = " + getDateTimeMills());
        System.out.println("getMonthString() = " + getMonthString());
        System.out.println("getMonthCNString() = " + getMonthCNString());
        System.out.println("getMonthCNString(now +7 months) = " + getMonthCNString(7));
        System.out.println("getMonthCNString(now -11 months) = " + getMonthCNString(-11));
        System.out.println("getDateHourMinuteCNString() = " + getDateHourMinuteCNString());
        System.out.println("getDateHourMinuteCNString(now) = " + getDateHourMinuteCNString(System.currentTimeMillis()));
        System.out.println("getMonthString(now +7 months) = " + getMonthString(7));
        System.out.println("getMonthString(now -11 months) = " + getMonthString(-11));
        System.out.println("getMonthStart(start of current month) = " + getMonthStart(0));
        System.out.println("getMonthStart(start of 7th month) = " + getMonthStart(7));
        System.out.println("getMonthStart(start of -11th current month) = " + getMonthStart(-11));
        System.out.println("getMonthEnd(end of current month) = " + getMonthEnd(0));
        System.out.println("getMonthEnd(end of 7th month) = " + getMonthEnd(7));
        System.out.println("getMonthEnd(end of -11th current month) = " + getMonthEnd(-11));
        System.out.println("addHours(\"2020-05-09 12:00:00\" +0) = " + addHours("2020-05-09 12:00:00", 0));
        System.out.println("addHours(\"2020-05-09 12:00:00\" +7) = " + addHours("2020-05-09 12:00:00", 7));
        System.out.println("addHours(\"2020-05-09 12:00:00\" -11) = " + addHours("2020-05-09 12:00:00", -11));
        System.out.println("addDate(\"2020-05-09\" +0) = " + addDate("2020-05-09", 0));
        System.out.println("addDate(\"2020-05-09\" +7) = " + addDate("2020-05-09", 7));
        System.out.println("addDate(\"2020-05-09\" -11) = " + addDate("2020-05-09", -11));
        System.out.println("addMonth(\"2020-05-09\" +0) = " + addMonth("2020-05-09", 0));
        System.out.println("addMonth(\"2020-05-09\" +7) = " + addMonth("2020-05-09", 7));
        System.out.println("addMonth(\"2020-05-09\" -11) = " + addMonth("2020-05-09", -11));
        System.out.println("isDateTimeMills(\"2020-05-09 12:00:00\") = " + isDateTimeMills("2020-05-09 12:00:00"));
        System.out.println("isDateTimeMills(\"2020-05-09 12:00:00.777\") = " + isDateTimeMills("2020-05-09 12:00:00.777"));
        System.out.println("isDateTimeMillsISO8601(\"2020-05-09 12:00:00\") = " + isDateTimeMillsISO8601("2020-05-09 12:00:00"));
        System.out.println("isDateTimeMillsISO8601(\"2020-05-09T12:00:00.777Z\") = " + isDateTimeMillsISO8601("2020-05-09T12:00:00.777Z"));
        System.out.println("isDateTimeISO8601(\"2020-05-09 12:00:00\") = " + isDateTimeISO8601("2020-05-09 12:00:00"));
        System.out.println("isDateTimeISO8601(\"2020-05-09T12:00:00Z\") = " + isDateTimeISO8601("2020-05-09T12:00:00Z"));
        System.out.println("isDateTime(\"2020-05-09 12:00:00\") = " + isDateTime("2020-05-09 12:00:00"));
        System.out.println("isDateTime(\"2020-05-09T12:00:00Z\") = " + isDateTime("2020-05-09T12:00:00Z"));
        System.out.println("isDateTimeNoSeparator(\"2020-05-09 12:00:00\") = " + isDateTimeNoSeparator("2020-05-09 12:00:00"));
        System.out.println("isDateTimeNoSeparator(\"20200509120000\") = " + isDateTimeNoSeparator("20200509120000"));
        System.out.println("isDate(\"2020-05-09 12:00:00\") = " + isDate("2020-05-09 12:00:00"));
        System.out.println("isDate(\"2020-05-09\") = " + isDate("2020-05-09"));
        System.out.println("isHour(\"12\") = " + isHour("12"));
        System.out.println("isHour(\"24\") = " + isHour("24"));
        System.out.println("isHour(\"25\") = " + isHour("25"));
        System.out.println("isTime(\"00:00:00))\") = " + isTime("00:00:00"));
        System.out.println("isTime(\"12:61:00))\") = " + isTime("12:61:00"));
        System.out.println("isTime(\"23:59:59))\") = " + isTime("23:59:59"));
        System.out.println("getTimestamp(\"2020-05-09\") = " + getTimestamp("2020-05-09"));
        System.out.println("getTimestamp(\"2020-05-09 12:00:00\") = " + getTimestamp("2020-05-09 12:00:00"));
        System.out.println("getTimestamp(\"2020-05-09 12:00:00.777\") = " + getTimestamp("2020-05-09 12:00:00.777"));
        System.out.println("getTimestamp(\"2020-05-09 12:00\") = " + getTimestamp("2020-05-09 12:00"));
        System.out.println("getTimestampNoSeparator(\"20200509120000\") = " + getTimestampNoSeparator("20200509120000"));
        System.out.println("getTimestampISO8601(\"2020-05-09T12:00:00Z\") = " + getTimestampISO8601("2020-05-09T12:00:00Z"));
        System.out.println("getTimestampISO8601WithMills(\"2020-05-09T12:00:00.777Z\") = " + getTimestampISO8601WithMills("2020-05-09T12:00:00.777Z"));
        System.out.println("daysBetweenDates(\"2020-06-01\", \"2020-06-09\") = " + daysBetweenDates("2020-06-01", "2020-06-09"));
        System.out.println("daysBetweenDates(\"2020-06-11\", \"2020-06-09\") = " + daysBetweenDates("2020-06-11", "2020-06-09"));
        System.out.println("daysBetweenTimestamps(now-86400000*3, now) = " + daysBetweenTimestamps(System.currentTimeMillis() - 86400000 * 3, System.currentTimeMillis()));
        System.out.println("daysBetweenTimestamps(now, now-86400000*2) = " + daysBetweenTimestamps(System.currentTimeMillis(), System.currentTimeMillis() - 86400000 * 2));
        System.out.println("daysBetweenDateTimes(\"2020-06-01 00:00:00\", \"2020-06-09 23:59:59\") = " + daysBetweenDateTimes("2020-06-01 00:00:00", "2020-06-09 23:59:59"));
        System.out.println("daysBetweenDateTimes(\"2020-06-11 00:00:00\", \"2020-06-09 23:59:59\") = " + daysBetweenDateTimes("2020-06-11 00:00:00", "2020-06-09 23:59:59"));
        System.out.println("minutesBetweenDateTimes(\"2020-06-01 00:00:00\", \"2020-06-09 23:59:59\") = " + minutesBetweenDateTimes("2020-06-01 00:00:00", "2020-06-09 23:59:59"));
        System.out.println("minutesBetweenDateTimes(\"2020-06-11 00:00:00\", \"2020-06-09 23:59:59\") = " + minutesBetweenDateTimes("2020-06-11 00:00:00", "2020-06-09 23:59:59"));
        System.out.println("secondsBetweenDateTimes(\"2020-06-01 00:00:00\", \"2020-06-09 23:59:59\") = " + secondsBetweenDateTimes("2020-06-01 00:00:00", "2020-06-09 23:59:59"));
        System.out.println("secondsBetweenDateTimes(\"2020-06-11 00:00:00\", \"2020-06-09 23:59:59\") = " + secondsBetweenDateTimes("2020-06-11 00:00:00", "2020-06-09 23:59:59"));
        System.out.println("parseDate(\"2020-05-09\", default now) = " + parseDate("2020-05-09", LocalDate.now()));
        System.out.println("parseDateTime(\"2020-05-09 12:00:00\", default now) = " + parseDateTime("2020-05-09 12:00:00", LocalDateTime.now()));
        System.out.println("parseNoSeparatorDateTime(\"20200509120000\", default now) = " + parseNoSeparatorDateTime("20200509120000", LocalDateTime.now()));
        System.out.println("getWeekOfDate(\"2020-05-09\") = " + getWeekOfDate("2020-05-09"));
        System.out.println("getHourOfDay(\"2020-05-09 12:00:00\") = " + getHourOfDateTime("2020-05-09 12:00:00"));

    }

    // ===========================================================   // Inner and Anonymous Classes
    // ===========================================================
}
