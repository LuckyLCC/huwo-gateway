package com.huwo.gateway.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.swing.text.StyledEditorKit;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-18  17:11
 */

@Data
@TableName("upstream_field")
public class UpstreamField {

    private Integer id;

    private Long upstreamApiId;

    private String upField;

    private Boolean isMust;

    private String sourceField;

    private String createTime;

    private String updateTime;

}
