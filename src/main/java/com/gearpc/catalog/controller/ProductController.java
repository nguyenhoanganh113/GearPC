package com.gearpc.catalog.controller;

import com.gearpc.catalog.application.dto.request.CreateProductRequest;
import com.gearpc.catalog.application.dto.request.SearchProductRequest;
import com.gearpc.catalog.application.dto.response.CreateProductResponse;
import com.gearpc.catalog.application.dto.response.DetailProductResponse;
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
        return ApiResponse.created(2500, "Sản phẩm được tạo thành công!", createProductResponse);
    }

    @GetMapping("/{id}")
    public ApiResponse<DetailProductResponse> getProductById(@NonNull @PathVariable UUID id) {
        DetailProductResponse response = productService.getProduct(id);
        return ApiResponse
                .ok(2500, "Lấy chi tiết sản phẩm thành công!", response);
    }

    @GetMapping("/search")
    public ApiResponse<PaginationResponse<DetailProductResponse>> searchProductsForAdmin(
            SearchProductRequest request,
            @PageableDefault(
                    size = 15,
                    sort = {"createdAt", "id"}, // tier-breaker
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        PaginationResponse<DetailProductResponse> products = productService.searchProductsForAdmin(request, pageable);
        return ApiResponse
                .ok(2500, "Tìm kiếm danh sách sản phẩm thành công", products);
    }

}
