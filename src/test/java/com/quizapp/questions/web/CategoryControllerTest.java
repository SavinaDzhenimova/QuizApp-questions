package com.quizapp.questions.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizapp.questions.model.dto.category.AddCategoryDTO;
import com.quizapp.questions.model.dto.category.CategoryDTO;
import com.quizapp.questions.model.dto.category.CategoryPageDTO;
import com.quizapp.questions.model.dto.category.UpdateCategoryDTO;
import com.quizapp.questions.service.interfaces.CategoryService;
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
}