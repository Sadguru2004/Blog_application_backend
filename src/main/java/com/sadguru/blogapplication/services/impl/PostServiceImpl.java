package com.sadguru.blogapplication.services.impl;

import com.sadguru.blogapplication.entities.Post;
import com.sadguru.blogapplication.entities.User;
import com.sadguru.blogapplication.entities.Category;
import com.sadguru.blogapplication.exceptions.ResourceNotFoundException;
import com.sadguru.blogapplication.payloads.CommentDTO;
import com.sadguru.blogapplication.payloads.PageResponse;
import com.sadguru.blogapplication.payloads.PostDTO;
import com.sadguru.blogapplication.repositories.PostRepo;
import com.sadguru.blogapplication.repositories.UserRepo;
import com.sadguru.blogapplication.repositories.CategoryRepo;
import com.sadguru.blogapplication.services.FileService;
import com.sadguru.blogapplication.services.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    private Post dtoToPost(PostDTO postDTO) {
        return modelMapper.map(postDTO, Post.class);
    }

    private PostDTO postToDto(Post post) {

        PostDTO postDTO = modelMapper.map(post, PostDTO.class);

        postDTO.setUserId(post.getUser().getId());
        postDTO.setCategoryId(post.getCategory().getCategoryId());
        postDTO.setUserName(post.getUser().getName());      // user name
        postDTO.setCategoryName(post.getCategory().getCategoryTitle()); // category name

        List<CommentDTO> commentDTOs = Optional.ofNullable(post.getComments())
                .orElse(Collections.emptyList())   // <- ensures empty list instead of null
                .stream()
                .map(comment -> {
                    CommentDTO dto = new CommentDTO();
                    dto.setCommentId(comment.getCommentId());
                    dto.setContent(comment.getContent());
                    dto.setUserName(comment.getUser().getName());
                    return dto;
                })
                .collect(Collectors.toList());

        postDTO.setComments(commentDTOs);
        return postDTO;
    }

    @Override
    public PostDTO createPost(PostDTO postDTO) {
        User user = userRepo.findById(postDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", postDTO.getUserId()));

        Category category = categoryRepo.findById(postDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Id", postDTO.getCategoryId()));

        Post post = dtoToPost(postDTO);
        post.setImageName("default.png");
        post.setUser(user);
        post.setCategory(category);
        post.setAddDate(new Date());

        Post savedPost = postRepo.save(post);
        return postToDto(savedPost);
    }

    @Override
    public PostDTO updatePost(PostDTO postDTO, Integer postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "Id", postId));

        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setImageName(postDTO.getImageName());

        // Optional: update category or user if passed
        if(postDTO.getCategoryId() != null) {
            Category category = categoryRepo.findById(postDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "Id", postDTO.getCategoryId()));
            post.setCategory(category);
        }

        if(postDTO.getUserId() != null) {
            User user = userRepo.findById(postDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "Id", postDTO.getUserId()));
            post.setUser(user);
        }

        Post updatedPost = postRepo.save(post);
        return postToDto(updatedPost);
    }

    @Override
    public void deletePost(Integer postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "Id", postId));

        String imageName = post.getImageName();

        if (imageName != null && !imageName.equals("default.png")) {
            fileService.deleteImage("images/", imageName);
        }

        postRepo.delete(post);
    }

    @Override
    public PageResponse<PostDTO> getAllPost(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Post> pagePost = this.postRepo.findAll(pageable);

        List<PostDTO> postDTOs = pagePost.getContent()
                .stream()
                .map(this::postToDto)
                .collect(Collectors.toList());

        PageResponse<PostDTO> response = new PageResponse<>();

        response.setContent(postDTOs);
        response.setPageNumber(pagePost.getNumber());
        response.setPageSize(pagePost.getSize());
        response.setTotalElements(pagePost.getTotalElements());
        response.setTotalPages(pagePost.getTotalPages());
        response.setLastPage(pagePost.isLast());

        return response;
    }

    @Override
    public PostDTO getPostById(Integer postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "Id", postId));
        return postToDto(post);
    }

    @Override
    public PageResponse<PostDTO> getPostByCategory(Integer categoryId, int pageNumber, int pageSize) {

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Id", categoryId));

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Post> pagePost = postRepo.findByCategory(category, pageable);

        List<PostDTO> postDTOs = pagePost.getContent()
                .stream()
                .map(this::postToDto)
                .collect(Collectors.toList());

        PageResponse<PostDTO> response = new PageResponse<>();

        response.setContent(postDTOs);
        response.setPageNumber(pagePost.getNumber());
        response.setPageSize(pagePost.getSize());
        response.setTotalElements(pagePost.getTotalElements());
        response.setTotalPages(pagePost.getTotalPages());
        response.setLastPage(pagePost.isLast());

        return response;
    }

    @Override
    public PageResponse<PostDTO> getPostsByUser(Integer userId, int pageNumber, int pageSize) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("addDate").descending());

        Page<Post> pagePost = postRepo.findByUser(user, pageable);

        List<PostDTO> postDTOs = pagePost.getContent()
                .stream()
                .map(this::postToDto)
                .collect(Collectors.toList());

        PageResponse<PostDTO> response = new PageResponse<>();

        response.setContent(postDTOs);
        response.setPageNumber(pagePost.getNumber());
        response.setPageSize(pagePost.getSize());
        response.setTotalElements(pagePost.getTotalElements()); // 🔥 total posts
        response.setTotalPages(pagePost.getTotalPages());
        response.setLastPage(pagePost.isLast());

        return response;
    }

    @Override
    public List<PostDTO> searchPosts(String keyword) {
        return postRepo.findByTitleContainingIgnoreCase(keyword).stream()
                .map(this::postToDto)
                .collect(Collectors.toList());
    }

}