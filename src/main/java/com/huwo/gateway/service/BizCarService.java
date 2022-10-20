package com.huwo.gateway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huwo.gateway.domain.dto.BizCarDTO;

public interface BizCarService extends IService<BizCarDTO> {

    BizCarDTO getCacheDataById(Long id);
}
