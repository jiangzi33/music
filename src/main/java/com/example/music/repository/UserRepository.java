package com.example.music.repository;

import com.example.music.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    public void add(String name, User user){
        redisTemplate.opsForValue().set(buildKey(name),user);
    }

    public User get(String name){
      return (User)redisTemplate.opsForValue().get(buildKey(name));
    }

    public void add(int userId, User user){
        redisTemplate.opsForValue().set(buildKey(userId),user);
    }

    public User get(int userId){
        return (User)redisTemplate.opsForValue().get(buildKey(userId));
    }

    public void delete(User user){
        redisTemplate.delete(buildKey(user.getId()));
        redisTemplate.delete(buildKey(user.getName()));
    }

    private String buildKey(String name){
        return "user_name:" + name;
    }

    private String buildKey(int id){
        return "user_id:" + id;
    }
}
