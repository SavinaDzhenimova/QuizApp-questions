package com.quizapp.questions.web;

import com.quizapp.questions.model.dto.*;
import com.quizapp.questions.model.enums.ApiStatus;
import com.quizapp.questions.service.interfaces.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> allCategories = this.categoryService.getAllCategories();
        return ResponseEntity.ok(allCategories);
    }

    @GetMapping
    public ResponseEntity<CategoryPageDTO> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        CategoryPageDTO categoryPageDTO = this.categoryService.getAllCategories(pageable);

        return ResponseEntity.ok(categoryPageDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        CategoryDTO categoryDTO = this.categoryService.getCategoryById(id);

        if (categoryDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Категория с ID " + id + " не е намерена."));
        }

        return ResponseEntity.ok(categoryDTO);
    }

    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody @Valid AddCategoryDTO addCategoryDTO) {
        ApiStatus apiStatus = this.categoryService.addCategory(addCategoryDTO);

        return switch (apiStatus) {
            case INVALID_REQUEST -> ResponseEntity.badRequest().build();
            case CREATED -> ResponseEntity.status(HttpStatus.CREATED).build();
            default -> ResponseEntity.internalServerError().build();
        };
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id,
                                            @RequestBody @Valid UpdateCategoryDTO updateCategoryDTO) {

        ApiStatus apiStatus = this.categoryService.updateCategory(id, updateCategoryDTO);

        return switch (apiStatus) {
            case NOT_FOUND -> ResponseEntity.notFound().build();
            case NO_CHANGES -> ResponseEntity.noContent().build();
            case UPDATED -> ResponseEntity.ok().build();
            default -> ResponseEntity.internalServerError().build();
        };
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        boolean isDeleted = this.categoryService.deleteCategoryById(id);

        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Категория с ID " + id + " не е намерена, за да бъде премахната."));
        }

        return ResponseEntity.noContent().build();
    }
}