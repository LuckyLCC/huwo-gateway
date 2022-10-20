package com.huwo.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huwo.gateway.domain.dto.BizMembershipDTO;
import com.huwo.gateway.mapper.BizMembershipMapper;
import com.huwo.gateway.service.BizMembershipService;
import com.huwo.gateway.utils.RedisClient;
import com.huwo.gateway.utils.RedisKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-18  15:53
 */
@Service
@DS("slave_1")
@Slf4j
public class BizMembershipServiceImpl extends ServiceImpl<BizMembershipMapper, BizMembershipDTO> implements BizMembershipService {

    @Autowired
    RedisClient redisClient;

    @Autowired
    private BizMembershipMapper bizMembershipMapper;

    @Override
    public BizMembershipDTO getCacheDataById(Long id) {

        String key = RedisKeyUtils.getPassengerKey(id);
        BizMembershipDTO dto = redisClient.get(key, BizMembershipDTO.class);
        if (null == dto) {
            dto = bizMembershipMapper.selectById(id);
            if (null != dto) {
                redisClient.set(key, dto, RedisKeyUtils.expireTime);
            }
        }
        return dto;
    }
}
