package com.huwo.gateway.strategy.order;

import com.alibaba.fastjson.JSON;
import com.huwo.data.upstream.api.biz.DuOperateDepart;
import com.huwo.data.upstream.api.biz.DuOperateLogin;
import com.huwo.data.upstream.api.common.ChannelEnum;
import com.huwo.data.upstream.api.common.IPCTypeEnum;
import com.huwo.gateway.common.DuBody;
import com.huwo.gateway.common.HwStaticInfo;
import com.huwo.gateway.domain.dto.BizMembershipDTO;
import com.huwo.gateway.common.HwOrderMessage;
import com.huwo.gateway.domain.dto.BizCarDTO;
import com.huwo.gateway.domain.dto.BizDriverDTO;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-19  18:11
 */
@Component
public class StateOrderExecuteStrategy extends IOrderStrategy {

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
        // 缓存订单号
        cacheOrderNo(sourceData);

        ArrayList<DuBody> duBodies = new ArrayList<>();

        //operateDepart
        DuOperateDepart operateDepart = convertToDuOperateDepart(sourceData);
        if (null != operateDepart) {
            operateDepart.setAddress(address);
            String jsonString = JSON.toJSONString(operateDepart);

            //组装DuBody
            DuBody body = new DuBody();
            body.setIpcType(IPCTypeEnum.OPERATE_DEPART.getType());
            body.setChannel(ChannelEnum.HW.name());
            body.setAddress(address);
            body.setData(jsonString);
            duBodies.add(body);
        }



        //DuOperateLogin
        DuOperateLogin operateLogin = convertToDuOperateLogin(sourceData);
        if (null != operateLogin) {
            operateDepart.setAddress(address);
            String jsonString2 = JSON.toJSONString(operateDepart);

            //组装DuBody
            DuBody body2 = new DuBody();
            body2.setIpcType(IPCTypeEnum.OPERATE_LOGIN.getType());
            body2.setChannel(ChannelEnum.HW.name());
            body2.setAddress(address);
            body2.setData(jsonString2);
            duBodies.add(body2);
        }


        return duBodies;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        OrderStrategyFactory.register("STATE_ORDER_EXECUTE", this);
    }


    private void cacheOrderNo(HwOrderMessage orderMessage) {
        String orderNo = orderMessage.getOrderNo();
        if (orderNo.startsWith("ZC")) {
            redisClient.set(RedisKeyUtils.getOrderKey(orderMessage.getOrderId().toString()), orderMessage.getOrderNo(), RedisKeyUtils.expireTime5H);
            return;
        }
        if (orderNo.startsWith("PC")) {
            BizDriverDTO dto = redisClient.get(RedisKeyUtils.getDriverKey(orderMessage.getDriverId()), BizDriverDTO.class);
            if (null == dto || StringUtils.isBlank(dto.getPhoneNo())) {
                return;
            }
            redisClient.set(RedisKeyUtils.getOrderPhoneKey(dto.getPhoneNo()), orderMessage.getOrderNo(), RedisKeyUtils.expireTime5H);
        }
    }


    private DuOperateDepart convertToDuOperateDepart(HwOrderMessage message) {

        DuOperateDepart operateDepart = new DuOperateDepart();
        operateDepart.setOrderId(message.getOrderNo());

        // 司机缓存查询
        BizDriverDTO driver = bizDriverService.getCacheDataById(message.getDriverId());
        if (null == driver || 0 != driver.getCheckState() || null == driver.getApproveDate()) {
            return null;
        }
        operateDepart.setLicenseId(driver.getLicenseNo());
        operateDepart.setDriverIDCard(driver.getLicenseNo());
        operateDepart.setDriverCertCard(driver.getCertificateNo());
        // 车辆缓存查询
        BizCarDTO vehicle = bizCarService.getCacheDataById(driver.getVehicleId());
        if (null != vehicle) {
            operateDepart.setVehicleNo(vehicle.getVehicleNo());
            operateDepart.setPlateColor(vehicle.getPlateColor());
        }

        // 通过 memberShipID 查询
        BizMembershipDTO passenger = bizMembershipService.getCacheDataById(message.getMembershipId());
        if (null != passenger) {
            operateDepart.setPassengerPhone(passenger.getPhoneNo());
        }

        operateDepart.setDepLongitude(CoordinateUtil.getIntegerCoordinate(message.getSlongitude()));
        operateDepart.setDepLatitude(CoordinateUtil.getIntegerCoordinate(message.getSlatitude()));
        // 出发时间为空 用事件时间作为出发时间
        long depTime = null == message.getStartDate() ? message.getActionEventDate() : message.getStartDate();
        operateDepart.setDepTime(RTimeUtil.milliTimestamp2Long(depTime));
        operateDepart.setOrderTime(RTimeUtil.milliTimestamp2Long(message.getCreateDate()));

        operateDepart.setEncrypt(1);
        operateDepart.setFareType("1");
        operateDepart.setPassengerWaitTime(0);
        operateDepart.setDriveWaitTime(0);
        operateDepart.setWaitMile(BigDecimal.ZERO);
        operateDepart.setWaitTime(0);

        return operateDepart;

    }

    private DuOperateLogin convertToDuOperateLogin(HwOrderMessage message) {
        DuOperateLogin operateLogin = new DuOperateLogin();
        // 司机缓存查询
        BizDriverDTO driver = bizDriverService.getCacheDataById(message.getDriverId());
        if (null == driver || 0 != driver.getCheckState() || null == driver.getApproveDate()) {
            return null;
        }

        operateLogin.setLicenseId(driver.getLicenseNo());
        // TODO 先默认呼我总部的社会信用码
        operateLogin.setIdentifier(HwStaticInfo.IDENTIFIER);
        operateLogin.setDriverIDCard(driver.getLicenseNo());
        operateLogin.setDriverCertCard(driver.getCertificateNo());
        operateLogin.setDriverPhone(driver.getPhoneNo());
        // 车辆缓存查询
        BizCarDTO vehicle = bizCarService.getCacheDataById(driver.getVehicleId());
        if (null != vehicle) {
            operateLogin.setVehicleNo(vehicle.getVehicleNo());
            operateLogin.setPlateColor(vehicle.getPlateColor());
            operateLogin.setBrand(vehicle.getBrand());
        }

        operateLogin.setEncrypt(1);
        long loginTime = null == message.getStartDate() ? message.getActionEventDate() : message.getStartDate();
        operateLogin.setLoginTime(RTimeUtil.milliTimestamp2Long(loginTime));
        operateLogin.setOnWorkTime(operateLogin.getLoginTime());
        operateLogin.setLongitude(CoordinateUtil.getIntegerCoordinate(message.getSlongitude()));
        operateLogin.setLatitude(CoordinateUtil.getIntegerCoordinate(message.getSlatitude()));

        return operateLogin;
    }


}
