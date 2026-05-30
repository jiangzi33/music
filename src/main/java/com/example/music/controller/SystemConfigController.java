package com.example.music.controller;

import com.example.music.controller.cmd.SystemConfigCmd;
import com.example.music.controller.converter.SystemConfigVOConverter;
import com.example.music.controller.vo.BaseVO;
import com.example.music.controller.vo.SingleSystemConfigVO;
import com.example.music.controller.vo.SystemConfigVO;
import com.example.music.entity.SystemConfig;
import com.example.music.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/systemConfig")
public class SystemConfigController {
    @Autowired
    private SystemConfigService systemConfigService;

    @PostMapping("/add")
    public BaseVO addSystemConfig(@RequestBody SystemConfigCmd cmd) {
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            systemConfigService.addSystemConfig(cmd);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            return baseVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
            return baseVO;
        }
    }

    @GetMapping("/modify")
    public BaseVO modifySystemConfig(String code, String value) {
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            systemConfigService.modifySystemConfig(code, value);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            return baseVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
            return baseVO;
        }
    }

    @GetMapping("/query")
    public SingleSystemConfigVO queryConfigByCode(String code) {
        long startTime = System.currentTimeMillis();
        long endTime;
        SingleSystemConfigVO singleSystemConfigVO = new SingleSystemConfigVO();
        try {
            SystemConfig systemConfig = systemConfigService.queryConfigByCode(code);
            SystemConfigVO systemConfigVO = SystemConfigVOConverter.convertToVO(systemConfig);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            singleSystemConfigVO.setSystemConfigVO(systemConfigVO);
            singleSystemConfigVO.setBaseVO(baseVO);
            return singleSystemConfigVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
            singleSystemConfigVO.setBaseVO(baseVO);
            return singleSystemConfigVO;
        }
    }

    @DeleteMapping("/delete")
    public BaseVO deleteSystemConfig(String code) {
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            systemConfigService.deleteSystemConfig(code);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            return baseVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
            return baseVO;
        }
    }
}
