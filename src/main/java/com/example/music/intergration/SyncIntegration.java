package com.example.music.intergration;

import com.example.music.controller.vo.BaseVO;
import com.example.music.exception.AcrossSysException;
import com.example.music.intergration.cmd.PlayRecordCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

@Component
public class SyncIntegration {
    @Autowired
    private RestTemplate restTemplate;
    public void syncPlayRecord(PlayRecordCmd cmd){
        String url = "http://127.0.0.1:8083/record/sync";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PlayRecordCmd> playRecordCmdHttpEntity = new HttpEntity<>(cmd,httpHeaders);
        ResponseEntity<BaseVO> baseVOResponseEntity = restTemplate.postForEntity(url, playRecordCmdHttpEntity, BaseVO.class);
        if(!baseVOResponseEntity.getBody().isSuccess() || baseVOResponseEntity.getBody().getCode()!=200){
            throw new AcrossSysException("acrossing system is fail");
        }
    }
}
