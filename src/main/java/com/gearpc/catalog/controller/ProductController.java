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
            @PageableDefault(
                    size = 15,
                    sort = {"price"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable,
            ProductSearchRequest productSearchRequest
    ) {
        PaginationResponse<DetailProductResponse> products =
                productService.searchProductsForAdmin(productSearchRequest, pageable);
        return ApiResponse
                .ok(2500, "Tìm kiếm danh sách sản phẩm thành công", products);
    }

    @PutMapping("/{id}")
    public ApiResponse<UpdateProductResponse> updateProduct(
            @NonNull @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        UpdateProductResponse response = productService.updateProduct(id, request);
        return ApiResponse.ok(2500, "Cập nhật sản phẩm thành công!", response);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UpdateProductResponse> updateProductStatus(
            @NonNull @PathVariable UUID id,
            @Valid @RequestBody UpdateProductStatusRequest request
    ) {
        UpdateProductResponse response = productService.updateProductStatus(id, request);
        return ApiResponse.ok(2500, "Cập nhật trạng thái sản phẩm thành công!", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@NonNull @PathVariable UUID id) {
        productService.deleteProduct(id);
        return ApiResponse.noContent(2500, "Xóa sản phẩm thành công!");
    }

}
