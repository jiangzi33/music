package com.example.music.controller.converter;

import com.example.music.controller.vo.SystemConfigVO;
import com.example.music.entity.SystemConfig;

public class SystemConfigVOConverter {
    public static SystemConfigVO convertToVO(SystemConfig systemConfig){
        SystemConfigVO systemConfigVO = new SystemConfigVO();
        systemConfigVO.setId(systemConfig.getId());
        systemConfigVO.setCode(systemConfig.getCode());
        systemConfigVO.setValue(systemConfig.getValue());
        return systemConfigVO;
    }
}
