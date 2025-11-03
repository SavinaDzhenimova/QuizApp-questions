package com.quizapp.questions.service.interfaces;

import com.quizapp.questions.model.dto.AddQuestionDTO;
import com.quizapp.questions.model.dto.QuestionDTO;
import com.quizapp.questions.model.entity.Question;

import java.util.List;

public interface QuestionService {

    Question addQuestion(AddQuestionDTO addQuestionDTO);

    List<QuestionDTO> getAllQuestions();

    QuestionDTO getQuestionById(Long id);

    boolean deleteQuestionById(Long id);
}