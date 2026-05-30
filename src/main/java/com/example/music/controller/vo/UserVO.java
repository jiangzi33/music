package com.example.music.controller.vo;

import java.util.Date;

public class UserVO {
    private int id;
    private String name;
    private String password;
    private int age;
    private String interests;
    private String email;
    private String status;
    private Date registerTime;

    public UserVO() {
    }

    public UserVO(int id, String name, String password, int age, String interests, String email, String status, Date registerTime) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.age = age;
        this.interests = interests;
        this.email = email;
        this.status = status;
        this.registerTime = registerTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }
}
