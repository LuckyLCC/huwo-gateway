package com.huwo.gateway.strategy.position;

import com.alibaba.fastjson.JSON;
import com.huwo.data.upstream.api.biz.DuPositionDriver;
import com.huwo.data.upstream.api.biz.DuPositionVehicle;
import com.huwo.data.upstream.api.common.ChannelEnum;
import com.huwo.data.upstream.api.common.IPCTypeEnum;
import com.huwo.gateway.common.DuBody;
import com.huwo.gateway.common.HwPositionMessage;
import com.huwo.gateway.domain.dto.BizCarDTO;
import com.huwo.gateway.domain.dto.BizDriverDTO;
import com.huwo.gateway.domain.dto.SysDepartmentDTO;
import com.huwo.gateway.factory.PositionStrategyFactory;
import com.huwo.gateway.service.BizCarService;
import com.huwo.gateway.service.BizDriverService;
import com.huwo.gateway.service.SysDepartmentService;
import com.huwo.gateway.utils.CoordinateUtil;
import com.huwo.gateway.utils.RTimeUtil;
import com.huwo.gateway.utils.RedisClient;
import com.huwo.gateway.utils.RedisKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-20  10:57
 */
@Component
@Slf4j
public class PositionDriverStrategy extends IPositionStrategy {

    @Autowired
    private BizCarService bizCarService;

    @Autowired
    private BizDriverService bizDriverService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Override
    protected List<DuBody> handleData(HwPositionMessage positionMessage) {
        String driverPhone = positionMessage.getVehicleID();
        if (!driverPhone.startsWith("1") || driverPhone.contains("c")) {
            log.debug("非司机/车辆定位信息：{}，{}", driverPhone, toJSONString(positionMessage));
            return null;
        }
        driverPhone = driverPhone.replace("d", "");
        BizDriverDTO driver = bizDriverService.getByPhone(driverPhone);
        // 没有司机信息 --> 不上报
        if (null == driver) {
            return null;
        }
        // 获取订单号，
        String orderNo = getOrderNo(positionMessage.getOrderID(), driverPhone);
        // 缓存司机位置, 在线时长
        cacheDriverPosition(positionMessage, driver, orderNo);
        // 没有订单号, 司机审核状态不为0, 没有审核时间 --> 不上报
        if (StringUtils.isBlank(orderNo) || 0 != driver.getCheckState() || null == driver.getApproveDate()) {
            return null;
        }
        // 没有车辆信息，车辆注册地为空 --> 不上报
        BizCarDTO car = bizCarService.getCacheDataById(driver.getVehicleId());
        if (null == car || null == car.getVehicleRegionCode()) {
            return null;
        }
        log.info("收到呼我定位：{}", JSON.toJSONString(positionMessage));

        //组装DuBody
        DuBody driverDuBody = driverHandle(positionMessage, driver, car, orderNo);
        DuBody VehicleDuBody = VehicleHandle(positionMessage, driver, car, orderNo);

        return Arrays.asList(driverDuBody,VehicleDuBody);
    }

    private DuBody VehicleHandle(HwPositionMessage vehicle, BizDriverDTO driver, BizCarDTO car, String orderNo) {
        DuPositionDriver positionDriver = new DuPositionDriver();
        positionDriver.setLicenseId(driver.getLicenseNo());
        positionDriver.setDriverRegionCode(driver.getAdCode());
        positionDriver.setVehicleNo(car.getVehicleNo());
        positionDriver.setPositionTime(RTimeUtil.milliTimestamp2Long(vehicle.getTimestamp()));
        String[] split = vehicle.getLocation().split(",");
        positionDriver.setLongitude(CoordinateUtil.getIntegerCoordinate(split[0]));
        positionDriver.setLatitude(CoordinateUtil.getIntegerCoordinate(split[1]));
        positionDriver.setEncrypt(1);
        positionDriver.setOrderId(orderNo);
        positionDriver.setPlateColor(car.getPlateColor());
        positionDriver.setVehicleRegionCode(car.getVehicleRegionCode());
        positionDriver.setDriverId(driver.getId().toString());
        positionDriver.setDriverCertCard(driver.getCertificateNo());
        // TODO 车型和定位类型要转换
        positionDriver.setVehicleType(vehicle.getVehicleType().toString());
        positionDriver.setPositionType("AUTO");
        positionDriver.setValidity(1);
        positionDriver.setBizStatus(1);
        positionDriver.setSpeed(vehicle.getSpeed());
        positionDriver.setDirection(vehicle.getDirection());



        //组装DuBody
        String jsonString = JSON.toJSONString(positionDriver);
        DuBody body = new DuBody();
        body.setIpcType(IPCTypeEnum.POSITION_VEHICLE.getType());
        body.setChannel(ChannelEnum.HW.name());
        body.setAddress(positionDriver.getDriverRegionCode());
        body.setData(jsonString);

        return body;

    }

    private DuBody driverHandle(HwPositionMessage vehicle, BizDriverDTO driver, BizCarDTO car, String orderNo) {
        DuPositionVehicle positionVehicle = new DuPositionVehicle();
        positionVehicle.setVehicleNo(car.getVehicleNo());
        positionVehicle.setVehicleRegionCode(car.getVehicleRegionCode());
        positionVehicle.setPositionTime(RTimeUtil.milliTimestamp2Long(vehicle.getTimestamp()));
        String[] split = vehicle.getLocation().split(",");
        positionVehicle.setLongitude(CoordinateUtil.getIntegerCoordinate(split[0]));
        positionVehicle.setLatitude(CoordinateUtil.getIntegerCoordinate(split[1]));
        positionVehicle.setEncrypt(1);
        positionVehicle.setOrderId(orderNo);
        positionVehicle.setDirection(vehicle.getDirection());
        positionVehicle.setSpeed(vehicle.getSpeed());
        positionVehicle.setWarnStatus(0);
        positionVehicle.setVehStatus(1);
        positionVehicle.setBizStatus(1);
        positionVehicle.setRegisterAreaCode(driver.getAdCode());

        //组装DuBody
        String jsonString = JSON.toJSONString(positionVehicle);
        DuBody body = new DuBody();
        body.setIpcType(IPCTypeEnum.POSITION_DRIVER.getType());
        body.setChannel(ChannelEnum.HW.name());
        body.setAddress(positionVehicle.getVehicleRegionCode());
        body.setData(jsonString);
        return body;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PositionStrategyFactory.register("POSITION", this);
    }


    private String getOrderNo(String orderId, String driverPhone) {
        String orderNo = redisClient.get(RedisKeyUtils.getOrderKey(orderId));
        if (StringUtils.isNotBlank(orderNo)) {
            return orderNo;
        }

        return redisClient.get(RedisKeyUtils.getOrderPhoneKey(driverPhone));
    }

    private void cacheDriverPosition(HwPositionMessage positionMessage, BizDriverDTO driver, String orderNo) {

        if (null != driver.getDepartmentId() && 0 == driver.getCheckState()) {
            SysDepartmentDTO department = sysDepartmentService.getCacheDataById(driver.getDepartmentId());
            long timestamp = positionMessage.getTimestamp() / 1000;
            // 缓存司机在线时长
            cacheOnlineDay(driver.getPhoneNo(), timestamp);
            // 缓存司机位置信息
            cachePosition(positionMessage, driver, orderNo, department, timestamp);
        }
    }

    private void cacheOnlineDay(String driverPhone, long timestamp) {

        // 获取到最后更新时间
        String onlineLastKey = RedisKeyUtils.getOnlineLastKey(driverPhone);
        LocalDateTime lastOnlineTime = redisClient.get(onlineLastKey, LocalDateTime.class);
        // 当前定位时间
        LocalDateTime newTime = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.of("+8"));
        // 当前定位时间为空 -> 设置最后在线时间并返回
        if (null == lastOnlineTime) {
            redisClient.set(onlineLastKey, newTime);
            return;
        }
        // 定位时间大于最后更新时间
        if (newTime.isAfter(lastOnlineTime)) {
            redisClient.set(onlineLastKey, newTime);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            long interval = ChronoUnit.SECONDS.between(lastOnlineTime.toInstant(ZoneOffset.of("+8")), newTime.toInstant(ZoneOffset.of("+8")));
            String dayKey = RedisKeyUtils.getOnlineDayKey(newTime);
            // 间隔时间小于 指定值 -> 说明是连续在线日在线时长加上 interval ，否则 -> 说明已下线，不作任何处理；
            if (interval <= 600) {
                // 日期在同一天
                if (lastOnlineTime.toLocalDate().equals(newTime.toLocalDate())) {
                    redisClient.hashIncr(dayKey, driverPhone, interval);
                }
                // 日期不在同一天, 分两段加
                else {
                    // 前一天日期key
                    String lastDayKey = lastOnlineTime.format(dateTimeFormatter);
                    long lastOnline = ChronoUnit.SECONDS.between(newTime, LocalDateTime.of(newTime.toLocalDate(), LocalTime.MIN));
                    long nextOnline = ChronoUnit.SECONDS.between(LocalDateTime.of(newTime.toLocalDate(), LocalTime.MIN), lastOnlineTime);
                    redisClient.hashIncr(lastDayKey, driverPhone, lastOnline);
                    redisClient.hashIncr(dayKey, driverPhone, nextOnline);
                }
            }
        }
    }

    private void cachePosition(HwPositionMessage positionMessage, BizDriverDTO driver, String orderNo, SysDepartmentDTO department, long timestamp) {
        if (LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")) - timestamp < 300) {
            String positionTime = RTimeUtil.timestamp2FormatStr(timestamp, "HHmm", false);
            String[] location = positionMessage.getLocation().split(",");
            int status = StringUtils.isBlank(orderNo) ? 1 : 2;
            // key规则：dpd:分公司名称：时间（时分-HHmm）
            String key = String.format("dpd%s%s", department.getName().replace("分公司", ""), positionTime);
            String hashKey = "d" + driver.getPhoneNo();
            // 值规则： 司机编号（d+手机号），经度，纬度，时间戳，状态（1：空闲，2：忙碌）
            String value = String.format("%s,%s,%s,%s,%d", hashKey, location[0], location[1], positionMessage.getTimestamp(), status);
            redisClient.hashSet(key, hashKey, value, 180L);
        }
    }


}
