package com.example.music.controller.converter;

import com.example.music.controller.vo.NotificationVO;
import com.example.music.entity.Notification;
import java.util.ArrayList;
import java.util.List;

public class NotificationVOConverter {

    public static NotificationVO convert(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationVO vo = new NotificationVO();
        vo.setId(notification.getId());
        vo.setFrom(notification.getFrom());
        vo.setTo(notification.getTo());
        vo.setTargetType(notification.getTargetType());
        vo.setTargetId(notification.getTargetId());
        vo.setOperation(notification.getOperation());
        vo.setContent(notification.getContent());
        vo.setOperationTime(notification.getOperationTime());

        return vo;
    }

    public static List<NotificationVO> convertList(List<Notification> notificationList) {
        if (notificationList == null) {
            return null;
        }

        List<NotificationVO> notificationVOList = new ArrayList<>();

        for(Notification notification: notificationList){
            notificationVOList.add(convert(notification));
        }

        return notificationVOList;
    }
}