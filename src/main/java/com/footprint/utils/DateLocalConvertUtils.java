package com.footprint.utils;

import java.time.*;
import java.util.Date;
import java.util.Objects;

/**
 * Date 与LocalDate、LocalDateTime、LocalTime相互转换
 * @author hui.zhou 11:21 2018/1/26
 */
public class DateLocalConvertUtils {

    /**
     * localDate转换成date
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate){
        Objects.requireNonNull(localDate, "localDate不能为空");
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    /**
     * Date 转换成LocalDate
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date){
        Objects.requireNonNull(date, "date不能为空");
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate;
    }

    /**
     * Date转换成LocalTime
     * @param date
     * @return
     */
    public static LocalTime dateToLocalTime(Date date){
        Objects.requireNonNull(date, "date不能为空");
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalTime localTime = localDateTime.toLocalTime();
        return localTime;
    }

    /**
     * localtime 转换成指定日期的date，如果localDate为空则默认为当天
     * @param localTime
     * @param localDate
     * @return
     */
    public static Date localTimeToDate(LocalTime localTime, LocalDate localDate){
        Objects.requireNonNull(localTime, "localTime不能为空");
        if(Objects.isNull(localDate)){
            localDate = LocalDate.now();
        }
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    /**
     * localdatetime 转换成Date
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime){
        Objects.requireNonNull(localDateTime, "localDateTime不能为空");
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    /**
     * date转换成localdatetime
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date){
        Objects.requireNonNull(date, "date不能为空");
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }
}
