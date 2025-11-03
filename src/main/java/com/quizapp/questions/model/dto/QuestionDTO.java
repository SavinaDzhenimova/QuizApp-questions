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

    private String correctAnswer;

    private List<String> options;
}