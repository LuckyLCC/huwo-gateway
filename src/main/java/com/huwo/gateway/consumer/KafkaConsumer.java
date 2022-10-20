package com.huwo.gateway.consumer;

import com.huwo.gateway.handler.HwOrderEventHandler;
import com.huwo.gateway.handler.HwPositionEventHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-17  15:15
 */
@Component
public class KafkaConsumer {

    private ScheduledExecutorService service = Executors.newScheduledThreadPool(10);


    @KafkaListener(topics = {"orders-flume-new"}, containerFactory = "kafkaListenerContainerFactory")
    public void onMessage2(List<ConsumerRecord<String, byte[]>> recordList) {
        System.out.println("orders-flume-new"+"======="+Thread.currentThread().getName());
        recordList.forEach((record) -> {
            service.execute(new HwOrderEventHandler(record.topic(), new String(record.value())));
        });
    }

    //不指定group，默认取yml里配置的
    @KafkaListener(topics = {"callmeappgps"}, containerFactory = "kafkaListenerContainerFactory")
    public void onMessage1Batch(List<ConsumerRecord<String, byte[]>> recordList) {
        System.out.println("callmeappgps"+"======="+Thread.currentThread().getName());
        recordList.forEach((record) -> {
            service.execute(new HwPositionEventHandler(record.topic(), new String(record.value())));
        });

    }

}
