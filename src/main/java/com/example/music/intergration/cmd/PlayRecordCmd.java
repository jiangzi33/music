package com.example.music.intergration.cmd;

public class PlayRecordCmd {
    private int userId;
    private int soundId;
    private int duration;
    private String bizScene;
    private String prizeCode;

    public String getBizScene() {
        return bizScene;
    }

    public void setBizScene(String bizScene) {
        this.bizScene = bizScene;
    }

    public String getPrizeCode() {
        return prizeCode;
    }

    public void setPrizeCode(String prizeCode) {
        this.prizeCode = prizeCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSoundId() {
        return soundId;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }
}
