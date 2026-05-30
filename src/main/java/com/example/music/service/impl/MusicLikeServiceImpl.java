package com.example.music.service.impl;

import com.example.music.entity.Music;
import com.example.music.entity.User;
import com.example.music.mapper.MusicMapper;
import com.example.music.mapper.UserMapper;
import com.example.music.repository.MusicLikeRepository;
import com.example.music.repository.MusicRankRepository;
import com.example.music.service.MusicLikeService;
import constant.MusicConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MusicLikeServiceImpl implements MusicLikeService {
    @Autowired
    private MusicLikeRepository musicLikeRepository;
    @Autowired
    private MusicRankRepository musicRankRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MusicMapper musicMapper;
    @Transactional
    @Override
    public void addLike(int musicId, int userId) {
        musicLikeRepository.addLike(musicId,userId);
        musicRankRepository.updateScore(musicId, MusicConstant.LIKE_SCORE);
    }

    @Transactional
    @Override
    public void deleteLike(int musicId, int userId) {
        musicLikeRepository.deleteLike(musicId,userId);
        musicRankRepository.updateScore(musicId,-MusicConstant.LIKE_SCORE);
    }

    @Override
    public List<User> getMusicLike(int musicId) {
        List<Integer> userIdList = musicLikeRepository.getMusicLike(musicId);
        List<User> userList = userMapper.queryByIds(userIdList);
        return userList;
    }

    @Override
    public List<Music> getUserLike(int userId) {
       List<Integer> musicIdList = musicLikeRepository.getUserLike(userId);
       List<Music> musicList = musicMapper.queryByIds(musicIdList);
       return musicList;
    }
}
