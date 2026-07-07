package com.example.music.service.impl;

import com.example.music.controller.converter.NotificationVOConverter;
import com.example.music.entity.Notification;
import com.example.music.mapper.NotificationMapper;
import com.example.music.service.NotificationEmitterRegistry;
import com.example.music.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private NotificationEmitterRegistry emitterRegistry;

    @Override
    public void addNotification(Notification notification) {
        notificationMapper.addNotification(notification);
        // 写库时数据库用 now() 生成时间,这里补上一个近似值供实时推送的载荷使用
        if (notification.getOperationTime() == null) {
            notification.setOperationTime(new Date());
        }
        // 实时推送给接收者,前端通过 SSE 无需刷新即可收到
        emitterRegistry.send(notification.getTo(), NotificationVOConverter.convert(notification));
    }

    @Override
    public List<Notification> queryByTo(int to, int start, int size) {
        return notificationMapper.queryByTo(to,start,size);
    }

    @Override
    public void deleteNotification(int id) {
        notificationMapper.deleteNotification(id);
    }
}
