package com.huwo.gateway.service.event;

import com.alibaba.fastjson.JSON;
import com.huwo.gateway.common.HwPositionMessage;
import com.huwo.gateway.factory.OrderStrategyFactory;
import com.huwo.gateway.factory.PositionStrategyFactory;
import com.huwo.gateway.strategy.position.IPositionStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-20  10:41
 */
@Service
@Slf4j
public class HwPositionEventService {


    public void handleEvent(List<HwPositionMessage> positionMessageList) {
        for (HwPositionMessage positionMessage : positionMessageList) {
            IPositionStrategy strategy = PositionStrategyFactory.getStrategy("POSITION");
            strategy.handleAndSendData(positionMessage);
        }
    }
}
