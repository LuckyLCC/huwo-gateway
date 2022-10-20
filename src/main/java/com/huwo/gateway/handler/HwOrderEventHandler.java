package com.huwo.gateway.handler;

import com.alibaba.fastjson.JSON;
import com.huwo.gateway.utils.SpringUtils;
import com.huwo.gateway.common.HwOrderMessage;
import com.huwo.gateway.service.event.HwOrderEventService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-17  18:02
 */
@Slf4j
public class HwOrderEventHandler implements Runnable {


    HwOrderEventService hwDepartmentService = SpringUtils.getBean(HwOrderEventService.class);


    private String topic;
    private String sourceData;

    public HwOrderEventHandler(String topic, String sourceData) {
        this.topic = topic;
        this.sourceData = sourceData;
    }

    @Override
    public void run() {

        HwOrderMessage orderMessage = JSON.parseObject(sourceData, HwOrderMessage.class);
        String actionEvent = orderMessage.getActionEvent();
        try {
            log.info("收到呼我订单事件：{},数据：{}", actionEvent, JSON.toJSONString(orderMessage));
            hwDepartmentService.handleEvent(actionEvent, orderMessage);
        } catch (Exception e) {
            log.error("处理呼我订单数据失败，数据：{}， 异常：", JSON.toJSONString(orderMessage), e);
        }
    }
}
