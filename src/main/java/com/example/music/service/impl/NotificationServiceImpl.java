package com.example.music.service.impl;

import com.example.music.entity.Notification;
import com.example.music.mapper.NotificationMapper;
import com.example.music.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Override
    public void addNotification(Notification notification) {
        notificationMapper.addNotification(notification);
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
