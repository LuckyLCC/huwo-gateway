package com.huwo.gateway.strategy.order;

import com.alibaba.fastjson.JSON;
import com.huwo.data.upstream.api.biz.DuOrderCancel;
import com.huwo.data.upstream.api.common.ChannelEnum;
import com.huwo.data.upstream.api.common.IPCTypeEnum;
import com.huwo.gateway.common.DuBody;
import com.huwo.gateway.common.HwOrderMessage;
import com.huwo.gateway.factory.OrderStrategyFactory;
import com.huwo.gateway.utils.RTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-20  09:30
 */
@Component
public class StateOrderCancelStrategy extends IOrderStrategy {
    @Override
    protected List<DuBody> handleData(HwOrderMessage sourceData, Integer address) {
        DuOrderCancel orderCancel = convertToDuOrderCancel(sourceData);
        orderCancel.setAddress(address);

        String jsonString = JSON.toJSONString(orderCancel);

        //组装DuBody
        DuBody body = new DuBody();
        body.setIpcType(IPCTypeEnum.ORDER_CANCEL.getType());
        body.setChannel(ChannelEnum.HW.name());
        body.setAddress(address);
        body.setData(jsonString);
        return Arrays.asList(body);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        OrderStrategyFactory.register("STATE_ORDER_CANCEL", this);
        OrderStrategyFactory.register("STATE_ORDER_CANCEL_PUNISH", this);
        OrderStrategyFactory.register("STATE_ORDER_CLOSED", this);
    }


    private DuOrderCancel convertToDuOrderCancel(HwOrderMessage message) {
        DuOrderCancel orderCancel = new DuOrderCancel();
        orderCancel.setOrderId(message.getOrderNo());
        orderCancel.setCancelTime(RTimeUtil.milliTimestamp2Long(message.getCancelDate()));
        orderCancel.setOperator(null == message.getCancelUserType() ? "3" : message.getCancelUserType().toString());
        int cancelTypeCode = getCancelTypeCode(Integer.valueOf(orderCancel.getOperator()), message.getActionEvent());

        orderCancel.setCancelTypeCode(Integer.toString(cancelTypeCode));
        orderCancel.setCancelReason(StringUtils.isBlank(message.getCancelReason()) ? "无" : message.getCancelReason());
        orderCancel.setOrderTime(RTimeUtil.milliTimestamp2Long(message.getCreateDate()));

        return orderCancel;
    }


    /**
     * 获取取消类型
     *
     * @param cancelUserType
     * @param actionEvent
     * @return
     */
    private static int getCancelTypeCode(Integer cancelUserType, String actionEvent) {

        if ("STATE_ORDER_CANCEL_PUNISH".equals(actionEvent)) {
            return cancelUserType + 3;
        }
        if ("STATE_ORDER_CLOSED".equals(actionEvent)) {
            return 3;
        }
        return cancelUserType;
    }
}
