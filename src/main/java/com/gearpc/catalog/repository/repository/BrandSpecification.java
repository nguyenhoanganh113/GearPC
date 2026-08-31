package com.gearpc.catalog.repository.repository;

import com.gearpc.catalog.domain.entity.Brand;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class BrandSpecification {

    public Specification<Brand> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("slug")), likePattern)
            );
        };
    }

    public Specification<Brand> isActive(Boolean active) {
        return (root, query, criteriaBuilder) -> {
            if (active == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("active"), active);
        };
    }

}
