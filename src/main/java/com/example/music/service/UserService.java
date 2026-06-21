package com.example.music.service;

import com.example.music.controller.cmd.RegisterCmd;
import com.example.music.controller.cmd.UserCmd;
import com.example.music.entity.User;

import java.util.List;

public interface UserService {
    void register(RegisterCmd cmd);
    void activate(String name, String code);
    void login(String name, String password);
    User queryByName(String name);
    User queryById(int id);
    void modifyUser(UserCmd cmd);
    void modifyUserByAdmin(UserCmd cmd);
    void addUser(UserCmd cmd);
    void deleteUser(int id);
    void updateInterests(int id, String interests);
    List<User> queryAll(int start, int pageSize);
}
