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
import com.gearpc.catalog.repository.ProductAttributeValueRepository;
import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryAttributeServiceImpl implements CategoryAttributeService {

    private final CategoryRepository categoryRepository;
    private final AttributeDefinitionRepository attributeDefinitionRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;
    private final ProductAttributeValueRepository productAttributeValueRepository;

    @Transactional
    @Override
    public CategoryAttributeResponse assignAttribute(UUID categoryId, UUID attributeDefinitionId, boolean required) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        AttributeDefinition attributeDefinition =
                attributeDefinitionRepository.findByIdAndActiveTrueAndDeletedAtIsNull(attributeDefinitionId)
                        .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_DEFINITION_NOT_FOUND));

        CategoryAttributeId id = new CategoryAttributeId(categoryId, attributeDefinitionId);

        if (categoryAttributeRepository.existsById(id)) {
            throw new AppException(ErrorCode.CATEGORY_ATTRIBUTE_EXISTS);
        }

        CategoryAttribute categoryAttribute = new CategoryAttribute(category, attributeDefinition, required);

        return toResponse(categoryAttributeRepository.save(categoryAttribute));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryAttributeResponse> getCategoryAttributes(UUID categoryId) {
        categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return categoryAttributeRepository
                .findAllByCategory_IdAndAttributeDefinition_DeletedAtIsNullOrderByAttributeDefinition_NameAsc(
                        categoryId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CategoryAttributeResponse updateRequired(
            UUID categoryId,
            UUID attributeDefinitionId,
            boolean required
    ) {
        CategoryAttribute categoryAttribute = findActiveCategoryAttribute(categoryId, attributeDefinitionId);
        categoryAttribute.setRequired(required);
        return toResponse(categoryAttribute);
    }

    @Override
    @Transactional
    public void removeAttribute(UUID categoryId, UUID attributeDefinitionId) {
        CategoryAttribute categoryAttribute =
                findActiveCategoryAttribute(categoryId, attributeDefinitionId);

        if (productAttributeValueRepository
                .existsByProduct_Category_IdAndAttributeDefinition_Id(
                        categoryId,
                        attributeDefinitionId
                )) {
            throw new AppException(ErrorCode.CATEGORY_ATTRIBUTE_IN_USE);
        }

        categoryAttributeRepository.delete(categoryAttribute);
    }

    private CategoryAttribute findActiveCategoryAttribute(UUID categoryId, UUID attributeDefinitionId) {
        CategoryAttributeId id = new CategoryAttributeId(categoryId, attributeDefinitionId);
        return categoryAttributeRepository
                .findByIdAndCategory_DeletedAtIsNullAndAttributeDefinition_DeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_ATTRIBUTE_NOT_FOUND));
    }

    private CategoryAttribute  findCategoryAttribute(UUID categoryId, UUID attributeDefinitionId) {
        CategoryAttributeId id = new CategoryAttributeId(categoryId, attributeDefinitionId);
        return categoryAttributeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_ATTRIBUTE_NOT_FOUND));
    }

    private CategoryAttributeResponse toResponse(CategoryAttribute categoryAttribute) {
        return new CategoryAttributeResponse(
                categoryAttribute.getCategory().getId(),
                categoryAttribute.getCategory().getName(),
                categoryAttribute.getAttributeDefinition().getId(),
                categoryAttribute.getAttributeDefinition().getName(),
                categoryAttribute.getAttributeDefinition().getCode(),
                categoryAttribute.getAttributeDefinition().getUnit(),
                categoryAttribute.getAttributeDefinition().getDataType(),
                categoryAttribute.isRequired()
        );
    }
}
