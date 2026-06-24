package com.example.music.service;

import com.example.music.controller.cmd.CommentCmd;
import com.example.music.entity.Comment;

import java.util.List;

public interface CommentService {
    void addComment(CommentCmd cmd);
    Comment queryById(int id);
    List<Comment> queryByMusicId(int musicId);
    void modifyComment(int id, String content);
    void deleteComment(int id);
    List<Comment> queryByParentId(int parentId);
}
