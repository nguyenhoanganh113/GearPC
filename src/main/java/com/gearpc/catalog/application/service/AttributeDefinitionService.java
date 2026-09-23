package com.gearpc.catalog.application.service;

import com.gearpc.catalog.application.dto.request.CreateAttributeDefinitionRequest;
import com.gearpc.catalog.application.dto.request.UpdateAttributeDefinitionRequest;
import com.gearpc.catalog.application.dto.response.AttributeDefinitionOptionResponse;
import com.gearpc.catalog.application.dto.response.AttributeDefinitionResponse;
import com.gearpc.catalog.domain.valueobject.enums.AttributeDataType;
import com.gearpc.common.dto.PaginationResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AttributeDefinitionService {

    AttributeDefinitionResponse createAttributeDefinition(CreateAttributeDefinitionRequest request);

    AttributeDefinitionResponse getAttributeDefinition(UUID id);

    PaginationResponse<AttributeDefinitionResponse> searchAttributeDefinitions(
            String keyword,
            Boolean active,
            AttributeDataType dataType,
            Pageable pageable
    );

    List<AttributeDefinitionOptionResponse> getActiveAttributeDefinitionOptions();

    AttributeDefinitionResponse updateAttributeDefinition(UUID id, UpdateAttributeDefinitionRequest request);

    AttributeDefinitionResponse updateAttributeDefinitionStatus(UUID id, Boolean active);

    void deleteAttributeDefinition(UUID id);
}
