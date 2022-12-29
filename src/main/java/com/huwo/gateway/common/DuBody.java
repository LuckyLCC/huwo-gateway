package com.huwo.gateway.common;

import lombok.Data;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-18  16:14
 */
@Data
public class DuBody {

    private String ipcType;
    private String channel;
    private String data;
    private String noUpstreamCity;
    private Integer address;
    private Long firstUpstreamTime;
    /**
     * 用于以后大数据处理，以事件时间语义
     */
    private Long timeStamp;

}
