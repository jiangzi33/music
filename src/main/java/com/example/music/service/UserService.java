package com.example.music.service;

import com.example.music.controller.cmd.RegisterCmd;
import com.example.music.controller.cmd.UserCmd;
import com.example.music.entity.User;

public interface UserService {
    void register(RegisterCmd cmd);
    void activate(String name, String code);
    void login(String name, String password);
    User queryByName(String name);
    User queryById(int id);
    void modifyUser(UserCmd cmd);
    void deleteUser(int id);
}
