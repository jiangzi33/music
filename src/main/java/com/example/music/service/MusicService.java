package com.example.music.service;

import com.example.music.controller.cmd.MusicCmd;
import com.example.music.entity.Music;

public interface MusicService {
    void addMusic(MusicCmd cmd);
    Music queryByTitle(String title);
    Music queryById(int id);
    void modifyMusic(MusicCmd cmd);
    void deleteMusic(int id);
}
