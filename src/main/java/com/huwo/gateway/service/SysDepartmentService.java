package com.huwo.gateway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huwo.gateway.domain.dto.SysDepartmentDTO;

public interface SysDepartmentService extends IService<SysDepartmentDTO> {

    SysDepartmentDTO getCacheDataById(Long id);
}
