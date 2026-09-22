package com.gearpc.catalog.controller;

import com.gearpc.catalog.application.dto.request.AssignCategoryAttributeRequest;
import com.gearpc.catalog.application.dto.response.CategoryAttributeResponse;
import com.gearpc.catalog.application.service.CategoryAttributeService;
import com.gearpc.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/categories")
public class CategoryAttributeController {

    private final CategoryAttributeService categoryAttributeService;

    @PostMapping("/{categoryId}/attributes")
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

}
