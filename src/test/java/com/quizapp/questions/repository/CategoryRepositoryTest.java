package com.quizapp.questions.repository;

import com.quizapp.questions.model.entity.Category;
import com.quizapp.questions.repository.spec.CategorySpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        this.category = Category.builder()
                .name("Maths")
                .description("Description")
                .questions(new ArrayList<>())
                .build();

        this.categoryRepository.save(category);
    }

    @Test
    void findAll_ShouldReturnEmptyPage_WhenCategoriesNotFound() {
        Specification<Category> spec = Specification
                .allOf(CategorySpecifications.hasName("Music"));
        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> page = this.categoryRepository.findAll(spec, pageable);

        assertThat(page).isEmpty();
    }

    @Test
    void findAll_ShouldReturnCategoriesWithSpecificationAndPageable_WhenCategoriesFound() {
        Specification<Category> spec = Specification
                .allOf(CategorySpecifications.hasName(this.category.getName()));
        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> page = this.categoryRepository.findAll(spec, pageable);

        assertThat(page).isNotEmpty();
        assertThat(page.getContent().get(0).getName()).isEqualTo("Maths");
        assertThat(page.getContent().get(0).getDescription()).isEqualTo("Description");
    }

    @Test
    void findByName_ShouldReturnCategory_WhenCategoryFound() {
        Optional<Category> optionalCategory = this.categoryRepository.findByName(this.category.getName());

        assertThat(optionalCategory).isNotEmpty();
        assertThat(optionalCategory.get().getName()).isEqualTo("Maths");
    }

    @Test
    void findByName_ShouldReturnEmpty_WhenCategoryNotFound() {
        Optional<Category> optionalCategory = this.categoryRepository.findByName("Music");

        assertThat(optionalCategory).isEmpty();
    }
}