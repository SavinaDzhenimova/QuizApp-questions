package com.quizapp.questions.service.interfaces;

import com.quizapp.questions.model.dto.AddCategoryDTO;
import com.quizapp.questions.model.entity.Category;

import java.util.Optional;

public interface CategoryService {
    Optional<Category> findCategoryByName(String name);

    Category addCategory(AddCategoryDTO addCategoryDTO);

    void deleteCategoryById(Long id);
}
