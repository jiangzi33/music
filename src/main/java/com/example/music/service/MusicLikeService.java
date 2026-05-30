package com.example.music.service;

import com.example.music.entity.Music;
import com.example.music.entity.User;

import java.util.List;

public interface MusicLikeService {
    void addLike(int musicId, int userId);
    void deleteLike(int musicId, int userId);
    List<User> getMusicLike(int musicId);
    List<Music> getUserLike(int userId);
}
