package com.example.music.service.impl;

import com.example.music.controller.cmd.CommentCmd;
import com.example.music.entity.Comment;
import com.example.music.entity.Music;
import com.example.music.entity.Notification;
import com.example.music.exception.CommentNotExistException;
import com.example.music.mapper.CommentMapper;
import com.example.music.mapper.MusicMapper;
import com.example.music.repository.MusicRankRepository;
import com.example.music.service.CommentService;
import com.example.music.service.NotificationService;
import constant.MusicConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private MusicRankRepository musicRankRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MusicMapper musicMapper;

    @Transactional
    @Override
    public void addComment(CommentCmd cmd) {
        Comment comment = buildComment(cmd);
        commentMapper.addComment(comment);
        int parentId = cmd.getParentId();
        musicRankRepository.updateScore(cmd.getMusicId(), MusicConstant.COMMENT_SCORE);
        if(parentId==0){
            return;
        }
        Comment parentComment = commentMapper.queryById(parentId);
        parentComment.setLeaf(false);
        commentMapper.modifyComment(parentComment);
        Notification notification = buildNotification(cmd.getUserId(), parentComment.getUserId(), "COMMENT", parentId, "APPLY", cmd.getContent());
        notificationService.addNotification(notification);
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
            throw new CommentNotExistException("comment not existed");
        }
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(id);
        int count = 0;
        while(!queue.isEmpty()){
            int node = queue.remove();
            count++;
            commentMapper.deleteComment(node);
            List<Comment> childComment = commentMapper.queryByParentId(node);
            for (int i = 0; i < childComment.size(); i++) {
                int childId = childComment.get(i).getId();
                queue.offer(childId);
            }
        }
        musicRankRepository.updateScore(comment.getMusicId(),-MusicConstant.COMMENT_SCORE*count);
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

    private Notification buildNotification(int from, int to, String targetType, int targetId, String operation,String content){
        Notification notification = new Notification();

        notification.setFrom(from);
        notification.setTo(to);
        notification.setTargetType(targetType);
        notification.setTargetId(targetId);
        notification.setOperation(operation);
        notification.setContent(content);

        return notification;
    }
}
