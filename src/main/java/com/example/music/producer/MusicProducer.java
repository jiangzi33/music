package com.example.music.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MusicProducer {
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void addTags(String topic, int musicId){
      String val = String.valueOf(musicId);
      kafkaTemplate.send(topic,val);
    }

}
