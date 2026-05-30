package com.example.music.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MusicLikeRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Transactional
    public void addLike(int musicId, int userId){
        redisTemplate.opsForList().rightPush(buildMusicLikeKey(musicId),userId);
        redisTemplate.opsForList().rightPush(buildUserLikeKey(userId),musicId);
    }

    public void deleteLike(int musicId, int userId){
        redisTemplate.opsForList().remove(buildMusicLikeKey(musicId),1,userId);
        redisTemplate.opsForList().remove(buildUserLikeKey(userId),1,musicId);
    }

    public List<Integer> getMusicLike(int musicId){
        List<Object> userList = redisTemplate.opsForList().range(buildMusicLikeKey(musicId),0,-1);
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            res.add((Integer) userList.get(i));
        }
        return res;
    }

    public List<Integer> getUserLike(int userId){
        List<Object> musicList = redisTemplate.opsForList().range(buildUserLikeKey(userId),0,-1);
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < musicList.size(); i++) {
            res.add((Integer) musicList.get(i));
        }
        return res;
    }

    private String buildMusicLikeKey(int musicId){
        return "music_like:" + musicId;
    }

    private String buildUserLikeKey(int userId){
        return "user_like:" + userId;
    }
}
