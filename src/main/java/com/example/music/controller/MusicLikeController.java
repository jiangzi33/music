package com.example.music.controller;


import com.example.music.controller.converter.MusicVOConverter;
import com.example.music.controller.converter.UserVOConverter;
import com.example.music.controller.vo.*;
import com.example.music.entity.Music;
import com.example.music.entity.User;
import com.example.music.service.MusicLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/music-like")
public class MusicLikeController {
    @Autowired
    private MusicLikeService musicLikeService;

    @PostMapping("/add")
    public BaseVO addLike(int musicId, int userId){
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            musicLikeService.addLike(musicId,userId);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
        }
    }

    @PutMapping("/cancel")
    public BaseVO deleteLike(int musicId, int userId){
        long startTime = System.currentTimeMillis();
        long endTime;
        try{
            musicLikeService.deleteLike(musicId,userId);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200,true,endTime-startTime,null);
            return baseVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500,false,endTime-startTime,"删除用户失败");
            return baseVO;
        }
    }

    @GetMapping("/get-music-like")
    public MultiUserVO getMusicLike(int musicId){
        long startTime = System.currentTimeMillis();
        long endTime;
        MultiUserVO multiUserVO = new MultiUserVO();
        try{
            List<User> userList = musicLikeService.getMusicLike(musicId);
            List<UserVO> userVOList = UserVOConverter.convert(userList);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            multiUserVO.setBaseVO(baseVO);
            multiUserVO.setUserVOList(userVOList);
            return multiUserVO;
        } catch (Exception e){
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
            multiUserVO.setBaseVO(baseVO);
            return multiUserVO;
        }
    }

    @GetMapping("/get-user-like")
    public MultiMusicVO getUserLike(int userId){
        long startTime = System.currentTimeMillis();
        long endTime;
        MultiMusicVO multiMusicVO = new MultiMusicVO();
        try{
            List<Music> musicList = musicLikeService.getUserLike(userId);
            List<MusicVO> musicVOList = MusicVOConverter.convertList(musicList);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            multiMusicVO.setMusicVOList(musicVOList);
            multiMusicVO.setBaseVO(baseVO);
            return multiMusicVO;
        } catch (Exception e){
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
            multiMusicVO.setBaseVO(baseVO);
            return multiMusicVO;
        }
    }
}
