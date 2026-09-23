package com.gearpc.catalog.application.service;

import com.gearpc.catalog.application.dto.request.UpdateProductAttributeValuesRequest;
import com.gearpc.catalog.application.dto.response.ProductAttributeValueResponse;

import java.util.List;
import java.util.UUID;

public interface ProductAttributeValueService {

    List<ProductAttributeValueResponse> getProductAttributeValues(UUID productId);

    List<ProductAttributeValueResponse> syncProductAttributeValues(
            UUID productId,
            UpdateProductAttributeValuesRequest request
    );
}
