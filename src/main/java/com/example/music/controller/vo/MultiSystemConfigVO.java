package com.example.music.controller.vo;

import java.util.List;

public class MultiSystemConfigVO {
    private BaseVO baseVO;
    private List<SystemConfigVO> systemConfigVOList;

    public BaseVO getBaseVO() {
        return baseVO;
    }

    public void setBaseVO(BaseVO baseVO) {
        this.baseVO = baseVO;
    }

    public List<SystemConfigVO> getSystemConfigVOList() {
        return systemConfigVOList;
    }

    public void setSystemConfigVOList(List<SystemConfigVO> systemConfigVOList) {
        this.systemConfigVOList = systemConfigVOList;
    }
}