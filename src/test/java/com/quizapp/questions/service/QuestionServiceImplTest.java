package com.quizapp.questions.service;

import com.quizapp.questions.model.dto.question.QuestionPageDTO;
import com.quizapp.questions.model.entity.Category;
import com.quizapp.questions.model.entity.Question;
import com.quizapp.questions.repository.CategoryRepository;
import com.quizapp.questions.repository.QuestionRepository;
import com.quizapp.questions.service.interfaces.CategoryService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceImplTest {

    @Mock
    private QuestionRepository mockQuestionRepo;

    @Mock
    private CategoryService mockCategoryService;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private Question question;

    @BeforeEach
    void setUp() {
        Category category = Category.builder()
                .id(1L)
                .name("Maths")
                .description("Description")
                .build();

        this.question = Question.builder()
                .id(1L)
                .category(category)
                .questionText("Question")
                .correctAnswer("A")
                .options(List.of("A", "B", "C", "D"))
                .build();
    }

    @Test
    void getAllQuestions_ShouldReturnMappedQuestionDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Question> page = new PageImpl<>(List.of(this.question), pageable, 1);

        when(this.mockQuestionRepo.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        QuestionPageDTO pageDTO = this.questionService.getAllQuestions("Question", 1L, pageable);

        Assertions.assertNotNull(pageDTO);
        Assertions.assertFalse(pageDTO.getQuestions().isEmpty());
        Assertions.assertEquals(1, pageDTO.getQuestions().size());
        Assertions.assertEquals(1L, pageDTO.getQuestions().get(0).getId());
        Assertions.assertEquals(1L, pageDTO.getQuestions().get(0).getCategoryId());
        Assertions.assertEquals("Maths", pageDTO.getQuestions().get(0).getCategoryName());
        Assertions.assertEquals("Question", pageDTO.getQuestions().get(0).getQuestionText());
        Assertions.assertEquals("A", pageDTO.getQuestions().get(0).getCorrectAnswer());
        Assertions.assertNotNull(pageDTO.getQuestions().get(0).getOptions());
        Assertions.assertEquals(0, pageDTO.getCurrentPage());
        Assertions.assertEquals(1, pageDTO.getTotalPages());
        Assertions.assertEquals(1L, pageDTO.getTotalElements());
        Assertions.assertEquals(10, pageDTO.getSize());
    }

    @Test
    void getAllQuestions_ShouldReturnEmptyPage_WhenQuestionsNotFound() {
        Pageable pageable = PageRequest.of(0, 10);

        when(this.mockQuestionRepo.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty());

        QuestionPageDTO pageDTO = this.questionService.getAllQuestions("Question", 5L, pageable);

        Assertions.assertNotNull(pageDTO);
        Assertions.assertTrue(pageDTO.getQuestions().isEmpty());
        Assertions.assertEquals(0, pageDTO.getCurrentPage());
        Assertions.assertEquals(1, pageDTO.getTotalPages());
        Assertions.assertEquals(0L, pageDTO.getTotalElements());
        Assertions.assertEquals(0, pageDTO.getSize());
    }
}