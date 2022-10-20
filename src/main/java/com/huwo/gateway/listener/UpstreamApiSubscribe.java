//package com.huwo.gateway.listener;
//
//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.huwo.gateway.domain.UpstreamApi;
//import com.huwo.gateway.domain.UpstreamBaseConfig;
//import com.huwo.gateway.service.UpstreamApiService;
//import com.huwo.gateway.service.UpstreamBaseConfigService;
//import com.huwo.gateway.utils.SpringUtils;
//import org.springframework.data.redis.connection.Message;
//import org.springframework.data.redis.connection.MessageListener;
//
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @Description: TODO
// * @Author: liuchang
// * @CreateTime: 2022-09-29  09:41
// */
//
//public class UpstreamApiSubscribe implements MessageListener {
//
//
//    UpstreamApiService upstreamApiService = SpringUtils.getBean(UpstreamApiService.class);
//
//    private static UpstreamApiSubscribe instance;
//
//    private UpstreamApiSubscribe() {
//    }
//
//
//    private ConcurrentHashMap<String, UpstreamApi> concurrentHashMap = new ConcurrentHashMap<>();
//
//    public static UpstreamApiSubscribe getInstance() {
//        if (instance == null) {
//            synchronized (UpstreamApiSubscribe.class) { // 此处为类级别的锁
//                if (instance == null) {
//                    instance = new UpstreamApiSubscribe();
//                }
//            }
//        }
//        return instance;
//    }
//
//    public ConcurrentHashMap<String, UpstreamApi> getConcurrentHashMap() {
//        if (concurrentHashMap.isEmpty()) {
//            QueryWrapper<UpstreamApi> queryWrapper = new QueryWrapper<UpstreamApi>();
//            queryWrapper.eq("is_up_flag", false);
//            List<UpstreamApi> list = upstreamApiService.list(queryWrapper);
//            list.forEach(upstreamApi -> concurrentHashMap.putIfAbsent(upstreamApi.getAdCode()+"-"+upstreamApi.getType(), upstreamApi));
//        }
//        return concurrentHashMap;
//    }
//
//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//        System.out.println(Thread.currentThread().getName());
//        System.out.println("订阅频道:" + new String(message.getChannel()));
//        System.out.println("接收数据:" + new String(message.getBody()));
//
//        String data = new String(message.getBody());
//        String parse = (String) JSON.parse(data);
//        UpstreamApi upstreamApi = JSON.parseObject(parse, UpstreamApi.class);
//
//
//        //使用的是CopyOnWriteArrayList这个并发修改类
//        if (!concurrentHashMap.isEmpty()) {
//            if (concurrentHashMap.keySet().contains(upstreamApi.getAdCode()+"-"+upstreamApi.getType()) && upstreamApi.getIsUpFlag()) {
//                concurrentHashMap.remove(upstreamApi.getAdCode()+"-"+upstreamApi.getType());
//            } else {
//                if(!upstreamApi.getIsUpFlag()){
//                    concurrentHashMap.putIfAbsent(upstreamApi.getAdCode()+"-"+upstreamApi.getType(), upstreamApi);
//                }
//            }
//        } else {
//            if(!upstreamApi.getIsUpFlag()){
//                concurrentHashMap.putIfAbsent(upstreamApi.getAdCode()+"-"+upstreamApi.getType(), upstreamApi);
//            }
//        }
//        System.out.println(concurrentHashMap);
//
//    }
//}
