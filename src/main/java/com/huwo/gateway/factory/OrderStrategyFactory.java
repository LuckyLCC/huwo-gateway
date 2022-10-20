package com.huwo.gateway.factory;

import com.huwo.gateway.strategy.order.IOrderStrategy;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-14  14:50
 */

public class OrderStrategyFactory {


    private static final Map<String, IOrderStrategy> strategies = new HashMap<>();


    public static IOrderStrategy getStrategy(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("type should not be empty.");
        }
        return strategies.get(type);
    }

    public static  void register(String name, IOrderStrategy iStrategy){
        if (!StringUtils.hasText(name) || null == iStrategy) {
           return;
        }
        strategies.put(name,iStrategy);

    }



}
