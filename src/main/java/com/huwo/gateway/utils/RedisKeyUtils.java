package com.huwo.gateway.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-18  11:32
 */
public class RedisKeyUtils {

    public static final Long expireTime = (long) ((5 * 60 + new Random().nextInt(30)) * 60);

    public static final Long expireTime5H = (long) (5 * 60 * 60);

    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String getDepartmentKey(Long departmentId) {
        return "data:hospice:gateway:hw:department:" + departmentId;
    }

    public static String getPassengerKey(Long passengerId) {
        return "data:hospice:gateway:hw:passenger:" + passengerId;
    }

    public static String getDriverKey(Long driverId) {
        return "data:hospice:gateway:hw:driver:id:" + driverId;
    }

    public static String getDriverPhoneKey(String phone) {
        return "data:hospice:gateway:hw:driver:phone:" + phone;
    }

    public static String getVehicleKey(Long vehicleId) {
        return "data:hospice:gateway:hw:vehicle:" + vehicleId;
    }

    public static String getOrderKey(String orderId) {
        return "data:hospice:gateway:hw:order:id" + orderId;
    }

    public static String getOrderPhoneKey(String driverPhone) {
        return "data:hospice:gateway:hw:order:phone" + driverPhone;
    }

    public static String getOnlineLastKey(String driverPhone) {
        return "online:last_time:" + driverPhone;
    }

    public static String getOnlineDayKey(LocalDateTime positionTime) {
        return "online:day:" + positionTime.format(dateTimeFormatter);
    }
}
