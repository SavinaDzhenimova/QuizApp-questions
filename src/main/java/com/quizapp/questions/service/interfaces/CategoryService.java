package com.quizapp.questions.service.interfaces;

import com.quizapp.questions.model.dto.AddCategoryDTO;
import com.quizapp.questions.model.dto.CategoryDTO;
import com.quizapp.questions.model.dto.UpdateCategoryDTO;
import com.quizapp.questions.model.entity.Category;
import com.quizapp.questions.model.enums.ApiStatus;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Optional<Category> findCategoryByName(String name);

    ApiStatus addCategory(AddCategoryDTO addCategoryDTO);

    boolean deleteCategoryById(Long id);

    List<CategoryDTO> getAllCategories();

    CategoryDTO getCategoryById(Long id);

    Optional<Category> findCategoryById(Long categoryId);

    ApiStatus updateCategory(Long id, UpdateCategoryDTO updateCategoryDTO);
}