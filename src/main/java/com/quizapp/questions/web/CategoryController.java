package com.quizapp.questions.web;

import com.quizapp.questions.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
}
