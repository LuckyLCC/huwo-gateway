package com.huwo.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huwo.gateway.mapper.SysDepartmentMapper;
import com.huwo.gateway.domain.dto.SysDepartmentDTO;
import com.huwo.gateway.service.SysDepartmentService;
import com.huwo.gateway.utils.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-17  18:43
 */
@Service
@DS("slave_1")
public class SysDepartmentServiceImpl extends ServiceImpl<SysDepartmentMapper, SysDepartmentDTO> implements SysDepartmentService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;



    public SysDepartmentDTO getCacheDataById(Long id) {

        String key = RedisKeyUtils.getDepartmentKey(id);
        String value = redisTemplate.opsForValue().get(key);
        SysDepartmentDTO sysDepartment = JSON.parseObject(value, SysDepartmentDTO.class);
        if (null == sysDepartment) {
            sysDepartment = sysDepartmentMapper.selectById(id);
            if (null != sysDepartment) {
                String jsonString = JSON.toJSONString(sysDepartment);
                redisTemplate.opsForValue().set(key, jsonString, (5 * 60 + new Random().nextInt(30)) * 60, TimeUnit.SECONDS);
            }
        }
        return sysDepartment;
    }
}
