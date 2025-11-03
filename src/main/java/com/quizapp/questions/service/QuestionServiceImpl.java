package com.quizapp.questions.service;

import com.quizapp.questions.model.dto.AddQuestionDTO;
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

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    private final CategoryService categoryService;

    @Override
    public Question addQuestion(AddQuestionDTO addQuestionDTO) {

        if (addQuestionDTO == null) {
            return null;
        }

        Optional<Category> optionalCategory = this.categoryService
                .findCategoryByName(addQuestionDTO.getCategory());

        if (optionalCategory.isEmpty()) {
            return null;
        }

        List<String> options = this.parseOptions(addQuestionDTO.getOptions());

        Question question = Question.builder()
                .questionText(addQuestionDTO.getQuestionText())
                .correctAnswer(addQuestionDTO.getCorrectAnswer())
                .category(optionalCategory.get())
                .options(options)
                .build();

        return this.questionRepository.saveAndFlush(question);
    }

    private List<String> parseOptions(String options) {
        if (options == null || options.isBlank()) {
            return new ArrayList<>();
        }

        return new ArrayList<>(
                Arrays.stream(options.split(","))
                        .map(String::trim)
                        .filter(opt -> !opt.isEmpty())
                        .toList()
        );
    }
}