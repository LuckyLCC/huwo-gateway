package com.huwo.gateway.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huwo.gateway.domain.dto.BizDriverDTO;
import com.huwo.gateway.domain.dto.SysDepartmentDTO;
import com.huwo.gateway.mapper.BizDriverMapper;
import com.huwo.gateway.service.BizDriverService;
import com.huwo.gateway.service.SysDepartmentService;
import com.huwo.gateway.utils.RedisClient;
import com.huwo.gateway.utils.RedisKeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-19  17:28
 */
@Service
@DS("slave_1")
public class BizDriverServiceImpl  extends ServiceImpl<BizDriverMapper, BizDriverDTO> implements BizDriverService {

    @Autowired
    RedisClient redisClient;

    @Autowired
    private BizDriverMapper bizDriverMapper;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Override
    public BizDriverDTO getCacheDataById(Long id) {

        String key = RedisKeyUtils.getDriverKey(id);
        BizDriverDTO dto = redisClient.get(key, BizDriverDTO.class);
        if (null == dto) {
            dto = bizDriverMapper.getBizDriverById(id);
            if (null != dto) {
                if (StringUtils.isBlank(dto.getCertificateNo())){
                    dto.setCertificateNo("无");
                }
                SysDepartmentDTO driverDepartment = sysDepartmentService.getCacheDataById(dto.getDepartmentId());
                if (null != driverDepartment && null != driverDepartment.getAdCode()) {
                    dto.setAdCode(driverDepartment.getAdCode());
                }
//            Long expireTime = RedisKey.expireTime;
                // 先改为10分钟
                Long expireTime = 600L;
                String key2 = RedisKeyUtils.getDriverKey(dto.getId());
                redisClient.set(key2, dto, expireTime);
                String phoneKey = RedisKeyUtils.getDriverPhoneKey(dto.getPhoneNo());
                redisClient.set(phoneKey, dto, expireTime);
            }
        }
        return dto;
    }

    @Override
    public BizDriverDTO getByPhone(String driverPhone) {
        String key = RedisKeyUtils.getDriverPhoneKey(driverPhone);
        BizDriverDTO dto = redisClient.get(key, BizDriverDTO.class);
        if (null == dto) {
            dto = bizDriverMapper.getByDriverPhone(driverPhone);
            if (null != dto) {
                if (StringUtils.isBlank(dto.getCertificateNo())){
                    dto.setCertificateNo("无");
                }
                SysDepartmentDTO driverDepartment = sysDepartmentService.getCacheDataById(dto.getDepartmentId());
                if (null != driverDepartment && null != driverDepartment.getAdCode()) {
                    dto.setAdCode(driverDepartment.getAdCode());
                }
//            Long expireTime = RedisKey.expireTime;
                // 先改为10分钟
                Long expireTime = 600L;
                String driverKey = RedisKeyUtils.getDriverKey(dto.getId());
                redisClient.set(driverKey, dto, expireTime);
                String phoneKey = RedisKeyUtils.getDriverPhoneKey(dto.getPhoneNo());
                redisClient.set(phoneKey, dto, expireTime);
            }
        }
        return dto;
    }


}
