package com.sadguru.blogapplication.services.impl;

import com.sadguru.blogapplication.entities.Comment;
import com.sadguru.blogapplication.entities.Post;
import com.sadguru.blogapplication.entities.User;
import com.sadguru.blogapplication.exceptions.ResourceNotFoundException;
import com.sadguru.blogapplication.payloads.CommentDTO;
import com.sadguru.blogapplication.repositories.CommentRepo;
import com.sadguru.blogapplication.repositories.PostRepo;
import com.sadguru.blogapplication.repositories.UserRepo;
import com.sadguru.blogapplication.services.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepo commentRepo;
    private final PostRepo postRepo;
    private final UserRepo userRepo;

    public CommentServiceImpl(CommentRepo commentRepo,
                              PostRepo postRepo,
                              UserRepo userRepo) {
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    @Override
    public CommentDTO createComment(CommentDTO commentDTO) {

        Post post = postRepo.findById(commentDTO.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "Id", commentDTO.getPostId()));

        User user = userRepo.findById(commentDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", commentDTO.getUserId()));

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setPost(post);
        comment.setUser(user);

        Comment saved = commentRepo.save(comment);

        CommentDTO dto = new CommentDTO();
        dto.setCommentId(saved.getCommentId());
        dto.setContent(saved.getContent());
        dto.setUserName(user.getName());
        return dto;
    }

    @Override
    public void deleteComment(Integer commentId, Integer userId) {

        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getPost().getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to delete this comment");
        }

        commentRepo.delete(comment);
    }

    @Override
    public List<CommentDTO> getCommentsByPost(Integer postId) {

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "Id", postId));

        List<Comment> comments = commentRepo.findByPost(post);

        return comments.stream().map(comment -> {
            CommentDTO dto = new CommentDTO();
            dto.setCommentId(comment.getCommentId());
            dto.setContent(comment.getContent());
            dto.setUserId(comment.getUser().getId());
            dto.setPostId(postId);
            return dto;
        }).collect(Collectors.toList());
    }
}