package com.huwo.gateway;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huwo.gateway.domain.dto.BizCarDTO;
import com.huwo.gateway.domain.dto.BizDriverDTO;
import com.huwo.gateway.domain.dto.SysDepartmentDTO;
import com.huwo.gateway.domain.UpstreamBaseConfig;
import com.huwo.gateway.mapper.BizCarMapper;
import com.huwo.gateway.mapper.BizDriverMapper;
import com.huwo.gateway.mapper.SysDepartmentMapper;
import com.huwo.gateway.service.SysDepartmentService;
import com.huwo.gateway.service.UpstreamBaseConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class HuwoGatewayApplicationTests {

    @Autowired
    private SysDepartmentMapper hwDepartmentMapper;


    @Autowired
    private SysDepartmentService hwDepartmentService;

    @Autowired
    private UpstreamBaseConfigService upstreamBaseConfigService;

    @Autowired
    private BizDriverMapper bizDriverMapper;
    @Autowired
    private BizCarMapper bizCarMapper;

//    @Autowired
//    private UpstreamBaseConfigSubscribe upstreamBaseConfigSubscribe;
    @Test
    void contextLoads() {
        SysDepartmentDTO byId = hwDepartmentService.getById(1);
        System.out.println(byId);
//        List<SysDepartment> sysDepartments = hwDepartmentMapper.selectList(null);
//        sysDepartments.forEach(System.out::println);
    }

    @Test
    void start1() {
        UpstreamBaseConfig build = UpstreamBaseConfig.builder().adCode("520600").cityName("国家").isUpFlag(true).build();
        upstreamBaseConfigService.save(build);
    }
    @Test
    void start2() {
        QueryWrapper<UpstreamBaseConfig> queryWrapper = new QueryWrapper<UpstreamBaseConfig>();
        queryWrapper.eq("is_up_flag",false);
        List<UpstreamBaseConfig> list = upstreamBaseConfigService.list(queryWrapper);
        System.out.println(list);
    }

    @Test
    void start3() {
        BizDriverDTO bizDriverById = bizDriverMapper.getBizDriverById(128L);
        System.out.println(bizDriverById);
    }

    @Test
    void start4() {
        BizCarDTO bizCarById = bizCarMapper.getBizCarById(87L);
        System.out.println(bizCarById);
    }




}
