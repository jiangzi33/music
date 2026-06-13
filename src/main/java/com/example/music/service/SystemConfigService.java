package com.example.music.service;

import com.example.music.controller.cmd.SystemConfigCmd;
import com.example.music.entity.SystemConfig;

import java.util.List;

public interface SystemConfigService {
    void addSystemConfig(SystemConfigCmd cmd);
    void modifySystemConfig(String code, String value);
    SystemConfig queryConfigByCode(String code);
    void deleteSystemConfig(String code);
    List<SystemConfig> queryAllConfig();
}
