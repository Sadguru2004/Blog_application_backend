package com.sadguru.blogapplication.services.impl;

import com.sadguru.blogapplication.entities.Category;
import com.sadguru.blogapplication.exceptions.ResourceNotFoundException;
import com.sadguru.blogapplication.payloads.CategoryDTO;
import com.sadguru.blogapplication.payloads.PageResponse;
import com.sadguru.blogapplication.repositories.CategoryRepo;
import com.sadguru.blogapplication.services.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    private Category dtoToCategory(CategoryDTO categoryDTO) {
        return modelMapper.map(categoryDTO, Category.class);
    }

    private CategoryDTO categoryToDto(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = dtoToCategory(categoryDTO);
        Category savedCategory = categoryRepo.save(category);
        return categoryToDto(savedCategory);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Integer categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Id", categoryId));

        category.setCategoryTitle(categoryDTO.getCategoryTitle());
        category.setCategoryDescription(categoryDTO.getCategoryDescription());

        Category updatedCategory = categoryRepo.save(category);
        return categoryToDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Id", categoryId));
        categoryRepo.delete(category);
    }

    @Override
    public CategoryDTO getCategory(Integer categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Id", categoryId));
        return categoryToDto(category);
    }

    @Override
    public PageResponse<CategoryDTO> getCategories(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Category> pageCategory = this.categoryRepo.findAll(pageable);

        List<CategoryDTO> categoryDTOs = pageCategory.getContent()
                .stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());

        PageResponse<CategoryDTO> response = new PageResponse<>();

        response.setContent(categoryDTOs);
        response.setPageNumber(pageCategory.getNumber());
        response.setPageSize(pageCategory.getSize());
        response.setTotalElements(pageCategory.getTotalElements());
        response.setTotalPages(pageCategory.getTotalPages());
        response.setLastPage(pageCategory.isLast());

        return response;
    }
}