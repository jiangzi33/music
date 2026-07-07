package com.example.music.service;

import com.example.music.entity.Notification;

import java.util.List;

public interface NotificationService {
    void addNotification(Notification notification);
    List<Notification> queryByTo(int to, int start, int size);
    void deleteNotification(int id);
}
