package com.example.music.controller.vo;

public class BaseVO {
    private int code;
    private boolean success;
    private long duration;
    private String errorCode;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public static BaseVO buildBaseVO(int code, boolean success, long duration,String errorCode){
        BaseVO baseVO = new BaseVO();
        baseVO.setCode(code);
        baseVO.setSuccess(success);
        baseVO.setDuration(duration);
        baseVO.setErrorCode(errorCode);
        return baseVO;
    }
}
