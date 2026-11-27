package com.gearpc.service;

import com.gearpc.dto.request.UpdateProductAttributeValuesRequest;
import com.gearpc.dto.response.ProductAttributeValueResponse;

import java.util.List;
import java.util.UUID;

public interface ProductAttributeValueService {

    List<ProductAttributeValueResponse> getProductAttributeValues(UUID productId);

    List<ProductAttributeValueResponse> syncProductAttributeValues(
            UUID productId,
            UpdateProductAttributeValuesRequest request
    );
}
