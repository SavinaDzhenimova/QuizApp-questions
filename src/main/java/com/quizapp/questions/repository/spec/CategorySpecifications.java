package com.quizapp.questions.repository.spec;

import com.quizapp.questions.model.entity.Category;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecifications {

    public static Specification<Category> hasName(String name) {
        return (root, query, cb) ->
                (name == null || name.isBlank())
                        ? null
                        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}