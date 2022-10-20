package com.huwo.gateway.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huwo.gateway.domain.dto.BizCarDTO;
import com.huwo.gateway.domain.dto.SysDepartmentDTO;
import com.huwo.gateway.mapper.BizCarMapper;
import com.huwo.gateway.service.BizCarService;
import com.huwo.gateway.service.SysDepartmentService;
import com.huwo.gateway.utils.RedisClient;
import com.huwo.gateway.utils.RedisKeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-19  16:30
 */
@Service
@DS("slave_1")
public class BizCarServiceImpl extends ServiceImpl<BizCarMapper, BizCarDTO> implements BizCarService {

    @Autowired
    RedisClient redisClient;

    @Autowired
    private BizCarMapper bizCarMapper;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Override
    public BizCarDTO getCacheDataById(Long id) {

        String key = RedisKeyUtils.getVehicleKey(id);
        BizCarDTO dto = redisClient.get(key, BizCarDTO.class);
        if (null == dto) {
            dto = bizCarMapper.getBizCarById(id);
            if (null != dto) {
                if (StringUtils.isBlank(dto.getCertificateNo())) {
                    dto.setCertificateNo("无");
                }
                if (StringUtils.isBlank(dto.getBrand())) {
                    dto.setBrand("无");
                }
                if (StringUtils.isBlank(dto.getPlateColor())) {
                    // 1-蓝色，5-绿色
                    dto.setPlateColor(dto.getVehicleNo().length() > 7 ? "5" : "1");
                }
                SysDepartmentDTO carDepartment = sysDepartmentService.getCacheDataById(dto.getDepartmentId());
                if (null != carDepartment && null != carDepartment.getAdCode()) {
                    dto.setVehicleRegionCode(carDepartment.getAdCode());
                }
                redisClient.set(key, dto, RedisKeyUtils.expireTime);
            }
        }
        return dto;

    }



}
