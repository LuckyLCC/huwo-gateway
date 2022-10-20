package com.huwo.gateway.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-18  17:03
 */

@Data
@TableName("upstream_base_config")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpstreamBaseConfig {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String adCode;

    private String cityName;

    private String upAddress;

    private String tokenURL;

    private Boolean isUpFlag;

    private String grantType;

    private String appId;

    private String appSecret;

    private String createTime;

    private String updateTime;


}
