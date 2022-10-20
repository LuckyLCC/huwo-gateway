package com.huwo.gateway.service.event;

import com.huwo.gateway.strategy.order.IOrderStrategy;
import com.huwo.gateway.domain.dto.SysDepartmentDTO;
import com.huwo.gateway.common.HwOrderMessage;
import com.huwo.gateway.factory.OrderStrategyFactory;
import com.huwo.gateway.service.SysDepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-17  18:15
 */
@Service
@Slf4j
public class HwOrderEventService {

    @Autowired
    private SysDepartmentService sysDepartmentService;

    public void handleEvent(String actionEvent, HwOrderMessage orderMessage) {

        //过滤条件
        if (filter(orderMessage)) {
            return;
        }

        // 缓存查询 未查询到部门或者部门adCode为空 返回
        SysDepartmentDTO department = sysDepartmentService.getCacheDataById(orderMessage.getDepartmentId());
        if (null == department || null == department.getAdCode()) {
            return;
        }
        Integer address = department.getAdCode();

        IOrderStrategy strategy = OrderStrategyFactory.getStrategy(actionEvent);
        strategy.handleAndSendData(orderMessage, address);

    }

    private boolean filter(HwOrderMessage orderMessage) {
        // 订单ID为空，或者是快车主单 返回
        if (StringUtils.isBlank(orderMessage.getOrderNo()) || (null != orderMessage.getIsKcMaster() && orderMessage.getIsKcMaster())) {
            return true;
        }

        //控制快车主单不上报
        if (orderMessage.getMembershipId() == null) {
            log.info("--------------该订单为快车主单，membershipId为null---------------");
            return true;
        }
        if (orderMessage.getMasterId() == null || orderMessage.getMasterId() == 0) {
            log.info("---------------该订单为快车主单，masterId为{}-----------------", orderMessage.getMasterId());
            return true;
        }
        if (orderMessage.getIsKcMaster()) {
            log.info("---------------该订单为快车主单，isKcMaster为true-----------------");
            return true;
        }
        //控制平湖快车不上报
        if (StringUtils.isNotBlank(orderMessage.getOrderNo())) {
            if (orderMessage.getOrderNo().startsWith("BC02204")) {
                return true;
            } else if (orderMessage.getOrderNo().startsWith("PC02203")) {
                return true;
            } else if (orderMessage.getOrderNo().startsWith("PC02301")) {
                return true;
            } else if (orderMessage.getOrderNo().startsWith("BC02404")) {
                return true;
            }
        }
        return false;
    }
}
