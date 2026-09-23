package com.gearpc.catalog.application.mapper;

import com.gearpc.catalog.application.dto.request.CreateAttributeDefinitionRequest;
import com.gearpc.catalog.application.dto.response.AttributeDefinitionOptionResponse;
import com.gearpc.catalog.application.dto.response.AttributeDefinitionResponse;
import com.gearpc.catalog.domain.entity.AttributeDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttributeDefinitionMapper {

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "categoryAttributes", ignore = true)
    AttributeDefinition toEntity(CreateAttributeDefinitionRequest request);

    AttributeDefinitionResponse toResponse(AttributeDefinition attributeDefinition);

    AttributeDefinitionOptionResponse toOptionResponse(AttributeDefinition attributeDefinition);
}
