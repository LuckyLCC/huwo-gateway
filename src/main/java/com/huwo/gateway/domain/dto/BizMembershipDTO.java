package com.huwo.gateway.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-18  15:49
 */
@Data
@TableName("biz_membership")
public class BizMembershipDTO implements Serializable {

    private Long id;

    @TableField("PhoneNo")
    private String phoneNo;

    @TableField("RealName")
    private String name;

    @TableField("Gender")
    private Integer gender;
}
