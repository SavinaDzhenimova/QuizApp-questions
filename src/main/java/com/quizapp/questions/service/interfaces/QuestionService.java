package com.quizapp.questions.service.interfaces;

import com.quizapp.questions.model.enums.ApiStatus;
import com.quizapp.questions.model.dto.AddQuestionDTO;
import com.quizapp.questions.model.dto.QuestionDTO;
import com.quizapp.questions.model.dto.UpdateQuestionDTO;
import com.quizapp.questions.model.entity.Question;

import java.util.List;

public interface QuestionService {

    Question addQuestion(AddQuestionDTO addQuestionDTO);

    List<QuestionDTO> getAllQuestions();

    QuestionDTO getQuestionById(Long id);

    ApiStatus updateQuestion(Long id, UpdateQuestionDTO updateQuestionDTO);

    boolean deleteQuestionById(Long id);

    List<QuestionDTO> getQuestionsByCategory(Long categoryId);
}