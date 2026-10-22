package com.gearpc.catalog.controller;

import com.gearpc.catalog.application.dto.request.AssignCategoryAttributeRequest;
import com.gearpc.catalog.application.dto.request.UpdateCategoryAttributeRequest;
import com.gearpc.catalog.application.dto.response.CategoryAttributeResponse;
import com.gearpc.catalog.application.service.CategoryAttributeService;
import com.gearpc.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/categories")
public class CategoryAttributeController {

    private final CategoryAttributeService categoryAttributeService;

    @PostMapping("/{categoryId}/attributes")
    @PreAuthorize("hasAuthority('PERM_UPDATE_CATEGORY')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryAttributeResponse> assignAttribute(
            @PathVariable UUID categoryId,
            @Valid @RequestBody AssignCategoryAttributeRequest request
    ) {

        CategoryAttributeResponse response =
                categoryAttributeService.assignAttribute(
                        categoryId,
                        request.attributeDefinitionId(),
                        request.required()
                );

        return ApiResponse.created(response);
    }

    @GetMapping("/{categoryId}/attributes")
    @PreAuthorize("hasAuthority('PERM_READ_CATEGORY')")
    public ApiResponse<List<CategoryAttributeResponse>> getCategoryAttributes(
            @PathVariable UUID categoryId
    ) {
        return ApiResponse.success(categoryAttributeService.getCategoryAttributes(categoryId));
    }

    @PatchMapping("/{categoryId}/attributes/{attributeDefinitionId}")
    @PreAuthorize("hasAuthority('PERM_UPDATE_CATEGORY')")
    public ApiResponse<CategoryAttributeResponse> updateRequired(
            @PathVariable UUID categoryId,
            @PathVariable UUID attributeDefinitionId,
            @Valid @RequestBody UpdateCategoryAttributeRequest request
    ) {
        return ApiResponse.success(
                categoryAttributeService.updateRequired(
                        categoryId,
                        attributeDefinitionId,
                        request.required()
                )
        );
    }

    @DeleteMapping("/{categoryId}/attributes/{attributeDefinitionId}")
    @PreAuthorize("hasAuthority('PERM_UPDATE_CATEGORY')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> removeAttribute(
            @PathVariable UUID categoryId,
            @PathVariable UUID attributeDefinitionId
    ) {
        categoryAttributeService.removeAttribute(categoryId, attributeDefinitionId);
        return ApiResponse.success();
    }
}
