package com.gearpc.catalog.controller;

import com.gearpc.catalog.application.dto.request.UpdateProductAttributeValuesRequest;
import com.gearpc.catalog.application.dto.response.ProductAttributeValueResponse;
import com.gearpc.catalog.application.service.ProductAttributeValueService;
import com.gearpc.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/products")
public class ProductAttributeValueController {

    private final ProductAttributeValueService productAttributeValueService;

    @GetMapping("/{productId}/attributes")
    public ApiResponse<List<ProductAttributeValueResponse>> getProductAttributeValues(
            @PathVariable UUID productId
    ) {
        return ApiResponse.success(productAttributeValueService.getProductAttributeValues(productId));
    }

    @PutMapping("/{productId}/attributes")
    public ApiResponse<List<ProductAttributeValueResponse>> syncProductAttributeValues(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductAttributeValuesRequest request
    ) {
        return ApiResponse.success(
                productAttributeValueService.syncProductAttributeValues(productId, request)
        );
    }
}
