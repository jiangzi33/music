package com.example.music.controller.converter;

import com.example.music.controller.vo.CommentVO;
import com.example.music.entity.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentVOConverter {

    public static CommentVO convert(Comment comment){

        if(comment == null){
            return null;
        }

        CommentVO vo = new CommentVO();

        vo.setId(comment.getId());
        vo.setUserId(comment.getUserId());
        vo.setContent(comment.getContent());
        vo.setMusicId(comment.getMusicId());
        vo.setParentId(comment.getParentId());
        vo.setCreateTime(comment.getCreateTime());
        vo.setLeaf(comment.isLeaf());

        return vo;
    }

    public static List<CommentVO> convertList(List<Comment> commentList){

        List<CommentVO> voList = new ArrayList<>();

        if(commentList == null || commentList.isEmpty()){
            return voList;
        }

        for(Comment comment : commentList){
            voList.add(convert(comment));
        }

        return voList;
    }

}