package com.example.music.controller;

import com.example.music.controller.cmd.MusicCmd;
import com.example.music.controller.converter.MusicVOConverter;
import com.example.music.controller.vo.BaseVO;
import com.example.music.controller.vo.MusicVO;
import com.example.music.controller.vo.SingleMusicVO;
import com.example.music.entity.Music;
import com.example.music.exception.*;
import com.example.music.service.MusicRankService;
import com.example.music.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/music")
public class MusicController {
    @Autowired
    private MusicService musicService;
    @Autowired
    private MusicRankService musicRankService;

    @PostMapping("/add")
    public BaseVO addMusic(@RequestBody MusicCmd cmd){
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            musicService.addMusic(cmd);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
        }
    }

    @PutMapping("/modify")
    public BaseVO modifyMusic(@RequestBody MusicCmd cmd){
        long startTime = System.currentTimeMillis();
        long endTime;
        try{
            musicService.modifyMusic(cmd);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200,true,endTime-startTime,null);
            return baseVO;
        } catch (MusicNotExistException e){
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500,false,endTime-startTime,e.getMessage());
            return baseVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500,false,endTime-startTime,"删除用户失败");
            return baseVO;
        }
    }

    @GetMapping("/id")
    public SingleMusicVO queryById(int id){
        long startTime = System.currentTimeMillis();
        long endTime;
        SingleMusicVO singleMusicVO = new SingleMusicVO();
        try{
            Music music = musicService.queryById(id);
            endTime = System.currentTimeMillis();
            MusicVO musicVO = MusicVOConverter.convert(music);
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            singleMusicVO.setMusicVO(musicVO);
            singleMusicVO.setBaseVO(baseVO);
            return singleMusicVO;
        } catch (Exception e){
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
            singleMusicVO.setBaseVO(baseVO);
            return singleMusicVO;
        }
    }

    @GetMapping("/title")
    public SingleMusicVO queryByTitle(String title){
        long startTime = System.currentTimeMillis();
        long endTime;
        SingleMusicVO singleMusicVO = new SingleMusicVO();
        try{
            Music music = musicService.queryByTitle(title);
            endTime = System.currentTimeMillis();
            MusicVO musicVO = MusicVOConverter.convert(music);
            musicVO.setHotScore(musicRankService.queryHotScore(musicVO.getId()));
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            singleMusicVO.setMusicVO(musicVO);
            singleMusicVO.setBaseVO(baseVO);
            return singleMusicVO;
        } catch (Exception e){
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
            singleMusicVO.setBaseVO(baseVO);
            return singleMusicVO;
        }
    }

    @DeleteMapping("/delete")
    public BaseVO delete(int id){
        long startTime = System.currentTimeMillis();
        long endTime;
        try{
            musicService.deleteMusic(id);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200,true,endTime-startTime,null);
            return baseVO;
        } catch (MusicNotExistException e){
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
