package com.sadguru.blogapplication.repositories;

import com.sadguru.blogapplication.entities.Comment;
import com.sadguru.blogapplication.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Integer> {

    List<Comment> findByPost(Post post);
}