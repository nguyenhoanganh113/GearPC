package com.gearpc.catalog.controller;

import com.gearpc.catalog.application.dto.request.CreateProductRequest;
import com.gearpc.catalog.application.dto.request.ProductSearchRequest;
import com.gearpc.catalog.application.dto.request.UpdateProductRequest;
import com.gearpc.catalog.application.dto.request.UpdateProductStatusRequest;
import com.gearpc.catalog.application.dto.response.CreateProductResponse;
import com.gearpc.catalog.application.dto.response.DetailProductResponse;
import com.gearpc.catalog.application.dto.response.UpdateProductResponse;
import com.gearpc.catalog.application.service.ProductService;
import com.gearpc.common.dto.ApiResponse;
import com.gearpc.common.dto.PaginationResponse;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponse<CreateProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        CreateProductResponse createProductResponse = productService.createProduct(request);
        return ApiResponse.created(createProductResponse);
    }

    @GetMapping("/{id}")
    public ApiResponse<DetailProductResponse> getProductById(@NonNull @PathVariable UUID id) {
        DetailProductResponse response = productService.getProduct(id);
        return ApiResponse.success(response);
    }

    @GetMapping("/search")
    public ApiResponse<PaginationResponse<DetailProductResponse>> searchProductsForAdmin(
            @PageableDefault(
                    size = 15,
                    sort = {"price"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable,
            @Valid ProductSearchRequest productSearchRequest
    ) {
        PaginationResponse<DetailProductResponse> products =
                productService.searchProductsForAdmin(productSearchRequest, pageable);
        return ApiResponse.success(products);
    }

    @PutMapping("/{id}")
    public ApiResponse<UpdateProductResponse> updateProduct(
            @NonNull @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        UpdateProductResponse response = productService.updateProduct(id, request);
        return ApiResponse.success(response);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UpdateProductResponse> updateProductStatus(
            @NonNull @PathVariable UUID id,
            @Valid @RequestBody UpdateProductStatusRequest request
    ) {
        UpdateProductResponse response = productService.updateProductStatus(id, request);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@NonNull @PathVariable UUID id) {
        productService.deleteProduct(id);
        return ApiResponse.success();
    }

}
