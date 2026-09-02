package com.gearpc.catalog.application.service;

import com.gearpc.catalog.application.dto.request.CreateProductRequest;
import com.gearpc.catalog.application.dto.request.SearchProductRequest;
import com.gearpc.catalog.application.dto.response.CreateProductResponse;
import com.gearpc.catalog.application.dto.response.DetailProductResponse;
import com.gearpc.common.dto.PaginationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface ProductService {

    CreateProductResponse createProduct(@Valid @RequestBody CreateProductRequest request);

    DetailProductResponse getProduct(@NotNull @RequestParam("id") UUID id);

    PaginationResponse<DetailProductResponse> searchProductsForAdmin(
            SearchProductRequest searchProductRequest,
            Pageable pageable
    );

}
