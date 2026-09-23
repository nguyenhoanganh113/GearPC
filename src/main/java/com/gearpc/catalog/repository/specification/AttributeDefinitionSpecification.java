package com.gearpc.catalog.repository.specification;

import com.gearpc.catalog.domain.entity.AttributeDefinition;
import com.gearpc.catalog.domain.valueobject.enums.AttributeDataType;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.Locale;

@UtilityClass
public class AttributeDefinitionSpecification {

    public Specification<AttributeDefinition> isNotDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedAt"));
    }

    public Specification<AttributeDefinition> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(keyword)) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + keyword.strip().toLowerCase(Locale.ROOT) + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), pattern)
            );
        };
    }

    public Specification<AttributeDefinition> isActive(Boolean active) {
        return (root, query, criteriaBuilder) -> active == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("active"), active);
    }

    public Specification<AttributeDefinition> hasDataType(AttributeDataType dataType) {
        return (root, query, criteriaBuilder) -> dataType == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("dataType"), dataType);
    }
}
