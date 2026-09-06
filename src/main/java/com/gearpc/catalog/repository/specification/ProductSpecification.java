package com.gearpc.catalog.repository.specification;

import com.gearpc.catalog.domain.entity.Product;
import com.gearpc.catalog.domain.valueobject.enums.ProductStatus;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

@UtilityClass
public class ProductSpecification {

    public Specification<Product> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if(!StringUtils.hasText(keyword)) {
                return criteriaBuilder.conjunction();
            }
            String pattern = "%" + keyword.toLowerCase(Locale.ROOT) + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("sku")), pattern)
            );
        };
    }

    public Specification<Product> hasPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            else if (minPrice != null && maxPrice == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            }
            else if (minPrice == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            else {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            }
        };
    }

    public Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("productStatus"), status);
        };
    }

    public Specification<Product> hasCategory(UUID categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

    public Specification<Product> hasBrand(UUID brandId) {
        return (root, query, criteriaBuilder) -> {
            if (brandId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("brand").get("id"), brandId);
        };
    }

    public Specification<Product> inStock(Boolean inStock) {
        return (root, query, criteriaBuilder) -> {
          if (inStock == null) {
              return criteriaBuilder.conjunction();
          }
          else if (inStock) {
              return criteriaBuilder.greaterThanOrEqualTo(root.get("stockQuantity"), 1);
          }
          else {
              return criteriaBuilder.equal(root.get("stockQuantity"), 0);
          }
        };
    }
}
