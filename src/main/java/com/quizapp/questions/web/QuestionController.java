package com.quizapp.questions.web;

import com.quizapp.questions.service.interfaces.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

}