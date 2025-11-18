package com.quizapp.questions.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CategoryPageDTO extends PageDTO {

    private List<CategoryDTO> categories;
}