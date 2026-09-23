package com.gearpc.catalog.controller;

import com.gearpc.catalog.application.dto.request.CreateAttributeDefinitionRequest;
import com.gearpc.catalog.application.dto.request.UpdateAttributeDefinitionRequest;
import com.gearpc.catalog.application.dto.response.AttributeDefinitionOptionResponse;
import com.gearpc.catalog.application.dto.response.AttributeDefinitionResponse;
import com.gearpc.catalog.application.service.AttributeDefinitionService;
import com.gearpc.catalog.domain.valueobject.enums.AttributeDataType;
import com.gearpc.common.dto.ApiResponse;
import com.gearpc.common.dto.PaginationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/attribute-definitions")
public class AttributeDefinitionController {

    private final AttributeDefinitionService attributeDefinitionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AttributeDefinitionResponse> createAttributeDefinition(
            @Valid @RequestBody CreateAttributeDefinitionRequest request
    ) {
        return ApiResponse.created(attributeDefinitionService.createAttributeDefinition(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<AttributeDefinitionResponse> getAttributeDefinition(@PathVariable UUID id) {
        return ApiResponse.success(attributeDefinitionService.getAttributeDefinition(id));
    }

    @GetMapping("/search")
    public ApiResponse<PaginationResponse<AttributeDefinitionResponse>> searchAttributeDefinitions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) AttributeDataType dataType,
            @PageableDefault(size = 15, sort = {"createdAt", "id"}, direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.success(
                attributeDefinitionService.searchAttributeDefinitions(keyword, active, dataType, pageable)
        );
    }

    @GetMapping("/options")
    public ApiResponse<List<AttributeDefinitionOptionResponse>> getActiveAttributeDefinitionOptions() {
        return ApiResponse.success(attributeDefinitionService.getActiveAttributeDefinitionOptions());
    }

    @PutMapping("/{id}")
    public ApiResponse<AttributeDefinitionResponse> updateAttributeDefinition(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAttributeDefinitionRequest request
    ) {
        return ApiResponse.success(attributeDefinitionService.updateAttributeDefinition(id, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<AttributeDefinitionResponse> updateAttributeDefinitionStatus(
            @PathVariable UUID id,
            @RequestParam Boolean active
    ) {
        return ApiResponse.success(attributeDefinitionService.updateAttributeDefinitionStatus(id, active));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteAttributeDefinition(@PathVariable UUID id) {
        attributeDefinitionService.deleteAttributeDefinition(id);
        return ApiResponse.success();
    }
}
