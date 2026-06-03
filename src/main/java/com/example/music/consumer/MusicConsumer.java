package com.example.music.consumer;

import com.example.music.entity.Music;
import com.example.music.entity.User;
import com.example.music.intergration.EmailUtil;
import com.example.music.mapper.UserMapper;
import com.example.music.repository.MusicInterestsRepository;
import com.example.music.repository.MusicRankRepository;
import com.example.music.service.MusicService;
import constant.MusicConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class MusicConsumer {
    @Autowired
    private MusicRankRepository musicRankRepository;
    @Autowired
    private MusicService musicService;
    @Autowired
    private MusicInterestsRepository musicInterestsRepository;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private UserMapper userMapper;
    @KafkaListener(topics = "add-music", groupId = MusicConstant.GROUP_ID)
    public void consumeMusic(String message) {
        int musicId = Integer.valueOf(message);
        musicRankRepository.addItem(musicId);
        Music music = musicService.queryById(musicId);
        String tags = music.getTags();
        String[] tagArray = tags.split(",");
        Set<Integer> recommendUserIds = new HashSet<>();
        for (int i = 0; i < tagArray.length; i++) {
            Set<Integer> items = musicInterestsRepository.getItems(tagArray[i]);
            recommendUserIds.addAll(items);
        }
        List<Integer> userIds = new ArrayList<>(recommendUserIds);
        List<User> userList = userMapper.queryByIds(userIds);
        for(User user : userList){
            try {
                String contents = MusicConstant.RECOMMEND_EMAIL_HTML;
                String replace = contents.replace("{{musicTitle}}", music.getTitle());
                String finalContent = replace.replace("{{musicAuthor}}", music.getAuthor());
                emailUtil.sendHtmlMail(user.getEmail(),"recommend",finalContent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
