package com.example.music.mapper;

import com.example.music.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    void addComment(Comment comment);
    Comment queryById(int id);
    List<Comment> queryByMusicId(int musicId);
    void modifyComment(Comment comment);
    void deleteComment(int id);
    List<Comment> queryByParentId(int parentId);
    void deleteCommentByMusicId(int musicId);
}
