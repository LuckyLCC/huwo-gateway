package com.huwo.gateway.strategy.order;

import com.alibaba.fastjson.JSON;
import com.huwo.data.upstream.api.biz.DuRatedPassenger;
import com.huwo.data.upstream.api.common.ChannelEnum;
import com.huwo.data.upstream.api.common.IPCTypeEnum;
import com.huwo.gateway.common.DuBody;
import com.huwo.gateway.common.HwOrderMessage;
import com.huwo.gateway.factory.OrderStrategyFactory;
import com.huwo.gateway.utils.RTimeUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-20  09:34
 */
@Component
public class StateOrderCommentedStrategy extends IOrderStrategy {

    @Override
    protected List<DuBody> handleData(HwOrderMessage sourceData, Integer address) {
        DuRatedPassenger ratedPassenger = convertToDuRatedPassenger(sourceData);
        ratedPassenger.setAddress(address);
        String jsonString = JSON.toJSONString(ratedPassenger);

        //组装DuBody
        DuBody body = new DuBody();
        body.setIpcType(IPCTypeEnum.RATED_PASSENGER.getType());
        body.setChannel(ChannelEnum.HW.name());
        body.setAddress(address);
        body.setData(jsonString);
        return Arrays.asList(body);
    }

    private DuRatedPassenger convertToDuRatedPassenger(HwOrderMessage message) {
        DuRatedPassenger ratedPassenger = new DuRatedPassenger();
        ratedPassenger.setOrderId(message.getOrderNo());
        ratedPassenger.setEvaluateTime(RTimeUtil.milliTimestamp2Long(message.getActionEventDate()));
        ratedPassenger.setServiceScore(new BigDecimal("5.0"));
        ratedPassenger.setTotalScore(95);

        return ratedPassenger;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        OrderStrategyFactory.register("STATE_ORDER_COMMENTED", this);
    }
}
