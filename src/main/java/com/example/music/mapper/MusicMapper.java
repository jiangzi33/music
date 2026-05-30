package com.example.music.mapper;

import com.example.music.entity.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MusicMapper{
    void addMusic(Music music);
    Music queryByTitle(String title);
    Music queryById(int id);
    List<Music> queryByIds(List<Integer> ids);
    void modifyMusic(Music music);
    void deleteMusic(int id);
}
