package com.quizapp.questions.web;

import com.quizapp.questions.model.enums.ApiStatus;
import com.quizapp.questions.model.dto.AddQuestionDTO;
import com.quizapp.questions.model.dto.QuestionDTO;
import com.quizapp.questions.model.dto.UpdateQuestionDTO;
import com.quizapp.questions.model.entity.Question;
import com.quizapp.questions.service.interfaces.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        List<QuestionDTO> questionDTOs = this.questionService.getAllQuestions();
        return ResponseEntity.ok(questionDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        QuestionDTO questionDTO = this.questionService.getQuestionById(id);

        if (questionDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Въпросът с ID " + id + " не е намерен."));
        }

        return ResponseEntity.ok(questionDTO);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByCategory(@PathVariable Long categoryId) {
        List<QuestionDTO> questionDTOs = this.questionService.getQuestionsByCategory(categoryId);
        return ResponseEntity.ok(questionDTOs);
    }

    @PostMapping
    public ResponseEntity<?> addQuestion(@RequestBody @Valid AddQuestionDTO addQuestionDTO) {
        Question savedQuestion = questionService.addQuestion(addQuestionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedQuestion);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id,
                                            @RequestBody @Valid UpdateQuestionDTO updateQuestionDTO) {

        ApiStatus apiStatus = this.questionService.updateQuestion(id, updateQuestionDTO);

        return switch (apiStatus) {
            case NOT_FOUND -> ResponseEntity.notFound().build();
            case NO_CHANGES -> ResponseEntity.noContent().build();
            case UPDATED -> ResponseEntity.ok().build();
            default -> ResponseEntity.internalServerError().build();
        };
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        boolean isDeleted = this.questionService.deleteQuestionById(id);

        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Въпросът с ID " + id + " не е намерен, за да бъде премахнат."));
        }

        return ResponseEntity.noContent().build();
    }
}