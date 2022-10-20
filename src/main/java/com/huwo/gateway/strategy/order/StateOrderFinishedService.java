package com.huwo.gateway.strategy.order;

import com.alibaba.fastjson.JSON;
import com.huwo.data.upstream.api.biz.DuOperatePay;
import com.huwo.data.upstream.api.common.ChannelEnum;
import com.huwo.data.upstream.api.common.IPCTypeEnum;
import com.huwo.gateway.common.DuBody;
import com.huwo.gateway.common.HwOrderMessage;
import com.huwo.gateway.domain.dto.BizCarDTO;
import com.huwo.gateway.domain.dto.BizDriverDTO;
import com.huwo.gateway.factory.OrderStrategyFactory;
import com.huwo.gateway.service.BizCarService;
import com.huwo.gateway.service.BizDriverService;
import com.huwo.gateway.utils.CoordinateUtil;
import com.huwo.gateway.utils.RTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-19  20:46
 */
@Component
public class StateOrderFinishedService extends IOrderStrategy {

    @Autowired
    private BizDriverService bizDriverService;

    @Autowired
    private BizCarService bizCarService;

    @Override
    protected List<DuBody> handleData(HwOrderMessage sourceData, Integer address) {

        DuOperatePay operatePay = convert(sourceData);
        if (null == operatePay) {
            return null;
        }
        operatePay.setAddress(address);
        operatePay.setOnArea(address);
        String jsonString = JSON.toJSONString(operatePay);

        //组装DuBody
        DuBody body = new DuBody();
        body.setIpcType(IPCTypeEnum.OPERATE_PAY.getType());
        body.setChannel(ChannelEnum.HW.name());
        body.setAddress(address);
        body.setData(jsonString);
        return Arrays.asList(body);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        OrderStrategyFactory.register("STATE_ORDER_FINISHED", this);
    }

    private  DuOperatePay convert(HwOrderMessage message) {
        DuOperatePay operatePay = new DuOperatePay();

        operatePay.setOrderId(message.getOrderNo());
        // 司机缓存查询
        BizDriverDTO driver = bizDriverService.getCacheDataById(message.getDriverId());
        if (null == driver || 0 != driver.getCheckState() || null == driver.getApproveDate()) {
            return null;
        }
        operatePay.setLicenseId(driver.getLicenseNo());
        operatePay.setDriverIDCard(driver.getLicenseNo());
        operatePay.setDriverName(driver.getDriverName());
        operatePay.setDriverCertCard(driver.getCertificateNo());
        // 车辆缓存查询
        BizCarDTO vehicle = bizCarService.getCacheDataById(driver.getVehicleId());
        if (null != vehicle) {
            operatePay.setVehicleNo(vehicle.getVehicleNo());
            operatePay.setPlateColor(vehicle.getPlateColor());
        }


        Long bookDepTime = null == message.getAppointDate() ? message.getCreateDate() : message.getAppointDate();
        operatePay.setBookDepTime(RTimeUtil.milliTimestamp2Long(bookDepTime));
        Long depTime = null == message.getStartDate() ? bookDepTime : message.getStartDate();
        operatePay.setDepTime(RTimeUtil.milliTimestamp2Long(depTime));
        long arriveTime = null == message.getEndDate() ? message.getActionEventDate() : message.getEndDate();
        operatePay.setDestTime(RTimeUtil.milliTimestamp2Long(arriveTime));
        operatePay.setDepLongitude(CoordinateUtil.getIntegerCoordinate(message.getSlongitude()));
        operatePay.setDepLatitude(CoordinateUtil.getIntegerCoordinate(message.getSlatitude()));
        operatePay.setDestLongitude(CoordinateUtil.getIntegerCoordinate(message.getElongitude()));
        operatePay.setDestLatitude(CoordinateUtil.getIntegerCoordinate(message.getElatitude()));

        operatePay.setFactPrice(message.getActFeeP());
        operatePay.setFarUpPrice(BigDecimal.ZERO);
        operatePay.setOtherUpPrice(BigDecimal.ZERO);
        operatePay.setPayState("1");
        operatePay.setInvoiceStatus("0");
        operatePay.setCallPrice(BigDecimal.ZERO);
        operatePay.setFareType("1");
        operatePay.setFareRuleUrl("无");
        operatePay.setDepArea(message.getSlocation());
        operatePay.setDestArea(message.getElocation());
        operatePay.setDriveMile((null == message.getDistance() || message.getDistance().compareTo(BigDecimal.ZERO) < 0) ? BigDecimal.ONE : message.getDistance().divide(BigDecimal.valueOf(1000L), 3, RoundingMode.UP));
        //  行驶时间  (结束-开始)/1000
        long driveTime = (arriveTime - depTime) / 1000;
        operatePay.setDriveTime(driveTime <= 0 ? 1 : driveTime);
        operatePay.setBookModel(3 == message.getCarType() ? "七座商务" : "五座车");
        operatePay.setModel(3 == message.getCarType() ? "七座商务" : "五座车");

        operatePay.setOrderTime(RTimeUtil.milliTimestamp2Long(message.getCreateDate()));
        operatePay.setPayTime(RTimeUtil.milliTimestamp2Long(arriveTime));

        return operatePay;
    }
}
