package com.quizapp.questions.web;

import com.quizapp.questions.model.dto.QuestionPageDTO;
import com.quizapp.questions.model.dto.AddQuestionDTO;
import com.quizapp.questions.model.dto.QuestionDTO;
import com.quizapp.questions.model.dto.UpdateQuestionDTO;
import com.quizapp.questions.service.interfaces.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<QuestionPageDTO> getAllQuestions(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(required = false) String questionText,
                                                           @RequestParam(required = false) Long categoryId) {

        String decodedText = questionText != null
                ? URLDecoder.decode(questionText, StandardCharsets.UTF_8)
                : "";

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        QuestionPageDTO questionPageDTO = questionService.getAllQuestions(decodedText, categoryId, pageable);

        return ResponseEntity.ok(questionPageDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long id) {
        QuestionDTO questionDTO = this.questionService.getQuestionById(id);
        return ResponseEntity.ok(questionDTO);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByCategory(@PathVariable Long categoryId) {
        List<QuestionDTO> questionDTOs = this.questionService.getQuestionsByCategory(categoryId);
        return ResponseEntity.ok(questionDTOs);
    }

    @PostMapping
    public ResponseEntity<Void> addQuestion(@RequestBody @Valid AddQuestionDTO addQuestionDTO) {
        this.questionService.addQuestion(addQuestionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateQuestion(@PathVariable Long id,
                                            @RequestBody @Valid UpdateQuestionDTO updateQuestionDTO) {
        this.questionService.updateQuestion(id, updateQuestionDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        this.questionService.deleteQuestionById(id);
        return ResponseEntity.noContent().build();
    }
}