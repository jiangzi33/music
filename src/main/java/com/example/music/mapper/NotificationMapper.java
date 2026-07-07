package com.example.music.mapper;

import com.example.music.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {
    void addNotification(Notification notification);
    List<Notification> queryByTo(int to, int start, int size);
    void deleteNotification(int id);
}
