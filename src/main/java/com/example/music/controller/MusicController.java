package com.example.music.controller;

import com.example.music.controller.cmd.MusicCmd;
import com.example.music.controller.converter.MusicVOConverter;
import com.example.music.controller.vo.BaseVO;
import com.example.music.controller.vo.MultiMusicVO;
import com.example.music.controller.vo.MusicVO;
import com.example.music.controller.vo.SingleMusicVO;
import com.example.music.controller.vo.UploadVO;
import com.example.music.entity.Music;
import com.example.music.exception.*;
import com.example.music.service.MusicRankService;
import com.example.music.service.MusicService;
import com.example.music.util.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/music")
public class MusicController {
    @Autowired
    private MusicService musicService;
    @Autowired
    private MusicRankService musicRankService;
    @Autowired
    private MinioUtil minioUtil;

    @PostMapping("/upload/audio")
    public UploadVO uploadAudio(@RequestParam("file") MultipartFile file) {
        return uploadToMinio(file, "audio");
    }

    @PostMapping("/upload/image")
    public UploadVO uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadToMinio(file, "image");
    }

    /**
     * 通用文件上传：将文件按分类存入 MinIO 对应文件夹（保留原始文件名），
     * 返回可直接访问的 URL（供音频/图片等场景复用）。
     *
     * @param folder 目标文件夹（如 audio、image）
     */
    private UploadVO uploadToMinio(MultipartFile file, String folder) {
        long startTime = System.currentTimeMillis();
        long endTime;
        UploadVO uploadVO = new UploadVO();
        try {
            if (file == null || file.isEmpty()) {
                endTime = System.currentTimeMillis();
                uploadVO.setBaseVO(BaseVO.buildBaseVO(500, false, endTime - startTime, "上传文件为空"));
                return uploadVO;
            }
            String url = minioUtil.uploadFile(file, folder);

            endTime = System.currentTimeMillis();
            uploadVO.setUrl(url);
            // 兼容旧字段：前端音频上传仍可读取 audioUrl
            uploadVO.setAudioUrl(url);
            uploadVO.setBaseVO(BaseVO.buildBaseVO(200, true, endTime - startTime, null));
            return uploadVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error("文件上传失败", e);
            uploadVO.setBaseVO(BaseVO.buildBaseVO(500, false, endTime - startTime, "文件上传失败"));
            return uploadVO;
        }
    }

    @PostMapping("/add")
    public BaseVO addMusic(@RequestBody MusicCmd cmd){
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            musicService.addMusic(cmd);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
        }
    }

    @PutMapping("/modify")
    public BaseVO modifyMusic(@RequestBody MusicCmd cmd){
        long startTime = System.currentTimeMillis();
        long endTime;
        try{
            musicService.modifyMusic(cmd);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200,true,endTime-startTime,null);
            return baseVO;
        } catch (MusicNotExistException e){
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            BaseVO baseVO = BaseVO.buildBaseVO(500,false,endTime-startTime,e.getMessage());
            return baseVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            BaseVO baseVO = BaseVO.buildBaseVO(500,false,endTime-startTime,"删除用户失败");
            return baseVO;
        }
    }

    @GetMapping("/id")
    public SingleMusicVO queryById(int id){
        long startTime = System.currentTimeMillis();
        long endTime;
        SingleMusicVO singleMusicVO = new SingleMusicVO();
        try{
            Music music = musicService.queryById(id);
            endTime = System.currentTimeMillis();
            MusicVO musicVO = MusicVOConverter.convert(music);
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            singleMusicVO.setMusicVO(musicVO);
            singleMusicVO.setBaseVO(baseVO);
            return singleMusicVO;
        } catch (Exception e){
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
            singleMusicVO.setBaseVO(baseVO);
            return singleMusicVO;
        }
    }

    @GetMapping("/title")
    public SingleMusicVO queryByTitle(String title){
        long startTime = System.currentTimeMillis();
        long endTime;
        SingleMusicVO singleMusicVO = new SingleMusicVO();
        try{
            Music music = musicService.queryByTitle(title);
            endTime = System.currentTimeMillis();
            MusicVO musicVO = MusicVOConverter.convert(music);
            musicVO.setHotScore(musicRankService.queryHotScore(musicVO.getId()));
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            singleMusicVO.setMusicVO(musicVO);
            singleMusicVO.setBaseVO(baseVO);
            return singleMusicVO;
        } catch (Exception e){
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
            singleMusicVO.setBaseVO(baseVO);
            return singleMusicVO;
        }
    }

    @DeleteMapping("/delete")
    public BaseVO delete(int id){
        long startTime = System.currentTimeMillis();
        long endTime;
        try{
            musicService.deleteMusic(id);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200,true,endTime-startTime,null);
            return baseVO;
        } catch (MusicNotExistException e){
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            BaseVO baseVO = BaseVO.buildBaseVO(500,false,endTime-startTime,e.getMessage());
            return baseVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            BaseVO baseVO = BaseVO.buildBaseVO(500,false,endTime-startTime,"删除用户失败");
            return baseVO;
        }
    }

    @GetMapping("/query-all")
    public MultiMusicVO queryAll(int start, int pageSize){
        long startTime = System.currentTimeMillis();
        long endTime;
        MultiMusicVO multiMusicVO = new MultiMusicVO();
        try{
            List<Music> musicList = musicService.queryAll(start,pageSize);
            endTime = System.currentTimeMillis();
            List<MusicVO> musicVOList = MusicVOConverter.convertList(musicList);
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            multiMusicVO.setMusicVOList(musicVOList);
            multiMusicVO.setBaseVO(baseVO);
            return multiMusicVO;
        } catch (Exception e){
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
            multiMusicVO.setBaseVO(baseVO);
            return multiMusicVO;
        }
    }
}
