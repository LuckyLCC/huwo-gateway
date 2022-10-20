package com.huwo.gateway.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-19  16:30
 */
@Data
public class BizCarDTO {

    private Long id;

    private String vehicleNo;

    private String plateColor;

    private String certificateNo;

    private String brand;

    private Long departmentId;

    private Integer vehicleRegionCode;
}
