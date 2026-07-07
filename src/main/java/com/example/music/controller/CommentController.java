package com.example.music.controller;

import com.example.music.controller.cmd.CommentCmd;
import com.example.music.controller.converter.CommentVOConverter;
import com.example.music.controller.vo.BaseVO;
import com.example.music.controller.vo.CommentVO;
import com.example.music.controller.vo.MultiCommentVO;
import com.example.music.entity.Comment;
import com.example.music.entity.User;
import com.example.music.exception.CommentNotExistException;
import com.example.music.service.CommentService;
import com.example.music.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public BaseVO addComment(@RequestBody CommentCmd cmd){
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            commentService.addComment(cmd);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
        }
    }

    @GetMapping("/query")
    public MultiCommentVO queryByMusicId(int musicId){
        long startTime = System.currentTimeMillis();
        long endTime;
        MultiCommentVO multiCommentVO = new MultiCommentVO();
        try {
            List<Comment> commentList = commentService.queryByMusicId(musicId);
            List<CommentVO> commentVOList = CommentVOConverter.convertList(commentList);
            fillUserNames(commentVOList);
            endTime = System.currentTimeMillis();
            BaseVO baseVO = BaseVO.buildBaseVO(200, true, endTime - startTime, null);
            multiCommentVO.setCommentVOList(commentVOList);
            multiCommentVO.setBaseVO(baseVO);
            return multiCommentVO;
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            BaseVO baseVO = BaseVO.buildBaseVO(500, false, endTime - startTime, "其他未知异常");
            multiCommentVO.setBaseVO(baseVO);
            return multiCommentVO;
        }
    }

    @GetMapping("/get")
    public CommentVO getById(int id){
        try {
            Comment comment = commentService.queryById(id);
            CommentVO vo = CommentVOConverter.convert(comment);
            if (vo != null) {
                try {
                    User user = userService.queryById(vo.getUserId());
                    vo.setUserName(user != null ? user.getName() : ("User #" + vo.getUserId()));
                } catch (Exception e) {
                    vo.setUserName("User #" + vo.getUserId());
                }
            }
            return vo;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @PutMapping("/modify")
    public BaseVO modifyComment(int id, String content){
        long startTime = System.currentTimeMillis();
        long endTime;
        try{
            commentService.modifyComment(id, content);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (CommentNotExistException e){
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, "修改评论失败");
        }
    }

    @DeleteMapping("/delete")
    public BaseVO deleteComment(int id){
        long startTime = System.currentTimeMillis();
        long endTime;
        try{
            commentService.deleteComment(id);
            endTime = System.currentTimeMillis();
            return BaseVO.buildBaseVO(200, true, endTime - startTime, null);
        } catch (CommentNotExistException e){
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, e.getMessage());
        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            log.error(e.getMessage());
            return BaseVO.buildBaseVO(500, false, endTime - startTime, "删除评论失败");
        }
    }

    private void fillUserNames(List<CommentVO> commentVOList){
        if (commentVOList == null || commentVOList.isEmpty()) {
            return;
        }
        Map<Integer, String> nameCache = new HashMap<>();
        for (CommentVO vo : commentVOList) {
            int userId = vo.getUserId();
            String name = nameCache.get(userId);
            if (name == null) {
                try {
                    User user = userService.queryById(userId);
                    name = user != null ? user.getName() : ("User #" + userId);
                } catch (Exception e) {
                    name = "User #" + userId;
                }
                nameCache.put(userId, name);
            }
            vo.setUserName(name);
        }
    }
}