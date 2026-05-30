package com.example.music.controller;

import com.example.music.controller.cmd.RegisterCmd;
import com.example.music.controller.vo.BaseVO;
import com.example.music.exception.*;
import com.example.music.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public BaseVO register(@RequestBody RegisterCmd cmd){
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            userService.register(cmd);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (UserDuplicatedRegisterException e) {
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
        } catch (UserNotAllowedException e) {
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
        }
    }

    @PutMapping("/activate")
    public BaseVO activate(String name, String code) {
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            userService.activate(name, code);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (UserNotExistException e) {
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
        } catch (UserFailActivatedException e) {
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
        }
    }

    @GetMapping("/login")
    public BaseVO login(String name, String password) {
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            userService.login(name, password);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (UserNotExistException e) {
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
        } catch (UserNotAllowedException e) {
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
        } catch (UserPasswordErrorException e) {
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
        }
    }

    @DeleteMapping("/delete")
    public BaseVO delete(int id){
        long startTime = System.currentTimeMillis();
        long endTime;
        try{
            userService.deleteUser(id);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200,true,endTime-startTime,null);
            return baseVO;
        } catch (UserNotExistException e){
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500,false,endTime-startTime,e.getMessage());
            return baseVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500,false,endTime-startTime,"删除用户失败");
            return baseVO;
        }
    }
}
