package com.gearpc.service.impl;

import com.gearpc.dto.response.CategoryAttributeResponse;
import com.gearpc.service.CategoryAttributeService;
import com.gearpc.entity.AttributeDefinition;
import com.gearpc.entity.Category;
import com.gearpc.entity.CategoryAttribute;
import com.gearpc.entity.Product;
import com.gearpc.entity.valueobject.CategoryAttributeId;
import com.gearpc.entity.valueobject.enums.ProductStatus;
import com.gearpc.repository.*;
import com.gearpc.exception.AppException;
import com.gearpc.exception.ErrorCode;
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
    private final ProductRepository productRepository;

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

        if (required) {
            validateActiveProductsHaveAttributeValue(categoryId, attributeDefinitionId);
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

        if (required && !categoryAttribute.isRequired()) {
            validateActiveProductsHaveAttributeValue(categoryId, attributeDefinitionId);
        }

        categoryAttribute.setRequired(required);
        return toResponse(categoryAttribute);
    }

    private void validateActiveProductsHaveAttributeValue(UUID categoryId, UUID attributeDefinitionId) {
        List<UUID> activeProductIds = productRepository
                .findAllByCategory_IdAndProductStatusAndDeletedAtIsNull(
                        categoryId,
                        ProductStatus.ACTIVE
                )
                .stream()
                .map(Product::getId)
                .toList();

        if (!activeProductIds.isEmpty()) {
            long productValueCount = productAttributeValueRepository
                    .countByProduct_IdInAndAttributeDefinition_Id(
                            activeProductIds,
                            attributeDefinitionId
                    );

            if (productValueCount < activeProductIds.size()) {
                throw new AppException(
                        ErrorCode.REQUIRED_ATTRIBUTE_SCHEMA_CHANGE_NOT_ALLOWED
                );
            }
        }
    }

    @Override
    @Transactional
    public void removeAttribute(UUID categoryId, UUID attributeDefinitionId) {
        CategoryAttribute categoryAttribute =
                findActiveCategoryAttribute(categoryId, attributeDefinitionId);

        if (productAttributeValueRepository
                .existsByProduct_Category_IdAndAttributeDefinition_IdAndProduct_DeletedAtIsNull(
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
