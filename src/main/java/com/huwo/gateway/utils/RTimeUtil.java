package com.huwo.gateway.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具
 *
 * @author ZhangAihua
 * @date 2021/5/7 0007 16:42
 */
@Slf4j
public class RTimeUtil {

    private static final String TIME_PATTERN = "yyyyMMddHHmmss";
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("+8");


    RTimeUtil() {
    }

    /**
     * @param pattern
     * @return
     */
    public static String now(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * @param
     * @return
     */
    public static Long time2Long() {
        return Long.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIME_PATTERN)));
    }

    /**
     * @param
     * @return
     */
    public static Long time2Long(LocalDateTime time) {
        return Long.valueOf(time.format(DateTimeFormatter.ofPattern(TIME_PATTERN)));
    }

    /**
     * 获取当前时间戳，单位秒
     *
     * @return
     */
    public static Long timestamp() {
        return timestamp(false);
    }

    /**
     * @param milliSecond
     * @return
     */
    public static Long timestamp(boolean milliSecond) {

        if (milliSecond) {
            return LocalDateTime.now().toInstant(ZONE_OFFSET).toEpochMilli();
        } else {
            return LocalDateTime.now().toEpochSecond(ZONE_OFFSET);
        }
    }

    /**
     * @param date
     * @param milliSecond
     * @return
     */
    public static Long covert2Timestamp(String date, boolean milliSecond) {
        return covert2Timestamp(date, TIME_PATTERN, milliSecond);
    }

    /**
     * @param date
     * @return
     */
    public static Long covert2Timestamp(String date, String pattern, boolean milliSecond) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime parse = LocalDateTime.parse(date, dateTimeFormatter);
        if (milliSecond) {
            return parse.toInstant(ZONE_OFFSET).toEpochMilli();
        } else {
            return parse.toEpochSecond(ZONE_OFFSET);
        }
    }


    /**
     * 时间戳转Long(默认格式)
     *
     * @param timestamp
     * @return
     */
    public static Long milliTimestamp2Long(Long timestamp) {
        return timestamp2Long(timestamp, true);
    }

    /**
     * 时间戳转Long(默认格式)
     *
     * @param timestamp
     * @return
     */
    public static Long timestamp2Long(Long timestamp, boolean milliSecond) {
        return timestamp2Long(timestamp, TIME_PATTERN, milliSecond);
    }


    /**
     * 时间戳转Long(指定格式)
     *
     * @param timestamp
     * @param pattern
     * @return
     */
    public static Long timestamp2Long(Long timestamp, String pattern, boolean milliSecond) {
        if (milliSecond) {
            timestamp = timestamp / 1000;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(timestamp, 0, ZONE_OFFSET);
        return Long.valueOf(localDateTime.format(dateTimeFormatter));

    }

    public static String timestamp2FormatStr(Long timestamp, String pattern) {
        return timestamp2FormatStr(timestamp, pattern, false);
    }

    public static String timestamp2FormatStr(Long timestamp, String pattern, boolean milliSecond) {
        if (milliSecond) {
            timestamp = timestamp / 1000;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(timestamp, 0, ZONE_OFFSET);
        return localDateTime.format(dateTimeFormatter);
    }
}
