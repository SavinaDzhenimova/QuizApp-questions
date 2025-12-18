package com.quizapp.questions.service.interfaces;

import com.quizapp.questions.model.dto.category.AddCategoryDTO;
import com.quizapp.questions.model.dto.category.CategoryDTO;
import com.quizapp.questions.model.dto.category.CategoryPageDTO;
import com.quizapp.questions.model.dto.category.UpdateCategoryDTO;
import com.quizapp.questions.model.entity.Category;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryService {

    Optional<Category> findCategoryByName(String name);

    void addCategory(AddCategoryDTO addCategoryDTO);

    CategoryPageDTO getAllCategories(String categoryName, Pageable pageable);

    CategoryDTO getCategoryById(Long id);

    Optional<Category> findCategoryById(Long categoryId);

    void updateCategory(Long id, UpdateCategoryDTO updateCategoryDTO);
}