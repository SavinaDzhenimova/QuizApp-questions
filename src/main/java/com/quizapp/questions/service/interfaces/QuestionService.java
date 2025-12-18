package com.quizapp.questions.service.interfaces;

import com.quizapp.questions.model.dto.question.QuestionPageDTO;
import com.quizapp.questions.model.dto.question.AddQuestionDTO;
import com.quizapp.questions.model.dto.question.QuestionDTO;
import com.quizapp.questions.model.dto.question.UpdateQuestionDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {

    void addQuestion(AddQuestionDTO addQuestionDTO);

    QuestionPageDTO getAllQuestions(String questionText, Long categoryId, Pageable pageable);

    QuestionDTO getQuestionById(Long id);

    void updateQuestion(Long id, UpdateQuestionDTO updateQuestionDTO);

    List<QuestionDTO> getQuestionsByCategory(Long categoryId);
}