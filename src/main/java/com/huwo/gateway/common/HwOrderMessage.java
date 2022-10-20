package com.huwo.gateway.common;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-17  18:11
 */
@Data
public class HwOrderMessage implements Serializable {

    private BigDecimal actFee;
    private BigDecimal actFeeP;
    private String actionEvent;
    private Long actionEventDate;
    private Boolean appoint;
    private Long appointDate;
    private Integer bizType;
    private Integer cancelUserType;
    private Integer carPoolNum;
    private Integer carType;
    private Integer childNumber;
    private BigDecimal cooperationFee;
    private Long createDate;
    private BigDecimal daccountFee;
    private Long dateFrom;
    private Long dateTo;
    private Long departmentId;
    private BigDecimal discount;
    private BigDecimal distance;
    private Long driverId;
    private String elatitude;
    private String elocation;
    private String elongitude;
    private BigDecimal fee;
    private Boolean isKcMaster;
    private String lineId;
    private Long masterId;
    private Long membershipId;
    private String orderArea;
    private Long orderId;
    private String orderNo;
    private Integer orderSource;
    private Long payDate;
    private Integer payType;
    private Integer pcType;
    private BigDecimal platformFee;
    private String rlatitude;
    private String rlocation;
    private String rlongitude;
    private String slatitude;
    private String slocation;
    private String slongitude;
    private Integer spendTime;
    private Integer state;
    private Integer type;

    private Long confirmTime;
    private String customerDeviceID;
    private String description;
    private Boolean fromAirport;
    private String orderCity;
    private String other;
    private String otherName;
    private String otherPhone;
    private String pLatitude;
    private String pLocation;
    private String pLongitude;
    private String pickType;
    private Long startDate;
    private Boolean toAirport;

    private  String carNo;
    private  String endOrderArea;

    private Long endDate;
    private BigDecimal sdistance;

    private Long cancelDate;
    private String cancelReason;

    private Integer destAddress;
    private String passengerName;
    private String passengerGender;
}
