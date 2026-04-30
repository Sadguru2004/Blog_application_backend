package com.sadguru.blogapplication.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private Integer postId;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String imageName;

    private Date addDate;

    private Integer categoryId;  // Link to Category

    private Integer userId;      // Link to User

    private String categoryName;

    private String userName;

    private List<CommentDTO> comments = new ArrayList<>();
}