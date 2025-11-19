package com.quizapp.questions.service;

import com.quizapp.questions.exception.CategoryNotFoundException;
import com.quizapp.questions.exception.NoChangesException;
import com.quizapp.questions.exception.QuestionNotFoundException;
import com.quizapp.questions.model.dto.QuestionPageDTO;
import com.quizapp.questions.model.dto.AddQuestionDTO;
import com.quizapp.questions.model.dto.QuestionDTO;
import com.quizapp.questions.model.dto.UpdateQuestionDTO;
import com.quizapp.questions.model.entity.Category;
import com.quizapp.questions.model.entity.Question;
import com.quizapp.questions.repository.QuestionRepository;
import com.quizapp.questions.repository.spec.QuestionSpecifications;
import com.quizapp.questions.service.interfaces.CategoryService;
import com.quizapp.questions.service.interfaces.QuestionService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryService categoryService;

    @Override
    public QuestionPageDTO getAllQuestions(String questionText, Long categoryId, Pageable pageable) {

        Specification<Question> spec = Specification
                .allOf(QuestionSpecifications.hasText(questionText))
                .and(QuestionSpecifications.hasCategory(categoryId));

        Page<Question> questionsPage = this.questionRepository.findAll(spec, pageable);

        List<QuestionDTO> questionDTOs = questionsPage.getContent().stream()
                .map(this::questionToDTO)
                .toList();

        return QuestionPageDTO.builder()
                .questions(questionDTOs)
                .totalPages(questionsPage.getTotalPages())
                .totalElements(questionsPage.getTotalElements())
                .currentPage(questionsPage.getNumber())
                .size(questionsPage.getSize())
                .build();
    }

    @Override
    public QuestionDTO getQuestionById(Long id) {
        return this.questionRepository.findById(id)
                .map(this::questionToDTO)
                .orElseThrow(() -> new QuestionNotFoundException(id));
    }

    @Override
    public List<QuestionDTO> getQuestionsByCategory(Long categoryId) {

        this.categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        return this.questionRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::questionToDTO)
                .collect(Collectors.toList());
    }

    private QuestionDTO questionToDTO(Question question) {
        return QuestionDTO.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .categoryId(question.getCategory().getId())
                .categoryName(question.getCategory().getName())
                .correctAnswer(question.getCorrectAnswer())
                .options(question.getOptions())
                .build();
    }

    @Override
    public void addQuestion(AddQuestionDTO addQuestionDTO) {

        if (addQuestionDTO == null) {
            throw new ValidationException("Невалидни входни данни.");
        }

        Category category = this.categoryService
                .findCategoryById(addQuestionDTO.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(addQuestionDTO.getCategoryId()));

        Question question = Question.builder()
                .questionText(addQuestionDTO.getQuestionText())
                .correctAnswer(addQuestionDTO.getCorrectAnswer())
                .category(category)
                .options(this.parseOptions(addQuestionDTO.getOptions()))
                .build();

        this.questionRepository.saveAndFlush(question);
    }

    @Override
    public void updateQuestion(Long id, UpdateQuestionDTO updateQuestionDTO) {
        Question question = this.questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id));

        boolean changed = false;

        if (!question.getQuestionText().equals(updateQuestionDTO.getQuestionText())) {
            question.setQuestionText(updateQuestionDTO.getQuestionText());
            changed = true;
        }

        if (!question.getCorrectAnswer().equals(updateQuestionDTO.getCorrectAnswer())) {
            question.setCorrectAnswer(updateQuestionDTO.getCorrectAnswer());
            changed = true;
        }

        List<String> updatedOptions = this.parseOptions(updateQuestionDTO.getOptions());
        if (!new HashSet<>(question.getOptions()).equals(new HashSet<>(updatedOptions))) {
            question.setOptions(updatedOptions);
            changed = true;
        }

        if (!changed) {
            throw new NoChangesException("Няма промени за запазване.");
        }

        this.questionRepository.saveAndFlush(question);
    }

    private List<String> parseOptions(String options) {
        if (options == null || options.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(options.split("\\s*,\\s*"))
                .map(String::trim)
                .filter(opt -> !opt.isEmpty())
                .collect(Collectors.toList());
    }
}