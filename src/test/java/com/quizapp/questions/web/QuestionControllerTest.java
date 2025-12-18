package com.quizapp.questions.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizapp.questions.model.dto.category.CategoryPageDTO;
import com.quizapp.questions.model.dto.question.QuestionDTO;
import com.quizapp.questions.model.dto.question.QuestionPageDTO;
import com.quizapp.questions.service.interfaces.QuestionService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = QuestionController.class)
public class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuestionService questionService;

    private QuestionDTO questionDTO;

    @BeforeEach
    void setUp() {
        this.questionDTO = QuestionDTO.builder()
                .id(1L)
                .categoryId(1L)
                .categoryName("Maths")
                .questionText("Question")
                .correctAnswer("A")
                .options(List.of("A", "B", "C", "D"))
                .build();
    }

    @Test
    void getAllQuestions_ShouldReturnPage_WhenQuestionsFound() throws Exception {
        QuestionPageDTO page = new QuestionPageDTO(List.of(this.questionDTO));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        when(this.questionService.getAllQuestions("", 1L, pageable))
                .thenReturn(page);

        this.mockMvc.perform(get("/api/questions")
                        .param("page", "0")
                        .param("size", "10")
                        .param("questionText", "")
                        .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questions[0].id").value(1L))
                .andExpect(jsonPath("$.questions[0].questionText").value("Question"))
                .andExpect(jsonPath("$.questions[0].correctAnswer").value("A"))
                .andExpect(jsonPath("$.questions[0].options").isArray())
                .andExpect(jsonPath("$.questions[0].options.length()").value(4))
                .andExpect(jsonPath("$.questions[0].options", containsInAnyOrder("A", "B", "C", "D")));;
    }

    @Test
    void getAllCategories_ShouldReturnEmptyPage_WhenCategoriesNotFound() throws Exception {
        QuestionPageDTO page = new QuestionPageDTO(Collections.emptyList());

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        when(this.questionService.getAllQuestions("", 5L, pageable))
                .thenReturn(page);

        this.mockMvc.perform(get("/api/questions")
                        .param("page", "0")
                        .param("size", "10")
                        .param("categoryId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questions").isArray())
                .andExpect(jsonPath("$.questions").isEmpty());
    }
}