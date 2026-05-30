package com.example.music.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class UserActivationRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public void setActivationCode(String name, String code){
        redisTemplate.opsForValue().set(buildKey(name),code,5, TimeUnit.MINUTES);
    }

    public String getActivationCode(String name){
       return (String) redisTemplate.opsForValue().get(buildKey(name));
    }
    private String buildKey(String name){
        return "user_activate:" + name;
    }
}
