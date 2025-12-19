package com.quizapp.questions.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizapp.questions.exception.CategoryNotFoundException;
import com.quizapp.questions.exception.DuplicateResourceException;
import com.quizapp.questions.exception.NoChangesException;
import com.quizapp.questions.model.dto.category.AddCategoryDTO;
import com.quizapp.questions.model.dto.category.CategoryDTO;
import com.quizapp.questions.model.dto.category.CategoryPageDTO;
import com.quizapp.questions.model.dto.category.UpdateCategoryDTO;
import com.quizapp.questions.service.interfaces.CategoryService;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        this.categoryDTO = CategoryDTO.builder()
                .id(1L)
                .name("Maths")
                .description("Description")
                .build();
    }

    @Test
    void getAllCategories_ShouldReturnPage_WhenCategoriesFound() throws Exception {
        CategoryPageDTO page = new CategoryPageDTO(List.of(this.categoryDTO));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        when(this.categoryService.getAllCategories("", pageable))
                .thenReturn(page);

        this.mockMvc.perform(get("/api/categories")
                        .param("page", "0")
                        .param("size", "10")
                        .param("categoryName", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[0].id").value(1L))
                .andExpect(jsonPath("$.categories[0].name").value("Maths"))
                .andExpect(jsonPath("$.categories[0].description").value("Description"));
    }

    @Test
    void getAllCategories_ShouldReturnEmptyPage_WhenCategoriesNotFound() throws Exception {
        CategoryPageDTO page = new CategoryPageDTO(Collections.emptyList());

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        when(this.categoryService.getAllCategories("", pageable))
                .thenReturn(page);

        this.mockMvc.perform(get("/api/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories").isEmpty());
    }

    @Test
    void getCategoryById_ShouldReturnCategoryDTO_WhenCategoryFound() throws Exception {
        when(this.categoryService.getCategoryById(1L))
                .thenReturn(this.categoryDTO);

        this.mockMvc.perform(get("/api/categories/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Maths"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void getCategoryById_ShouldReturnProblemDetail_WhenCategoryNotFound() throws Exception {
        when(this.categoryService.getCategoryById(5L))
                .thenThrow(new CategoryNotFoundException(5L));

        this.mockMvc.perform(get("/api/categories/5"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Category not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Категория с id 5 не е намерена."))
                .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"))
                .andExpect(jsonPath("$.type").value("urn:problem:category_not_found"))
                .andExpect(jsonPath("$.instance").value("/api/categories/5"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void addCategory_ShouldCreateNewCategory_WhenInputDataIsValid() throws Exception {
        AddCategoryDTO addCategoryDTO = AddCategoryDTO.builder()
                .name("Music")
                .description("Description")
                .build();

        this.mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(addCategoryDTO)))
                .andExpect(status().isCreated());

        verify(this.categoryService, times(1)).addCategory(addCategoryDTO);
    }

    @Test
    void addCategory_ShouldReturnProblemDetail_WhenDuplicateResource() throws Exception {
        AddCategoryDTO addCategoryDTO = AddCategoryDTO.builder()
                .name("Maths")
                .description("Description")
                .build();

        doThrow(new DuplicateResourceException("Категория с име Maths вече съществува."))
                .when(this.categoryService).addCategory(addCategoryDTO);

        this.mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(addCategoryDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Duplicate resource"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Категория с име Maths вече съществува."))
                .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"))
                .andExpect(jsonPath("$.type").value("urn:problem:duplicate_resource"))
                .andExpect(jsonPath("$.instance").value("/api/categories"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void addCategory_ShouldReturnProblemDetail_WhenValidationError() throws Exception {
        AddCategoryDTO addCategoryDTO = new AddCategoryDTO();

        this.mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(addCategoryDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Internal server error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.type").value("urn:problem:internal_server_error"))
                .andExpect(jsonPath("$.instance").value("/api/categories"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void updateCategory_ShouldUpdateCategory_WhenCategoryExists() throws Exception {
        UpdateCategoryDTO updateCategoryDTO = UpdateCategoryDTO.builder()
                .id(1L)
                .name("Maths")
                .description("New description")
                .build();

        this.mockMvc.perform(put("/api/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateCategoryDTO)))
                .andExpect(status().isOk());

        verify(this.categoryService, times(1)).updateCategory(1L, updateCategoryDTO);
    }

    @Test
    void updateCategory_ShouldReturnProblemDetail_WhenCategoryNotFound() throws Exception {
        UpdateCategoryDTO updateCategoryDTO = UpdateCategoryDTO.builder()
                .id(5L)
                .name("Maths")
                .description("New description")
                .build();

        doThrow(new CategoryNotFoundException(5L))
                .when(this.categoryService).updateCategory(5L, updateCategoryDTO);

        this.mockMvc.perform(put("/api/categories/{id}", 5L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateCategoryDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Category not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Категория с id 5 не е намерена."))
                .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"))
                .andExpect(jsonPath("$.type").value("urn:problem:category_not_found"))
                .andExpect(jsonPath("$.instance").value("/api/categories/5"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void updateCategory_ShouldReturnProblemDetail_WhenNoChanges() throws Exception {
        UpdateCategoryDTO updateCategoryDTO = UpdateCategoryDTO.builder()
                .id(1L)
                .name("Maths")
                .description("Description")
                .build();

        doThrow(new NoChangesException("Няма промени за запазване."))
                .when(this.categoryService).updateCategory(1L, updateCategoryDTO);

        this.mockMvc.perform(put("/api/categories/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateCategoryDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("No changes"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Няма промени за запазване."))
                .andExpect(jsonPath("$.code").value("NO_CHANGES"))
                .andExpect(jsonPath("$.type").value("urn:problem:no_changes"))
                .andExpect(jsonPath("$.instance").value("/api/categories/1"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}