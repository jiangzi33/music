package com.example.music.service.impl;

import com.example.music.controller.cmd.MusicCmd;
import com.example.music.entity.Music;
import com.example.music.exception.MusicNotExistException;
import com.example.music.mapper.MusicMapper;
import com.example.music.producer.MusicProducer;
import com.example.music.repository.MusicRankRepository;
import com.example.music.repository.MusicRepository;
import com.example.music.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MusicServiceImpl implements MusicService {
    @Autowired
    private MusicMapper musicMapper;
    @Autowired
    private MusicRepository musicRepository;
    @Autowired
    private MusicRankRepository musicRankRepository;
    @Autowired
    private MusicProducer musicProducer;
    @Override
    public void addMusic(MusicCmd cmd) {
        Music music = buildMusic(cmd);
        musicMapper.addMusic(music);
        Music musicInDB = musicMapper.queryByTitle(cmd.getTitle());
        musicRankRepository.addItem(musicInDB.getId());
        musicProducer.send("add-music",musicInDB.getId());
    }

    @Override
    public Music queryByTitle(String title) {
        Music music = musicRepository.get(title);
        if(music==null){
           music = musicMapper.queryByTitle(title);
           musicRepository.add(title, music);
        }
        return music;
    }

    @Override
    public Music queryById(int id) {
        Music music = musicRepository.get(id);
        if(music==null){
            music = musicMapper.queryById(id);
            musicRepository.add(id,music);
        }
        return music;
    }

    @Transactional
    @Override
    public void modifyMusic(MusicCmd cmd) {
        Music music = musicMapper.queryByTitle(cmd.getTitle());
        if(music==null){
            throw new MusicNotExistException("music is not existed");
        }
        music.setAuthor(cmd.getAuthor());
        music.setContent(cmd.getContent());
        music.setTitle(cmd.getTitle());
        music.setTags(cmd.getTags());
        music.setPictureUrl(cmd.getPictureUrl());
        musicRepository.delete(music);
        musicMapper.modifyMusic(music);
    }

    @Transactional
    @Override
    public void deleteMusic(int id) {
        Music music = musicMapper.queryById(id);
        if(music==null){
            throw new MusicNotExistException("music is not existed");
        }
        musicMapper.deleteMusic(id);
        musicRepository.delete(music);
        musicRankRepository.removeItem(id);
    }

    @Override
    public List<Music> queryAll(int start, int pageSize) {
        return musicMapper.queryAll(start,pageSize);
    }

    private Music buildMusic(MusicCmd cmd){
        Music music = new Music();
        music.setTitle(cmd.getTitle());
        music.setContent(cmd.getContent());
        music.setAuthor(cmd.getAuthor());
        music.setTags(cmd.getTags());
        music.setPictureUrl(cmd.getPictureUrl());
        return music;
    }
}
