package com.quizapp.questions.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizapp.questions.exception.CategoryNotFoundException;
import com.quizapp.questions.exception.QuestionNotFoundException;
import com.quizapp.questions.model.dto.category.AddCategoryDTO;
import com.quizapp.questions.model.dto.category.CategoryPageDTO;
import com.quizapp.questions.model.dto.category.UpdateCategoryDTO;
import com.quizapp.questions.model.dto.question.AddQuestionDTO;
import com.quizapp.questions.model.dto.question.QuestionDTO;
import com.quizapp.questions.model.dto.question.QuestionPageDTO;
import com.quizapp.questions.model.dto.question.UpdateQuestionDTO;
import com.quizapp.questions.service.interfaces.QuestionService;
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
                .andExpect(jsonPath("$.questions[0].categoryName").value("Maths"))
                .andExpect(jsonPath("$.questions[0].questionText").value("Question"))
                .andExpect(jsonPath("$.questions[0].correctAnswer").value("A"))
                .andExpect(jsonPath("$.questions[0].options").isArray())
                .andExpect(jsonPath("$.questions[0].options.length()").value(4))
                .andExpect(jsonPath("$.questions[0].options", containsInAnyOrder("A", "B", "C", "D")));
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

    @Test
    void getQuestionById_ShouldReturnQuestion_WhenQuestionExists() throws Exception {
        when(this.questionService.getQuestionById(1L))
                .thenReturn(this.questionDTO);

        this.mockMvc.perform(get("/api/questions/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.categoryName").value("Maths"))
                .andExpect(jsonPath("$.questionText").value("Question"))
                .andExpect(jsonPath("$.correctAnswer").value("A"))
                .andExpect(jsonPath("$.options").isArray())
                .andExpect(jsonPath("$.options.length()").value(4))
                .andExpect(jsonPath("$.options", containsInAnyOrder("A", "B", "C", "D")));
    }

    @Test
    void getQuestionById_ShouldReturnProblemDetail_WhenQuestionNotFound() throws Exception {
        when(this.questionService.getQuestionById(5L))
                .thenThrow(new QuestionNotFoundException(5L));

        mockMvc.perform(get("/api/questions/5"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Question not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Въпрос с id 5 не е намерен."))
                .andExpect(jsonPath("$.code").value("QUESTION_NOT_FOUND"))
                .andExpect(jsonPath("$.type").value("urn:problem:question_not_found"))
                .andExpect(jsonPath("$.instance").value("/api/questions/5"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getQuestionsByCategory_ShouldReturnListWithQuestionsDTO_WhenCategoryFound() throws Exception {
        when(this.questionService.getQuestionsByCategory(1L))
                .thenReturn(List.of(this.questionDTO));

        this.mockMvc.perform(get("/api/questions/category/{categoryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].categoryName").value("Maths"))
                .andExpect(jsonPath("$[0].questionText").value("Question"))
                .andExpect(jsonPath("$[0].correctAnswer").value("A"))
                .andExpect(jsonPath("$[0].options").isArray())
                .andExpect(jsonPath("$[0].options.length()").value(4))
                .andExpect(jsonPath("$[0].options", containsInAnyOrder("A", "B", "C", "D")));
    }

    @Test
    void getQuestionsByCategory_ShouldReturnEmptyList_WhenQuestionsNotFound() throws Exception {
        when(this.questionService.getQuestionsByCategory(5L))
                .thenReturn(Collections.emptyList());

        this.mockMvc.perform(get("/api/questions/category/{categoryId}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void addQuestion_ShouldCreateNewQuestion_WhenInputDataIsValid() throws Exception {
        AddQuestionDTO addQuestionDTO = AddQuestionDTO.builder()
                .categoryId(1L)
                .questionText("New question")
                .correctAnswer("C")
                .options("A, B, C, D")
                .build();

        this.mockMvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(addQuestionDTO)))
                .andExpect(status().isCreated());

        verify(this.questionService, times(1)).addQuestion(addQuestionDTO);
    }

    @Test
    void updateQuestion_ShouldUpdateQuestion_WhenQuestionExists() throws Exception {
        UpdateQuestionDTO updateQuestionDTO = UpdateQuestionDTO.builder()
                .id(1L)
                .categoryName("Music")
                .questionText("Updated")
                .correctAnswer("B")
                .options("A, B, C, E")
                .build();

        this.mockMvc.perform(put("/api/questions/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateQuestionDTO)))
                .andExpect(status().isOk());

        verify(this.questionService, times(1)).updateQuestion(1L, updateQuestionDTO);
    }
}