package com.example.music.mapper;

import com.example.music.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    void addUser(User user);
    User queryByName(String name);
    User queryById(int id);
    void modifyUser(User user);
    void deleteUser(int id);
    List<User> queryByIds(List<Integer> ids);
}
