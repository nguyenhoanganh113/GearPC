package com.gearpc.catalog.application.service;

import com.gearpc.catalog.application.dto.request.CreateProductRequest;
import com.gearpc.catalog.application.dto.response.CreateProductResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

public interface ProductService {

    CreateProductResponse createProduct(@Valid @RequestBody CreateProductRequest request);

}
