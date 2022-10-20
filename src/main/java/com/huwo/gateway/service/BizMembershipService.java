package com.huwo.gateway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huwo.gateway.domain.dto.BizMembershipDTO;

public interface BizMembershipService extends IService<BizMembershipDTO> {

     BizMembershipDTO getCacheDataById(Long id);
}
