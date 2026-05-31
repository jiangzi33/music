package com.example.music.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestConsumer {
   @KafkaListener(topics = {"test-topic"},groupId = "test-group")
    public void consumeMessage(String message){
       System.out.println("收到的消息： " + message);
   }
}
