package com.huwo.gateway.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huwo.gateway.domain.UpstreamBaseConfig;
import com.huwo.gateway.service.UpstreamBaseConfigService;
import com.huwo.gateway.service.event.HwOrderEventService;
import com.huwo.gateway.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 上报基础配置监听器
 * @Author: liuchang
 * @CreateTime: 2022-09-29  09:41
 */

public class UpstreamBaseConfigSubscribe implements MessageListener {


    UpstreamBaseConfigService upstreamBaseConfigService = SpringUtils.getBean(UpstreamBaseConfigService.class);

    private static UpstreamBaseConfigSubscribe instance;

    private UpstreamBaseConfigSubscribe() {
    }


    private ConcurrentHashMap<String, UpstreamBaseConfig> concurrentHashMap = new ConcurrentHashMap<>();

    public static UpstreamBaseConfigSubscribe getInstance() {
        if (instance == null) {
            synchronized (UpstreamBaseConfigSubscribe.class) { // 此处为类级别的锁
                if (instance == null) {
                    instance = new UpstreamBaseConfigSubscribe();
                }
            }
        }
        return instance;
    }

    public ConcurrentHashMap<String, UpstreamBaseConfig> getConcurrentHashMap() {
        if (concurrentHashMap.isEmpty()) {
            QueryWrapper<UpstreamBaseConfig> queryWrapper = new QueryWrapper<UpstreamBaseConfig>();
            queryWrapper.eq("is_up_flag", false);
            List<UpstreamBaseConfig> list = upstreamBaseConfigService.list(queryWrapper);
            list.forEach(upstreamBaseConfig -> concurrentHashMap.putIfAbsent(upstreamBaseConfig.getAdCode(), upstreamBaseConfig));
        }
        return concurrentHashMap;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println(Thread.currentThread().getName());
        System.out.println("订阅频道:" + new String(message.getChannel()));
        System.out.println("接收数据:" + new String(message.getBody()));

        String data = new String(message.getBody());
        String parse = (String) JSON.parse(data);
        UpstreamBaseConfig upstreamBaseConfig = JSON.parseObject(parse, UpstreamBaseConfig.class);


        //使用的是CopyOnWriteArrayList这个并发修改类
        if (!concurrentHashMap.isEmpty()) {
            if (concurrentHashMap.keySet().contains(upstreamBaseConfig.getAdCode()) && upstreamBaseConfig.getIsUpFlag()) {
                concurrentHashMap.remove(upstreamBaseConfig.getAdCode());
            } else {
                if(!upstreamBaseConfig.getIsUpFlag()){
                    concurrentHashMap.putIfAbsent(upstreamBaseConfig.getAdCode(), upstreamBaseConfig);
                }
            }
        } else {
            if(!upstreamBaseConfig.getIsUpFlag()){
                concurrentHashMap.putIfAbsent(upstreamBaseConfig.getAdCode(), upstreamBaseConfig);
            }
        }
        System.out.println(concurrentHashMap);

    }
}
