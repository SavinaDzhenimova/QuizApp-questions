package com.quizapp.questions.service.interfaces;

import com.quizapp.questions.model.dto.QuestionPageDTO;
import com.quizapp.questions.model.dto.AddQuestionDTO;
import com.quizapp.questions.model.dto.QuestionDTO;
import com.quizapp.questions.model.dto.UpdateQuestionDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {

    void addQuestion(AddQuestionDTO addQuestionDTO);

    QuestionPageDTO getAllQuestions(String questionText, Long categoryId, Pageable pageable);

    QuestionDTO getQuestionById(Long id);

    void updateQuestion(Long id, UpdateQuestionDTO updateQuestionDTO);

    void deleteQuestionById(Long id);

    List<QuestionDTO> getQuestionsByCategory(Long categoryId);
}