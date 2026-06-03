package com.example.music.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class MusicInterestsRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    public void addItem(String tag, int userId){
        redisTemplate.opsForSet().add(buildKey(tag),userId);
    }

    public void deleteItem(String tag, int userId){
        redisTemplate.opsForSet().remove(buildKey(tag),userId);
    }

    public Set<Integer> getItems(String tag){
       Set<Object> objectSet = redisTemplate.opsForSet().members(buildKey(tag));
       Set<Integer> res = new HashSet<>();
       for (Object o : objectSet){
           res.add((Integer) o);
       }
       return res;
    }

    private String buildKey(String tag){
        return "recommend_by_music_tag:" + tag;
    }
}
