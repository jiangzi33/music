package com.example.music.service.impl;

import com.example.music.controller.cmd.CommentCmd;
import com.example.music.entity.Comment;
import com.example.music.entity.Music;
import com.example.music.exception.CommentNotExistException;
import com.example.music.mapper.CommentMapper;
import com.example.music.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Transactional
    @Override
    public void addComment(CommentCmd cmd) {
        Comment comment = buildComment(cmd);
        commentMapper.addComment(comment);
        int parentId = cmd.getParentId();
        if(parentId==0){
            return;
        }
        Comment parentComment = commentMapper.queryById(parentId);
        parentComment.setLeaf(false);
        commentMapper.modifyComment(parentComment);
    }

    @Override
    public Comment queryById(int id) {
        return commentMapper.queryById(id);
    }

    @Override
    public List<Comment> queryByMusicId(int musicId) {
        return commentMapper.queryByMusicId(musicId);
    }

    @Override
    public void modifyComment(int id, String content) {
        Comment comment = commentMapper.queryById(id);
        if(comment==null){
            throw new CommentNotExistException("comment not exist");
        }
        comment.setContent(content);
        commentMapper.modifyComment(comment);
    }

    @Override
    @Transactional
    public void deleteComment(int id) {
        Comment comment = commentMapper.queryById(id);
        if(comment==null){
            throw new CommentNotExistException("comment not exist");
        }
        commentMapper.deleteComment(id);
    }

    @Override
    public List<Comment> queryByParentId(int parentId) {
        return commentMapper.queryByParentId(parentId);
    }

    private Comment buildComment(CommentCmd cmd) {

        Comment comment = new Comment();

        comment.setUserId(cmd.getUserId());
        comment.setContent(cmd.getContent());
        comment.setMusicId(cmd.getMusicId());
        comment.setParentId(cmd.getParentId());
        // 新建评论默认是叶子评论
        comment.setLeaf(true);

        return comment;
    }
}
