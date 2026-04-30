package com.sadguru.blogapplication.controllers;

import com.sadguru.blogapplication.payloads.CommentDTO;
import com.sadguru.blogapplication.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/")
    public ResponseEntity<CommentDTO> createComment(@Valid  @RequestBody CommentDTO commentDTO) {
        return ResponseEntity.ok(commentService.createComment(commentDTO));
    }


    @DeleteMapping("/{commentId}/user/{userId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Integer commentId,
            @PathVariable Integer userId
    ) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok("Comment deleted successfully");
    }


    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable Integer postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }
}