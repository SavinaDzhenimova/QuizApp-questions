package com.quizapp.questions.service.interfaces;

import com.quizapp.questions.model.dto.AddQuestionDTO;
import com.quizapp.questions.model.entity.Question;

public interface QuestionService {
    Question addQuestion(AddQuestionDTO addQuestionDTO);
}
