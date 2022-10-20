package com.huwo.gateway.strategy.order;

import com.alibaba.fastjson.JSON;
import com.huwo.data.upstream.api.common.ChannelEnum;
import com.huwo.data.upstream.api.common.IPCTypeEnum;
import com.huwo.gateway.common.DuBody;
import com.huwo.gateway.common.HwOrderMessage;
import com.huwo.gateway.common.HwPositionMessage;
import com.huwo.gateway.domain.PlateForm;
import com.huwo.gateway.domain.UpstreamBaseConfig;
import com.huwo.gateway.listener.PlateFormSubscribe;
import com.huwo.gateway.listener.UpstreamBaseConfigSubscribe;
import com.huwo.gateway.utils.KafkaUtils;
import com.mysql.cj.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class IOrderStrategy implements InitializingBean {

    //数据上报
    public void handleAndSendData(HwOrderMessage sourceData, Integer address) {

        //处理数据
        List<DuBody> duBodyList = handleData(sourceData, address);

        if (CollectionUtils.isEmpty(duBodyList)) {
            return;
        }
        //平台过滤
        ConcurrentHashMap<String, PlateForm> plateFormMap = PlateFormSubscribe.getInstance().getConcurrentHashMap();
        if (!CollectionUtils.isEmpty(plateFormMap)) {
            //过滤
            ConcurrentHashMap.KeySetView<String, PlateForm> keys = plateFormMap.keySet();
            if (keys.contains(ChannelEnum.HW.name())) {
                return;
            }

        }

        //数据过滤--粗粒度过滤
        ConcurrentHashMap<String, UpstreamBaseConfig> baseMap = UpstreamBaseConfigSubscribe.getInstance().getConcurrentHashMap();
        if (!CollectionUtils.isEmpty(baseMap) && baseMap.keySet().contains(address)) {
            //过滤
            return;
        }

//        //数据过滤--细粒度过滤
//        ConcurrentHashMap<String, UpstreamApi> ApiMap = UpstreamApiSubscribe.getInstance().getConcurrentHashMap();
//        if (!CollectionUtils.isEmpty(ApiMap)) {
//            for (String key : ApiMap.keySet()) {
//                if (key.equals(address + "-" + duBody.getIpcType()) && !ApiMap.get(key).getIsUpFlag()) {
//                    return;
//                }
//            }
//        }

        //发送kafka
        sendMessage(duBodyList);
    }

    protected void sendMessage(List<DuBody> duBodys) {
        for (DuBody duBody : duBodys) {
            try {
                String topic = IPCTypeEnum.getTopicByType(duBody.getIpcType());
                String finalTopic = "lx-datahospice.liuchang." + topic;
                KafkaUtils.sendMessage(JSON.toJSONString(duBody), finalTopic);
               log.info("发送数据成功，发送topic为{}，发送数据为{}",finalTopic,JSON.toJSONString(duBody));
            }catch (Exception e){
                log.error("lx-datahospice.liuchang出错数据为{}，集合为{}", JSON.toJSONString(duBody),JSON.toJSONString(duBodys));
            }


        }
    }

    /**
     * @param sourceData
     * @param address
     * @return
     * @Description: 数据进一步组装，模板模式接口，利于后期拓展
     * @Author: liuchang
     * @Date: 2022-10-17 12:26
     * @Param: [sourceData]
     * @Return: java.lang.String
     */
    //数据进一步组装
    protected abstract List<DuBody> handleData(HwOrderMessage sourceData, Integer address);



}
