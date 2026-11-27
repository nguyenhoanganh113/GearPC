package com.gearpc.controller;

import com.gearpc.dto.response.DetailProductResponse;
import com.gearpc.service.ProductService;
import com.gearpc.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cache-demo/products")
@RequiredArgsConstructor
public class ProductCacheDemoController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ApiResponse<DetailProductResponse> getProductById(@PathVariable UUID id) {
        return ApiResponse.success(productService.getProduct(id));
    }

}
