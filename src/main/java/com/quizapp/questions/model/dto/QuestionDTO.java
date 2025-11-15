package com.quizapp.questions.model.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {

    private Long id;

    private String questionText;

    private Long categoryId;

    private String categoryName;

    private String correctAnswer;

    private List<String> options;
}