package org.hxz.service.frameworks.utils;


import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Created by hxz on 2020/9/4 15:47.
 */

@SuppressWarnings("unused")
public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    private static final DateTimeFormatter YMDHM_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter YMDHMS_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter YMDH_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
    private static final DateTimeFormatter YMD_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter YM_TIME = DateTimeFormatter.ofPattern("yyyyMM");
    /**
     * 返回当前时间
     */
    public static String getDateTimeHM() {
        return YMDHM_TIME.format(LocalDateTime.now());
    }

    /**
     * 返回当前时间
     */
    public static String getNowDate() {
        return YMD_TIME.format(LocalDateTime.now());
    }

    /**
     * 返回当前时间
     */
    public static String getDateTimeHMS() {
        return YMDHMS_TIME.format(LocalDateTime.now());
    }

    /**
     * 获取两个时间的小时差
     */
    public static int getHour(String beginTime, String finishTime) {
        try {
            LocalDateTime begin = LocalDateTime.parse(beginTime, YMDHMS_TIME);
            LocalDateTime finish = LocalDateTime.parse(finishTime, YMDHMS_TIME);
            Duration duration = Duration.between(begin, finish);
            return (int) duration.toHours();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取两个时间的分钟差
     */
    public static int getMinutes(String beginTime, String finishTime) {
        try {
            LocalDateTime begin = LocalDateTime.parse(beginTime, YMDHMS_TIME);
            LocalDateTime finish = LocalDateTime.parse(finishTime, YMDHMS_TIME);
            Duration duration = Duration.between(begin, finish);
            return (int) duration.toMinutes();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     */
    public static String plusDay(String time, int p) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(time, YMDHMS_TIME);
            return localDateTime.plusDays(p).format(YMDHMS_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     */
    public static String plusHalfHour(String time) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(time, YMDHMS_TIME);
            return localDateTime.plusMinutes(30).format(YMDHMS_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     */
    public static String subHalfHour(String time) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(time, YMDHMS_TIME);
            return localDateTime.plusMinutes(-30).format(YMDHMS_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取本周开始时间 与 结束时间（2020-07-07格式）
     */
    public static String getStartOrEndDayOfWeek(LocalDate today, Boolean isFirst) {
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        DayOfWeek week = today.getDayOfWeek();
        int value = week.getValue();
        if (isFirst) {
            resDate = today.minusDays(value - 1);
        } else {
            resDate = today.plusDays(7 - value);
        }
        return resDate.toString();
    }

    public static int betweenDay(String startTime, String endTime) {
        try {
            LocalDateTime startLocalTime = LocalDateTime.parse(startTime, YMDHMS_TIME);
            LocalDateTime endLocalTime = LocalDateTime.parse(endTime, YMDHMS_TIME);
            Duration duration = Duration.between(startLocalTime, endLocalTime);
            return (int) duration.toDays();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }





    public static boolean compareDateTime(String startTime, String endTime) {
        try {
            LocalDateTime startLocalTime = LocalDateTime.parse(startTime, YMDHMS_TIME);
            LocalDateTime endLocalTime = LocalDateTime.parse(endTime, YMDHMS_TIME);
            return startLocalTime.isAfter(endLocalTime) || startLocalTime.isEqual(endLocalTime);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * @param milli 传入的时间戳
     * @return  返回系统时区当日的最小时间戳
     */
    public static long getTodayMinEpochMilli(long milli){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milli), ZoneId.systemDefault())
                .with(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    /**
     * @param milli 传入的时间戳
     * @return 返回系统时区当日的最大时间戳
     */
    public static long getTodayMaxEpochMilli(long milli){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milli), ZoneId.systemDefault())
                .with(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 返回当前时间的增加/减少天数后的时间戳
     */
    public static long getPlusDaysEpochMilli(long milli,int days){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milli), ZoneId.systemDefault())
                .plusDays(days).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    /**
     * 现在到明日零点的分钟数
     * @return
     */
    public static long getTomorrowMinTime(){
        LocalDateTime begin = LocalDateTime.now();
        LocalDateTime finish = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN);
        Duration duration = Duration.between(begin, finish);
        return (int) duration.toMinutes();
    }

    public static void main(String[] args) {
        long mill = System.currentTimeMillis();
        System.out.println(LocalDateTime.ofInstant(Instant.ofEpochMilli(mill), ZoneId.systemDefault()).plusDays(30).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        System.out.println(LocalDateTime.ofInstant(Instant.ofEpochMilli(mill), ZoneId.systemDefault()).plusDays(-30).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());



        System.out.println(getTomorrowMinTime());

    }

}
