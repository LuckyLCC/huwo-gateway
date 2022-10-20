package com.huwo.gateway.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-17  18:42
 */
@Data
@TableName("sys_department")
public class SysDepartmentDTO {

    private Long id;

    @TableField("adcode")
    private Integer adCode;

    private String name;
}
