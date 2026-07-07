package com.example.music.controller.vo;

import java.util.List;

public class MultiNotificationVO {
    private BaseVO baseVO;
    private List<NotificationVO> notificationVOList;

    public BaseVO getBaseVO() {
        return baseVO;
    }

    public void setBaseVO(BaseVO baseVO) {
        this.baseVO = baseVO;
    }

    public List<NotificationVO> getNotificationVOList() {
        return notificationVOList;
    }

    public void setNotificationVOList(List<NotificationVO> notificationVOList) {
        this.notificationVOList = notificationVOList;
    }
}
