package com.example.music.enums;

public enum UserStatusEnum {
    INIT("INIT"), NORMAL("NORMAL"), ABNORMAL("ABNORMAL");
    private String code;

    UserStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
