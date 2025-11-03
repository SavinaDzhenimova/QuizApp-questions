package com.quizapp.questions.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddQuestionDTO {

    @NotBlank(message = "Въведете съдържание на въпроса!")
    private String questionText;

    @NotNull
    private String category;

    @NotBlank(message = "Въведете правилен отговор на въпроса!")
    private String correctAnswer;

    @NotBlank(message = "Въведете опционални отговори на въпроса!")
    private String options;
}