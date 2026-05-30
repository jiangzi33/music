package com.example.music.service.impl;

import com.example.music.controller.cmd.SystemConfigCmd;
import com.example.music.entity.SystemConfig;
import com.example.music.mapper.SystemConfigMapper;
import com.example.music.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    @Override
    public void addSystemConfig(SystemConfigCmd cmd) {
        SystemConfig systemConfig = buildSystemConfig(cmd);
        systemConfigMapper.addSystemConfig(systemConfig);
    }

    @Override
    public void modifySystemConfig(String code, String value) {
        systemConfigMapper.modifySystemConfig(code,value);
    }

    @Override
    public SystemConfig queryConfigByCode(String code) {
        SystemConfig systemConfig = systemConfigMapper.queryConfigByCode(code);
        return systemConfig;
    }

    @Override
    public void deleteSystemConfig(String code) {
        systemConfigMapper.deleteSystemConfig(code);
    }

    private SystemConfig buildSystemConfig(SystemConfigCmd cmd){
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setCode(cmd.getCode());
        systemConfig.setValue(cmd.getValue());
        return systemConfig;
    }
}
