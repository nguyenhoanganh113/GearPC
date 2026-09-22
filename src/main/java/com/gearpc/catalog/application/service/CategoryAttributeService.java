package com.gearpc.catalog.application.service;

import com.gearpc.catalog.application.dto.response.CategoryAttributeResponse;

import java.util.UUID;

public interface CategoryAttributeService {

    CategoryAttributeResponse assignAttribute(UUID categoryId, UUID attributeDefinitionId, boolean required);

}
