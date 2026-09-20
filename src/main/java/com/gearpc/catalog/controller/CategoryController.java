package com.gearpc.catalog.controller;

import com.gearpc.catalog.application.dto.request.CreateCategoryRequest;
import com.gearpc.catalog.application.dto.request.UpdateCategoryRequest;
import com.gearpc.catalog.application.dto.response.*;
import com.gearpc.catalog.application.service.CategoryService;
import com.gearpc.common.dto.ApiResponse;
import com.gearpc.common.dto.PaginationResponse;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateCategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CreateCategoryResponse response = categoryService.createCategory(request);
        return ApiResponse.created(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<DetailCategoryResponse> getCategory(@PathVariable UUID id) {
        DetailCategoryResponse category = categoryService.getCategory(id);
        return ApiResponse.success(category);
    }

    @GetMapping("/search")
    public ApiResponse<PaginationResponse<DetailCategoryResponse>> searchCategoriesForAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(
                    size = 15,
                    sort = {"createdAt", "id"}, // tier-breaker
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        PaginationResponse<DetailCategoryResponse> categoryPaginationResponse =
                categoryService.searchCategoriesForAdmin(keyword, active, pageable);
        return ApiResponse.success(categoryPaginationResponse);
    }

    @GetMapping("/options")
    public ApiResponse<List<CategoryOptionResponse>> getActiveCategoryOptions() {
        List<CategoryOptionResponse> options = categoryService.getActiveCategoryOptions();
        return ApiResponse.success(options);
    }

    @PutMapping("/{id}")
    public ApiResponse<UpdateCategoryResponse> updateCategory(
            @NonNull @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        UpdateCategoryResponse response = categoryService.updateCategory(id, request);
        return ApiResponse.success(response);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UpdateCategoryResponse> updateCategoryStatus(
            @NonNull @PathVariable UUID id,
            @RequestParam Boolean active
    ) {
        UpdateCategoryResponse response = categoryService.updateCategoryStatus(id, active);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteCategory(@NonNull @PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success();
    }

}
