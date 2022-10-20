package com.huwo.gateway.common;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ZhangAihua
 * @date 2021/5/10 0010 9:29
 */
@Data
public class HwPositionMessage implements Serializable {
    
     private String vehicleID;
     private Integer vehicleType;
     private Integer state;
     private Integer carryMode;
     private String city;
     private Integer locationtype;
     private String location;
     private Integer accuracy;
     private BigDecimal direction;
     private BigDecimal speed;
     private BigDecimal height;
     private Long deviceTime;
     private Long timestamp;
     private String orderID;
     private Integer seats;
}
