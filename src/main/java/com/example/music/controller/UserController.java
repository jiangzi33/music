package com.example.music.controller;

import com.example.music.controller.cmd.RegisterCmd;
import com.example.music.controller.cmd.UserCmd;
import com.example.music.controller.converter.UserVOConverter;
import com.example.music.controller.vo.BaseVO;
import com.example.music.controller.vo.MultiUserVO;
import com.example.music.controller.vo.SingleUserVO;
import com.example.music.entity.User;
import com.example.music.exception.*;
import com.example.music.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public BaseVO register(@RequestBody RegisterCmd cmd) {
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            userService.register(cmd);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (UserDuplicatedRegisterException e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
        } catch (UserNotAllowedException e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
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

    @PutMapping("/update")
    public BaseVO updateInterests(int userId, String interests) {
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            userService.updateInterests(userId, interests);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            return baseVO;
        } catch (UserNotExistException e) {
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
            return baseVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "删除用户失败");
            return baseVO;
        }
    }

    @GetMapping("/info")
    public SingleUserVO info(String name) {
        long startTime = System.currentTimeMillis();
        long endTime;
        SingleUserVO singleUserVO = new SingleUserVO();
        try {
            User user = userService.queryByName(name);
            endTime = System.currentTimeMillis();
            if (user == null) {
                singleUserVO.setBaseVO(BaseVO.buildBaseVO(500, false, endTime - startTime, "user not found"));
                return singleUserVO;
            }
            singleUserVO.setUserVO(UserVOConverter.convert(user));
            singleUserVO.setBaseVO(BaseVO.buildBaseVO(200, true, endTime - startTime, null));
            return singleUserVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            singleUserVO.setBaseVO(BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常"));
            return singleUserVO;
        }
    }

    @GetMapping("/id")
    public SingleUserVO infoById(int id) {
        long startTime = System.currentTimeMillis();
        long endTime;
        SingleUserVO singleUserVO = new SingleUserVO();
        try {
            User user = userService.queryById(id);
            endTime = System.currentTimeMillis();
            if (user == null) {
                singleUserVO.setBaseVO(BaseVO.buildBaseVO(500, false, endTime - startTime, "user not found"));
                return singleUserVO;
            }
            singleUserVO.setUserVO(UserVOConverter.convert(user));
            singleUserVO.setBaseVO(BaseVO.buildBaseVO(200, true, endTime - startTime, null));
            return singleUserVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            singleUserVO.setBaseVO(BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常"));
            return singleUserVO;
        }
    }

    @GetMapping("/query-all")
    public MultiUserVO queryAll(int start, int pageSize) {
        long startTime = System.currentTimeMillis();
        long endTime;
        MultiUserVO multiUserVO = new MultiUserVO();
        try {
            List<User> userList = userService.queryAll(start, pageSize);
            endTime = System.currentTimeMillis();
            multiUserVO.setUserVOList(UserVOConverter.convert(userList));
            multiUserVO.setBaseVO(BaseVO.buildBaseVO(200, true, endTime - startTime, null));
            return multiUserVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            multiUserVO.setBaseVO(BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常"));
            return multiUserVO;
        }
    }

    @PostMapping("/add")
    public BaseVO add(@RequestBody UserCmd cmd) {
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            userService.addUser(cmd);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (UserDuplicatedRegisterException e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
        }
    }

    @PutMapping("/modify")
    public BaseVO modify(@RequestBody UserCmd cmd) {
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            userService.modifyUserByAdmin(cmd);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (UserNotExistException e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
        }
    }

    @DeleteMapping("/delete")
    public BaseVO delete(int id) {
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            userService.deleteUser(id);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            return baseVO;
        } catch (UserNotExistException e) {
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
            return baseVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "删除用户失败");
            return baseVO;
        }
    }
}
