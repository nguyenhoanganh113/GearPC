package com.gearpc.catalog.application.service.impl;

import com.gearpc.catalog.application.dto.response.CategoryAttributeResponse;
import com.gearpc.catalog.application.service.CategoryAttributeService;
import com.gearpc.catalog.domain.entity.AttributeDefinition;
import com.gearpc.catalog.domain.entity.Category;
import com.gearpc.catalog.domain.entity.CategoryAttribute;
import com.gearpc.catalog.domain.valueobject.CategoryAttributeId;
import com.gearpc.catalog.repository.AttributeDefinitionRepository;
import com.gearpc.catalog.repository.CategoryAttributeRepository;
import com.gearpc.catalog.repository.CategoryRepository;
import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryAttributeServiceImpl implements CategoryAttributeService {

    private final CategoryRepository categoryRepository;
    private final AttributeDefinitionRepository attributeDefinitionRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;

    @Transactional
    @Override
    public CategoryAttributeResponse assignAttribute(UUID categoryId, UUID attributeDefinitionId, boolean required) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        AttributeDefinition attributeDefinition =
                attributeDefinitionRepository.findByIdAndDeletedAtIsNull(attributeDefinitionId)
                        .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_DEFINITION_NOT_FOUND));

        CategoryAttributeId id = new CategoryAttributeId(categoryId, attributeDefinitionId);

        if (categoryAttributeRepository.existsById(id)) {
            throw new AppException(ErrorCode.CATEGORY_ATTRIBUTE_EXISTS);
        }

        CategoryAttribute categoryAttribute = new CategoryAttribute(category, attributeDefinition, required);

        CategoryAttribute saved = categoryAttributeRepository.save(categoryAttribute);

        return new CategoryAttributeResponse(
                saved.getCategory().getId(),
                saved.getCategory().getName(),
                saved.getAttributeDefinition().getId(),
                saved.getAttributeDefinition().getName(),
                saved.getAttributeDefinition().getCode(),
                saved.getAttributeDefinition().getUnit(),
                saved.getAttributeDefinition().getDataType(),
                saved.isRequired()
        );
    }
}
