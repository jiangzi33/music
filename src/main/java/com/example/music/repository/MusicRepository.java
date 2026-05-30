package com.example.music.repository;

import com.example.music.entity.Music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MusicRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public void add(String name, Music music){
        redisTemplate.opsForValue().set(buildKey(name),music);
    }

    public Music get(String title){
        return (Music) redisTemplate.opsForValue().get(buildKey(title));
    }

    public void add(int musicId, Music music){
        redisTemplate.opsForValue().set(buildKey(musicId),music);
    }

    public Music get(int musicId){
        return (Music) redisTemplate.opsForValue().get(buildKey(musicId));
    }

    public void delete(Music music){
        redisTemplate.delete(buildKey(music.getId()));
        redisTemplate.delete(buildKey(music.getTitle()));
    }

    private String buildKey(String title){
        return "music_title:" + title;
    }

    private String buildKey(int id){
        return "music_id:" + id;
    }
}
