package com.quizapp.questions.service;

import com.quizapp.questions.exception.CategoryNotFoundException;
import com.quizapp.questions.model.dto.CategoryDTO;
import com.quizapp.questions.model.entity.Category;
import com.quizapp.questions.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository mockCategoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        this.category = Category.builder()
                .id(1L)
                .name("Maths")
                .description("Description")
                .questions(new ArrayList<>())
                .build();

        this.categoryDTO = CategoryDTO.builder()
                .id(1L)
                .name("Maths")
                .description("Description")
                .build();
    }

    @Test
    void findCategoryByName_ShouldReturnEmptyCategory_WhenCategoryNotFound() {
        when(this.mockCategoryRepository.findByName("Music"))
                .thenReturn(Optional.empty());

        Optional<Category> optionalCategory = this.categoryService.findCategoryByName("Music");

        Assertions.assertTrue(optionalCategory.isEmpty());
    }

    @Test
    void findCategoryByName_ShouldReturnCategory_WhenCategoryFound() {
        when(this.mockCategoryRepository.findByName("Maths"))
                .thenReturn(Optional.of(this.category));

        Optional<Category> optionalCategory = this.categoryService.findCategoryByName("Maths");

        Assertions.assertTrue(optionalCategory.isPresent());
        Assertions.assertEquals(optionalCategory.get(), this.category);
        Assertions.assertEquals(this.category.getId(), optionalCategory.get().getId());
        Assertions.assertEquals(this.category.getName(), optionalCategory.get().getName());
        Assertions.assertEquals(this.category.getDescription(), optionalCategory.get().getDescription());
        Assertions.assertNotNull(optionalCategory.get().getQuestions());
        Assertions.assertTrue(optionalCategory.get().getQuestions().isEmpty());
    }

    @Test
    void findCategoryById_ShouldReturnEmptyCategory_WhenCategoryNotFound() {
        when(this.mockCategoryRepository.findById(5L))
                .thenReturn(Optional.empty());

        Optional<Category> optionalCategory = this.categoryService.findCategoryById(5L);

        Assertions.assertTrue(optionalCategory.isEmpty());
    }

    @Test
    void findCategoryById_ShouldReturnCategory_WhenCategoryFound() {
        when(this.mockCategoryRepository.findById(1L))
                .thenReturn(Optional.of(this.category));

        Optional<Category> optionalCategory = this.categoryService.findCategoryById(1L);

        Assertions.assertTrue(optionalCategory.isPresent());
        Assertions.assertEquals(optionalCategory.get(), this.category);
        Assertions.assertEquals(this.category.getId(), optionalCategory.get().getId());
        Assertions.assertEquals(this.category.getName(), optionalCategory.get().getName());
        Assertions.assertEquals(this.category.getDescription(), optionalCategory.get().getDescription());
        Assertions.assertNotNull(optionalCategory.get().getQuestions());
        Assertions.assertTrue(optionalCategory.get().getQuestions().isEmpty());
    }

    @Test
    void getCategoryById_ShouldReturnCategoryDTO_WhenCategoryFound() {
        when(this.mockCategoryRepository.findById(1L))
                .thenReturn(Optional.of(this.category));

        CategoryDTO result = this.categoryService.getCategoryById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Maths", result.getName());
        Assertions.assertEquals("Description", result.getDescription());
    }

    @Test
    void getCategoryById_ShouldThrowException_WhenCategoryNotFound() {
        when(this.mockCategoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        CategoryNotFoundException exception = Assertions.assertThrows(CategoryNotFoundException.class,
                () -> this.categoryService.getCategoryById(1L));

        Assertions.assertEquals("Категория с id 1 не е намерена.", exception.getMessage());
    }
}