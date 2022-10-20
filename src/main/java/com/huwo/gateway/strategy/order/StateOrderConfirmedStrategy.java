package com.huwo.gateway.strategy.order;

import com.alibaba.fastjson.JSON;
import com.huwo.data.upstream.api.biz.DuOrderMatch;
import com.huwo.data.upstream.api.common.ChannelEnum;
import com.huwo.data.upstream.api.common.IPCTypeEnum;
import com.huwo.gateway.common.DuBody;
import com.huwo.gateway.domain.dto.BizCarDTO;
import com.huwo.gateway.domain.dto.BizDriverDTO;
import com.huwo.gateway.common.HwOrderMessage;
import com.huwo.gateway.factory.OrderStrategyFactory;
import com.huwo.gateway.service.BizCarService;
import com.huwo.gateway.service.BizDriverService;
import com.huwo.gateway.utils.CoordinateUtil;
import com.huwo.gateway.utils.RTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-19  16:17
 */
@Component
public class StateOrderConfirmedStrategy extends IOrderStrategy {

    @Autowired
    private BizCarService bizCarService;

    @Autowired
    private BizDriverService bizDriverService;

    @Override
    protected List<DuBody> handleData(HwOrderMessage sourceData, Integer address) {
        //生成订单
        DuOrderMatch orderMatch = convert(sourceData);
        if (null == orderMatch) {
            return null;
        }
        orderMatch.setAddress(address);
        orderMatch.setDepartCity(address.toString());
        String jsonString = JSON.toJSONString(orderMatch);

        //组装DuBody
        DuBody body = new DuBody();
        body.setIpcType(IPCTypeEnum.ORDER_MATCH.getType());
        body.setChannel(ChannelEnum.HW.name());
        body.setAddress(address);
        body.setData(jsonString);

        return Arrays.asList(body);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        OrderStrategyFactory.register("STATE_ORDER_CONFIRMED", this);
    }

    private  DuOrderMatch convert(HwOrderMessage message) {
        DuOrderMatch orderMatch = new DuOrderMatch();
        orderMatch.setOrderId(message.getOrderNo());

        // 司机缓存查询
        BizDriverDTO driver = bizDriverService.getCacheDataById(message.getDriverId());
        if (null == driver || 0 != driver.getCheckState() || null == driver.getApproveDate()) {
            return null;
        }
        orderMatch.setLicenseId(driver.getLicenseNo());
        orderMatch.setDriverPhone(driver.getPhoneNo());
        orderMatch.setDriverName(driver.getDriverName());
        orderMatch.setIdNo(driver.getLicenseNo());
        orderMatch.setDriCertNo(driver.getCertificateNo());
        // 车辆缓存查询
        BizCarDTO vehicle = bizCarService.getCacheDataById(driver.getVehicleId());
        if (null != vehicle) {
            orderMatch.setVehicleNo(vehicle.getVehicleNo());
            orderMatch.setPlateColor(vehicle.getPlateColor());
            orderMatch.setCarCertNo(vehicle.getCertificateNo());
        }

        orderMatch.setEncrypt(1);
        orderMatch.setDistributeTime(RTimeUtil.milliTimestamp2Long(message.getConfirmTime()));
        orderMatch.setResStatus("已响应");
        orderMatch.setResTime(0);
        // 订单成功时车辆经纬 暂取出发点经纬
        orderMatch.setLongitude(CoordinateUtil.getIntegerCoordinate(message.getSlongitude()));
        orderMatch.setLatitude(CoordinateUtil.getIntegerCoordinate(message.getSlatitude()));
        orderMatch.setOrderTime(RTimeUtil.milliTimestamp2Long(message.getCreateDate()));

        orderMatch.setDepartLocale(message.getSlocation());
        orderMatch.setDestCity("0");

        orderMatch.setDepartLocaleDetail(message.getSlocation());
        orderMatch.setDepartLon(CoordinateUtil.getIntegerCoordinate(message.getSlongitude()));
        orderMatch.setDepartLat(CoordinateUtil.getIntegerCoordinate(message.getSlatitude()));
        orderMatch.setDesLocale(message.getElocation());
        orderMatch.setDesLocaleDetail(message.getElocation());
        orderMatch.setDesLon(CoordinateUtil.getIntegerCoordinate(message.getElongitude()));
        orderMatch.setDesLat(CoordinateUtil.getIntegerCoordinate(message.getElatitude()));

        return orderMatch;
    }
}
