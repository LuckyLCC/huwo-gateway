package com.huwo.gateway.strategy.order;

import com.huwo.gateway.common.DuBody;
import com.huwo.gateway.common.HwOrderMessage;
import com.huwo.gateway.factory.OrderStrategyFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: STATE_ORDER_ADVANCE_PAYMENT等几个策略不做处理
 * @Author: liuchang
 * @CreateTime: 2022-10-20  10:14
 */
@Component
public class StateOrderAdvancePaymentStrategy extends IOrderStrategy {

    @Override
    protected List<DuBody> handleData(HwOrderMessage sourceData, Integer address) {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        OrderStrategyFactory.register("STATE_ORDER_ADVANCE_PAYMENT", this);
        OrderStrategyFactory.register("STATE_ORDER_PICK_PENDING", this);
        OrderStrategyFactory.register("STATE_ORDER_EXECUTE_PENDING", this);
        OrderStrategyFactory.register("STATE_ORDER_CANCEL_PUNISH_FINISHED", this);
    }
}
