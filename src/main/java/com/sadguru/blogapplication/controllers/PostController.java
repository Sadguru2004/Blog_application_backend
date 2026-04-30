package com.sadguru.blogapplication.controllers;

import com.sadguru.blogapplication.config.AppConstants;
import com.sadguru.blogapplication.payloads.ApiResponse;
import com.sadguru.blogapplication.payloads.PageResponse;
import com.sadguru.blogapplication.payloads.PostDTO;
import com.sadguru.blogapplication.payloads.PostResponse;
import com.sadguru.blogapplication.services.FileService;
import com.sadguru.blogapplication.services.PostService;
import com.sadguru.blogapplication.config.AppConstants;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private FileService fileService;

    // Create Post
    @PostMapping("/")
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostDTO postDTO) {
        PostDTO createdPost = postService.createPost(postDTO);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    // Update Post
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@Valid @RequestBody PostDTO postDTO,
                                              @PathVariable("id") Integer id) {
        PostDTO updatedPost = postService.updatePost(postDTO, id);
        return ResponseEntity.ok(updatedPost);
    }

    // Delete Post
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable("id") Integer id) {
        postService.deletePost(id);
        return new ResponseEntity<>(new ApiResponse("Post deleted successfully", true), HttpStatus.OK);
    }

    // Get single Post
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable("id") Integer id) {
        PostDTO post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    // Get all Posts
    @GetMapping("/")
    public ResponseEntity<PageResponse<PostDTO>> getAllPosts(
            @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR) String sortDir
    ) {
        return ResponseEntity.ok(
                postService.getAllPost(pageNumber, pageSize, sortBy, sortDir)
        );
    }

    // Get Posts by User
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponse<PostDTO>> getPostsByUser(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return ResponseEntity.ok(
                postService.getPostsByUser(userId, pageNumber, pageSize)
        );
    }

    // Search Posts by Title
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<PostDTO>> searchPosts(@PathVariable("keyword") String keyword) {
        List<PostDTO> posts = postService.searchPosts(keyword);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/image/upload/{postId}")
    public ResponseEntity<PostDTO> uploadPostImage(
            @RequestParam("image") MultipartFile image,
            @PathVariable Integer postId
    ) throws IOException {

        PostDTO postDTO = this.postService.getPostById(postId);

        String fileName = this.fileService.uploadImage("images/", image);

        postDTO.setImageName(fileName);

        PostDTO updatedPost = this.postService.updatePost(postDTO, postId);

        return ResponseEntity.ok(updatedPost);
    }

    @GetMapping("/image/{imageName}")
    public void serveImage(
            @PathVariable String imageName,
            HttpServletResponse response
    ) throws IOException {

        InputStream is = fileService.getResource("images/", imageName);

        response.setContentType("image/png");

        StreamUtils.copy(is, response.getOutputStream());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PageResponse<PostDTO>> getPostsByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize
    ) {
        PageResponse<PostDTO> response =
                postService.getPostByCategory(categoryId, pageNumber, pageSize);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}