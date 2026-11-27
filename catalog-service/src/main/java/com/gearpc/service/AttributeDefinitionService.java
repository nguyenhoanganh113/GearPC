package com.gearpc.service;

import com.gearpc.dto.request.CreateAttributeDefinitionRequest;
import com.gearpc.dto.request.UpdateAttributeDefinitionRequest;
import com.gearpc.dto.response.AttributeDefinitionOptionResponse;
import com.gearpc.dto.response.AttributeDefinitionResponse;
import com.gearpc.entity.valueobject.enums.AttributeDataType;
import com.gearpc.dto.PaginationResponse;
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
