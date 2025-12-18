package com.quizapp.questions.model.dto.question;

import com.quizapp.questions.model.dto.PageDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class QuestionPageDTO extends PageDTO {

    private List<QuestionDTO> questions;
}