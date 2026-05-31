package com.example.music.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestProducer {
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void sendMessages(String topic, String message){
        kafkaTemplate.send(topic,message);
        System.out.println("投递消息成功: topic =" + topic + " message= " + message );
    }
}
