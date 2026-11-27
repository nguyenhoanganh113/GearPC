package com.gearpc.service;

import com.gearpc.dto.request.CreateProductRequest;
import com.gearpc.dto.request.ProductSearchRequest;
import com.gearpc.dto.request.UpdateProductRequest;
import com.gearpc.dto.request.UpdateProductStatusRequest;
import com.gearpc.dto.response.CreateProductResponse;
import com.gearpc.dto.response.DetailProductResponse;
import com.gearpc.dto.response.UpdateProductResponse;
import com.gearpc.dto.PaginationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface ProductService {

    CreateProductResponse createProduct(@Valid @RequestBody CreateProductRequest request);

    DetailProductResponse getProduct(@NotNull @RequestParam("id") UUID id);

    PaginationResponse<DetailProductResponse> searchProductsForAdmin(
            ProductSearchRequest productSearchRequest,
            Pageable pageable
    );

    UpdateProductResponse updateProduct(
            @NotNull @RequestParam("id") UUID id,
            @Valid @RequestBody UpdateProductRequest request
    );

    UpdateProductResponse updateProductStatus(
            @NotNull @RequestParam("id") UUID id,
            @RequestBody UpdateProductStatusRequest request
    );

    void deleteProduct(@NotNull @RequestParam("id") UUID id);

}
