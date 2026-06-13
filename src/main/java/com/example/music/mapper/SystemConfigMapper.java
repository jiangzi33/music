package com.example.music.mapper;

import com.example.music.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SystemConfigMapper {
    void addSystemConfig(SystemConfig systemConfig);
    void modifySystemConfig(String code, String value);
    SystemConfig queryConfigByCode(String code);
    void deleteSystemConfig(String code);
    List<SystemConfig> queryAllConfig();
}
