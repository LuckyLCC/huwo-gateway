package com.huwo.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huwo.gateway.domain.UpstreamBaseConfig;
import com.huwo.gateway.domain.UpstreamField;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UpstreamFieldMapper extends BaseMapper<UpstreamField> {
}
