package com.example.music.controller;

import com.example.music.controller.vo.BaseVO;
import com.example.music.producer.TestProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-message")
public class MessageTestController {
    @Autowired
    private TestProducer testProducer;

    @PostMapping("/sendMessage")
    public BaseVO send(String topic, String message){
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            testProducer.sendMessages(topic,message);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200,true,endTime-startTime,null);
            return baseVO;
        } catch (Exception e){
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(500,false,endTime-startTime,"其他未知异常");
        }
    }
}
