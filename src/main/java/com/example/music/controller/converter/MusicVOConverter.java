package com.example.music.controller.converter;

import com.example.music.controller.vo.MusicVO;
import com.example.music.entity.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicVOConverter {

    public static MusicVO convert(Music music){

        if(music == null){
            return null;
        }

        MusicVO vo = new MusicVO();

        vo.setId(music.getId());
        vo.setTitle(music.getTitle());
        vo.setContent(music.getContent());
        vo.setAuthor(music.getAuthor());
        vo.setTags(music.getTags());
        vo.setPublishTime(music.getPublishTime());
        vo.setPictureUrl(music.getPictureUrl());
        vo.setAudioUrl(music.getAudioUrl());

        return vo;
    }

    public static List<MusicVO> convertList(List<Music> musicList){

        List<MusicVO> voList = new ArrayList<>();

        if(musicList == null || musicList.isEmpty()){
            return voList;
        }

        for(Music music : musicList){
            voList.add(convert(music));
        }

        return voList;
    }

}
