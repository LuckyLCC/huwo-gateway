package com.huwo.gateway.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huwo.gateway.domain.PlateForm;
import com.huwo.gateway.domain.UpstreamBaseConfig;
import com.huwo.gateway.service.PlateFormService;
import com.huwo.gateway.service.UpstreamBaseConfigService;
import com.huwo.gateway.utils.SpringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 平台配置监听器
 * @Author: liuchang
 * @CreateTime: 2022-10-20  09:40
 */
public class PlateFormSubscribe implements MessageListener {

    PlateFormService plateFormService = SpringUtils.getBean(PlateFormService.class);

    private static PlateFormSubscribe instance;

    private PlateFormSubscribe() {
    }

    private ConcurrentHashMap<String, PlateForm> concurrentHashMap = new ConcurrentHashMap<>();

    public static PlateFormSubscribe getInstance() {
        if (instance == null) {
            synchronized (PlateFormSubscribe.class) { // 此处为类级别的锁
                if (instance == null) {
                    instance = new PlateFormSubscribe();
                }
            }
        }
        return instance;
    }

    public ConcurrentHashMap<String, PlateForm> getConcurrentHashMap() {
        if (concurrentHashMap.isEmpty()) {
            QueryWrapper<PlateForm> queryWrapper = new QueryWrapper<PlateForm>();
            queryWrapper.eq("is_up_flag", false);
            List<PlateForm> list = plateFormService.list(queryWrapper);
            list.forEach(plateForm -> concurrentHashMap.putIfAbsent(plateForm.getChannel(), plateForm));
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
        PlateForm plateForm = JSON.parseObject(parse, PlateForm.class);


        //使用的是CopyOnWriteArrayList这个并发修改类
        if (!concurrentHashMap.isEmpty()) {
            if (concurrentHashMap.keySet().contains(plateForm.getChannel()) && plateForm.getIsUpFlag()) {
                concurrentHashMap.remove(plateForm.getChannel());
            } else {
                if(!plateForm.getIsUpFlag()){
                    concurrentHashMap.putIfAbsent(plateForm.getChannel(), plateForm);
                }
            }
        } else {
            if(!plateForm.getIsUpFlag()){
                concurrentHashMap.putIfAbsent(plateForm.getChannel(), plateForm);
            }
        }
        System.out.println(JSON.toJSONString(concurrentHashMap));
    }
}
