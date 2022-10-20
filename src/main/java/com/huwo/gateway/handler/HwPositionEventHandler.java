package com.huwo.gateway.handler;

import com.alibaba.fastjson.JSONObject;
import com.huwo.gateway.common.HwPositionMessage;
import com.huwo.gateway.service.event.HwOrderEventService;
import com.huwo.gateway.service.event.HwPositionEventService;
import com.huwo.gateway.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.alibaba.fastjson.JSON.parseArray;
import static com.alibaba.fastjson.JSON.parseObject;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-17  18:05
 */
@Slf4j
public class HwPositionEventHandler implements Runnable{

    HwPositionEventService hwPositionEventService = SpringUtils.getBean(HwPositionEventService.class);

    private String topic;
    private String sourceData;


    public HwPositionEventHandler(String topic, String sourceData) {
            this.topic=topic;
            this.sourceData=sourceData;
    }

    @Override
    public void run() {
        log.debug("收到呼我定位：{}", sourceData);
        JSONObject jsonObject = parseObject(sourceData);
        JSONObject data = jsonObject.getObject("data", JSONObject.class);

        List<HwPositionMessage> vehicles = parseArray(data.getString("vehicles"), HwPositionMessage.class);
        hwPositionEventService.handleEvent(vehicles);
    }
}
