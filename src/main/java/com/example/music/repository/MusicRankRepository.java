package com.example.music.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class MusicRankRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public void addItem(int musicId){
        redisTemplate.opsForZSet().add(buildKey(),musicId,0);
    }

    public void updateScore(int musicId, double delta){
        redisTemplate.opsForZSet().incrementScore(buildKey(),musicId,delta);
    }

    public void removeItem(int musicId){
        redisTemplate.opsForZSet().remove(buildKey(),musicId);
    }

    public List<Integer> getTopNFavoriteMusics(int n){
        List<Integer> integerList = new ArrayList<>();
        Set<Object> objectSet = redisTemplate.opsForZSet().reverseRange(buildKey(), 0, n - 1);
        for (Object o : objectSet){
            integerList.add((Integer) o);
        }
        return integerList;
    }

    public double getScore(int musicId){
       double res = redisTemplate.opsForZSet().score(buildKey(),musicId);
       return res;
    }

    private String buildKey(){
        return"music_rank";
    }
}
