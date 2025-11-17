package com.quizapp.questions.service;

import com.quizapp.questions.model.dto.AddQuestionDTO;
import com.quizapp.questions.model.dto.QuestionDTO;
import com.quizapp.questions.model.dto.UpdateQuestionDTO;
import com.quizapp.questions.model.entity.Category;
import com.quizapp.questions.model.entity.Question;
import com.quizapp.questions.repository.QuestionRepository;
import com.quizapp.questions.service.interfaces.CategoryService;
import com.quizapp.questions.service.interfaces.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    private final CategoryService categoryService;

    @Override
    public List<QuestionDTO> getAllQuestions() {
        return this.questionRepository.findAll()
                .stream()
                .map(this::questionToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionDTO getQuestionById(Long id) {
        return this.questionRepository.findById(id)
                .map(this::questionToDTO)
                .orElse(null);
    }

    @Override
    public List<QuestionDTO> getQuestionsByCategory(Long categoryId) {
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
    public Question addQuestion(AddQuestionDTO addQuestionDTO) {

        if (addQuestionDTO == null) {
            throw new RuntimeException("Не е намерен въпрос!");
        }

        Optional<Category> optionalCategory = this.categoryService
                .findCategoryById(addQuestionDTO.getCategoryId());

        if (optionalCategory.isEmpty()) {
            return null;
        }

        Question question = Question.builder()
                .questionText(addQuestionDTO.getQuestionText())
                .correctAnswer(addQuestionDTO.getCorrectAnswer())
                .category(optionalCategory.get())
                .options(this.parseOptions(addQuestionDTO.getOptions()))
                .build();

        return this.questionRepository.saveAndFlush(question);
    }

    @Override
    public QuestionDTO updateQuestion(Long id, UpdateQuestionDTO updateQuestionDTO) {
        Optional<Question> optionalQuestion = this.questionRepository.findById(id);

        if (optionalQuestion.isEmpty()) {
            return null;
        }

        Question question = optionalQuestion.get();

        question.setQuestionText(updateQuestionDTO.getQuestionText());
        question.setCorrectAnswer(updateQuestionDTO.getCorrectAnswer());
        question.setOptions(this.parseOptions(updateQuestionDTO.getOptions()));

        Question updatedQuestion = this.questionRepository.saveAndFlush(question);

        return this.questionToDTO(updatedQuestion);
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

    @Override
    public boolean deleteQuestionById(Long id) {
        if (!this.questionRepository.existsById(id)) {
            return false;
        }

        this.questionRepository.deleteById(id);
        return true;
    }
}