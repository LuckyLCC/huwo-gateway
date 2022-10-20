package com.huwo.gateway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huwo.gateway.domain.dto.BizDriverDTO;

public interface BizDriverService  extends IService<BizDriverDTO> {
    BizDriverDTO getCacheDataById(Long driverId);

    BizDriverDTO getByPhone(String driverPhone);
}
