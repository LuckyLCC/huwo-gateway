package com.huwo.gateway.factory;

import com.huwo.gateway.strategy.order.IOrderStrategy;
import com.huwo.gateway.strategy.position.IPositionStrategy;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-20  11:30
 */
public class PositionStrategyFactory {

    private static final Map<String, IPositionStrategy> strategies = new HashMap<>();


    public static IPositionStrategy getStrategy(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("type should not be empty.");
        }
        return strategies.get(type);
    }

    public static  void register(String name, IPositionStrategy iStrategy){
        if (!StringUtils.hasText(name) || null == iStrategy) {
            return;
        }
        strategies.put(name,iStrategy);

    }
}
