package com.example.music.controller;

import com.example.music.controller.converter.NotificationVOConverter;
import com.example.music.controller.vo.BaseVO;
import com.example.music.controller.vo.MultiNotificationVO;
import com.example.music.controller.vo.NotificationVO;
import com.example.music.entity.Notification;
import com.example.music.service.NotificationEmitterRegistry;
import com.example.music.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

@RestController
@RequestMapping("/notification")
@Slf4j
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationEmitterRegistry emitterRegistry;

    /** 订阅实时通知流,前端用 EventSource 连接后即可无刷新收到新通知。 */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(int to) {
        return emitterRegistry.subscribe(to);
    }

    @GetMapping("/query")
    public MultiNotificationVO queryByTO(int to, int start, int size){
        long startTime = System.currentTimeMillis();
        long endTime;
        MultiNotificationVO multiNotificationVO = new MultiNotificationVO();
        try {
            List<Notification> notificationList = notificationService.queryByTo(to,start,size);
            List<NotificationVO> notificationVOList = NotificationVOConverter.convertList(notificationList);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            multiNotificationVO.setBaseVO(baseVO);
            multiNotificationVO.setNotificationVOList(notificationVOList);
            return multiNotificationVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
            multiNotificationVO.setBaseVO(baseVO);
            return multiNotificationVO;
        }
    }


    @DeleteMapping("/delete")
    public BaseVO deleteComment(int id){
        long startTime = System.currentTimeMillis();
        long endTime;
        try{
            notificationService.deleteNotification(id);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, "删除评论失败");
        }
    }
}
