package com.huwo.gateway.strategy.order;

import com.alibaba.fastjson.JSON;
import com.huwo.data.upstream.api.biz.DuOperateArrive;
import com.huwo.data.upstream.api.biz.DuOperateLogout;
import com.huwo.data.upstream.api.common.ChannelEnum;
import com.huwo.data.upstream.api.common.IPCTypeEnum;
import com.huwo.gateway.common.DuBody;
import com.huwo.gateway.common.HwStaticInfo;
import com.huwo.gateway.common.HwOrderMessage;
import com.huwo.gateway.domain.dto.BizCarDTO;
import com.huwo.gateway.domain.dto.BizDriverDTO;
import com.huwo.gateway.domain.dto.BizMembershipDTO;
import com.huwo.gateway.factory.OrderStrategyFactory;
import com.huwo.gateway.service.BizCarService;
import com.huwo.gateway.service.BizDriverService;
import com.huwo.gateway.service.BizMembershipService;
import com.huwo.gateway.utils.CoordinateUtil;
import com.huwo.gateway.utils.RTimeUtil;
import com.huwo.gateway.utils.RedisClient;
import com.huwo.gateway.utils.RedisKeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-19  20:51
 */

@Component
public class StateOrderPayPendingStrategy extends IOrderStrategy {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private BizDriverService bizDriverService;

    @Autowired
    private BizCarService bizCarService;

    @Autowired
    private BizMembershipService bizMembershipService;

    @Override
    protected List<DuBody> handleData(HwOrderMessage sourceData, Integer address) {
        // 删除订单号缓存
        delOrderNo(sourceData);

        ArrayList<DuBody> duBodies = new ArrayList<>();

        DuOperateArrive operateArrive = convertToDuOperateArrive(sourceData);
        if (null != operateArrive) {
            operateArrive.setAddress(address);
            String jsonString = JSON.toJSONString(operateArrive);

            //组装DuBody
            DuBody body = new DuBody();
            body.setIpcType(IPCTypeEnum.OPERATE_ARRIVE.getType());
            body.setChannel(ChannelEnum.HW.name());
            body.setAddress(address);
            body.setData(jsonString);
            duBodies.add(body);
        }

        DuOperateLogout operateLogout = convertToDuOperateLogout(sourceData);
        if (null != operateLogout) {
            operateLogout.setAddress(address);
            String jsonString2 = JSON.toJSONString(operateLogout);

            //组装DuBody
            DuBody body2 = new DuBody();
            body2.setIpcType(IPCTypeEnum.OPERATE_LOGOUT.getType());
            body2.setChannel(ChannelEnum.HW.name());
            body2.setAddress(address);
            body2.setData(jsonString2);
            duBodies.add(body2);
        }


        return duBodies;

    }

    private DuOperateLogout convertToDuOperateLogout(HwOrderMessage message) {
        DuOperateLogout operateLogout = new DuOperateLogout();

        // 司机缓存查询
        BizDriverDTO driver = bizDriverService.getCacheDataById(message.getDriverId());
        if (null == driver || 0 != driver.getCheckState() || null == driver.getApproveDate()) {
            return null;
        }
        operateLogout.setLicenseId(driver.getLicenseNo());
        // TODO 先默认呼我总部的社会信用码
        operateLogout.setIdentifier(HwStaticInfo.IDENTIFIER);
        operateLogout.setDriverIDCard(driver.getLicenseNo());
        operateLogout.setDriverCertCard(driver.getCertificateNo());
        operateLogout.setDriverPhone(driver.getPhoneNo());
        // 车辆缓存查询
        BizCarDTO vehicle = bizCarService.getCacheDataById(driver.getVehicleId());
        if (null != vehicle) {
            operateLogout.setVehicleNo(vehicle.getVehicleNo());
            operateLogout.setPlateColor(vehicle.getPlateColor());
        }

        Long bookDepTime = null == message.getAppointDate() ? message.getCreateDate() : message.getAppointDate();
        long depTime = null == message.getStartDate() ? bookDepTime : message.getStartDate();
        operateLogout.setOnWorkTime(depTime);
        long logoutTime = null == message.getStartDate() ? message.getActionEventDate() : message.getEndDate();
        operateLogout.setLogoutTime(RTimeUtil.milliTimestamp2Long(logoutTime));
        operateLogout.setOffWorkTime(operateLogout.getLogoutTime());
        operateLogout.setLongitude(CoordinateUtil.getIntegerCoordinate(message.getElongitude()));
        operateLogout.setLatitude(CoordinateUtil.getIntegerCoordinate(message.getElatitude()));
        operateLogout.setFactPrice(message.getActFeeP());

        operateLogout.setEncrypt(1);
        operateLogout.setDriveCount(0);
        operateLogout.setDriveMile(BigDecimal.valueOf(0));
        operateLogout.setDriveTime(0);
        return operateLogout;
    }

    private DuOperateArrive convertToDuOperateArrive(HwOrderMessage message) {
        DuOperateArrive operateArrive = new DuOperateArrive();
        operateArrive.setOrderId(message.getOrderNo());

        // 司机缓存查询
        BizDriverDTO driver = bizDriverService.getCacheDataById(message.getDriverId());
        if (null == driver || 0 != driver.getCheckState() || null == driver.getApproveDate()) {
            return null;
        }
        operateArrive.setDriverPhone(driver.getPhoneNo());
        operateArrive.setDriverIDCard(driver.getLicenseNo());
        operateArrive.setDriverCertCard(driver.getCertificateNo());
        // 车辆缓存查询
        BizCarDTO vehicle = bizCarService.getCacheDataById(driver.getVehicleId());
        if (null != vehicle) {
            operateArrive.setVehicleNo(vehicle.getVehicleNo());
        }


        // 通过 memberShipID 查询
        BizMembershipDTO passenger = bizMembershipService.getCacheDataById(message.getMembershipId());
        if (null != passenger) {
            operateArrive.setPassengerPhone(passenger.getPhoneNo());
        }

        long bookDepTime = null == message.getAppointDate() ? message.getCreateDate() : message.getAppointDate();
        long depTime = null == message.getStartDate() ? bookDepTime : message.getStartDate();
        long destTime = null == message.getEndDate() ? message.getActionEventDate() : message.getEndDate();
        operateArrive.setDepTime(RTimeUtil.milliTimestamp2Long(depTime));
        operateArrive.setDestTime(RTimeUtil.milliTimestamp2Long(destTime));
        operateArrive.setDepLongitude(CoordinateUtil.getIntegerCoordinate(message.getSlongitude()));
        operateArrive.setDepLatitude(CoordinateUtil.getIntegerCoordinate(message.getSlatitude()));
        operateArrive.setDestLongitude(CoordinateUtil.getIntegerCoordinate(message.getElongitude()));
        operateArrive.setDestLatitude(CoordinateUtil.getIntegerCoordinate(message.getElatitude()));
        operateArrive.setOrderTime(RTimeUtil.milliTimestamp2Long(message.getCreateDate()));

        operateArrive.setEncrypt(1);
        operateArrive.setDriveMile((null == message.getDistance() || message.getDistance().compareTo(BigDecimal.ZERO) < 0) ? BigDecimal.ONE : message.getDistance().divide(BigDecimal.valueOf(1000L), 3, RoundingMode.UP));
        long driveTime = (message.getEndDate() - depTime) / 1000;
        operateArrive.setDriveTime(driveTime <= 0 ? 1 : (int) driveTime);

        operateArrive.setStartDuration(0);
        operateArrive.setFactDrivingDuration(0);
        operateArrive.setDurationFee(new BigDecimal("0"));
        operateArrive.setFactDrivingMile(new BigDecimal("0"));
        operateArrive.setMileFee(new BigDecimal("0"));
        operateArrive.setFactFarMile(new BigDecimal("0"));
        operateArrive.setFarFee(new BigDecimal("0"));

        return operateArrive;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        OrderStrategyFactory.register("STATE_ORDER_PAY_PENDING", this);
    }

    private void delOrderNo(HwOrderMessage orderMessage) {
        String orderNo = orderMessage.getOrderNo();
        if (orderNo.startsWith("ZC")) {
            redisClient.delete(RedisKeyUtils.getOrderKey(orderMessage.getOrderId().toString()));
            return;
        }
        if (orderNo.startsWith("PC")) {
            BizDriverDTO dto = redisClient.get(RedisKeyUtils.getDriverKey(orderMessage.getDriverId()), BizDriverDTO.class);
            if (null == dto || StringUtils.isBlank(dto.getPhoneNo())) {
                return;
            }
            redisClient.delete(RedisKeyUtils.getOrderPhoneKey(dto.getPhoneNo()));
        }
    }
}
