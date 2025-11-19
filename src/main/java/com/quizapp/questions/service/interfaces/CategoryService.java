package com.quizapp.questions.service.interfaces;

import com.quizapp.questions.model.dto.AddCategoryDTO;
import com.quizapp.questions.model.dto.CategoryDTO;
import com.quizapp.questions.model.dto.CategoryPageDTO;
import com.quizapp.questions.model.dto.UpdateCategoryDTO;
import com.quizapp.questions.model.entity.Category;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryService {

    Optional<Category> findCategoryByName(String name);

    void addCategory(AddCategoryDTO addCategoryDTO);

    void deleteCategoryById(Long id);

    CategoryPageDTO getAllCategories(String categoryName, Pageable pageable);

    CategoryDTO getCategoryById(Long id);

    Optional<Category> findCategoryById(Long categoryId);

    void updateCategory(Long id, UpdateCategoryDTO updateCategoryDTO);
}