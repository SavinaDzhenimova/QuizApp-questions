package com.quizapp.questions.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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