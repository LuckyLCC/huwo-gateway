package com.huwo.gateway.strategy.position;

import com.alibaba.fastjson.JSON;
import com.huwo.data.upstream.api.common.IPCTypeEnum;
import com.huwo.gateway.common.DuBody;
import com.huwo.gateway.common.HwPositionMessage;
import com.huwo.gateway.domain.PlateForm;
import com.huwo.gateway.domain.UpstreamBaseConfig;
import com.huwo.gateway.listener.PlateFormSubscribe;
import com.huwo.gateway.listener.UpstreamBaseConfigSubscribe;
import com.huwo.gateway.utils.KafkaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-20  11:24
 */
@Component
@Slf4j
public abstract class IPositionStrategy implements InitializingBean {


    @Value("${plateFormName}")
    private String plateFormName;


    //数据上报
    public void handleAndSendData(HwPositionMessage positionMessage) {

        //处理数据
        List<DuBody> duBodyList = handleData(positionMessage);
        if (CollectionUtils.isEmpty(duBodyList)) {
            return;
        }

        //筛选数据
        //平台过滤
        ConcurrentHashMap<String, PlateForm> plateFormMap = PlateFormSubscribe.getInstance().getConcurrentHashMap();
        if (!CollectionUtils.isEmpty(plateFormMap)) {
            //过滤
            ConcurrentHashMap.KeySetView<String, PlateForm> keys = plateFormMap.keySet();
            if (keys.contains(plateFormName)) {
                return;
            }
        }

        //城市过滤
        ConcurrentHashMap<String, UpstreamBaseConfig> baseMap = UpstreamBaseConfigSubscribe.getInstance().getConcurrentHashMap();
        if (!CollectionUtils.isEmpty(baseMap)) {
            //过滤
            ConcurrentHashMap.KeySetView<String, UpstreamBaseConfig> keySet = baseMap.keySet();
            duBodyList = duBodyList.stream().filter(duBody -> !keySet.contains(String.valueOf(duBody.getAddress()))).collect(Collectors.toList());

        }
        //发送数据
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
     * @Description:
     * @Author: liuchang
     * @Date: 2022-10-20 11:36
     * @Param: [vehicle, driver, car, orderNo]
     * @Return: java.util.List<com.huwo.gateway.common.DuBody>
     **/
    //数据进一步组装
    protected abstract List<DuBody> handleData(HwPositionMessage vehicle);


}
