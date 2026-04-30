package com.sadguru.blogapplication.services;

import com.sadguru.blogapplication.payloads.CommentDTO;

import java.util.List;

public interface CommentService {

    CommentDTO createComment(CommentDTO commentDTO);

    void deleteComment(Integer commentId, Integer userId);

    List<CommentDTO> getCommentsByPost(Integer postId);
}