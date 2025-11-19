package com.quizapp.questions.exception;

public class QuestionNotFoundException extends RuntimeException {

    public QuestionNotFoundException(Long id) {
        super("Въпрос с id " + id + " не е намерен.");
    }
}