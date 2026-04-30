package com.sadguru.blogapplication.services;

import com.sadguru.blogapplication.payloads.PageResponse;
import com.sadguru.blogapplication.payloads.PostDTO;
import com.sadguru.blogapplication.payloads.PostResponse;

import java.util.List;

public interface PostService {

    PostDTO createPost(PostDTO postDTO);

    PostDTO updatePost(PostDTO postDTO, Integer postId);

    void deletePost(Integer postId);

    PageResponse<PostDTO> getAllPost(int pageNumber, int pageSize, String sortBy, String sortDir);

    PostDTO getPostById(Integer postId);

    PageResponse<PostDTO> getPostByCategory(Integer categoryId, int pageNumber, int pageSize);

    PageResponse<PostDTO> getPostsByUser(Integer userId, int pageNumber, int pageSize);

    List<PostDTO> searchPosts(String keyword);
}