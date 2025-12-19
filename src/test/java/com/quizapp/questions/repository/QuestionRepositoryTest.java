package com.quizapp.questions.repository;

import com.quizapp.questions.model.entity.Category;
import com.quizapp.questions.model.entity.Question;
import com.quizapp.questions.repository.spec.QuestionSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class QuestionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestionRepository questionRepository;

    private Category category;
    private Question question;

    @BeforeEach
    void setUp() {
        this.category = Category.builder()
                .name("Maths")
                .description("Description")
                .build();
        this.category = entityManager.persistFlushFind(this.category);

        this.question = Question.builder()
                .category(this.category)
                .questionText("Question")
                .correctAnswer("A")
                .options(List.of("A", "B", "C", "D"))
                .build();
        this.question = entityManager.persistFlushFind(this.question);
    }

    @Test
    void findAll_ShouldReturnQuestionsWithSpecificationAndPageable_WhenQuestionsFound() {
        Specification<Question> spec = Specification
                .allOf(QuestionSpecifications.hasCategory(this.question.getCategory().getId()))
                .and(QuestionSpecifications.hasText("Question"));
        Pageable pageable = PageRequest.of(0, 10);

        Page<Question> page = this.questionRepository.findAll(spec, pageable);

        assertThat(page).isNotEmpty();
        assertThat(page.getContent().get(0).getQuestionText()).isEqualTo("Question");
        assertThat(page.getContent().get(0).getCorrectAnswer()).isEqualTo("A");
        assertThat(page.getContent().get(0).getOptions()).containsExactly("A", "B", "C", "D");
        assertThat(page.getContent().get(0).getCategory().getName()).isEqualTo("Maths");
        assertThat(page.getContent().get(0).getCategory().getDescription()).isEqualTo("Description");
    }

    @Test
    void findAll_ShouldReturnEmpty_WhenQuestionsNotFound() {
        Specification<Question> spec = Specification
                .allOf(QuestionSpecifications.hasCategory(this.question.getCategory().getId()))
                .and(QuestionSpecifications.hasText("Missing"));
        Pageable pageable = PageRequest.of(0, 10);

        Page<Question> page = this.questionRepository.findAll(spec, pageable);

        assertThat(page).isEmpty();
    }

    @Test
    void findByCategoryId_ShouldReturnQuestions_WhenCategoryFound() {
        List<Question> questions = this.questionRepository.findByCategoryId(this.category.getId());

        assertThat(questions).isNotEmpty();
        assertThat(questions.get(0).getQuestionText()).isEqualTo("Question");
        assertThat(questions.get(0).getCorrectAnswer()).isEqualTo("A");
        assertThat(questions.get(0).getOptions()).containsExactly("A", "B", "C", "D");
        assertThat(questions.get(0).getCategory().getName()).isEqualTo("Maths");
        assertThat(questions.get(0).getCategory().getDescription()).isEqualTo("Description");
    }

    @Test
    void findByCategoryId_ShouldReturnEmptyQuestions_WhenCategoryNotFound() {
        List<Question> questions = this.questionRepository.findByCategoryId(999L);

        assertThat(questions).isEmpty();
    }
}