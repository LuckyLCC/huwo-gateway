package com.huwo.gateway.strategy.order;

import com.alibaba.fastjson.JSON;
import com.huwo.data.upstream.api.biz.DuOrderCreate;
import com.huwo.data.upstream.api.common.ChannelEnum;
import com.huwo.data.upstream.api.common.IPCTypeEnum;
import com.huwo.gateway.common.DuBody;
import com.huwo.gateway.domain.dto.BizMembershipDTO;
import com.huwo.gateway.common.HwOrderMessage;
import com.huwo.gateway.factory.OrderStrategyFactory;
import com.huwo.gateway.service.BizMembershipService;
import com.huwo.gateway.utils.CoordinateUtil;
import com.huwo.gateway.utils.RTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-18  15:11
 */

@Component
public class StateOrderNewStrategy extends IOrderStrategy {

    @Autowired
    private BizMembershipService bizMembershipService;


    @Override
    protected List<DuBody> handleData(HwOrderMessage sourceData, Integer address) {

        //生成订单
        DuOrderCreate orderCreate = convert(sourceData);
        orderCreate.setAddress(address);
        orderCreate.setDepartCity(address.toString());
        String jsonString = JSON.toJSONString(orderCreate);

        //组装DuBody
        DuBody body = new DuBody();
        body.setIpcType(IPCTypeEnum.ORDER_CREATE.getType());
        body.setChannel(ChannelEnum.HW.name());
        body.setAddress(address);
        body.setData(jsonString);
        return Arrays.asList(body);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        OrderStrategyFactory.register("STATE_ORDER_NEW", this);
    }

    public DuOrderCreate convert(HwOrderMessage message) {

        DuOrderCreate orderCreate = new DuOrderCreate();
        if (StringUtils.isNotBlank(message.getOtherPhone())) {
            orderCreate.setPassengerPhone(message.getOtherPhone());
            orderCreate.setPsgName("乘客");
            //贵州新添字段
            orderCreate.setPassengerName("乘客");
            orderCreate.setPassengerGender("0");
        } else {
            // 通过 memberShipID 查询
            BizMembershipDTO passenger = bizMembershipService.getCacheDataById(message.getMembershipId());
            if (null != passenger) {
                orderCreate.setPassengerPhone(passenger.getPhoneNo());
                orderCreate.setPsgName(StringUtils.isBlank(passenger.getName()) ? "乘客" : passenger.getName());
                orderCreate.setPsgGender(passenger.getGender());
                //贵州新添字段
                orderCreate.setPassengerName(StringUtils.isBlank(passenger.getName()) ? "乘客" : passenger.getName());
                orderCreate.setPassengerGender(String.valueOf(passenger.getGender()));
            }
        }
        orderCreate.setOrderId(message.getOrderNo());
        orderCreate.setOrderTime(RTimeUtil.milliTimestamp2Long(message.getCreateDate()));
        orderCreate.setDepartTime(message.getAppointDate() == null ? orderCreate.getOrderTime() : RTimeUtil.milliTimestamp2Long(message.getAppointDate()));
        orderCreate.setDeparture(message.getSlocation());
        orderCreate.setDepLongitude(CoordinateUtil.getIntegerCoordinate(message.getSlongitude()));
        orderCreate.setDepLatitude(CoordinateUtil.getIntegerCoordinate(message.getSlatitude()));
        orderCreate.setDestination(message.getElocation());
        orderCreate.setDestLongitude(CoordinateUtil.getIntegerCoordinate(message.getElongitude()));
        orderCreate.setDestLatitude(CoordinateUtil.getIntegerCoordinate(message.getElatitude()));
        orderCreate.setEncrypt(1);
        orderCreate.setFareType("1");

        // 呼我 '订单来源（0-app,1-微信小程序,2-html5页面,3-携程订单，4-新版小程序）
        orderCreate.setOrderSource(convertOrderSource(message.getOrderSource()));
        orderCreate.setPsgTotal(message.getCarPoolNum());
        orderCreate.setIsReserve(1);
        orderCreate.setIsVoice(0);
        orderCreate.setPreMile(new BigDecimal("0"));
        orderCreate.setPreTime(message.getSpendTime());
        orderCreate.setPreFare(new BigDecimal("0"));
        orderCreate.setUseTime(0L);
        orderCreate.setUseLocale(message.getSlocation());
        orderCreate.setUseLon(CoordinateUtil.getIntegerCoordinate(message.getSlongitude()));
        orderCreate.setUseLat(CoordinateUtil.getIntegerCoordinate(message.getSlatitude()));
        orderCreate.setTaxiTypeCode(0);
        orderCreate.setServiceTypeCode("");

        orderCreate.setDestCity("0");
        orderCreate.setVehType(message.getType() == 1 ? "ZC" : "KC");
        orderCreate.setPassengerNote("");
        orderCreate.setVoiceUrl("");

        //贵州新添字段
        orderCreate.setDestAddress(2260);

        return orderCreate;
    }


    /**
     * 将呼我订单来源转换为上报的来源
     *
     * @param hwSource
     * @return
     */
    private Integer convertOrderSource(Integer hwSource) {
        if (0 == hwSource) {
            return 0;
        }
        if (1 == hwSource || 4 == hwSource) {
            return 3;
        }
        if (2 == hwSource || 3 == hwSource) {
            return 1;
        }
        return 4;

    }
}
