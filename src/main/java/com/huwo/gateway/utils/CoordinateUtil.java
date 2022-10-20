package com.huwo.gateway.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 坐标工具
 *
 * @author ZhangAihua
 * @date 2021/6/7 0007 9:18
 */
public class CoordinateUtil {

    CoordinateUtil(){
    }

    /**
     * String 转 Integer
     *
     * @param coordinate
     * @return
     */
    public static Integer getIntegerCoordinate(String coordinate) {
        return getIntegerCoordinate(new BigDecimal(coordinate));
    }

    /**
     * BigDecimal 转 Integer
     *
     * @param coordinate
     * @return
     */
    public static Integer getIntegerCoordinate(BigDecimal coordinate) {
        return coordinate.multiply(BigDecimal.valueOf(1000000L)).setScale(0, RoundingMode.DOWN).intValue();
    }
}
