package com.chen.HospitalSelection.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 日期工具类
 * 提供日期时间的格式化和解析方法
 */
public class DateUtil {

    /**
     * 默认日期时间格式
     */
    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式
     */
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 时间格式
     */
    private static final String TIME_PATTERN = "HH:mm:ss";

    /**
     * 默认格式化器
     */
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN);

    /**
     * 日期格式化器
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    /**
     * 时间格式化器
     */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);

    /**
     * 格式化日期时间（使用默认格式）
     *
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * 格式化日期时间（自定义格式）
     *
     * @param dateTime 日期时间
     * @param pattern  格式模式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * 格式化日期
     *
     * @param dateTime 日期时间
     * @return 格式化后的日期字符串
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_FORMATTER);
    }

    /**
     * 格式化时间
     *
     * @param dateTime 日期时间
     * @return 格式化后的时间字符串
     */
    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(TIME_FORMATTER);
    }

    /**
     * 解析日期字符串（使用默认格式）
     *
     * @param dateStr 日期字符串
     * @return LocalDateTime对象
     */
    public static LocalDateTime parse(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateStr, DEFAULT_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 解析日期字符串（自定义格式）
     *
     * @param dateStr 日期字符串
     * @param pattern 格式模式
     * @return LocalDateTime对象
     */
    public static LocalDateTime parse(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取格式化的当前时间
     *
     * @return 格式化的当前时间字符串
     */
    public static String nowStr() {
        return format(now());
    }

    /**
     * 判断日期是否在指定范围内
     *
     * @param date     日期
     * @param start    开始日期
     * @param end      结束日期
     * @return true-在范围内，false-不在范围内
     */
    public static boolean isBetween(LocalDateTime date, LocalDateTime start, LocalDateTime end) {
        if (date == null || start == null || end == null) {
            return false;
        }
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * 判断日期是否在当前时间之后
     *
     * @param date 日期
     * @return true-在当前时间之后，false-不在
     */
    public static boolean isAfterNow(LocalDateTime date) {
        if (date == null) {
            return false;
        }
        return date.isAfter(LocalDateTime.now());
    }

    /**
     * 判断日期是否在当前时间之前
     *
     * @param date 日期
     * @return true-在当前时间之前，false-不在
     */
    public static boolean isBeforeNow(LocalDateTime date) {
        if (date == null) {
            return false;
        }
        return date.isBefore(LocalDateTime.now());
    }
}
