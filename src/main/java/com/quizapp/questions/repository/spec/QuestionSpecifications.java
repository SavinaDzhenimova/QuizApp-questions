package com.quizapp.questions.repository.spec;

import com.quizapp.questions.model.entity.Question;
import org.springframework.data.jpa.domain.Specification;

public class QuestionSpecifications {

    public static Specification<Question> hasText(String text) {
        return (root, query, cb) ->
                text == null || text.isBlank()
                        ? null
                        : cb.like(cb.lower(root.get("questionText")), "%" + text.toLowerCase() + "%");
    }

    public static Specification<Question> hasCategory(Long categoryId) {
        return (root, query, cb) ->
                categoryId == null
                        ? null
                        : cb.equal(root.get("category").get("id"), categoryId);
    }
}