package com.quizapp.questions.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail, HttpServletRequest request,
                                             String code) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("urn:problem:" + code.toLowerCase()));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("code", code);
        return problem;
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ProblemDetail handleCategoryNotFound(CategoryNotFoundException ex, HttpServletRequest request) {

        return buildProblemDetail(HttpStatus.NOT_FOUND, "Category not found", ex.getMessage(), request,
                "CATEGORY_NOT_FOUND");
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ProblemDetail handleQuestionNotFound(QuestionNotFoundException  ex, HttpServletRequest request) {

        return buildProblemDetail(HttpStatus.NOT_FOUND, "Question not found", ex.getMessage(), request,
                "QUESTION_NOT_FOUND");
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ProblemDetail handleInvalidRequest(InvalidRequestException ex, HttpServletRequest request) {

        return buildProblemDetail(HttpStatus.BAD_REQUEST, "Invalid request", ex.getMessage(), request,
                "INVALID_REQUEST");
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex, HttpServletRequest request) {

        return buildProblemDetail(HttpStatus.CONFLICT, "Resource conflict", ex.getMessage(), request,
                "CONFLICT");
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {

        return buildProblemDetail(HttpStatus.CONFLICT, "Duplicate resource", ex.getMessage(), request,
                "DUPLICATE_RESOURCE");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, HttpServletRequest request) {

        return buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", ex.getMessage(),
                request, "INTERNAL_SERVER_ERROR");
    }
}