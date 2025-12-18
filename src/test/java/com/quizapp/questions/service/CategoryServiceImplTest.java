package com.quizapp.questions.service;

import com.quizapp.questions.exception.CategoryNotFoundException;
import com.quizapp.questions.exception.DuplicateResourceException;
import com.quizapp.questions.model.dto.AddCategoryDTO;
import com.quizapp.questions.model.dto.CategoryDTO;
import com.quizapp.questions.model.dto.CategoryPageDTO;
import com.quizapp.questions.model.entity.Category;
import com.quizapp.questions.repository.CategoryRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

    @Test
    void getAllCategories_ShouldReturnPageWithMappedCategoryDTOs() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> page = new PageImpl<>(List.of(this.category), pageable, 1);

        when(this.mockCategoryRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        CategoryPageDTO pageDTO = this.categoryService.getAllCategories("Maths", pageable);

        Assertions.assertNotNull(pageDTO);
        Assertions.assertFalse(pageDTO.getCategories().isEmpty());
        Assertions.assertEquals(1, pageDTO.getCategories().size());
        Assertions.assertEquals(1L, pageDTO.getCategories().get(0).getId());
        Assertions.assertEquals("Maths", pageDTO.getCategories().get(0).getName());
        Assertions.assertEquals(0, pageDTO.getCurrentPage());
        Assertions.assertEquals(1, pageDTO.getTotalPages());
        Assertions.assertEquals(1L, pageDTO.getTotalElements());
        Assertions.assertEquals(10, pageDTO.getSize());
    }

    @Test
    void getAllCategories_ShouldReturnEmptyPage_WhenCategoriesNotFound() {
        Pageable pageable = PageRequest.of(0, 10);

        when(this.mockCategoryRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty());

        CategoryPageDTO pageDTO = this.categoryService.getAllCategories("Music", pageable);

        Assertions.assertNotNull(pageDTO);
        Assertions.assertTrue(pageDTO.getCategories().isEmpty());
        Assertions.assertEquals(0, pageDTO.getCategories().size());
        Assertions.assertEquals(0, pageDTO.getCurrentPage());
        Assertions.assertEquals(1, pageDTO.getTotalPages());
        Assertions.assertEquals(0L, pageDTO.getTotalElements());
        Assertions.assertEquals(0, pageDTO.getSize());
    }

    @Test
    void addCategory_ShouldThrowException_WhenInputDataIsInvalid() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> this.categoryService.addCategory(null));

        Assertions.assertEquals("Невалидни входни данни.", exception.getMessage());
        verifyNoInteractions(this.mockCategoryRepository);
    }

    @Test
    void addCategory_ShouldThrowException_WhenCategoryExists() {
        AddCategoryDTO addCategoryDTO = AddCategoryDTO.builder()
                .name("Maths")
                .description("Description")
                .build();

        when(this.mockCategoryRepository.findByName("Maths"))
                .thenReturn(Optional.of(this.category));

        DuplicateResourceException exception = Assertions.assertThrows(DuplicateResourceException.class,
                () -> this.categoryService.addCategory(addCategoryDTO));

        Assertions.assertEquals("Категория с име Maths вече съществува.", exception.getMessage());
        verify(this.mockCategoryRepository, times(1)).findByName("Maths");
        verify(this.mockCategoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void addCategory_ShouldSaveCategory_WhenInputDataIsValid() {
        AddCategoryDTO addCategoryDTO = AddCategoryDTO.builder()
                .name("Music")
                .description("Description")
                .build();

        when(this.mockCategoryRepository.findByName("Music"))
                .thenReturn(Optional.empty());

        this.categoryService.addCategory(addCategoryDTO);

        verify(this.mockCategoryRepository, times(1)).findByName("Music");
        verify(this.mockCategoryRepository, times(1)).saveAndFlush(any(Category.class));
    }
}