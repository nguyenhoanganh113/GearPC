package com.gearpc.catalog.repository;

import com.gearpc.catalog.domain.entity.Product;
import com.gearpc.catalog.domain.valueobject.enums.ProductStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    boolean existsByNameAndDeletedAtIsNull(String name);

    boolean existsBySku(String sku);

    @EntityGraph(attributePaths = "category")
    Optional<Product> findByIdAndDeletedAtIsNullAndCategory_DeletedAtIsNull(UUID id);

    Optional<Product> findByIdAndDeletedAtIsNull(UUID productId);

    boolean existsByCategory_IdAndDeletedAtIsNull(UUID categoryId);

    boolean existsByBrand_IdAndDeletedAtIsNull(UUID brandId);

    boolean existsByCategory_IdAndProductStatusAndDeletedAtIsNull(
            UUID categoryId,
            ProductStatus productStatus
    );

    boolean existsByBrand_IdAndProductStatusAndDeletedAtIsNull(
            UUID brandId,
            ProductStatus productStatus
    );

    List<Product> findAllByCategory_IdInAndProductStatusAndDeletedAtIsNull(
            Collection<UUID> categoryIds,
            ProductStatus productStatus
    );

    List<Product> findAllByCategory_IdAndProductStatusAndDeletedAtIsNull(
            UUID categoryId,
            ProductStatus productStatus
    );
}
