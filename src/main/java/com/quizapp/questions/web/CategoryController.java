package com.quizapp.questions.web;

import com.quizapp.questions.model.dto.*;
import com.quizapp.questions.service.interfaces.CategoryService;
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

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<CategoryPageDTO> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(required = false) String categoryName) {

        String decodedText = categoryName != null
                ? URLDecoder.decode(categoryName, StandardCharsets.UTF_8)
                : "";

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        CategoryPageDTO categoryPageDTO = this.categoryService.getAllCategories(decodedText, pageable);

        return ResponseEntity.ok(categoryPageDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO categoryDTO = this.categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryDTO);
    }

    @PostMapping
    public ResponseEntity<Void> addCategory(@RequestBody @Valid AddCategoryDTO addCategoryDTO) {
        this.categoryService.addCategory(addCategoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id,
                                            @RequestBody @Valid UpdateCategoryDTO updateCategoryDTO) {

        this.categoryService.updateCategory(id, updateCategoryDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        this.categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }
}