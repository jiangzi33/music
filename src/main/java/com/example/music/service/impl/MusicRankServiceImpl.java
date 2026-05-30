package com.example.music.service.impl;

import com.example.music.entity.Music;
import com.example.music.mapper.MusicMapper;
import com.example.music.repository.MusicRankRepository;
import com.example.music.service.MusicRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MusicRankServiceImpl implements MusicRankService {
    @Autowired
    private MusicRankRepository musicRankRepository;
    @Autowired
    private MusicMapper musicMapper;
    @Override
    public List<Music> getTopNFavoriteMusics(int n) {
       /* List<Integer> musicIdList = musicRankRepository.getTopNFavoriteMusics(n);
        List<Music> musicList = new ArrayList<>();
        for (int i = 0; i < musicIdList.size(); i++) {
            int musicId = musicIdList.get(i);
            Music music = musicMapper.queryById(musicId);
            musicList.add(music);
        } */

        List<Integer> musicIdList = musicRankRepository.getTopNFavoriteMusics(n);
        List<Music> musicList = musicMapper.queryByIds(musicIdList);
        return musicList;
    }
}
