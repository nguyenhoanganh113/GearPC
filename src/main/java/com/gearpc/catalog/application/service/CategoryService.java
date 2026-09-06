package com.gearpc.catalog.application.service;

import com.gearpc.catalog.application.dto.request.CreateCategoryRequest;
import com.gearpc.catalog.application.dto.request.UpdateCategoryRequest;
import com.gearpc.catalog.application.dto.response.*;
import com.gearpc.common.dto.PaginationResponse;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CreateCategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);

    PaginationResponse<DetailCategoryResponse> searchCategoriesForAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            Pageable pageable
    );

    List<CategoryOptionResponse> getActiveCategoryOptions();

    DetailCategoryResponse getCategory(@NonNull UUID id);

    UpdateCategoryResponse updateCategory(@NonNull UUID id, UpdateCategoryRequest updateCategoryRequest);

    UpdateCategoryResponse updateCategoryStatus(@NonNull UUID id, Boolean active);

    void deleteCategory(@NonNull UUID id);

}
