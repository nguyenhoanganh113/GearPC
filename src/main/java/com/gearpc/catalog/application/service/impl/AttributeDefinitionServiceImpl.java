package com.gearpc.catalog.application.service.impl;

import com.gearpc.catalog.application.dto.request.CreateAttributeDefinitionRequest;
import com.gearpc.catalog.application.dto.request.UpdateAttributeDefinitionRequest;
import com.gearpc.catalog.application.dto.response.AttributeDefinitionOptionResponse;
import com.gearpc.catalog.application.dto.response.AttributeDefinitionResponse;
import com.gearpc.catalog.application.mapper.AttributeDefinitionMapper;
import com.gearpc.catalog.application.service.AttributeDefinitionService;
import com.gearpc.catalog.domain.entity.AttributeDefinition;
import com.gearpc.catalog.domain.entity.Product;
import com.gearpc.catalog.domain.valueobject.enums.AttributeDataType;
import com.gearpc.catalog.domain.valueobject.enums.ProductStatus;
import com.gearpc.catalog.repository.AttributeDefinitionRepository;
import com.gearpc.catalog.repository.CategoryAttributeRepository;
import com.gearpc.catalog.repository.ProductAttributeValueRepository;
import com.gearpc.catalog.repository.ProductRepository;
import com.gearpc.catalog.repository.specification.AttributeDefinitionSpecification;
import com.gearpc.common.dto.PaginationResponse;
import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttributeDefinitionServiceImpl implements AttributeDefinitionService {

    private final AttributeDefinitionRepository attributeDefinitionRepository;
    private final AttributeDefinitionMapper attributeDefinitionMapper;
    private final CategoryAttributeRepository categoryAttributeRepository;
    private final ProductAttributeValueRepository productAttributeValueRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public AttributeDefinitionResponse createAttributeDefinition(CreateAttributeDefinitionRequest request) {
        validateUniqueCode(request.code());

        AttributeDefinition attributeDefinition = attributeDefinitionMapper.toEntity(request);
        attributeDefinitionRepository.save(attributeDefinition);
        return attributeDefinitionMapper.toResponse(attributeDefinition);
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeDefinitionResponse getAttributeDefinition(UUID id) {
        return attributeDefinitionMapper.toResponse(findExistingAttribute(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<AttributeDefinitionResponse> searchAttributeDefinitions(
            String keyword,
            Boolean active,
            AttributeDataType dataType,
            Pageable pageable
    ) {
        Specification<AttributeDefinition> specification = Specification.allOf(
                AttributeDefinitionSpecification.isNotDeleted(),
                AttributeDefinitionSpecification.hasKeyword(keyword),
                AttributeDefinitionSpecification.isActive(active),
                AttributeDefinitionSpecification.hasDataType(dataType)
        );

        Page<AttributeDefinition> page = attributeDefinitionRepository.findAll(specification, pageable);
        List<AttributeDefinitionResponse> content = page.getContent().stream()
                .map(attributeDefinitionMapper::toResponse)
                .toList();

        return PaginationResponse.<AttributeDefinitionResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeDefinitionOptionResponse> getActiveAttributeDefinitionOptions() {
        return attributeDefinitionRepository.findAllByActiveTrueAndDeletedAtIsNullOrderByNameAsc()
                .stream()
                .map(attributeDefinitionMapper::toOptionResponse)
                .toList();
    }

    @Override
    @Transactional
    public AttributeDefinitionResponse updateAttributeDefinition(
            UUID id,
            UpdateAttributeDefinitionRequest request
    ) {
        AttributeDefinition attributeDefinition = findExistingAttribute(id);

        if (request.name() != null) {
            attributeDefinition.setName(request.name());
        }

        if (request.code() != null && !request.code().equalsIgnoreCase(attributeDefinition.getCode())) {
            if (attributeDefinitionRepository.existsByCodeIgnoreCase(request.code())) {
                throw new AppException(ErrorCode.ATTRIBUTE_DEFINITION_EXISTS);
            }
            attributeDefinition.setCode(request.code());
        }

        Optional.ofNullable(request.unit()).ifPresent(attributeDefinition::setUnit);

        if (request.dataType() != null && request.dataType() != attributeDefinition.getDataType()) {

            if (productAttributeValueRepository.existsByAttributeDefinition_IdAndProduct_DeletedAtIsNull(id)) {
                throw new AppException(ErrorCode.ATTRIBUTE_DATA_TYPE_CHANGE_NOT_ALLOWED);
            }

            attributeDefinition.setDataType(request.dataType());
        }

        return attributeDefinitionMapper.toResponse(attributeDefinition);
    }

    @Override
    @Transactional
    public AttributeDefinitionResponse updateAttributeDefinitionStatus(UUID id, Boolean active) {
        AttributeDefinition attributeDefinition = findExistingAttribute(id);

        if (Boolean.TRUE.equals(active) && !attributeDefinition.isActive()) {
            List<UUID> categoryIds = categoryAttributeRepository
                    .findAllByAttributeDefinition_IdAndRequiredTrueAndCategory_ActiveTrueAndCategory_DeletedAtIsNull(id)
                    .stream()
                    .map(categoryAttribute -> categoryAttribute.getCategory().getId())
                    .distinct()
                    .toList();

            if (!categoryIds.isEmpty()) {
                List<UUID> activeProductIds = productRepository
                        .findAllByCategory_IdInAndProductStatusAndDeletedAtIsNull(
                                categoryIds,
                                ProductStatus.ACTIVE
                        )
                        .stream()
                        .map(Product::getId)
                        .toList();

                if (!activeProductIds.isEmpty()) {
                    long productValueCount = productAttributeValueRepository
                            .countByProduct_IdInAndAttributeDefinition_Id(
                                    activeProductIds,
                                    id
                            );

                    if (productValueCount < activeProductIds.size()) {
                        throw new AppException(ErrorCode.ATTRIBUTE_REACTIVATION_NOT_ALLOWED);
                    }
                }
            }
        }

        Optional.ofNullable(active).ifPresent(attributeDefinition::setActive);

        return attributeDefinitionMapper.toResponse(attributeDefinition);
    }

    @Override
    @Transactional
    public void deleteAttributeDefinition(UUID id) {
        AttributeDefinition attributeDefinition = findExistingAttribute(id);

        boolean assignedToCategory =
                categoryAttributeRepository
                        .existsByAttributeDefinition_Id(id);

        boolean usedByProduct =
                productAttributeValueRepository
                        .existsByAttributeDefinition_IdAndProduct_DeletedAtIsNull(id);

        if (assignedToCategory || usedByProduct) {
            throw new AppException(
                    ErrorCode.ATTRIBUTE_DEFINITION_IN_USE
            );
        }

        attributeDefinition.softDelete();
    }

    private AttributeDefinition findExistingAttribute(UUID id) {
        return attributeDefinitionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_DEFINITION_NOT_FOUND));
    }

    private void validateUniqueCode(String code) {
        if (attributeDefinitionRepository.existsByCodeIgnoreCase(code)) {
            throw new AppException(ErrorCode.ATTRIBUTE_DEFINITION_EXISTS);
        }
    }
}
