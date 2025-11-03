package com.quizapp.questions.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCategoryDTO {

    @NotBlank(message = "Въведете име на категорията!")
    private String name;

    @NotBlank(message = "Въведете описание за категорията!")
    private String description;
}