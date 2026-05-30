package com.example.music.controller.vo;

import java.util.List;

public class MultiMusicVO {
    private BaseVO baseVO;
    private List<MusicVO> musicVOList;

    public BaseVO getBaseVO() {
        return baseVO;
    }

    public void setBaseVO(BaseVO baseVO) {
        this.baseVO = baseVO;
    }

    public List<MusicVO> getMusicVOList() {
        return musicVOList;
    }

    public void setMusicVOList(List<MusicVO> musicVOList) {
        this.musicVOList = musicVOList;
    }
}
