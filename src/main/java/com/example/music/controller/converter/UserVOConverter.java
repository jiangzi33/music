package com.example.music.controller.converter;

import com.example.music.controller.vo.UserVO;
import com.example.music.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserVOConverter {
    public static UserVO convert(User user) {
        if (user == null) {
            return null;
        }

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setName(user.getName());
        userVO.setPassword(user.getPassword());
        userVO.setAge(user.getAge());
        userVO.setInterests(user.getInterests());
        userVO.setEmail(user.getEmail());
        userVO.setStatus(user.getStatus());
        userVO.setRegisterTime(user.getRegisterTime());

        return userVO;
    }


    public static List<UserVO> convert(List<User> userList) {
        List<UserVO> userVOList = new ArrayList<>();

        if (userList == null) {
            return userVOList;
        }

        for (User user : userList) {
            userVOList.add(convert(user));
        }

        return userVOList;
    }
}