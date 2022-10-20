package com.huwo.gateway.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-19  17:12
 */
@Data
public class BizDriverDTO {

    private Long id;


    private String driverNo;


    private String phoneNo;


    private Integer adCode;


    private Long operatorId;


    private Long departmentId;

    private String licenseNo;

    private String driverName;
    private Long vehicleId;
    private Integer checkState;

    private LocalDateTime approveDate;

    private String certificateNo;
}
