package com.quizapp.questions.repository;

import com.quizapp.questions.model.dto.QuestionDTO;
import com.quizapp.questions.model.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByCategoryId(Long categoryId);
}