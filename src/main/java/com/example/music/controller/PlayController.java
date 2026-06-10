package com.example.music.controller;

import com.example.music.controller.vo.BaseVO;
import com.example.music.intergration.SyncIntegration;
import com.example.music.intergration.cmd.PlayRecordCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/play")
public class PlayController {
    @Autowired
    private SyncIntegration syncIntegration;

    @PostMapping("/play")
    public BaseVO play(@RequestBody PlayRecordCmd cmd){
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            syncIntegration.syncPlayRecord(cmd);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
        }
    }
}
