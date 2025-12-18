package com.quizapp.questions.model.dto.category;

import com.quizapp.questions.model.dto.PageDTO;
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