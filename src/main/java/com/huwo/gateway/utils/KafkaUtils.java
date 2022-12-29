package com.huwo.gateway.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-14  16:26
 */
@Component
@Slf4j
public class KafkaUtils {

    private static KafkaTemplate kafkaTemplate;

    @Autowired
    public void setService(KafkaTemplate kafkaTemplate) {
        KafkaUtils.kafkaTemplate = kafkaTemplate;
    }

    public static void sendMessage(String message, String topic) {
        kafkaTemplate.send(topic, message);

    }

}
