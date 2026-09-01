package com.gearpc.catalog.controller;

import com.gearpc.catalog.application.dto.request.CreateProductRequest;
import com.gearpc.catalog.application.dto.response.CreateProductResponse;
import com.gearpc.catalog.application.service.ProductService;
import com.gearpc.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponse<CreateProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        CreateProductResponse createProductResponse = productService.createProduct(request);
        return ApiResponse.created(2500, "Sản phẩm được tạo thành công!", createProductResponse);
    }

}
