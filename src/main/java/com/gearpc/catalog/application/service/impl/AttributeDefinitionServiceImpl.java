package com.gearpc.catalog.application.service.impl;

import com.gearpc.catalog.application.dto.request.CreateAttributeDefinitionRequest;
import com.gearpc.catalog.application.dto.request.UpdateAttributeDefinitionRequest;
import com.gearpc.catalog.application.dto.response.AttributeDefinitionOptionResponse;
import com.gearpc.catalog.application.dto.response.AttributeDefinitionResponse;
import com.gearpc.catalog.application.mapper.AttributeDefinitionMapper;
import com.gearpc.catalog.application.service.AttributeDefinitionService;
import com.gearpc.catalog.domain.entity.AttributeDefinition;
import com.gearpc.catalog.domain.valueobject.enums.AttributeDataType;
import com.gearpc.catalog.repository.AttributeDefinitionRepository;
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
        Optional.ofNullable(request.dataType()).ifPresent(attributeDefinition::setDataType);

        return attributeDefinitionMapper.toResponse(attributeDefinition);
    }

    @Override
    @Transactional
    public AttributeDefinitionResponse updateAttributeDefinitionStatus(UUID id, Boolean active) {
        AttributeDefinition attributeDefinition = findExistingAttribute(id);
        Optional.ofNullable(active).ifPresent(attributeDefinition::setActive);
        return attributeDefinitionMapper.toResponse(attributeDefinition);
    }

    @Override
    @Transactional
    public void deleteAttributeDefinition(UUID id) {
        findExistingAttribute(id).softDelete();
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
