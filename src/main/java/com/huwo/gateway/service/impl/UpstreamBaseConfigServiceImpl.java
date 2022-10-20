package com.huwo.gateway.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huwo.gateway.domain.UpstreamBaseConfig;
import com.huwo.gateway.mapper.UpstreamBaseConfigMapper;
import com.huwo.gateway.service.UpstreamBaseConfigService;
import org.springframework.stereotype.Service;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-18  17:17
 */
@Service
public class UpstreamBaseConfigServiceImpl extends ServiceImpl<UpstreamBaseConfigMapper, UpstreamBaseConfig> implements UpstreamBaseConfigService {
}
