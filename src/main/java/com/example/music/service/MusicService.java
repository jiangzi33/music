package com.example.music.service;

import com.example.music.controller.cmd.MusicCmd;
import com.example.music.entity.Music;

import java.util.List;

public interface MusicService {
    void addMusic(MusicCmd cmd);
    Music queryByTitle(String title);
    Music queryById(int id);
    void modifyMusic(MusicCmd cmd);
    void deleteMusic(int id);
    List<Music> queryAll(int start, int pageSize);
}
