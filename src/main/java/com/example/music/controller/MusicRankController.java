package com.example.music.controller;

import com.example.music.controller.converter.MusicVOConverter;
import com.example.music.controller.vo.BaseVO;
import com.example.music.controller.vo.MultiMusicVO;
import com.example.music.controller.vo.MusicVO;
import com.example.music.controller.vo.SingleMusicVO;
import com.example.music.entity.Music;
import com.example.music.service.MusicRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/music-rank")
public class MusicRankController {
    @Autowired
    private MusicRankService musicRankService;

    @GetMapping("/topN")
    public MultiMusicVO getTopNFavoriteMusics(int n){
        long startTime = System.currentTimeMillis();
        long endTime;
        MultiMusicVO multiMusicVO = new MultiMusicVO();
        try{
            List<Music> musicList = musicRankService.getTopNFavoriteMusics(n);
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
