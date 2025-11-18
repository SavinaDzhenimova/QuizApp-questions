package com.quizapp.questions.service;

import com.quizapp.questions.model.dto.AddCategoryDTO;
import com.quizapp.questions.model.dto.CategoryDTO;
import com.quizapp.questions.model.dto.CategoryPageDTO;
import com.quizapp.questions.model.dto.UpdateCategoryDTO;
import com.quizapp.questions.model.entity.Category;
import com.quizapp.questions.model.enums.ApiStatus;
import com.quizapp.questions.repository.CategoryRepository;
import com.quizapp.questions.repository.spec.CategorySpecifications;
import com.quizapp.questions.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public CategoryPageDTO getAllCategories(String categoryName, Pageable pageable) {

        Specification<Category> spec = Specification
                .allOf(CategorySpecifications.hasName(categoryName));

        Page<Category> categoriesPage = this.categoryRepository.findAll(spec, pageable);

        List<CategoryDTO> categoryDTOs = categoriesPage.getContent().stream()
                .map(this::categoryToDTO)
                .toList();

        return CategoryPageDTO.builder()
                .categories(categoryDTOs)
                .totalElements(categoriesPage.getTotalElements())
                .totalPages(categoriesPage.getTotalPages())
                .currentPage(categoriesPage.getNumber())
                .size(categoriesPage.getSize())
                .build();
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        return this.categoryRepository.findById(id)
                .map(this::categoryToDTO)
                .orElse(null);
    }

    @Override
    public Optional<Category> findCategoryById(Long categoryId) {
        return this.categoryRepository.findById(categoryId);
    }

    private CategoryDTO categoryToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    @Override
    public ApiStatus addCategory(AddCategoryDTO addCategoryDTO) {

        if (addCategoryDTO == null) {
            return ApiStatus.VALIDATION_ERROR;
        }

        Optional<Category> optionalCategory = this.categoryRepository.findByName(addCategoryDTO.getName());

        if (optionalCategory.isPresent()) {
            return ApiStatus.CONFLICT;
        }

        Category category = Category.builder()
                .name(addCategoryDTO.getName())
                .description(addCategoryDTO.getDescription())
                .build();

        this.categoryRepository.saveAndFlush(category);
        return ApiStatus.CREATED;
    }

    @Override
    public ApiStatus updateCategory(Long id, UpdateCategoryDTO updateCategoryDTO) {
        Optional<Category> optionalCategory = this.categoryRepository.findById(id);

        if (optionalCategory.isEmpty()) {
            return ApiStatus.NOT_FOUND;
        }

        Category category = optionalCategory.get();
        boolean changed = false;

        if (!category.getDescription().equals(updateCategoryDTO.getDescription())) {
            category.setDescription(updateCategoryDTO.getDescription());
            changed = true;
        }

        if (!changed) {
            return ApiStatus.NO_CHANGES;
        }

        this.categoryRepository.saveAndFlush(category);
        return ApiStatus.UPDATED;
    }

    @Override
    public boolean deleteCategoryById(Long id) {
        if (!this.categoryRepository.existsById(id)) {
            return false;
        }

        this.categoryRepository.deleteById(id);
        return true;
    }
}