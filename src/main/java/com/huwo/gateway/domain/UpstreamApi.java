package com.huwo.gateway.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-18  17:09
 */
@Data
@TableName("upstream_api")
public class UpstreamApi {


    private Integer id;

    private String adCode;

    private String cityName;

    private String type;

    private String typeApi;

    private String upWay;

//    private Boolean isUpFlag;

    private String createTime;

    private String updateTime;
}
