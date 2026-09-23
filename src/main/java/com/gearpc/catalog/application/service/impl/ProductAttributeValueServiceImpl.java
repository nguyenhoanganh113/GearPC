package com.gearpc.catalog.application.service.impl;

import com.gearpc.catalog.application.dto.request.ProductAttributeValueRequest;
import com.gearpc.catalog.application.dto.request.UpdateProductAttributeValuesRequest;
import com.gearpc.catalog.application.dto.response.ProductAttributeValidationErrorResponse;
import com.gearpc.catalog.application.dto.response.ProductAttributeValueResponse;
import com.gearpc.catalog.application.service.ProductAttributeValueService;
import com.gearpc.catalog.domain.entity.AttributeDefinition;
import com.gearpc.catalog.domain.entity.CategoryAttribute;
import com.gearpc.catalog.domain.entity.Product;
import com.gearpc.catalog.domain.entity.ProductAttributeValue;
import com.gearpc.catalog.domain.valueobject.enums.AttributeDataType;
import com.gearpc.catalog.domain.valueobject.enums.ProductAttributeValidationReason;
import com.gearpc.catalog.repository.CategoryAttributeRepository;
import com.gearpc.catalog.repository.ProductAttributeValueRepository;
import com.gearpc.catalog.repository.ProductRepository;
import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductAttributeValueServiceImpl implements ProductAttributeValueService {

    private final ProductRepository productRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;
    private final ProductAttributeValueRepository productAttributeValueRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeValueResponse> getProductAttributeValues(UUID productId) {
        Product product = findProduct(productId);
        return buildAttributeResponses(product);
    }

    @Override
    @Transactional
    public List<ProductAttributeValueResponse> syncProductAttributeValues(
            UUID productId,
            UpdateProductAttributeValuesRequest request
    ) {
        // 1. Chỉ đồng bộ thuộc tính cho product và category chưa bị soft delete.
        Product product = findProduct(productId);
        UUID categoryId = product.getCategory().getId();

        // 2. Lấy toàn bộ attribute đã được cấu hình cho category của product.
        List<CategoryAttribute> categoryAttributes = categoryAttributeRepository.findAllByCategory_Id(categoryId);
        Map<UUID, CategoryAttribute> assignedAttributes = categoryAttributes.stream()
                .collect(Collectors.toMap(
                        categoryAttribute -> categoryAttribute.getAttributeDefinition().getId(),
                        categoryAttribute -> categoryAttribute
                ));

        // 3. Kiểm tra toàn bộ request và gom lỗi theo từng attribute để client sửa trong một lần.
        Map<UUID, ProductAttributeValueRequest> requestedValues = new HashMap<>();
        List<ProductAttributeValidationErrorResponse> validationErrors = new ArrayList<>();

        for (ProductAttributeValueRequest item : request.attributes()) {
            if (requestedValues.putIfAbsent(item.attributeDefinitionId(), item) != null) {
                validationErrors.add(toValidationError(
                        item,
                        assignedAttributes.get(item.attributeDefinitionId()),
                        ProductAttributeValidationReason.DUPLICATE
                ));
                continue;
            }

            CategoryAttribute categoryAttribute = assignedAttributes.get(item.attributeDefinitionId());
            if (categoryAttribute == null) {
                validationErrors.add(toValidationError(item, null, ProductAttributeValidationReason.NOT_ALLOWED));
                continue;
            }

            AttributeDefinition definition = categoryAttribute.getAttributeDefinition();
            if (definition.isDeleted()) {
                validationErrors.add(toValidationError(item, categoryAttribute, ProductAttributeValidationReason.DELETED));
                continue;
            }
            if (!definition.isActive()) {
                validationErrors.add(toValidationError(item, categoryAttribute, ProductAttributeValidationReason.INACTIVE));
                continue;
            }

            try {
                validateValue(definition.getDataType(), item.value());
            } catch (AppException exception) {
                validationErrors.add(toValidationError(item, categoryAttribute, ProductAttributeValidationReason.INVALID_VALUE));
            }
        }

        // 4. Bổ sung chi tiết các required attribute không xuất hiện trong request.
        List<CategoryAttribute> missingRequiredAttributes = assignedAttributes.values().stream()
                .filter(CategoryAttribute::isRequired)
                .filter(item -> item.getAttributeDefinition().isActive())
                .filter(item -> !item.getAttributeDefinition().isDeleted())
                .filter(item -> !requestedValues.containsKey(item.getAttributeDefinition().getId()))
                .toList();

        missingRequiredAttributes.forEach(categoryAttribute -> validationErrors.add(
                toValidationError(null, categoryAttribute, ProductAttributeValidationReason.REQUIRED_MISSING)
        ));

        if (!validationErrors.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_ATTRIBUTE_VALIDATION_FAILED, validationErrors);
        }

        // 5. Lấy dữ liệu hiện tại để xác định value cần thêm, cập nhật hoặc xóa.
        Map<UUID, ProductAttributeValue> existingValues = productAttributeValueRepository
                .findAllByProduct_IdOrderByAttributeDefinition_NameAsc(productId)
                .stream()
                .collect(Collectors.toMap(
                        value -> value.getAttributeDefinition().getId(),
                        value -> value
                ));

        // 6. Xóa các value không còn xuất hiện trong request PUT.
        List<ProductAttributeValue> valuesToDelete = existingValues.entrySet().stream()
                .filter(entry -> !requestedValues.containsKey(entry.getKey()))
                .map(Map.Entry::getValue)
                .toList();
        productAttributeValueRepository.deleteAll(valuesToDelete);

        // 7. Cập nhật value đã tồn tại hoặc tạo mới value chưa có.
        List<ProductAttributeValue> valuesToSave = requestedValues.values().stream()
                .map(item -> {
                    ProductAttributeValue existing = existingValues.get(item.attributeDefinitionId());
                    if (existing != null) {
                        existing.setValue(item.value());
                        return existing;
                    }

                    AttributeDefinition definition = assignedAttributes
                            .get(item.attributeDefinitionId())
                            .getAttributeDefinition();
                    return new ProductAttributeValue(product, definition, item.value());
                })
                .toList();

        productAttributeValueRepository.saveAll(valuesToSave);

        // 8. Trả toàn bộ schema của category, kể cả optional attribute chưa có value.
        return buildAttributeResponses(product);
    }

    private Product findProduct(UUID productId) {
        return productRepository.findByIdAndDeletedAtIsNullAndCategory_DeletedAtIsNull(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private List<ProductAttributeValueResponse> buildAttributeResponses(Product product) {
        UUID productId = product.getId();
        UUID categoryId = product.getCategory().getId();

        Map<UUID, String> valuesByAttributeId = productAttributeValueRepository
                .findAllByProduct_IdOrderByAttributeDefinition_NameAsc(productId)
                .stream()
                .collect(Collectors.toMap(
                        value -> value.getAttributeDefinition().getId(),
                        ProductAttributeValue::getValue
                ));

        return categoryAttributeRepository
                .findAllByCategory_IdAndAttributeDefinition_ActiveTrueAndAttributeDefinition_DeletedAtIsNullOrderByAttributeDefinition_NameAsc(
                        categoryId
                )
                .stream()
                .map(categoryAttribute -> {
                    AttributeDefinition definition = categoryAttribute.getAttributeDefinition();
                    return new ProductAttributeValueResponse(
                            productId,
                            definition.getId(),
                            definition.getName(),
                            definition.getCode(),
                            definition.getUnit(),
                            definition.getDataType(),
                            categoryAttribute.isRequired(),
                            valuesByAttributeId.get(definition.getId())
                    );
                })
                .toList();
    }

    private void validateValue(AttributeDataType dataType, String value) {
        try {
            switch (dataType) {
                case NUMBER -> new BigDecimal(value);
                case BOOLEAN -> {
                    if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                        throw new IllegalArgumentException();
                    }
                }
                case DATE -> LocalDate.parse(value);
                case TEXT, SELECT -> {
                    // Non-blank validation is handled by the request DTO.
                }
            }
        } catch (IllegalArgumentException | DateTimeParseException exception) {
            throw new AppException(ErrorCode.INVALID_PRODUCT_ATTRIBUTE_VALUE);
        }
    }

    private ProductAttributeValidationErrorResponse toValidationError(
            ProductAttributeValueRequest request,
            CategoryAttribute categoryAttribute,
            ProductAttributeValidationReason reason
    ) {
        AttributeDefinition definition = categoryAttribute == null
                ? null
                : categoryAttribute.getAttributeDefinition();

        return new ProductAttributeValidationErrorResponse(
                definition != null ? definition.getId() : request.attributeDefinitionId(),
                definition != null ? definition.getCode() : null,
                definition != null ? definition.getName() : null,
                request != null ? request.value() : null,
                reason
        );
    }

}
