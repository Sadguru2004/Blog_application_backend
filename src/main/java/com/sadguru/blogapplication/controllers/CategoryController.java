package com.sadguru.blogapplication.controllers;

import com.sadguru.blogapplication.payloads.ApiResponse;
import com.sadguru.blogapplication.payloads.CategoryDTO;
import com.sadguru.blogapplication.payloads.PageResponse;
import com.sadguru.blogapplication.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Create Category
    @PostMapping("/")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    // Update Category
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                      @PathVariable("id") Integer id) {
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryDTO, id);
        return ResponseEntity.ok(updatedCategory);
    }

    // Delete Category
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable("id") Integer id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(new ApiResponse("Category deleted successfully", true), HttpStatus.OK);
    }

    // Get single Category
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable("id") Integer id) {
        CategoryDTO category = categoryService.getCategory(id);
        return ResponseEntity.ok(category);
    }

    // Get all Categories
    @GetMapping("/")
    public ResponseEntity<PageResponse<CategoryDTO>> getAllCategories(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "categoryId") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(categoryService.getCategories(pageNumber, pageSize, sortBy, sortDir));
    }
}