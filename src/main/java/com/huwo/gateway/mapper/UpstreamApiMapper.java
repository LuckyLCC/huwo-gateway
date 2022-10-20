package com.huwo.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huwo.gateway.domain.UpstreamApi;
import com.huwo.gateway.domain.UpstreamBaseConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UpstreamApiMapper extends BaseMapper<UpstreamApi> {
}
