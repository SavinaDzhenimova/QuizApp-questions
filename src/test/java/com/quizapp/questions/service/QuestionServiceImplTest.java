package com.quizapp.questions.service;

import com.quizapp.questions.exception.CategoryNotFoundException;
import com.quizapp.questions.exception.NoChangesException;
import com.quizapp.questions.exception.QuestionNotFoundException;
import com.quizapp.questions.model.dto.question.AddQuestionDTO;
import com.quizapp.questions.model.dto.question.QuestionDTO;
import com.quizapp.questions.model.dto.question.QuestionPageDTO;
import com.quizapp.questions.model.dto.question.UpdateQuestionDTO;
import com.quizapp.questions.model.entity.Category;
import com.quizapp.questions.model.entity.Question;
import com.quizapp.questions.repository.QuestionRepository;
import com.quizapp.questions.service.interfaces.CategoryService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
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
public class QuestionServiceImplTest {

    @Mock
    private QuestionRepository mockQuestionRepo;

    @Mock
    private CategoryService mockCategoryService;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private Category createCategory() {
        return Category.builder()
                .id(1L)
                .name("Maths")
                .description("Description")
                .build();
    }

    private Question createQuestion() {
        return Question.builder()
                .id(1L)
                .category(createCategory())
                .questionText("Question")
                .correctAnswer("A")
                .options(new ArrayList<>(List.of("A", "B", "C", "D")))
                .build();
    }

    private AddQuestionDTO createAddQuestionDTO(Long categoryId) {
        return AddQuestionDTO.builder()
                .categoryId(categoryId)
                .questionText("Question")
                .correctAnswer("B")
                .options("A, B, C, D")
                .build();
    }

    private UpdateQuestionDTO createUpdateDTO(String text, String answer, String options) {
        return UpdateQuestionDTO.builder()
                .categoryName("Maths")
                .questionText(text)
                .correctAnswer(answer)
                .options(options)
                .build();
    }

    @Test
    void getAllQuestions_ShouldReturnMappedQuestionDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Question> page = new PageImpl<>(List.of(this.createQuestion()), pageable, 1);

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

    @Test
    void getCategoryById_ShouldReturnCategoryDTO_WhenCategoryFound() {
        when(this.mockQuestionRepo.findById(1L))
                .thenReturn(Optional.of(this.createQuestion()));

        QuestionDTO result = this.questionService.getQuestionById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(1L, result.getCategoryId());
        Assertions.assertEquals("Maths", result.getCategoryName());
        Assertions.assertEquals("Question", result.getQuestionText());
        Assertions.assertEquals("A", result.getCorrectAnswer());
        Assertions.assertNotNull(result.getOptions());
        Assertions.assertEquals(List.of("A", "B", "C", "D"), result.getOptions());
    }

    @Test
    void getQuestionById_ShouldThrowException_WhenQuestionNotFound() {
        when(this.mockQuestionRepo.findById(5L))
                .thenReturn(Optional.empty());

        QuestionNotFoundException exception = Assertions.assertThrows(QuestionNotFoundException.class,
                () -> this.questionService.getQuestionById(5L));

        Assertions.assertEquals("Въпрос с id 5 не е намерен.", exception.getMessage());
    }

    @Test
    void getQuestionsByCategory_ShouldThrowException_WhenCategoryNotFound() {
        when(this.mockCategoryService.findCategoryById(5L))
                .thenReturn(Optional.empty());

        CategoryNotFoundException exception = Assertions.assertThrows(CategoryNotFoundException.class,
                () -> this.questionService.getQuestionsByCategory(5L));

        Assertions.assertEquals("Категория с id 5 не е намерена.", exception.getMessage());
    }

    @Test
    void getQuestionsByCategory_ShouldReturnMappedQuestionDTOs_WhenCategoryFound() {
        when(this.mockCategoryService.findCategoryById(1L))
                .thenReturn(Optional.of(this.createCategory()));
        when(this.mockQuestionRepo.findByCategoryId(1L))
                .thenReturn(List.of(this.createQuestion()));

        List<QuestionDTO> result = this.questionService.getQuestionsByCategory(1L);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(1L, result.get(0).getCategoryId());
        Assertions.assertEquals("Maths", result.get(0).getCategoryName());
        Assertions.assertEquals("Question", result.get(0).getQuestionText());
        Assertions.assertEquals("A", result.get(0).getCorrectAnswer());
        Assertions.assertNotNull(result.get(0).getOptions());
        Assertions.assertEquals(List.of("A", "B", "C", "D"), result.get(0).getOptions());
    }

    @Test
    void addQuestion_ShouldThrowException_WhenInputDataIsNotValid() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> this.questionService.addQuestion(null));

        Assertions.assertEquals("Невалидни входни данни.", exception.getMessage());
        verifyNoInteractions(this.mockCategoryService);
        verifyNoInteractions(this.mockQuestionRepo);
    }

    @Test
    void addQuestion_ShouldThrowException_WhenCategoryNotFound() {
        AddQuestionDTO addQuestionDTO = this.createAddQuestionDTO(5L);

        when(this.mockCategoryService.findCategoryById(5L))
                .thenReturn(Optional.empty());

        CategoryNotFoundException exception = Assertions.assertThrows(CategoryNotFoundException.class,
                () -> this.questionService.addQuestion(addQuestionDTO));

        Assertions.assertEquals("Категория с id 5 не е намерена.", exception.getMessage());
        verify(this.mockCategoryService, times(1)).findCategoryById(5L);
        verifyNoInteractions(this.mockQuestionRepo);
    }

    @Test
    void addQuestion_ShouldSaveQuestion_WhenCategoryFoundAndInputDataIsValid() {
        AddQuestionDTO addQuestionDTO = this.createAddQuestionDTO(1L);

        when(this.mockCategoryService.findCategoryById(1L))
                .thenReturn(Optional.of(this.createCategory()));

        this.questionService.addQuestion(addQuestionDTO);

        verify(this.mockCategoryService, times(1)).findCategoryById(1L);
        verify(this.mockQuestionRepo, times(1)).saveAndFlush(any(Question.class));
    }

    @Test
    void updateQuestion_ShouldThrowException_WhenQuestionNotFound() {
        UpdateQuestionDTO updateQuestionDTO = this.createUpdateDTO("Question", "B", "A, B, C, D");

        when(this.mockQuestionRepo.findById(5L))
                .thenReturn(Optional.empty());

        QuestionNotFoundException exception = Assertions.assertThrows(QuestionNotFoundException.class,
                () -> this.questionService.updateQuestion(5L, updateQuestionDTO));

        Assertions.assertEquals("Въпрос с id 5 не е намерен.", exception.getMessage());
        verify(this.mockQuestionRepo, times(1)).findById(5L);
        verify(this.mockQuestionRepo, never()).saveAndFlush(any(Question.class));
    }

    @Test
    void updateQuestion_ShouldThrowException_WhenNoChanges() {
        UpdateQuestionDTO updateQuestionDTO = UpdateQuestionDTO.builder()
                .categoryName("Maths")
                .questionText("Question")
                .correctAnswer("A")
                .options("A, B, C, D")
                .build();

        when(this.mockQuestionRepo.findById(1L))
                .thenReturn(Optional.of(this.createQuestion()));

        NoChangesException exception = Assertions.assertThrows(NoChangesException.class,
                () -> this.questionService.updateQuestion(1L, updateQuestionDTO));

        Assertions.assertEquals("Няма промени за запазване.", exception.getMessage());
        verify(this.mockQuestionRepo, times(1)).findById(1L);
        verify(this.mockQuestionRepo, never()).saveAndFlush(any(Question.class));
    }

    @Test
    void updateQuestion_ShouldUpdateQuestion_WhenChangesFound() {
        Question question = this.createQuestion();

        UpdateQuestionDTO updateQuestionDTO = this.createUpdateDTO("Question 1","B", "A, B, C, E");

        when(this.mockQuestionRepo.findById(1L))
                .thenReturn(Optional.of(question));

        this.questionService.updateQuestion(1L, updateQuestionDTO);

        Assertions.assertEquals("Question 1", question.getQuestionText());
        Assertions.assertEquals("B", question.getCorrectAnswer());
        Assertions.assertEquals(List.of("A", "B", "C", "E"), question.getOptions());
        verify(this.mockQuestionRepo, times(1)).findById(1L);
        verify(this.mockQuestionRepo, times(1)).saveAndFlush(any(Question.class));
    }
}