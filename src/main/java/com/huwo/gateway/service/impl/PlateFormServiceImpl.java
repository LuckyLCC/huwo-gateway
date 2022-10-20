package com.huwo.gateway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huwo.gateway.domain.PlateForm;
import com.huwo.gateway.domain.dto.BizMembershipDTO;
import com.huwo.gateway.mapper.BizMembershipMapper;
import com.huwo.gateway.mapper.PlateFormMapper;
import com.huwo.gateway.service.BizMembershipService;
import com.huwo.gateway.service.PlateFormService;
import org.springframework.stereotype.Service;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-20  09:49
 */
@Service
public class PlateFormServiceImpl extends ServiceImpl<PlateFormMapper, PlateForm> implements PlateFormService {
}
