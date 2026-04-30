package com.sadguru.blogapplication.services;

import com.sadguru.blogapplication.payloads.CategoryDTO;
import com.sadguru.blogapplication.payloads.PageResponse;

import java.util.List;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Integer categoryId);

    void deleteCategory(Integer categoryId);

    CategoryDTO getCategory(Integer categoryId);

    PageResponse<CategoryDTO> getCategories(int pageNumber, int pageSize, String sortBy, String sortDir);
}