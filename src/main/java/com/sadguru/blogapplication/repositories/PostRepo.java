package com.sadguru.blogapplication.repositories;

import com.sadguru.blogapplication.entities.Category;
import com.sadguru.blogapplication.entities.Post;
import com.sadguru.blogapplication.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepo extends JpaRepository<Post,Integer> {

    Page<Post> findByCategory(Category category, Pageable pageable);

    Page<Post> findByUser(User user, Pageable pageable);

    List<Post> findByTitleContainingIgnoreCase(String keyword);
}
