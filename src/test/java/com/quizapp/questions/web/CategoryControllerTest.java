package com.quizapp.questions.web;

import com.quizapp.questions.model.dto.category.CategoryDTO;
import com.quizapp.questions.model.dto.category.CategoryPageDTO;
import com.quizapp.questions.service.interfaces.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void getAllCategories_ShouldReturnPage_WhenCategoriesFound() throws Exception {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .id(1L)
                .name("Maths")
                .description("Description")
                .build();
        CategoryPageDTO page = new CategoryPageDTO(List.of(categoryDTO));

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
}