package com.quizapp.questions.service;

import com.quizapp.questions.repository.CategoryRepository;
import com.quizapp.questions.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    
}