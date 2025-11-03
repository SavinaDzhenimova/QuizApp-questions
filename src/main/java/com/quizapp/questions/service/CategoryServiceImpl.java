package com.quizapp.questions.service;

import com.quizapp.questions.model.dto.AddCategoryDTO;
import com.quizapp.questions.model.entity.Category;
import com.quizapp.questions.repository.CategoryRepository;
import com.quizapp.questions.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Optional<Category> findCategoryByName(String name) {
        return this.categoryRepository.findByName(name);
    }

    @Override
    public Category addCategory(AddCategoryDTO addCategoryDTO) {

        Category category = Category.builder()
                .name(addCategoryDTO.getName())
                .description(addCategoryDTO.getDescription())
                .build();

        return this.categoryRepository.saveAndFlush(category);
    }

    @Override
    public void deleteCategoryById(Long id) {
        this.categoryRepository.deleteById(id);
    }
}